/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nforce.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import nforce.graphs.Cluster;
import nforce.graphs.Graph;
import nforce.graphs.Vertex;

/**
 *
 * @author penpen926
 */
public class PostProcessor {

    /**
     * This method performs the post-processing step 2 moving vertex.
     * @param graph
     */
    protected static void postProcessMove(Graph graph) {
        boolean next;
        ArrayList<Cluster> clusters = graph.getClusters();
        // Step 2. Moving vertex.
        do {
            next = false;
            Collections.sort(clusters);
            for (int i = 0; i < clusters.size(); i++) {
                ArrayList<Vertex> VerticesClusteri = clusters.get(i).getVertices();
                Iterator<Vertex> VtxIter = VerticesClusteri.iterator();
                while (VtxIter.hasNext()) {
                    Vertex vtx = VtxIter.next();
                    for (int j = i + 1; j < clusters.size(); j++) {
                        //if this merge can really reduce the cost
                        if (CostComputer.computeMoveCost(graph, vtx, clusters.get(i), clusters.get(j)) < 0) {
                            //then we assign new cluster number to the current point
                            Cluster.movePoint(vtx, clusters.get(i), clusters.get(j));
                            next = true;
                            break;
                        }
                    }
                    if (next == true) {
                        break;
                    }
                }
            }
        } while (next);
    }

    /**
     * This method performs the post-processing step 1 moving vertex.
     * @param graph
     */
    protected static void postProcessMerge(Graph graph) {
        boolean next;
        ArrayList<Cluster> clusters = graph.getClusters();
        do {
            next = false;
            Collections.sort(clusters);
            for (int i = 0; i < clusters.size(); i++) {
                for (int j = i + 1; j < clusters.size(); j++) {
                    //if this merge can really reduce the cost
                    if (CostComputer.computeMergeCost(graph, clusters.get(i), clusters.get(j)) < 0) {
                        //then we merge the two clusters, assigning the cluster number to be the smaller one
                        clusters.get(i).addCluster(clusters.get(j));
                        //remove cluster j
                        clusters.remove(j);
                        j--; //since the index j object is removed, all subsequent objects are shifted left, thus we have to
                        // minus 1 from j.
                        //proceed to another round
                        next = true;
                    }
                }
            }
            graph.setClusters(clusters);
        } while (next);
    }
    
}
