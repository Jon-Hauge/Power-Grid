package student;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.TreeSet;

import graph.*;

public class ProblemSolver implements IProblem {

	// O(m log m) for entire method
	@Override
	public <V, E extends Comparable<E>> ArrayList<Edge<V>> mst(WeightedGraph<V, E> g) {
		// O(1)
		ArrayList<Edge<V>> mst = new ArrayList<>();
		HashSet<V> visited = new HashSet<>();
		PriorityQueue<Edge<V>> toSearch = new PriorityQueue<>(
			(e1, e2) -> g.getWeight(e1).compareTo(g.getWeight(e2)));

		// O(1)
		Iterator<V> iter = g.vertices().iterator();
		if (!iter.hasNext()) {
			return mst; 
		}

		// O(1)
		V startVertice = iter.next();
		visited.add(startVertice);

		// O(degree(startVertice) * log m) for entire loop
		for (Edge<V> e : g.adjacentEdges(startVertice)) { // O(degree(startVertice))
			toSearch.add(e); // O(log m)
		}

		// O(m log m) for entire loop
		while (!toSearch.isEmpty()) { // O(m)
			Edge<V> edge = toSearch.poll(); // O(log m)
			// O(1)
			V nextVertice = null;
			V a = edge.a;
			V b = edge.b;

			// O(1)
			if (visited.contains(a) && !visited.contains(b)) {
				nextVertice = b;
			} else if (visited.contains(b) && !visited.contains(a)) {
				nextVertice = a;
			} else {
				continue;
			}

			// O(1)
			mst.add(edge);
			visited.add(nextVertice);

			// O(degree(nextVertice) * log m) for entire loop
			for (Edge<V> e : g.adjacentEdges(nextVertice)) { // O(degree(nextVertice))
				V otherVertice = null; // O(1)

				// O(1)
				if (e.a.equals(nextVertice)) {
					otherVertice = e.b; 
				} else {
					otherVertice = e.a;
				}
				if (!visited.contains(otherVertice)) {
					toSearch.add(e); // O(log m)
				}
			}
		}

		return mst;
	}

	// O(n + m) for entire method
	@Override
	public <V> V lca(Graph<V> g, V root, V u, V v) {
		HashMap<V, V> parents = bfs(g, root).getKey(); // O(n + m)

		// O(1)
		HashSet<V> ancestors = new HashSet<>();
		V current = u;
		
		// O(n) for entire loop
		while (current != null) { // O(n)
			// O(1)
			ancestors.add(current);
			current = parents.get(current);
		}

		current = v; // O(1)
		// O(n) for entire loop
		while (current != null) { // O(n)
			// O(1)
			if (ancestors.contains(current)) {
				return current;
			}
			current = parents.get(current);
		}
		
		return null;
	}

	// O((n + m) log m) for entire method
	@Override
	public <V> Edge<V> addRedundant(Graph<V> g, V root) {
		// O(1)
		HashMap<V, Integer> size = new HashMap<>();
		HashSet<V> visited = new HashSet<>();

		computeSubtreeSizes(g, root, visited, size); // O(n + m)

		HashMap<Edge<V>, Integer> vulnerability = new HashMap<>(); // O(1)
		// O(m) for entire loop
		for (Edge<V> edge : g.edges()) { // O(m)
			vulnerability.put(edge, Math.min(size.get(edge.a), size.get(edge.b))); // O(1)
		}
		
		TreeSet<Integer> unique = new TreeSet<>(vulnerability.values()); // O(m log m)
		ArrayList<Integer> values = new ArrayList<>(unique); // O(m)

		values.add(0, -1); // O(m)
		
		// O(1)
		V bestA = null;
		V bestB = null;
		int low = 0;
		int high = values.size() - 1;

		// O((n + m) log m) for entire loop
		while (low <= high) { // O(m)
			int middle = (low + high) / 2; // O(1)
			Object[] path = heavyPathEndpoints(g, vulnerability, values.get(middle)); // O(n + m)
			
			// O(1)
			if (path != null) {
				bestA = (V) path[0]; 
				bestB = (V) path[1];
				high = middle - 1;
			} else {
				low = middle + 1;
			}
		}
		if (bestA == null || bestB == null) { // O(1)
			bestA = bfs(g, root).getValue(); // O(n + m)
			bestB = bfs(g, bestA).getValue(); // O(n + m)
		}

		return new Edge<V>(bestA, bestB); // O(1)
	}

