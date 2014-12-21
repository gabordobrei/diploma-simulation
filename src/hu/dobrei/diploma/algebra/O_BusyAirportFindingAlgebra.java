package hu.dobrei.diploma.algebra;

import hu.dobrei.diploma.network.Route;

public class O_BusyAirportFindingAlgebra extends AbstractAlgebra<Double> {

	@Override
	public Double W(Route route) {
		return (route.getSourceAirport().nepszeruseg + route.getDestinationAirport().nepszeruseg) / 2.0;
	}

	@Override
	public Double bigOPlus(Double w1, Double w2) {
		return Math.max(w1, w2);
	}

	@Override
	public Double phi() {
		return 0.0;
	}

	@Override
	public Double best() {
		return Double.POSITIVE_INFINITY;
	}

	@Override
	public int order(Double w1, Double w2) {
		return -1 * Double.compare(w1, w2);
	}
}
