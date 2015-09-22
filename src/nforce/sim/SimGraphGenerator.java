/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nforce.sim;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * This class contains the random graph generators.
 * @author penpen926
 */
public class SimGraphGenerator {
    
    /**
     * This method generates random hiergraph
     * @param setSizes
     * @param outputFile
     * @param mean
     * @param stdev
     * @return 
     */
    public float genHierGraph(int[] setSizes, String outputFile, float mean, float stdev){
        /* First and foremost, check the legality of all parameters. */
        /* setSizes cannot be null.*/
        if(setSizes == null)
            throw new IllegalArgumentException("(biforce.sim.SimGraphGen.generatorHierGeneralGraphXml) Parameter setSizes is null.");
        /* Elements in setSizes cannot be smaller than 1. */
        for(int i=0;i<setSizes.length;i++){
            if(setSizes[i]<=0)
                throw new IllegalArgumentException("(biforce.sim.SimGraphGen.generatorHierGeneralGraphXml) The element "+i+" in setSizes is smaller or equal to zero:  "+setSizes[i]);
        }
        /* The String outputFile cannot be null.*/
        if(outputFile == null)
            throw new IllegalArgumentException("(biforce.sim.SimGraphGen.generatorHierGeneralGraphXml) The parameter outputFile is null.");
        /* outputFile must be a legal file path. */
        /* Generate xml file. */
        FileWriter fw =null; 
        BufferedWriter bw = null;
        try{
            fw = new FileWriter(outputFile);
            bw = new BufferedWriter(fw);
        }catch(IOException e){
            System.out.println("(biforce.sim.SimGraphGen.generatorHierGeneralGraphXml) Outputfile error: "+outputFile);
            e.printStackTrace();
            return -1;
        }
        /* The mean must be a legal float. */
        if(Double.isNaN(mean))
            throw new IllegalArgumentException("(biforce.sim.SimGraphGen.generatorHierGeneralGraphXml) Parameter mean is not a float float number.");
        if(Double.isNaN(stdev) || stdev<0)
            throw new IllegalArgumentException("(biforce.sim.SimGraphGen.generatorHierGeneralGraphXml) Parameter stdev is illegal:  "+stdev);
        
        float cost = 0; /* The cost */
        /* Get the vertex count. */
        int vertexCount = 0;
        for(int i=0;i<setSizes.length;i++)
            vertexCount += setSizes[i];
        /* Create a one=dimensional array for all vertices.  */
        ArrayList<String> vertices = new ArrayList<>();
        
        /* Insert the values into the vertices. */
        for(int i=0;i<setSizes.length;i++){
            for(int j=0;j<setSizes[i];j++){
                String value = i+"_"+j;
                vertices.add(value);
            }
        }
        /* Permutate the arraylist. */
        Collections.shuffle(vertices);
        /* Create the pivot index array. */
        ArrayList<Integer> pivotIndexes = new ArrayList<>();
        int assignedElements = 0; /* Number of elements assigned. */
        do{
            /* Randomly get an integer from 1 to vtxNum-assignedElements. */
            assignedElements += (int)( Math.random()*(vertexCount- assignedElements)+1);
            pivotIndexes.add(assignedElements);
        }while(vertexCount-assignedElements != 0);
        
        /* Assign the inter-set edge weights. */
        ArrayList<float[][]> interEdgeWeights = new ArrayList<>();
        for(int i=0;i<setSizes.length-1;i++){
            float[][] weights = new float[setSizes[i]][setSizes[i+1]];
            for(int j=0;j<setSizes[i];j++)
                for(int k=0;k<setSizes[i+1];k++){
                    /* Get the indexes of the two vertices in the vertices array. */
                    String value1 = i+"_"+j;
                    String value2 = (i+1)+"_"+k;
                    int idx1 = vertices.indexOf(value1);
                    int idx2 = vertices.indexOf(value2);
                    /* Check if these two vertices are in the same pre-defiend cluster. */
                    if(inSameClust(idx1,idx2,pivotIndexes)){
                        Random r = new Random();
                        weights[j][k] = (float)r.nextGaussian()*stdev+mean;
                        if(weights[j][k] <0)
                            cost -= weights[j][k];
                    }
                    else{
                        Random r = new Random();
                        weights[j][k] = (float)r.nextGaussian()*stdev-mean;
                        if(weights[j][k]>0)
                            cost+= weights[j][k];
                    }    
                }
            interEdgeWeights.add(weights);
        }
        /* Output the root element.*/
        try{
            bw.write("<document>\n");
            /* Output the entity.*/
            bw.write("<entity levels=\""+setSizes.length+"\">\n");
            for(int i=0;i<setSizes.length;i++){
                for(int j=0;j<setSizes[i]-1;j++)
                    bw.write(i+"_"+j+"\t"); /* Output the row from 0 to setSizes[i]-1. */
                bw.write(i+"_"+(setSizes[i]-1)+"\n");
            }
            bw.write("</entity>\n");
            /* Output the inter-matrix.*/
            for(int i=0;i<setSizes.length-1;i++){
                bw.write("<matrix matrixLevel=\""+i+" "+(i+1)+"\">\n");
                /* Output the ith inter-matrix. */
                float[][] interMatrix = interEdgeWeights.get(i);
                for(int j=0;j<interMatrix.length;j++){
                    for(int k=0;k<interMatrix[0].length-1;k++)
                        bw.write(interMatrix[j][k]+"\t");
                    bw.write(interMatrix[j][interMatrix[0].length-1]+"\n");
                }
                bw.write("</matrix>\n");
            }
            bw.write("</document>\n");
            bw.flush();
            bw.close();
            fw.close();
        }catch(IOException e){
            System.out.println("(biforce.sim.SimGraphGen.generatorHierGeneralGraphXml) Write file failed:  "+outputFile);
            e.printStackTrace();
            return -1;
        }
        
        return cost;
    }
    
