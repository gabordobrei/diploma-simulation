package hu.dobrei.diploma.network;

import java.util.List;

import com.google.common.collect.Iterables;

public class Airport {

	enum DST {
		E, // Europe,
		A, // US/Canada,
		S, // South America,
		O, // Australia,
		Z, // New Zealand,
		N, // None,
		U; // Unknown

		public static DST parseDST(String s) {
			switch (s) {
			case "E":
				return E;
			case "A":
				return A;
			case "S":
				return S;
			case "O":
				return O;
			case "Z":
				return Z;
			case "N":
				return N;
			case "U":
				return U;
			}
			return null;
		}

	};

	private final int airportId;
	private final String name;
	private final String city;
	private final String country;
	private final String IATA_FAA;
	private final String ICAO;

	private final double latitude;
	private final double longitude;
	private final double altitude;

	private final double zimezone;
	private final DST dst;

	public int kifok = 0, befok = 0;

	private Airport previous;
	private List<Airport> prev;
	private boolean scanned = false;

	public Airport(int airportId, String name, String city, String country, String IATA_FAA, String ICAO,
			double latitude, double longitude, double altitude, double zimezone, DST dst) {
		super();
		this.airportId = airportId;
		this.name = name;
		this.city = city;
		this.country = country;
		this.IATA_FAA = IATA_FAA;
		this.ICAO = ICAO;
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
		this.zimezone = zimezone;
		this.dst = dst;
	}

	public Airport(Iterable<String> split) {
		this.airportId = Integer.parseInt(Iterables.get(split, 0));
		this.name = Iterables.get(split, 1);
		this.city = Iterables.get(split, 2);
		this.country = Iterables.get(split, 3);
		this.IATA_FAA = Iterables.get(split, 4).equals("\\N") ? null : Iterables.get(split, 4);
		this.ICAO = Iterables.get(split, 5).equals("\\N") ? null : Iterables.get(split, 5);
		this.latitude = Double.parseDouble(Iterables.get(split, 6));
		this.longitude = Double.parseDouble(Iterables.get(split, 7));
		this.altitude = Double.parseDouble(Iterables.get(split, 8));
		this.zimezone = Double.parseDouble(Iterables.get(split, 9));
		this.dst = DST.parseDST(Iterables.get(split, 10));
	}

	public int getId() {
		return airportId;
	}

	public String getName() {
		return name;
	}

	public String getCity() {
		return city;
	}

	public String getCountry() {
		return country;
	}

	public String getIATA_FAA() {
		return IATA_FAA;
	}

	public String getICAO() {
		return ICAO;
	}

	public Double getLatitude() {
		return latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public Double getAltitude() {
		return altitude;
	}

	public double getZimezone() {
		return zimezone;
	}

	public DST getDst() {
		return dst;
	}

	public Airport getPrevious() {
		return previous;
	}

	public void setPrevious(Airport previous) {
		this.previous = previous;
	}

	public List<Airport> getPrev() {
		return prev;
	}

	public void setPrev(List<Airport> prev) {
		this.prev = prev;
	}

	@Override
	public String toString() {
		return "(" + airportId + ", " + name + ")";
	}

	public String toLongString() {
		return "Airport [airportID=" + airportId + ", name=" + name + ", city=" + city + ", country=" + country
				+ ", IATA_FAA=" + IATA_FAA + ", ICAO=" + ICAO + ", latitude=" + latitude + ", longitude=" + longitude
				+ ", altitude=" + altitude + ", zimezone=" + zimezone + ", dst=" + dst + "]";
	}

	public void scan() {
		this.scanned = true;
	}

	public boolean isScanned() {
		return this.scanned;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + airportId;
		return result;
	}
}
