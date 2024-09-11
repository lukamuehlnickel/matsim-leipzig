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

public class CreateTunnelLine {

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
        vehicleType.getCapacity().setSeats(10000);
        vehicleType.getCapacity().setStandingRoom(10000);
        scenario.getTransitVehicles().addVehicleType(vehicleType);


        // create pt nodes and links --> adapt network
        var startNode = network.getFactory().createNode(Id.createNodeId("pt_start"), new Coord(731432.9941460779 - 100, 5691546.204461314 - 100));
        network.addNode(startNode);
        var Plagwitz = network.getNodes().get(Id.createNodeId("pt_008010209"));
        var Lindenauer = network.getNodes().get(Id.createNodeId("pt_000012320"));
        var Sportforum = network.getNodes().get(Id.createNodeId("pt_000011071"));
        var Hbf = network.getNodes().get(Id.createNodeId("pt_008098205"));
        var Gerichtsweg = network.getNodes().get(Id.createNodeId("pt_000011064"));
        var Riebeckstraße = network.getNodes().get(Id.createNodeId("pt_000011330"));
        var AngerCrottendorf = network.getNodes().get(Id.createNodeId("pt_008010008"));
        var endNode = network.getFactory().createNode(Id.createNodeId("pt_end"), new Coord(738266.2826501003 + 100, 5692349.772973169 + 100));
        network.addNode(endNode);

        //both directions w=western direction, e=eastern direction
        var startLink_w = createLink("pt_1_w", startNode, Plagwitz);
        var connection1_w = createLink("pt_2_w", Plagwitz, Lindenauer);
        var connection2_w = createLink("pt_3_w", Lindenauer, Sportforum);
        var connection3_w = createLink("pt_4_w", Sportforum, Hbf);
        var connection4_w = createLink("pt_5_w", Hbf, Gerichtsweg);
        var connection5_w = createLink("pt_6_w", Gerichtsweg, Riebeckstraße);
        var connection6_w = createLink("pt_7_w", Riebeckstraße, AngerCrottendorf);
        var endLink_w = createLink("pt_8_w", AngerCrottendorf, endNode);
        network.addLink(connection1_w);
        network.addLink(connection2_w);
        network.addLink(connection3_w);
        network.addLink(connection4_w);
        network.addLink(connection5_w);
        network.addLink(connection6_w);
        network.addLink(startLink_w);
        network.addLink(endLink_w);

        var startLink_e = createLink("pt_1_e", endNode, AngerCrottendorf);
        var connection1_e = createLink("pt_2_e", AngerCrottendorf, Riebeckstraße);
        var connection2_e = createLink("pt_3_e", Riebeckstraße, Gerichtsweg);
        var connection3_e = createLink("pt_4_e", Gerichtsweg, Hbf);
        var connection4_e = createLink("pt_5_e", Hbf, Sportforum);
        var connection5_e = createLink("pt_6_e", Sportforum, Lindenauer);
        var connection6_e = createLink("pt_7_e", Lindenauer, Plagwitz);
        var endLink_e = createLink("pt_8_e", Plagwitz, startNode);
        network.addLink(connection1_e);
        network.addLink(connection2_e);
        network.addLink(connection3_e);
        network.addLink(connection4_e);
        network.addLink(connection5_e);
        network.addLink(connection6_e);
        network.addLink(startLink_e);
        network.addLink(endLink_e);


        // create TransitStopFacility
        //here you have to create TransitStop facilities for each added stop in the tunnelline
        var stopPlagwitz_w = scheduleFactory.createTransitStopFacility(Id.create("Stop_Plagwitz_w", TransitStopFacility.class), Plagwitz.getCoord(), false);
        stopPlagwitz_w.setLinkId(createLink("pt_2_w", Plagwitz, Lindenauer).getId());
        var stopLindenauer_w = scheduleFactory.createTransitStopFacility(Id.create("Stop_Lindenauer_w", TransitStopFacility.class), Lindenauer.getCoord(), false);
        stopLindenauer_w.setLinkId(createLink("pt_3_w", Lindenauer, Sportforum).getId());
        var stopSportforum_w = scheduleFactory.createTransitStopFacility(Id.create("Stop_Sportforum_w", TransitStopFacility.class), Sportforum.getCoord(), false);
        stopSportforum_w.setLinkId(createLink("pt_4_w", Sportforum, Hbf).getId());
        var stopHbf_w = scheduleFactory.createTransitStopFacility(Id.create("Stop_Hbf_w", TransitStopFacility.class), Hbf.getCoord(), false);
        stopHbf_w.setLinkId(createLink("pt_5_w", Hbf, Gerichtsweg).getId());
        var stopGerichtsweg_w = scheduleFactory.createTransitStopFacility(Id.create("Stop_Gerichtsweg_w", TransitStopFacility.class), Gerichtsweg.getCoord(), false);
        stopGerichtsweg_w.setLinkId(createLink("pt_6_w", Gerichtsweg, Riebeckstraße).getId());
        var stopRiebeckstraße_w = scheduleFactory.createTransitStopFacility(Id.create("Stop_Riebeckstraße_w", TransitStopFacility.class), Riebeckstraße.getCoord(), false);
        stopRiebeckstraße_w.setLinkId(createLink("pt_7_w", Riebeckstraße, AngerCrottendorf).getId());
        var stopAngerCrottendorf_w = scheduleFactory.createTransitStopFacility(Id.create("Stop_AngerCrottendorf_w", TransitStopFacility.class), AngerCrottendorf.getCoord(), false);
        stopAngerCrottendorf_w.setLinkId(createLink("pt_8_w", AngerCrottendorf, endNode).getId());

