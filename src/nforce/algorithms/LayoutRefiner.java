/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nforce.algorithms;

import nforce.graphs.Graph;
import nforce.graphs.Vertex;

/**
 *
 * @author penpen926
 */
public class LayoutRefiner {

    /**
     *
     * @param vtx
     * @param coords
     * @param p
     * @return
     */
    protected static float euclidDist(Vertex vtx, float[] coords, Param p) {
        //check if they have the same dimension
        //System.out.println(pt.getCoordinates().length);
        //System.out.println(coords.length);
        if (vtx.getCoords().length != coords.length) {
            System.out.println(vtx.getCoords().length);
            System.out.println(coords.length);
            throw new IllegalArgumentException("Point 1 and the given coordinates have different dimension");
        }
        float Dist = 0;
        for (int i = 0; i < p.getDim(); i++) {
            Dist += (vtx.getCoords()[i] - coords[i]) * (vtx.getCoords()[i] - coords[i]);
        }
        Dist = (float) Math.sqrt(Dist);
        return Dist;
    }

    /**
     * This method calculates and returns the displacement vector.
     * @param graph
     * @param p
     * @return
     */
    protected static float[][] displace(Graph graph, Param p, int iter) {
        float[][] dispVectors = new float[graph.vertexCount()][p.getDim()];
        /* To avoid unnecessary calculation, we calculate attCoeff and repCoeff first. */
        float attCoeff = p.getFatt() / graph.vertexCount();
        float repCoeff = p.getFrep() / graph.vertexCount();
        /* Calculate the displacement vector for each point. */
        for (int i = 1; i < graph.vertexCount(); i++) {
            for (int j = 0; j < i; j++) {
                Vertex vtx1 = graph.getVertices().get(i);
                Vertex vtx2 = graph.getVertices().get(j);
                float ew = graph.edgeWeight(vtx1, vtx2);
                /* If there is no edge between Pt1 and Pt2, continue. */
                if (Double.isNaN(ew)) {
                    continue;
                }
                float dist = graph.dist(vtx1, vtx2);
                if (dist == 0) {
                    continue;
                }
                /* Calculate the force */
                /* If there is an edge between two node, then we calculate
                 * the attration force threshold. */
                float force;
                //if(ew >p.getThresh()){
                if (ew > 0) {
                    //force = (float)((ew- p.getThresh())*Math.log10(dist+1)*attCoeff/dist);
                    force = (float) (ew * Math.log10(dist + 1) * attCoeff / dist);
                } else {
                    /* Else, we calculate the repulsion force. */
                    //force = (float)(repCoeff*(ew - p.getThresh())/Math.log10(dist+1)/dist);
                    force = (float) (repCoeff * ew / Math.log10(dist + 1) / dist);
                }
                //for each dim
                for (int d = 0; d < p.getDim(); d++) {
                    //update the displacement vector for Pt1
                    dispVectors[i][d] += (vtx2.getCoords()[d] - vtx1.getCoords()[d]) * force;
                    //update the displacement vector for Pt2, with same magnitude but opposite direction;
                    dispVectors[j][d] -= (vtx2.getCoords()[d] - vtx1.getCoords()[d]) * force;
                }
            }
        }
        /* Adjust the dispVectors according to the cooling factor. */
        float Mi = CoolingProcessor.cooling(p.getM0(), iter);
        for (int i = 0; i < graph.vertexCount(); i++) {
            /* The magnitude of the original displacement vector. */
            float Mag = getMagnitude(dispVectors[i]);
            //System.out.println("i: "+iter+"\tmagnitude: "+Mag+"\tcooling: "+Mi);
            /* The minor of Mi and Mag. */
            float MinM = Math.min(1, Mi / Mag);
            for (int j = 0; j < p.getDim(); j++) {
                dispVectors[i][j] *= MinM;
            }
        }
        return dispVectors;
    }

    /**
     * This method returns the magnitude of a vector based on coordinates.
     * @param Coords
     * @return
     */
    protected static float getMagnitude(float[] Coords) {
        float M = 0;
        for (int i = 0; i < Coords.length; i++) {
            M += Coords[i] * Coords[i];
        }
        return (float) Math.sqrt(M);
    }
    
}
