package hu.dobrei.diploma.network;

import com.google.common.collect.Iterables;

public class Airline implements Comparable<Airline> {

	private final int airlineId;
	private final String name;
	private final String alias;
	private final String IATA;
	private final String ICAO;
	private final String callsign;
	private final String country;
	private final boolean active;
	public int flightCount = 0;

	public Airline(int airlineId, String name, String alias, String IATA, String ICAO, String callsign, String country,
			boolean active) {
		super();
		this.airlineId = airlineId;
		this.name = name;
		this.alias = alias;
		this.IATA = IATA;
		this.ICAO = ICAO;
		this.callsign = callsign;
		this.country = country;
		this.active = active;
	}

	public Airline(Iterable<String> split) {
		this.airlineId = Integer.parseInt(Iterables.get(split, 0));
		this.name = Iterables.get(split, 1);
		this.alias = Iterables.get(split, 2);
		IATA = Iterables.get(split, 3);
		ICAO = Iterables.get(split, 4);
		this.callsign = Iterables.get(split, 5);
		this.country = Iterables.get(split, 6);
		this.active = Boolean.parseBoolean(Iterables.get(split, 7));
	}

	public int getId() {
		return airlineId;
	}

	public String getName() {
		return name;
	}

	public String getAlias() {
		return alias;
	}

	public String getIATA() {
		return IATA;
	}

	public String getICAO() {
		return ICAO;
	}

	public String getCallsign() {
		return callsign;
	}

	public String getCountry() {
		return country;
	}

	public boolean isActive() {
		return active;
	}

	@Override
	public int compareTo(Airline o) {
		return -1 * Integer.compare(flightCount, o.flightCount);
	}
}
