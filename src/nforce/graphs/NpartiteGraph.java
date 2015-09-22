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
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import nforce.constants.nForceConstants;

/**
 * This clas is the second version of npartite graph.
 * @author penpen926
 */
public class NpartiteGraph extends Graph{
    protected float cost = 0; /* This stores the editing cost.*/
    protected int[] setSizes = null;
    /* Here since we have different sets of edges between different vertex sets,
     * we got to have more than one edge weights matrices.*/
    protected ArrayList<float[][]> intraEdgeWeights = null;
    protected ArrayList<float[][]> interEdgeWeights = null;
    /* This matrix stores the distances. */
    /* Distances, unlike edge weight, must be defined between two arbitrary vertices. It does not matter 
    whether the two vertices come from the same set or not, since later single-linkage and kmeans clustering
    use pairwise distances. */
    /* Note that the distance matrix is a LOWER TRIANGULAR MATRIX. */
    /* In MatrixHierNpartiteGraph, since we have distances between different vertices, we have 
    more than one distances.*/
    //ArrayList<float[][]> distances = null;
    /* 2nd version of distances, for test. */
    protected float[][] distMatrix = null;
    
    /**
     * The constructor.
     * @param filePath
     * @param isHeader
     * @param isXmlFile 
     */
    public NpartiteGraph(String filePath, boolean isHeader, boolean isXmlFile){
        /* Init the vertices arrayList. */
        vertices = new ArrayList<>();
        /* Init the distance arrayList. */
        //distances = new ArrayList<>();
        /* Init the action arrayList. */
        actions = new ArrayList<>();
        /* If the input is in xml format. */
        if(isXmlFile)
            try{
                readXmlGraph(filePath);
            }catch(IOException e){
                System.out.println("(MatrixGeneralNpartiteGraph.constructor) "
                        + " MatrixGeneralNpartiteGraph readGraph in xml failed:"
                        + " "+filePath);
                return;
            }
        /* If the input file is with header.*/
        else if(isHeader)
            try{
                readGraphWithHeader(filePath);
            }catch(IOException e){
                System.out.println("(MatrixGeneralNpartiteGraph.constructor)"
                        + " MatrixGeneralNpartiteGraph readGraphWithHeader failed:"
                        + " "+filePath);
            }
        
        else
           try{
                readGraph(filePath);
            }catch(IOException e){
                System.out.println("(MatrixGeneralNpartiteGraph.constructor)"
                        + " MatrixGeneralNpartiteGraph readGraph no header failed:"
                        + " "+filePath);
            } 
        /* 2nd version of the distance matrix. Now the first version of MatrixHierGeneraGraph */
        distMatrix = new float[vertices.size()][vertices.size()];
        for(int i=0;i<distMatrix.length;i++)
            for(int j=0;j<distMatrix[0].length;j++)
                distMatrix[i][j] = Float.NaN;
    }
    
    /**
     * Constructor
     * @param vertices
     * @param actions
     * @param clusters
     * @param setSizes
     * @param intraEdgeWeights
     * @param interEdgeWeights 
     */
    public NpartiteGraph(ArrayList<Vertex> vertices, ArrayList<Action> actions, 
            ArrayList<Cluster> clusters, 
            int[] setSizes,
            ArrayList<float[][]> intraEdgeWeights, 
            ArrayList<float[][]> interEdgeWeights){
        this.vertices = vertices;
        this.actions = actions;
        this.setSizes = setSizes;
        this.intraEdgeWeights=  intraEdgeWeights;
        this.interEdgeWeights = interEdgeWeights;
    }
    
    /**
     * This method performs breadth-first search.
     * @param Vtx The start vertex.
     * @return 
     */
    @Override
    public NpartiteSubgraph bfs(Vertex Vtx){
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
         /* Create a new subkpartitegraph. */
         NpartiteSubgraph sub = new NpartiteSubgraph(result,this);
         return sub;
    }
    
