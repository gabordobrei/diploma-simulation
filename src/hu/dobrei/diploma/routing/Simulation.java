package hu.dobrei.diploma.routing;

import hu.dobrei.diploma.algebra.K_EarlyAdopterFindingAlgebra;
import hu.dobrei.diploma.algebra.LeastHopAlgebra;
import hu.dobrei.diploma.algebra.O_BusyAirportFindingAlgebra;
import hu.dobrei.diploma.algebra.ShortestAlgebra;
import hu.dobrei.diploma.network.Airport;
import hu.dobrei.diploma.network.Flight;
import hu.dobrei.diploma.network.OpenFlightsNetwork;
import hu.dobrei.diploma.network.Route;
import hu.dobrei.diploma.network.Route.RelaxedRoute;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Charsets;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

		routings = ImmutableSet.of(ks, kl, os, ol, s, l);

		printCurrentTime("Preparation time: ");
	}

	public void run() {
		String fileName;
		List<List<Route>> simulatedRoutes;

		for (Routing<Integer> routing : routings) {
			restartTimer();

			simulatedRoutes = Lists.newLinkedList();
			for (Flight flight : flightsOfAirline) {
				simulatedRoutes.add(getSimulationResult(routing, flight));
			}

			fileName = getFileName(routing.getAlgebra());
			//saveSimulationToFile(simulatedRoutes, fileName);
			printCurrentTime(fileName);
		}
	}

	private void printCurrentTime(String fileName) {
		System.out.println(fileName + ": " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + " ms.");
	}

	private void restartTimer() {
		stopwatch.reset();
		stopwatch.start();
	}

	private String getSimpleName(Object obj) {
		return obj.getClass().getSimpleName();
	}

	private String getFileName(Object obj) {
		String algebraName = getSimpleName(obj);
		return "sim/" + algebraName + ".json";
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

	private void saveSimulationToFile(List<List<Route>> routes, String fileName) {
		List<List<RelaxedRoute>> toSave = Lists.newLinkedList();
		for (List<Route> list : routes) {
			List<RelaxedRoute> line = Lists.newLinkedList();
			for (Route route : list) {
				line.add(route.getRelaxation());
			}
			toSave.add(line);
		}

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		try {
			Files.write(gson.toJson(toSave), new File(fileName), Charsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