    /**
     * This method generates the random hierarchy graph with intra-edges.
     * @param setSizes
     * @param outputFile
     * @param mean
     * @param stdev
     * @return 
     */
    public float genHierGraphWIE(int[] setSizes, String outputFile, float mean, float stdev){
        /* First and foremost, check the legality of all parameters. */
        /* setSizes cannot be null.*/
        if(setSizes == null)
            throw new IllegalArgumentException("(biforce.sim.SimGraphGen.generatorHierGeneralGraphXml) Parameter setSizes is null.");
        /* Elements in setSizes cannot be smaller than 1. */
        for(int i=0;i<setSizes.length;i++){
            if(setSizes[i]<=0)
                throw new IllegalArgumentException("(biforce.sim.SimGraphGen.generatorHierGeneralGraphXml) The element "+i+" in setSizes is smaller or equal to zero:  "+setSizes[i]);
        }
        /* The String outputFile cannot be null.*/
        if(outputFile == null)
            throw new IllegalArgumentException("(biforce.sim.SimGraphGen.generatorHierGeneralGraphXml) The parameter outputFile is null.");
        /* outputFile must be a legal file path. */
        /* Generate xml file. */
        FileWriter fw =null; 
        BufferedWriter bw = null;
        try{
            fw = new FileWriter(outputFile);
            bw = new BufferedWriter(fw);
        }catch(IOException e){
            System.out.println("(biforce.sim.SimGraphGen.generatorHierGeneralGraphXml) Outputfile error: "+outputFile);
            e.printStackTrace();
            return -1;
        }
        /* The mean must be a legal float. */
        if(Double.isNaN(mean))
            throw new IllegalArgumentException("(biforce.sim.SimGraphGen.generatorHierGeneralGraphXml) Parameter mean is not a float float number.");
        if(Double.isNaN(stdev) || stdev<0)
            throw new IllegalArgumentException("(biforce.sim.SimGraphGen.generatorHierGeneralGraphXml) Parameter stdev is illegal:  "+stdev);
        
        float cost = 0; /* The cost */
        /* Get the vertex count. */
        int vertexCount = 0;
        for(int i=0;i<setSizes.length;i++)
            vertexCount += setSizes[i];
        /* Create a one=dimensional array for all vertices.  */
        ArrayList<String> vertices = new ArrayList<>();
        
        /* Insert the values into the vertices. */
        for(int i=0;i<setSizes.length;i++){
            for(int j=0;j<setSizes[i];j++){
                String value = i+"_"+j;
                vertices.add(value);
            }
        }
        /* Permutate the arraylist. */
        Collections.shuffle(vertices);
        /* Create the pivot index array. */
        ArrayList<Integer> pivotIndexes = new ArrayList<>();
        int assignedElements = 0; /* Number of elements assigned. */
        do{
            /* Randomly get an integer from 1 to vtxNum-assignedElements. */
            assignedElements += (int)( Math.random()*(vertexCount- assignedElements)+1);
            pivotIndexes.add(assignedElements);
        }while(vertexCount-assignedElements != 0);
        /* Assign the intra-set edge weights. */
        ArrayList<float[][]> intraEdgeWeights = new ArrayList<>();
        for(int i=0;i<setSizes.length;i++){
            float[][] weights = new float[setSizes[i]][setSizes[i]];
            /* Generate edge weights. */
            for(int j=0;j<setSizes[i];j++)
                for(int k=j+1;k<setSizes[i];k++){
                    /* Get the indexes of the two vertices in the vertices array. */
                    String value1 = i+"_"+j;
                    String value2 = i+"_"+k;
                    int idx1 = vertices.indexOf(value1);
                    int idx2 = vertices.indexOf(value2); 
                    /* Check if these two nodes are in the same pre-defined cluster. */
                    if(inSameClust(idx1, idx2,pivotIndexes)){
                        Random r = new Random();
                        /* In the intra edge weight matrix, we have to assign two edge weights. */
                        weights[j][k] = (float)r.nextGaussian()*stdev+mean;
                        weights[k][j] = weights[j][k];
                        if(weights[j][k]<0)
                            cost-= weights[j][k];
                    }
                    else{
                        Random r = new Random();
                        /* In the intra edge weight matrix, we have to assign two edge weights. */
                        weights[j][k] = (float)r.nextGaussian()*stdev-mean;
                        weights[k][j] = weights[j][k];
                        if(weights[j][k]>0)
                            cost+= weights[j][k];
                    }
                }
            intraEdgeWeights.add(weights);
        }
        /* Assign the inter-set edge weights. */
        ArrayList<float[][]> interEdgeWeights = new ArrayList<>();
        for(int i=0;i<setSizes.length-1;i++){
            float[][] weights = new float[setSizes[i]][setSizes[i+1]];
            for(int j=0;j<setSizes[i];j++)
                for(int k=0;k<setSizes[i+1];k++){
                    /* Get the indexes of the two vertices in the vertices array. */
                    String value1 = i+"_"+j;
                    String value2 = (i+1)+"_"+k;
                    int idx1 = vertices.indexOf(value1);
                    int idx2 = vertices.indexOf(value2);
                    /* Check if these two vertices are in the same pre-defiend cluster. */
                    if(inSameClust(idx1,idx2,pivotIndexes)){
                        Random r = new Random();
                        weights[j][k] = (float)r.nextGaussian()*stdev+mean;
                        if(weights[j][k] <0)
                            cost -= weights[j][k];
                    }
                    else{
                        Random r = new Random();
                        weights[j][k] = (float)r.nextGaussian()*stdev-mean;
                        if(weights[j][k]>0)
                            cost+= weights[j][k];
                    }    
                }
            interEdgeWeights.add(weights);
        }
        /* Output the root element.*/
        try{
            bw.write("<document>\n");
            /* Output the entity.*/
            bw.write("<entity levels=\""+setSizes.length+"\">\n");
            for(int i=0;i<setSizes.length;i++){
                for(int j=0;j<setSizes[i]-1;j++)
                    bw.write(i+"_"+j+"\t"); /* Output the row from 0 to setSizes[i]-1. */
                bw.write(i+"_"+(setSizes[i]-1)+"\n");
            }
            bw.write("</entity>\n");
            /* Output the intra-matrix. */
            for(int i=0;i<setSizes.length;i++){
                bw.write("<matrix matrixLevel=\""+i+" "+i+"\">\n");
                /* Output the ith intra-matrix. */
                float[][] intraMatrix = intraEdgeWeights.get(i);
                for(int j=0;j<intraMatrix.length;j++){
                    for(int k=0;k<intraMatrix[0].length-1;k++)
                        bw.write(intraMatrix[j][k]+"\t"); 
                    bw.write(intraMatrix[j][intraMatrix[0].length-1]+"\n");
                }
                bw.write("</matrix>\n");
            }
            /* Output the inter-matrix.*/
            for(int i=0;i<setSizes.length-1;i++){
                bw.write("<matrix matrixLevel=\""+i+" "+(i+1)+"\">\n");
                /* Output the ith inter-matrix. */
                float[][] interMatrix = interEdgeWeights.get(i);
                for(int j=0;j<interMatrix.length;j++){
                    for(int k=0;k<interMatrix[0].length-1;k++)
                        bw.write(interMatrix[j][k]+"\t");
                    bw.write(interMatrix[j][interMatrix[0].length-1]+"\n");
                }
                bw.write("</matrix>\n");
            }
            bw.write("</document>\n");
            bw.flush();
            bw.close();
            fw.close();
        }catch(IOException e){
            System.out.println("(biforce.sim.SimGraphGen.generatorHierGeneralGraphXml) Write file failed:  "+outputFile);
            e.printStackTrace();
            return -1;
        }
        
        return cost;
    }
    
