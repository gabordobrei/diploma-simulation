package hu.dobrei.diploma.algebra;

import hu.dobrei.diploma.network.Route;

public class K_EarlyAdopterFindingAlgebra extends AbstractAlgebra<Integer> {

	@Override
	public Integer W(Route route) {
		return route == null ? Integer.MIN_VALUE : route.getFlightCount();
	}

	@Override
	public Integer bigOPlus(Integer w1, Integer w2) {
		return w1 + w2;
	}

	@Override
	public Integer phi() {
		return 0;
	}

	@Override
	public Integer best() {
		return Integer.MAX_VALUE;
	}

	@Override
	public int order(Integer w1, Integer w2) {
		return -1 * Integer.compare(w1, w2);
	}

}
