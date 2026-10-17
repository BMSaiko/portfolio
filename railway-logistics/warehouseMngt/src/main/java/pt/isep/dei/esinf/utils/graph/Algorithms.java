package pt.isep.dei.esinf.utils.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.function.BinaryOperator;

import pt.isep.dei.esinf.utils.graph.matrix.MatrixGraph;
import pt.isep.dei.esinf.utils.interfaces.Graph;

/**
 *
 * @author DEI-ISEP
 *
 */
public class Algorithms {

    /**
     * Performs breadth-first search of a Graph starting in a vertex
     *
     * @param g    Graph instance
     * @param vert vertex that will be the source of the search
     * @return a LinkedList with the vertices of breadth-first search
     */
    public static <V, E> LinkedList<V> BreadthFirstSearch(Graph<V, E> g, V vert) {
        if (!g.validVertex(vert)) return null;

        LinkedList<V> qbfs = new LinkedList<>();
        ArrayList<V> qaux = new ArrayList<>();
        boolean[] visited = new boolean[g.numVertices()];
        qbfs.add(vert);
        qaux.add(vert);
        visited[g.key(vert)] = true;

        while (!qaux.isEmpty()) {
            V vOrig = qaux.remove(0);
            for (V vAdj : g.adjVertices(vOrig)) {
                if (!visited[g.key(vAdj)]) {
                    qbfs.add(vAdj);
                    qaux.add(vAdj);
                    visited[g.key(vAdj)] = true;
                }
            }
        }
        return qbfs;
    }

    /**
     * Performs depth-first search starting in a vertex
     *
     * @param g       Graph instance
     * @param vOrig   vertex of graph g that will be the source of the search
     * @param visited set of previously visited vertices
     * @param qdfs    return LinkedList with vertices of depth-first search
     */
    private static <V, E> void DepthFirstSearch(Graph<V, E> g, V vOrig, boolean[] visited, LinkedList<V> qdfs) {
        qdfs.add(vOrig);
        visited[g.key(vOrig)] = true;
        for (V vAdj : g.adjVertices(vOrig)) {
            if (!visited[g.key(vAdj)]) {
                DepthFirstSearch(g, vAdj, visited, qdfs);
            }
        }
    }

    /**
     * Performs depth-first search starting in a vertex
     *
     * @param g    Graph instance
     * @param vert vertex of graph g that will be the source of the search
     * @return a LinkedList with the vertices of depth-first search
     */
    public static <V, E> LinkedList<V> DepthFirstSearch(Graph<V, E> g, V vert) {
        if (!g.validVertex(vert)) return null;

        LinkedList<V> qdfs = new LinkedList<>();
        boolean[] visited = new boolean[g.numVertices()];
        DepthFirstSearch(g, vert, visited, qdfs);
        return qdfs;
    }

    /**
     * Returns all paths from vOrig to vDest
     *
     * @param g       Graph instance
     * @param vOrig   Vertex that will be the source of the path
     * @param vDest   Vertex that will be the end of the path
     * @param visited set of discovered vertices
     * @param path    stack with vertices of the current path (the path is in reverse order)
     * @param paths   ArrayList with all the paths (in correct order)
     */
    private static <V, E> void allPaths(Graph<V, E> g, V vOrig, V vDest, boolean[] visited, LinkedList<V> path, ArrayList<LinkedList<V>> paths) {
        visited[g.key(vOrig)] = true;
        path.push(vOrig);

        if (vOrig.equals(vDest)) {
            LinkedList<V> pathcopy = new LinkedList<>(path);
            Collections.reverse(pathcopy);
            paths.add(pathcopy);
        } else {
            for (V vAdj : g.adjVertices(vOrig)) {
                if (!visited[g.key(vAdj)]) {
                    allPaths(g, vAdj, vDest, visited, path, paths);
                }
            }
        }

        path.pop();
        visited[g.key(vOrig)] = false;
    }

    /**
     * Returns all paths from vOrig to vDest
     *
     * @param g     Graph instance
     * @param vOrig information of the Vertex origin
     * @param vDest information of the Vertex destination
     * @return paths ArrayList with all paths from vOrig to vDest
     */
    public static <V, E> ArrayList<LinkedList<V>> allPaths(Graph<V, E> g, V vOrig, V vDest) {
        if (!g.validVertex(vOrig) || !g.validVertex(vDest)) return null;

        ArrayList<LinkedList<V>> paths = new ArrayList<>();
        boolean[] visited = new boolean[g.numVertices()];
        LinkedList<V> path = new LinkedList<>();
        allPaths(g, vOrig, vDest, visited, path, paths);
        return paths;
    }

