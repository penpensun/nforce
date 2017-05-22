/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nforce.util;
import java.io.*;
import java.util.ArrayList;
import nforce.graphs.BipartiteGraph;
import nforce.graphs.GeneralGraph;
import nforce.graphs.HierGraph;
import nforce.graphs.HierGraphWIE;
import nforce.graphs.NpartiteGraph;
import nforce.graphs.Vertex;
/**
 * This class contains several parsers useful to extract information from xml formatted inputs for
 * graphs. Some parsers are also responsible to assign values to the corresponding variables in the 
 * graph objects.
 * @author penpen926
 */
public class XmlInputParser {
    /**
     * This parser parses the content of an "entity" element, extracting the names of the nodes, creating
     * Vertex objects and assign them to the corresponding inputGraph.
     * @param entityContent
     * @param inputGraph 
     */
    public void parseEntityString(String entityContent, HierGraphWIE inputGraph){
        /* Check if setSizes in the inputGraph has been checked.*/
        if(inputGraph == null || inputGraph.getSetSizes() == null)
            throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) The setSizes array is not initialized in the graph.");
        
        /* First check if the array setSizes is initialized.*/
        /* Split the content of the entity by lines. */
        String[] contentSplits = entityContent.split("\n");
        /* The length of the contentSplits might be larger than the number of different levels, yet if
        the contentSplits' length is smaller than the number of the levels, then we have to throw an exception.*/
        if(contentSplits.length<inputGraph.getSetSizes().length)
            throw new IllegalStateException("(biforce.graphs.XmlInputParser.parseEntity) The levels in the content of element \"entity\" "+
                    "is smaller then the length of setSizes. setSizes: "+inputGraph.getSetSizes().length+"  content:  "+contentSplits.length);
        /* Create nodes and push into vertices. */
        int setIdx = 0;
        for(int i=0;i<contentSplits.length;i++){
            /* Get the line. */
            String line = contentSplits[i];
            line = line.trim();
            /* Jump the empty line. */
            if(line.isEmpty())
                continue;
            /* Split the line. */
            String[] lineSplits = line.split("\t");
            inputGraph.getSetSizes()[setIdx] = lineSplits.length; /* The number of nodes in set i is the length of the splits on line i. */
            for(int j=0;j<lineSplits.length;j++){
                /* For each split, we create node. */
                String value = String.copyValueOf(lineSplits[j].toCharArray());
                Vertex vtx = new Vertex(value,setIdx,j); /* The value of the node is the split. 
                The level is i. The index is j.*/
                // Set the dist index of the given node.
                vtx.setDistIdx(inputGraph.getVertices().size()); // A big big bug, fixed on 31.03.2015
                inputGraph.getVertices().add(vtx); /* Push the node into vertices. */
            }
            setIdx++;
        }
        /* Check if all levels are initialized.*/
        if(setIdx < inputGraph.getSetSizes().length)
            throw new IllegalStateException("(biforce.graphs.XmlInputParser.parseEntity) Not enough node sets given by element \"entity\": "+
                    setIdx+"  length of setSizes:  "+inputGraph.getSetSizes().length);
    }
    
    /**
     * This parser parses the entity string for MatrixHierNpartiteGraph.
     * @param entityContent
     * @param inputGraph 
     */
    public void parseEntityString(String entityContent, HierGraph inputGraph){
        /* Check if setSizes in the inputGraph has been checked.*/
        if(inputGraph == null || inputGraph.getSetSizes() == null)
            throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) The setSizes array is not initialized in the graph.");
        
        /* First check if the array setSizes is initialized.*/
        /* Split the content of the entity by lines. */
        String[] contentSplits = entityContent.split("\n");
        /* The length of the contentSplits might be larger than the number of different levels, yet if
        the contentSplits' length is smaller than the number of the levels, then we have to throw an exception.*/
        if(contentSplits.length<inputGraph.getSetSizes().length)
            throw new IllegalStateException("(biforce.graphs.XmlInputParser.parseEntity) The levels in the content of element \"entity\" "+
                    "is smaller then the length of setSizes. setSizes: "+inputGraph.getSetSizes().length+"  content:  "+contentSplits.length);
        /* Create nodes and push into vertices. */
        int setIdx = 0;
        for(int i=0;i<contentSplits.length;i++){
            /* Get the line. */
            String line = contentSplits[i];
            line = line.trim();
            /* Jump the empty line. */
            if(line.isEmpty())
                continue;
            /* Split the line. */
            String[] lineSplits = line.split("\t");
            inputGraph.getSetSizes()[setIdx] = lineSplits.length; /* The number of nodes in set i is the length of the splits on line i. */
            for(int j=0;j<lineSplits.length;j++){
                /* For each split, we create node. */
                String value = String.copyValueOf(lineSplits[j].toCharArray());
                Vertex vtx = new Vertex(value,setIdx,j); /* The value of the node is the split. 
                The level is i. The index is j.*/
                // Set the dist index of the given node.
                vtx.setDistIdx(inputGraph.getVertices().size()); // A big big bug, fixed on 31.03.2015
                inputGraph.getVertices().add(vtx); /* Push the node into vertices. */
            }
            setIdx++;
        }
        /* Check if all levels are initialized.*/
        if(setIdx < inputGraph.getSetSizes().length)
            throw new IllegalStateException("(biforce.graphs.XmlInputParser.parseEntity) Not enough node sets given by element \"entity\": "+
                    setIdx+"  length of setSizes:  "+inputGraph.getSetSizes().length);
    }
    
    /**
     * This parser parses the entity string for MatrixGeneralNpartiteGraph
     * @param entityContent
     * @param inputGraph 
     */
    public void parseEntityString(String entityContent, NpartiteGraph inputGraph){
        /* Check if setSizes in the inputGraph has been checked.*/
        if(inputGraph == null || inputGraph.getSetSizes() == null)
            throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) The setSizes array is not initialized in the graph.");
        
        /* First check if the array setSizes is initialized.*/
        /* Split the content of the entity by lines. */
        String[] contentSplits = entityContent.split("\n");
        /* The length of the contentSplits might be larger than the number of different levels, yet if
        the contentSplits' length is smaller than the number of the levels, then we have to throw an exception.*/
        if(contentSplits.length<inputGraph.getSetSizes().length)
            throw new IllegalStateException("(biforce.graphs.XmlInputParser.parseEntity) The levels in the content of element \"entity\" "+
                    "is smaller then the length of setSizes. setSizes: "+inputGraph.getSetSizes().length+"  content:  "+contentSplits.length);
        /* Create nodes and push into vertices. */
        int setIdx = 0;
        for(int i=0;i<contentSplits.length;i++){
            /* Get the line. */
            String line = contentSplits[i];
            line = line.trim();
            /* Jump the empty line. */
            if(line.isEmpty())
                continue;
            /* Split the line. */
            String[] lineSplits = line.split("\t");
            inputGraph.getSetSizes()[setIdx] = lineSplits.length; /* The number of nodes in set i is the length of the splits on line i. */
            for(int j=0;j<lineSplits.length;j++){
                /* For each split, we create node. */
                String value = String.copyValueOf(lineSplits[j].toCharArray());
                Vertex vtx = new Vertex(value,setIdx,j); /* The value of the node is the split. 
                The level is i. The index is j.*/
                // Set the dist index of the given node.
                vtx.setDistIdx(inputGraph.getVertices().size()); // A big big bug, fixed on 31.03.2015
                inputGraph.getVertices().add(vtx); /* Push the node into vertices. */
            }
            setIdx++;
        }
        /* Check if all levels are initialized.*/
        if(setIdx < inputGraph.getSetSizes().length)
            throw new IllegalStateException("(biforce.graphs.XmlInputParser.parseEntity) Not enough node sets given by element \"entity\": "+
                    setIdx+"  length of setSizes:  "+inputGraph.getSetSizes().length);
    }
    
    
    /**
     * This method parses the node content for Bipartite Graph
     * @param entityContent
     * @param inputGraph 
     */
    public void parseEntityString(String entityContent, BipartiteGraph inputGraph){
        
        inputGraph.setVertices(new ArrayList<>());
        if(inputGraph == null)
            throw new IllegalArgumentException("The setsizes array is not initialized in the graph.");
         /* First check if the array setSizes is initialized.*/
        /* Split the content of the entity by lines. */
        String[] contentSplits = entityContent.split("\n");
        /* The length of the contentSplits might be larger than the number of different levels, yet if
        the contentSplits' length is smaller than the number of the levels, then we have to throw an exception.*/
        if(contentSplits.length<2)
            throw new IllegalStateException("(biforce.graphs.XmlInputParser.parseEntity) The levels in the content of element \"entity\" "+
                    "is smaller then the length of setSizes. setSizes: "+2+"  content:  "+contentSplits.length);
        /* Create nodes and push into vertices. */
        int setIdx = 0;
        for(int i=0;i<contentSplits.length;i++){
            /* Get the line. */
            String line = contentSplits[i];
            line = line.trim();
            /* Jump the empty line. */
            if(line.isEmpty())
                continue;
            /* Split the line. */
            String[] lineSplits = line.split("\t");
            if (i ==0)
                inputGraph.setSet0Size(lineSplits.length);
            if(i ==1)
                inputGraph.setSet1Size(lineSplits.length);
            for(int j=0;j<lineSplits.length;j++){
                /* For each split, we create node. */
                String value = String.copyValueOf(lineSplits[j].toCharArray());
                Vertex vtx = new Vertex(value,setIdx,j); /* The value of the node is the split. 
                The level is i. The index is j.*/
                // Set the dist index of the given node.
                vtx.setDistIdx(inputGraph.getVertices().size()); // A big big bug, fixed on 31.03.2015
                inputGraph.getVertices().add(vtx); /* Push the node into vertices. */
            }
            setIdx++;
        }
        /* Check if all levels are initialized.*/
        if(setIdx < 2)
            throw new IllegalStateException("(biforce.graphs.XmlInputParser.parseEntity) Not enough node sets given by element \"entity\": "+
                    setIdx+"  length of setSizes:  "+2);
    }
    
    
    /**
     * This method parses the node content for MatrixGraph
     * @param entityContent
     * @param inputGraph 
     */
    public void parseEntityString(String entityContent, GeneralGraph inputGraph){
         /* Check if setSizes in the inputGraph has been checked.*/
        if(inputGraph == null)
            throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) The setSizes array is not initialized in the graph.");
        String[] splits = entityContent.split("\t");
        for(int i=0;i<splits.length;i++){
            String value = String.copyValueOf(splits[i].toCharArray());
            Vertex vtx = new Vertex(value,0,i);
            inputGraph.getVertices().add(vtx);
        }
        
    }
    
    
    /**
     * This method reads the names of the nodes from the given inupt file.
     * @param inputFile
     * @param inputGraph 
     */
    public void parseEntityFile(String inputFile, HierGraphWIE inputGraph){
        /* Init the FileReader.*/
        FileReader fr = null;
        BufferedReader br = null;
        try{
            fr = new FileReader(inputFile);
            br = new BufferedReader(fr);
        }catch(IOException e){
            System.err.println("(biforce.graphs.XmlInputParser.parseEntityFile) Invalid inputFile:  "+inputFile);
            return;
        }
        /* Check if setSizes in the inputGraph has been checked.*/
        if(inputGraph == null || inputGraph.getSetSizes() == null)
            throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) The setSizes array is not initialized in the graph.");
        String line = null;
        /* Create nodes and push into vertices. */
        int setIdx = 0;
        try{
        while((line = br.readLine())!= null){
            line = line.trim();
            if(line.isEmpty())
                continue;
            /* Split the line. */
            String[] lineSplits = line.split("\t");
            inputGraph.getSetSizes()[setIdx] = lineSplits.length; /* The number of nodes in set i is the length of the splits on line i. */
            for(int j=0;j<lineSplits.length;j++){
                /* For each split, we create node. */
                String value = String.copyValueOf(lineSplits[j].toCharArray());
                Vertex vtx = new Vertex(value,setIdx,j); /* The value of the node is the split. 
                The level is i. The index is j.*/
                // Set the dist index of the given node.
                vtx.setDistIdx(inputGraph.getVertices().size()); // A big big bug, fixed on 31.03.2015
                inputGraph.getVertices().add(vtx); /* Push the node into vertices. */
            }
            setIdx++;
        }
        }catch(IOException e){
            System.err.println("(biforce.graphs.XmlInputParser.parseEntity) readLine() error.");
            return;
        }
        /* Check if all levels are initialized.*/
        if(setIdx < inputGraph.getSetSizes().length)
            throw new IllegalStateException("(biforce.graphs.XmlInputParser.parseEntity) Not enough node sets given by element \"entity\": "+
                    setIdx+"  length of setSizes:  "+inputGraph.getSetSizes().length);
    }
   
    /**
     * This method reads the names of the entities from the input file for a MatrixHierNpartiteGraph.
     * @param inputFile
     * @param inputGraph 
     */
    public void parseEntityFile(String inputFile, HierGraph inputGraph){
        /* Init the FileReader.*/
        FileReader fr = null;
        BufferedReader br = null;
        try{
            fr = new FileReader(inputFile);
            br = new BufferedReader(fr);
        }catch(IOException e){
            System.err.println("(biforce.graphs.XmlInputParser.parseEntityFile) Invalid inputFile:  "+inputFile);
            return;
        }
        /* Check if setSizes in the inputGraph has been checked.*/
        if(inputGraph == null || inputGraph.getSetSizes() == null)
            throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) The setSizes array is not initialized in the graph.");
        String line = null;
        /* Create nodes and push into vertices. */
        int setIdx = 0;
        try{
        while((line = br.readLine())!= null){
            line = line.trim();
            if(line.isEmpty())
                continue;
            /* Split the line. */
            String[] lineSplits = line.split("\t");
            inputGraph.getSetSizes()[setIdx] = lineSplits.length; /* The number of nodes in set i is the length of the splits on line i. */
            for(int j=0;j<lineSplits.length;j++){
                /* For each split, we create node. */
                String value = String.copyValueOf(lineSplits[j].toCharArray());
                Vertex vtx = new Vertex(value,setIdx,j); /* The value of the node is the split. 
                The level is i. The index is j.*/
                // Set the dist index of the given node.
                vtx.setDistIdx(inputGraph.getVertices().size()); // A big big bug, fixed on 31.03.2015
                inputGraph.getVertices().add(vtx); /* Push the node into vertices. */
            }
            setIdx++;
        }
        }catch(IOException e){
            System.err.println("(biforce.graphs.XmlInputParser.parseEntity) readLine() error.");
            return;
        }
        /* Check if all levels are initialized.*/
        if(setIdx < inputGraph.getSetSizes().length)
            throw new IllegalStateException("(biforce.graphs.XmlInputParser.parseEntity) Not enough node sets given by element \"entity\": "+
                    setIdx+"  length of setSizes:  "+inputGraph.getSetSizes().length);
    }
    
    /**
     * This method parses the entity file from the given input file for a MatrixGeneralNpartiteGraph
     * @param inputFile
     * @param inputGraph 
     */
    public void parseEntityFile(String inputFile, NpartiteGraph inputGraph){
        /* Init the FileReader.*/
        FileReader fr = null;
        BufferedReader br = null;
        try{
            fr = new FileReader(inputFile);
            br = new BufferedReader(fr);
        }catch(IOException e){
            System.err.println("(biforce.graphs.XmlInputParser.parseEntityFile) Invalid inputFile:  "+inputFile);
            return;
        }
        /* Check if setSizes in the inputGraph has been checked.*/
        if(inputGraph == null || inputGraph.getSetSizes() == null)
            throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) The setSizes array is not initialized in the graph.");
        String line = null;
        /* Create nodes and push into vertices. */
        int setIdx = 0;
        try{
        while((line = br.readLine())!= null){
            line = line.trim();
            if(line.isEmpty())
                continue;
            /* Split the line. */
            String[] lineSplits = line.split("\t");
            inputGraph.getSetSizes()[setIdx] = lineSplits.length; /* The number of nodes in set i is the length of the splits on line i. */
            for(int j=0;j<lineSplits.length;j++){
                /* For each split, we create node. */
                String value = String.copyValueOf(lineSplits[j].toCharArray());
                Vertex vtx = new Vertex(value,setIdx,j); /* The value of the node is the split. 
                The level is i. The index is j.*/
                // Set the dist index of the given node.
                vtx.setDistIdx(inputGraph.getVertices().size()); // A big big bug, fixed on 31.03.2015
                inputGraph.getVertices().add(vtx); /* Push the node into vertices. */
            }
            setIdx++;
        }
        }catch(IOException e){
            System.err.println("(biforce.graphs.XmlInputParser.parseEntity) readLine() error.");
            return;
        }
        /* Check if all levels are initialized.*/
        if(setIdx < inputGraph.getSetSizes().length)
            throw new IllegalStateException("(biforce.graphs.XmlInputParser.parseEntity) Not enough node sets given by element \"entity\": "+
                    setIdx+"  length of setSizes:  "+inputGraph.getSetSizes().length);
    }
    
    /**
     * This method parses the entity information from a given file for bipartite graph.
     * @param inputFile
     * @param inputGraph 
     */
    public final void parseEntityFile(String inputFile, BipartiteGraph inputGraph){
        /* Init the FileReader.*/
        FileReader fr = null;
        BufferedReader br = null;
        try{
            fr = new FileReader(inputFile);
            br = new BufferedReader(fr);
        }catch(IOException e){
            System.err.println("(biforce.graphs.XmlInputParser.parseEntityFile) Invalid inputFile:  "+inputFile);
            return;
        }
        /* Check if setSizes in the inputGraph has been checked.*/
        if(inputGraph == null)
            throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) The setSizes array is not initialized in the graph.");
        String line = null;
        /* Create nodes and push into vertices. */
        int setIdx = 0;
        try{
        int i =0;
        while((line = br.readLine())!= null){
            line = line.trim();
            if(line.isEmpty())
                continue;
            /* Split the line. */
            String[] lineSplits = line.split("\t");
            if(i ==0)
                inputGraph.setSet0Size(lineSplits.length);/* The number of nodes in set i is the length of the splits on line i. */
            if(i == 1)
                inputGraph.setSet1Size(lineSplits.length);
            for(int j=0;j<lineSplits.length;j++){
                /* For each split, we create node. */
                String value = String.copyValueOf(lineSplits[j].toCharArray());
                Vertex vtx = new Vertex(value,setIdx,j); /* The value of the node is the split. 
                The level is i. The index is j.*/
                // Set the dist index of the given node.
                vtx.setDistIdx(inputGraph.getVertices().size()); // A big big bug, fixed on 31.03.2015
                inputGraph.getVertices().add(vtx); /* Push the node into vertices. */
            }
            setIdx++;
            i++;
        }
        }catch(IOException e){
            System.err.println("(biforce.graphs.XmlInputParser.parseEntity) readLine() error.");
            return;
        }
        /* Check if all levels are initialized.*/
        if(setIdx < 2)
            throw new IllegalStateException("(biforce.graphs.XmlInputParser.parseEntity) Not enough node sets given by element \"entity\": "+
                    setIdx+"  length of setSizes:  "+2);
    }
    
    
    /**
     * This method parses the entity in a given file for MatrixGraph
     * @param inputFile
     * @param inputGraph 
     */
    public final void parseEntityFile(String inputFile, GeneralGraph inputGraph){
        /* Init the FileReader.*/
        FileReader fr = null;
        BufferedReader br = null;
        try{
            fr = new FileReader(inputFile);
            br = new BufferedReader(fr);
        }catch(FileNotFoundException e){
            System.err.println("(biforce.graphs.XmlInputParser.parseEntityFile) Invalid inputFile:  "+inputFile);
            return;
        }
        String line;
        try{
        int idx=0;
        while((line = br.readLine())!= null){
            Vertex vtx = new Vertex(line.trim(), 0,idx);
            idx++;
            inputGraph.getVertices().add(vtx);
        }
        }catch(IOException e){
            System.err.println("(XmlInputParser.parseEntityFile) Reading error:  "+inputFile);
            return;
        }
    }
    /**
     * This parser parses the matrix string for inter-matrix for MatrixHierGeneralGraph.
     * @param matrixContent
     * @param matrixLevel1
     * @param matrixLevel2 
     * @param inputGraph 
     */
    public void parseInterMatrixString(String matrixContent, int matrixLevel1, int matrixLevel2,
            HierGraphWIE inputGraph){
        /* Get the inter matrix in the interEdgeWeights. The index of interMatrix is the min of matrixLevel1 and matrixLevel2. */
        float[][] interMatrix = inputGraph.getInterEdgeWeights().get(Math.min(matrixLevel1, matrixLevel2));
        /* Split the matrixContent. */
        String[] rowsStr = matrixContent.split("\n");
        for(int i=0;i<rowsStr.length;i++){
            String row = rowsStr[i];
            row = row.trim();
            /* Split the row into columns. */
            String[] cols = row.split("\\s+");
            /* Here we check the size of the matrix, see if it matches setSizes. */
            if(rowsStr.length != inputGraph.getSetSizes()[Math.min(matrixLevel1, matrixLevel2)] || 
                    cols.length != inputGraph.getSetSizes()[Math.max(matrixLevel1, matrixLevel2)])
                throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) The size of the inter-matrix does not match setSizes. Index of the set: "
                        +matrixLevel1+","+matrixLevel2
                        +" setSize of the row:  "+inputGraph.getSetSizes()[Math.min(matrixLevel1, matrixLevel2)]+", row:"+rowsStr.length+
                        ",setSize of the column:  "+inputGraph.getSetSizes()[Math.max(matrixLevel1, matrixLevel2)]+" cols:"+cols.length);
            for(int j=0;j<cols.length;j++){
                /* First check the number format in the matrix. */
                float value = Float.NaN;
                try{
                    value = Float.parseFloat(String.copyValueOf(cols[j].toCharArray()));
                }catch(NumberFormatException e){
                    throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) Number format error:  "+cols[j]);
                }
                /* In the inter matrix, value cannot be NaN. */
                if(Double.isNaN(value))
                    throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) NaN in the xml for inter matrix. ");
                 /* Check the if the value in the interMatrix is duplicated initialized. */
                if(!Double.isNaN(interMatrix[i][j]))
                    throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) Duplicated initialization,"+
                                " value in the matrix:  "+interMatrix[i][j]+"  value in the xml file:  "+value);
                interMatrix[i][j] = value;
            }
        }
        /* Re-set the inter-matrix. */
        inputGraph.getInterEdgeWeights().set(Math.min(matrixLevel1,matrixLevel2), interMatrix);
    }
    
    /**
     * This parser parses the matrix string for inter-matrix for MatrixHierNpartiteGraph.
     * @param matrixContent
     * @param matrixLevel1
     * @param matrixLevel2
     * @param inputGraph 
     */
    public void parseInterMatrixString(String matrixContent, int matrixLevel1, int matrixLevel2, 
            HierGraph inputGraph){
        float[][] interMatrix = inputGraph.getEdgeWeights().get(Math.min(matrixLevel1, matrixLevel2));
        String[] rowsStr = matrixContent.split("\n");
        for(int i=0;i<rowsStr.length;i++){
            String row = rowsStr[i];
            row = row.trim();
            /* Split the row into columns. */
            String[] cols = row.split("\\s+");
            /* Here we check the size of the matrix, see if it matches setSizes. */
            if(rowsStr.length != inputGraph.getSetSizes()[Math.min(matrixLevel1, matrixLevel2)] || 
                    cols.length != inputGraph.getSetSizes()[Math.max(matrixLevel1, matrixLevel2)])
                throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) The size of the inter-matrix does not match setSizes. Index of the set: "
                        +matrixLevel1+","+matrixLevel2
                        +" setSize of the row:  "+inputGraph.getSetSizes()[Math.min(matrixLevel1, matrixLevel2)]+", row:"+rowsStr.length+
                        ",setSize of the column:  "+inputGraph.getSetSizes()[Math.max(matrixLevel1, matrixLevel2)]+" cols:"+cols.length);
            for(int j=0;j<cols.length;j++){
                /* First check the number format in the matrix. */
                float value = Float.NaN;
                try{
                    value = Float.parseFloat(String.copyValueOf(cols[j].toCharArray()));
                }catch(NumberFormatException e){
                    throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) Number format error:  "+cols[j]);
                }
                /* In the inter matrix, value cannot be NaN. */
                if(Double.isNaN(value))
                    throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) NaN in the xml for inter matrix. ");
                 /* Check the if the value in the interMatrix is duplicated initialized. */
                if(!Double.isNaN(interMatrix[i][j]))
                    throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) Duplicated initialization,"+
                                " value in the matrix:  "+interMatrix[i][j]+"  value in the xml file:  "+value);
                interMatrix[i][j] = value;
            }
        }
        /* Re-set the inter-matrix. */
        inputGraph.getEdgeWeights().set(Math.min(matrixLevel1,matrixLevel2), interMatrix);
    }
    
    /**
     * This parser parses the inter matrix string for a MatrixGeneralNpartiteGraph.
     * @param matrixContent
     * @param matrixLevel1
     * @param matrixLevel2
     * @param inputGraph 
     */
    public void parseInterMatrixString(String matrixContent, int matrixLevel1, int matrixLevel2,
            NpartiteGraph inputGraph){
        float[][] interMatrix;
        if(matrixLevel1 - matrixLevel2 == 1 || matrixLevel1 - matrixLevel2 == -1)
            interMatrix= inputGraph.getInterEdgeWeights().get(Math.min(matrixLevel1, matrixLevel2));
        else if(matrixLevel1 - matrixLevel2 == inputGraph.getSetSizes().length-1 ||
                matrixLevel1 - matrixLevel2 == -inputGraph.getSetSizes().length+1)
            interMatrix = inputGraph.getInterEdgeWeights().get(inputGraph.getSetSizes().length-1);
        else throw new IllegalArgumentException("(XmlInputParser.parseInterMatrixString) Illegal matrix levels:  "+matrixLevel1+"  "+matrixLevel2);
        
        String[] rowsStr = matrixContent.split("\n");
        for(int i=0;i<rowsStr.length;i++){
            String row = rowsStr[i];
            row = row.trim();
            /* Split the row into columns. */
            String[] cols = row.split("\\s+");
            /* Here we check the size of the matrix, see if it matches setSizes. */
            if(rowsStr.length != inputGraph.getSetSizes()[Math.min(matrixLevel1, matrixLevel2)] || 
                    cols.length != inputGraph.getSetSizes()[Math.max(matrixLevel1, matrixLevel2)])
                throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) The size of the inter-matrix does not match setSizes. Index of the set: "
                        +matrixLevel1+","+matrixLevel2
                        +" setSize of the row:  "+inputGraph.setSizes[Math.min(matrixLevel1, matrixLevel2)]+", row:"+rowsStr.length+
                        ",setSize of the column:  "+inputGraph.setSizes[Math.max(matrixLevel1, matrixLevel2)]+" cols:"+cols.length);
            for(int j=0;j<cols.length;j++){
                /* First check the number format in the matrix. */
                float value = Float.NaN;
                try{
                    value = Float.parseFloat(String.copyValueOf(cols[j].toCharArray()));
                }catch(NumberFormatException e){
                    throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) Number format error:  "+cols[j]);
                }
                /* In the inter matrix, value cannot be NaN. */
                if(Double.isNaN(value))
                    throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) NaN in the xml for inter matrix. ");
                 /* Check the if the value in the interMatrix is duplicated initialized. */
                if(!Double.isNaN(interMatrix[i][j]))
                    throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) Duplicated initialization,"+
                                " value in the matrix:  "+interMatrix[i][j]+"  value in the xml file:  "+value);
                interMatrix[i][j] = value;
            }
        }
        /* Re-set the inter-matrix. */
        if(matrixLevel1 - matrixLevel2 == 1 || matrixLevel1 - matrixLevel2 == -1)
            inputGraph.interEdgeWeights.set(Math.min(matrixLevel1, matrixLevel2), interMatrix);
        else if(matrixLevel1 - matrixLevel2 == inputGraph.setSizes.length-1 ||
                matrixLevel1 - matrixLevel2 == -inputGraph.setSizes.length+1)
            inputGraph.interEdgeWeights.set(inputGraph.setSizes.length-1, interMatrix);
    }
    
    /**
     * This method parses the matrix string for a bipartite graph.
     * @param matrixContent
     * @param inputGraph 
     */
    public void parseInterMatrixString(String matrixContent, BipartiteGraph inputGraph){
        float[][] interMatrix = inputGraph.edgeWeights;
        String[] rowsStr = matrixContent.split("\n");
        for(int i=0;i<rowsStr.length;i++){
            String row = rowsStr[i];
            row = row.trim();
            /* Split the row into columns. */
            String[] cols = row.split("\\s+");
            /* Here we check the size of the matrix, see if it matches setSizes. */
            if(rowsStr.length != inputGraph.set0Size || 
                    cols.length != inputGraph.set1Size)
                throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) The size of the inter-matrix does not match setSizes. Index of the set: "
                        +0+","+1
                        +" setSize of the row:  "+0+", row:"+rowsStr.length+
                        ",setSize of the column:  "+1+" cols:"+cols.length);
            for(int j=0;j<cols.length;j++){
                /* First check the number format in the matrix. */
                float value = Float.NaN;
                try{
                    value = Float.parseFloat(String.copyValueOf(cols[j].toCharArray()));
                }catch(NumberFormatException e){
                    throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) Number format error:  "+cols[j]);
                }
                /* In the inter matrix, value cannot be NaN. */
                if(Double.isNaN(value))
                    throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) NaN in the xml for inter matrix. ");
                 /* Check the if the value in the interMatrix is duplicated initialized. */
                if(!Double.isNaN(interMatrix[i][j]))
                    throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) Duplicated initialization,"+
                                " value in the matrix:  "+interMatrix[i][j]+"  value in the xml file:  "+value);
                interMatrix[i][j] = value;
            }
        }
    }
    
   /**
    * This parser parses the inter-matrix file between matrixLevel1 and matrixLevel2.
    * @param matrixFile
    * @param matrixLevel1
    * @param matrixLevel2
    * @param inputGraph 
    */
    public void parseInterMatrixFile(String matrixFile, int matrixLevel1,int matrixLevel2, 
            HierGraphWIE inputGraph){
        FileReader fr = null;
        BufferedReader br = null;
        try{
            fr = new FileReader(matrixFile);
            br = new BufferedReader(fr);
        }catch(IOException e){
            System.err.println("(biforce.graphs.XmlInputParser.parseInterMatrixFile) Invalid inputFile:  "+matrixFile);
            return;
        }
        /* Get the inter matrix in the interEdgeWeights. The index of interMatrix is the min of matrixLevel1 and matrixLevel2. */
        float[][] interMatrix = inputGraph.interEdgeWeights.get(Math.min(matrixLevel1, matrixLevel2));
        int i =0; /* Create the row index.*/
        try{
        String row = null;
        while((row = br.readLine())!=null){
            if(row.trim().isEmpty())
                continue;
            /* Split the row into columns. */
            String[] cols = row.split("\\s+");
            
            for(int j=0;j<cols.length;j++){
                /* First check the number format in the matrix. */
                float value = Float.NaN;
                try{
                    value = Float.parseFloat(String.copyValueOf(cols[j].toCharArray()));
                }catch(NumberFormatException e){
                    throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) Number format error:  "+cols[j]);
                }
                /* In the inter matrix, value cannot be NaN. */
                if(Double.isNaN(value))
                    throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) NaN in the xml for inter matrix. ");
                 /* Check the if the value in the interMatrix is duplicated initialized. */
                if(!Double.isNaN(interMatrix[i][j]))
                    throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) Duplicated initialization,"+
                                " value in the matrix:  "+interMatrix[i][j]+"  value in the xml file:  "+value);
                interMatrix[i][j] = value;
            }
            i++;
        }
        }catch(IOException e){
            System.err.println("(biforce.graphs.XmlInputParser.parseEntity) readLine() error.");
            return;
        }
        /* Here we check the size of the matrix, see if it matches setSizes. */
        if(i!= inputGraph.setSizes[Math.min(matrixLevel1, matrixLevel2)] || 
                interMatrix[0].length != inputGraph.setSizes[Math.max(matrixLevel1, matrixLevel2)])
            throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) The size of the inter-matrix does not match setSizes. Index of the set: "
                    +matrixLevel1+","+matrixLevel2
                    +" setSize of the row:  "+inputGraph.setSizes[Math.min(matrixLevel1, matrixLevel2)]+", row:"+i+
                    ",setSize of the column:  "+inputGraph.setSizes[Math.max(matrixLevel1, matrixLevel2)]+" cols:"+interMatrix[0].length);
        /* Re-set the inter-matrix. */
        inputGraph.interEdgeWeights.set(Math.min(matrixLevel1,matrixLevel2), interMatrix);
        
    }
  
    
    /**
     * This parser parses the inter-matrix file for MatrixHierNpartiteGraph.
     * @param matrixFile
     * @param matrixLevel1
     * @param matrixLevel2
     * @param inputGraph 
     */
    public void parseInterMatrixFile(String matrixFile, int matrixLevel1, int matrixLevel2,
            HierGraph inputGraph){
        FileReader fr = null;
        BufferedReader br = null;
        try{
            fr = new FileReader(matrixFile);
            br = new BufferedReader(fr);
        }catch(IOException e){
            System.err.println("(biforce.graphs.XmlInputParser.parseInterMatrixFile) Invalid inputFile:  "+matrixFile);
            return;
        }
        /* Get the inter matrix in the interEdgeWeights. The index of interMatrix is the min of matrixLevel1 and matrixLevel2. */
        float[][] interMatrix = inputGraph.edgeWeights.get(Math.min(matrixLevel1, matrixLevel2));
        int i =0; /* Create the row index.*/
        try{
        String row = null;
        while((row = br.readLine())!=null){
            if(row.trim().isEmpty())
                continue;
            /* Split the row into columns. */
            String[] cols = row.split("\\s+");
            
            for(int j=0;j<cols.length;j++){
                /* First check the number format in the matrix. */
                float value = Float.NaN;
                try{
                    value = Float.parseFloat(String.copyValueOf(cols[j].toCharArray()));
                }catch(NumberFormatException e){
                    throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) Number format error:  "+cols[j]);
                }
                /* In the inter matrix, value cannot be NaN. */
                if(Double.isNaN(value))
                    throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) NaN in the xml for inter matrix. ");
                 /* Check the if the value in the interMatrix is duplicated initialized. */
                if(!Double.isNaN(interMatrix[i][j]))
                    throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) Duplicated initialization,"+
                                " value in the matrix:  "+interMatrix[i][j]+"  value in the xml file:  "+value);
                interMatrix[i][j] = value;
            }
            i++;
        }
        }catch(IOException e){
            System.err.println("(biforce.graphs.XmlInputParser.parseEntity) readLine() error.");
            return;
        }
        /* Here we check the size of the matrix, see if it matches setSizes. */
        if(i!= inputGraph.setSizes[Math.min(matrixLevel1, matrixLevel2)] || 
                interMatrix[0].length != inputGraph.setSizes[Math.max(matrixLevel1, matrixLevel2)])
            throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) The size of the inter-matrix does not match setSizes. Index of the set: "
                    +matrixLevel1+","+matrixLevel2
                    +" setSize of the row:  "+inputGraph.setSizes[Math.min(matrixLevel1, matrixLevel2)]+", row:"+i+
                    ",setSize of the column:  "+inputGraph.setSizes[Math.max(matrixLevel1, matrixLevel2)]+" cols:"+interMatrix[0].length);
        /* Re-set the inter-matrix. */
        inputGraph.edgeWeights.set(Math.min(matrixLevel1,matrixLevel2), interMatrix);
        
    }
    
    /**
     * This parse parses the inter-matrix from the given file for MatrixGeneralNpartiteGraph
     * @param matrixFile
     * @param matrixLevel1
     * @param matrixLevel2
     * @param inputGraph 
     */
    public void parseInterMatrixFile(String matrixFile, int matrixLevel1, int matrixLevel2,
            NpartiteGraph inputGraph){
        FileReader fr = null;
        BufferedReader br = null;
        try{
            fr = new FileReader(matrixFile);
            br = new BufferedReader(fr);
        }catch(IOException e){
            System.err.println("(biforce.graphs.XmlInputParser.parseInterMatrixFile) Invalid inputFile:  "+matrixFile);
            return;
        }
        /* Get the inter matrix in the interEdgeWeights. The index of interMatrix is the min of matrixLevel1 and matrixLevel2. */
        float[][] interMatrix = null;
        if(matrixLevel1 - matrixLevel2 == 1 || matrixLevel1 - matrixLevel2 == -1)
            interMatrix= inputGraph.interEdgeWeights.get(Math.min(matrixLevel1, matrixLevel2));
        else if(matrixLevel1 - matrixLevel2 == inputGraph.setSizes.length-1 ||
                matrixLevel1 - matrixLevel2 == -inputGraph.setSizes.length+1)
            interMatrix = inputGraph.interEdgeWeights.get(inputGraph.setSizes.length-1);
        else throw new IllegalArgumentException("(XmlInputParser.parseInterMatrixFile) Illegal matrix levels:  "+matrixLevel1+"  "+matrixLevel2);
        
        int i =0; /* Create the row index.*/
        try{
        String row = null;
        while((row = br.readLine())!=null){
            if(row.trim().isEmpty())
                continue;
            /* Split the row into columns. */
            String[] cols = row.split("\\s+");
            
            for(int j=0;j<cols.length;j++){
                /* First check the number format in the matrix. */
                float value = Float.NaN;
                try{
                    value = Float.parseFloat(String.copyValueOf(cols[j].toCharArray()));
                }catch(NumberFormatException e){
                    throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) Number format error:  "+cols[j]);
                }
                /* In the inter matrix, value cannot be NaN. */
                //if(Float.isNaN(value))
                  //  throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) NaN in the xml for inter matrix. ");
                 /* Check the if the value in the interMatrix is duplicated initialized. */
                if(!Float.isNaN(interMatrix[i][j]))
                    throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) Duplicated initialization,"+
                                " value in the matrix:  "+interMatrix[i][j]+"  value in the xml file:  "+value);
                interMatrix[i][j] = value;
            }
            i++;
        }
        }catch(IOException e){
            System.err.println("(biforce.graphs.XmlInputParser.parseEntity) readLine() error.");
            return;
        }
        /* Here we check the size of the matrix, see if it matches setSizes. */
        if(i!= inputGraph.setSizes[Math.min(matrixLevel1, matrixLevel2)] || 
                interMatrix[0].length != inputGraph.setSizes[Math.max(matrixLevel1, matrixLevel2)])
            throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) The size of the inter-matrix does not match setSizes. Index of the set: "
                    +matrixLevel1+","+matrixLevel2
                    +" setSize of the row:  "+inputGraph.setSizes[Math.min(matrixLevel1, matrixLevel2)]+", row:"+i+
                    ",setSize of the column:  "+inputGraph.setSizes[Math.max(matrixLevel1, matrixLevel2)]+" cols:"+interMatrix[0].length);
        /* Re-set the inter-matrix. */
        if(matrixLevel1 - matrixLevel2 == 1 || matrixLevel1 - matrixLevel2 == -1)
            inputGraph.interEdgeWeights.set(Math.min(matrixLevel1, matrixLevel2), interMatrix);
        else if(matrixLevel1 - matrixLevel2 == inputGraph.setSizes.length-1 ||
                matrixLevel1 - matrixLevel2 == -inputGraph.setSizes.length+1)
            inputGraph.interEdgeWeights.set(inputGraph.setSizes.length-1, interMatrix);
    }
    
    /**
     * This method parses the inter-matrix from the given file.
     * @param matrixFile
     * @param inputGraph 
     */
    public void parseInterMatrixFile(String matrixFile, BipartiteGraph inputGraph){
        FileReader fr = null;
        BufferedReader br = null;
        try{
            fr = new FileReader(matrixFile);
            br = new BufferedReader(fr);
        }catch(IOException e){
            System.err.println("(biforce.graphs.XmlInputParser.parseInterMatrixFile) Invalid inputFile:  "+matrixFile);
            return;
        }
        /* Get the inter matrix in the interEdgeWeights. The index of interMatrix is the min of matrixLevel1 and matrixLevel2. */
        float[][] interMatrix = inputGraph.edgeWeights;
       
        int i =0; /* Create the row index.*/
        try{
        String row = null;
        while((row = br.readLine())!=null){
            if(row.trim().isEmpty())
                continue;
            /* Split the row into columns. */
            String[] cols = row.split("\\s+");
            
            for(int j=0;j<cols.length;j++){
                /* First check the number format in the matrix. */
                float value = Float.NaN;
                try{
                    value = Float.parseFloat(String.copyValueOf(cols[j].toCharArray()));
                }catch(NumberFormatException e){
                    throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) Number format error:  "+cols[j]);
                }
                /* In the inter matrix, value cannot be NaN. */
                //if(Float.isNaN(value))
                  //  throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) NaN in the xml for inter matrix. ");
                 /* Check the if the value in the interMatrix is duplicated initialized. */
                if(!Float.isNaN(interMatrix[i][j]))
                    throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) Duplicated initialization,"+
                                " value in the matrix:  "+interMatrix[i][j]+"  value in the xml file:  "+value);
                interMatrix[i][j] = value;
            }
            i++;
        }
        }catch(IOException e){
            System.err.println("(biforce.graphs.XmlInputParser.parseEntity) readLine() error.");
            return;
        }
        
    }
    
    
    /**
     * This parser parses the inter-matrix file between matrixLevel1 and matrixLevel2 for MatrixHierNpartiteGraph.
     * @param matrixFile
     * @param matrixLevel1
     * @param matrixLevel2
     * @param inputGraph 
     */
    public void parseIntraMatrixFile(String matrixFile, int matrixLevel1, int matrixLevel2, 
            HierGraph inputGraph){
        FileReader fr = null;
        BufferedReader br = null;
        try{
            fr = new FileReader(matrixFile);
            br = new BufferedReader(fr);
        }catch(IOException e){
            System.err.println("(biforce.graphs.XmlInputParser.parseIntraMatrixFile) Invalid inputFile:  "+matrixFile);
            return;
        }
        /* Get the inter matrix in the interEdgeWeights. The index of interMatrix is the min of matrixLevel1 and matrixLevel2. */
        float[][] interMatrix = inputGraph.edgeWeights.get(Math.min(matrixLevel1, matrixLevel2));
        int i =0; /* Create the row index.*/
        try{
        String row = null;
        while((row = br.readLine())!=null){
            if(row.trim().isEmpty())
                continue;
            /* Split the row into columns. */
            String[] cols = row.split("\\s+");
            
            for(int j=0;j<cols.length;j++){
                /* First check the number format in the matrix. */
                float value = Float.NaN;
                try{
                    value = Float.parseFloat(String.copyValueOf(cols[j].toCharArray()));
                }catch(NumberFormatException e){
                    throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) Number format error:  "+cols[j]);
                }
                /* In the inter matrix, value cannot be NaN. */
                if(Double.isNaN(value))
                    throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) NaN in the xml for inter matrix. ");
                 /* Check the if the value in the interMatrix is duplicated initialized. */
                if(!Double.isNaN(interMatrix[i][j]))
                    throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) Duplicated initialization,"+
                                " value in the matrix:  "+interMatrix[i][j]+"  value in the xml file:  "+value);
                interMatrix[i][j] = value;
            }
            i++;
        }
        }catch(IOException e){
            System.err.println("(biforce.graphs.XmlInputParser.parseEntity) readLine() error.");
            return;
        }
        /* Here we check the size of the matrix, see if it matches setSizes. */
        if(i!= inputGraph.setSizes[Math.min(matrixLevel1, matrixLevel2)] || 
                interMatrix[0].length != inputGraph.setSizes[Math.max(matrixLevel1, matrixLevel2)])
            throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) The size of the inter-matrix does not match setSizes. Index of the set: "
                    +matrixLevel1+","+matrixLevel2
                    +" setSize of the row:  "+inputGraph.setSizes[Math.min(matrixLevel1, matrixLevel2)]+", row:"+i+
                    ",setSize of the column:  "+inputGraph.setSizes[Math.max(matrixLevel1, matrixLevel2)]+" cols:"+interMatrix[0].length);
        /* Re-set the inter-matrix. */
        inputGraph.edgeWeights.set(Math.min(matrixLevel1,matrixLevel2), interMatrix);
    }
    
    /**
     * This method extracts an intra-matrix string for MatrixHierGeneralGraph.
     * @param matrixContent
     * @param matrixLevel 
     * @param inputGraph 
     */
    public void parseIntraMatrixString(String matrixContent, int matrixLevel,
            HierGraphWIE inputGraph){
        float[][] intraMatrix = inputGraph.intraEdgeWeights.get(matrixLevel);
        /* Split the matrixContent. */
        String[] rowsStr = matrixContent.split("\n");
        for(int i=0;i<rowsStr.length;i++){
            String row = rowsStr[i];
            /* Split the row into columns.*/
            String[] cols = row.split("\\s+");
            /* Here we check the size of the matrix, see if it matches setSizes. */
            if(rowsStr.length != inputGraph.setSizes[matrixLevel] || 
                    cols.length != inputGraph.setSizes[matrixLevel])
                throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) The size of the intra-matrix does not match setSizes, setSize:  "+
                        inputGraph.setSizes[matrixLevel]+", row:"+rowsStr.length+", cols:"+cols.length);
            for(int j=0;j<cols.length;j++){
                /* If this is a self-edge, then we just check if the value in the matrix is also NaN. */
                if(i==j && !Double.isNaN(intraMatrix[i][j]) )
                    throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) Self-edge error, matrixLevel: "+
                            matrixLevel+" ,row/col is:  "+i);
                else if(i==j)
                    continue;
                else{/* If i!= j.*/
                    /* First check the number format of the value in the matrix. */
                    float value = Float.NaN;
                    try{
                        value = Float.parseFloat(String.copyValueOf(cols[j].toCharArray()));
                    }catch(NumberFormatException e){
                        e.printStackTrace();
                        throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) Number format error:  "+cols[j]);
                    }
                    /* Here the value cannot be NaN, since it's not a self-edge.*/
                    if(Double.isNaN(value))
                        throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) Non-self edge weight cannot be NaN:  "+value+"  "+i+","+j);
                    /* Check if the value in the matrix is already initialized. */
                    if(!Double.isNaN(intraMatrix[i][j]))
                        throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) Duplicated initialization,"+
                                " value in the matrix:  "+intraMatrix[i][j]+"  value in the xml file:  "+value);
                    /* We then assign the value in the intraMatrix. */
                    intraMatrix[i][j] = value;
                    /* Here we have to check if the intraMatrix is symmetric. */
                    if(i>j && intraMatrix[i][j] != intraMatrix[j][i])
                        throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) Intra-matrix is not symmetric: "+
                                i+","+j+":  "+intraMatrix[i][j]+";  "+i+","+j+":  "+intraMatrix[j][i]);
                }
            }
        }
        inputGraph.intraEdgeWeights.set(matrixLevel, intraMatrix);
    }
    
    /**
     * This parser parses intra-matrix with string for MatrixGeneralNpartiteGraph.
     * @param matrixContent
     * @param matrixLevel
     * @param inputGraph 
     */
    public void parseIntraMatrixString(String matrixContent, int matrixLevel,
            NpartiteGraph inputGraph){
        
    }
    /**
     * 
     * This method extracts an intra matrix file for MatrixHierGeneralGraph.
     * @param matrixFile
     * @param matrixLevel
     * @param inputGraph
     */
    public void parseIntraMatrixFile(String matrixFile, int matrixLevel,
            HierGraphWIE inputGraph){
        /* Create FileReader and BufferedReader. */
        FileReader fr = null;
        BufferedReader br = null;
        try{
            fr = new FileReader(matrixFile);
            br = new BufferedReader(fr);
        }catch(IOException e){
            System.err.println("(biforce.graphs.XmlInputParser.parseIntraMatrixFile) Invalid inputFile:  "+matrixFile);
            return;
        }
        /* Read the matrix in the file.*/
        float[][] intraMatrix = inputGraph.intraEdgeWeights.get(matrixLevel);
        /* Split the matrixContent. */
        String row = null;
        int i = 0; /* This is the row index.*/
        try{
        while((row = br.readLine())!= null){
            row = row.trim();
            if(row.isEmpty())
                continue;
            /* Split the row into columns.*/
            String[] cols = row.split("\\s+");
            
            for(int j=0;j<cols.length;j++){
                /* If this is a self-edge, then we just check if the value in the matrix is also NaN. */
                if(i==j && !Double.isNaN(intraMatrix[i][j]) )
                    throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) Self-edge error, matrixLevel: "+
                            matrixLevel+" ,row/col is:  "+i);
                else if(i==j) {} 
                else{/* If i!= j.*/
                    /* First check the number format of the value in the matrix. */
                    float value = Float.NaN;
                    try{
                        value = Float.parseFloat(String.copyValueOf(cols[j].toCharArray()));
                    }catch(NumberFormatException e){
                        e.printStackTrace();
                        throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) Number format error:  "+cols[j]);
                    }
                    /* Here the value cannot be NaN, since it's not a self-edge. 
                    Rule changed, because pathway-pathway sims are all NaNs. 2015.03.08*/
                    //if(Double.isNaN(value))
                      //  throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) Non-self edge weight cannot be NaN:  "+matrixFile+"\n"+value+"  "+i+","+j);
                    /* Check if the value in the matrix is already initialized. */
                    if(!Double.isNaN(intraMatrix[i][j]))
                        throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) Duplicated initialization,"+
                                " value in the matrix:  "+intraMatrix[i][j]+"  value in the xml file:  "+value);
                    /* We then assign the value in the intraMatrix. */
                    intraMatrix[i][j] = value;
                    /* Here we have to check if the intraMatrix is symmetric. */
                    /* This if is added because pathway-pathway sims are all NaNs. 2015.03.08 */
                    if(i >j && Double.isNaN(intraMatrix[i][j]) && Double.isNaN(intraMatrix[j][i]))
                        continue;
                    if(i>j && intraMatrix[i][j] != intraMatrix[j][i])
                        throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) Intra-matrix is not symmetric: "+matrixFile+"\n"+
                                i+","+j+":  "+intraMatrix[i][j]+";  "+i+","+j+":  "+intraMatrix[j][i]);
                }
            }
            i++;
        }
        }catch(IOException e){
            System.err.println("(biforce.graphs.XmlInputParser.parseEntity) readLine() error.");
            return;
        }
        /* Here we check the size of the matrix, see if it matches setSizes. */
        if(i != inputGraph.setSizes[matrixLevel] || 
                intraMatrix[0].length != inputGraph.setSizes[matrixLevel])
            throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) The size of the intra-matrix does not match setSizes, setSize:  "+
                    inputGraph.setSizes[matrixLevel]+", row:"+i+", cols:"+intraMatrix[0].length);
        inputGraph.intraEdgeWeights.set(matrixLevel, intraMatrix);
    }
    
    /**
     * This parser parses the given intra matrix file for MatrixGeneralNpartiteGraph
     * @param matrixFile
     * @param matrixLevel
     * @param inputGraph 
     */
    public void parseIntraMatrixFile(String matrixFile, int matrixLevel,
            NpartiteGraph inputGraph){
        /* Create FileReader and BufferedReader. */
        FileReader fr = null;
        BufferedReader br = null;
        try{
            fr = new FileReader(matrixFile);
            br = new BufferedReader(fr);
        }catch(IOException e){
            System.err.println("(biforce.graphs.XmlInputParser.parseIntraMatrixFile) Invalid inputFile:  "+matrixFile);
            return;
        }
        /* Read the matrix in the file.*/
        float[][] intraMatrix = inputGraph.intraEdgeWeights.get(matrixLevel);
        /* Split the matrixContent. */
        String row = null;
        int i = 0; /* This is the row index.*/
        try{
        while((row = br.readLine())!= null){
            row = row.trim();
            if(row.isEmpty())
                continue;
            /* Split the row into columns.*/
            String[] cols = row.split("\\s+");
            
            for(int j=0;j<cols.length;j++){
                /* If this is a self-edge, then we just check if the value in the matrix is also NaN. */
                if(i==j && !Double.isNaN(intraMatrix[i][j]) )
                    throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) Self-edge error, matrixLevel: "+
                            matrixLevel+" ,row/col is:  "+i);
                else if(i==j) {} 
                else{/* If i!= j.*/
                    /* First check the number format of the value in the matrix. */
                    float value = Float.NaN;
                    try{
                        value = Float.parseFloat(String.copyValueOf(cols[j].toCharArray()));
                    }catch(NumberFormatException e){
                        e.printStackTrace();
                        throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) Number format error:  "+cols[j]);
                    }
                    /* Here the value cannot be NaN, since it's not a self-edge. 
                    Rule changed, because pathway-pathway sims are all NaNs. 2015.03.08*/
                    //if(Double.isNaN(value))
                      //  throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) Non-self edge weight cannot be NaN:  "+matrixFile+"\n"+value+"  "+i+","+j);
                    /* Check if the value in the matrix is already initialized. */
                    if(!Double.isNaN(intraMatrix[i][j]))
                        throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) Duplicated initialization,"+
                                " value in the matrix:  "+intraMatrix[i][j]+"  value in the xml file:  "+value);
                    /* We then assign the value in the intraMatrix. */
                    intraMatrix[i][j] = value;
                    /* Here we have to check if the intraMatrix is symmetric. */
                    /* This if is added because pathway-pathway sims are all NaNs. 2015.03.08 */
                    if(i >j && Double.isNaN(intraMatrix[i][j]) && Double.isNaN(intraMatrix[j][i]))
                        continue;
                    if(i>j && intraMatrix[i][j] != intraMatrix[j][i])
                        throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) Intra-matrix is not symmetric: "+matrixFile+"\n"+
                                i+","+j+":  "+intraMatrix[i][j]+";  "+i+","+j+":  "+intraMatrix[j][i]);
                }
            }
            i++;
        }
        }catch(IOException e){
            System.err.println("(biforce.graphs.XmlInputParser.parseEntity) readLine() error.");
            return;
        }
        /* Here we check the size of the matrix, see if it matches setSizes. */
        if(i != inputGraph.setSizes[matrixLevel] || 
                intraMatrix[0].length != inputGraph.setSizes[matrixLevel])
            throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) The size of the intra-matrix does not match setSizes, setSize:  "+
                    inputGraph.setSizes[matrixLevel]+", row:"+i+", cols:"+intraMatrix[0].length);
        inputGraph.intraEdgeWeights.set(matrixLevel, intraMatrix);
    }
    
    
    
    /**
     * This method parses the matrix string for MatrixGraph.
     * @param matrixContent
     * @param inputGraph 
     */
    public final void parseMatrixString(String matrixContent, GeneralGraph inputGraph){
        float[][] matrix = inputGraph.edgeWeights;
        String[] rowsStr = matrixContent.split("\n");
        for(int i=0;i<rowsStr.length;i++){
            String row = rowsStr[i];
            /* Split the row into columns.*/
            String[] cols = row.split("\\s+");
            /* Here we check the size of the matrix, see if it matches setSizes. */
            if(rowsStr.length != inputGraph.vertexCount() || 
                    cols.length != inputGraph.vertexCount())
                throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) The size of the intra-matrix does not match setSizes, setSize:  "+
                        inputGraph.vertexCount()+", row:"+rowsStr.length+", cols:"+cols.length);
            for(int j=0;j<cols.length;j++){
                /* If this is a self-edge, then we just check if the value in the matrix is also NaN. */
                if(i==j && !Double.isNaN(matrix[i][j]) )
                    throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) Self-edge error, matrixLevel: matrixLevel row/col is:  "+i);
                else if(i==j)
                {}
                else{/* If i!= j.*/
                    /* First check the number format of the value in the matrix. */
                    float value = Float.NaN;
                    try{
                        value = Float.parseFloat(String.copyValueOf(cols[j].toCharArray()));
                    }catch(NumberFormatException e){
                        e.printStackTrace();
                        throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) Number format error:  "+cols[j]);
                    }
                    /* Here the value cannot be NaN, since it's not a self-edge.*/
                    if(Double.isNaN(value))
                        throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) Non-self edge weight cannot be NaN:  "+value+"  "+i+","+j);
                    /* Check if the value in the matrix is already initialized. */
                    if(!Double.isNaN(matrix[i][j]))
                        throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) Duplicated initialization,"+
                                " value in the matrix:  "+matrix[i][j]+"  value in the xml file:  "+value);
                    /* We then assign the value in the intraMatrix. */
                    matrix[i][j] = value;
                    /* Here we have to check if the intraMatrix is symmetric. */
                    if(i>j && matrix[i][j] != matrix[j][i])
                        throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) Intra-matrix is not symmetric: "+
                                i+","+j+":  "+matrix[i][j]+";  "+i+","+j+":  "+matrix[j][i]);
                }
            }
        }
        
    }
    
    /**
     * This method parses the matrix file for MatrixGraph
     * @param matrixFile
     * @param inputGraph 
     */
    public final void parseMatrixFile(String matrixFile, GeneralGraph inputGraph){
        FileReader fr = null;
        BufferedReader br = null;
        try{
            fr = new FileReader(matrixFile);
            br = new BufferedReader(fr);
        }catch(IOException e){
            System.err.println("(biforce.graphs.XmlInputParser.parseIntraMatrixFile) Invalid inputFile:  "+matrixFile);
            return;
        }
        /* Read the matrix in the file.*/
        float[][] matrix = inputGraph.edgeWeights;
        /* Split the matrixContent. */
        String row = null;
        int i = 0; /* This is the row index.*/
        try{
        while((row = br.readLine())!= null){
            row = row.trim();
            if(row.isEmpty())
                continue;
            /* Split the row into columns.*/
            String[] cols = row.split("\\s+");
            
            for(int j=0;j<cols.length;j++){
                /* If this is a self-edge, then we just check if the value in the matrix is also NaN. */
                if(i==j && !Double.isNaN(matrix[i][j]) )
                    throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) Self-edge error, matrixLevel: row/col is:  "+i);
                else if(i==j) {} 
                else{/* If i!= j.*/
                    /* First check the number format of the value in the matrix. */
                    float value = Float.NaN;
                    try{
                        value = Float.parseFloat(String.copyValueOf(cols[j].toCharArray()));
                    }catch(NumberFormatException e){
                        e.printStackTrace();
                        throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) Number format error:  "+cols[j]);
                    }
                    /* Here the value cannot be NaN, since it's not a self-edge. 
                    Rule changed, because pathway-pathway sims are all NaNs. 2015.03.08*/
                    //if(Double.isNaN(value))
                      //  throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) Non-self edge weight cannot be NaN:  "+matrixFile+"\n"+value+"  "+i+","+j);
                    /* Check if the value in the matrix is already initialized. */
                    if(!Double.isNaN(matrix[i][j]))
                        throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) Duplicated initialization,"+
                                " value in the matrix:  "+matrix[i][j]+"  value in the xml file:  "+value);
                    /* We then assign the value in the intraMatrix. */
                    matrix[i][j] = value;
                    /* Here we have to check if the intraMatrix is symmetric. */
                    /* This if is added because pathway-pathway sims are all NaNs. 2015.03.08 */
                    if(i >j && Double.isNaN(matrix[i][j]) && Double.isNaN(matrix[j][i]))
                        continue;
                    if(i>j && matrix[i][j] != matrix[j][i])
                        throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) Intra-matrix is not symmetric: "+matrixFile+"\n"+
                                i+","+j+":  "+matrix[i][j]+";  "+i+","+j+":  "+matrix[j][i]);
                }
            }
            i++;
        }
        }catch(IOException e){
            System.err.println("(biforce.graphs.XmlInputParser.parseEntity) readLine() error.");
            return;
        }
        /* Here we check the size of the matrix, see if it matches setSizes. */
        if(i != inputGraph.vertexCount() || 
                matrix[0].length != inputGraph.vertexCount())
            throw new IllegalArgumentException("(biforce.graphs.XmlInputParser.parseEntity) The size of the intra-matrix does not match setSizes, setSize:  "+
                    inputGraph.vertexCount()+", row:"+i+", cols:"+matrix[0].length);
    }
    
}

