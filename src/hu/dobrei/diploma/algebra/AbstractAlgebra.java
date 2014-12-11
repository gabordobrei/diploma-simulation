package hu.dobrei.diploma.algebra;

import hu.dobrei.diploma.network.Route;

import java.util.List;

public abstract class AbstractAlgebra<T> {

	public abstract T W(Route route);

	public abstract T bigOPlus(T w1, T w2);

	public abstract T phi();

	public abstract T best();

	/**
	 * 
	 * @param w1
	 *            1. él súlya
	 * @param w2
	 *            2. él súlya
	 * @return <0 ha az első jobb, >0 ha a második, és 0, ha ugyanolyan
	 */
	public abstract int order(T w1, T w2);

	public List<Route> getPreferredPath(List<Route> p1, List<Route> p2) {
		return order(p1, p2) < 0 ? p1 : p2;
	}

	public T W(List<Route> path) {
		T sum = W(path.get(0));
		for (int i = 1; i < path.size(); i++) {
			Route route = path.get(i);
			sum = bigOPlus(sum, W(route));
		}
		return sum;
	}

	public T bigOPlus(Route f1, Route f2) {
		return bigOPlus(W(f1), W(f2));
	}

	public int order(List<Route> p1, List<Route> p2) {
		return order(W(p1), W(p2));
	}
}
