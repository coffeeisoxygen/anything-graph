// package com.coffeecode.algorithm.pathfinding;

// import java.util.ArrayList;
// import java.util.Collections;
// import java.util.HashSet;
// import java.util.LinkedList;
// import java.util.List;
// import java.util.Queue;
// import java.util.Set;
// import java.util.Map;
// import java.util.HashMap;
// import java.util.Objects;

// import com.coffeecode.algorithm.core.AbstractGraphAlgorithm;
// import com.coffeecode.event.core.EventManager;
// import com.coffeecode.event.state.GraphState;
// import com.coffeecode.event.core.AlgorithmEvent;

// import com.coffeecode.model.LocationGraph;
// import com.coffeecode.model.LocationNode;

// import lombok.extern.slf4j.Slf4j;


// @Slf4j
// public class BFSAlgorithm extends AbstractGraphAlgorithm {
//     private final Queue<LocationNode> queue;
//     private final Set<LocationNode> visited;
//     private GraphState graphState;
//     private final Map<LocationNode, Double> distances;

//     public BFSAlgorithm(LocationGraph graph, EventManager eventManager) {
//             super(graph, eventManager);
//             this.queue = new LinkedList<>();
//             this.visited = new HashSet<>();
//             this.distances = new HashMap<>();
//         }

//     @Override
//     public String getName() {
//         return "Breadth First Search";
//     }

//     @Override
//     public List<LocationNode> execute(LocationGraph graph, LocationNode start, 
//             LocationNode end, GraphState graphState) {
//         this.start = start;
//         this.end = end;
//         this.graphState = graphState;
//         this.end = end;
//         this.graphState = graphState;
        
//         reset();
//         queue.offer(start);
        
//         // Prepare for step execution
//         while (!queue.isEmpty() && !isComplete) {
//             LocationNode current = queue.peek(); // Don't remove yet
//             steps.offer(new Step(current, calculateProgress()));
//         }
        
//         return getCurrentPath();
//     }

//     @Override
//     public void executeStep() {
//         if (isPaused || isComplete || queue.isEmpty()) {
//             return;
//         }

//         // Get current node
//         LocationNode current = queue.poll();
//         visited.add(current);
//         graphState.markNodeProcessing(current);
        
//         double progress = calculateProgress();
        
//         // Check if target found
//         if (current.equals(end)) {
//             isComplete = true;
//             graphState.markNodeVisited(current);
//             eventManager.publish(new AlgorithmEvent.Completed(getMetrics()));
//             return;
//         }

//         // Process neighbors
//         for (LocationNode neighbor : graph.getDestinations(current)) {
//             if (!visited.contains(neighbor) && !queue.contains(neighbor)) {
//                 queue.offer(neighbor);
//                 previous.put(neighbor, current);
                
//                 // Update distances
//                 double currentDist = distances.getOrDefault(current, 0.0);
//                 graph.getEdge(current, neighbor).ifPresent(edge -> {
//                     double newDist = currentDist + edge.getWeight();
//                     distances.put(neighbor, newDist);
//                     graphState.updateDistance(neighbor, newDist);
//                 });
                
//                 // Update visualization
//                 graphState.markEdgeProcessing(current, neighbor);
//             }
//         }

//         // Mark current node as visited
//         graphState.markNodeVisited(current);
        
//         // Publish step completion
//         eventManager.publish(new AlgorithmEvent.StepCompleted(current, progress));
//     }

//     @Override
//     public boolean hasNextStep() {
//         return !queue.isEmpty() && !isComplete;
//     }

//     private double calculateProgress() {
//         return visited.size() / (double) graph.getNodeCount();
//     }

//     private Map<String, Double> getMetrics() {
//         Map<String, Double> metrics = new HashMap<>();
//         metrics.put("visitedNodes", (double) visited.size());
//         metrics.put("totalNodes", (double) graph.getNodeCount());
//         metrics.put("pathLength", (double) getCurrentPath().size());
//         metrics.put("pathDistance", getCurrentDistance());
//         return metrics;
//     }

//     @Override
//     protected List<LocationNode> reconstructPath() {
//         if (end == null || !visited.contains(end)) {
//             return Collections.emptyList();
//         }

//         List<LocationNode> path = new ArrayList<>();
//         LocationNode current = end;

//         while (current != null) {
//             path.add(0, current);
//             current = previous.get(current);
//         }

//         return path;
//     }

//     @Override
//     public double getCurrentDistance() {
//         return end != null ? distances.getOrDefault(end, Double.POSITIVE_INFINITY) : 0.0;
//     }

//     @Override
//     public void reset() {
//         visited.clear();
//         queue.clear();
//         previous.clear();
//         distances.clear();
//         steps.clear();
//         isComplete = false;
//         isPaused = false;
//     }

//     // Helper method to validate input
//     private void validateInput(LocationGraph graph, LocationNode start, LocationNode end) {
//         Objects.requireNonNull(graph, "Graph cannot be null");
//         Objects.requireNonNull(start, "Start node cannot be null");
//         Objects.requireNonNull(end, "End node cannot be null");

//         if (!graph.getNodes().contains(start)) {
//             throw new IllegalArgumentException("Start node not in graph");
//         }
//         if (!graph.getNodes().contains(end)) {
//             throw new IllegalArgumentException("End node not in graph");
//         }
//     }
// }
