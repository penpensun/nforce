/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nforce.io;
import nforce.graphs.Graph;
import nforce.graphs.GeneralGraph;
import nforce.graphs.BipartiteGraph;
import nforce.graphs.HierGraph;
import nforce.graphs.HierGraphWIE;
import nforce.graphs.NpartiteGraph;
import java.io.File;
import java.io.IOException;
import nforce.algorithms.*;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
/**
 *
 * @author penpen926
 */
public class Main {
    /**
     * This method calls nforce by parsing the command line.
     * @param args 
     */
    public void parseCommandLine(String args[]){
        
    }
    
    /**
     * This method calls nforce by parsing the config file.
     */
    public static void parseConfigFile(String configFile){
        FileReader fr = null;
        BufferedReader br = null;
        Config conf = new Config();
        try{
            fr = new FileReader(configFile);
            br = new BufferedReader(fr);
            String line = null;
            while((line = br.readLine())!= null){
                line= line.trim();
                if(line.isEmpty() || line.startsWith("#"))
                    continue;
                //Check each case:
                if(line.startsWith("graphType = ")){
                    try{
                        conf.graphType = Integer.parseInt(line.split("graphType = ")[1].trim());
                    }catch(NumberFormatException e){
                        // What if the number format is wrong.
                        System.err.println(" Config file. Graph type number format error:  "+line);
                        e.printStackTrace();
                        System.exit(0);
                    }
                    catch(IndexOutOfBoundsException e){
                        // What if the graph type string does not exist.
                        System.err.println(" Config file. Graph type is not given:  "+line);
                        e.printStackTrace();
                        System.exit(0);
                    }
                }
                if(line.startsWith("input = ")){
                    try{
                        //What if the input string does not exist.
                        conf.input = String.copyValueOf(line.split("input = ")[1].trim().toCharArray());
                    }catch(IndexOutOfBoundsException e){
                        System.err.println("Config file. Input string does not exist: "+line);
                        System.exit(0);
                    }
                }
                if(line.startsWith("isFormatXml = ")){
                    try{
                        conf.isXmlFormat = Boolean.parseBoolean(line.split("ixFormatXml = ")[1].trim());
                    }catch(IndexOutOfBoundsException e){
                        System.err.println("Config file. isXmlFormat does not exists: "+line);
                        System.exit(0);
                    }
                }
                if(line.startsWith("hasHeader = ")){
                    try{
                        conf.hasHeader = Boolean.parseBoolean(line.split("hasHeader = ")[1].trim());
                    }catch(IndexOutOfBoundsException e){
                        conf.hasHeader = false;
                    }
                }
                if(line.startsWith("clusterOutput = ")){
                    try{
                        conf.clusterOutput = String.copyValueOf(line.split("clusterOutput = ")[1].trim().toCharArray());
                    }catch(IndexOutOfBoundsException e){
                        System.err.println("Config file. The given cluster output file does not exist: "+line);
                        System.exit(0);
                    }
                }
                if(line.startsWith("editOutput = ")){
                    String[] splits = line.split("editOutput = ");
                    if(splits.length <2)
                        conf.editOutput=  null;
                    else{
                        conf.editOutput = String.copyValueOf(splits[1].trim().toCharArray());
                        
                        if(conf.editOutput.equalsIgnoreCase("null"))
                            conf.editOutput = null;
                    }
                }
                if(line.startsWith("graphOutput = ")){
                    String[] splits = line.split("graphOutput = ");
                    if(splits.length <2)
                        conf.graphOutput = null;
                    else{
                        conf.graphOutput = String.copyValueOf(splits[1].trim().toCharArray());
                        if(conf.graphOutput.equalsIgnoreCase("null"))
                            conf.graphOutput = null;
                    }
                }
                if(line.startsWith("threshold = ")){
                    try{
                        String[] splits = line.split("threshold = ");
                        if(splits.length <2)
                            conf.p.setThresh(Float.NaN);
                        else {
                            String threshString = splits[1].trim();
                            if(threshString.equalsIgnoreCase("null"))
                                conf.p.setThresh(Float.NaN);
                            else
                                conf.p.setThresh(Float.parseFloat(threshString));
                        }
                    }catch(NumberFormatException e){
                        System.err.println("Config file. Illegal number format of the thresh: "+line);
                    }
                }
                if(line.startsWith("threshArray = ")){
                    try{
                        String[] splits = line.split("threshArray = ");
                        if(splits.length <2)
                            conf.p.setThreshArray(null);
                        else{
                            String threshArrayString = splits[1].trim();
                            //Split the threhArray string.
                            String[] threshArraySplits = threshArrayString.split(" +");
                            float[] threshArray = new float[threshArraySplits.length];
                            for(int i=0;i<threshArray.length;i++){
                                threshArray[i] = Float.parseFloat(threshArraySplits[i]);
                            }
                        }
                    }catch(NumberFormatException e){
                        System.err.println("Config file. Illegal number format for thresh array:  "+line);
                    }
                }
            }
            
        
        }catch(IOException e){
            System.err.println("Config file reading error.");
            e.printStackTrace();
        }
        
        // Run nforce on the graph
        Graph resultGraph = runGraph(conf);
        
        // Output the results
        // Output the cluster output.
        resultGraph.writeClusterTo(conf.clusterOutput, true);
        if(conf.editOutput != null)
            resultGraph.writeResultInfoTo(conf.editOutput);
        if(conf.graphOutput != null)
            resultGraph.writeGraphTo(conf.graphOutput, true);
    }
    /**
     * This method runs nforce with the given config object.
     * @param conf
     * @return 
     */
    public static Graph runGraph(Config conf){
        // Check the parameters.
        /* Check the parameters. */
        if(conf.p.getThreshArray() != null && !Float.isNaN(conf.p.getThresh()))
            throw new IllegalArgumentException("nforce cannot run due to parameter error: "
                    + "Multiple thresholds and single threshold are both given.");
        if(conf.p.getThreshArray() == null && Float.isNaN(conf.p.getThresh()))
            throw new IllegalArgumentException("nforce cannot run due to parameter error: "
                    + "Neither multiple thresholds nor single threshold is given.");
        if(conf.p.getLowerth() <0)
            throw new IllegalArgumentException(" Error! The lower-bound of the threshold cannot be smaller than 0.");
        if(conf.p.getUpperth() <0)
            throw new IllegalArgumentException(" Error! The upper-bound of the threshold cannot be smaller than 0.");
        if(conf.p.getStep()<0)
            throw new IllegalArgumentException(" Error! The step of the threshold cannot be smaller than 0.");
        // Check input file.
        if(conf.input == null)
            throw new IllegalArgumentException(" Error! The input graph cannot be null.");
        if(!new File(conf.input).exists())
            throw new IllegalArgumentException(" Error! The input file does not exist:  "+conf.input);
        
        // Check cluster output file.
        if(conf.clusterOutput==null)
            throw new IllegalArgumentException(" Error! The cluster output cannotbe null.");
        try{
            FileWriter fw = new FileWriter(conf.clusterOutput);
            fw.close();
        }catch(IOException e){
            throw new IllegalArgumentException(" Error! Illegal cluster output file: "+conf.clusterOutput);
        }
        // Check the graph output.
        if(conf.graphOutput != null && 
                !new File(conf.graphOutput).canWrite())
            throw new IllegalArgumentException(" Error! Illegal graph output file: "+conf.graphOutput);
        
        
        // Check the edit output.
         if(conf.editOutput != null && 
                !new File(conf.editOutput).canWrite())
            throw new IllegalArgumentException(" Error! Illegal edit output file: "+conf.editOutput);
         // Init the graph.
        Graph inputGraph = null;
        switch(conf.graphType){
            case 1: /* bipartite graph. */
                inputGraph = new BipartiteGraph(conf.input,conf.hasHeader, conf.isXmlFormat); 
                break;
            case 2:/* hierarchy npartite graph. */
                inputGraph = new HierGraph(conf.input, conf.hasHeader, conf.isXmlFormat);
                break;
            case 3: /* hierarchy general graph. */
                inputGraph = new HierGraphWIE(conf.input,conf.hasHeader,conf.isXmlFormat);
                break;
            case 4: /* General graph. */
                inputGraph = new GeneralGraph(conf.input,conf.hasHeader, conf.isXmlFormat);
                break;
            case 5: /* General npartite graph. */
                inputGraph = new NpartiteGraph(conf.input, conf.hasHeader, conf.isXmlFormat);
                break;
            default: 
                throw new IllegalArgumentException("(runGraph) The given graph type is illegal.");   
        }
         nForceOnGraph nforce = new nForceOnGraph();
         try{
             boolean isMultipleThresh = !(conf.p.getThreshArray() == null);
             inputGraph = nforce.run(inputGraph, conf.p, 1, isMultipleThresh); /* The main entrace. */
        }catch(IOException e){
            System.err.println("(runGraph) The algorithm error.");
            return null;
        }
        return inputGraph;
    }

    
    public static void main(String args[]){
        String a= "5";
        System.out.println(Integer.parseInt(a));
    }
}