    /**
     * This method returns all connected components.
     * @return 
     */
    @Override
    public ArrayList<NpartiteSubgraph> connectedComponents() {
        ArrayList<NpartiteSubgraph> connectecComps = new ArrayList<>();
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
            NpartiteSubgraph comp = bfs(Seed);
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
     * This method detracts all edge weights in matrices.
     */
    @Override
    public final void detractThresh() {
        /* Detract the threshold from inter-edgeweights. */
        for(int i=0;i<interEdgeWeights.size();i++){
            float[][] weights = interEdgeWeights.get(i);
            for(int j=0;j<weights.length;j++)
                for(int k=0;k<weights[0].length;k++)
                    weights[j][k] -=thresh;
        }
        /* Detract the threshold from the intra-edgeweights.*/
        for(int i=0;i<intraEdgeWeights.size();i++){
            float[][] weights = intraEdgeWeights.get(i);
            for(int j=0;j<weights.length;j++)
                for(int k=0;k<weights[0].length;k++)
                    weights[j][k] -= thresh;
        }
        this.threshDetracted = true;
    }
    
    @Override
    public final void detractThresh(float thresh) {
        /* Check if the threshold has already detracted */
        if(threshDetracted)
            throw new IllegalArgumentException("(MatrixHierNpartiteGraph.detractThresh)"
                    + "  The threshold is already detracted.");
        else
            setThreshold(thresh);
        detractThresh();
    }
    
    @Override
    public final void detractThresh(float[] thresh){
        if(threshDetracted)
            throw new IllegalArgumentException("(MatrixHierNpartiteGraph.detractThresh) The threshold is already detracted.");
        else{
            // Check the length.
            if(thresh.length != interEdgeWeights.size()+intraEdgeWeights.size())
                throw new IllegalArgumentException("(MatrixHierNpartiteGraph.detractThresh) The length of the threshold array does not fit the graph:  "+thresh.length+"  "+interEdgeWeights.size()+intraEdgeWeights.size());
            
            for(int i=0;i<thresh.length;i++){
                if(i%2 == 0){
                    // The threshold for an intra edge weight matrix.
                    float[][] weights = intraEdgeWeights.get(i/2);
                    for(int j=0;j<weights.length;j++)
                        for(int k=0;k<weights[0].length;k++)
                            weights[j][k] -=thresh[i];
                }
                else{
                    //The threshold for an inter edge weight matrix.
                    float[][] weights = interEdgeWeights.get(i/2);
                    for(int j=0;j<weights.length;j++)
                        for(int k=0;k<weights[0].length;k++)
                            weights[j][k] -=thresh[i];
                }
            }
        }
        this.threshDetracted = true;
    }
    
    /**
     * This method gets the distance between two vertices. 
     * @param vtx1
     * @param vtx2
     * @return 
     */
    @Override
    public float dist(Vertex vtx1, Vertex vtx2) {
        /* First check if the distances between the two vertices are defined. */
        /*
        if( vtx1.getVtxSet() - vtx2.getVtxSet() != 1 &&
                vtx1.getVtxSet() - vtx2.getVtxSet() != -1)
            return Double.NaN;
        int minVtxSet = Math.min(vtx1.getVtxSet(),vtx2.getVtxSet());
        if(minVtxSet == vtx1.getVtxSet())
            return distances.get(minVtxSet)[vtx1.getVtxIdx()][vtx2.getVtxSet()];
        else
            return distances.get(minVtxSet)[vtx2.getVtxIdx()][vtx1.getVtxSet()];
        */
        /* 2nd version of distances. */
        return distMatrix[vtx1.getDistIdx()][vtx2.getDistIdx()];
    }
    
    /**
     * Get the edge weight.
     * @param vtx1
     * @param vtx2
     * @return 
     */
    @Override
    public float edgeWeight(Vertex vtx1, Vertex vtx2) {
        /* Check if it is an intra-edge. */
        if(vtx1.getVtxSet() == vtx2.getVtxSet())
            /* First get the edge weight matrix and then get the right edge weight. */
            return intraEdgeWeights.get(vtx1.getVtxSet())
                    [vtx1.getVtxIdx()][vtx2.getVtxIdx()];
        /* Check if it  is an inter-edge.  */
        if(vtx1.getVtxSet() - vtx2.getVtxSet() == 1 ||
                vtx1.getVtxSet() - vtx2.getVtxSet() == -1){
            int minVtxLvl = Math.min(vtx1.getVtxSet(),vtx2.getVtxSet());
            if(minVtxLvl == vtx1.getVtxSet())
                return interEdgeWeights.get(minVtxLvl)[vtx1.getVtxIdx()][vtx2.getVtxIdx()];
            else
                return interEdgeWeights.get(minVtxLvl)[vtx2.getVtxIdx()][vtx1.getVtxIdx()];
        }
        else if((vtx1.getVtxSet() ==0 && vtx2.getVtxSet() == setSizes.length-1)||
                (vtx2.getVtxSet() ==0 && vtx1.getVtxSet() == setSizes.length-1)){
            int maxVtxLvl = Math.max(vtx1.getVtxSet(), vtx2.getVtxSet());
            if(maxVtxLvl == vtx1.getVtxSet())
                return interEdgeWeights.get(maxVtxLvl)[vtx2.getVtxIdx()][vtx1.getVtxIdx()];
            else
                return interEdgeWeights.get(maxVtxLvl)[vtx1.getVtxIdx()][vtx2.getVtxIdx()];
        }
        /* If it is not an intra-edge nor is it an inter-edge, then we return NaN for cross edges. */
        return Float.NaN;
    }
    
    @Override
    public float edgeWeight(int vtxIdx1, int vtxIdx2) {
        throw new UnsupportedOperationException("This edgeWeight(int, int) is not supported in MatrixGeneralNpartiteGraph.");
    }
    
    /**
     * Return the edge weight matrix
     * 
     * @question return final ?
     * @param i
     * @return 
     */
    public float[][] interEdgeWeightMatrix(int i){
        /* First check the index. */
        if( i<0 || i>= interEdgeWeights.size()){
            throw new IllegalArgumentException("(MatrixGeneralNpartiteGraph.interEdgeWeighMatrix) Index out of bound:  "+
                    i);
        }
        return interEdgeWeights.get(i);   
    }
    
    
    public float[][] intraEdgeWeightMatrix(int i){
        if( i<0 || i>= interEdgeWeights.size()){
            throw new IllegalArgumentException("(MatrixGeneralNpartiteGraph.interEdgeWeighMatrix) Index out of bound:  "+
                    i);
        }
        return interEdgeWeights.get(i);   
    }
    
    /**
     * This method computes the euclidean distances between two vertices.
     * @param vtx1
     * @param vtx2
     * @return 
     */
    private float euclidDist(Vertex vtx1,Vertex vtx2){
        if(vtx1.getCoords() == null ||
                vtx2.getCoords() == null)
            throw new IllegalArgumentException ("(MatrixHierNpartiteGraph.euclidDist) Null coordinates:  "+
                    "vtx1:  "+vtx1.getCoords().toString()+"  vtx2:  "+vtx2.getCoords().toString());
        //check if they have the same dimension
        if(vtx1.getCoords().length !=vtx2.getCoords().length )
            throw new IllegalArgumentException("Point 1 and Point 2 have different dimension");
        float Dist= 0;
        for(int i=0;i<vtx1.getCoords().length;i++){
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
    
    /**
     * Get the sizes of sets.
     * @return 
     */
    public int[] getSetSizes(){
        return setSizes;
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
        if(!(graph instanceof HierGraphWIE))
            return false;
        HierGraphWIE converted = (HierGraphWIE)(graph);
        
        /* Check the vertices. */
        if(this.vertices.size() != converted.vertices.size())
            return false;
        for(int i=0;i<vertices.size();i++)
            if(!this.vertices.get(i).equals(converted.vertices.get(i)))
                return false;
        /* Check the setsizes. */
        if(this.setSizes.length != converted.setSizes.length)
            return false;
        for(int i=0;i<setSizes.length;i++)
            if(this.setSizes[i] != converted.setSizes[i])
                return false;
        
        /* Check the edge weights. */
        /* InterEdgeWeights. */
        if(interEdgeWeights.size() != converted.interEdgeWeights.size())
            return false;
        /* Check every matrix in interEdgeWeights. */
        for(int i=0;i<interEdgeWeights.size();i++)
            if( !isMatrixEqual(interEdgeWeights.get(i),converted.interEdgeWeights.get(i)))
                return false;
        /* IntraEdgeWeights. */
        if(intraEdgeWeights.size()!= converted.intraEdgeWeights.size())
            return false;
        /* Check very matri xin the intraEdgeWeights. */
        for(int j=0;j<intraEdgeWeights.size();j++)
            if(!isMatrixEqual(intraEdgeWeights.get(j),converted.intraEdgeWeights.get(j)))
                return false;
        return true;
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
                    ("(MatrixHierNpartiteGraph.neighbours) Threshold must be detracted first.");
            detractThresh();
        }
        ArrayList<Vertex> neighbours = new ArrayList<>();
        for(Vertex vtx: vertices){
            /* If they are from the same set, then it's impossible there's an edge
             * between them.*/
            float ew  = edgeWeight(currentVtx, vtx);
            if(!Float.isNaN(ew) && ew>0)
                neighbours.add(vtx);       
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
        //if(ew == 0)
          //  throw new IllegalArgumentException("There is a null-edge between vtx1 and vtx2");
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
     * This method pushes a vertex into the graph.
     * @param vtx
     * @return 
     */
    private int pushVertex(String value, int vertexSet){
        Vertex vtx = new Vertex(value,vertexSet,-1);
        //U. Note: we should add a new meothod, searching a vertex in a given
        //vertex list (arrayList or Linkedlist). This method should be a static
        //method
        int idx = vertices.indexOf(vtx);
        if(idx == -1){
            try{
            /* 2nd version of distances. */
            vtx.setDistIdx(vertices.size());
            vertices.add(vtx);
            //set the index of the new vertex according to its vertex set
            vtx.setVtxIdx(setSizes[vertexSet]);
            setSizes[vertexSet]++;
            }catch(IndexOutOfBoundsException e){
                System.out.println("(MatrixHierNpartiteGraph.pushVertex) setSizes: Index out of bounds exception: "+vertexSet+"  "+setSizes.length);
                return -1;
            }
            return vtx.getVtxIdx();
        }
        else
            return ((ArrayList<Vertex>)vertices).get(idx).getVtxIdx();
        
    }
    
    /**
     * Read the graph from the input file.
     * @param filePath
     * @throws IOException 
     */
    @Override
    public final void readGraph(String filePath) throws IOException {
        FileReader fr = new FileReader(filePath);
        BufferedReader br = new BufferedReader(fr);
        String line = null;
        /* Reader the file for the first time and gather the information
         * about the number of sets, the number of vertices.*/
        int maxSetIdx = 0;
        while((line = br.readLine())!= null){
            String[] splits = line.split("\\s+");
            /* If the splits has the length of 2, then we have only one node in the line. */
            if(splits.length == 2){
                /* Get the max size index.*/
                try{
                int setIdx = Integer.parseInt(splits[1]);
                if(maxSetIdx < setIdx)
                    maxSetIdx = setIdx;
                }catch(NumberFormatException e){
                    System.out.println("(MatrixHierNpartiteGraph.readGraph) Number format information: "+
                            line);
                    return;
                }
            }
            /* If the splits has the length of 4, then we have two nodes in the line. */
            else if(splits.length ==5 ){
                try{
                int setIdx1 = Integer.parseInt(splits[1]);
                int setIdx2 = Integer.parseInt(splits[3]);
                if(maxSetIdx <setIdx1)
                    maxSetIdx = setIdx1;
                if(maxSetIdx <setIdx2)
                    maxSetIdx = setIdx2;
                }catch(NumberFormatException e){
                    System.out.println("(MatrixHierNpartiteGraph.readGraph) Number format information: "+
                            line);
                    return;
                }
            }
            /* Else, there must be an error. */
            else{
                System.out.println("(MatrixHierNpartiteGraph.readGraph) Line with wrong tokens: "+
                        line);
                return;
            }
            
        }
        /* Init setSizes. */
        setSizes = new int[maxSetIdx+1];
        br.close();
        fr.close();
        
        /* Read the file for the second time, get the sizes of all vertex sets,
         to init the edgeWeight matrix, but not to assign any values in setSizes.
         The values in setSizes are left for pushVertex() to init.*/
        int[] sizes = new int[maxSetIdx+1];
        fr = new FileReader(filePath);
        br = new BufferedReader(fr);
        line = null;
        /* We use hashsets to get the numbers of vertices. */
        ArrayList<HashSet<String>> vertexHashSets = new ArrayList<>();
        /* Init all hashsets in vertexHashSets. */
        for(int i=0;i<maxSetIdx+1;i++)
            vertexHashSets.add(new HashSet<String>());
        
        while((line = br.readLine())!= null){
            String[] splits = line.split("\\s+");
            /* If there are two tokens in the line, then we have one vertex in the line. */
            if(splits.length == 2)
                vertexHashSets.get(Integer.parseInt(splits[1]))
                        .add(String.copyValueOf(splits[0].toCharArray()));
            else if(splits.length ==5){
                vertexHashSets.get(Integer.parseInt(splits[1]))
                        .add(String.copyValueOf(splits[0].toCharArray()));;
                vertexHashSets.get(Integer.parseInt(splits[3]))
                        .add(String.copyValueOf(splits[2].toCharArray()));;
            }
        }
        
        /* Then we give values to sizes. */
        for(int i=0;i<maxSetIdx+1;i++)
            sizes[i] = vertexHashSets.get(i).size();
        
        /* Init thte edgeWeights matrix. */
        interEdgeWeights = new ArrayList<>();
        intraEdgeWeights = new ArrayList<>();
        /* Init the values for interEdgeWeights. */
        for(int i=0;i<maxSetIdx;i++){
            float[][] weights = new float[sizes[i]][sizes[i+1]];
            /* Init the values. */
            for(int j=0;j<weights.length;j++)
                for(int k=0;k<weights[0].length;k++)
                    weights[j][k] = Float.NaN;
            interEdgeWeights.add(weights);
        }
        /* Init the values for intraEdgeWeights. */
        for(int i=0;i<=maxSetIdx;i++){
            float[][] weights = new float[sizes[i]][sizes[i]];
            /* Init the values. */
            for(int j=0;j<weights.length;j++)
                for(int k=0;k<weights[0].length;k++)
                    weights[j][k] = Float.NaN;
            intraEdgeWeights.add(weights);
        }
        br.close();
        fr.close();
        
        /* Read the file for the third time, assign the edge weights. */
        fr = new FileReader(filePath);
        br = new BufferedReader(fr);
        while((line = br.readLine())!= null){
            String[] splits = line.split("\\s+");
            /* If we have only one vertex in the line, we just push it into vertices arrayList. */
            if(splits.length ==2){
                String vtxName = String.copyValueOf(splits[0].toCharArray());
                int vtxLvl = -1;
                /* Catch the NumberFormatException. */
                try{
                    vtxLvl = Integer.parseInt(String.copyValueOf(splits[1].toCharArray()));
                }catch(NumberFormatException e){
                    System.out.println("(MatrixHierGeneralGraph.readGraph) Invalid format for vertex set index:  "+splits[1]);
                    System.exit(0);
                }
                //Push vertex
                pushVertex(vtxName,vtxLvl);
            }
            /* If not, then we have two vertices in the line, we push them into vertices and assign
             * the edge weight between them.*/
            else{
                String vtx1Name = String.copyValueOf(splits[0].toCharArray());
                //int Vtx1Lvl = Integer.parseInt(String.copyValueOf(splits2[1].toCharArray()));
                int vtx1Lvl = -1;
                /* Catch the NumberFormatException. */
                try{
                    vtx1Lvl = Integer.parseInt(String.copyValueOf(splits[1].toCharArray()));
                }catch(NumberFormatException e){
                    System.out.println("(MatrixHierGeneralGraph.readGraph) Invalid format for vertex set index:  "+splits[0]);
                    System.exit(0);
                }
                
                String vtx2Name = String.copyValueOf(splits[2].toCharArray());
                //int Vtx2Lvl = Integer.parseInt(String.copyValueOf(splits2[3].toCharArray()));
                int vtx2Lvl = -1;
                /* Catch the NumberFormatException. */
                try{
                    vtx2Lvl = Integer.parseInt(String.copyValueOf(splits[3].toCharArray()));
                }catch(NumberFormatException e){
                    System.out.println("(MatrixHierGeneralGraph.readGraph) Invalid format for vertex set index:  "+splits[3]);
                    System.exit(0);
                }
                /* Check if it is a inter-edge. */
                if(vtx1Lvl - vtx2Lvl == 1 || vtx1Lvl - vtx2Lvl == -1 || 
                        vtx1Lvl - vtx2Lvl == setSizes.length-1 ||
                        vtx1Lvl - vtx2Lvl == -setSizes.length+1){
                    /* The vertices are pushed into the graph and the edge weight is read. */
                    int idx1 = pushVertex(vtx1Name,vtx1Lvl);
                    int idx2 = pushVertex(vtx2Name,vtx2Lvl);
                    float ew = Float.parseFloat(String.copyValueOf(splits[4].toCharArray()));

                    //U. note: here we need a method to assign inter-edgeweight.
                    int minVtxLvl = Math.min(vtx1Lvl, vtx2Lvl);
                    if(vtx1Lvl == minVtxLvl)
                        interEdgeWeights.get(minVtxLvl)[idx1][idx2] = ew;
                    else
                        interEdgeWeights.get(minVtxLvl)[idx2][idx1] = ew;
                }
                /* Check if it is a intra-edge. */
                else if(vtx1Lvl== vtx2Lvl){
                    /* The two vertices are pushed into the graph and the edge weight is read.*/
                    int idx1 = pushVertex(vtx1Name, vtx1Lvl);
                    int idx2 = pushVertex(vtx2Name, vtx2Lvl);
                    float ew = Float.parseFloat(String.copyValueOf(splits[4].toCharArray()));
                    /*Assign the intra-edgeweight. */
                    intraEdgeWeights.get(vtx1Lvl)[idx1][idx2] = ew;
                    intraEdgeWeights.get(vtx1Lvl)[idx2][idx1] = ew;
                }
                /* Else, it must be a cross-level edge, then we throw out an exception .*/
                else
                    throw new IllegalArgumentException("(MatrixHierGeneralGraph.readGraph) Cross level edge: "+line);
            }
        }
        br.close();
        fr.close();
        /* Init the cluster object. */
        clusters = new ArrayList<>();
    }
    
    /**
     * The rule for header:
     * NumberOfSets Set1Size Set2Size ... SetNSize
     * @param filePath
     * @throws IOException 
     */
    public final void readGraphWithHeader(String filePath) throws IOException{
        FileReader fr = new FileReader(filePath);
        BufferedReader br =new BufferedReader(fr);
        /* Read the header, assign setSizes */
        String line = null;
        line = br.readLine();
        String[] headerSplits = line.split("\\s+");
        /* Create the dummy set sizes just to create edgeWeights, because of method pushVertex().*/
        int[] sizes = null;
        try{
        sizes = new int[Integer.parseInt(headerSplits[0])];
        for(int i=0;i<headerSplits.length-1;i++){
            sizes[i] = Integer.parseInt(headerSplits[i+1]);
        }
        }catch(NumberFormatException e){
            System.out.println("(MatrixHierGeneralGraph.readGraphWithHeader) Header number format problem: "+line);
            System.exit(0);
        }
        catch(IndexOutOfBoundsException e){
            System.out.println("((MatrixHierGeneralGraph.readGraphWithHeader) Number of sets and the given sizes do not match: "+line);
            System.exit(0);
        }
        /* However, here we must int setSizes, set all elements to 0. */
        setSizes = new int[Integer.parseInt(headerSplits[0])];
        for(int i=0;i<setSizes.length;i++)
            setSizes[i] = 0;
        /* Init the interEdgeWeight matrix. */
        interEdgeWeights = new ArrayList<>();
        intraEdgeWeights = new ArrayList<>(); 
        for(int i=0;i<setSizes.length-1;i++){
            float[][] weights = new float[sizes[i]][sizes[i+1]];
            /* Init the values. */
            for(int j=0;j<weights.length;j++)
                for(int k=0;k<weights[0].length;k++)
                    weights[j][k] = Float.NaN;
            interEdgeWeights.add(weights);
        }
        /* Init the intraEdgeWegith matrix. */
        for(int i=0;i<setSizes.length;i++){
            float[][] weights = new float[sizes[i]][sizes[i]];
            for(int j=0;j<weights.length;j++)
                for(int k=0;k<weights[0].length;k++)
                    weights[j][k] = Float.NaN;
            
            intraEdgeWeights.add(weights);
        }
        
        /* Read the graph.*/
        while((line = br.readLine())!= null){
            String[] splits = line.split("\\s+");
            /* If we have only one vertex in the line, we just push it into vertices arrayList. */
            if(splits.length ==2){
                String vtxName = String.copyValueOf(splits[0].toCharArray());
                int vtxLvl = -1;
                /* Catch the NumberFormatException. */
                try{
                    vtxLvl = Integer.parseInt(String.copyValueOf(splits[1].toCharArray()));
                }catch(NumberFormatException e){
                    System.out.println("(readGraph) Invalid format for vertex set index:  "+splits[1]);
                    System.exit(0);
                }
                //Push vertex
                pushVertex(vtxName,vtxLvl);
            }
            /* If not, then we have two vertices in the line, we push them into vertices and assign
             * the edge weight between them.*/
            else{
                String vtx1Name = String.copyValueOf(splits[0].toCharArray());
                //int Vtx1Lvl = Integer.parseInt(String.copyValueOf(splits2[1].toCharArray()));
                int vtx1Lvl = -1;
                /* Catch the NumberFormatException. */
                try{
                    vtx1Lvl = Integer.parseInt(String.copyValueOf(splits[1].toCharArray()));
                }catch(NumberFormatException e){
                    System.out.println("(readGraph) Invalid format for vertex set index:  "+splits[0]);
                    System.exit(0);
                }
                
                String vtx2Name = String.copyValueOf(splits[2].toCharArray());
                //int Vtx2Lvl = Integer.parseInt(String.copyValueOf(splits2[3].toCharArray()));
                int vtx2Lvl = -1;
                /* Catch the NumberFormatException. */
                try{
                    vtx2Lvl = Integer.parseInt(String.copyValueOf(splits[3].toCharArray()));
                }catch(NumberFormatException e){
                    System.out.println("(readGraph) Invalid format for vertex set index:  "+splits[3]);
                    System.exit(0);
                }

                /* Check if it is a inter-edge. */
                if(vtx1Lvl - vtx2Lvl == 1 || vtx1Lvl - vtx2Lvl == -1||
                        vtx1Lvl - vtx2Lvl == setSizes.length-1 ||
                        vtx1Lvl - vtx2Lvl == -setSizes.length+1){
                    /* The vertices are pushed into the graph and the edge weight is read. */
                    int idx1 = pushVertex(vtx1Name,vtx1Lvl);
                    int idx2 = pushVertex(vtx2Name,vtx2Lvl);
                    float ew = Float.parseFloat(String.copyValueOf(splits[4].toCharArray()));

                    //U. note: here we need a method to assign inter-edgeweight.
                    int minVtxLvl = Math.min(vtx1Lvl, vtx2Lvl);
                    if(vtx1Lvl == minVtxLvl)
                        interEdgeWeights.get(minVtxLvl)[idx1][idx2] = ew;
                    else
                        interEdgeWeights.get(minVtxLvl)[idx2][idx1] = ew;
                }
                /* Check if it is a intra-edge. */
                else if(vtx1Lvl== vtx2Lvl){
                    /* The two vertices are pushed into the graph and the edge weight is read.*/
                    int idx1 = pushVertex(vtx1Name, vtx1Lvl);
                    int idx2 = pushVertex(vtx2Name, vtx2Lvl);
                    float ew = Float.parseFloat(String.copyValueOf(splits[4].toCharArray()));
                    /*Assign the intra-edgeweight. */
                    intraEdgeWeights.get(vtx1Lvl)[idx1][idx2] = ew;
                    intraEdgeWeights.get(vtx1Lvl)[idx2][idx1] = ew;
                }
                /* Else, it must be a cross-level edge, then we throw out an exception .*/
                else
                    throw new IllegalArgumentException("(MatrixHierGeneralGraph.readGraph) Cross level edge: "+line);
            }
        }
        br.close();
        fr.close();
        /* Init the cluster object. */
        clusters = new ArrayList<>();
    }
    
    /**
     * This method reads graph from the "entity-matrix" style input.
     * @throws IOException 
     * @param filePath The input file. 
     * untested.
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
        try{
            setSizes = new int[Integer.parseInt(levelStr)];/* Init setSizes. */
        }catch(NumberFormatException e){
            throw new NumberFormatException("(biforce.graphs.MatrixHierGeneralGraph.readGraphInXml) Attribute \"levels\" number format error:  "+levelStr);
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
        /* Init all matrices. */
        /* We have setSizes.length intraEdgeWeightMatrix. */
        intraEdgeWeights = new ArrayList<> ();
        for(int i=0;i<setSizes.length;i++){
            float[][] intraMatrix = new float[setSizes[i]][setSizes[i]]; 
            /* Give matrix the init values. */
            for(int j=0;j<setSizes[i];j++)
                for(int k=0;k<setSizes[i];k++)
                    intraMatrix[j][k] = Float.NaN;
            /* Push this intraMatrix to intraEdgeWeights. */
            intraEdgeWeights.add(intraMatrix);
        }
        /* Init the inter matrices. */
        interEdgeWeights = new ArrayList<>();
        for(int i=0;i<setSizes.length-1;i++){
            float[][] interMatrix = new float[setSizes[i]][setSizes[i+1]];
            /* Give matrix the init values. */
            for(int j=0;j<setSizes[i];j++)
                for(int k=0;k<setSizes[i+1];k++)
                    interMatrix[j][k] = Float.NaN;
            /* Push this interMatrix into interEdgeWeights. */
            interEdgeWeights.add(interMatrix);
        }
        //Init the top-rear matrix.
        float[][] interMatrix = new float[setSizes[0]][setSizes[setSizes.length-1]];
        for(int i=0;i<interMatrix.length;i++)
            for(int j=0;j<interMatrix[0].length;j++)
                interMatrix[i][j] = Float.NaN;
        interEdgeWeights.add(interMatrix);
        
        /* Read the edge weights from the xml input file. */
        ArrayList<Element> matrixElementList = new ArrayList<>(docRoot.getChildren("matrix"));
        /* First check the number of elements in matrixElementList, if not equal to 2*setSizes.length-1. */
        // There are 2*setSizes.length matrix, half intra-matrices, half inter-matrices.
        // No check of the number of the matrices anymore 2015.06.01
        //if(matrixElementList.size() != 2*setSizes.length) 
          //  throw new IllegalArgumentException("(biforce.graphs.MatrixHierGeneralGraph.readGraphInXml) The number of matrices is wrong:  "+matrixElementList.size());
        
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
                /* Read the content. */
                String matrixContent = matrix.getContent(0).getValue().trim();
                /* Check if the matrix is stored in an external file.*/
                String hasExternMatrixFile = matrix.getAttributeValue("externalFile");
                if(hasExternMatrixFile == null || hasExternMatrixFile.equalsIgnoreCase("false")){
                    XmlInputParser parser = new XmlInputParser();
                    parser.parseIntraMatrixString(matrixContent, matrixLevel1, this);
                }
                /* If the node names are stored in an external file.*/
                else if(hasExternMatrixFile.equalsIgnoreCase("true")){
                    XmlInputParser parser = new XmlInputParser();
                    parser.parseIntraMatrixFile(matrixContent,matrixLevel1,this);
                }
                else
                    throw new IllegalStateException("(biforce.graphs.MatrixHierGeneralGraph.readGraphInXml) Illegal attribute of \"externalFile\": "+hasExternMatrixFile);
            }
            else{ 
                /* For inter-matrix. */
                String matrixContent = matrix.getContent(0).getValue().trim();
                /* Check if the matrix is stored in an external file.*/
                String hasExternMatrixFile=matrix.getAttributeValue("externalFile");
                if(hasExternMatrixFile == null|| hasExternMatrixFile.equalsIgnoreCase("false")){
                    XmlInputParser parser = new XmlInputParser();
                    parser.parseInterMatrixString(matrixContent, matrixLevel1, matrixLevel2, this);
                }
                else if(hasExternMatrixFile.equalsIgnoreCase("true")){
                    XmlInputParser parser = new XmlInputParser();
                    parser.parseInterMatrixFile(matrixContent, matrixLevel1, matrixLevel2, this);
                }
                else
                    throw new IllegalStateException("(biforce.graphs.MatrixHierGeneralGraph.readGraphInXml))Illegal attribute of \"externalFile\": "+hasExternMatrixFile);
                        
            }
        }
        clusters = new ArrayList<>();
    }
    
    /**
     * This method re-add the threshold to edge weights, provided the threshold has been
     * detracted before.
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
        /* Restore the edge weights. */
        else{
            for(int i=0;i<interEdgeWeights.size();i++){
                float[][] weights = interEdgeWeights.get(i);
                for(int j=0;j<weights.length;j++)
                    for(int k=0;k<weights[0].length;k++)
                        weights[j][k] +=thresh;
            }
            for(int i=0;i<intraEdgeWeights.size();i++){
                float[][] weights = intraEdgeWeights.get(i);
                for(int j=0;j<weights.length;j++)
                    for(int k=0;k<weights[0].length;k++)
                        weights[j][k] += thresh;
            }
        }
        threshDetracted = false;
    }
    
