/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nforce.graphs;

import nforce.util.XmlInputParser;
import nforce.util.Action;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import nforce.constants.nForceConstants;

/**
 * This class is the object for normal class.
 * @author penpen926
 */
public class GeneralGraph extends Graph{
    /* The float value for cost. */
    float cost = 0;
    /* The two dimensional array stores the adjacency matrix.*/
    float[][] edgeWeights = null;
    /* This matrix stores the distances. */
    /* Distances, unlike edge weight, must be defined between two arbitrary vertices. It does not matter 
    whether the two vertices come from the same set or not, since later single-linkage and kmeans clustering
    use pairwise distances. */
    /* Note that the distance matrix is a LOWER TRIANGULAR MATRIX. */
    float[][] distances = null;
    /**
     * Constructor.
     * @param filePath
     * @param isHeader 
     * @param isXmlFile 
     */
    public GeneralGraph(String filePath, boolean isHeader, boolean isXmlFile){
        vertices= new ArrayList<>();
        actions =new ArrayList<>();
        clusters = new ArrayList<>();
        if(isXmlFile)
            try{
                readXmlGraph(filePath);
            }catch(IOException e){
                System.out.println("(MatrixGraph.constructor) "
                        + " MatrixGraph readGraph in xml failed:"
                        + " "+filePath);
                return;
            }
        else if(isHeader)
            try{
                readGraphWithHeader(filePath);
            }catch(IOException e){
                System.out.println("(MatrixBipartiteGraph2.constructor)"
                        + " MaxtrixBipartiteGraph readGraphWithHeader failed:"
                        + " "+filePath);
            }
        else
            try{
                readGraph(filePath);
            }catch(IOException e){
                System.out.println("(MatrixBipartiteGraph2.constructor)"
                        + " MatrixBipartiteGraph readGraph failed:"
                        +" "+filePath);
            }
        
        /* Init the distance matrix. */
        distances = new float[vertices.size()][vertices.size()];
        for(int i=0;i<vertices.size();i++)
            for(int j=0;j<vertices.size();j++)
                distances[i][j] = -1;
    }
    /**
     * Constructor with thresh to detract.
     * @param filePath
     * @param isHeader
     * @param thresh 
     */
    public GeneralGraph(String filePath, boolean isHeader, boolean isXmlFile, float thresh){
        if(isXmlFile)
            try{
                readXmlGraph(filePath);
            }catch(IOException e){
                System.out.println("(MatrixGraph.constructor) "
                        + " MatrixGraph readGraph in xml failed:"
                        + " "+filePath);
                return;
            }
        else if(isHeader)
            try{
                readGraphWithHeader(filePath);
            }catch(IOException e){
                System.out.println("MaxtrixBipartiteGraph readGraphWithHeader failed.");
            }
        else
            try{
                readGraph(filePath);
            }catch(IOException e){
                System.out.println("MatrixBipartiteGraph readGraph failed.");
            }
        setThreshold(thresh);
        detractThresh();
        /* Init the distance matrix. */
        distances = new float[vertices.size()][vertices.size()];
        for(int i=0;i<vertices.size();i++)
            for(int j=0;j<vertices.size();j++)
                distances[i][j] = -1;
    }
    
