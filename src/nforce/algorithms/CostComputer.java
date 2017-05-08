/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nforce.algorithms;

import nforce.graphs.Cluster;
import nforce.graphs.Graph;
import nforce.graphs.Vertex;

/**
 *
 * @author penpen926
 */
public class CostComputer {

    /**
     * This method returns the difference of the cost (a positive value means increase in cost, and a
     * negative value means decrease in cost) if we move the given vertex to the cluster clt from its original
     * cluster
     * @param graph
     * @param toMove
     * @param sourceClr
     * @param destClr
     * @return
     */
    protected static float computeMoveCost(Graph graph, Vertex toMove, Cluster sourceClr, Cluster destClr) {
        //check if the point toMove belongs to the cluster sourceClr
        if (toMove.getClustNum() != sourceClr.getClustIdx()) {
            System.out.println("(BiForceOnGraph4 computeMoveCost) Vertex to move is in cluster: " + toMove.getClustNum());
            System.out.println("(BiForceOnGraph4 computeMoveCost) Dest cluster: " + sourceClr.getVertices().get(0).getClustNum());
            throw new IllegalArgumentException("(BiForceOnGraph4 computeMoveCost) " + "The point to move does not belong to the source cluster.");
        }
        float costDiff = 0.0F;
        /* Compute the costDiff for moving the given vertex out of the sourceClr. */
        for (Vertex vtx : sourceClr.getVertices()) {
            float ew = graph.edgeWeight(toMove, vtx);
            /* If there is a non-defined edge, then we continue. */
            if (Double.isNaN(ew)) {
                continue;
            }
            /* If there is a negative-edge, it gives negative difference since we have to
            delete the inserted edge.*/
            if (ew < 0) {
                costDiff += ew;
                /* If there is a positive-edge, it gives positive difference since we have to
                delete the original edge.*/
            } else {
                costDiff += ew;
            }
        }
        /* Compute the costDiff for moving the given vertex into the given destClr. */
        for (Vertex vtx : destClr.getVertices()) {
            float ew = graph.edgeWeight(toMove, vtx);
            /* If there is a non-defined edge, then we continue. */
            if (ew == 0) {
                continue;
            }
            /* If there is a negative-edge, it gives positive difference since we have to
            insert the missing edge.*/
            if (ew < 0) {
                costDiff -= ew;
                /* If there is a positive-edge, it gives negative difference since we have to
                insert back the deleted edge.*/
            } else {
                costDiff -= ew;
            }
        }
        return costDiff;
    }

    /**
     * This method returns the difference of the cost (a positive value means increase in cost, and a
     * negative value means decrease in cost) if we merge cluster c1 and c2 together.
     * @param Points
     * @param c1
     * @param c2
     * @return
     */
    protected static float computeMergeCost(Graph graph, Cluster c1, Cluster c2) {
        float CostDiff = 0;
        for (Vertex pt1 : c1.getVertices()) {
            for (Vertex pt2 : c2.getVertices()) {
                float ew = graph.edgeWeight(pt1, pt2);
                if (ew == 0 || Double.isNaN(ew)) {
                    continue;
                }
                if (ew < 0) {
                    CostDiff -= ew;
                } else {
                    CostDiff -= ew;
                }
            }
        }
        return CostDiff;
    }

    /**
     * This method calculates the costs for a given cluster assignment
     *
     * @Untested
     * @param Points
     * @return
     * @throws IllegalArgumentException. When there is point without a cluster assignment.
     */
    protected static void assignActions(Graph graph) {
        //first check if there is no unassigned vertex
        for (Vertex vtx : graph.getVertices()) {
            if (vtx.getClustNum() == -1) {
                throw new IllegalArgumentException("BiForceOnGraph4:  " + vtx.getValue() + " not assigned with any cluster.");
            }
        }
        for (int i = 0; i < graph.getVertices().size(); i++) {
            for (int j = i + 1; j < graph.getVertices().size(); j++) {
                Vertex vtx1 = graph.getVertices().get(i);
                Vertex vtx2 = graph.getVertices().get(j);
                if (vtx1.getValue().equals("drug_118") && vtx2.getValue().equals("disease_916")) {
                    System.out.println();
                }
                if (vtx2.getValue().equals("drug_118") && vtx1.getValue().equals("disease_916")) {
                    System.out.println();
                }
                float ew = graph.edgeWeight(vtx1, vtx2);
                //if there is no edge between them, then continue;
                //if(ew == 0 || Float.isNaN(ew))
                if (Float.isNaN(ew)) {
                    continue;
                }
                //case 1, in the same cluster with negative edge: then we need an addition
                if (vtx1.getClustNum() == vtx2.getClustNum() && ew <= 0) {
                    graph.pushAction(vtx1, vtx2);
                } else if (vtx1.getClustNum() != vtx2.getClustNum() && ew > 0) {
                    graph.pushAction(vtx1, vtx2);
                }
            }
        }
    }

    /**
     * This class returns the cost of editing from a given set of points. Without constructing the Actions objects, this method
     * should return the cost much quicker
     * @param graph
     * @param p
     * @return
     */
    protected static float computeCost(Graph graph) {
        float cost = 0;
        //first check if there is no unassigned vertex
        for (Vertex vtx : graph.getVertices()) {
            if (vtx.getClustNum() == -1) {
                throw new IllegalArgumentException(vtx.getValue() + " not assigned with any cluster");
            }
        }
        for (int i = 0; i < graph.getVertices().size(); i++) {
            for (int j = i + 1; j < graph.getVertices().size(); j++) {
                Vertex vtx1 = graph.getVertices().get(i);
                Vertex vtx2 = graph.getVertices().get(j);
                float ew = graph.edgeWeight(vtx1, vtx2);
                /* If there is no edge between vtx1 and vtx2, then continue. */
                if (Double.isNaN(ew)) {
                    continue;
                }
                //case 1, in the same cluster with negative edge: then we need an addition
                if (vtx1.getClustNum() == vtx2.getClustNum() && ew < 0) {
                    cost -= ew;
                }
                //case 2, in different clusters but with a positive edge: we need a deletion
                if (vtx1.getClustNum() != vtx2.getClustNum() && ew > 0) {
                    cost += ew;
                }
            }
        }
        //System.out.println(cost);
        return cost;
    }
    
}