	// O(n + m) for entire method
	private <V> Map.Entry<HashMap<V, V>, V> bfs(Graph<V> g, V root) {
		// O(1)
		HashMap<V, V> parents = new HashMap<>();
		Queue<V> queue = new LinkedList<>();
		parents.put(root, null);
		queue.add(root);
		V farthest = root;

		// O(n + m) for entire loop
		while (!queue.isEmpty()) { // O(n)
			// O(1)
			V nextVertice = queue.poll();
			farthest = nextVertice;

			// O(degree(nextVertice)) for entire loop, sums up to O(m) after entire traversal
			for (V neighbor : g.neighbours(nextVertice)) { // O(degree(nextVertice))
				// O(1)
				if (!parents.containsKey(neighbor)) {
					parents.put(neighbor, nextVertice);
					queue.add(neighbor);
				}
			}
		}
		return new AbstractMap.SimpleEntry<>(parents, farthest);	
	}

	// O(n + m) for entire method
	private <V> int computeSubtreeSizes(Graph<V> g, V vertice, HashSet<V> visited, HashMap<V, Integer> size) {
		// O(1)
		if (!visited.add(vertice)) {
			return 0;
		}
		int sz = 1;

		// O(n + m) for entire loop
		for (V neighbor : g.neighbours(vertice)) { // O(degree(vertice)), sums up to m for entire traversal
			sz += computeSubtreeSizes(g, neighbor, visited, size); // O(n + m) total for all calls
		}
		size.put(vertice, sz); // O(1)
		
		return sz;
	}

	// O(n + m) for entire method
	private <V> Object[] heavyPathEndpoints(Graph<V> g, HashMap<Edge<V>, Integer> vulnerability, Integer k) {
		HashMap<V, ArrayList<V>> heavy = new HashMap<>(); // O(1)
		
		// O(m) for entire loop
		for (Edge<V> edge : g.edges()) { // O(m)
			// O(1)
			if (vulnerability.get(edge) > k) {
				heavy.computeIfAbsent(edge.a, x -> new ArrayList<>()).add(edge.b);
				heavy.computeIfAbsent(edge.b, x -> new ArrayList<>()).add(edge.a);
			}
		}
		// O(1)
		if (heavy.isEmpty()) {
			return new Object[]{null, null};
		}

		// O(1)
		int degree1 = 0;
		V a = null;
		V b = null;
		V start = null;
		
		// O(n) for entire loop
		for (var entry : heavy.entrySet()) { // O(n)
			// O(1)
			int degree = entry.getValue().size();
			if (degree > 2) {
				return null;
			}
			if (degree == 1) {
				if (a == null) {
					a = entry.getKey();
				} else {
					b = entry.getKey();
				}
				degree1++;
			}
			start = entry.getKey();
		}
		if (degree1 < 2 && heavy.size() > 1) {
			return null;
		}

		// O(1)
		HashSet<V> seen = new HashSet<>();
		Queue<V> toSearch = new LinkedList<>();
		seen.add(start);
		toSearch.add(start);

		// O(n + m) for entire loop
		while (!toSearch.isEmpty()) { // O(m)
			V currentVertice = toSearch.poll(); // O(1)

			// O(m) for entire traversal
			for (V neighbor : heavy.getOrDefault(currentVertice, new ArrayList<>())) { // O(degree(currentVertice)), sums up to m for entire traversal
				// O(1)
				if (seen.add(neighbor)) {
					toSearch.add(neighbor);
				}
			}
		}
		// O(1)
		if (seen.size() != heavy.size()) {
			return null;
		}
		if (a == null) {
			a = start;
		}
		if (b == null) {
			b = heavy.getOrDefault(a, new ArrayList<>()).stream().findFirst().orElse(a);
		}
		return new Object[]{a, b};
	}
}
