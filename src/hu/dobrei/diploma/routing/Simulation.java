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
import java.util.concurrent.TimeUnit;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Stopwatch;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;
import com.google.common.io.Files;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class Simulation {
	private final Stopwatch stopwatch;
	private final OpenFlightsNetwork network = new OpenFlightsNetwork();
	private final List<Flight> flightsOfAirline;
	private final List<Routing> routings;

	public Simulation() {
		stopwatch = Stopwatch.createStarted();

		try {
			parseNetwork(network);
		} catch (IOException e) {
			e.printStackTrace();
		}
		flightsOfAirline = network.getCoreFlights();

		final Routing<Integer, Integer> s = new Routing<>(network, new ShortestAlgebra());
		final Routing<Integer, Integer> l = new Routing<>(network, new LeastHopAlgebra());
		final Routing<Integer, Integer> ls = new Routing<>(network, new LeastHopAlgebra(), new ShortestAlgebra());
		final Routing<Integer, Integer> sl = new Routing<>(network, new ShortestAlgebra(), new LeastHopAlgebra());
		final Routing<Integer, Integer> ks = new Routing<>(network, new K_EarlyAdopterFindingAlgebra(), new ShortestAlgebra());
		final Routing<Integer, Integer> kl = new Routing<>(network, new K_EarlyAdopterFindingAlgebra(), new LeastHopAlgebra());
		final Routing<Double, Integer> os = new Routing<>(network, new O_BusyAirportFindingAlgebra(), new ShortestAlgebra());
		final Routing<Double, Integer> ol = new Routing<>(network, new O_BusyAirportFindingAlgebra(), new LeastHopAlgebra());

		routings = Lists.newLinkedList();
//		routings.add(s);
//		routings.add(l);
//		routings.add(ls);
//		routings.add(sl);
//		routings.add(ks);
//		routings.add(kl);
		routings.add(os);
		routings.add(ol);

		printCurrentTime("Preparation time: ");
	}

	
	public void run() {
		String fileName;
		String result;
		String meta;
		List<List<Route>> simulatedRoutes;

		/*-
		restartTimer();
		simulatedRoutes = Lists.newLinkedList();
		List<Route> routeList;
		Airport u, v;
		for (Flight flight : flightsOfAirline) {
			u = flight.getSourceAirport();
			v = flight.getDestinationAirport();
			Route r = network.getRoute(u, v);
			routeList = Lists.newLinkedList();
			routeList.add(r);
			simulatedRoutes.add(routeList);
		}
		fileName = "default.csv";
		result = processRoutes(simulatedRoutes);
		meta = getMeta(simulatedRoutes);
		saveSimulationToFile(result, meta, fileName);
		printCurrentTime(fileName);
		//*/

		for (Routing routing : routings) {
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
		Table<Integer, Integer, Integer> edgeCount = HashBasedTable.create();
		for (List<Route> list : simulatedRoutes) {
			for (Route route : list) {
				route.getSourceAirport().kifok = 0;
				route.getDestinationAirport().befok = 0;
			}
		}
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
		meta.add("ID,Out,In");
		for (Cell<Integer, Integer, Integer> c : edgeCount.cellSet()) {
			int id = c.getRowKey();
			int kifok = c.getColumnKey();
			int befok = c.getValue();
			meta.add(id + "," + kifok + "," + befok);
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

	private List<Route> getSimulationResult(Routing routing, Flight flight) {
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
		content.add("OUT,IN,HC,AL,DAL");
		for (List<Route> route : routes) {
			String line = processListOfRoutes(route);
			content.add(line);
		}
		return Joiner.on('\n').join(content);
	}

	private String processListOfRoutes(List<Route> journey) {
		int hopCount = journey.size();
		long length = 0;
		for (Route route : journey) {
			length += route.getLength();
		}
		if (journey.size() > 1) {
			System.out.println(journey);
		}
		Airport s = journey.get(0).getSourceAirport();
		Airport d = journey.get(journey.size() - 1).getDestinationAirport();
		String outEdges = String.valueOf(s.getId());
		String inEdges = String.valueOf(d.getId());
		Route r = network.getRoute(s, d);
		if (journey.size() > 1) {
			System.err.println(r);
		}
		long defaultLength = r.getLength();
		List<String> line = Lists.newLinkedList();
		line.add(outEdges);
		line.add(inEdges);
		line.add(String.valueOf(hopCount));
		line.add(String.valueOf(length));
		line.add(String.valueOf(defaultLength));
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
