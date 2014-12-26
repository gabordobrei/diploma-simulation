package hu.dobrei.diploma.network;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.CharMatcher;
import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;

public class OpenFlightsNetwork {
	private final Map<Integer, Airport> airports;
	private final Map<Integer, Airline> airlines;
	private final Map<Integer, Flight> flights;
	private final Table<Airport, Airport, Route> innerGraph;
	private final List<Flight> coreFlights;
	private static int d;

	private int size;
	private final Set<Integer> coreAirlines = ImmutableSet.of(5209, 4296, 2009, 24, 5265, 751, 1767, 1758, 4547, 3320);

	public OpenFlightsNetwork() {
		this.airports = Maps.newHashMap();
		this.airlines = Maps.newHashMap();
		this.flights = Maps.newHashMap();
		this.innerGraph = HashBasedTable.create();
		this.coreFlights = Lists.newLinkedList();

		OpenFlightsNetwork.d = 1;
	}

	public static int d() {
		return d;
	}

	public Map<Integer, Airport> getAirports() {
		return airports;
	}

	public Map<Integer, Airline> getAirlines() {
		return airlines;
	}

	public Map<Integer, Flight> getFlights() {
		return flights;
	}

	public Flight getFlight(Airport sourceAirport, Airport destinationAirport, Airline airline) {
		for (Flight flight : flights.values()) {
			if (flight.getAirline().equals(airline) && flight.getSourceAirport().equals(sourceAirport)
					&& flight.getDestinationAirport().equals(destinationAirport)) {
				return flight;
			}
		}

		return null;
	}

	public Route getRoute(Airport u, Airport v) {
		return innerGraph.get(u, v);
	}

	public Airport getAirport(int airportId) {
		return airports.get(airportId);
	}

	public Route getRoute(int sourceAirportId, int destinationAirportId) {
		Airport sourceAirport = airports.get(sourceAirportId);
		Airport destinationAirport = airports.get(destinationAirportId);
		return this.getRoute(sourceAirport, destinationAirport);
	}

	public boolean hasFlightBetween(Airport sourceAirport, Airport destinationAirport) {
		return innerGraph.contains(sourceAirport, destinationAirport);
	}

	public Set<Airport> neighboursOf(Airport airport) {
		return innerGraph.row(airport).keySet();
	}

	public int size() {
		return size;
	}

	public void readAirlines(File file) throws IOException {
		Files.readLines(file, Charsets.UTF_8, new LineProcessor<Void>() {

			@Override
			public Void getResult() {
				return null;
			}

			@Override
			public boolean processLine(String airlineString) throws IOException {
				Iterable<String> args = Splitter.on(',').split(CharMatcher.is('\"').removeFrom(airlineString));
				try {
					Airline airline = new Airline(args);
					airlines.put(airline.getId(), airline);
				} catch (NumberFormatException e) {
				}
				return true;
			}
		});
	}

	public void readAirports(File file) throws IOException {
		Files.readLines(file, Charsets.UTF_8, new LineProcessor<Void>() {

			@Override
			public Void getResult() {
				return null;
			}

			@Override
			public boolean processLine(String airportString) throws IOException {
				Iterable<String> args = Splitter.on(',').split(CharMatcher.is('\"').removeFrom(airportString));
				try {
					Airport airport = new Airport(args);
					airports.put(airport.getId(), airport);
				} catch (NumberFormatException e) {
				}
				return true;
			}
		});
	}

	public void readNetwork(File file) throws IOException {
		Files.readLines(file, Charsets.UTF_8, new LineProcessor<Void>() {

			@Override
			public Void getResult() {
				return null;
			}

			@Override
			public boolean processLine(String flightString) throws IOException {
				if (airports.isEmpty() || airlines.isEmpty()) {
					return false;
				}

				List<String> args = Splitter.on(',').splitToList(flightString);

				try {
					Flight parsedFlight = parseFlight(args);
					flights.put(parsedFlight.getId(), parsedFlight);

					Airport sourceAirport = parsedFlight.getSourceAirport();
					Airport destinationAirport = parsedFlight.getDestinationAirport();
					parsedFlight.getAirline().flightCount++;
					
					if (!innerGraph.contains(sourceAirport, destinationAirport)) {
						innerGraph.put(sourceAirport, destinationAirport, new Route(sourceAirport, destinationAirport));
					}
					innerGraph.get(sourceAirport, destinationAirport).increaseFlightCount();

					if (isCoreFlight(parsedFlight)) {
						coreFlights.add(parsedFlight);
					}

				} catch (IllegalArgumentException e) {
				}
				return true;
			}

			private boolean isCoreFlight(Flight parsedFlight) {
				int airLineId = parsedFlight.getAirline().getId();
				return coreAirlines.contains(airLineId);
			}

			private Flight parseFlight(List<String> args) throws IllegalArgumentException {
				Airline airline = airlines.get(Integer.parseInt(args.get(1)));
				Airport sourceAirport = airports.get(Integer.parseInt(args.get(3)));
				Airport destinationAirport = airports.get(Integer.parseInt(args.get(5)));

				if (airline == null || sourceAirport == null || destinationAirport == null) {
					throw new IllegalArgumentException();
				}

				return new Flight(airline, sourceAirport, destinationAirport);
			}
		});

		this.size = innerGraph.size();
	}

	public List<Flight> getFlightsbyAirline(int airlineId) {
		Airline airline = airlines.get(airlineId);
		return this.getFlightsbyAirline(airline);
	}

	public List<Flight> getFlightsbyAirline(Airline airline) {
		List<Flight> f = Lists.newLinkedList();
		for (Flight flight : flights.values()) {
			if (flight.getAirline().equals(airline)) {
				f.add(flight);
			}
		}

		return f;
	}

	public List<Flight> getCoreFlights() {
		return coreFlights;
	}
}
