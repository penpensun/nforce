/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nforce.graphs;

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
import nforce.constants.nForceConstants;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 * The 2nd version, MatrixBipartiteGraph2 extends Graph2. Incorporating the function
 * computing distances is the main feature.
 * @author Peng
 */
public class BipartiteGraph extends Graph{
    //This variable stores the cost of the cluster editing, with initial value of 0
    float cost=0;
    
    /* These two variables store the sizes of the two vertex sets. */
    int set0Size =0;
    int set1Size =0;
    /*This 2-dimensional array stores the adjacency matrix */
    float[][] edgeWeights = null;
    /* This matrix stores the distances. */
    /* Distances, unlike edge weight, must be defined between two arbitrary vertices. It does not matter 
    whether the two vertices come from the same set or not, since later single-linkage and kmeans clustering
    use pairwise distances. */
    /* Note that the distance matrix is a LOWER TRIANGULAR MATRIX. */
    float[][] distances = null;
    
    /* Constructor */
    /**
     * This constructor initializes a MatrxiBipartiteGraph object.
     * 
     * @param filePath The input file path of graph.
     * @param isHeader Is the input file with header.
     */
    public BipartiteGraph(String filePath, boolean isHeader, boolean isXmlFormat) {
        if(isXmlFormat)
            try{
            readXmlGraph(filePath);
            }catch(IOException e){
                System.err.println("Bipartite graph reading xml format error: "+filePath);
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
        distances = new float[set0Size+set1Size][set0Size+set1Size];
        for(int i=0;i<set0Size+set1Size;i++)
            for(int j=0;j<set0Size+set1Size;j++)
                distances[i][j] = -1;
    }
    
    public BipartiteGraph(String filePath, boolean isHeader,float thresh){
        if(isHeader)
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
        distances = new float[set0Size+set1Size][set0Size+set1Size];
        for(int i=0;i<set0Size+set1Size;i++)
            for(int j=0;j<set0Size+set1Size;j++)
                distances[i][j] = -1;
    }
    
    
    
    @Override
    public BipartiteSubgraph bfs(Vertex Vtx) {
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
             for(Vertex vtx: nei)
             {
                 if(!marker.get(vtx.toString()))
                 {
                     marker.put(vtx.toString(),true);
                     queue.add(vtx);
                     result.add(vtx);
                 }
             }

         }
         //create a new subkpartitegraph
         BipartiteSubgraph sub = new BipartiteSubgraph(result,this);
         return sub;
    }

    /**
     * This method returns the connected components in the form of 
     * List<MatrixBipartiteSubgraphV2>.
     * 
     * @return 
     */
    @Override
    public List<BipartiteSubgraph> connectedComponents() {
        ArrayList<BipartiteSubgraph> connectecComps = new ArrayList<>();
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
            BipartiteSubgraph comp = bfs(Seed);
            connectecComps.add(comp);
            //remove all the vertex in the comp from indicatorList
            for(Vertex vtx: comp.getSubvertices()){
                indicatorList.remove(vtx);
            }
        }
        connectecComps.trimToSize();
        return connectecComps;
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
    
    /**
     * This method detracts all edge weights with threshold.
     */
    @Override
    public final void detractThresh(){
        /* First check if the thresh is detracted. */
        if(threshDetracted)
            throw new IllegalArgumentException("(MatrixBipartiteGraph2.detractThresh)"
                    + "  The threshold is already detracted.");
        /* Second check if the threshold has been set. */
        if(thresh == Double.MAX_VALUE)
            throw new IllegalArgumentException("(MatrixBipartiteGraph2.detractThresh)"
                    + "  The threshold must be set at first.");
        else{
            for(int i=0;i<set0Size;i++)
                for(int j=0;j<set1Size;j++)
                    edgeWeights[i][j] -=thresh;
        }
        threshDetracted = true;
    }
    
    /**
     * This method implements detractThresh(float thresh)
     * @param thresh 
     */
    @Override
    public final void detractThresh(float thresh){
        /* Check if the threshold has already detracted */
        if(threshDetracted)
            throw new IllegalArgumentException("(MatrixBipartiteGraph2.detractThresh)"
                    + "  The threshold is already detracted.");
        else
            setThreshold(thresh);
        detractThresh();
    }
    
    /**
     * To detract difference thresholds is not provided in MatrixBipartiteGraph2.
     * @param thresh 
     */
    @Override
    public final void detractThresh(float[] thresh){
        throw new UnsupportedOperationException("(MatrixBipartiteGraph2.detractThresh) Multiple thresholds is not supported in bipartite graph.");
    }
    
