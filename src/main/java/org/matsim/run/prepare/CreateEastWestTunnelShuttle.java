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

public class CreateEastWestTunnelShuttle {
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
        var startNode = network.getFactory().createNode(Id.createNodeId("pt_start"), new Coord(735406.4205697721 + 100, 5693694.332504915 + 100));
        network.addNode(startNode);
        var Hbf = network.getNodes().get(Id.createNodeId("pt_008098205"));
        var Plagwitz = network.getNodes().get(Id.createNodeId("pt_008010209"));
        var endNode = network.getFactory().createNode(Id.createNodeId("pt_end"), new Coord(731432.9941460779 - 100, 5691546.204461314 - 100));
        network.addNode(endNode);

        //code example
        //var Sportforum = network.getNodes().get(Id.createNodeId("pt_sportforum"), new Coord(730606.4205697721, 5695594.332504915));
        //network.addNode(Sportforum);


        //both directions w=western direction, e=eastern direction
        var startLink_w = createLink("pt_1_w", startNode, Hbf);
        var connection_w = createLink("pt_2_w", Hbf, Plagwitz);
        var endLink_w = createLink("pt_3_w", Plagwitz, endNode);
        network.addLink(connection_w);
        network.addLink(startLink_w);
        network.addLink(endLink_w);

        var startLink_e = createLink("pt_1_e", endNode, Plagwitz);
        var connection_e = createLink("pt_2_e", Plagwitz, Hbf);
        var endLink_e = createLink("pt_3_e", Hbf, startNode);
        network.addLink(connection_e);
        network.addLink(startLink_e);
        network.addLink(endLink_e);


        // create TransitStopFacility
        var stop1_facility_w = scheduleFactory.createTransitStopFacility(Id.create("Stop_1_w", TransitStopFacility.class), startNode.getCoord(), false);
        stop1_facility_w.setLinkId(startLink_w.getId());
        var stop2_facility_w = scheduleFactory.createTransitStopFacility(Id.create("Stop_2_w", TransitStopFacility.class), endNode.getCoord(), false);
        stop2_facility_w.setLinkId(endLink_w.getId());

        scenario.getTransitSchedule().addStopFacility(stop1_facility_w);
        scenario.getTransitSchedule().addStopFacility(stop2_facility_w);

        var stop1_facility_e = scheduleFactory.createTransitStopFacility(Id.create("Stop_1_e", TransitStopFacility.class), endNode.getCoord(), false);
        stop1_facility_e.setLinkId(startLink_e.getId());
        var stop2_facility_e = scheduleFactory.createTransitStopFacility(Id.create("Stop_2_e", TransitStopFacility.class), startNode.getCoord(), false);
        stop2_facility_e.setLinkId(endLink_e.getId());

        scenario.getTransitSchedule().addStopFacility(stop1_facility_e);
        scenario.getTransitSchedule().addStopFacility(stop2_facility_e);

        // create TransitRouteStop
        var Stop1_w = scheduleFactory.createTransitRouteStop(stop1_facility_w, 0, 10);
        var Stop2_w = scheduleFactory.createTransitRouteStop(stop2_facility_w, 300, 310);

        var Stop1_e = scheduleFactory.createTransitRouteStop(stop1_facility_e, 0, 10);
        var Stop2_e = scheduleFactory.createTransitRouteStop(stop2_facility_e, 300, 310);

        // create TransitRoute
        var networkRoute_w = RouteUtils.createLinkNetworkRouteImpl(startLink_w.getId(), List.of(connection_w.getId()), endLink_w.getId());
        var route_w = scheduleFactory.createTransitRoute(Id.create("route-w", TransitRoute.class), networkRoute_w, List.of(Stop1_w, Stop2_w), "pt");

        var networkRoute_e = RouteUtils.createLinkNetworkRouteImpl(startLink_e.getId(), List.of(connection_e.getId()), startLink_e.getId());
        var route_e = scheduleFactory.createTransitRoute(Id.create("route-e", TransitRoute.class), networkRoute_e, List.of(Stop1_e, Stop2_e), "pt");

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
        new NetworkWriter(network).write(root.resolve("network-with-shuttle.xml.gz").toString());
        new TransitScheduleWriter(scenario.getTransitSchedule()).writeFile(root.resolve("transit-Schedule-shuttle.xml.gz").toString());
        new MatsimVehicleWriter(scenario.getTransitVehicles()).writeFile(root.resolve("transit-vehicles-shuttle.xml.gz").toString());
    }

    private static Link createLink(String id, Node from, Node to) {

        var link = networkFactory.createLink(Id.createLinkId(id), from, to);
        link.setAllowedModes(Set.of(TransportMode.pt));
        link.setFreespeed(100);
        link.setCapacity(10000);
        return link;
    }

}