    /**
     * This method removes the action with the given index from the arraylist actions.
     * @param Index
     * @return 
     */
    @Override
    public boolean removeAction(int Index) {
        try{
            actions.remove(Index);
        }catch(IndexOutOfBoundsException e){
            System.out.println("(MatrixHierNpartiteGraph.removeAction) Index out of bounds: "+
                   Index+"  size: "+actions.size());
            return false;
        }
        return true;
    }
    
    /**
     * This method sets the distance between two given vertices. 
     * Apparently, the two vertices must be in the "neighbouring" vertex sets.
     * @param vtx1
     * @param vtx2
     * @param dist 
     */
    public void setDist(Vertex vtx1, Vertex vtx2){
        /* For the 2nd version of distance, the first version of MatixHierGeneralGraph. */
        float dist = euclidDist(vtx1,vtx2);
        distMatrix[vtx1.getDistIdx()][vtx2.getDistIdx()] = dist;
        distMatrix[vtx2.getDistIdx()][vtx1.getDistIdx()] = dist;
    }

    @Override
    public void setEdgeWeight(Vertex vtx1, Vertex vtx2, float edgeWeight) {
        /* Check if it is an inter-edgeweight. */
        if(vtx1.getVtxSet() - vtx2.getVtxSet() == 1 || 
                vtx1.getVtxSet() - vtx2.getVtxSet() == -1){
            int minVtxSet = Math.min(vtx1.getVtxSet(),vtx2.getVtxSet());
            if(minVtxSet == vtx1.getVtxSet())
                interEdgeWeights.get(minVtxSet)
                        [vtx1.getVtxIdx()][vtx2.getVtxIdx()]= edgeWeight;
            else
                interEdgeWeights.get(minVtxSet)
                        [vtx2.getVtxIdx()][vtx1.getVtxIdx()]= edgeWeight;
        }
        // Check if it is an edge between top set and rear set.
        if(vtx1.getVtxSet() - vtx2.getVtxSet() == setSizes.length-1 ||
                vtx1.getVtxSet() - vtx2.getVtxSet() == 1- setSizes.length){
            int minVtxSet = Math.min(vtx1.getVtxSet(),vtx2.getVtxSet());
            if(minVtxSet == vtx1.getVtxSet())
                interEdgeWeights.get(vtx2.getVtxSet())
                        [vtx1.getVtxIdx()][vtx2.getVtxIdx()]= edgeWeight;
            else
                interEdgeWeights.get(vtx2.getVtxSet())
                        [vtx2.getVtxIdx()][vtx1.getVtxIdx()]= edgeWeight;
        }
            
        /* Check if it is an intra-edgeweight. */
        else if(vtx1.getVtxSet() == vtx2.getVtxSet())
            intraEdgeWeights.get(vtx1.getVtxSet())[vtx1.getVtxIdx()][vtx2.getVtxIdx()]= edgeWeight;
    }

    
     @Override
    public boolean takeAction(int idx) {
        /* First the check the index. */
        if(idx <0 || idx >= actions.size())
            throw new IllegalArgumentException("(MatrixBipartitGraph2.takeAction) Wrong action index: "+
                    idx);
        Action act = actions.get(idx);
        
        if(act.getOriginalWeight()>0){
            setEdgeWeight(act.getVtx1(),act.getVtx2(),nForceConstants.FORBIDDEN);
            //System.out.println("Forbidden");
        }
        else{
            setEdgeWeight(act.getVtx1(),act.getVtx2(),nForceConstants.PERMENANT);
            //System.out.println("Permenant");
        }
        return true;
    }

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
     * This method updates/re-sets all pairwise distances.
     */
    @Override
    public void updateDist() {
        for(int i=0;i<vertices.size();i++)
            for(int j=i+1;j<vertices.size();j++)
                setDist(vertices.get(i),vertices.get(j));
                
    }