    /**
     * This method performs the breadth-first search to get a connected component.
     * @param Vtx
     * @return 
     */
    @Override
    public GeneralSubgraph bfs(Vertex Vtx) {
        LinkedList<Vertex> queue = new LinkedList<>();
        //create a marker
        HashMap<String, Boolean> marker = new HashMap<>();
        //init the haspmap

        //create a new arrayList<Vertex> as result
        ArrayList<Vertex> result = new ArrayList<>();

        for(Vertex vtx: getVertices()){
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
         //create a new subkpartitegraph
         GeneralSubgraph sub = new GeneralSubgraph(result,this);
         return sub;
    }

    /**
     * This method returns a list of connectec components of the current graph.
     * @return 
     */
    @Override
    public List<GeneralSubgraph> connectedComponents() {
        ArrayList<GeneralSubgraph> connectecComps = new ArrayList<>();
        //create a indicator LinkedList of vertices, when a vertex is included in one of the subgraphs, then it is 
        //removed from the indicator LinkedList
        LinkedList<Vertex> indicatorList = new LinkedList<>();
        //add all the vertex into the IndicatorArray
        for(Vertex vtx: getVertices()){
            indicatorList.add(vtx);
        }
        //While there is still unvisited vertex, we use it as the seed to search for subgraphs.
        while(!indicatorList.isEmpty()){
            Vertex Seed = indicatorList.pollFirst();
            GeneralSubgraph comp = bfs(Seed);
            connectecComps.add(comp);
            //remove all the vertex in the comp from indicatorList
            for(Vertex vtx: comp.getSubvertices()){
                indicatorList.remove(vtx);
            }
        }
        connectecComps.trimToSize();
        return connectecComps;
    }

    @Override
    public final void detractThresh() {
        /* First check if the thresh is detracted. */
        if(threshDetracted)
            throw new IllegalArgumentException("(MatrixBipartiteGraph2.detractThresh)"
                    + "  The threshold is already detracted.");
        /* Second check if the threshold has been set. */
        if(thresh == Double.MAX_VALUE)
            throw new IllegalArgumentException("(MatrixBipartiteGraph2.detractThresh)"
                    + "  The threshold must be set at first.");
        else{
            for(int i=0;i<vertices.size();i++)
                for(int j=0;j<vertices.size();j++)
                    edgeWeights[i][j] -=thresh;
        }
        threshDetracted = true;
    }

    @Override
    public final void detractThresh(float thresh) {
        /* Check if the threshold has already detracted */
        if(threshDetracted)
            throw new IllegalArgumentException("(MatrixBipartiteGraph2.detractThresh)"
                    + "  The threshold is already detracted.");
        else
            setThreshold(thresh);
        detractThresh();
    }
    
    @Override
    public final void detractThresh(float[] thresh){
        throw new UnsupportedOperationException("(MatrixGraph.detractThresh) Multiple thresholds is not supported in a normal graph.");
    }

    /**
     * This method returns the distances.
     * @param vtx1
     * @param vtx2
     * @return 
     */
    @Override
    public float dist(Vertex vtx1, Vertex vtx2) {
        return distances[vtx1.getVtxIdx()][vtx2.getVtxIdx()];
    }

    @Override
    public float edgeWeight(Vertex vtx1, Vertex vtx2) {
       int vtxIdx1 = vtx1.getVtxIdx();
       int vtxIdx2 = vtx2.getVtxIdx();
       return edgeWeight(vtxIdx1, vtxIdx2);
    }

    @Override
    public float edgeWeight(int vtxIdx1, int vtxIdx2) {
        /* Check the bound. */
        if(vtxIdx1>= vertices.size() || vtxIdx2>= vertices.size() || vtxIdx1 <0 || vtxIdx2 <0)
            throw new IndexOutOfBoundsException("(MatrixGraph edgeWeight) Index out of bound. "+vtxIdx1+" "+vtxIdx2+" size: "+vertices.size());
        return edgeWeights[vtxIdx1][vtxIdx2];
    }
    
    /**
     * This method computes the euclid distance between two vertices.
     * @param vtx1
     * @param vtx2
     * @return 
     */
    private float euclidDist(Vertex vtx1, Vertex vtx2){
        if(vtx1.getCoords() == null ||
                vtx2.getCoords() == null)
            throw new IllegalArgumentException ("(MatrixBipartiteGraph.euclidDist) Null coordinates:  "+
                    "vtx1:  "+vtx1.getCoords().toString()+"  vtx2:  "+vtx2.getCoords().toString());
        //check if they have the same dimension
        if(vtx1.getCoords().length !=vtx2.getCoords().length )
            throw new IllegalArgumentException("Point 1 and Point 2 have different dimension");
        float Dist= 0;
        for(int i=0;i<vtx1.getCoords().length;i++)
        {
            Dist+= (vtx1.getCoords()[i]-vtx2.getCoords()[i])*
                    (vtx1.getCoords()[i]-vtx2.getCoords()[i]);
        }
        Dist = (float)Math.sqrt(Dist);
        return Dist;
    }

    @Override
    public float getCost() {
       return cost;
    }

    @Override
    public boolean isActionTaken(int actIdx) {
        Vertex vtx1 = actions.get(actIdx).getVtx1();
        Vertex vtx2 = actions.get(actIdx).getVtx2();
        if(edgeWeight(vtx1,vtx2) == actions.get(actIdx).getOriginalWeight())
            return false;
        else return true;
    }
    
    @Override 
    public boolean isSame(Graph graph){
        if(!(graph instanceof GeneralGraph))
            return false;
        GeneralGraph converted = (GeneralGraph) graph;
        /* Check the vertices. */
        if(vertices.size()!= converted.vertices.size())
            return false;
        
        for(int i=0;i<vertices.size();i++)
            if(!vertices.get(i).equals(vertices.get(i)))
                return false;
        
        /* Check the edge weights. */
        return isMatrixEqual(edgeWeights,converted.edgeWeights);
    }
    
    /**
     * This method returns if the two matrices are same.
     * @param matrix1
     * @param matrix2
     * @return 
     */
    public boolean isMatrixEqual(float[][] matrix1, float[][] matrix2){
        if(matrix1.length != matrix2.length)
            return false;
        if(matrix1[0].length != matrix2[0].length)
            return false;
        for(int i=0;i<matrix1.length;i++)
            for(int j=0;j<matrix2.length;j++)
                if(matrix1[i][j]!= matrix2[i][j]) 
                    return false;
        return true;
    }

    @Override
    public ArrayList<Vertex> neighbours(Vertex currentVtx) {
        /* Here we add a pre-condition: Check if the threshold is detracted. */
        if(!threshDetracted){
            System.out.println
                    ("(MatrixBipartiteGraph2.neighbours) Threshold must be detracted first.");
            detractThresh();
        }
        ArrayList<Vertex> neighbours = new ArrayList<>();
        for(Vertex v: vertices){
           if(edgeWeight(currentVtx,v)>0)
               neighbours.add(v);       
        }
        //neighbours.trimToSize();
        /* If no neighbour is found, then return null. */
        if(neighbours.isEmpty())
            return null;
        return neighbours;
    }

    
    
    @Override
    public boolean pushAction(Vertex vtx1, Vertex vtx2) {
        /* First check if thresh is already detracted, if not we have 
         to first detract threshold. */
        if(!threshDetracted)
            detractThresh();
        
        float ew = edgeWeight(vtx1, vtx2);
       // if(ew == 0)
         //   throw new IllegalArgumentException("There is a null-edge between vtx1 and vtx2");
        Action act = null;
        act = new Action(vtx1,vtx2,ew);
        actions.add(act);
        if(act.getOriginalWeight() > 0){
            cost += act.getOriginalWeight();
        }
        else{
            cost -= act.getOriginalWeight();
        }
        return true;
    }
    
    
    /**
     * This method reads graph from file.
     * @param filePath
     * @throws IOException 
     */
    @Override
    public void readGraph(String filePath) throws IOException{
        /* Init the vertices and actions. */
        vertices = new ArrayList<>();
        actions = new ArrayList<>();
        
        /* Init the file readers. */
        FileReader fr = new FileReader(filePath);
        BufferedReader br = new BufferedReader(fr);
        String line = null;
        /* Create a hashset to find out how many vertices, using the uniqueness of the elements in HashSet. */
        HashSet<String> vertexValues = new HashSet<>();
        /* First read, get the number of the vertices and initialize how many nodes are there in the graph. */
        while((line = br.readLine())!= null){
            String[] split = line.split("\\s+");
            /* If there is only one vertex in the line. */
            if(split.length == 1){
                vertexValues.add(String.copyValueOf(split[0].toCharArray()));
            }
            /* If there are two vertices in the line. */
            else{
                vertexValues.add(String.copyValueOf(split[0].toCharArray()));
                vertexValues.add(String.copyValueOf(split[1].toCharArray()));
            }  
        }
        
        edgeWeights = new float[vertexValues.size()][vertexValues.size()];
        /* Initialize the edge weights. */
        for(int i=0;i<edgeWeights.length;i++)
            for(int j=0;j<edgeWeights[0].length;j++)
                edgeWeights[i][j] = Float.NaN;
        fr.close();
        br.close();
        /* Second read, initialize the elements and the edge weights. Re-open the file.*/
        fr = new FileReader(filePath);
        br = new BufferedReader(fr);
        while((line = br.readLine())!= null){
            String[] split = line.split("\\s+");
            /* If there is only one vertex in the line, then we just push it into the vertices arraylist. */
            if(split.length == 1){
                String value = String.copyValueOf(split[0].toCharArray());
                Vertex newVtx = new Vertex(value,0,-1);
                /* If we haven't read this vertex before, then we insert it into vertices. */
                if(!vertices.contains(newVtx)){
                    /* Set the index. */
                    newVtx.setVtxIdx(vertices.size());
                    vertices.add(newVtx);
                }
            }
            /* If there are two vertices in the line, we push them into vertices and assign the edge weight. */
            else{
                /* We create two new vertices. */
                String value1 = String.copyValueOf(split[0].toCharArray());
                String value2 = String.copyValueOf(split[1].toCharArray());
                Vertex newVtx1 = new Vertex(value1,0,-1);
                Vertex newVtx2 = new Vertex(value2,0,-1);
                int idx1 = vertices.indexOf(newVtx1);
                int idx2 = vertices.indexOf(newVtx2);
                /* If we haven't added newVtx1 before, then we add it into vertices and assign idx1 as the size of vertices. */
                if(idx1 == -1){
                    idx1 = vertices.size();
                    newVtx1.setVtxIdx(idx1);
                    vertices.add(newVtx1);
                }
                /* Same to idx2. */
                if(idx2 == -1){
                    idx2 = vertices.size();
                    newVtx2.setVtxIdx(idx2);
                    vertices.add(newVtx2);
                }
                /* Assign the value in edge weight matrix. */
                /* If the index is a self loop. */
                if(idx1 == idx2)
                    continue;
                edgeWeights[idx1][idx2] = Float.parseFloat(String.copyValueOf(split[2].toCharArray()));
                edgeWeights[idx2][idx1] = Float.parseFloat(String.copyValueOf(split[2].toCharArray()));
            }     
        }
        fr.close();
        br.close();
    }
    
    
    /**
     * 
     * @param filePath
     * @throws IOException 
     */
    public void readGraphWithHeader(String filePath) throws IOException{
        vertices = new ArrayList<>();
        actions = new ArrayList<>();
        /* Init the file readers. */
        FileReader fr = new FileReader(filePath);
        BufferedReader br = new BufferedReader(fr);
        String line = null;
        /* Get the number of vertices in the graph. */
        int size = -1;
        line=br.readLine();
        try{
            size = Integer.parseInt(line);
        }catch(NumberFormatException e){
            System.out.println("(MatrixGraph readGraphWithHeader) Invalid number format: "+line);
            return;
        }
        /* Create the edge weights matrix. */
        edgeWeights = new float[size][size];
        /* Init the values in the edgeWeight matrix to NaN */
        for(int i=0;i<size;i++)
            for(int j=0;j<size;j++)
                edgeWeights[i][j] = Float.NaN;
        
        while((line = br.readLine())!= null){
            String[] split = line.split("\\s+");
            /* If there is only one vertex in the line, then we just push it into the vertices arraylist. */
            if(split.length == 1){
                String value = String.copyValueOf(split[0].toCharArray());
                Vertex newVtx = new Vertex(value,0,-1);
                /* If we haven't read this vertex before, then we insert it into vertices. */
                if(!vertices.contains(newVtx)){
                    /* Set the index. */
                    newVtx.setVtxIdx(vertices.size());
                    vertices.add(newVtx);
                }
            }
            /* If there are two vertices in the line, we push them into vertices and assign the edge weight. */
            else{
                /* We create two new vertices. */
                String value1 = String.copyValueOf(split[0].toCharArray());
                String value2 = String.copyValueOf(split[1].toCharArray());
                Vertex newVtx1 = new Vertex(value1,0,-1);
                Vertex newVtx2 = new Vertex(value2,0,-1);
                int idx1 = vertices.indexOf(newVtx1);
                int idx2 = vertices.indexOf(newVtx2);
                /* If we haven't added newVtx1 before, then we add it into vertices and assign idx1 as the size of vertices. */
                if(idx1 == -1){
                    idx1 = vertices.size();
                    newVtx1.setVtxIdx(idx1);
                    vertices.add(newVtx1);
                }
                /* Same to idx2. */
                if(idx2 == -1){
                    idx2 = vertices.size();
                    newVtx2.setVtxIdx(idx2);
                    vertices.add(newVtx2);
                }
                /* Assign the value in edge weight matrix. */
                /* If the index is a self loop. */
                if(idx1 == idx2)
                    continue;
                /* Assign the value in edge weight matrix. */
                edgeWeights[idx1][idx2] = Float.parseFloat(String.copyValueOf(split[2].toCharArray()));
                edgeWeights[idx2][idx1] = Float.parseFloat(String.copyValueOf(split[2].toCharArray()));
            }
        }
        br.close();
        fr.close();
    }
    
    
    
    public final void readXmlGraph(String filePath) throws IOException{
        /* Read the input file. */
        SAXBuilder builder = new SAXBuilder();
        Document graphInput = null;
        try{
            graphInput = builder.build(new File(filePath));
        }catch(JDOMException e){
            System.out.println("(biforce.graphs.MatrixHierGeneralGraph.readGraphInXml) Document init error:  "+filePath);
            return;
        }
        /* Get the root element. */
        Element docRoot, entity;
        try{
            docRoot = graphInput.getRootElement();
        }catch(IllegalStateException e){
            System.out.println("(biforce.graphs.MatrixHierGeneralGraph.readGraphInXml) The input document does not have a root element.");
            e.printStackTrace();
            return;
        }
        /* 1. Anaylze the element "entity", get the name of the nodes.*/
        /* Get the child element of "entity", throw an IllegalStateException.*/
        entity = docRoot.getChild("entity");
        if(entity == null) /* If no such "entity" child element is found, then throw the exception.*/
            throw new IllegalStateException("(biforce.graphs.MatrixHierGeneralGraph.readGraphInXml) The input document does not have an \"entity\" child element.");
         String hasExternEntityFile = entity.getAttributeValue("externalFile");
        /* Get the content in entity. */
        String entityContent = entity.getContent(0).getValue().trim();
        /* Check entityContent. It cannot be null.*/
        if(entityContent == null)
            throw new IllegalStateException("(biforce.graphs.MatrixHierGeneralGraph.readGraphInXml) The content of the element entity is null.");
        /* If there is an external file for the nodes' names, then the content of the element should be the path
        of the external file. Otherwise, it should be the nodes' names themselves. */
        if(hasExternEntityFile == null || hasExternEntityFile.equalsIgnoreCase("false")){
            XmlInputParser parser = new XmlInputParser();
            parser.parseEntityString(entityContent,this);
        }
        /* If the node names are stored in an external file.*/
        else if(hasExternEntityFile.equalsIgnoreCase("true")){
            XmlInputParser parser = new XmlInputParser();
            parser.parseEntityFile(entityContent,this);
        }
        else 
            throw new IllegalStateException("(biforce.graphs.MatrixHierGeneralGraph.readGraphInXml) Illegal attribute of \"externalFile\": "+hasExternEntityFile);
        
        //Init the matrix
        edgeWeights = new float[vertices.size()][vertices.size()];
        for(int i=0;i<edgeWeights.length;i++)
            for(int j=0;j<edgeWeights[0].length;j++)
                edgeWeights[i][j] = Float.NaN;
        
         /* Read the edge weights from the xml input file. */
        ArrayList<Element> matrixElementList = new ArrayList<>(docRoot.getChildren("matrix"));
        /* First check the number of elements in matrixElementList, if not equal to 2*setSizes.length-1. */
        // There are 2*setSizes.length matrix, half intra-matrices, half inter-matrices.
        if(matrixElementList.size() != 1) 
            throw new IllegalArgumentException("(biforce.graphs.MatrixGraph.readGraphInXml) The number of matrices is wrong:  "+matrixElementList.size());
        Element matrix = matrixElementList.get(0);
        String matrixContent = matrix.getContent(0).getValue().trim();
        String hasExternMatrixFile = matrix.getAttributeValue("externalFile");
                if(hasExternMatrixFile == null || hasExternMatrixFile.equalsIgnoreCase("false")){
                    XmlInputParser parser = new XmlInputParser();
                    parser.parseMatrixString(matrixContent, this);
                }
                /* If the node names are stored in an external file.*/
                else if(hasExternMatrixFile.equalsIgnoreCase("true")){
                    XmlInputParser parser = new XmlInputParser();
                    parser.parseMatrixFile(matrixContent,this);
                }
                else
                    throw new IllegalStateException("(biforce.graphs.MatrixGraph.readGraphInXml) Illegal attribute of \"externalFile\": "+hasExternMatrixFile);
    }
    
    /**
     * This method reads the graph in a matrix format.
     * @param filePath
     * @throws IOException 
     */

    /**
     * This method reads the graph in a matrix format.
     * @throws IOException
     */
    @Override
    public void restoreThresh() {
        /* First check if the thresh is detracted, if not then throw an exception. */
        if(!threshDetracted)
            throw new IllegalArgumentException("(MatrixBipartiteGraph2.restoreThresh)"
                    + "  The threshold is not yet detracted.");
        /* Second check if the threshold has been set. */
        if(thresh == Double.MAX_VALUE)
            throw new IllegalArgumentException("(MatrixBipartiteGraph2.detractThresh)"
                    + "  The threshold must be set at first.");
        else{
            for(int i=0;i<vertices.size();i++)
                for(int j=0;j<vertices.size();j++)
                    edgeWeights[i][j] +=thresh;
        }
        threshDetracted = false;
    }

    @Override
    public boolean removeAction(int index) {
        if(index<0 || index>= actions.size()){
            System.out.println("(MatrixBipartiteGraph2.removeAction) Index out of bounds: "+index);
            return false;
        }
        actions.remove(index);
        return true;
    }

    @Override
    public void setEdgeWeight(Vertex vtx1, Vertex vtx2, float edgeWeight) {
        int idx1 = vtx1.getVtxIdx();
        int idx2 = vtx2.getVtxIdx();
        /* Check if out of bounds. */
        if(idx1<0 || idx1 >= vertices.size()){
            throw new IllegalArgumentException("(MatrixGraph.setEdgeWeight) vtx1 index out of bound:  "+idx1+"  size:"+vertices.size());
        }
        if(idx2<0 || idx2>= vertices.size()){
            throw new IllegalArgumentException("(MatrixGraph.setEdgeWeight) vtx2 index out of bound:  "+idx2+"  size:"+vertices.size());
        }
        
        if(threshDetracted){
            edgeWeights[vtx1.getVtxIdx()][vtx2.getVtxIdx()] = edgeWeight-thresh;
        }
        else
            edgeWeights[vtx1.getVtxIdx()][vtx2.getVtxIdx()] = edgeWeight;
        
    }

    @Override
    public boolean takeAction(int idx) {
        /* First the check the index. */
        if(idx <0 || idx >= actions.size())
            throw new IllegalArgumentException("(MatrixBipartitGraph2.takeAction) Wrong action index: "+
                    idx);
        Action act = actions.get(idx);
        if(act.getOriginalWeight()>0)
            setEdgeWeight(act.getVtx1(),act.getVtx2(),nForceConstants.FORBIDDEN);
        else
            setEdgeWeight(act.getVtx1(),act.getVtx2(),nForceConstants.PERMENANT);
        return true;
    }

    @Override
    public boolean takeActions() {
        for(int i=0;i<actions.size();i++)
            this.takeAction(i);
        return true;
    }

    @Override
    public void updateDist() {
        for(int i=0;i<vertices.size();i++)
            for(int j=i+1;j<vertices.size();j++){
                Vertex vtx1 = vertices.get(i);
                Vertex vtx2 = vertices.get(j);
                float dist = euclidDist(vtx1,vtx2);
                distances[vtx1.getVtxIdx()][vtx2.getVtxIdx()] = dist;
                distances[vtx2.getVtxIdx()][vtx1.getVtxIdx()] = dist;
            }
    }

    @Override
    public void updatePos(float[][] dispVector) {
        /* For each vertex, update the position. */
        for(int i=0;i<vertexCount();i++){
            /* Compute the new coordinates. */
            float[] coords = vertices.get(i).getCoords();
            for(int j=0;j<coords.length;j++){
                coords[j] += dispVector[i][j];
            }
            /* Set the coordinates. */
            vertices.get(i).setCoords(coords);
        }
    }

    /**
     * This method returns how many vertex set. In this case: 1.
     * @return 
     */
    @Override
    public int vertexSetCount() {
        return 1;
    }
    
  

    @Override
    public void writeGraphTo(String filePath, boolean outFmt) {
        if(outFmt)
            writeXmlGraphTo(filePath);
        else
            writePlainGraphTo(filePath);
    }
    
    public void writeXmlGraphTo(String filePath){
        FileWriter fw = null;
        BufferedWriter bw = null;
        try{
            fw = new FileWriter(filePath);
            bw = new BufferedWriter(fw);
        }catch(IOException e){
            System.err.println("(MatrixBipartiteGraph2.writeXmlGraphTo) Xml output error.");
            return;
        }
        try{
            bw.write("<document>\n");
            /* Output the entity element. */
            bw.write("<entity levels=\"2\">\n");
            for(int i=0;i<vertices.size();i++){
                    bw.write(vertices.get(i).getValue()+"\t");
            }
            bw.write("\n");
            bw.write("</entity>\n");
            /* Output the matrix. */
            bw.write("<matrix matrixLevel=\"0  0\">\n");
            for(int i=0;i<edgeWeights.length;i++){
                for(int j=0;j<edgeWeights[0].length-1;j++)
                    bw.write(edgeWeights[i][j]+"\t");
                bw.write(edgeWeights[i][edgeWeights[0].length-1]+"\n");
            }
            bw.write("</matrix>\n");
            bw.write("</document>\n");
            bw.flush();
            bw.close();
            fw.close();
        }catch(IOException e){
            System.err.println("(MatrixBipartiteGraph2.writeXmlGraphTo) Xml writing error.");
            return;
        }
    }
    
    public void writePlainGraphTo(String filePath){
        
    }

    @Override
    public void writeClusterTo(String filePath, boolean isXmlFile) {
        if(isXmlFile){
            writeXmlClusterTo(filePath);
        }
        else
            writePlainClusterTo(filePath);
    }
    
    
    public void writePlainClusterTo(String filePath){
        FileWriter fw =null;
        BufferedWriter bw = null;
        try{
            fw = new FileWriter(filePath);
            bw = new BufferedWriter(fw);
        }catch(IOException e){
            System.err.println("(MatrixBipartiteGraph2.writeClusterTo) Clusters writing error.");
            return;
        }
        try{
        for(int i=0;i<clusters.size();i++){
            for(int j=0;j<clusters.get(i).getVertices().size()-1;j++){
                bw.write(clusters.get(i).getVertices().get(j).getValue()+"\t");
            }
            bw.write(clusters.get(i).getVertices()
                    .get(clusters.get(i).getVertices().size()-1).getValue()+"\n");
        }
        bw.flush();
        bw.close();
        fw.close();
        }catch(IOException e){
            System.err.println("(MatrixBipartiteGraph2.writeClusterTo) Clusters writing error.");
            return;
        }
    }

     /**
     * This method writes the resulted clusters into the filePath.
     * @param filePath 
     */
    public void writeXmlClusterTo(String filePath){
        try{
            FileWriter fw = new FileWriter(filePath);
            BufferedWriter bw = new BufferedWriter(fw);
            
            for(int i=0;i<clusters.size();i++){
                writeSingleCluster(bw, clusters.get(i));
            }
            bw.flush();
            bw.close();
            fw.close();
            
        }catch(IOException e){
            System.err.println("(MatrixGraph.writeXmlClusterTo) Cluster writing error.");
            e.printStackTrace();
            return;
        }
    }
    
    
    /**
     * This method writes the indices of the vertices in each cluster to the given filePath.
     * This method is designed for drug-repositioning project.
     * @param filePath 
     */
    private void writeXmlPreClusterIndexMapping(String filePath){
        try{
            FileWriter fw = new FileWriter(filePath);
            BufferedWriter bw = new BufferedWriter(fw);
            
            for(int i=0;i<clusters.size();i++){
                writeSingleClusterIndexMapping(bw, clusters.get(i));
            }
            bw.flush();
            bw.close();
            fw.close();
            
        }catch(IOException e){
            System.err.println("(MatrixGraph.writeXmlClusterTo) Cluster writing error.");
            e.printStackTrace();
            return;
        }
    }
    
    /**
     * This method writes the vertex -- precluster mapping to the given file path.
     * This method is designe d for drug repositioning.
     * @param filePath 
     */
    private final void writeVertexPreClusterMapping(String filePath, String clusterPrefix){
        try{
            FileWriter fw = new FileWriter(filePath);
            BufferedWriter bw = new BufferedWriter(fw);
            for(int i=0;i<clusters.size();i++){
                writeVertexSingleClusterMapping(bw,clusters.get(i),clusterPrefix);
            }
        }catch(IOException e){
            System.err.println("(MatrixGraph.writeVertexPreClusterMapping) vertex -- precluster mapping output error.");
        }
    }
    
    
    
    
    /**
     * This method write 
     * @param bw
     * @param cluster 
     */
    public void writeSingleClusterIndexMapping(BufferedWriter bw, Cluster cluster){
         ArrayList<Vertex> clusterVertices = cluster.getVertices();
        // For test
        if(clusterVertices.isEmpty()){
            System.out.println("Empty cluster");
            return;
        }
        try{
        bw.write("<cluster  "+clusterVertices.get(0).getClustNum()+">\n");
        /* We output the cluster in separated sets. */
        
        for(int j=0;j<clusterVertices.size();j++)
                bw.write(clusterVertices.get(j).getVtxIdx()+"\t");
            
        bw.write("\n");
        bw.flush();
        bw.write("</cluster>\n");
        }catch(IOException e){
            System.err.println("(MatrixGraph.writeSingleClusterIndexMapping) Single cluster output error.");
            e.printStackTrace();
            return;
        }
    }
    
    
    /**
     * This method writes the mapping from vertex to a single cluster for the given cluster,
     * using the given BufferedWriter.
     * @param bw
     * @param cluster
     * @param clusterPrefix 
     */
    private void writeVertexSingleClusterMapping(BufferedWriter bw, Cluster cluster, String clusterPrefix){
        ArrayList<Vertex> clusterVertices = cluster.getVertices();
        if(clusterVertices.isEmpty()){
            System.out.println("Empty cluster");
            return;
        }
        try{
            for(Vertex vtx: clusterVertices){
                bw.write(vtx.getValue()+"\t"+new StringBuilder(clusterPrefix).append("_").append(cluster.getClustIdx())+"\n");
            }
            bw.flush();
        }catch(IOException e){
            System.err.println("(MatrixGraph.writeVertexSingleClusterMapping) Output error.");
            e.printStackTrace();
            return;
        }
            
    }
    /**
     * This method writes a single cluster to the given path.
     * @param filePath 
     */
    @Override
    public void writeResultInfoTo(String filePath) {
        FileWriter fw = null;
        BufferedWriter bw = null;
        try{
        fw = new FileWriter(filePath);
        bw = new BufferedWriter(fw);
        }catch(IOException e){
            System.err.println("(MatrixBipartiteGraph2.writeResultInfoTo) Result file opening error. ");
            return;
        }
        try{
        for(int i=0;i<actions.size();i++){
            bw.write(actions.get(i).getVtx1().getValue()+"\t"+actions.get(i).getVtx1().getVtxSet()+"\t"+
                    actions.get(i).getVtx2().getValue()+"\t"+actions.get(i).getVtx2().getVtxSet()+"\t"+
                    actions.get(i).getOriginalWeight()+"\n");
        }
        }catch(IOException e){
            System.err.println("(MatrixGraph.writeResultInfoTo) Writing error.");
            return;
        }
    }
    
    
    /**
     * This method writes a single cluster using the given BufferedWriter
     * @param bw 
     * @param cluster 
     */
    private void writeSingleCluster(BufferedWriter bw, Cluster cluster){
        ArrayList<Vertex> clusterVertices = cluster.getVertices();
        // For test
        if(clusterVertices.isEmpty()){
            System.out.println("Empty cluster");
            return;
        }
        try{
        bw.write("<cluster  "+clusterVertices.get(0).getClustNum()+">\n");
        /* We output the cluster in separated sets. */
        
        for(int j=0;j<clusterVertices.size();j++)
                bw.write(clusterVertices.get(j).getValue()+"\t");
            
        bw.write("\n");
        bw.flush();
        bw.write("</cluster>\n");
        }catch(IOException e){
            System.err.println("(MatrixHierGeneralGraph.writeSingleCluster) Single cluster output error.");
            e.printStackTrace();
            return;
        }
    }
    
    
    public void writerInterEwMatrix(String outFile, int idx){
        
    }
    public void writeIntraEwMatrix(String outFile, int idx){
        
    }
    public void writeDistanceMatrix(String file){
        
    }
    
}