     /**
     * This method returns the distance between two given vertices. 
     * @param vtx1
     * @param vtx2
     * @return 
     */
    @Override
    public float dist(Vertex vtx1, Vertex vtx2) {
        /* First compute the indexes of vtx1 and vtx2 in the matrix. */
        int distIdxVtx1 = vtx1.getVtxSet()*set0Size+vtx1.getVtxIdx();
        int distIdxVtx2 = vtx2.getVtxSet()*set0Size+vtx2.getVtxIdx();
        if(distIdxVtx1 <distIdxVtx2)
            return distances[distIdxVtx1][distIdxVtx2];
        else return distances[distIdxVtx2][distIdxVtx1];
    }

    @Override
    public float edgeWeight(Vertex vtx1, Vertex vtx2) {
        /* Check if two vertices are of same vertex set,then the edge is not defined. */
        if(vtx1.getVtxSet() == vtx2.getVtxSet())
            return nForceConstants.NON_DEF_EDGE;
        
        /* Check the index bounds. */
        /* If the vtx1 is in set0. */
        if(vtx1.getVtxSet() == 0){
            if(vtx1.getVtxIdx()<0|| vtx1.getVtxIdx()>=set0Size)
                throw new IllegalArgumentException("(MatrixBipartiteGraph2.edgeWeight)"
                        + " vtx1 index out of bound:  "+vtx1.getValue()
                        +"  "+vtx1.getVtxSet()+"  "+vtx1.getVtxIdx());
        }
        /* If the vtx1 is in set1. */
        else if(vtx1.getVtxSet() == 1){
            if(vtx1.getVtxIdx() <0 || vtx1.getVtxIdx()>= set1Size)
                throw new IllegalArgumentException("(MatrixBipartiteGraph2.edgeWeight)"
                        +"  vtx1 index out of bound:  "+vtx1.getValue()+"  "
                        +"  "+vtx1.getVtxSet()+"  "+vtx1.getVtxIdx());
        }
        
        /*If the vtx2 is is in set0.*/
        if(vtx2.getVtxSet() == 0){
            if(vtx2.getVtxIdx()<0 || vtx2.getVtxIdx()>=set0Size)
                throw new IllegalArgumentException("(MatrixBipartiteGraph2.edgeWeight)"
                        + " vtx2 index out of bound:  "+vtx2.getValue()
                        +"  "+vtx2.getVtxSet()+"  "+vtx2.getVtxIdx()); 
        }
        
        else if(vtx2.getVtxSet() ==1){
            if(vtx2.getVtxIdx() <0 || vtx2.getVtxIdx()>= set1Size)
                throw new IllegalArgumentException("(MatrixBipartiteGraph2.edgeWeight)"
                        +"  vtx1 index out of bound:  "+vtx2.getValue()+"  "
                        +"  "+vtx2.getVtxSet()+"  "+vtx2.getVtxIdx());
        }
        
        /* Extract the edge weight. */
        if(vtx1.getVtxSet() == 0 && vtx2.getVtxSet() == 1)
            return edgeWeights[vtx1.getVtxIdx()][vtx2.getVtxIdx()];
        else
            return edgeWeights[vtx2.getVtxIdx()][vtx1.getVtxIdx()];
        
    }
    
    /**
     * Return the edge weight given two indexes.
     * Note the vtxIdx1 must be an index in set0 and vtxIdx2 must be an index in set1.
     * @param vtxIdx1
     * @param vtxIdx2
     * @return 
     */
    @Override
    public float edgeWeight(int vtxIdx1, int vtxIdx2){
        
        /* Check the index bounds. */
        if(vtxIdx1<0 || vtxIdx1 >= set0Size)
            throw new IllegalArgumentException("VtxIdx1 out of bound:  "+vtxIdx1);
        else if(vtxIdx2 <0 || vtxIdx2>=set1Size)
            throw new IllegalArgumentException("VtxIdx2 out of bound:  "+vtxIdx2);
        return edgeWeights[vtxIdx1][vtxIdx2];
    }
    
