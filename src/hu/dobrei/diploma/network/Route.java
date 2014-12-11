package hu.dobrei.diploma.network;

public class Route {
	private final int length;
	private int flightCount = 0;
	private final Airport sourceAirport;
	private final Airport destinationAirport;

	public Route(Airport sourceAirport, Airport destinationAirport) {
		this.sourceAirport = sourceAirport;
		this.destinationAirport = destinationAirport;
		length = Haversine(sourceAirport, destinationAirport);
	}

	private int Haversine(Airport sourceAirport, Airport destinationAirport) {
		final Coordinate source = new Coordinate(sourceAirport);
		final Coordinate destination = new Coordinate(destinationAirport);
		final double haversineParameter = getHaversineParameter(source, destination);

		return getDistance(haversineParameter);
	}

	public final class Coordinate {
		final private double lon;
		final private double lat;

		public Coordinate(Airport airport) {
			this.lon = airport.getLongitude();
			this.lat = airport.getLatitude();
		}
	}

	private double getHaversineParameter(Coordinate s, Coordinate d) {
		final double sindLong = getDeltaSine(s.lon, d.lon);
		final double sindLat = getDeltaSine(s.lat, d.lat);
		return sindLat * sindLat + getCosineValue(s.lat) * getCosineValue(d.lat) * sindLong * sindLong;
	}

	private double getCosineValue(final double sourceLatitude) {
		return Math.cos(Math.toRadians(sourceLatitude));
	}

	private int getDistance(final double a) {
		final double b = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		return (int) Math.round(6371 * 1000 * b);
	}

	private double getDeltaSine(final double sourceLongitude, final double destinationLongitude) {
		return Math.sin(Math.toRadians(sourceLongitude - destinationLongitude) / 2.0);
	}

	public int getLength() {
		return length;
	}

	public void increaseFlightCount() {
		this.flightCount++;
	}

	public int getFlightCount() {
		return this.flightCount;
	}

	public Airport getSourceAirport() {
		return sourceAirport;
	}

	public Airport getDestinationAirport() {
		return destinationAirport;
	}

	@Override
	public String toString() {
		return "{length: " + length + ", flightCount: " + flightCount + ", sourceAirport: " + sourceAirport.getId()
				+ ", destinationAirport: " + destinationAirport.getId() + "}";
	}

	public RelaxedRoute getRelaxation() {
		return new RelaxedRoute(length, flightCount, sourceAirport.getId(), destinationAirport.getId());
	}
	
	@SuppressWarnings("unused")
	public class RelaxedRoute {
		private final int length;
		private final int flightCount;
		private final int sourceAirportId;
		private final int destinationAirportId;

		public RelaxedRoute(int length, int flightCount, int sourceAirportId, int destinationAirportId) {
			this.length = length;
			this.flightCount = flightCount;
			this.sourceAirportId = sourceAirportId;
			this.destinationAirportId = destinationAirportId;
		}
	}
}
