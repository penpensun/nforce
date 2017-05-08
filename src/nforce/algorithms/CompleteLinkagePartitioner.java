/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nforce.algorithms;

import java.util.ArrayList;
import nforce.graphs.Cluster;
import nforce.graphs.Graph;
import nforce.graphs.Vertex;

/**
 *
 * @author GGTTF
 */
public class CompleteLinkagePartitioner {

    /**
     * This method performs the complete linkage chain clustering.
     *
     * cluster.dist = max(c1,c2);
     * First assign single-node clusters. then check all pairs.
     * If clusters.dist < thresh. Then merge.
     * @param graph
     * @param distThresh
     */
    protected static void completeLinkageClust(Graph graph, float distThresh) {
        ArrayList<Vertex> vertices = graph.getVertices();
        ArrayList<Cluster> clusters = new ArrayList<>();
        for (int i = 0; i < vertices.size(); i++) {
            vertices.get(i).setClustNum(i);
            Cluster clust = new Cluster();
            clust.addVertex(vertices.get(i));
            clusters.add(clust);
        }
        boolean merged = true;
        while (merged) {
            merged = false;
            // Check all pairs and merge possible pairs.
            for (int i = 0; i < clusters.size(); i++) {
                for (int j = i + 1; j < clusters.size(); j++) {
                    if (Cluster.dist(clusters.get(i), clusters.get(j), graph, 2) < distThresh) {
                        clusters.get(i).addCluster(clusters.get(j));
                        clusters.remove(j);
                        j--;
                        merged = true;
                    }
                }
            }
        }
    }
    
}
