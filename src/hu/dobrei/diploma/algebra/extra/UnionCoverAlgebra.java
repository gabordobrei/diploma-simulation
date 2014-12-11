package hu.dobrei.diploma.algebra.extra;

import hu.dobrei.diploma.algebra.AbstractAlgebra;
import hu.dobrei.diploma.network.Route;

public class UnionCoverAlgebra extends AbstractAlgebra<Integer> {

	public UnionCoverAlgebra() {
		throw new UnsupportedOperationException("\n\nEnnek az algebrának nincs is értelme :D\n");
	}

	@Override
	public Integer W(Route route) {
		// TODO W
		return null;
	}

	@Override
	public Integer bigOPlus(Integer w1, Integer w2) {
		return (w1 * w2) == 0 ? 0 : (w1 + w2);
	}

	@Override
	public Integer phi() {
		return Integer.MAX_VALUE;
	}

	@Override
	public Integer best() {
		return 0;
	}

	@Override
	public int order(Integer w1, Integer w2) {
		return Integer.compare(w1, w2);
	}

}
