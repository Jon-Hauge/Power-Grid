package student;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.TreeSet;

import graph.*;

public class ProblemSolver implements IProblem {

	// O(E log E) for entire method
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

		// O(degree(startVertice) * log E) for entire loop
		for (Edge<V> e : g.adjacentEdges(startVertice)) { // O(degree(startVertice))
			toSearch.add(e); // O(log E)
		}

		// O(E log E) for entire loop
		while (!toSearch.isEmpty()) { // O(E)
			Edge<V> edge = toSearch.poll(); // O(log E)
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

			// O(degree(nextVertice) * log E) for entire loop
			for (Edge<V> e : g.adjacentEdges(nextVertice)) { // O(degree(nextVertice))
				V otherVertice = null; // O(1)

				// O(1)
				if (e.a.equals(nextVertice)) {
					otherVertice = e.b; 
				} else {
					otherVertice = e.a;
				}
				if (!visited.contains(otherVertice)) {
					toSearch.add(e); // O(log E)
				}
			}
		}

		return mst;
	}

	// O(V + E) for entire method
	@Override
	public <V> V lca(Graph<V> g, V root, V u, V v) {
		// O(1)
		HashMap<V, V> parents = new HashMap<>();
		Queue<V> queue = new LinkedList<>();
		parents.put(root, null);
		queue.add(root);
		V farthest = root;

		// O(V + E) for entire loop
		while (!queue.isEmpty()) { // O(V)
			// O(1)
			V nextVertice = queue.poll();
			farthest = nextVertice;

			// O(degree(nextVertice)) for entire loop, sums up to O(E) after entire traversal
			for (V neighbor : g.neighbours(nextVertice)) { // O(degree(nextVertice))
				// O(1)
				if (!parents.containsKey(neighbor)) {
					parents.put(neighbor, nextVertice);
					queue.add(neighbor);
				}
			}
		}

		// O(1)
		HashSet<V> ancestors = new HashSet<>();
		V current = u;
		
		// O(V) for entire loop
		while (current != null) { // O(V)
			// O(1)
			ancestors.add(current);
			current = parents.get(current);
		}

		current = v; // O(1)
		// O(V) for entire loop
		while (current != null) { // O(V)
			// O(1)
			if (ancestors.contains(current)) {
				return current;
			}
			current = parents.get(current);
		}
		
		return null;
	}

	// O((V + E) log E) for entire method
	@Override
	public <V> Edge<V> addRedundant(Graph<V> g, V root) {
		// O(1)
		HashMap<V, Integer> size = new HashMap<>();
		HashSet<V> visited = new HashSet<>();

		// The following function and computing the vulnerability-map
		// was inspired by the implementation in the PowerOutage class.
		computeSubtreeSizes(g, root, visited, size); // O(V + E)

		HashMap<Edge<V>, Integer> vulnerability = new HashMap<>(); // O(1)
		// O(E) for entire loop
		for (Edge<V> edge : g.edges()) { // O(E)
			vulnerability.put(edge, Math.min(size.get(edge.a), size.get(edge.b))); // O(1)
		}
		
		TreeSet<Integer> unique = new TreeSet<>(vulnerability.values()); // O(E log E)
		ArrayList<Integer> values = new ArrayList<>(unique); // O(E)

		values.add(0, -1); // O(E)
		
		// O(1)
		V bestA = null;
		V bestB = null;
		int low = 0;
		int high = values.size() - 1;

		// O((V + E) log E) for entire loop
		while (low <= high) { // O(E)
			int middle = (low + high) / 2; // O(1)
			Object[] path = heavyPathEndpoints(g, vulnerability, values.get(middle)); // O(V + E)
			
			// O(1)
			if (path != null) {
				bestA = (V) path[0]; 
				bestB = (V) path[1];
				high = middle - 1;
			} else {
				low = middle + 1;
			}
		}
		return new Edge<V>(bestA, bestB); // O(1)
	}

	// O(V + E) for entire method
	private <V> int computeSubtreeSizes(Graph<V> g, V vertice, HashSet<V> visited, HashMap<V, Integer> size) {
		// O(1)
		if (!visited.add(vertice)) {
			return 0;
		}
		int sz = 1;

		// O(V + E) for entire loop
		for (V neighbor : g.neighbours(vertice)) { // O(degree(vertice)), sums up to E for entire traversal
			sz += computeSubtreeSizes(g, neighbor, visited, size); // O(V + E) total for all calls
		}
		size.put(vertice, sz); // O(1)
		
		return sz;
	}

	/* OpenAI's ChatGPT was used to formulate and/or coming up with ideas for parts of the code*
    * This applies to the idea of constructing subgraphs above the vulnerable candidates, and
	* the implementation of this using a hashmap and going through each entry.
	* All code that was helped by AI is understood in and of itself and in regard to the rest of the program.
    * OpenAI - https://chatgpt.com - retrieved 16.10.25
    */ 
	// O(n + m) for entire method
	private <V> Object[] heavyPathEndpoints(Graph<V> g, HashMap<Edge<V>, Integer> vulnerability, Integer k) {
		HashMap<V, ArrayList<V>> heavy = new HashMap<>(); // O(1)
		
		// O(E) for entire loop
		for (Edge<V> edge : g.edges()) { // O(E)
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
		
		// O(V) for entire loop
		for (var entry : heavy.entrySet()) { // O(V)
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

		// O(V + E) for entire loop
		while (!toSearch.isEmpty()) { // O(E)
			V currentVertice = toSearch.poll(); // O(1)

			// O(E) for entire traversal
			for (V neighbor : heavy.getOrDefault(currentVertice, new ArrayList<>())) { // O(degree(currentVertice)), sums up to E for entire traversal
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