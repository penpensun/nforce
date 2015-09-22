/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nforce.graphs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * The matrix bipartite subgraph 
 * @author Peng Sun
 */
public class BipartiteSubgraph extends Subgraph implements Comparable<BipartiteSubgraph>{
    /* This is the supergraph. */
    private BipartiteGraph supergraph = null;
    
    
    /**
     * The constructor. 
     */
    public BipartiteSubgraph(ArrayList<Vertex> subvertices,
            BipartiteGraph supergraph){
        this.subvertices = subvertices;
        this.supergraph = supergraph;     
    }
    
    @Override
    public BipartiteSubgraph bfs(Vertex Vtx) {
        LinkedList<Vertex> queue = new LinkedList<>();
        //create a marker
        HashMap<String, Boolean> marker = new HashMap<>();
        //init the haspmap

        //create a new arrayList<Vertex> as result
        ArrayList<Vertex> result = new ArrayList<>();

        for(Vertex vtx: getSubvertices()){
            marker.put(vtx.toString(), Boolean.FALSE);
        }

        //enqueue source and mark source
         queue.add(Vtx);
         result.add(Vtx);
         marker.put(Vtx.toString(),true);

         //while queue is not empty
         while(!queue.isEmpty()){
             //dequeue an item from queue into CurrentVtx
             Vertex CurrentVtx = queue.pollLast();
             //get the nei of CurrentVtx
             ArrayList<Vertex> nei = neighbours(CurrentVtx);

             /* If no neighbour is found, then we continue. */
             if(nei == null)
                 continue;
             //for each neighbour
             for(Vertex vtx: nei){
                 if(!marker.get(vtx.toString())){
                     marker.put(vtx.toString(),true);
                     queue.add(vtx);
                     result.add(vtx);
                 }
             }

         }
         //Create a new subkpartitegraph
         BipartiteSubgraph sub = new BipartiteSubgraph(result,this.supergraph);
         return sub;
    }
    
    /**
     * This method compares two subgraphs, based on the number of vertices.
     * @param o
     * @return 
     */
    @Override
    public int compareTo(BipartiteSubgraph o) {
        if(this.vertexCount() < o.vertexCount())
            return -1;
        else if(this.vertexCount() == o.vertexCount())
            return 0;
        else return 1;
    }

    /**
     * Get all connected components.
     * @return 
     */
    @Override
    public List<BipartiteSubgraph> connectedComponents() {
        ArrayList<BipartiteSubgraph> connectedComps = new ArrayList<>();
        //create a indicator LinkedList of vertices, when a vertex is included in one of the subgraphs, then it is 
        //removed from the indicator LinkedList
        LinkedList<Vertex> indicatorList = new LinkedList<>();
        //add all the vertex into the IndicatorArray
        for(Vertex vtx: getSubvertices()){
            indicatorList.add(vtx);
        }
        //While there is still unvisited vertex, we use it as the seed to search for subgraphs.
        while(!indicatorList.isEmpty()){
            Vertex Seed = indicatorList.pollFirst();
            BipartiteSubgraph ConnectedComponent = bfs(Seed);
            connectedComps.add(ConnectedComponent);
            //remove all the vertex in the ConnectedComponent from indicatorList
            for(Vertex vtx: ConnectedComponent.getSubvertices()){
                indicatorList.remove(vtx);
            }
        }
        connectedComps.trimToSize();
        return connectedComps;
    }

    /**
     * Return the edge weight.
     * @param vtx1
     * @param vtx2
     * @return 
     */
    @Override
    public float edgeWeight(Vertex vtx1, Vertex vtx2) {
        return supergraph.edgeWeight(vtx1, vtx2);
    }
    
    /**
     * This method returns if the given object is equal to the current subgraph.
     * @param subgraph
     * @return 
     */
    @Override
    public boolean equals(Object input){
        if(!(input instanceof BipartiteSubgraph ))
            throw new IllegalArgumentException("(MatrixBipartiteSubgraphV2.equals) "
                    + "The input object is not a MatrixBipartiteSubgraphV2.");
        
        BipartiteSubgraph subgraph = (BipartiteSubgraph)input;
        if(this.subvertices.size() != subgraph.subvertices.size())
            return false;
        else{
            for(Vertex vtx: subvertices)
                if(!subgraph.subvertices.contains(vtx))
                    return false;
        }
        return true;  
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + Objects.hashCode(this.supergraph);
        return hash;
    }

    /**
     * Return the supergraph.
     * @return 
     */
    @Override
    public BipartiteGraph getSuperGraph() {
        return supergraph;
    }
    
    

    @Override
    public ArrayList<Vertex> neighbours(Vertex vtx) {
        ArrayList<Vertex> superNbs = supergraph.neighbours(vtx);
        ArrayList<Vertex> answer = new ArrayList<>();
        
        /* Check if the vertex has neighbours in the supergraph. */
        if(superNbs == null)
            return null;
        for(Vertex v:superNbs){
            if(subvertices.contains(v))
                answer.add(v);
        }
        return answer;
    }

    /**
     * Set the edge weight between vtx1 and vtx2.
     * @param Vtx1
     * @param Vtx2
     * @param EdgeWeight 
     */
    @Override
    public void setEdgeWeight(Vertex vtx1, Vertex vtx2, float edgeWeight) {
        supergraph.setEdgeWeight(vtx1, vtx2, edgeWeight);
    }

    /**
     * This method returns how many vertex sets are there in the Subgraph, namely, 2.
     * @return 
     */
    @Override
    public final int vertexSetCount() {
        return 2;
    }    
}
