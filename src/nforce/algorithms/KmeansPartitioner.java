/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nforce.algorithms;

import java.util.ArrayList;
import nforce.graphs.Graph;
import nforce.graphs.Vertex;

/**
 *
 * @author GGTTF
 */
public class KmeansPartitioner {

    /**
     * This is the version 2 of k-means clustering, with some bug fixed.
     * @param Points
     * @param k
     */
    protected static void kmeansClust(Graph input, int k, Param p) {
        /* Init the cluster number of the vertices. */
        for (Vertex vtx : input.getVertices()) {
            vtx.setClustNum(-1);
        }
        /* Randomly choose k points as k centroids. */
        ArrayList<Integer> vtxIndexes = new ArrayList<>();
        /* Init the coords array for centroids. */
        float[][] centroids = new float[k][p.getDim()];
        /* Create an array to record the sizes of all clusters. */
        int[] clusterSizes = new int[k];
        for (int i = 0; i < clusterSizes.length; i++) {
            clusterSizes[i] = 0;
        }
        /* Init the coordinates of centroids. */
        for (float[] cen : centroids) {
            for (int j = 0; j < p.getDim(); j++) {
                cen[j] = -1;
            }
        }
        /* Randomly pick up k nodes as the initial k centroids. */
        for (int i = 0; i < input.vertexCount(); i++) {
            vtxIndexes.add(i);
        }
        vtxIndexes.trimToSize();
        for (int i = 0; i < k; i++) {
            int idx = (int) (Math.random() * vtxIndexes.size());
            /* Create the coordinate array, and copy the coordinates*/
            System.arraycopy(input.getVertices().get(i).getCoords(), 0, centroids[i], 0, p.getDim());
            vtxIndexes.remove(idx);
        }
        boolean isConverged = false;
        while (!isConverged) {
            isConverged = true;
            /* Assign the points to closest centroid. */
            for (Vertex vtx : input.getVertices()) {
                float minDist = Float.MAX_VALUE;
                int closestCentroidIdx = -1;
                for (int i = 0; i < centroids.length; i++) {
                    float dist = LayoutRefiner.euclidDist(vtx, centroids[i], p);
                    if (dist < minDist) {
                        minDist = dist;
                        closestCentroidIdx = i;
                    }
                }
                /* Check the validity of closestCentroidIdx, minDist. */
                if (closestCentroidIdx == -1) {
                    throw new IllegalArgumentException("(BiForceOnGraph4 kmeansClust) " + "Nearest centroid idx cannot be -1:  " + vtx.getValue());
                }
                /* Check if any change is made. */
                if (vtx.getClustNum() != closestCentroidIdx) {
                    isConverged = false;
                }
                vtx.setClustNum(closestCentroidIdx);
                clusterSizes[closestCentroidIdx]++;
            }
            /* After assigning clusters,
             * re-compute the coordinates for centroids. */
            for (int i = 0; i < centroids.length; i++) {
                /* Jump over the clusters with no points in it. */
                if (clusterSizes[i] == 0) {
                    continue;
                }
                /* For the cluster with point(s),
                 * re-initialize the coordinates of centroids. */
                for (int j = 0; j < p.getDim(); j++) {
                    centroids[i][j] = 0;
                }
            }
            /* Re-compute the coordinates for centroids. */
            /* First compute addition. */
            for (Vertex vtx : input.getVertices()) {
                for (int i = 0; i < p.getDim(); i++) {
                    centroids[vtx.getClustNum()][i] += vtx.getCoords()[i];
                }
            }
            /* Second compute the average. */
            for (int i = 0; i < centroids.length; i++) {
                /*If no points in this cluster then we do nothing. */
                if (clusterSizes[i] == 0) {
                } else {
                    for (int j = 0; j < p.getDim(); j++) {
                        centroids[i][j] /= clusterSizes[i];
                    }
                }
            }
        }
        /* Finally, check if there's any point unassigned. */
        for (Vertex vtx : input.getVertices()) {
            if (vtx.getClustNum() == -1) {
                throw new IllegalArgumentException("Not all points are assigned with clusters");
            }
        }
    }
    
}