    /**
     * Computes shortest-path distance from a source vertex to all reachable
     * vertices of a graph g with non-negative edge weights
     * This implementation uses Dijkstra's algorithm with a priority queue
     * for O((V+E)logV) complexity instead of O(V²)
     *
     * @param g        Graph instance
     * @param vOrig    Vertex that will be the source of the path
     * @param visited  set of previously visited vertices
     * @param pathKeys minimum path vertices keys
     * @param dist     minimum distances
     */
    private static <V, E> void shortestPathDijkstra(Graph<V, E> g, V vOrig, Comparator<E> ce, BinaryOperator<E> sum, E zero, boolean[] visited, V[] pathKeys, E[] dist) {
        int vkey = g.key(vOrig);
        dist[vkey] = zero;
        pathKeys[vkey] = vOrig;

        // Priority queue to efficiently get the vertex with minimum distance
        // Stores pairs of (vertex, distance)
        PriorityQueue<VertexDistance<V, E>> pq = new PriorityQueue<>(
            (a, b) -> ce.compare(a.distance, b.distance)
        );
        
        pq.offer(new VertexDistance<>(vOrig, zero));

        while (!pq.isEmpty()) {
            VertexDistance<V, E> current = pq.poll();
            V vertex = current.vertex;
            vkey = g.key(vertex);
            
            // Skip if already visited
            if (visited[vkey]) continue;
            
            visited[vkey] = true;
            
            // Relax edges from current vertex
            for (Edge<V, E> edge : g.outgoingEdges(vertex)) {
                V vAdj = edge.getVDest();
                int vkeyAdj = g.key(vAdj);
                
                if (!visited[vkeyAdj]) {
                    E newDist = sum.apply(dist[vkey], edge.getWeight());
                    
                    // Update if we found a shorter path
                    if (dist[vkeyAdj] == null || ce.compare(newDist, dist[vkeyAdj]) < 0) {
                        dist[vkeyAdj] = newDist;
                        pathKeys[vkeyAdj] = vertex;
                        pq.offer(new VertexDistance<>(vAdj, newDist));
                    }
                }
            }
        }
    }

    /**
     * Helper class to store vertex-distance pairs for the priority queue
     */
    private record VertexDistance<V, E>(V vertex, E distance) { }

    /**
     * Shortest-path between two vertices
     *
     * @param g         graph
     * @param vOrig     origin vertex
     * @param vDest     destination vertex
     * @param ce        comparator between elements of type E
     * @param sum       sum two elements of type E
     * @param zero      neutral element of the sum in elements of type E
     * @param shortPath returns the vertices which make the shortest path
     * @return if vertices exist in the graph and are connected, true, false otherwise
     */
    public static <V, E> E shortestPath(Graph<V, E> g, V vOrig, V vDest, Comparator<E> ce, BinaryOperator<E> sum, E zero, LinkedList<V> shortPath) {
        if (!g.validVertex(vOrig) || !g.validVertex(vDest)) return null;

        shortPath.clear();
        int numVerts = g.numVertices();
        boolean[] visited = new boolean[numVerts];
        @SuppressWarnings("unchecked") V[] pathKeys = (V[]) new Object[numVerts];
        @SuppressWarnings("unchecked") E[] dist = (E[]) new Object[numVerts];
        for (int i = 0; i < numVerts; i++) {
            dist[i] = null;
            pathKeys[i] = null;
        }

        shortestPathDijkstra(g, vOrig, ce, sum, zero, visited, pathKeys, dist);

        E length = dist[g.key(vDest)];
        if (length == null) return null;

        getPath(g, vOrig, vDest, pathKeys, shortPath);
        return length;
    }