    /**
     * This method generates random general graph.
     * @param setSize
     * @param outputFile
     * @param mean
     * @param stdev
     * @return 
     */
    public float genGeneralGraph(int vtxNum, String outputFile, float mean, float stdev){
        
        /* The pivots pivotIndexes stands tell the division of the pre-defined clusters.
         Each cluster contains the vertices with index from pivotIdexes[i] to (but not
         include) pivotIndexes[i+1] i>=0. For the first cluster, we can infer its
         size.*/
        ArrayList<Integer> pivotIndexes = new ArrayList<>();
        int assignedElements = 0; /* Number of elements assigned. */
        do{
            /* Randomly get an integer from 1 to vtxNum-assignedElements. */
            assignedElements += (int)( Math.random()*(vtxNum- assignedElements)+1);
            pivotIndexes.add(assignedElements);
            
        }while(vtxNum-assignedElements != 0);
                
        /* Now create the edge weight matrix. */
        float[][] edgeWeights = new float[vtxNum][vtxNum] ;
        for(int i =0;i<edgeWeights.length;i++)
            for(int j=0;j<edgeWeights[0].length;j++)
                edgeWeights[i][j] = Float.NaN;
        /* Init the cost. */
        float cost = 0;
        /* Assign the edge weights. */
        for(int i=0;i<edgeWeights.length;i++)
            for(int j=i+1;j<edgeWeights[0].length;j++){
                if(inSameClust(i,j,pivotIndexes)){
                    /* Use util.Random to generate normal distribution and get edge
                     weights for intra-edges.*/
                    Random r = new Random();
                    float ew = (float)r.nextGaussian()*stdev+mean;
                    edgeWeights[i][j] = ew;
                    edgeWeights[j][i] = ew;
                    if(edgeWeights[i][j]<0)
                        cost-= edgeWeights[i][j]; /* If we have a negative intra-edge, then add it
                         * to cost.*/
                }
                else{
                    /* For the inter-edges. */
                    Random r = new Random();
                    float ew = (float)r.nextGaussian()*stdev-mean;
                    edgeWeights[i][j] = ew;
                    edgeWeights[j][i] = ew;
                    if(edgeWeights[i][j] >0)
                        cost += edgeWeights[i][j]; /* if we have a positive inter-edge, the we add
                         * it to cost.*/
                }
            }
        /* Output the graph file. */
        /* First output the number of vertices. */
        try{
        FileWriter fw = new FileWriter(outputFile);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(vtxNum+"\r\n");
        for(int i=0;i<edgeWeights.length;i++)
            for(int j=i+1;j<edgeWeights.length;j++){
                bw.write("v"+i+"\t"+"v"+j+"\t"+edgeWeights[i][j]+"\r\n");
            }
        
        bw.flush();
        bw.close();
        fw.close();
        }catch(IOException e){
            System.out.println("File output error, output file path:  "+outputFile);
        }
        return cost;
    }
    
