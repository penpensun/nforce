/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nforce.graphs;

/**
 * This class is the 2nd verion of class Vertex. 
 * The majore change is the key words of some class elements.
 * @author penpen926
 */
public class Vertex implements Comparable<Vertex>{
    private int vtxSet = -1;
    private String value= null;
    private int vtxIdx = 0;
    private int distIdx = 0; /* This is for MatirxHierNpartiteGraph, distance matrix 2nd version. */
    private float[] coords = null;
    private int clustNum=-1;
    
    /**
     * Constructor.
     * @param value
     * @param level
     * @param vtxidx 
     */
    public Vertex(String value, int level, int vtxidx){
        this.value = value;
        this.vtxSet = level;
        this.coords = null;
        this.vtxIdx = vtxidx;
    }
    
    /**
     * Constructor.
     * @param value
     * @param level
     * @param coords
     * @param vtxidx 
     */
    public Vertex(String value, int level, float[] coords, int vtxidx)
    {
        this.value = value;
        this.vtxSet = level;
        this.coords = coords;
        this.vtxIdx = vtxidx;
    }
    
    @Override
    public boolean equals(Object o)
    {
        //first check if the object is an instace of ActinoVertex
        if(!(o instanceof Vertex ))
            throw new IllegalArgumentException("this given object is not an instance of ActinoVertex");
        Vertex act = (Vertex)o;
        return (value.equals(act.getValue()) );
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
    
    @Override
    public int compareTo(Vertex vtx)
    {
        //first check if the object is an instace of ActinoVertex
        if(!(vtx instanceof Vertex)) {
            throw new IllegalArgumentException("this given object is not an instance of ActinoVertex");
        }
        return (value.compareTo(vtx.value));
    }
    
    
    /**
     * Accessor for clustNum.
     * @return 
     */
    public final int getClustNum(){
        return clustNum;
    }
    /**
     * Accessor for coords.
     * @return 
     */
    public final float[] getCoords(){
        return coords;
    }
    
    public final int getDistIdx(){
        return distIdx;
    }
    
    /**
     * Accessor for value.
     * @return 
     */
    public final String getValue(){
        return value;
    }
    
    
    /**
     * Accessor for vtxIdx.
     * @return 
     */
    public final int getVtxIdx()
    {
        return vtxIdx;
    }
    
    /**
     * Accessor for vertexSet
     * @return 
     */
    public final int getVtxSet(){
        return vtxSet;
    }
    
    /**
     * Mutator for clustNume.
     * @param clustNum 
     */
    public final void setClustNum(int clustNum){
        this.clustNum = clustNum;
    }
    
    /**
     * Mutator for coords.
     * @param coords 
     */
    public final void setCoords(float[] coords){
        this.coords = coords;
    }
    
    public final void setDistIdx(int distIdx){
        this.distIdx = distIdx;
    }
    /**
     * Mutator for value.
     * @param value 
     */
    public final void setValue(String value){
        this.value = value;
    }
    
    /**
     * Mutator for vtxIdx.
     * @param vtxIdx 
     */
    public final void setVtxIdx(int vtxIdx){
        this.vtxIdx = vtxIdx;
    }
    
    /**
     * Mutator for vertexSet.
     * 
     */
    public final void setVertexSet(int vtxSet){
        this.vtxSet = vtxSet;
    }
}
