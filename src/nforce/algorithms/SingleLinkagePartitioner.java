/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nforce.algorithms;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;
import nforce.graphs.Graph;
import nforce.graphs.Vertex;

/**
 *
 * @author GGTTF
 */
public class SingleLinkagePartitioner {

    /**
     * This method implements normal partition after displacement.
     * @param graph
     * @param p
     */
    protected static void singleLinkagePartition(Graph graph, Param p) {
        System.out.println("Partition the nodes. ");
        float minCostSL = Float.MAX_VALUE;
        float costAti;
        float minCostThresh = 0;
        System.out.println("Start single-linkage partitioning.");
        int[] slClusters = new int[graph.getVertices().size()];
        // Normal sl clustering
        for (float i = p.getLowerth(); i <= p.getUpperth(); i += p.getStep()) {
            singleLinkageClust(graph, i);
            costAti = CostComputer.computeCost(graph);
            if (costAti < minCostSL) {
                minCostSL = costAti;
                minCostThresh = i;
                //record the cluster assignment
                for (int j = 0; j < graph.getVertices().size(); j++) {
                    slClusters[j] = graph.getVertices().get(j).getClustNum();
                }
            }
        }
        float minCostKmeans = Float.MAX_VALUE;
        int minK = 0;
        float costAtk;
        int[] kmeansClusters = new int[graph.getVertices().size()];
        System.out.println("Start k-mean partitioning.");
        //in case the initial k=2 is larger than or equal to VertexList.size()/3
        if (graph.getVertices().size() < 9) {
            for (int k = 2; k <= 4; k++) {
                KmeansPartitioner.kmeansClust(graph, k, p);
                costAtk = CostComputer.computeCost(graph);
                if (minCostKmeans == -1 || costAtk < minCostKmeans) {
                    minCostKmeans = costAtk;
                    minK = k;
                    //record the cluster assignment
                    for (int i = 0; i < graph.getVertices().size(); i++) {
                        kmeansClusters[i] = graph.getVertices().get(i).getClustNum();
                    }
                }
            }
        } else if (graph.getVertices().size() < 200) {
            for (int k = 2; k < graph.getVertices().size() / 4; k++) {
                KmeansPartitioner.kmeansClust(graph, k, p);
                costAtk = CostComputer.computeCost(graph);
                if (minCostKmeans == -1 || costAtk < minCostKmeans) {
                    minCostKmeans = costAtk;
                    minK = k;
                    //record the cluster assignment
                    for (int i = 0; i < graph.getVertices().size(); i++) {
                        kmeansClusters[i] = graph.getVertices().get(i).getClustNum();
                    }
                }
            }
        } else {
            for (int k = 2; k < 20; k++) {
                KmeansPartitioner.kmeansClust(graph, k, p);
                costAtk = CostComputer.computeCost(graph);
                if (minCostKmeans == -1 || costAtk < minCostKmeans) {
                    minCostKmeans = costAtk;
                    minK = k;
                    //record the cluster assignment
                    for (int i = 0; i < graph.getVertices().size(); i++) {
                        kmeansClusters[i] = graph.getVertices().get(i).getClustNum();
                    }
                }
            }
        }
        // Check whether single linkage or kmeans give the better result
        if (minCostKmeans <= minCostSL) {
            System.out.println("K means is better.");
            // System.out.println("Best k is "+minK);
            // Restore the cluster assignment of kmeans
            for (int i = 0; i < graph.getVertices().size(); i++) {
                graph.getVertices().get(i).setClustNum(kmeansClusters[i]);
            }
        } else {
            System.out.println("Sl is better.");
            // Restore the cluster assignments of singlelinkage clustering
            for (int i = 0; i < graph.getVertices().size(); i++) {
                graph.getVertices().get(i).setClustNum(slClusters[i]);
            }
        }
        //Assign the clusters
        ClusterAssigner.assignClusters(graph);
        System.out.println("Partitioning completed.");
        // Post-processing
        // Step 1, merge Clusters
        System.out.println("Start post-processing: 1. Merging Clusters.");
        PostProcessor.postProcessMerge(graph);
        // Step 2. Moving vertex.
        System.out.println("Post-processing: 2. Moving vertex. ");
        PostProcessor.postProcessMove(graph);
        System.out.println("Post-processing complete.");
    }

    /**
     * This method performs single-linkage cluster.
     * @param VertexList
     * @param distThresh
     */
    private static void singleLinkageClust(Graph graph, float distThresh) {
        // For test
        try {
            FileWriter fw = new FileWriter("./test_sl_clusters.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("##############\t" + distThresh + "\n");
            bw.flush();
            bw.close();
            fw.close();
        } catch (IOException e) {
            System.err.println("(BiForceOnGraph4.singleLinageClust) File writer init error.");
            return;
        }
        //init all the clust num to be -1
        for (Vertex vtx : graph.getVertices()) {
            vtx.setClustNum(-1);
        }
        int currentClustIdx = 0;
        Vertex noClustVtx;
        while ((noClustVtx = ClusterAssigner.getFirstUnassignedVertex(graph.getVertices())) != null) {
            noClustVtx.setClustNum(currentClustIdx);
            //check if all the vertex are assigned with some cluster number
            ArrayList<Vertex> forTest = new ArrayList<>();
            forTest.add(noClustVtx); // For test.
            Stack<Vertex> verticesToVisit = new Stack<>();
            verticesToVisit.add(noClustVtx);
            while (!verticesToVisit.isEmpty()) {
                Vertex seed = verticesToVisit.pop();
                /* For all the other unassigned vertices in the VertexList. */
                for (Vertex vtx : graph.getVertices()) {
                    if (vtx.getClustNum() != -1) {
                        continue;
                    }
                    if (graph.dist(vtx, seed) < distThresh) {
                        vtx.setClustNum(currentClustIdx);
                        verticesToVisit.add(vtx);
                        forTest.add(vtx); // For test.
                    }
                }
            }
           
            forTest.clear();
            /* Increase the cluster number by 1. */
            currentClustIdx++;
        }
    }
    
}
