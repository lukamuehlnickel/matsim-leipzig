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

public class CreateEastWestTunnelS13 {
    private static final LinkNetworkRouteFactory routeFactory = new LinkNetworkRouteFactory();
    private static final NetworkFactory networkFactory = NetworkUtils.createNetwork().getFactory();
    private static final TransitScheduleFactory scheduleFactory = ScenarioUtils.createScenario(ConfigUtils.createConfig()).getTransitSchedule().getFactory();

        public static void main (String[]args){


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
            var startNode = network.getFactory().createNode(Id.createNodeId("pt_start"), new Coord(727683.5310132444 - 100, 5689963.884861143 - 100));
            network.addNode(startNode);
            var MilitzerAllee = network.getNodes().get(Id.createNodeId("pt_008012344"));
            var Karlsruherstr = network.getNodes().get(Id.createNodeId("pt_008010186"));
            var AlleeCenter = network.getNodes().get(Id.createNodeId("pt_008013070"));
            var GruenauerAllee = network.getNodes().get(Id.createNodeId("pt_008011771"));
            var Plagwitz = network.getNodes().get(Id.createNodeId("pt_008010209"));
            var Lindenauer = network.getNodes().get(Id.createNodeId("pt_000012320"));
            var Sportforum = network.getNodes().get(Id.createNodeId("pt_000011071"));
            var Hbf = network.getNodes().get(Id.createNodeId("pt_008098205"));
            var Gerichtsweg = network.getNodes().get(Id.createNodeId("pt_000011064"));
            var Riebeckstraße = network.getNodes().get(Id.createNodeId("pt_000011330"));
            var AngerCrottendorf = network.getNodes().get(Id.createNodeId("pt_008010008"));
            var endNode = network.getFactory().createNode(Id.createNodeId("pt_end"), new Coord(760260.4855673861 + 100, 5696988.698130682 + 100));
            network.addNode(endNode);
            var Engelsdorf = network.getNodes().get(Id.createNodeId("pt_008011491"));
            var Borsdorf = network.getNodes().get(Id.createNodeId("pt_008010059"));
            var Gerichshain = network.getNodes().get(Id.createNodeId("pt_008011626"));
            var Machern = network.getNodes().get(Id.createNodeId("pt_008012284"));
            var Altenbach = network.getNodes().get(Id.createNodeId("pt_008011018"));
            var Bennewitz = network.getNodes().get(Id.createNodeId("pt_008013362"));
            var Wurzen = network.getNodes().get(Id.createNodeId("pt_008013361"));

            //both directions w=western direction, we=west to east , ew=east to west
            var startLink_we = createLink("pt_start_we", startNode, MilitzerAllee);
            var connect1_we = network.getLinks().get(Id.createLinkId("pt_11272"));
            var connect2_we = network.getLinks().get(Id.createLinkId("pt_11273"));
            var connect3_we = network.getLinks().get(Id.createLinkId("pt_11274"));
            var connect4_we = network.getLinks().get(Id.createLinkId("pt_11275"));
            var connect5_we = createLink("pt_1_we", Plagwitz, Lindenauer);
            var connect6_we = createLink("pt_2_we", Lindenauer, Sportforum);
            var connect7_we = createLink("pt_3_we", Sportforum, Hbf);
            var connect8_we = createLink("pt_4_we", Hbf, Gerichtsweg);
            var connect9_we = createLink("pt_5_we", Gerichtsweg, Riebeckstraße);
            var connect10_we = createLink("pt_6_we", Riebeckstraße, AngerCrottendorf);
            var connect11_we = network.getLinks().get(Id.createLinkId("pt_11359"));
            var connect12_we = network.getLinks().get(Id.createLinkId("pt_10867"));
            var connect13_we = network.getLinks().get(Id.createLinkId("pt_11360"));
            var connect14_we = network.getLinks().get(Id.createLinkId("pt_11361"));
            var connect15_we = network.getLinks().get(Id.createLinkId("pt_11362"));
            var connect16_we = network.getLinks().get(Id.createLinkId("pt_11363"));
            var connect17_we = network.getLinks().get(Id.createLinkId("pt_11364"));

            var endLink_we = createLink("pt_end_we", Wurzen, endNode);
            network.addLink(connect5_we);
            network.addLink(connect6_we);
            network.addLink(connect7_we);
            network.addLink(connect8_we);
            network.addLink(connect9_we);
            network.addLink(connect10_we);
            network.addLink(startLink_we);
            network.addLink(endLink_we);

            var startLink_ew = createLink("pt_1_ew", endNode, Wurzen);
            var connect1_ew = network.getLinks().get(Id.createLinkId("pt_11389"));
            var connect2_ew = network.getLinks().get(Id.createLinkId("pt_11390"));
            var connect3_ew = network.getLinks().get(Id.createLinkId("pt_11391"));
            var connect4_ew = network.getLinks().get(Id.createLinkId("pt_11392"));
            var connect5_ew = network.getLinks().get(Id.createLinkId("pt_11393"));
            var connect6_ew = network.getLinks().get(Id.createLinkId("pt_10852"));
            var connect7_ew = network.getLinks().get(Id.createLinkId("pt_11394"));
            var connect8_ew = createLink("pt_2_ew", AngerCrottendorf, Riebeckstraße);
            var connect9_ew = createLink("pt_3_ew", Riebeckstraße, Gerichtsweg);
            var connect10_ew = createLink("pt_4_ew", Gerichtsweg, Hbf);
            var connect11_ew = createLink("pt_5_ew", Hbf, Sportforum);
            var connect12_ew = createLink("pt_6_ew", Sportforum, Lindenauer);
            var connect13_ew = createLink("pt_7_ew", Lindenauer, Plagwitz);
            var connect14_ew = network.getLinks().get(Id.createLinkId("pt_11266"));
            var connect15_ew = network.getLinks().get(Id.createLinkId("pt_11267"));
            var connect16_ew = network.getLinks().get(Id.createLinkId("pt_11268"));
            var connect17_ew = network.getLinks().get(Id.createLinkId("pt_11269"));
            var endLink_ew = createLink("pt_end_ew", MilitzerAllee, startNode);
            network.addLink(connect8_ew);
            network.addLink(connect9_ew);
            network.addLink(connect10_ew);
            network.addLink(connect11_ew);
            network.addLink(connect12_ew);
            network.addLink(connect13_ew);
            network.addLink(startLink_ew);
            network.addLink(endLink_ew);

            // create TransitStopFacility
            var stop1_facility_we = scheduleFactory.createTransitStopFacility(Id.create("Leipzig-Militzer Allee_we", TransitStopFacility.class), MilitzerAllee.getCoord(), false);
            stop1_facility_we.setLinkId(startLink_we.getId());

            var stop2_facility_we = scheduleFactory.createTransitStopFacility(Id.create("Leipzig-Karlsruher Str._we", TransitStopFacility.class), Karlsruherstr.getCoord(), false);
            stop2_facility_we.setLinkId(connect1_we.getId());

            var stop3_facility_we = scheduleFactory.createTransitStopFacility(Id.create("Leipzig-Allee-Center_we", TransitStopFacility.class), AlleeCenter.getCoord(), false);
            stop3_facility_we.setLinkId(connect2_we.getId());

            var stop4_facility_we = scheduleFactory.createTransitStopFacility(Id.create("Leipzig-Grünauer Allee_we", TransitStopFacility.class), GruenauerAllee.getCoord(), false);
            stop4_facility_we.setLinkId(connect3_we.getId());

            var stop5_facility_we = scheduleFactory.createTransitStopFacility(Id.create("Leipzig-Plagwitz_we", TransitStopFacility.class), Plagwitz.getCoord(), false);
            stop5_facility_we.setLinkId(connect4_we.getId());

            var stop6_facility_we = scheduleFactory.createTransitStopFacility(Id.create("Leipzig-Lindenauer_we", TransitStopFacility.class), Lindenauer.getCoord(), false);
            stop6_facility_we.setLinkId(connect5_we.getId());

            var stop7_facility_we = scheduleFactory.createTransitStopFacility(Id.create("Leipzig-Sportforum_we", TransitStopFacility.class), Sportforum.getCoord(), false);
            stop7_facility_we.setLinkId(connect6_we.getId());

            var stop8_facility_we = scheduleFactory.createTransitStopFacility(Id.create("Leipzig-Hbf_we", TransitStopFacility.class), Hbf.getCoord(), false);
            stop8_facility_we.setLinkId(connect7_we.getId());

            var stop9_facility_we = scheduleFactory.createTransitStopFacility(Id.create("Leipzig-Gerichtsweg_we", TransitStopFacility.class), Gerichtsweg.getCoord(), false);
            stop9_facility_we.setLinkId(connect8_we.getId());

            var stop10_facility_we = scheduleFactory.createTransitStopFacility(Id.create("Leipzig-Riebeckstraße_we", TransitStopFacility.class), Riebeckstraße.getCoord(), false);
            stop10_facility_we.setLinkId(connect9_we.getId());

            var stop11_facility_we = scheduleFactory.createTransitStopFacility(Id.create("Leipzig-Anger-Crottendorf_we", TransitStopFacility.class), AngerCrottendorf.getCoord(), false);
            stop11_facility_we.setLinkId(connect10_we.getId());

            var stop12_facility_we = scheduleFactory.createTransitStopFacility(Id.create("Leipzig-Engelsdorf_we", TransitStopFacility.class), Engelsdorf.getCoord(), false);
            stop12_facility_we.setLinkId(connect11_we.getId());

            var stop13_facility_we = scheduleFactory.createTransitStopFacility(Id.create("Borsdorf_we", TransitStopFacility.class), Borsdorf.getCoord(), false);
            stop13_facility_we.setLinkId(connect12_we.getId());

            var stop14_facility_we = scheduleFactory.createTransitStopFacility(Id.create("Gerichshain_we", TransitStopFacility.class), Gerichshain.getCoord(), false);
            stop14_facility_we.setLinkId(connect13_we.getId());

            var stop15_facility_we = scheduleFactory.createTransitStopFacility(Id.create("Machern_we", TransitStopFacility.class), Machern.getCoord(), false);
            stop15_facility_we.setLinkId(connect14_we.getId());

            var stop16_facility_we = scheduleFactory.createTransitStopFacility(Id.create("Altenbach_we", TransitStopFacility.class), Altenbach.getCoord(), false);
            stop16_facility_we.setLinkId(connect15_we.getId());

            var stop17_facility_we = scheduleFactory.createTransitStopFacility(Id.create("Bennewitz_we", TransitStopFacility.class), Bennewitz.getCoord(), false);
            stop17_facility_we.setLinkId(connect16_we.getId());

            var stop18_facility_we = scheduleFactory.createTransitStopFacility(Id.create("Wurzen_we", TransitStopFacility.class), Wurzen.getCoord(), false);
            stop18_facility_we.setLinkId(connect17_we.getId());

            // Erster Stopp bleibt gleich, weil die Nummerierung bei 1 beginnt
            var stop1_facility_ew = scheduleFactory.createTransitStopFacility(Id.create("Wurzen_ew", TransitStopFacility.class), Wurzen.getCoord(), false);
            stop1_facility_ew.setLinkId(startLink_ew.getId());

            var stop2_facility_ew = scheduleFactory.createTransitStopFacility(Id.create("Bennewitz_ew", TransitStopFacility.class), Bennewitz.getCoord(), false);
            stop2_facility_ew.setLinkId(connect1_ew.getId());

            var stop3_facility_ew = scheduleFactory.createTransitStopFacility(Id.create("Altenbach_ew", TransitStopFacility.class), Altenbach.getCoord(), false);
            stop3_facility_ew.setLinkId(connect2_ew.getId());

            var stop4_facility_ew = scheduleFactory.createTransitStopFacility(Id.create("Machern_ew", TransitStopFacility.class), Machern.getCoord(), false);
            stop4_facility_ew.setLinkId(connect3_ew.getId());

            var stop5_facility_ew = scheduleFactory.createTransitStopFacility(Id.create("Gerichshain_ew", TransitStopFacility.class), Gerichshain.getCoord(), false);
            stop5_facility_ew.setLinkId(connect4_ew.getId());

            var stop6_facility_ew = scheduleFactory.createTransitStopFacility(Id.create("Borsdorf_ew", TransitStopFacility.class), Borsdorf.getCoord(), false);
            stop6_facility_ew.setLinkId(connect5_ew.getId());

            var stop7_facility_ew = scheduleFactory.createTransitStopFacility(Id.create("Leipzig-Engelsdorf_ew", TransitStopFacility.class), Engelsdorf.getCoord(), false);
            stop7_facility_ew.setLinkId(connect6_ew.getId());

            var stop8_facility_ew = scheduleFactory.createTransitStopFacility(Id.create("Leipzig-Anger-Crottendorf_ew", TransitStopFacility.class), AngerCrottendorf.getCoord(), false);
            stop8_facility_ew.setLinkId(connect7_ew.getId());

            var stop9_facility_ew = scheduleFactory.createTransitStopFacility(Id.create("Leipzig-Riebeckstraße_ew", TransitStopFacility.class), Riebeckstraße.getCoord(), false);
            stop9_facility_ew.setLinkId(connect8_ew.getId());

            var stop10_facility_ew = scheduleFactory.createTransitStopFacility(Id.create("Leipzig-Gerichtsweg_ew", TransitStopFacility.class), Gerichtsweg.getCoord(), false);
            stop10_facility_ew.setLinkId(connect9_ew.getId());

            var stop11_facility_ew = scheduleFactory.createTransitStopFacility(Id.create("Leipzig-Hbf_ew", TransitStopFacility.class), Hbf.getCoord(), false);
            stop11_facility_ew.setLinkId(connect10_ew.getId());

            var stop12_facility_ew = scheduleFactory.createTransitStopFacility(Id.create("Leipzig-Sportforum_ew", TransitStopFacility.class), Sportforum.getCoord(), false);
            stop12_facility_ew.setLinkId(connect11_ew.getId());

            var stop13_facility_ew = scheduleFactory.createTransitStopFacility(Id.create("Leipzig-Lindenauer_ew", TransitStopFacility.class), Lindenauer.getCoord(), false);
            stop13_facility_ew.setLinkId(connect12_ew.getId());

            var stop14_facility_ew = scheduleFactory.createTransitStopFacility(Id.create("Leipzig-Plagwitz_ew", TransitStopFacility.class), Plagwitz.getCoord(), false);
            stop14_facility_ew.setLinkId(connect13_ew.getId());

            var stop15_facility_ew = scheduleFactory.createTransitStopFacility(Id.create("Leipzig-Grünauer Allee_ew", TransitStopFacility.class), GruenauerAllee.getCoord(), false);
            stop15_facility_ew.setLinkId(connect14_ew.getId());

            var stop16_facility_ew = scheduleFactory.createTransitStopFacility(Id.create("Leipzig-Allee-Center_ew", TransitStopFacility.class), AlleeCenter.getCoord(), false);
            stop16_facility_ew.setLinkId(connect15_ew.getId());

            var stop17_facility_ew = scheduleFactory.createTransitStopFacility(Id.create("Leipzig-Karlsruher Str._ew", TransitStopFacility.class), Karlsruherstr.getCoord(), false);
            stop17_facility_ew.setLinkId(connect16_ew.getId());

            var stop18_facility_ew = scheduleFactory.createTransitStopFacility(Id.create("Leipzig-Militzer Allee_ew", TransitStopFacility.class), startNode.getCoord(), false);
            stop18_facility_ew.setLinkId(connect17_ew.getId());

            //add stop facilities for we direction
            scenario.getTransitSchedule().addStopFacility(stop1_facility_we);
            scenario.getTransitSchedule().addStopFacility(stop2_facility_we);
            scenario.getTransitSchedule().addStopFacility(stop3_facility_we);
            scenario.getTransitSchedule().addStopFacility(stop4_facility_we);
            scenario.getTransitSchedule().addStopFacility(stop5_facility_we);
            scenario.getTransitSchedule().addStopFacility(stop6_facility_we);
            scenario.getTransitSchedule().addStopFacility(stop7_facility_we);
            scenario.getTransitSchedule().addStopFacility(stop8_facility_we);
            scenario.getTransitSchedule().addStopFacility(stop9_facility_we);
            scenario.getTransitSchedule().addStopFacility(stop10_facility_we);
            scenario.getTransitSchedule().addStopFacility(stop11_facility_we);
            scenario.getTransitSchedule().addStopFacility(stop12_facility_we);
            scenario.getTransitSchedule().addStopFacility(stop13_facility_we);
            scenario.getTransitSchedule().addStopFacility(stop14_facility_we);
            scenario.getTransitSchedule().addStopFacility(stop15_facility_we);
            scenario.getTransitSchedule().addStopFacility(stop16_facility_we);
            scenario.getTransitSchedule().addStopFacility(stop17_facility_we);
            scenario.getTransitSchedule().addStopFacility(stop18_facility_we);

            //same for ew direction
            scenario.getTransitSchedule().addStopFacility(stop1_facility_ew);
            scenario.getTransitSchedule().addStopFacility(stop2_facility_ew);
            scenario.getTransitSchedule().addStopFacility(stop3_facility_ew);
            scenario.getTransitSchedule().addStopFacility(stop4_facility_ew);
            scenario.getTransitSchedule().addStopFacility(stop5_facility_ew);
            scenario.getTransitSchedule().addStopFacility(stop6_facility_ew);
            scenario.getTransitSchedule().addStopFacility(stop7_facility_ew);
            scenario.getTransitSchedule().addStopFacility(stop8_facility_ew);
            scenario.getTransitSchedule().addStopFacility(stop9_facility_ew);
            scenario.getTransitSchedule().addStopFacility(stop10_facility_ew);
            scenario.getTransitSchedule().addStopFacility(stop11_facility_ew);
            scenario.getTransitSchedule().addStopFacility(stop12_facility_ew);
            scenario.getTransitSchedule().addStopFacility(stop13_facility_ew);
            scenario.getTransitSchedule().addStopFacility(stop14_facility_ew);
            scenario.getTransitSchedule().addStopFacility(stop15_facility_ew);
            scenario.getTransitSchedule().addStopFacility(stop16_facility_ew);
            scenario.getTransitSchedule().addStopFacility(stop17_facility_ew);
            scenario.getTransitSchedule().addStopFacility(stop18_facility_ew);

            // create TransitRouteStop
            var Stop1_we = scheduleFactory.createTransitRouteStop(stop1_facility_we, 0, 10);
            var Stop2_we = scheduleFactory.createTransitRouteStop(stop2_facility_we, 120, 130);
            var Stop3_we = scheduleFactory.createTransitRouteStop(stop3_facility_we, 240, 250);
            var Stop4_we = scheduleFactory.createTransitRouteStop(stop4_facility_we, 360, 370);
            var Stop5_we = scheduleFactory.createTransitRouteStop(stop5_facility_we, 540, 550);
            var Stop6_we = scheduleFactory.createTransitRouteStop(stop6_facility_we, 660, 670);
            var Stop7_we = scheduleFactory.createTransitRouteStop(stop7_facility_we, 780, 790);
            var Stop8_we = scheduleFactory.createTransitRouteStop(stop8_facility_we, 900, 910);
            var Stop9_we = scheduleFactory.createTransitRouteStop(stop9_facility_we, 1020, 1030);
            var Stop10_we = scheduleFactory.createTransitRouteStop(stop10_facility_we, 1140, 1150);
            var Stop11_we = scheduleFactory.createTransitRouteStop(stop11_facility_we, 1260, 1270);
            var Stop12_we = scheduleFactory.createTransitRouteStop(stop12_facility_we, 1620, 1630);
            var Stop13_we = scheduleFactory.createTransitRouteStop(stop13_facility_we, 1860, 1870);
            var Stop14_we = scheduleFactory.createTransitRouteStop(stop14_facility_we, 2040, 2050);
            var Stop15_we = scheduleFactory.createTransitRouteStop(stop15_facility_we, 2220, 2230);
            var Stop16_we = scheduleFactory.createTransitRouteStop(stop16_facility_we, 2400, 2410);
            var Stop17_we = scheduleFactory.createTransitRouteStop(stop17_facility_we, 2580, 2590);
            var Stop18_we = scheduleFactory.createTransitRouteStop(stop18_facility_we, 2760, 2770);

            var Stop1_ew = scheduleFactory.createTransitRouteStop(stop1_facility_ew, 0, 10);
            var Stop2_ew = scheduleFactory.createTransitRouteStop(stop2_facility_ew, 180, 190);
            var Stop3_ew = scheduleFactory.createTransitRouteStop(stop3_facility_ew, 360, 370);
            var Stop4_ew = scheduleFactory.createTransitRouteStop(stop4_facility_ew, 540, 550);
            var Stop5_ew = scheduleFactory.createTransitRouteStop(stop5_facility_ew, 720, 730);
            var Stop6_ew = scheduleFactory.createTransitRouteStop(stop6_facility_ew, 900, 910);
            var Stop7_ew = scheduleFactory.createTransitRouteStop(stop7_facility_ew, 1140, 1150);
            var Stop8_ew = scheduleFactory.createTransitRouteStop(stop8_facility_ew, 1500, 1510);
            var Stop9_ew = scheduleFactory.createTransitRouteStop(stop9_facility_ew, 1620, 1630);
            var Stop10_ew = scheduleFactory.createTransitRouteStop(stop10_facility_ew, 1740, 1750);
            var Stop11_ew = scheduleFactory.createTransitRouteStop(stop11_facility_ew, 1860, 1870);
            var Stop12_ew = scheduleFactory.createTransitRouteStop(stop12_facility_ew, 1980, 1990);
            var Stop13_ew = scheduleFactory.createTransitRouteStop(stop13_facility_ew, 2100, 2110);
            var Stop14_ew = scheduleFactory.createTransitRouteStop(stop14_facility_ew, 2220, 2230);
            var Stop15_ew = scheduleFactory.createTransitRouteStop(stop15_facility_ew, 2400, 2410);
            var Stop16_ew = scheduleFactory.createTransitRouteStop(stop16_facility_ew, 2520, 2530);
            var Stop17_ew = scheduleFactory.createTransitRouteStop(stop17_facility_ew, 2640, 2650);
            var Stop18_ew = scheduleFactory.createTransitRouteStop(stop18_facility_ew, 2760, 2770);

            // create TransitRoute
            var networkRoute_we = RouteUtils.createLinkNetworkRouteImpl(startLink_we.getId(), List.of(connect1_we.getId(),connect2_we.getId(),
                    connect3_we.getId(),connect4_we.getId(),connect5_we.getId(),
                    connect6_we.getId(),connect7_we.getId(),connect8_we.getId(),
                    connect9_we.getId(),connect10_we.getId(),connect11_we.getId(),
                    connect12_we.getId(),connect13_we.getId(),connect14_we.getId(),
                    connect15_we.getId(), connect16_we.getId(),connect17_we.getId()), endLink_we.getId());
            var route_we = scheduleFactory.createTransitRoute(Id.create("S13-we", TransitRoute.class), networkRoute_we,
                    List.of(Stop1_we, Stop2_we, Stop3_we, Stop4_we, Stop5_we, Stop6_we, Stop7_we, Stop8_we,
                            Stop9_we, Stop10_we, Stop11_we, Stop12_we, Stop13_we, Stop14_we, Stop15_we, Stop16_we, Stop17_we, Stop18_we),"pt");

            var networkRoute_ew = RouteUtils.createLinkNetworkRouteImpl(startLink_ew.getId(), List.of(connect1_ew.getId(),connect2_ew.getId(),
                    connect3_ew.getId(),connect4_ew.getId(),connect5_ew.getId(),
                    connect6_ew.getId(),connect7_ew.getId(),connect8_ew.getId(),
                    connect9_ew.getId(),connect10_ew.getId(),connect11_ew.getId(),
                    connect12_ew.getId(),connect13_ew.getId(),connect14_ew.getId(),
                    connect15_ew.getId(), connect16_ew.getId(),connect17_ew.getId()), endLink_ew.getId());
            var route_ew = scheduleFactory.createTransitRoute(Id.create("S13-ew", TransitRoute.class), networkRoute_ew,
                    List.of(Stop1_ew, Stop2_ew, Stop3_ew, Stop4_ew, Stop5_ew, Stop6_ew, Stop7_ew, Stop8_ew,
                            Stop9_ew, Stop10_ew, Stop11_ew, Stop12_ew, Stop13_ew, Stop14_ew, Stop15_ew, Stop16_ew, Stop17_ew, Stop18_ew),"pt");

            // create Departures & corresponding Vehicles
            for (int i = 9 * 3600; i < 13 * 3600; i += 300) {
                var departure = scheduleFactory.createDeparture(Id.create("departure_" + i, Departure.class), i);
                var vehicle = scenario.getTransitVehicles().getFactory().createVehicle(Id.createVehicleId("shuttle_vehicle_w_" + i), vehicleType);
                departure.setVehicleId(vehicle.getId());

                scenario.getTransitVehicles().addVehicle(vehicle);
                route_we.addDeparture(departure);
            }

            for (int i = 9 * 3600; i < 13 * 3600; i += 300) {
                var departure = scheduleFactory.createDeparture(Id.create("departure_" + i, Departure.class), i);
                var vehicle = scenario.getTransitVehicles().getFactory().createVehicle(Id.createVehicleId("shuttle_vehicle_e_" + i), vehicleType);
                departure.setVehicleId(vehicle.getId());

                scenario.getTransitVehicles().addVehicle(vehicle);
                route_ew.addDeparture(departure);
            }

            // create TransitLine
            var line = scheduleFactory.createTransitLine(Id.create("S13_we", TransitLine.class));
            line.addRoute(route_we);
            scenario.getTransitSchedule().addTransitLine(line);

            var line1 = scheduleFactory.createTransitLine(Id.create("S13_ew", TransitLine.class));
            line1.addRoute(route_ew);
            scenario.getTransitSchedule().addTransitLine(line1);

            // export input files required for simulation.
            new NetworkWriter(network).write(root.resolve("network-with-sbahn-complete.xml.gz").toString());
            new TransitScheduleWriter(scenario.getTransitSchedule()).writeFile(root.resolve("transit-Schedule-complete.xml.gz").toString());
            new MatsimVehicleWriter(scenario.getTransitVehicles()).writeFile(root.resolve("transit-vehicles-complete.xml.gz").toString());
        }

        private static Link createLink(String id, Node from, Node to) {

            var link = networkFactory.createLink(Id.createLinkId(id), from, to);
            link.setAllowedModes(Set.of(TransportMode.pt));
            link.setFreespeed(100);
            link.setCapacity(10000);
            return link;
        }

}