    /**
     * Shortest-path between a vertex and all other vertices
     *
     * @param g     graph
     * @param vOrig start vertex
     * @param ce    comparator between elements of type E
     * @param sum   sum two elements of type E
     * @param zero  neutral element of the sum in elements of type E
     * @param paths returns all the minimum paths
     * @param dists returns the corresponding minimum distances
     * @return if vOrig exists in the graph true, false otherwise
     */
    public static <V, E> boolean shortestPaths(Graph<V, E> g, V vOrig, Comparator<E> ce, BinaryOperator<E> sum, E zero, ArrayList<LinkedList<V>> paths, ArrayList<E> dists) {
        if (!g.validVertex(vOrig)) return false;

        paths.clear();
        dists.clear();
        int numVerts = g.numVertices();
        boolean[] visited = new boolean[numVerts];
        @SuppressWarnings("unchecked") V[] pathKeys = (V[]) new Object[numVerts];
        @SuppressWarnings("unchecked") E[] dist = (E[]) new Object[numVerts];
        for (int i = 0; i < numVerts; i++) {
            dist[i] = null;
            pathKeys[i] = null;
        }

        shortestPathDijkstra(g, vOrig, ce, sum, zero, visited, pathKeys, dist);

        for (int i = 0; i < numVerts; i++) {
            paths.add(new LinkedList<>());
            dists.add(dist[i]);
            if (dist[i] != null) {
                getPath(g, vOrig, g.vertex(i), pathKeys, paths.get(i));
            }
        }
        return true;
    }

    /**
     * Extracts from pathKeys the minimum path between voInf and vdInf
     * The path is constructed from the end to the beginning
     *
     * @param g        Graph instance
     * @param vOrig    information of the Vertex origin
     * @param vDest    information of the Vertex destination
     * @param pathKeys minimum path vertices keys
     * @param path     stack with the minimum path (correct order)
     */
    private static <V, E> void getPath(Graph<V, E> g, V vOrig, V vDest, V[] pathKeys, LinkedList<V> path) {
        if (vOrig.equals(vDest)) path.addFirst(vOrig);
        else if (pathKeys[g.key(vDest)] != null) {
            path.addFirst(vDest);
            getPath(g, vOrig, pathKeys[g.key(vDest)], pathKeys, path);
        }
    }

    /**
     * Calculates the minimum distance graph using Floyd-Warshall
     *
     * @param g   initial graph
     * @param ce  comparator between elements of type E
     * @param sum sum two elements of type E
     * @return the minimum distance graph
     */
    public static <V, E> MatrixGraph<V, E> minDistGraph(Graph<V, E> g, Comparator<E> ce, BinaryOperator<E> sum) {
        int numVerts = g.numVertices();
        if (numVerts == 0) return null;

        // Create a new MatrixGraph with same vertices
        MatrixGraph<V, E> minDistGraph = new MatrixGraph<>(g.isDirected());

        // Add all vertices
        for (V vert : g.vertices()) {
            minDistGraph.addVertex(vert);
        }

        // If there are no edges, return the graph with just vertices
        if (g.numEdges() == 0) {
            return minDistGraph;
        }

        // We need a way to represent "infinity" and "zero"
        // Since we don't know type E, we'll work directly with the graph

        // First, copy all direct edges
        for (Edge<V, E> edge : g.edges()) {
            minDistGraph.addEdge(edge.getVOrig(), edge.getVDest(), edge.getWeight());
        }

        // Floyd-Warshall algorithm using the graph operations
        ArrayList<V> vertices = g.vertices();

        // We'll use a 2D array to store distances
        @SuppressWarnings("unchecked") E[][] dist = (E[][]) new Object[numVerts][numVerts];

        // Initialize distance matrix
        for (int i = 0; i < numVerts; i++) {
            for (int j = 0; j < numVerts; j++) {
                if (i == j) {
                    dist[i][j] = null; // Distance to self
                } else {
                    Edge<V, E> edge = g.edge(vertices.get(i), vertices.get(j));
                    dist[i][j] = (edge != null) ? edge.getWeight() : null;
                }
            }
        }

        // Floyd-Warshall
        for (int k = 0; k < numVerts; k++) {
            for (int i = 0; i < numVerts; i++) {
                if (dist[i][k] != null) {
                    for (int j = 0; j < numVerts; j++) {
                        if (dist[k][j] != null) {
                            E s = sum.apply(dist[i][k], dist[k][j]);
                            if (dist[i][j] == null || ce.compare(s, dist[i][j]) < 0) {
                                dist[i][j] = s;
                            }
                        }
                    }
                }
            }
        }

        // Update the graph with minimum distances
        // Clear existing edges first
        MatrixGraph<V, E> resultGraph = new MatrixGraph<>(g.isDirected());
        for (V vert : vertices) {
            resultGraph.addVertex(vert);
        }

        for (int i = 0; i < numVerts; i++) {
            for (int j = 0; j < numVerts; j++) {
                if (i != j && dist[i][j] != null) {
                    // Only add edge if there's a path
                    resultGraph.addEdge(vertices.get(i), vertices.get(j), dist[i][j]);
                }
            }
        }

        return resultGraph;
    }

}