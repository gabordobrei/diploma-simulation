package hu.dobrei.diploma.network;

public class Flight {

	private static int ID;
	private final int id;
	private final Airline airline;
	private final Airport sourceAirport;
	private final Airport destinationAirport;
	
	public Flight(Airline airline, Airport sourceAirport, Airport destinationAirport) {
		this.id = Flight.ID++;
		this.airline = airline;
		this.sourceAirport = sourceAirport;
		this.destinationAirport = destinationAirport;
	}	

	public int getId() {
		return id;
	}

	public Airline getAirline() {
		return airline;
	}

	public Airport getSourceAirport() {
		return sourceAirport;
	}

	public Airport getDestinationAirport() {
		return destinationAirport;
	}
}
