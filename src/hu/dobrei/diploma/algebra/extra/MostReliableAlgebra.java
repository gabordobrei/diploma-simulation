package hu.dobrei.diploma.algebra.extra;

import hu.dobrei.diploma.algebra.AbstractAlgebra;
import hu.dobrei.diploma.network.Route;

public class MostReliableAlgebra extends AbstractAlgebra<Double> {

	@Override
	public Double W(Route route) {
		// TODO W
		return null;
	}

	@Override
	public Double bigOPlus(Double w1, Double w2) {
		return w1 * w2;
	}

	@Override
	public Double phi() {
		return 0.0;
	}

	@Override
	public Double best() {
		return 1.0;
	}

	// Itt a nagyobb érték a jobb
	@Override
	public int order(Double w1, Double w2) {
		return -1 * Double.compare(w1, w2);
	}

}