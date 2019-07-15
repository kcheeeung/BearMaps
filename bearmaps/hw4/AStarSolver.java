package bearmaps.hw4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import bearmaps.proj2ab.ArrayHeapMinPQ;
import edu.princeton.cs.algs4.Stopwatch;


public class AStarSolver<Vertex> implements ShortestPathsSolver<Vertex> {
    boolean goalReached = false;
    private ArrayHeapMinPQ<Vertex> pq;
    private HashMap<Vertex, Double> distTo;
    private HashMap<Vertex, Vertex> edgeTo;
    private HashSet<Vertex> visited;
    
    private SolverOutcome outcome;
    private List<Vertex> solution;
    private double solutionWeight;
    private int numStatesExplored;
    private double timeSpent;

    public AStarSolver(AStarGraph<Vertex> input, Vertex start, Vertex goal, double timeout) {
        Stopwatch sw = new Stopwatch();
        pq = new ArrayHeapMinPQ<>();
        distTo = new HashMap<>();
        edgeTo = new HashMap<>();
        visited = new HashSet<>();
        solution = new ArrayList<>();

        List<WeightedEdge<Vertex>> neighbors;
        Vertex current;

        pq.add(start, input.estimatedDistanceToGoal(start, goal));
        distTo.put(start, 0.0);
        numStatesExplored = 0;

        do {
            current = pq.removeSmallest();
            visited.add(current);
            numStatesExplored++;

            neighbors = input.neighbors(current);
            for (WeightedEdge<Vertex> e : neighbors) {
                Vertex from = e.from();
                Vertex tVertex = e.to();
                double totalDist = distTo.get(from) + e.weight();
                double priority = totalDist + input.estimatedDistanceToGoal(tVertex, goal);

                if (!distTo.containsKey(tVertex)) {
                    // If not in distTo(some vertex), then the dist was infinity
                    distTo.put(tVertex, totalDist);
                    edgeTo.put(tVertex, from);
                    pq.add(tVertex, priority);
                }

                if (totalDist < distTo.get(tVertex)) {
                    // if new distance closer, relax
                    distTo.replace(tVertex, totalDist);
                    edgeTo.replace(tVertex, from);
                    
                    if (pq.contains(tVertex)) {
                        pq.changePriority(tVertex, priority);
                    } else {
                        pq.add(tVertex, priority);
                    }
                }
            }

            if (current.equals(goal) && sw.elapsedTime() < timeout) {
                solutionWeight = distTo.get(goal);
                outcome = SolverOutcome.SOLVED;
                pathBuilder(current, start);
                Collections.reverse(solution);
                break;
            } else if (sw.elapsedTime() >= timeout) {
                outcome = SolverOutcome.TIMEOUT;
                break;
            } else if (pq.size() == 0) {
                outcome = SolverOutcome.UNSOLVABLE;
                break;
            }
        } while (pq.size() > 0);
        timeSpent = sw.elapsedTime();
    }
    
    private void pathBuilder(Vertex current, Vertex start) {
        while (current != null) {
            solution.add(current);
            current = edgeTo.get(current);
        }
    }

    @Override
    public SolverOutcome outcome() {
        return outcome;
    }

    @Override
    public List<Vertex> solution() {
        return solution;
    }

    @Override
    public double solutionWeight() {
        if (outcome == SolverOutcome.SOLVED) {
            return solutionWeight;
        } else {
            return 0;
        }
    }

    @Override
    public int numStatesExplored() {
        return numStatesExplored;
    }

    @Override
    public double explorationTime() {
        return timeSpent;
    }
}
