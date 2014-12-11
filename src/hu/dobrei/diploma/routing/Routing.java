package hu.dobrei.diploma.routing;

import hu.dobrei.diploma.algebra.AbstractAlgebra;
import hu.dobrei.diploma.network.Airport;
import hu.dobrei.diploma.network.OpenFlightsNetwork;

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
	private OpenFlightsNetwork graph;
	private AbstractAlgebra<T> algebra;
	
	private Set<List<Airport>> allPreferredPath;

	public Routing(OpenFlightsNetwork graph, AbstractAlgebra<T> algebra) {
		this.graph = graph;
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

	public List<Airport> getFirstPreferredPathsTo(Airport destinationAirport) {
		return this.getAllPreferredPathsTo(destinationAirport).iterator().next();
	}

	public Set<List<Airport>> getAllPreferredPathsTo(Airport destinationAirport) {
		allPreferredPath = new HashSet<List<Airport>>();

		getPreferredPath(new ArrayList<Airport>(), destinationAirport);

		return allPreferredPath;
	}

	private List<Airport> getPreferredPath(List<Airport> preferredPath, Airport destinationAirport) {
		List<Airport> prev = destinationAirport.getPrev();
		if (prev == null) {
			preferredPath.add(destinationAirport);
			Collections.reverse(preferredPath);
			allPreferredPath.add(preferredPath);
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

	public List<Airport> computeSimplePreferredPathsBetween(Airport s, Airport t) {
		final Map<Airport, Airport> prev = Maps.newHashMap();
		final Map<Airport, T> Distance = Maps.newHashMap();
		PriorityQueue<Airport> queue = new PriorityQueue<Airport>(graph.getAirports().values().size(),
				new Comparator<Airport>() {
					@Override
					public int compare(Airport ap1, Airport ap2) {
						return algebra.order(Distance.get(ap1), Distance.get(ap2));
					}
				});

		for (Airport airport : graph.getAirports().values()) {
			Distance.put(airport, algebra.phi());
			prev.put(airport, null);
		}
		Distance.put(s, algebra.best());
		queue.add(s);

		while (!queue.isEmpty()) {
			Airport u = queue.poll();
			u.scan();
			for (Airport v : graph.neighboursOf(u)) {

				if (!v.isScanned()) {
					T weight = algebra.W(graph.getRoute(u, v));

					T distanceThroughU = algebra.bigOPlus(Distance.get(u), weight);
					if (algebra.order(distanceThroughU, Distance.get(v)) < 0) {
						Distance.put(v, distanceThroughU);
						prev.put(v, u);
						queue.remove(v);
						queue.add(v);
					}
				}
			}
		}

		List<Airport> toReturn = Lists.newLinkedList();
		Airport u = t;
		while (prev.get(u) != null) {
			toReturn.add(u);
			u = prev.get(u);
		}
		toReturn.add(s);

		Collections.reverse(toReturn);
		return toReturn;
	}
	
	public AbstractAlgebra<T> getAlgebra() {
		return algebra;
	}
}
