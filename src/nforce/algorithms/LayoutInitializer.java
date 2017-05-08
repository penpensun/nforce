/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nforce.algorithms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import nforce.graphs.Graph;
import nforce.graphs.Vertex;

/**
 *
 * @author penpen926
 */
public class LayoutInitializer {

    /**
     * This method initializes the layout of a given graph.
     * @param graph
     * @param p
     * @return
     */
    protected static ArrayList<Vertex> initLayout(Graph graph, Param p) {
        int dim = p.getDim();
        float radius = p.getRadius();
        Random rd = new Random();
        int vtces = graph.vertexCount();
        ArrayList<Vertex> vertexList;
        vertexList = new ArrayList<>();
        Iterator it = graph.getVertices().iterator();
        int i = 0;
        //for each vertex create a point with random coordinates
        //initiate two constant, used to distribute points on the surface of a sphere
        float inc = (float) (Math.PI * (3 - Math.sqrt(5)));
        float off = (float) 2.0 / vtces;
        while (it.hasNext()) {
            float[] coords = new float[dim];
            //retrieve the vertex
            Vertex vtx = (Vertex) it.next();
            // if dim==2 distribute points equidistantly on the circle
            if (dim == 2) {
                //coords[0] = (radius*Math.cos((i*2*Math.PI)/vtces)) + rd.nextGaussian();
                coords[0] = (float) (radius * Math.cos((i * 2 * Math.PI) / vtces));
                //coords[1] = radius*Math.sin((i*2*Math.PI)/vtces)+rd.nextGaussian();
                coords[1] = (float) (radius * Math.sin((i * 2 * Math.PI) / vtces));
                vtx.setCoords(coords);
                vertexList.add(vtx);
                i++;
            } else if (dim == 3) {
                float y = i * off - 1 + (off / 2.0F);
                float r = (float) Math.sqrt(1 - y * y);
                float phi = i * inc;
                coords[0] = (float) Math.cos(phi) * r * radius;
                coords[1] = y * radius;
                coords[2] = (float) Math.sin(phi) * r * radius;
                vtx.setCoords(coords);
                vertexList.add(vtx);
                i++;
            } else {
                float norm = 0;
                //generate dim pseudorandom numbers between -1 and 1
                // normalize these vectors and scale them to a length of 50
                for (int j = 0; j < dim; j++) {
                    coords[j] = rd.nextFloat() * 2 - 1;
                    norm += Math.pow(coords[j], 2);
                }
                norm = (float) Math.sqrt(norm);
                for (int j = 0; j < dim; j++) {
                    coords[j] /= norm;
                    coords[j] *= radius;
                }
                vtx.setCoords(coords);
                vertexList.add(vtx);
            }
        }
        return vertexList;
    }
    
}
