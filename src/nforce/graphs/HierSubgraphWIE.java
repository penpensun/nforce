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

/**
 * The second verson of subgraph of hierachy subgraph with intra-edges.
 * @author penpen926
 */
public class HierSubgraphWIE extends Subgraph implements Comparable<HierSubgraphWIE>{
    /* The supergraph. */
    HierGraphWIE supergraph = null;
    
    public HierSubgraphWIE
            (ArrayList<Vertex> subVertices, HierGraphWIE supergraph){
        this.subvertices = subVertices;
        this.supergraph = supergraph;
    }
    /**
     * This method returns the connected component given a vertex using breadth-first search.
     * @param Vtx
     * @return 
     */
    @Override
    public HierSubgraphWIE bfs(Vertex Vtx) {
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
         /* Create a new subhierNpartitegraph. */
         HierSubgraphWIE sub = new HierSubgraphWIE(result,this.supergraph);
         return sub;
    }
    
    @Override
    public int compareTo(HierSubgraphWIE o) {
        if(this.vertexCount() < o.vertexCount())
            return -1;
        else if(this.vertexCount() == o.vertexCount())
            return 0;
        else return 1;
    }

    /**
     * This method returns the connected components in this subgraph.
     * @return 
     */
    @Override
    public List<? extends Subgraph> connectedComponents() {
        ArrayList<HierSubgraphWIE> connectedComps = new ArrayList<>();
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
            HierSubgraphWIE ConnectedComponent = bfs(Seed);
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
     * This method returns the edge weight given two vertices.
     * @param vtx1
     * @param vtx2
     * @return 
     */
    @Override
    public float edgeWeight(Vertex vtx1, Vertex vtx2) {
        return supergraph.edgeWeight(vtx1,vtx2);
    }

    @Override
    public boolean equals(Object o){
        /* First we check if the given object is an instance of MatrixHierGeneralSubgraph. */
        if(o instanceof HierSubgraphWIE){
            HierSubgraphWIE sub = (HierSubgraphWIE)o;
            ArrayList<Vertex> subVertices2 = sub.getSubvertices();
            /* Then we check the sizes of the two Vertex arraylists. */
            if(this.subvertices.size() != subVertices2.size())
                return false;
            /* Then we check the vertex one by one. */
            /* We first generate a copy of the subvertices in this class. */
            ArrayList<Vertex> subverticesCopy = new ArrayList(subvertices);
            for(int i=0;i<subVertices2.size();i++){
                if(!subverticesCopy.remove(subVertices2.get(i)))
                    return false;
            }
            /* If all vertices in subVertices2 can be found in subverticesCopy, 
             * then we return true.*/
            return true;
        }
        else
            return false;
    }
    @Override
    public HierGraphWIE getSuperGraph() {
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

    @Override
    public void setEdgeWeight(Vertex vtx1, Vertex vtx2, float edgeWeight) {
        supergraph.setEdgeWeight(vtx1, vtx2, edgeWeight);
    }

    @Override
    public int vertexSetCount() {
        return subvertices.size();
    }

    
}
