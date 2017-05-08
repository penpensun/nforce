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
public class ClusterAssigner {

    /**
     * This method returns the first unassigned vertex in the vertex list.
     * If there is no unassigned vertex, then null is returned.
     * @param vertexList
     * @return
     */
    protected static Vertex getFirstUnassignedVertex(ArrayList<Vertex> VertexList) {
        for (Vertex vtx : VertexList) {
            if (vtx.getClustNum() == -1) {
                return vtx;
            }
        }
        return null;
    }

    /**
     * This method assigns clusters to each node in the given graph.
     * @param graph
     */
    protected static void assignClusters(Graph graph) {
        int maxClusterIdx = 0;
        for (int i = 0; i < graph.vertexCount(); i++) {
            if (maxClusterIdx < graph.getVertices().get(i).getClustNum()) {
                maxClusterIdx = graph.getVertices().get(i).getClustNum();
            }
        }
        //Assign the vertex
        ArrayList<Vertex>[] clusterArray = new ArrayList[maxClusterIdx + 1];
        for (int i = 0; i <= maxClusterIdx; i++) {
            clusterArray[i] = new ArrayList<>();
        }
        for (Vertex vtx : graph.getVertices()) {
            clusterArray[vtx.getClustNum()].add(vtx);
        }
        ArrayList<Cluster> clusters = new ArrayList();
        try {
            for (int i = 0; i <= maxClusterIdx; i++) {
                //29.03.2015. Add only the non-empty clusters
                if (!clusterArray[i].isEmpty()) {
                    clusters.add(new Cluster(clusterArray[i]));
                }
            }
        } catch (IllegalArgumentException e) {
            System.out.print("catch");
        }
        graph.setClusters(clusters);
    }
    
}