        scenario.getTransitSchedule().addStopFacility(stopPlagwitz_w);
        scenario.getTransitSchedule().addStopFacility(stopLindenauer_w);
        scenario.getTransitSchedule().addStopFacility(stopSportforum_w);
        scenario.getTransitSchedule().addStopFacility(stopHbf_w);
        scenario.getTransitSchedule().addStopFacility(stopGerichtsweg_w);
        scenario.getTransitSchedule().addStopFacility(stopRiebeckstraße_w);
        scenario.getTransitSchedule().addStopFacility(stopAngerCrottendorf_w);

        var stopAngerCrottendorf_e = scheduleFactory.createTransitStopFacility(Id.create("Stop_AngerCrottendorf_e", TransitStopFacility.class), AngerCrottendorf.getCoord(), false);
        stopAngerCrottendorf_e.setLinkId(createLink("pt_7_e", AngerCrottendorf, Riebeckstraße).getId());
        var stopRiebeckstraße_e = scheduleFactory.createTransitStopFacility(Id.create("Stop_Riebeckstraße_e", TransitStopFacility.class), Riebeckstraße.getCoord(), false);
        stopRiebeckstraße_e.setLinkId(createLink("pt_6_e", Riebeckstraße, Gerichtsweg).getId());
        var stopGerichtsweg_e = scheduleFactory.createTransitStopFacility(Id.create("Stop_Gerichtsweg_e", TransitStopFacility.class), Gerichtsweg.getCoord(), false);
        stopGerichtsweg_e.setLinkId(createLink("pt_5_e", Gerichtsweg, Hbf).getId());
        var stopHbf_e = scheduleFactory.createTransitStopFacility(Id.create("Stop_Hbf_e", TransitStopFacility.class), Hbf.getCoord(), false);
        stopHbf_e.setLinkId(createLink("pt_4_e", Hbf, Sportforum).getId());
        var stopSportforum_e = scheduleFactory.createTransitStopFacility(Id.create("Stop_Sportforum_e", TransitStopFacility.class), Sportforum.getCoord(), false);
        stopSportforum_e.setLinkId(createLink("pt_3_e", Sportforum, Lindenauer).getId());
        var stopLindenauer_e = scheduleFactory.createTransitStopFacility(Id.create("Stop_Lindenauer_e", TransitStopFacility.class), Lindenauer.getCoord(), false);
        stopLindenauer_e.setLinkId(createLink("pt_2_e", Lindenauer, Plagwitz).getId());
        var stopPlagwitz_e = scheduleFactory.createTransitStopFacility(Id.create("Stop_Plagwitz_e", TransitStopFacility.class), Plagwitz.getCoord(), false);
        stopPlagwitz_e.setLinkId(createLink("pt_1_e", Plagwitz, startNode).getId());

        scenario.getTransitSchedule().addStopFacility(stopAngerCrottendorf_e);
        scenario.getTransitSchedule().addStopFacility(stopRiebeckstraße_e);
        scenario.getTransitSchedule().addStopFacility(stopGerichtsweg_e);
        scenario.getTransitSchedule().addStopFacility(stopHbf_e);
        scenario.getTransitSchedule().addStopFacility(stopSportforum_e);
        scenario.getTransitSchedule().addStopFacility(stopLindenauer_e);
        scenario.getTransitSchedule().addStopFacility(stopPlagwitz_e);

