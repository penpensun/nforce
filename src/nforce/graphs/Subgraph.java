/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nforce.graphs;

import java.util.ArrayList;
import java.util.List;

/**
 * This abstract class is subgraph, modified according to GraphV2.
 * 
 * Major modifications:
 * (1) Changed from "interface" to "abstract class". In abstract class Subgraph, element 
 * Subvertices is defined, to store the vertices.
 * @author penpen926
 */
public abstract class Subgraph {
    /* Elements. */
    /* The vertices in the subgraph. */
    protected ArrayList<Vertex> subvertices;

    
    /* Methods. */
    /* Return a connected component by breadth-first search given a vertex. */
    public abstract Subgraph bfs(Vertex Vtx);
    /* Return all connected components. */
    public abstract List<? extends Subgraph> connectedComponents();
    
    /* This method returns the edge weight. */
    public abstract float edgeWeight(Vertex vtx1, Vertex vtx2);
    
    /* This method returns true, since it's a subgraph. */
    public final boolean isSubgraph(){
        return true;
    }
    
    /* Return the Subvertices in this subgraph. */
    public final ArrayList<Vertex> getSubvertices(){
        return subvertices;
    }
    
    /* This method returns the super graph. */
    public abstract Graph getSuperGraph();
    
    /* Return the neighbours of a given vertex. */
    public abstract ArrayList<Vertex> neighbours(Vertex vtx);
    
    
    public abstract void setEdgeWeight(Vertex Vtx1, Vertex Vtx2, float EdgeWeight);
    /* Set the sub vertices. */
    public final void setSubvertices(ArrayList<Vertex> Subvertices){
        this.subvertices = Subvertices;
    }
    
    
    /* Return the number of vertex sets. */
    public abstract int vertexSetCount();
    
    /* Return the number of vertices. */
    public int vertexCount(){
        return subvertices.size();
    }
    
    
}
