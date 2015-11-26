/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nforce.algorithms;

import org.junit.Test;
import static org.junit.Assert.*;
import nforce.io.Config;
import nforce.sim.SimGraphGenerator;
import nforce.io.Main;
import nforce.graphs.Graph;

/**
 *
 * @author penpen926
 */
public class nForceOnGraphTest {
    SimGraphGenerator sim = new SimGraphGenerator();
    String bipartiteInput = "../testdata/stdtest/bipartite_input.txt";
    String npartiteInput = "../testdata/stdtest/npartite_input.txt";
    String hierInput = "../testdata/stdtest/hier_input.txt";
    String hierWieInput = "../testdata/stdtest/hierwie_input.txt";
    String generalInput = "../testdata/stdtest/general_input.txt";
    
    String bipartiteOutput = "../testdata/stdtest/bipartite_output.txt";
    String npartiteOutput = "../testdata/stdtest/npartite_output.txt";
    String hierOutput = "../testdata/stdtest/hier_output.txt";
    String hierWieOutput = "../testdata/stdtest/hierwie_output.txt";
    String generalOutput = "../testdata/stdtest/general_output.txt";
    String paramFile = "./parameters.ini";
    float mean = 20;
    float stdev = 15;
    public nForceOnGraphTest() {
    }

    @Test
    public void testOnBipartiteGraph() {
        int[] setSizes = {20,20};
        Config conf = new Config();
        conf.input = bipartiteInput;
        conf.clusterOutput = bipartiteOutput;
        conf.isXmlFormat = true;
        conf.hasHeader = false;
        conf.p = new Param();
        conf.p.setThresh(0f);
        conf.p.setThreshArray(null);
        conf.p = new Param(paramFile);
        conf.graphType = 1;
        float stdCost = sim.genBipartiteGraph(setSizes, bipartiteInput, mean, stdev);
        Main mainAlgor = new Main();
        Graph resultGraph = mainAlgor.runGraph(conf);
        assertEquals(stdCost,resultGraph.getCost(),0.01*stdCost);
        System.out.println("Bipartite graph cost: "+stdCost+" "+resultGraph.getCost());
    }
    
    @Test
    public void testOnBipartiteGraph2(){
        String inputFile = "/Users/penpen926/work/projects/BiForce/data/testdata/unit_test/MatrixBipartiteGraph/run_input.txt";
        Config conf = new Config();
        conf.input = inputFile;
        conf.clusterOutput = bipartiteOutput;
        conf.isXmlFormat = false;
        conf.hasHeader = false;
        conf.p = new Param();
        conf.p.setThresh(0f);
        conf.p = new Param(paramFile);
        conf.graphType = 1;
        Main mainAlgor = new Main();
        Graph resultGraph = mainAlgor.runGraph(conf);
        System.out.println("Bipartite graph cost: "+resultGraph.getCost());
    }
    
    @Test
    public void testOnGeneralGraph(){
        int size = 100;
        Config conf = new Config();
        conf.input = generalInput;
        conf.clusterOutput = generalOutput;
        conf.isXmlFormat = true;
        conf.hasHeader = false;
        conf.p = new Param();
        conf.p.setThresh(0f);
        conf.p = new Param(paramFile);
        conf.graphType = 4;
        float stdCost = sim.genGeneralGraph2(size, generalInput, mean, stdev);
        Main mainAlgor = new Main();
        Graph resultGraph = mainAlgor.runGraph(conf);
        assertEquals(stdCost,resultGraph.getCost(),0.01*stdCost);
        System.out.println("General graph costs:  "+stdCost+"  "+resultGraph.getCost());
    }
    
    @Test
    public void testOnHierGraph(){
        int[] setSizes = {50,50,50};
        Config conf = new Config();
        conf.input = hierInput;
        conf.clusterOutput = hierOutput;
        conf.isXmlFormat = true;
        conf.hasHeader = false;
        conf.p = new Param();
        conf.p.setThresh(0f);
        conf.p.setThreshArray(null);
        conf.p = new Param(paramFile);
        conf.graphType = 2;
        float stdCost = sim.genHierGraph(setSizes, hierInput, mean, stdev);
        Main mainAlgor = new Main();
        Graph resultGraph = mainAlgor.runGraph(conf);
        assertEquals(stdCost,resultGraph.getCost(),0.01*stdCost);
        System.out.println("Hier graph cost: "+stdCost+" "+resultGraph.getCost());
    }
    
    @Test
    public void testOnHierGraphWIE(){
        int[] setSizes = {50,50,50,50};
        Config conf = new Config();
        conf.input = hierWieInput;
        conf.clusterOutput = hierWieOutput;
        conf.isXmlFormat = true;
        conf.hasHeader = false;
        conf.p = new Param();
        conf.p.setThresh(0f);
        conf.p.setThreshArray(null);
        conf.p = new Param(paramFile);
        conf.graphType = 3;
        float stdCost = sim.genHierGraphWIE(setSizes, hierWieInput, mean, stdev);
        Main mainAlgor = new Main();
        Graph resultGraph = mainAlgor.runGraph(conf);
        assertEquals(stdCost,resultGraph.getCost(),0.01*stdCost);
        System.out.println("Hier graph wie cost : "+stdCost+" "+resultGraph.getCost());
    }
    
    @Test
    public void testOnNpartiteGraph(){
        int[] setSizes = {50,50,50,50};
        Config conf = new Config();
        conf.input = npartiteInput;
        conf.clusterOutput = npartiteOutput;
        conf.isXmlFormat = true;
        conf.hasHeader = false;
        conf.p = new Param();
        conf.p.setThresh(0f);
        conf.p.setThreshArray(null);
        conf.p = new Param(paramFile);
        conf.graphType = 5;
        float stdCost = sim.genHierGraphWIE(setSizes, npartiteInput, mean, stdev);
        Main mainAlgor = new Main();
        Graph resultGraph = mainAlgor.runGraph(conf);
        assertEquals(stdCost,resultGraph.getCost(),0.01*stdCost);
        System.out.println("Hier graph wie cost : "+stdCost+" "+resultGraph.getCost());
    }
    
}