        // create TransitRouteStop --- here you have to add the stopps with 120 sec each
        var stopPlagwitzStop1_w = scheduleFactory.createTransitRouteStop(stopPlagwitz_w, 0, 10);
        var stopLindenauerStop1_w = scheduleFactory.createTransitRouteStop(stopLindenauer_w, 120, 130);
        var stopSportforumStop1_w = scheduleFactory.createTransitRouteStop(stopSportforum_w, 240, 250);
        var stopHbfStop1_w = scheduleFactory.createTransitRouteStop(stopHbf_w, 360, 370);
        var stopGerichtswegStop1_w = scheduleFactory.createTransitRouteStop(stopGerichtsweg_w, 480, 490);
        var stopRiebeckstraßeStop1_w = scheduleFactory.createTransitRouteStop(stopRiebeckstraße_w, 600, 610);
        var stopAngerCrottendorfStop1_w = scheduleFactory.createTransitRouteStop(stopAngerCrottendorf_w, 720, 730);

        // east direction
        var stopAngerCrottendorfStop1_e = scheduleFactory.createTransitRouteStop(stopAngerCrottendorf_e, 0, 10);
        var stopRiebeckstraßeStop1_e = scheduleFactory.createTransitRouteStop(stopRiebeckstraße_e, 120, 130);
        var stopGerichtswegStop1_e = scheduleFactory.createTransitRouteStop(stopGerichtsweg_e, 240, 250);
        var stopHbfStop1_e = scheduleFactory.createTransitRouteStop(stopHbf_e, 360, 370);
        var stopSportforumStop1_e = scheduleFactory.createTransitRouteStop(stopSportforum_e, 480, 490);
        var stopLindenauerStop1_e = scheduleFactory.createTransitRouteStop(stopLindenauer_e, 600, 610);
        var stopPlagwitzStop1_e = scheduleFactory.createTransitRouteStop(stopPlagwitz_e, 720, 730);

        // create TransitRoute
        var networkRoute_w = RouteUtils.createLinkNetworkRouteImpl(startLink_w.getId(), List.of(connection1_w.getId(),connection2_w.getId(),connection3_w.getId(),connection4_w.getId(),connection5_w.getId(), connection6_w.getId()), endLink_w.getId());
        var networkRoute_e = RouteUtils.createLinkNetworkRouteImpl(startLink_e.getId(), List.of(connection1_e.getId(),connection2_e.getId(),connection3_e.getId(),connection4_e.getId(),connection5_e.getId(),connection6_e.getId()), startLink_e.getId());

        //modify this, add stops to list
        var route_w = scheduleFactory.createTransitRoute(Id.create("route-w", TransitRoute.class), networkRoute_w, List.of(stopPlagwitzStop1_w, stopLindenauerStop1_w, stopSportforumStop1_w, stopHbfStop1_w, stopGerichtswegStop1_w, stopRiebeckstraßeStop1_w, stopAngerCrottendorfStop1_w), "pt");
        var route_e = scheduleFactory.createTransitRoute(Id.create("route-e", TransitRoute.class), networkRoute_e, List.of(stopAngerCrottendorfStop1_e, stopRiebeckstraßeStop1_e, stopGerichtswegStop1_e, stopHbfStop1_e, stopSportforumStop1_e, stopLindenauerStop1_e, stopPlagwitzStop1_e), "pt");

        // create Departures & corresponding Vehicles
        for (int i = 9 * 3600; i < 13 * 3600; i += 300) {
            var departure = scheduleFactory.createDeparture(Id.create("departure_" + i, Departure.class), i);
            var vehicle = scenario.getTransitVehicles().getFactory().createVehicle(Id.createVehicleId("shuttle_vehicle_w_" + i), vehicleType);
            departure.setVehicleId(vehicle.getId());

            scenario.getTransitVehicles().addVehicle(vehicle);
            route_w.addDeparture(departure);
        }

        for (int i = 9 * 3600; i < 13 * 3600; i += 300) {
            var departure = scheduleFactory.createDeparture(Id.create("departure_" + i, Departure.class), i);
            var vehicle = scenario.getTransitVehicles().getFactory().createVehicle(Id.createVehicleId("shuttle_vehicle_e_" + i), vehicleType);
            departure.setVehicleId(vehicle.getId());

            scenario.getTransitVehicles().addVehicle(vehicle);
            route_e.addDeparture(departure);
        }

        // create TransitLine
        var line = scheduleFactory.createTransitLine(Id.create("Shuttle-w", TransitLine.class));
        line.addRoute(route_w);
        scenario.getTransitSchedule().addTransitLine(line);

        var line1 = scheduleFactory.createTransitLine(Id.create("Shuttle-e", TransitLine.class));
        line1.addRoute(route_e);
        scenario.getTransitSchedule().addTransitLine(line1);

        // export input files required for simulation.
        new NetworkWriter(network).write(root.resolve("network-with-sbahn-tunnel.xml.gz").toString());
        new TransitScheduleWriter(scenario.getTransitSchedule()).writeFile(root.resolve("transit-Schedule-tunnel.xml.gz").toString());
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
