package hu.dobrei.diploma.routing;

import hu.dobrei.diploma.algebra.K_EarlyAdopterFindingAlgebra;
import hu.dobrei.diploma.algebra.LeastHopAlgebra;
import hu.dobrei.diploma.algebra.O_BusyAirportFindingAlgebra;
import hu.dobrei.diploma.algebra.ShortestAlgebra;
import hu.dobrei.diploma.network.Airport;
import hu.dobrei.diploma.network.Flight;
import hu.dobrei.diploma.network.OpenFlightsNetwork;
import hu.dobrei.diploma.network.Route;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Stopwatch;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;
import com.google.common.io.Files;

public class Simulation {
	private final Stopwatch stopwatch;
	private final OpenFlightsNetwork network = new OpenFlightsNetwork();
	private final List<Flight> flightsOfAirline;
	private final Set<Routing<Integer>> routings;

	@SuppressWarnings("unchecked")
	public Simulation() {
		stopwatch = Stopwatch.createStarted();

		try {
			parseNetwork(network);
		} catch (IOException e) {
			e.printStackTrace();
		}
		flightsOfAirline = network.getCoreFlights();

		final Routing<Integer> s = new Routing<>(network, new ShortestAlgebra());
		final Routing<Integer> l = new Routing<>(network, new LeastHopAlgebra());
		final Routing<Integer> ks = new Routing<>(network, new K_EarlyAdopterFindingAlgebra(), new ShortestAlgebra());
		final Routing<Integer> kl = new Routing<>(network, new K_EarlyAdopterFindingAlgebra(), new LeastHopAlgebra());
		final Routing<Integer> os = new Routing<>(network, new O_BusyAirportFindingAlgebra(), new ShortestAlgebra());
		final Routing<Integer> ol = new Routing<>(network, new O_BusyAirportFindingAlgebra(), new LeastHopAlgebra());

		routings = ImmutableSet.of(s, l, ks, kl, os, ol);

		printCurrentTime("Preparation time: ");
	}

	public void run() {
		String fileName;
		String result;
		String meta;
		List<List<Route>> simulatedRoutes;

		for (Routing<Integer> routing : routings) {
			restartTimer();

			simulatedRoutes = Lists.newLinkedList();
			for (Flight flight : flightsOfAirline) {
				simulatedRoutes.add(getSimulationResult(routing, flight));
			}

			fileName = getFileName(routing.getAlgebraName());
			result = processRoutes(simulatedRoutes);
			meta = getMeta(simulatedRoutes);
			saveSimulationToFile(result, meta, fileName);
			printCurrentTime(fileName);
		}
	}

	private String getMeta(List<List<Route>> simulatedRoutes) {
		// TODO Auto-generated method stub
		Table<Integer, Integer, Integer> edgeCount = HashBasedTable.create();
		for (List<Route> list : simulatedRoutes) {
			for (Route route : list) {
				route.getSourceAirport().kifok++;
				route.getDestinationAirport().befok++;
			}
		}

		Airport a;
		for (List<Route> list : simulatedRoutes) {
			for (Route route : list) {
				a = route.getSourceAirport();
				edgeCount.put(a.getId(), a.kifok, a.befok);
				a = route.getDestinationAirport();
				edgeCount.put(a.getId(), a.kifok, a.befok);
			}
		}

		List<String> meta = Lists.newLinkedList();
		meta.add("ID,Out,In,Avg");
		for (Cell<Integer, Integer, Integer> c : edgeCount.cellSet()) {
			int id = c.getRowKey();
			int kifok = c.getColumnKey();
			int befok = c.getValue();
			double avg = (kifok + befok) / 2;
			meta.add(id + "," + kifok + "," + befok + "," + avg);
		}

		return Joiner.on('\n').join(meta);
	}

	private void printCurrentTime(String fileName) {
		System.out.println(fileName + ": " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + " ms.");
	}

	private void restartTimer() {
		stopwatch.reset();
		stopwatch.start();
	}

	private String getFileName(String algebraName) {
		return algebraName + ".csv";
	}

	private List<Route> getSimulationResult(Routing<Integer> routing, Flight flight) {
		Airport sourceAirport = flight.getSourceAirport();
		Airport destinationAirport = flight.getDestinationAirport();
		routing.computeAllPreferredPathsFrom(sourceAirport);
		List<Airport> airportList = routing.getFirstPreferredPathTo(destinationAirport);
		return convertAiportListToFlightList(airportList);
	}

	private List<Route> convertAiportListToFlightList(List<Airport> airportList) {
		List<Route> routeList = Lists.newLinkedList();

		Route r;
		Airport s = airportList.get(0);
		Airport d;
		for (int i = 1; i < airportList.size(); i++) {
			d = airportList.get(i);
			r = network.getRoute(s, d);
			s = d;
			routeList.add(r);
		}

		return routeList;
	}

	private void saveSimulationToFile(String content, String meta, String fileName) {
		try {
			Files.write(content, new File("sim/" + fileName), Charsets.UTF_8);
			Files.write(meta, new File("sim/meta-" + fileName), Charsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String processRoutes(List<List<Route>> routes) {
		List<String> content = Lists.newLinkedList();
		content.add("HC,AL,OUT,IN");
		for (List<Route> route : routes) {
			String line = processListOfRoutes(route);
			content.add(line);
		}
		return Joiner.on('\n').join(content);
	}

	private String processListOfRoutes(List<Route> journey) {
		// TODO
		int hopCount = journey.size();
		long length = 0;
		// List<String> out = Lists.newLinkedList();
		// List<String> in = Lists.newLinkedList();
		for (Route route : journey) {
			length += route.getLength();
			// out.add(String.valueOf(route.getSourceAirport().getId()));
			// in.add(String.valueOf(route.getDestinationAirport().getId()));
		}

		// String outEdges = Joiner.on(';').join(out);
		// String inEdges = Joiner.on(';').join(in);

		String outEdges = String.valueOf(journey.get(0).getSourceAirport().getId());
		String inEdges = String.valueOf(journey.get(journey.size() - 1).getDestinationAirport().getId());
		List<String> line = Lists.newLinkedList();
		line.add(String.valueOf(hopCount));
		line.add(String.valueOf(length));
		line.add(outEdges);
		line.add(inEdges);
		return Joiner.on(',').join(line);
	}

	private void parseNetwork(OpenFlightsNetwork network) throws IOException {
		network.readAirlines(new File("airlines.dat"));
		network.readAirports(new File("airports.dat"));
		network.readNetwork(new File("routes.dat"));
	}

	@SuppressWarnings("unused")
	private String getPrintableRoute(Route route) {
		StringBuilder sb = new StringBuilder("");
		Airport s = route.getSourceAirport();
		Airport d = route.getDestinationAirport();

		sb.append(s.getId());
		sb.append(" -- ");
		sb.append(d.getId());
		sb.append(": ");
		sb.append(route.getLength());
		return sb.toString();
	}
}