    @Override
    public final float getCost() {
        return cost;
    }

    
    /**
     * This method returns if the current action has been taken.
     * @param actIdx
     * @return 
     */
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
        if(!(graph instanceof BipartiteGraph))
            return false;
        BipartiteGraph converted = (BipartiteGraph)graph;
        /* Check set0Size, set1Size. */
        if(this.set0Size != converted.set0Size)
            return false;
        if(this.set1Size != converted.set1Size)
            return false;
        /* Check edgeWeights. */
        if(this.edgeWeights.length != converted.edgeWeights.length)
            return false;
        if(this.edgeWeights[0].length != converted.edgeWeights[0].length)
            return false;
        for(int i=0;i<edgeWeights.length;i++)
            for(int j=0;j<edgeWeights[0].length;j++)
                if(this.edgeWeights[i][j] != converted.edgeWeights[i][j])
                    return false;
        
        /* Check the vertices. */
        if(this.vertices.size() != converted.vertices.size())
            return false;
        for(int i=0;i<vertices.size();i++)
            if( !vertices.get(i).equals(converted.vertices.get(i)))
                return false;
        return true;
    }
    
    /**
     * The method returns the nei of the given vertex.
     * precondition: Threshold detracted.
     * @param vtx
     * @return 
     */
    @Override
    public ArrayList<Vertex> neighbours(Vertex currentVtx){
        /* Here we add a pre-condition: Check if the threshold is detracted. */
        if(!threshDetracted){
            System.out.println
                    ("(MatrixBipartiteGraph2.neighbours) Threshold must be detracted first.");
            detractThresh();
        }
        ArrayList<Vertex> neighbours = new ArrayList<>();
        for(Vertex vtx: vertices){
            /* If they are from the same set, then it's impossible there's an edge
             * between them.*/
            if(currentVtx.getVtxSet() ==
                    vtx.getVtxSet())
                continue;
            else
                if(edgeWeight(currentVtx,vtx)>0)
                    neighbours.add(vtx);       
        }
        //neighbours.trimToSize();
        /* If no neighbour is found, then return null. */
        if(neighbours.isEmpty())
            return null;
        return neighbours;
        
    }
    

    /**
     * 
     * @param vtx1
     * @param vtx2
     * @param isEdgeInsertion
     * @return 
     */
    @Override
    public boolean pushAction(Vertex vtx1, Vertex vtx2) {
        /* First check if thresh is already detracted, if not we have 
         to first detract threshold. */
        if(!threshDetracted)
            detractThresh();
        
        float ew = edgeWeight(vtx1, vtx2);
        if(ew == 0)
            throw new IllegalArgumentException("There is a null-edge between vtx1 and vtx2");
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
     * This method pushes one vertex into the graph. 
     * Currently it's a private method, avoiding any insertion of vertices after
     * the graph is initialized.
     * @param vtx
     * @return The index of the pushed vertex.
     */
    private int pushVertex(String vertexValue, int vertexSet ){
        Vertex vtx = new Vertex(vertexValue,vertexSet,-1);
        //U. Note: we should add a new meothod, searching a vertex in a given
        //vertex list (arrayList or Linkedlist). This method should be a static
        //method
        int Idx = ((ArrayList<Vertex>)vertices).indexOf(vtx);
        if(Idx == -1){
            vertices.add(vtx);
            //set the index of the new vertex according to its vertex set
            if(vertexSet == 0){
                vtx.setVtxIdx(set0Size);
                set0Size++;
                return set0Size-1;
            }
            else if(vertexSet ==1){
                vtx.setVtxIdx(set1Size);
                set1Size++;
                return set1Size-1;
            }
            else
                throw new IllegalArgumentException("Vertex lvel can only be 0 or 1");
        }
        else
            return ((ArrayList<Vertex>)vertices).get(Idx).getVtxIdx();
    }
  

    /**
    * This method reads file into MatrixBipartiteGraph.
    * @param filePath
    * @throws IOException 
    */
    @Override
    public final void readGraph(String filePath) throws IOException {
        //Init the vertices and the actions
        vertices = new ArrayList<>();
        actions = new ArrayList<>();
        //Read the input file for the first time, to init the 
        //EdgeWeight matrix
        FileReader fr = new FileReader(filePath);
        BufferedReader br= new BufferedReader(fr);
        String line;
        //no header in the file
        //Size1, Size2 are two variables just to init EdgeWeight matrix.
        //When the input file is read for the first time, we do not create any vertex or add any vertex into
        //arraylist vertices.
        int Size1 = 0;
        int Size2 = 0;
        // Here, we used 2 hashset to count how many vertices we have
        HashSet<String> Set1 = new HashSet<>();
        HashSet<String> Set2 = new HashSet<>();
        
        while((line = br.readLine())!= null)
        {
            line = line.trim();
            if(line.isEmpty())
                continue;
            String[] split  = line.split("\t");
            //if there is only vertex, no other link
            if(split.length ==2){
                int setIdx = -1;
                /* Catch the NumberFormatException. */
                try{
                    setIdx = Integer.parseInt(split[1]);
                }catch(NumberFormatException e){
                    System.out.println("(readGraph) Invalid number format for vertex set index: "+  split[1]);
                    edgeWeights = null;
                    return;
                }
                
                if(setIdx == 0 )
                    Set1.add(String.copyValueOf(split[0].toCharArray()));
                else if(setIdx == 1)
                    Set2.add(String.copyValueOf(split[0].toCharArray()));
                else{
                    System.out.println("(readGraph) Error: For bipartite graph, level number can only be 0 or 1");
                    edgeWeights = null;
                    return;
                }
                
            }
            else{
                int set0Idx =-1,set1Idx = -1;
                /* Catch the NumberFormatException. */
                try{
                    set0Idx = Integer.parseInt(split[1]);
                }catch(NumberFormatException e){
                    System.out.println("(readGraph) Invalid number format for vertex set index: "+split[1]);
                    System.exit(0);
                }
                
                try{
                    set1Idx = Integer.parseInt(split[3]);
                }catch(NumberFormatException e){
                    System.out.println("(readGraph) Invalid number format for vertex set index:  "+split[3]);
                    System.exit(0);
                }
                if(set0Idx == 0)
                    Set1.add(String.copyValueOf(split[0].toCharArray()));
                else if(Integer.parseInt(split[1]) == 1)
                    Set2.add(String.copyValueOf(split[0].toCharArray()));
                else{
                    System.out.println("(readGraph) Error: For bipartite graph, level number can only be 0 or 1");
                    System.exit(0);
                }

                if(Integer.parseInt(split[3]) == 0)
                    Set1.add(String.copyValueOf(split[2].toCharArray()));
                else if(Integer.parseInt(split[3]) == 1)
                    Set2.add(String.copyValueOf(split[2].toCharArray()));
                else{
                    System.out.println("(readGraph) Error: For bipartite graph, level number can only be 0 or 1");
                    System.exit(0);
                }
            }
        }
        br.close();
        fr.close();
        
        Size1 = Set1.size();
        Size2 = Set2.size();
        Set1 = null;
        Set2 = null;
        //U. note: in the new version, we are to implement an npartitegraph based on matrix.
        //Edges between two arbitrarily different vertex sets are permitted.
        //Thus, if we have n vertex sets, we have n(n-1)/2 edge weight matrices
        edgeWeights = new float[Size1][Size2];
        /* Set the init values of edgeWeights to NaN. */
        for(int i=0;i<Size1;i++)
            for(int j=0;j<Size2;j++)
                edgeWeights[i][j] = Float.NaN;
        //Init the values;
        
        //re-read the file, create vertices and init edge weights.
        fr = new FileReader(filePath);
        br = new BufferedReader(fr);
        while((line = br.readLine())!= null)
        {
            line = line.trim();
            if(line.isEmpty())
                continue;
            String[] split = line.split("\t");
            //if there is only vertex, no other link
            if(split.length ==2)
            {
                String vtxName = String.copyValueOf(split[0].toCharArray());
                int vtxLvl = -1;
                /* Catch the NumberFormatException. */
                try{
                    vtxLvl = Integer.parseInt(String.copyValueOf(split[1].toCharArray()));
                }catch(NumberFormatException e){
                    System.out.println("(readGraph) Invalid format for vertex set index:  "+split[1]);
                    System.exit(0);
                }
                
                //Push vertex
                pushVertex(vtxName,vtxLvl);
            }
            else{
                String Vtx1Name = String.copyValueOf(split[0].toCharArray());
                //int Vtx1Lvl = Integer.parseInt(String.copyValueOf(splits2[1].toCharArray()));
                int vtx1Lvl = -1;
                /* Catch the NumberFormatException. */
                try{
                    vtx1Lvl = Integer.parseInt(String.copyValueOf(split[1].toCharArray()));
                }catch(NumberFormatException e){
                    System.out.println("(readGraph) Invalid format for vertex set index:  "+split[0]);
                    System.exit(0);
                }
                
                String Vtx2Name = String.copyValueOf(split[2].toCharArray());
                //int Vtx2Lvl = Integer.parseInt(String.copyValueOf(splits2[3].toCharArray()));
                int vtx2Lvl = -1;
                /* Catch the NumberFormatException. */
                try{
                    vtx2Lvl = Integer.parseInt(String.copyValueOf(split[3].toCharArray()));
                }catch(NumberFormatException e){
                    System.out.println("(readGraph) Invalid format for vertex set index:  "+split[3]);
                    System.exit(0);
                }

                //U. note: here it's not proper to use addVertex() just to search for an existing vertex.
                //Thus, we should add a new method findVertex(). maybe a static method in Class Vertex, or a normal
                //method in the new NpartiteGraph class
                int Idx1 = pushVertex(Vtx1Name,vtx1Lvl);
                int Idx2 = pushVertex(Vtx2Name,vtx2Lvl);
                float ew = Float.parseFloat(String.copyValueOf(split[4].toCharArray()));
                
                //U. note: here we need a method to assign edgeweight.
                if(vtx1Lvl == 0 && vtx2Lvl ==1)
                    try{
                        edgeWeights[Idx1][Idx2] = ew;
                    }catch(ArrayIndexOutOfBoundsException e){
                        System.out.println("(readGraph) Vertex index is out of bound: "+Idx1+"  "+Idx2);
                        System.exit(0);
                    }
                else if(vtx1Lvl ==1 && vtx2Lvl ==0)
                    try{
                        edgeWeights[Idx2][Idx1] = ew;
                    }catch(ArrayIndexOutOfBoundsException e){
                        System.out.println("(readGraph) Vertex index is out of bound:  "+Idx1+"  "+Idx2);
                    }
                else throw new IllegalArgumentException("Vertex lvl can only be 0 or 1");
            }
        }
        br.close();
        fr.close();
        //Init the clusters.
        clusters = new ArrayList<>();
    }
    
    /**
     * This methdo reads a graph from an input file with header, i.e. the first row 
     * of the row gives the number of vertices in the graph in each vertex set.
     * @param filePath 
     */
    public final void readGraphWithHeader(String filePath) throws IOException{
        /* These two variables store the number of vertices given by the graph header.*/
        int size0,size1;
        //Init the vertices and the actions
        vertices = new ArrayList<>();
        actions = new ArrayList<>();
        /* Init the objects to read graphs. */
        FileReader fr = new FileReader(filePath);
        BufferedReader br= new BufferedReader(fr);
        String line;
        
        /* Read the first line to obtain the numbers of vertices in two vertex sets. */
        line = br.readLine();
        /* Assign the set sizes. */
        String[] splits1 = line.split("\\s+");
        /* Check the length of splits1 array. If longer than 2, then gives error 
         * and aborts the programme. */
        if(splits1.length != 2){
            System.out.println("(readGraphWithHeader) For a bipartite graph, header must only contain 2 numbers.");
            System.exit(0);
        }
        try{
            size0 = Integer.parseInt(splits1[0]);
            size1 = Integer.parseInt(splits1[1]);
            /* Init the float array for edge weights. */
            edgeWeights = new float[size0][size1];
        }catch(NumberFormatException e){
            System.out.println("(readGraphWithHeader) The header of the input graph contains "
                    +"invalid number formats.");
            return;
        }
        
        /* Set the init values of edge weight matrix to Double.NaN. */
        for(int i=0;i<size0;i++)
            for(int j=0;j<size1;j++)
                edgeWeights[i][j] = Float.NaN;

        /* Read the input file. */
        while((line = br.readLine())!= null){
            line = line.trim();
            if(line.isEmpty())
                continue;
            String[] splits2 = line.split("\t");
            //if there is only vertex, no other link
            if(splits2.length ==2)
            {
                String vtxName = String.copyValueOf(splits2[0].toCharArray());
                int vtxLvl = -1;
                /* Catch the NumberFormatException. */
                try{
                    vtxLvl = Integer.parseInt(String.copyValueOf(splits2[1].toCharArray()));
                }catch(NumberFormatException e){
                    System.out.println("(readGraphWithHeader) Invalid format for vertex set index:  "+splits2[1]);
                    System.exit(0);
                }
                
                //Push vertex
                pushVertex(vtxName,vtxLvl);
            }
            else{
                String vtx1Name = String.copyValueOf(splits2[0].toCharArray());
                //int Vtx1Lvl = Integer.parseInt(String.copyValueOf(splits2[1].toCharArray()));
                int vtx1Lvl = Integer.parseInt(String.copyValueOf(splits2[1].toCharArray()));
                /* Check if vtx1Lvl is equal to 0 or 1.*/
                if(vtx1Lvl != 0 && vtx1Lvl != 1){
                    System.out.println("(readGraphWithHeader) Invalid vtx1Lv1. ");
                    return;
                }
                    
                String vtx2Name = String.copyValueOf(splits2[2].toCharArray());
                //int Vtx2Lvl = Integer.parseInt(String.copyValueOf(splits2[3].toCharArray()));
                int vtx2Lvl = Integer.parseInt(String.copyValueOf(splits2[3].toCharArray()));
                if(vtx2Lvl != 0 && vtx2Lvl != 1){
                    System.out.println("(readGraphWithHeader) Invalid vtx2Lvl.");
                    return;
                }
                //U. note: here it's not proper to use addVertex() just to search for an existing vertex.
                //Thus, we should add a new method findVertex(). maybe a static method in Class Vertex, or a normal
                //method in the new NpartiteGraph class
                int idx1 = pushVertex(vtx1Name,vtx1Lvl);
                /* Here we must check the index, whether the index is out of the bound. */
                if(vtx1Lvl == 0 && idx1 >= size0){
                    System.out.println("(readGraphWithHeader) idx1 out of the bound of set0Size.");
                    return;
                }
                else if(vtx1Lvl == 1 && idx1>=size1){
                    System.out.println("(readGraphWithHeader) idx1 out of the bound of set1Size.");
                    return;
                }
                    
                int idx2 = pushVertex(vtx2Name,vtx2Lvl);
                /* Here we must check the index, whether the index if out of the bound. */
                 if(vtx2Lvl == 0 && idx2 >= size0){
                    System.out.println("(readGraphWithHeader) idx2 out of the bound of set0Size.");
                    return;
                }
                else if(vtx2Lvl == 1 && idx2 >= size1){
                    System.out.println("(readGraphWithHeader) idx2 out of the bound of set1Size.");
                    return;
                }
                 
                float ew = Float.parseFloat(String.copyValueOf(splits2[4].toCharArray()));
               
                //U. note: here we need a method to assign edgeweight.
                if(vtx1Lvl == 0 && vtx2Lvl ==1)
                    edgeWeights[idx1][idx2] = ew;
                else if(vtx1Lvl ==1 && vtx2Lvl ==0)
                    edgeWeights[idx2][idx1] = ew;
                else throw new IllegalArgumentException("(readGraphWithHeader) Vertex lvl can only be 0 or 1");
            }
        }
        /* Check if the sizes given in the header match the real sizes obtained from the file. */
        if(size0 != set0Size ||
                size1 != set1Size){
            System.out.println("(MatrixBipartiteGraph2.readWithHeader) Sizes in the header"
                    + " do not match the real sizes: ");
            System.out.println("Header sizes:  "+size0+"  "+size1);
            System.out.println("Real sizes:  "+set0Size+"  "+set1Size);
            System.exit(0);
        }
        br.close();
        fr.close();
        //Init the clusters.
        clusters = new ArrayList<>();
    }

    /**
     * This method restores the threshold from the edge weights.
     */
    @Override
    public void restoreThresh(){
        /* First check if the thresh is detracted, if not then throw an exception. */
        if(!threshDetracted)
            throw new IllegalArgumentException("(MatrixBipartiteGraph2.restoreThresh)"
                    + "  The threshold is not yet detracted.");
        /* Second check if the threshold has been set. */
        if(thresh == Double.MAX_VALUE)
            throw new IllegalArgumentException("(MatrixBipartiteGraph2.detractThresh)"
                    + "  The threshold must be set at first.");
        else{
            for(int i=0;i<set0Size;i++)
                for(int j=0;j<set1Size;j++)
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
    
    /**
     * This method reads the graph in xml format.
     * @param filePath 
     */
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
        
        /* Get how many levels are there in this graph.*/
        String levelStr = entity.getAttributeValue("levels"); /* Get how many sets are there in the graph. */
        /* levelStr is required. */
        if(levelStr == null){
            /* If no "levels" attribute is defined, then an exception is thrown. */
            throw new IllegalArgumentException("(biforce.graphs.MatrixHierGeneralGraph.readGraphInXml) Attribute \"levels\" must be given.");
        }
        
        /* Check if there is an external file for the node names.*/
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
        
        edgeWeights = new float[set0Size][set1Size];
        /* Init all matrices. */
        /* We have setSizes.length intraEdgeWeightMatrix. */
        for(int i=0;i<edgeWeights.length;i++)
            for(int j=0;j<edgeWeights[0].length;j++)
                edgeWeights[i][j] = Float.NaN;
        /* Read the edge weights from the xml input file. */
        ArrayList<Element> matrixElementList = new ArrayList<>(docRoot.getChildren("matrix"));
        /* First check the number of elements in matrixElementList, if not equal to 2*setSizes.length-1. */
        if(matrixElementList.size() != 1)
            throw new IllegalArgumentException("(biforce.graphs.MatrixHierGeneralGraph.readGraphInXml) The number of matrices is wrong:  "+matrixElementList.size());
        
        /* 2. Assign the edge weights. */
        for(Element matrix: matrixElementList){
            /* First check where is this matrix. */
            String matrixLevel = matrix.getAttributeValue("matrixLevel");
            if(matrixLevel == null) /* The matrixLevel attribute is required. */
                throw new IllegalArgumentException("(biforce.graphs.MatrixHierGeneralGraph.readGraphInXml) The matrixLevel attribute is required.");
            String[] matrixLevelSplits = matrixLevel.split("\\s+");
            /* If string matrixLevel cannot be splitted into two splits, then it must be wrong. */
            if(matrixLevelSplits.length !=2)
                throw new IllegalArgumentException("(biforce.graphs.MatrixHierGeneralGraph.readGraphInXml) The attribute matrixLevel error:  "+matrixLevel);
            /* Get the two levels. */
            int matrixLevel1=-1,matrixLevel2=-1;
            try{
                matrixLevel1= Integer.parseInt(String.copyValueOf(matrixLevelSplits[0].toCharArray()));
                matrixLevel2= Integer.parseInt(String.copyValueOf(matrixLevelSplits[1].toCharArray()));
            }catch(NumberFormatException e){
                throw new IllegalArgumentException("(biforce.graphs.MatrixHierGeneralGraph.readGraphInXml) The attribute matrixLevel error:  "+matrixLevel);
            }
            /* Check if the matrix is a intra- or inter edge weight matrix. */
            if(matrixLevel1 == matrixLevel2){ /* Intra matrix. */
                
            }
            else{ 
                /* For inter-matrix. */
                String matrixContent = matrix.getContent(0).getValue().trim();
                /* Check if the matrix is stored in an external file.*/
                String hasExternMatrixFile=matrix.getAttributeValue("externalFile");
                if(hasExternMatrixFile == null|| hasExternMatrixFile.equalsIgnoreCase("false")){
                    XmlInputParser parser = new XmlInputParser();
                    parser.parseInterMatrixString(matrixContent, this);
                }
                else if(hasExternMatrixFile.equalsIgnoreCase("true")){
                    XmlInputParser parser = new XmlInputParser();
                    parser.parseInterMatrixFile(matrixContent, this);
                }
                else
                    throw new IllegalStateException("(biforce.graphs.MatrixHierGeneralGraph.readGraphInXml))Illegal attribute of \"externalFile\": "+hasExternMatrixFile);
                        
            }
        }
        clusters = new ArrayList<>();
        actions = new ArrayList<>();
    }
    
    @Override
    public void setEdgeWeight(Vertex vtx1, Vertex vtx2, float edgeWeight) {
        if(vtx1.getVtxSet() == vtx2.getVtxSet()){
            throw new IllegalArgumentException("(MatrixBipartiteGraph2) Vtx1 and vtx2"
                    + "must not come from the same vertex set.");
        }
        /* If vtx1 comes from vertex 1, then we swap them. */
        if(vtx1.getVtxSet() ==1){
            Vertex swap = vtx1;
            vtx1 = vtx2;
            vtx2 = swap;
        }
        /* Check the index bound. */
        if(vtx1.getVtxIdx() >= set0Size || vtx1.getVtxIdx()<0 ||
                vtx2.getVtxIdx() >= set1Size || vtx2.getVtxIdx() <0)
            throw new IllegalArgumentException("(MatrixBipartiteGraph.setEdgeWeight)"
                    + " Index out of bound. vtxIdx1:  "+vtx1.getVtxIdx()+"  vtxIdx2:  "+vtx2.getVtxIdx());
        if(threshDetracted){
            edgeWeights[vtx1.getVtxIdx()][vtx2.getVtxIdx()] = edgeWeight-thresh;
        }
        else
            edgeWeights[vtx1.getVtxIdx()][vtx2.getVtxIdx()] = edgeWeight;
    }

    /**
     * This method takes the action. 
     * @param idx
     * @return 
     */
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

    /**
     * Take all actions in the actions arrayList.
     * @return 
     */
    @Override
    public boolean takeActions() {
        boolean flag = true;
        for(int i=0;i<actions.size();i++){
            if(!takeAction(i))
                flag = false;
        }
        return flag;
    }

    

    /**
     * This method updates the distances.
     */
    @Override
    public void updateDist() {
        for(int i=0;i<vertices.size();i++)
            for(int j=i+1;j<vertices.size();j++){
                Vertex vtx1 = vertices.get(i);
                Vertex vtx2 = vertices.get(j);
                
                /* Compute the index of the vertices in distance matrix. */
                int distIdxVtx1 = set0Size*vtx1.getVtxSet()+vtx1.getVtxIdx();
                int distIdxVtx2 = set0Size*vtx2.getVtxSet()+vtx2.getVtxIdx();
                /* Keep distances as a lower triangular matrix. */
                if(distIdxVtx1 <distIdxVtx2)
                    distances[distIdxVtx1][distIdxVtx2] = euclidDist(vtx1,vtx2);
                else 
                    distances[distIdxVtx2][distIdxVtx1] = euclidDist(vtx1,vtx2);
            }
    }
    
    /**
     * This method updates the positions.
     * @param dispVector 
     */
    @Override
    public void updatePos(float[][] dispVector){
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

    @Override
    public int vertexSetCount() {
        return 2;
    }

    @Override
    public void writeGraphTo(String filePath, boolean outFmt) {
        /* Check the out put file. */
        if(filePath == null)
            throw new IllegalArgumentException("(MatrixBipartiteGraph2.writeGraphTo) The output path cannot be null.");
        if(!outFmt)
            writePlainGraphTo(filePath);
        else writeXmlGraphTo(filePath);
    }
    
    /**
     * This method writes the graph as xml format.
     * @param filePath 
     */
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
                if(vertices.get(i).getVtxSet() == 0)
                    bw.write(vertices.get(i).getValue()+"\t");
            }
            bw.write("\n");
            for(int i=0;i<vertices.size();i++){
                if(vertices.get(i).getVtxSet() ==1)
                    bw.write(vertices.get(i).getValue()+"\t");
            }
            bw.write("\n");
            bw.write("</entity>\n");
            /* Output the matrix. */
            bw.write("<matrix matrixLevel=\"0  1\">\n");
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
    
    /**
     * This method writes to the given filePath the graph in plain format with header.
     * @param filePath 
     */
    public void writePlainGraphTo(String filePath){
        FileWriter fw = null;
        BufferedWriter bw= null;
        try{
        fw = new FileWriter(filePath);
        bw = new BufferedWriter(fw);
        }catch(IOException e){
            System.err.println("(MatrixBipartiteGraph2.writePlainGraphTo) File writing error.");
            return;
        }
        try{
            /* First output the header. */
            bw.write(set0Size+"\t"+set1Size+"\n");
            /* Then output the matrix. */
            for(int i=0;i<vertices.size();i++)
                for(int j=i+1;j<vertices.size();j++){
                    if(vertices.get(i).getVtxSet() == 
                            vertices.get(j).getVtxSet())
                        continue;
                    else{
                        bw.write(vertices.get(i).getValue()+"\t"+vertices.get(i).getVtxSet()+"\t"+
                                vertices.get(j).getValue()+"\t"+vertices.get(j).getVtxSet()+
                                        "\t"+edgeWeight(vertices.get(i),vertices.get(j))+"\n");
                    }
                }
            bw.flush();
            bw.close();
            fw.close();
        }catch(IOException e){
            System.err.println("(MatrixBipartiteGraph2.writePlainGraphTo) Writing error.");
            return;
        }
    }

    /**
     * This method writes the result clusters to a given file.
     * @param filePath 
     */
    @Override
    public void writeClusterTo(String filePath, boolean isXmlFile) {
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
     * This method writes the actions stored in the graph.
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
            System.err.println("(MatrixBipartiteGraph2.writeResultInfoTo) Result file opening error: "+filePath);
            return;
        }
        try{
        for(int i=0;i<actions.size();i++){
            bw.write(actions.get(i).getVtx1().getValue()+"\t"+actions.get(i).getVtx1().getVtxSet()+"\t"+
                    actions.get(i).getVtx2().getValue()+"\t"+actions.get(i).getVtx2().getVtxSet()+"\t"+
                    actions.get(i).getOriginalWeight()+"\n");
        }
        }catch(IOException e){
            System.err.println("(MatrixBiPartiteGraph2.writeResultInfoTo) Writing error.");
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