    /**
     * This method updates the positions of the vertices given the displacement 
     * vectors for all vertices.
     * @param dispVector 
     */
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
     * This method returns the number of vertex sets.
     * @return 
     */
    @Override
    public int vertexSetCount() {
        return setSizes.length;
    }
    
    
    /**
     * This method writes the distance matrix into the given file. 
     * This method is used to output the running information of bi-force
     * @param file 
     */
    public void writeDistanceMatrix(String file){
        try{
        FileWriter fw = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fw);
        for(int i=0;i<distMatrix.length;i++){
            for(int j=0;j<distMatrix[i].length-1;j++)
                bw.write(distMatrix[i][j]+"\t");
            bw.write(distMatrix[i][distMatrix[i].length-1]+"\n");
        }
        bw.flush();
        bw.close();
        fw.close();
        }catch(IOException e){
            System.err.println("(MatrixHierGeneralGraph.writeDistanceMatrix) File writer init error.");
        }
    }
    
    /**
     * This method outputs the intra matrix with index idx to the given file.
     * @param outFile
     * @param idx 
     */
    public void writeIntraEwMatrix(String outFile, int idx){
        if(idx>= intraEdgeWeights.size() || idx <0)
            throw new IllegalArgumentException("(MatrixHierGeneralGraph.writeIntraEwMatrix) The given index is out of the bound or illegal: "+idx);
        float[][] matrix = intraEdgeWeights.get(idx);
        try{
        FileWriter fw = new FileWriter(outFile);
        BufferedWriter bw = new BufferedWriter(fw);
        for(int i=0;i<matrix.length;i++){
            for(int j=0;j<matrix[i].length-1;j++)
                bw.write(matrix[i][j]+"\t");
            bw.write(matrix[i][matrix[i].length-1]+"\n");
        }
        bw.flush();
        bw.close();
        fw.close();
        }catch(IOException e){
            System.err.println("(MatrixHierGeneralGraph.writeInatrEwMatrix) File writer init error.");
        }
    }
    
    /**
     * This method outputs the inter edge weight matrix into the given out file.
     * @param outFile
     * @param idx 
     */
    public void writerInterEwMatrix(String outFile, int idx){
        if(idx>= interEdgeWeights.size() || idx <0)
            throw new IllegalArgumentException("(MatrixHierGeneralGraph.writeIntraEwMatrix) The given index is out of the bound or illegal: "+idx);
        float[][] matrix = interEdgeWeights.get(idx);
        try{
        FileWriter fw = new FileWriter(outFile);
        BufferedWriter bw = new BufferedWriter(fw);
        for(int i=0;i<matrix.length;i++){
            for(int j=0;j<matrix[i].length-1;j++)
                bw.write(matrix[i][j]+"\t");
            bw.write(matrix[i][matrix[i].length-1]+"\n");
        }
        bw.flush();
        bw.close();
        fw.close();
        }catch(IOException e){
            System.err.println("(MatrixHierGeneralGraph.writeInatrEwMatrix) File writer init error.");
        }
    }

    /**
     * This method outputs the cluster result to the given path.
     * @param filePath  The file path to output.
     * @param isXmlFile  If the file is outputted as xml format.
     */
    @Override
    public void writeClusterTo(String filePath, boolean isXmlFile) {
        if(isXmlFile)
            writeXmlClusterTo(filePath);
        else
            writePlainClusterTo(filePath);
    }
    
    /**
     * This method writes the graph in the given format into the given filePath.
     * @param filePath
     * @param isXmlFile 
     */
    @Override
    public void writeGraphTo(String filePath, boolean isXmlFile) {
        if(!isXmlFile) /* If plain graph is to be outputted. */
            writePlainGraphTo(filePath);
        else
            writeXmlGraphTo(filePath);
    }
    
    /**
     * This method outputs the resulted graph in xml format into the given filePath.
     * @param filePath 
     */
    public void writeXmlGraphTo(String filePath){
        /* Write the xml. */
        FileWriter fw = null;
        BufferedWriter bw = null;
        try{
            fw = new FileWriter(filePath);
            bw = new BufferedWriter(fw);
        }catch(IOException e){
            System.err.println("(MatrixHierGeneralGraph.writeXmlGraphTo) Xml output error.");
            return;
        }
        try{
            bw.write("<document>\n");
            /* Output the entity element. */
            bw.write("<entity levels=\""+setSizes.length+"\">\n");
            /* Output the vertices. */
            for(int k=0;k<setSizes.length;k++){
                for(int i=0;i<vertices.size();i++){
                    if(vertices.get(i).getVtxSet() == k)
                        bw.write(vertices.get(i).getValue()+"\t");
                }
                bw.write("\n");
            }
           
            bw.write("</entity>\n");
            /* Output the interEdgeWeight matrix. */
            for(int l=0;l<setSizes.length;l++){
                bw.write("<matrix matrixLevel=\""+l+"  "+(l+1)+"\">\n");
                float[][] matrix = interEdgeWeights.get(l);
                for(int j=0;j<matrix.length;j++){
                    for(int k=0;k<matrix[0].length-1;k++)
                        bw.write(matrix[j][k]+"\t");
                    bw.write(matrix[j][matrix[0].length-1]+"\n");
                }
                bw.write("</matrix>\n");
            }
            /* Output the intraEdgeWeight matrix. */
            for(int l=0;l<intraEdgeWeights.size();l++){
                bw.write("<matrix matrixLevel=\""+l+"  "+l+"\">\n");
                float[][] matrix = intraEdgeWeights.get(l);
                for(int j=0;j<matrix.length;j++){
                    for(int k=0;k<matrix[0].length-1;k++)
                        bw.write(matrix[j][k]+"\t");
                    bw.write(matrix[j][matrix[0].length-1]+"\n");
                }
                bw.write("</matrix>\n");
            }
            bw.write("</document>\n");
            bw.flush();
            bw.close();
            fw.close();
        }catch(IOException e){
            System.err.println("(MatrixHierGeneralGraph.writeXmlGraphTo) Xml writing error.");
            return;
        }
    }
    
    public void writePlainGraphTo(String filePath){
        
    }
    
    
     /**
     * This method writes the cluster in plain format into filePath.
     * @param filePath 
     */
    public void writePlainClusterTo(String filePath){
        FileWriter fw =null;
        BufferedWriter bw = null;
        try{
            fw = new FileWriter(filePath);
            bw = new BufferedWriter(fw);
        }catch(IOException e){
            System.err.println("(MatrixHierGeneralGraph.writeClusterTo) Clusters writing error.");
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
            System.err.println("(MatrixHierGeneralGraph.writeClusterTo) Clusters writing error.");
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
            System.err.println("(MatrixHierGeneralGraph.writeXmlClusterTo) Cluster writing error.");
            e.printStackTrace();
            return;
        }
    }

    /**
     * This method writes a single cluster using the given BufferedWriter
     * @param bw 
     * @param cluster 
     */
    public void writeSingleCluster(BufferedWriter bw, Cluster cluster){
        ArrayList<Vertex> clusterVertices = cluster.getVertices();
        // For test
        if(clusterVertices.isEmpty()){
            System.out.println("Empty cluster");
            return;
        }
        try{
        bw.write("<cluster  "+clusterVertices.get(0).getClustNum()+">\n");
        /* We output the cluster in separated sets. */
        for(int i=0;i<setSizes.length;i++){
            for(int j=0;j<clusterVertices.size();j++)
                if(clusterVertices.get(j).getVtxSet() == i)
                    bw.write(clusterVertices.get(j).getValue()+"\t");
            bw.write("\n");
        }
        bw.flush();
        bw.write("</cluster>\n");
        }catch(IOException e){
            System.err.println("(MatrixHierGeneralGraph.writeSingleCluster) Single cluster output error.");
            e.printStackTrace();
            return;
        }
    }

    /**
     * This method writes the information of the result into the given filePath.
     * @param filePath 
     */
    @Override
    public void writeResultInfoTo(String filePath){
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
            System.err.println("(MatrixHierGeneralGraph.writeResultInfoTo) Writing error.");
            return;
        }
    }
    

    
    
}
