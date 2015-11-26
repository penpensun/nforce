/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nforce.algorithms.example;

import nforce.algorithms.Param;
import nforce.io.Config;

/**
 * This is a demonstration, have fun.
 * @author penpen926
 */
public class Example {
    public static void runNforce(){
        //To run n-force, the best way is to create an nforce.io.Config object, which
        //contains all information needed to serve into the pipeline.
        nforce.io.Config conf = new nforce.io.Config();
        conf = new Config();
        //The input, now the n-force has been upgraded to use xml file as input.
        conf.input = "../../repos/inputxml.txt";
        //The cluster output, a mandatory parameter.
        conf.clusterOutput = "../../repos/repos_cluster_out.txt";
        //The editing details, not mandatory.
        conf.editOutput = null;
        //The result graph output, not mandatory.
        conf.graphOutput = null;
        // Here gives the user the graph type.
        // We have 5 types of graph:
        // 1. bipartite graph
        // 2. hierarchy n partite graph.
        // 3. hierarchy general graph.
        // 4. general graph.
        // 5. general n-partite graph.
        // @Nic, in your case, I think you can use 5.
        conf.graphType = 5; // Graph indicator
        conf.isXmlFormat = true; // Just to tell n-force your input is in xml format.
        
        // Create a parameter object. The default values are
        // stored in a file called "parameteres.ini"
        conf.p = new Param("./parameters.ini"); 
        
        // Set the density parameter. the threshold
        conf.p.setThresh(0.85F);
        
        // Thresh array is a paremter that you can set different thresholds for different
        // node set. Normally it is set to null. 
        conf.p.setThreshArray(null);
        
        
    }
}
