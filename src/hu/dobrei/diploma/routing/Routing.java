package hu.dobrei.diploma.routing;

import hu.dobrei.diploma.algebra.AbstractAlgebra;
import hu.dobrei.diploma.network.Airport;
import hu.dobrei.diploma.network.OpenFlightsNetwork;
import hu.dobrei.diploma.network.Route;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class Routing<T> {
	private final OpenFlightsNetwork graph;
	private final AbstractAlgebra<T> algebra;
	private final AbstractAlgebra<T> algebra2;

	private Set<List<Airport>> allPreferredPath;

	public Routing(OpenFlightsNetwork graph, AbstractAlgebra<T> algebra) {
		this.graph = graph;
		this.algebra = algebra;
		this.algebra2 = null;
	}

	public Routing(OpenFlightsNetwork graph, AbstractAlgebra<T> algebra2, AbstractAlgebra<T> algebra) {
		this.graph = graph;
		this.algebra2 = algebra2;
		this.algebra = algebra;
	}

	public void computeAllPreferredPathsFrom(Airport sourceAirport) {
		clearPrevs();
		final Map<Airport, T> sourceDistance = Maps.newHashMap();
		for (Airport airport : graph.getAirports().values()) {
			sourceDistance.put(airport, algebra.phi());
		}
		sourceDistance.put(sourceAirport, algebra.best());

		PriorityQueue<Airport> queue = new PriorityQueue<Airport>(graph.getAirports().values().size(),
				new Comparator<Airport>() {
					@Override
					public int compare(Airport ap1, Airport ap2) {
						return algebra.order(sourceDistance.get(ap1), sourceDistance.get(ap2));
					}
				});
		queue.add(sourceAirport);
		List<Airport> prev = null;

		while (!queue.isEmpty()) {
			Airport u = queue.poll();

			Collection<Airport> neighbours = graph.neighboursOf(u);
			for (Iterator<Airport> iterator = neighbours.iterator(); iterator.hasNext();) {
				Airport nv = iterator.next();
				prev = nv.getPrev();
				T weight = algebra.W(graph.getRoute(u, nv));

				T distanceThroughU = algebra.bigOPlus(sourceDistance.get(u), weight);
				if (algebra.order(distanceThroughU, sourceDistance.get(nv)) < 0) {
					queue.remove(nv);
					sourceDistance.put(nv, distanceThroughU);
					nv.setPrevious(u);
					queue.add(nv);
					prev = Lists.newLinkedList();
					prev.add(u);
					nv.setPrev(prev);
				} else if (algebra.order(distanceThroughU, sourceDistance.get(nv)) == 0) {
					if (prev != null)
						prev.add(u);
					else {
						prev = Lists.newLinkedList();
						prev.add(u);
						nv.setPrev(prev);
					}
				}
			}
		}
	}

	private void clearPrevs() {
		for (Airport a : graph.getAirports().values()) {
			a.setPrevious(null);
			a.setPrev(null);
		}
	}

	public List<Airport> getPreferredPathTo(Airport destinationAirport) {
		List<Airport> airportList = Lists.newLinkedList();
		for (Airport airport = destinationAirport; airport != null; airport.getPrevious()) {
			airportList.add(airport);
		}

		Collections.reverse(airportList);
		return airportList;
	}

	public List<Airport> getFirstPreferredPathTo(Airport destinationAirport) {
		Set<List<Airport>> allPreferredPaths = this.getAllPreferredPathsTo(destinationAirport);
		List<Airport> airportList = null;
		if (algebra2 != null) {
			T min = algebra2.phi();
			for (List<Airport> list : allPreferredPaths) {
				List<Route> routeList;
				if (!list.get(list.size() - 1).equals(destinationAirport)) {
					routeList = getRoutes(list.subList(0, list.size() - 2));
				} else {
					routeList = getRoutes(list);
				}
				T act = algebra2.W(routeList);
				if (algebra2.order(act, min) <= 0) {
					min = act;
					airportList = list;
				}
			}
		} else {
			airportList = allPreferredPaths.iterator().next();
		}
		return airportList;
	}

	private List<Route> getRoutes(List<Airport> airports) {
		List<Route> routes = Lists.newLinkedList();
		for (int i = 1; i < airports.size(); i++) {
			Route r = graph.getRoute(airports.get(i - 1), airports.get(i));
			routes.add(r);
		}
		return routes;
	}

	public Set<List<Airport>> getAllPreferredPathsTo(Airport destinationAirport) {
		allPreferredPath = new HashSet<List<Airport>>();

		getPreferredPath(new ArrayList<Airport>(), destinationAirport);

		return allPreferredPath;
	}

	private List<Airport> getPreferredPath(List<Airport> preferredPath, Airport destinationAirport) {
		List<Airport> prev = destinationAirport.getPrev();
		if (prev == null) {
			if (preferredPath.get(0).getId() != destinationAirport.getId()) {
				preferredPath.add(destinationAirport);
				Collections.reverse(preferredPath);
				allPreferredPath.add(preferredPath);
			}
		} else {
			List<Airport> updatedPath = Lists.newArrayList(preferredPath);
			updatedPath.add(destinationAirport);

			for (Iterator<Airport> iterator = prev.iterator(); iterator.hasNext();) {
				Airport vertex = (Airport) iterator.next();
				getPreferredPath(updatedPath, vertex);
			}
		}
		return preferredPath;
	}

	public String getAlgebraName() {
		if (algebra2 == null) {
			return algebra.getClass().getSimpleName();
		}
		return algebra2.getClass().getSimpleName() + algebra.getClass().getSimpleName();
	}
}