    /**
     * This method generates the 
     * @param vtxNum
     * @param outputFile
     * @param mean
     * @param stdev
     * @return 
     */
    public float genGeneralGraph2(int vtxNum, String outputFile, float mean, float stdev){
        /* First and foremost, check the legality of all parameters. */
        /* setSizes cannot be null.*/
        if(vtxNum<=0)
            throw new IllegalArgumentException(
                    "Parameter vtxNum is smaller or equal to 0.");
       
        /* The String outputFile cannot be null.*/
        if(outputFile == null)
            throw new IllegalArgumentException("The parameter outputFile is null.");
        /* outputFile must be a legal file path. */
        /* Generate xml file. */
        FileWriter fw =null; 
        BufferedWriter bw = null;
        try{
            fw = new FileWriter(outputFile);
            bw = new BufferedWriter(fw);
        }catch(IOException e){
            System.out.println("(biforce.sim.SimGraphGen.generatorHierGeneralGraphXml) Outputfile error: "+outputFile);
            e.printStackTrace();
            return -1;
        }
        /* The mean must be a legal float. */
        if(Double.isNaN(mean))
            throw new IllegalArgumentException("(biforce.sim.SimGraphGen.generatorHierGeneralGraphXml) Parameter mean is not a float float number.");
        if(Double.isNaN(stdev) || stdev<0)
            throw new IllegalArgumentException("(biforce.sim.SimGraphGen.generatorHierGeneralGraphXml) Parameter stdev is illegal:  "+stdev);
        
        float cost = 0; /* The cost */
        /* Create a one=dimensional array for all vertices.  */
        ArrayList<String> vertices = new ArrayList<>();
        
        /* Insert the values into the vertices. */
        for(int i=0;i<vtxNum;i++){
            String value = String.valueOf(i);
            vertices.add(value);
        }
        
        /* Permutate the arraylist. */
        Collections.shuffle(vertices);
        /* Create the pivot index array. */
        ArrayList<Integer> pivotIndexes = new ArrayList<>();
        int assignedElements = 0; /* Number of elements assigned. */
        do{
            /* Randomly get an integer from 1 to vtxNum-assignedElements. */
            assignedElements += (int)( Math.random()*(vtxNum- assignedElements)+1);
            pivotIndexes.add(assignedElements);
        }while(vtxNum-assignedElements != 0);
        /* Assign the intra-set edge weights. */
        float[][] weights = new float[vtxNum][vtxNum];
        //Init the weights.
        for(int i=0;i<weights.length;i++)
            for(int j=0;j<weights[0].length;j++)
                weights[i][j] = Float.NaN;
        
        for(int i=0;i<vtxNum;i++)
            for(int j=i+1;j<vtxNum;j++){
                String value1 = String.valueOf(i);
                String value2 = String.valueOf(j);
                int idx1 = vertices.indexOf(value1);
                int idx2 = vertices.indexOf(value2);
                if(inSameClust(idx1, idx2, pivotIndexes)){
                    Random r = new Random();
                    weights[i][j] = (float)r.nextGaussian()*stdev+mean;
                    weights[j][i] = weights[i][j];
                    if(weights[j][i]<0)
                            cost-= weights[j][i];
                }else{
                    Random r = new Random();
                    weights[i][j] = (float)r.nextGaussian()*stdev-mean;
                    weights[j][i] = weights[i][j];
                    if(weights[j][i]>0)
                            cost+= weights[j][i];
                }
            }
        
        /* Output the root element.*/
        try{
            bw.write("<document>\n");
            /* Output the entity.*/
            bw.write("<entity levels=\""+1+"\">\n");
            
            for(int j=0;j<vtxNum-1;j++)
                bw.write(j+"\t"); /* Output the row from 0 to setSizes[i]-1. */
            bw.write(vtxNum-1+"\n");
            
            bw.write("</entity>\n");
            /* Output the intra-matrix. */

            bw.write("<matrix matrixLevel=\""+0+" "+0+"\">\n");
            /* Output the ith intra-matrix. */
            
            for(int j=0;j<weights.length;j++){
                for(int k=0;k<weights[0].length-1;k++)
                    bw.write(weights[j][k]+"\t"); 
                bw.write(weights[j][weights[0].length-1]+"\n");
            }
            bw.write("</matrix>\n");
            bw.write("</document>\n");
            bw.flush();
            bw.close();
            fw.close();
        }catch(IOException e){
            System.out.println("(biforce.sim.SimGraphGen.generatorHierGeneralGraphXml) Write file failed:  "+outputFile);
            e.printStackTrace();
            return -1;
        }
        
        return cost;
    }
    /**
     * This method generates random npartite graph.
     * @param setSizes
     * @param outputFile
     * @param mean
     * @param stdev
     * @return 
     */
    public float genNpartiteGraph(int[] setSizes, String outputFile, float mean, float stdev){
        /* First and foremost, check the legality of all parameters. */
        /* setSizes cannot be null.*/
        if(setSizes == null)
            throw new IllegalArgumentException("(biforce.sim.SimGraphGen.generatorHierGeneralGraphXml) Parameter setSizes is null.");
        /* Elements in setSizes cannot be smaller than 1. */
        for(int i=0;i<setSizes.length;i++){
            if(setSizes[i]<=0)
                throw new IllegalArgumentException("(biforce.sim.SimGraphGen.generatorHierGeneralGraphXml) The element "+i+" in setSizes is smaller or equal to zero:  "+setSizes[i]);
        }
        /* The String outputFile cannot be null.*/
        if(outputFile == null)
            throw new IllegalArgumentException("(biforce.sim.SimGraphGen.generatorHierGeneralGraphXml) The parameter outputFile is null.");
        /* outputFile must be a legal file path. */
        /* Generate xml file. */
        FileWriter fw =null; 
        BufferedWriter bw = null;
        try{
            fw = new FileWriter(outputFile);
            bw = new BufferedWriter(fw);
        }catch(IOException e){
            System.out.println("(biforce.sim.SimGraphGen.generatorHierGeneralGraphXml) Outputfile error: "+outputFile);
            e.printStackTrace();
            return -1;
        }
        /* The mean must be a legal float. */
        if(Double.isNaN(mean))
            throw new IllegalArgumentException("(biforce.sim.SimGraphGen.generatorHierGeneralGraphXml) Parameter mean is not a float float number.");
        if(Double.isNaN(stdev) || stdev<0)
            throw new IllegalArgumentException("(biforce.sim.SimGraphGen.generatorHierGeneralGraphXml) Parameter stdev is illegal:  "+stdev);
        
        float cost = 0; /* The cost */
        /* Get the vertex count. */
        int vertexCount = 0;
        for(int i=0;i<setSizes.length;i++)
            vertexCount += setSizes[i];
        /* Create a one=dimensional array for all vertices.  */
        ArrayList<String> vertices = new ArrayList<>();
        
        /* Insert the values into the vertices. */
        for(int i=0;i<setSizes.length;i++){
            for(int j=0;j<setSizes[i];j++){
                String value = i+"_"+j;
                vertices.add(value);
            }
        }
        /* Permutate the arraylist. */
        Collections.shuffle(vertices);
        /* Create the pivot index array. */
        ArrayList<Integer> pivotIndexes = new ArrayList<>();
        int assignedElements = 0; /* Number of elements assigned. */
        do{
            /* Randomly get an integer from 1 to vtxNum-assignedElements. */
            assignedElements += (int)( Math.random()*(vertexCount- assignedElements)+1);
            pivotIndexes.add(assignedElements);
        }while(vertexCount-assignedElements != 0);
        /* Assign the intra-set edge weights. */
        ArrayList<float[][]> intraEdgeWeights = new ArrayList<>();
        for(int i=0;i<setSizes.length;i++){
            float[][] weights = new float[setSizes[i]][setSizes[i]];
            /* Generate edge weights. */
            for(int j=0;j<setSizes[i];j++)
                for(int k=j+1;k<setSizes[i];k++){
                    /* Get the indexes of the two vertices in the vertices array. */
                    String value1 = i+"_"+j;
                    String value2 = i+"_"+k;
                    int idx1 = vertices.indexOf(value1);
                    int idx2 = vertices.indexOf(value2); 
                    /* Check if these two nodes are in the same pre-defined cluster. */
                    if(inSameClust(idx1, idx2,pivotIndexes)){
                        Random r = new Random();
                        /* In the intra edge weight matrix, we have to assign two edge weights. */
                        weights[j][k] = (float)r.nextGaussian()*stdev+mean;
                        weights[k][j] = weights[j][k];
                        if(weights[j][k]<0)
                            cost-= weights[j][k];
                    }
                    else{
                        Random r = new Random();
                        /* In the intra edge weight matrix, we have to assign two edge weights. */
                        weights[j][k] = (float)r.nextGaussian()*stdev-mean;
                        weights[k][j] = weights[j][k];
                        if(weights[j][k]>0)
                            cost+= weights[j][k];
                    }
                }
            intraEdgeWeights.add(weights);
        }
        /* Assign the inter-set edge weights. */
        ArrayList<float[][]> interEdgeWeights = new ArrayList<>();
        for(int i1=0;i1<setSizes.length-1;i1++){
            for(int i2=i1+1;i2<setSizes.length;i2++){
                float[][] weights = new float[setSizes[i1]][setSizes[i2]];
                for(int j=0;j<setSizes[i1];j++)
                    for(int k=0;k<setSizes[i2];k++){
                        /* Get the indexes of the two vertices in the vertices array. */
                        String value1 = i1+"_"+j;
                        String value2 = i2+"_"+k;
                        int idx1 = vertices.indexOf(value1);
                        int idx2 = vertices.indexOf(value2);
                        /* Check if these two vertices are in the same pre-defiend cluster. */
                        if(inSameClust(idx1,idx2,pivotIndexes)){
                            Random r = new Random();
                            weights[j][k] = (float)r.nextGaussian()*stdev+mean;
                            if(weights[j][k] <0)
                                cost -= weights[j][k];
                        }
                        else{
                            Random r = new Random();
                            weights[j][k] = (float)r.nextGaussian()*stdev-mean;
                            if(weights[j][k]>0)
                                cost+= weights[j][k];
                        }    
                    }
                interEdgeWeights.add(weights);
            }
        }
        /* Output the root element.*/
        try{
            bw.write("<document>\n");
            /* Output the entity.*/
            bw.write("<entity levels=\""+setSizes.length+"\">\n");
            for(int i=0;i<setSizes.length;i++){
                for(int j=0;j<setSizes[i]-1;j++)
                    bw.write(i+"_"+j+"\t"); /* Output the row from 0 to setSizes[i]-1. */
                bw.write(i+"_"+(setSizes[i]-1)+"\n");
            }
            bw.write("</entity>\n");
            /* Output the intra-matrix. */
            for(int i=0;i<setSizes.length;i++){
                bw.write("<matrix matrixLevel=\""+i+" "+i+"\">\n");
                /* Output the ith intra-matrix. */
                float[][] intraMatrix = intraEdgeWeights.get(i);
                for(int j=0;j<intraMatrix.length;j++){
                    for(int k=0;k<intraMatrix[0].length-1;k++)
                        bw.write(intraMatrix[j][k]+"\t"); 
                    bw.write(intraMatrix[j][intraMatrix[0].length-1]+"\n");
                }
                bw.write("</matrix>\n");
            }
            /* Output the inter-matrix.*/
            for(int i=0;i<setSizes.length-1;i++){
                bw.write("<matrix matrixLevel=\""+i+" "+(i+1)+"\">\n");
                /* Output the ith inter-matrix. */
                float[][] interMatrix = interEdgeWeights.get(i);
                for(int j=0;j<interMatrix.length;j++){
                    for(int k=0;k<interMatrix[0].length-1;k++)
                        bw.write(interMatrix[j][k]+"\t");
                    bw.write(interMatrix[j][interMatrix[0].length-1]+"\n");
                }
                bw.write("</matrix>\n");
            }
            bw.write("</document>\n");
            bw.flush();
            bw.close();
            fw.close();
        }catch(IOException e){
            System.out.println("(biforce.sim.SimGraphGen.generatorHierGeneralGraphXml) Write file failed:  "+outputFile);
            e.printStackTrace();
            return -1;
        }
        
        return cost;
    }
    
    
    /**
     * This method generates random bipartite graph.
     * @param setSizes
     * @param outputFile
     * @param mean
     * @param stdev
     * @return 
     */
    public float genBipartiteGraph(int[] setSizes, String outputFile, float mean, float stdev){
        /* First and foremost, check the legality of all parameters. */
        /* setSizes cannot be null.*/
        if(setSizes == null)
            throw new IllegalArgumentException("(biforce.sim.SimGraphGen.generatorHierGeneralGraphXml) Parameter setSizes is null.");
        /* Elements in setSizes cannot be smaller than 1. */
        for(int i=0;i<setSizes.length;i++){
            if(setSizes[i]<=0)
                throw new IllegalArgumentException("(biforce.sim.SimGraphGen.generatorHierGeneralGraphXml) The element "+i+" in setSizes is smaller or equal to zero:  "+setSizes[i]);
        }
        /* The String outputFile cannot be null.*/
        if(outputFile == null)
            throw new IllegalArgumentException("(biforce.sim.SimGraphGen.generatorHierGeneralGraphXml) The parameter outputFile is null.");
        /* outputFile must be a legal file path. */
        /* Generate xml file. */
        FileWriter fw =null; 
        BufferedWriter bw = null;
        try{
            fw = new FileWriter(outputFile);
            bw = new BufferedWriter(fw);
        }catch(IOException e){
            System.out.println("(biforce.sim.SimGraphGen.generatorHierGeneralGraphXml) Outputfile error: "+outputFile);
            e.printStackTrace();
            return -1;
        }
        /* The mean must be a legal float. */
        if(Double.isNaN(mean))
            throw new IllegalArgumentException("(biforce.sim.SimGraphGen.generatorHierGeneralGraphXml) Parameter mean is not a float float number.");
        if(Double.isNaN(stdev) || stdev<0)
            throw new IllegalArgumentException("(biforce.sim.SimGraphGen.generatorHierGeneralGraphXml) Parameter stdev is illegal:  "+stdev);
        
        float cost = 0; /* The cost */
        /* Get the vertex count. */
        int vertexCount = 0;
        for(int i=0;i<setSizes.length;i++)
            vertexCount += setSizes[i];
        /* Create a one=dimensional array for all vertices.  */
        ArrayList<String> vertices = new ArrayList<>();
        
        /* Insert the values into the vertices. */
        for(int i=0;i<setSizes.length;i++){
            for(int j=0;j<setSizes[i];j++){
                String value = i+"_"+j;
                vertices.add(value);
            }
        }
        /* Permutate the arraylist. */
        Collections.shuffle(vertices);
        /* Create the pivot index array. */
        ArrayList<Integer> pivotIndexes = new ArrayList<>();
        int assignedElements = 0; /* Number of elements assigned. */
        do{
            /* Randomly get an integer from 1 to vtxNum-assignedElements. */
            assignedElements += (int)( Math.random()*(vertexCount- assignedElements)+1);
            pivotIndexes.add(assignedElements);
        }while(vertexCount-assignedElements != 0);
        
        /* Assign the inter-set edge weights. */
        ArrayList<float[][]> interEdgeWeights = new ArrayList<>();
        for(int i=0;i<setSizes.length-1;i++){
            float[][] weights = new float[setSizes[i]][setSizes[i+1]];
            for(int j=0;j<setSizes[i];j++)
                for(int k=0;k<setSizes[i+1];k++){
                    /* Get the indexes of the two vertices in the vertices array. */
                    String value1 = i+"_"+j;
                    String value2 = (i+1)+"_"+k;
                    int idx1 = vertices.indexOf(value1);
                    int idx2 = vertices.indexOf(value2);
                    /* Check if these two vertices are in the same pre-defiend cluster. */
                    if(inSameClust(idx1,idx2,pivotIndexes)){
                        Random r = new Random();
                        weights[j][k] = (float)r.nextGaussian()*stdev+mean;
                        if(weights[j][k] <0)
                            cost -= weights[j][k];
                    }
                    else{
                        Random r = new Random();
                        weights[j][k] = (float)r.nextGaussian()*stdev-mean;
                        if(weights[j][k]>0)
                            cost+= weights[j][k];
                    }    
                }
            interEdgeWeights.add(weights);
        }
        /* Output the root element.*/
        try{
            bw.write("<document>\n");
            /* Output the entity.*/
            bw.write("<entity levels=\""+setSizes.length+"\">\n");
            for(int i=0;i<setSizes.length;i++){
                for(int j=0;j<setSizes[i]-1;j++)
                    bw.write(i+"_"+j+"\t"); /* Output the row from 0 to setSizes[i]-1. */
                bw.write(i+"_"+(setSizes[i]-1)+"\n");
            }
            bw.write("</entity>\n");
            /* Output the inter-matrix.*/
            for(int i=0;i<setSizes.length-1;i++){
                bw.write("<matrix matrixLevel=\""+i+" "+(i+1)+"\">\n");
                /* Output the ith inter-matrix. */
                float[][] interMatrix = interEdgeWeights.get(i);
                for(int j=0;j<interMatrix.length;j++){
                    for(int k=0;k<interMatrix[0].length-1;k++)
                        bw.write(interMatrix[j][k]+"\t");
                    bw.write(interMatrix[j][interMatrix[0].length-1]+"\n");
                }
                bw.write("</matrix>\n");
            }
            bw.write("</document>\n");
            bw.flush();
            bw.close();
            fw.close();
        }catch(IOException e){
            System.out.println("(biforce.sim.SimGraphGen.generatorHierGeneralGraphXml) Write file failed:  "+outputFile);
            e.printStackTrace();
            return -1;
        }
        
        return cost;
    }
    
    /**
     * This method works for generatorGeneralGraph1(), to check whether two given indexes 
     * in the same pre-defined clusters.
     * @param i
     * @param j
     * @param pivotIndexes
     * @return 
     */
    public boolean inSameClust(int i, int j, ArrayList<Integer> pivotIndexes){
        int maxPivotI=0,maxPivotJ=0;
        for(int k=0;k<pivotIndexes.size();k++){
            if(pivotIndexes.get(k)<=i)
                maxPivotI = pivotIndexes.get(k);
            if(pivotIndexes.get(k)<=j)
                maxPivotJ = pivotIndexes.get(k);
        }
        return maxPivotI == maxPivotJ;
    }
}
