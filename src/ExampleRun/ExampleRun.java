/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ExampleRun;
import nforce.sim.SimGraphGenerator;
import nforce.algorithms.Param;
import nforce.io.Config;
import nforce.graphs.NpartiteGraph;
import nforce.graphs.Cluster;
import java.util.ArrayList;
/**
 *
 * @author penpen926
 */
public class ExampleRun {
    
    public void run(){
        String inputGraph = "../npartite_sim_graph.txt";
        String graphOutput = "../npartite_sim_graph_out.txt";
        String clusterOutput = "../npartite_sim_cluster_out.txt";
        String editOutput = "../npartite_sim_edit_out.txt";
        // Read the paramter file.
        String parameterFile = "./parameters.ini";
        Param p = new Param(parameterFile);
        
        //Generate the artificial graph.
        SimGraphGenerator simGen = new SimGraphGenerator();
        float mean = 15;
        float dev = 10;
        int[] setSize = {60,60,60};
        simGen.genNpartiteGraph(setSize, inputGraph, mean, dev);
        
        //Create the config object.
        Config conf = new Config();
        conf.clusterOutput = clusterOutput;
        conf.editOutput = editOutput;
        conf.graphOutput = graphOutput;
        conf.input = inputGraph;
        conf.p = p;
        conf.isXmlFormat = true;
        conf.graphType = 5;
        
        //NpartiteGraph g = (NpartiteGraph)nforce.io.Main.execute(conf);
        nforce.io.Main.runConfig(conf);
        
    }
    
    
    public void run2(){
        String configFile ="./test_config.txt";
        nforce.io.Main.runGraph(1, configFile);
    }
    
    public static void main(String args[]){
        ExampleRun r = new ExampleRun();
        r.run();
        //r.run2();
    }
}
