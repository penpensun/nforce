/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nforce.util;

import nforce.graphs.Vertex;

/**
 * New version of action.
 * @author peng sun
 */
public class Action {
    private Vertex vtx1;
    private Vertex vtx2;
    private double OriginalWeight;
    
    public Action(Vertex vtx1, Vertex vtx2, double OriginalWeight)
    {
        this.vtx1 = vtx1;
        this.vtx2 = vtx2;
        this.OriginalWeight = OriginalWeight;
    }
    
    public Vertex getVtx1()
    {
        return vtx1;
    }
    
    public Vertex getVtx2()
    {
        return vtx2;
    }
    
    public double getOriginalWeight() 
    {
        return OriginalWeight;
    }
}
