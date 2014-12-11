package hu.dobrei.diploma.algebra.extra;

import hu.dobrei.diploma.algebra.AbstractAlgebra;
import hu.dobrei.diploma.network.Route;

public class InfectionLimitAlgebra extends AbstractAlgebra<Double> {

	public InfectionLimitAlgebra() {
		throw new UnsupportedOperationException("\n\nEnnek az algebrának nincs is értelme :D\n");
	}

	@Override
	public Double W(Route route) {
		// TODO W
		return null;
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
		return 1.0;
	}

	@Override
	public int order(Double w1, Double w2) {
		return -1 * Double.compare(w1, w2);
	}

}
