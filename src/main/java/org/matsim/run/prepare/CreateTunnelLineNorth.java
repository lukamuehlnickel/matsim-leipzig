
package org.matsim.run.prepare;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.NetworkFactory;
import org.matsim.api.core.v01.network.NetworkWriter;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.population.routes.LinkNetworkRouteFactory;
import org.matsim.core.population.routes.RouteUtils;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.pt.transitSchedule.api.*;
import org.matsim.vehicles.MatsimVehicleReader;
import org.matsim.vehicles.MatsimVehicleWriter;
import org.matsim.vehicles.VehicleType;

import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

public class CreateTunnelLineNorth {

    private static final LinkNetworkRouteFactory routeFactory = new LinkNetworkRouteFactory();
    private static final NetworkFactory networkFactory = NetworkUtils.createNetwork().getFactory();
    private static final TransitScheduleFactory scheduleFactory = ScenarioUtils.createScenario(ConfigUtils.createConfig()).getTransitSchedule().getFactory();

    public static void main(String[] args) {


        // read in network and create scenario
        var root = Paths.get("./input/v1.3");
        var network = NetworkUtils.readNetwork(root.resolve("leipzig-v1.3-network-with-pt.xml.gz").toString());
        var scenario = ScenarioUtils.createScenario(ConfigUtils.createConfig());

        // read in existing pt files
        var transitSchedule = Paths.get("./input/v1.3/leipzig-v1.3-transitSchedule.xml.gz");
        var vehicleTypes = Paths.get("./input/v1.3/leipzig-v1.3-transitVehicles.xml.gz");
        new TransitScheduleReader(scenario).readFile(transitSchedule.toString());
        MatsimVehicleReader vehicleReader = new MatsimVehicleReader(scenario.getTransitVehicles());
        vehicleReader.readFile(vehicleTypes.toString());

        // create vehicle type
        var vehicleType = scenario.getVehicles().getFactory().createVehicleType(Id.create("shuttle", VehicleType.class));
        vehicleType.setLength(20);
        vehicleType.setPcuEquivalents(2);
        vehicleType.setMaximumVelocity(36);
        vehicleType.setNetworkMode(TransportMode.pt);
        vehicleType.setDescription("shuttle vehicle type");
        vehicleType.getCapacity().setSeats(400);
        vehicleType.getCapacity().setStandingRoom(800);
        scenario.getTransitVehicles().addVehicleType(vehicleType);


        // create pt nodes and links --> adapt network
        var startNode = network.getFactory().createNode(Id.createNodeId("pt_start"), new Coord(731432.9941460779 - 100, 5691546.204461314 - 100));
        network.addNode(startNode);
        var Plagwitz = network.getNodes().get(Id.createNodeId("pt_008010209"));
        var Lindenauer = network.getNodes().get(Id.createNodeId("pt_000012320"));
        var Sportforum = network.getNodes().get(Id.createNodeId("pt_000011071"));
        var Hbf = network.getNodes().get(Id.createNodeId("pt_008098205"));
        var FriedrichListPlatz = network.getNodes().get(Id.createNodeId("pt_000012706"));
        var LeipzigOst = network.getFactory().createNode(Id.createNodeId("pt_LeipzigOst"), new Coord(737175.6875, 5694209));
        var endNode = network.getFactory().createNode(Id.createNodeId("pt_end"), new Coord(737175.6875 - 100, 5694209 - 100));
        network.addNode(LeipzigOst);
        network.addNode(endNode);

        //both directions w=western direction, e=eastern direction
        var startLink_we = createLink("pt_1_we", startNode, Plagwitz);
        var connection1_we = createLink("pt_2_we", Plagwitz, Lindenauer);
        var connection2_we = createLink("pt_3_we", Lindenauer, Sportforum);
        var connection3_we = createLink("pt_4_we", Sportforum, Hbf);
        var connection4_we = createLink("pt_5_we", Hbf, FriedrichListPlatz);
        var connection5_we = createLink("pt_6_we", FriedrichListPlatz, LeipzigOst);
        var endLink_we = createLink("pt_7_we", LeipzigOst, endNode);
        network.addLink(connection1_we);
        network.addLink(connection2_we);
        network.addLink(connection3_we);
        network.addLink(connection4_we);
        network.addLink(connection5_we);
        network.addLink(startLink_we);
        network.addLink(endLink_we);

        var startLink_ew = createLink("pt_1_ew", endNode, LeipzigOst);
        var connection1_ew = createLink("pt_2_ew", LeipzigOst, FriedrichListPlatz);
        var connection2_ew = createLink("pt_3_ew", FriedrichListPlatz, Hbf);
        var connection3_ew = createLink("pt_4_ew", Hbf, Sportforum);
        var connection4_ew = createLink("pt_5_ew", Sportforum, Lindenauer);
        var connection5_ew = createLink("pt_6_ew", Lindenauer, Plagwitz);
        var endLink_ew = createLink("pt_7_ew", Plagwitz, startNode);
        network.addLink(connection1_ew);
        network.addLink(connection2_ew);
        network.addLink(connection3_ew);
        network.addLink(connection4_ew);
        network.addLink(connection5_ew);
        network.addLink(startLink_ew);
        network.addLink(endLink_ew);


        // create TransitStopFacility
        //here you have to create TransitStop facilities for each added stop in the tunnelline
        var stop1_facility_we = scheduleFactory.createTransitStopFacility(Id.create("Leipzig-Plagwitz_we", TransitStopFacility.class), Plagwitz.getCoord(), false);
        stop1_facility_we.setLinkId(startLink_we.getId());

        var stop2_facility_we = scheduleFactory.createTransitStopFacility(Id.create("Leipzig-Lindenauer_we", TransitStopFacility.class), Lindenauer.getCoord(), false);
        stop2_facility_we.setLinkId(connection1_we.getId());

        var stop3_facility_we = scheduleFactory.createTransitStopFacility(Id.create("Leipzig-Sportforum_we", TransitStopFacility.class), Sportforum.getCoord(), false);
        stop3_facility_we.setLinkId(connection2_we.getId());

        var stop4_facility_we = scheduleFactory.createTransitStopFacility(Id.create("Leipzig-Hbf_we", TransitStopFacility.class), Hbf.getCoord(), false);
        stop4_facility_we.setLinkId(connection3_we.getId());

        var stop5_facility_we = scheduleFactory.createTransitStopFacility(Id.create("Leipzig-FriedrichListPlatz_we", TransitStopFacility.class), FriedrichListPlatz.getCoord(), false);
        stop5_facility_we.setLinkId(connection4_we.getId());

        var stop6_facility_we = scheduleFactory.createTransitStopFacility(Id.create("Leipzig-LeipzigOst_we", TransitStopFacility.class), LeipzigOst.getCoord(), false);
        stop6_facility_we.setLinkId(connection5_we.getId());

        scenario.getTransitSchedule().addStopFacility(stop1_facility_we);
        scenario.getTransitSchedule().addStopFacility(stop2_facility_we);
        scenario.getTransitSchedule().addStopFacility(stop3_facility_we);
        scenario.getTransitSchedule().addStopFacility(stop4_facility_we);
        scenario.getTransitSchedule().addStopFacility(stop5_facility_we);
        scenario.getTransitSchedule().addStopFacility(stop6_facility_we);

        //other direction
        var stop1_facility_ew = scheduleFactory.createTransitStopFacility(Id.create("Leipzig-LeipzigOst_ew", TransitStopFacility.class),LeipzigOst.getCoord(), false);
        stop1_facility_ew.setLinkId(startLink_ew.getId());

        var stop2_facility_ew = scheduleFactory.createTransitStopFacility(Id.create("Leipzig-FriedrichListPlatz_ew", TransitStopFacility.class), FriedrichListPlatz.getCoord(), false);
        stop2_facility_ew.setLinkId(connection1_ew.getId());

        var stop3_facility_ew = scheduleFactory.createTransitStopFacility(Id.create("Leipzig-Hbf_ew", TransitStopFacility.class), Hbf.getCoord(), false);
        stop3_facility_ew.setLinkId(connection2_ew.getId());

        var stop4_facility_ew = scheduleFactory.createTransitStopFacility(Id.create("Leipzig-Sportforum_ew", TransitStopFacility.class), Sportforum.getCoord(), false);
        stop4_facility_ew.setLinkId(connection3_ew.getId());

        var stop5_facility_ew = scheduleFactory.createTransitStopFacility(Id.create("Leipzig-Lindenauer_ew", TransitStopFacility.class), Lindenauer.getCoord(), false);
        stop5_facility_ew.setLinkId(connection4_ew.getId());

        var stop6_facility_ew = scheduleFactory.createTransitStopFacility(Id.create("Leipzig-Plagwitz_ew", TransitStopFacility.class), Plagwitz.getCoord(), false);
        stop6_facility_ew.setLinkId(connection5_ew.getId());

        scenario.getTransitSchedule().addStopFacility(stop1_facility_ew);
        scenario.getTransitSchedule().addStopFacility(stop2_facility_ew);
        scenario.getTransitSchedule().addStopFacility(stop3_facility_ew);
        scenario.getTransitSchedule().addStopFacility(stop4_facility_ew);
        scenario.getTransitSchedule().addStopFacility(stop5_facility_ew);
        scenario.getTransitSchedule().addStopFacility(stop6_facility_ew);

        // create TransitRouteStop --- here you have to add the stopps with 120 sec each
        var Stop1_we = scheduleFactory.createTransitRouteStop(stop1_facility_we, 0, 10);
        var Stop2_we = scheduleFactory.createTransitRouteStop(stop2_facility_we, 120, 130);
        var Stop3_we = scheduleFactory.createTransitRouteStop(stop3_facility_we, 240, 250);
        var Stop4_we = scheduleFactory.createTransitRouteStop(stop4_facility_we, 360, 370);
        var Stop5_we = scheduleFactory.createTransitRouteStop(stop5_facility_we, 480, 490);
        var Stop6_we = scheduleFactory.createTransitRouteStop(stop6_facility_we, 600, 610);

        // east direction
        var Stop1_ew = scheduleFactory.createTransitRouteStop(stop1_facility_ew, 0, 10);
        var Stop2_ew = scheduleFactory.createTransitRouteStop(stop2_facility_ew, 120, 130);
        var Stop3_ew = scheduleFactory.createTransitRouteStop(stop3_facility_ew, 240, 250);
        var Stop4_ew = scheduleFactory.createTransitRouteStop(stop4_facility_ew, 360, 370);
        var Stop5_ew = scheduleFactory.createTransitRouteStop(stop5_facility_ew, 480, 490);
        var Stop6_ew = scheduleFactory.createTransitRouteStop(stop6_facility_ew, 600, 610);

        // create TransitRoute
        var networkRoute_we = RouteUtils.createLinkNetworkRouteImpl(startLink_we.getId(), List.of(connection1_we.getId(),connection2_we.getId(),connection3_we.getId(),connection4_we.getId(),connection5_we.getId()), endLink_we.getId());
        var networkRoute_ew = RouteUtils.createLinkNetworkRouteImpl(startLink_ew.getId(), List.of(connection1_ew.getId(),connection2_ew.getId(),connection3_ew.getId(),connection4_ew.getId(),connection5_ew.getId()), startLink_ew.getId());

        //modify this, add stops to list
        var route_we = scheduleFactory.createTransitRoute(Id.create("route-we", TransitRoute.class), networkRoute_we, List.of(Stop1_we, Stop2_we, Stop3_we, Stop4_we, Stop5_we, Stop6_we), "pt");
        var route_ew = scheduleFactory.createTransitRoute(Id.create("route-ew", TransitRoute.class), networkRoute_ew, List.of(Stop1_ew, Stop2_ew, Stop3_ew, Stop4_ew, Stop5_ew, Stop6_ew), "pt");

        // create Departures & corresponding Vehicles
        for (int i = 6 * 3600; i < 23 * 3600; i += 300) {
            var departure = scheduleFactory.createDeparture(Id.create("departure_" + i, Departure.class), i);
            var vehicle = scenario.getTransitVehicles().getFactory().createVehicle(Id.createVehicleId("shuttle_vehicle_w_" + i), vehicleType);
            departure.setVehicleId(vehicle.getId());

            scenario.getTransitVehicles().addVehicle(vehicle);
            route_we.addDeparture(departure);
        }

        for (int i = 6 * 3600; i < 23 * 3600; i += 300) {
            var departure = scheduleFactory.createDeparture(Id.create("departure_" + i, Departure.class), i);
            var vehicle = scenario.getTransitVehicles().getFactory().createVehicle(Id.createVehicleId("shuttle_vehicle_e_" + i), vehicleType);
            departure.setVehicleId(vehicle.getId());

            scenario.getTransitVehicles().addVehicle(vehicle);
            route_ew.addDeparture(departure);
        }

        // create TransitLine
        var line = scheduleFactory.createTransitLine(Id.create("Shuttle-we", TransitLine.class));
        line.addRoute(route_we);
        scenario.getTransitSchedule().addTransitLine(line);

        var line1 = scheduleFactory.createTransitLine(Id.create("Shuttle-ew", TransitLine.class));
        line1.addRoute(route_ew);
        scenario.getTransitSchedule().addTransitLine(line1);

        // export input files required for simulation.
        new NetworkWriter(network).write(root.resolve("network-with-sbahn-tunnel_north.xml.gz").toString());
        new TransitScheduleWriter(scenario.getTransitSchedule()).writeFile(root.resolve("transit-Schedule-tunnel_north.xml.gz").toString());
        new MatsimVehicleWriter(scenario.getTransitVehicles()).writeFile(root.resolve("transit-vehicles-tunnel.xml.gz").toString());
    }

    private static Link createLink(String id, Node from, Node to) {

        var link = networkFactory.createLink(Id.createLinkId(id), from, to);
        link.setAllowedModes(Set.of(TransportMode.pt));
        link.setFreespeed(100);
        link.setCapacity(10000);
        return link;
    }

}

