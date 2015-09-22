/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nforce.algorithms;
import nforce.constants.nForceConstants;
import nforce.graphs.Graph;
import nforce.graphs.Action;
import nforce.graphs.Vertex;
import nforce.graphs.Cluster;
import java.util.ArrayList;
import java.util.Random;
import java.util.Iterator;
import java.util.Stack;
import java.io.IOException;
import java.util.Collections;
import java.io.FileWriter;
import java.io.BufferedWriter;
/**
 * This biforce is based on Graph.
 * @author Peng Sun.
 */
public class nForceOnGraph {
    /**
     * This method calculates the costs for a given cluster assignment
     * 
     * @Untested 
     * @param Points
     * @return 
     * @throws IllegalArgumentException. When there is point without a cluster assignment.
     */
    private void assignActions(Graph graph){

        //first check if there is no unassigned vertex
        for(Vertex vtx:graph.getVertices())
            if(vtx.getClustNum() ==-1)
                throw new IllegalArgumentException("BiForceOnGraph4:  "+
                        vtx.getValue()+" not assigned with any cluster.");

        for(int i=0;i<graph.getVertices().size();i++)
            for(int j=i+1;j<graph.getVertices().size();j++){
                Vertex vtx1 = graph.getVertices().get(i);
                Vertex vtx2 = graph.getVertices().get(j);
                 if(vtx1.getValue().equals("drug_118") && vtx2.getValue().equals("disease_916"))
                    System.out.println();
                if(vtx2.getValue().equals("drug_118") && vtx1.getValue().equals("disease_916"))
                    System.out.println();
                float ew = graph.edgeWeight(vtx1, vtx2);
                //if there is no edge between them, then continue;
                //if(ew == 0 || Float.isNaN(ew))
                if(Float.isNaN(ew))
                    continue;
                //case 1, in the same cluster with negative edge: then we need an addition
                if(vtx1.getClustNum() ==vtx2.getClustNum() &&
                        ew <=0 ){
                   graph.pushAction(vtx1, vtx2);
                }
                
                //case 2, in different clusters but with a positive edge: we need a deletion
                else if(vtx1.getClustNum() !=vtx2.getClustNum()
                        && ew > 0){
                   graph.pushAction(vtx1, vtx2);
                } 
            }
    }
    
    /**
     * This method assigns clusters to each node in the given graph.
     * @param graph 
     */
    private void assignClusters(Graph graph){
        int maxClusterIdx = 0;
        for(int i=0;i<graph.vertexCount();i++)
            if(maxClusterIdx < graph.getVertices().get(i).getClustNum())
                maxClusterIdx = graph.getVertices().get(i).getClustNum();
        
        //Assign the vertex
        ArrayList<Vertex>[] clusterArray = new ArrayList[maxClusterIdx+1];
        for(int i=0;i<=maxClusterIdx;i++)
            clusterArray[i] = new ArrayList<>();
        
        for(Vertex vtx: graph.getVertices())
            clusterArray[vtx.getClustNum()].add(vtx);
        
        ArrayList<Cluster> clusters = new ArrayList();
        try{
        for(int i=0;i<=maxClusterIdx;i++){
            //29.03.2015. Add only the non-empty clusters
            if(!clusterArray[i].isEmpty())
                clusters.add(new Cluster(clusterArray[i]));
        }
                }catch(IllegalArgumentException e){
            System.out.print("catch");
        }
        graph.setClusters(clusters);
    }
    
    /**
     * This method assigns clusters before chain clustering. 
     * @param graph 
     */
    private void assignClustersChainBeforePostPro(Graph graph){
        int maxClusterIdx = 0;
        for(int i=0;i<graph.vertexCount();i++)
            if(maxClusterIdx < graph.getVertices().get(i).getClustNum())
                maxClusterIdx = graph.getVertices().get(i).getClustNum();
        
        //Assign the vertex
        ArrayList<Vertex>[] clusterArray = new ArrayList[maxClusterIdx+1];
        for(int i=0;i<=maxClusterIdx;i++)
            clusterArray[i] = new ArrayList<>();
        
        for(Vertex vtx: graph.getVertices()){
            // For chain clustering
            if(vtx.getVtxSet() != 0)
                continue;
            clusterArray[vtx.getClustNum()].add(vtx);
        }
        
        ArrayList<Cluster> clusters = new ArrayList();
        try{
        for(int i=0;i<=maxClusterIdx;i++){
            //29.03.2015. Add only the non-empty clusters
            if(!clusterArray[i].isEmpty())
                clusters.add(new Cluster(clusterArray[i]));
        }
                }catch(IllegalArgumentException e){
            System.out.print("catch");
        }
        graph.setClusters(clusters);
    }
    /**
     * This class returns the cost of editing from a given set of points. Without constructing the Actions objects, this method
     * should return the cost much quicker
     * @param graph
     * @param p
     * @return 
     */
    public float computeCost(Graph graph){

        float cost = 0;
        //first check if there is no unassigned vertex
        for(Vertex vtx:graph.getVertices())
            if(vtx.getClustNum() ==-1)
                throw new IllegalArgumentException(vtx.getValue()+" not assigned with any cluster");
        
        for(int i=0;i< graph.getVertices().size();i++)
            for(int j=i+1;j<graph.getVertices().size();j++){
                Vertex vtx1 = graph.getVertices().get(i);
                Vertex vtx2 = graph.getVertices().get(j);
                
                float ew = graph.edgeWeight(vtx1, vtx2);
                /* If there is no edge between vtx1 and vtx2, then continue. */
                if(Double.isNaN(ew))
                    continue;
                //case 1, in the same cluster with negative edge: then we need an addition
                if(vtx1.getClustNum() ==vtx2.getClustNum()
                        && ew < 0)
                   cost -= ew;           
                //case 2, in different clusters but with a positive edge: we need a deletion
                if(vtx1.getClustNum() !=vtx2.getClustNum()
                        && ew > 0)
                   cost += ew;           
            }
        //System.out.println(cost);
        return cost;
    }
    /**
     * Computing cost used for chain clustering.
     * @param graph
     * @return 
     */
    public float computeCostChain(Graph graph){
        float cost = 0;
        
        for(int i=0;i< graph.getVertices().size();i++)
            for(int j=i+1;j<graph.getVertices().size();j++){
                Vertex vtx1 = graph.getVertices().get(i);
                Vertex vtx2 = graph.getVertices().get(j);
                if(vtx1.getVtxSet() != 0 || vtx2.getVtxSet()!= 0)
                    continue;
                float ew = graph.edgeWeight(vtx1, vtx2);
                /* If there is no edge between vtx1 and vtx2, then continue. */
                if(Double.isNaN(ew))
                    continue;
                //case 1, in the same cluster with negative edge: then we need an addition
                if(vtx1.getClustNum() ==vtx2.getClustNum()
                        && ew < 0)
                   cost -= ew;           
                //case 2, in different clusters but with a positive edge: we need a deletion
                if(vtx1.getClustNum() !=vtx2.getClustNum()
                        && ew > 0)
                   cost += ew;           
            }
        //System.out.println(cost);
        return cost;
    }
    /**
     * This method returns the difference of the cost (a positive value means increase in cost, and a 
     * negative value means decrease in cost) if we merge cluster c1 and c2 together.
     * @param Points
     * @param c1
     * @param c2
     * @return 
     */
    private float computeMergeCost(Graph graph, Cluster c1, Cluster c2 ){
        float CostDiff = 0;
        for(Vertex pt1: c1.getVertices())
            for(Vertex pt2:c2.getVertices()){
                float ew = graph.edgeWeight(pt1, pt2);
                if(ew == 0 || Double.isNaN(ew))
                    continue;
                if(ew<0)
                    CostDiff-= ew;
                else
                    CostDiff-= ew;
            }
        return CostDiff;
    }
    
   /**
    * This method returns the difference of the cost (a positive value means increase in cost, and a 
    * negative value means decrease in cost) if we move the given vertex to the cluster clt from its original
    * cluster
    * @param graph
    * @param toMove
    * @param sourceClr
    * @param destClr
    * @return 
    */
    private float computeMoveCost(Graph graph, Vertex toMove, 
            Cluster sourceClr, Cluster destClr){
        //check if the point toMove belongs to the cluster sourceClr
        if(toMove.getClustNum() != sourceClr.getClustIdx()){
            System.out.println("(BiForceOnGraph4 computeMoveCost) Vertex to move is in cluster: "
                    + toMove.getClustNum());
            System.out.println("(BiForceOnGraph4 computeMoveCost) Dest cluster: "
                    + sourceClr.getVertices().get(0).getClustNum());
            throw new IllegalArgumentException("(BiForceOnGraph4 computeMoveCost) "
                    + "The point to move does not belong to the source cluster.");
        }
        float costDiff = 0f;
        /* Compute the costDiff for moving the given vertex out of the sourceClr. */
        for(Vertex vtx: sourceClr.getVertices()){
            float ew = graph.edgeWeight(toMove, vtx);
            /* If there is a non-defined edge, then we continue. */
            if(Double.isNaN(ew))
                continue;
            /* If there is a negative-edge, it gives negative difference since we have to 
             delete the inserted edge.*/
            if(ew <0)
                costDiff += ew;
            /* If there is a positive-edge, it gives positive difference since we have to
             delete the original edge.*/
            else
                costDiff += ew;
        }
        /* Compute the costDiff for moving the given vertex into the given destClr. */
        for(Vertex vtx: destClr.getVertices()){
            
            float ew = graph.edgeWeight(toMove,vtx);
            /* If there is a non-defined edge, then we continue. */
            if(ew == 0)
                continue;
            /* If there is a negative-edge, it gives positive difference since we have to 
              insert the missing edge.*/
            if(ew<0)
                costDiff -= ew;
            /* If there is a positive-edge, it gives negative difference since we have to
             insert back the deleted edge.*/
            else
                costDiff -= ew;
        }
        return costDiff;
    }
    
    /**
     * This method implements the cooling process.
     * @param M0
     * @param iter
     * @return 
     */
    private float cooling(float M0, int iter)
    {
        
        if(iter == 1)
            return M0;
        else if(iter == 2)
            return M0/3.0f*2.0f;
        else
            return M0/iter*3;
    }
    
    /**
     * This method calculates and returns the displacement vector.
     * @param graph
     * @param p
     * @return 
     */
    private float[][] displace(Graph graph, Param p, int iter){
        float[][] dispVectors = new float[graph.vertexCount()][p.getDim()];
        
        /* To avoid unnecessary calculation, we calculate attCoeff and repCoeff first. */
        float attCoeff =p.getFatt()/graph.vertexCount();
        float repCoeff = p.getFrep()/graph.vertexCount();
        
        /* Calculate the displacement vector for each point. */
        for(int i=1;i<graph.vertexCount();i++){
            for(int j=0;j<i;j++){
                Vertex vtx1 = graph.getVertices().get(i);
                Vertex vtx2 = graph.getVertices().get(j);
                float ew = graph.edgeWeight(vtx1, vtx2);
                /* If there is no edge between Pt1 and Pt2, continue. */
                if(Double.isNaN(ew))
                    continue;
                float dist = graph.dist(vtx1,vtx2);
                if(dist == 0)
                    continue;
                /* Calculate the force */
                /* If there is an edge between two node, then we calculate 
                 * the attration force threshold. */
                float force;
                
                //if(ew >p.getThresh()){
                if(ew>0){
                    //force = (float)((ew- p.getThresh())*Math.log10(dist+1)*attCoeff/dist);
                    force =(float)(ew*Math.log10(dist+1)*attCoeff/dist);
                }
                else{
                    /* Else, we calculate the repulsion force. */
                    //force = (float)(repCoeff*(ew - p.getThresh())/Math.log10(dist+1)/dist);
                    force =(float)(repCoeff*ew /Math.log10(dist+1)/dist);
                }
                //for each dim
                for(int d=0;d<p.getDim();d++){
                    //update the displacement vector for Pt1
                    dispVectors[i][d] += (vtx2.getCoords()[d] - vtx1.getCoords()[d])*force;
                    //update the displacement vector for Pt2, with same magnitude but opposite direction;
                    dispVectors[j][d] -= (vtx2.getCoords()[d] - vtx1.getCoords()[d])*force;
                }
            }
        }
        /* Adjust the dispVectors according to the cooling factor. */
        float Mi = cooling(p.getM0(),iter);
        for(int i=0;i<graph.vertexCount();i++){
            /* The magnitude of the original displacement vector. */
            float Mag = getMagnitude(dispVectors[i]);
            //System.out.println("i: "+iter+"\tmagnitude: "+Mag+"\tcooling: "+Mi);
            /* The minor of Mi and Mag. */
            float MinM = Math.min(1, Mi/Mag);
            for(int j=0;j<p.getDim();j++)
                dispVectors[i][j] *= MinM;
        }
        return dispVectors;
    }
    
    
    /**
     * This method returns the first unassigned vertex in the vertex list. 
     * If there is no unassigned vertex, then null is returned.
     * @param vertexList
     * @return 
     */
    private Vertex getFirstUnassignedVertex(ArrayList<Vertex> VertexList){
        for(Vertex vtx: VertexList){
            if(vtx.getClustNum() == -1)
                return vtx;
        }
        return null;
    }
    
    
    /**
     * This method returns the magnitude of a vector based on coordinates.
     * @param Coords
     * @return 
     */
    private float getMagnitude(float[] Coords)
    {
        float M = 0;
        for(int i=0;i<Coords.length;i++){
            M+= Coords[i]*Coords[i];
        }
        return (float)Math.sqrt(M);
    }
    
    /**
     * This method initializes the layout of a given graph.
     * @param graph
     * @param p
     * @return 
     */
    private ArrayList<Vertex> initLayout(Graph graph, Param p){
        
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
        float inc = (float)(Math.PI * (3- Math.sqrt(5)));
        float off = (float)2.0/vtces;
        while(it.hasNext()){
            float[] coords = new float[dim];
            //retrieve the vertex
            Vertex vtx=(Vertex)it.next();
            // if dim==2 distribute points equidistantly on the circle
            if (dim==2){
                //coords[0] = (radius*Math.cos((i*2*Math.PI)/vtces)) + rd.nextGaussian();
                coords[0] = (float)(radius*Math.cos((i*2*Math.PI)/vtces));
                //coords[1] = radius*Math.sin((i*2*Math.PI)/vtces)+rd.nextGaussian();
                coords[1] = (float)(radius*Math.sin((i*2*Math.PI)/vtces));
                vtx.setCoords(coords);
                vertexList.add(vtx);
                i++;
            }
            //if dim == 3, distribute the points uniformly on the surface of the sphere
            //Here, we use an algorithm called on greek golden ratio, the "Golden Section Spiral"
            else if(dim == 3)
            {
                float y = i * off -1 +(off/2.0f);
                float r = (float)Math.sqrt(1-y*y);
                float phi = i*inc;
                coords[0] = (float)Math.cos(phi)*r*radius;
                coords[1] = y*radius;
                coords[2] = (float)Math.sin(phi)*r*radius;
                vtx.setCoords(coords);
                vertexList.add(vtx);
                i++;
            }
            else {
                float norm=0;
                //generate dim pseudorandom numbers between -1 and 1
                // normalize these vectors and scale them to a length of 50
                for (int j=0; j<dim; j++){
                    coords[j] = rd.nextFloat()*2 -1;
                    norm += Math.pow(coords[j], 2);
                }
                norm = (float)Math.sqrt(norm);
                for (int j=0; j<dim; j++){
                    coords[j] /= norm;
                    coords[j] *= radius;
                }
                vtx.setCoords(coords);
                vertexList.add(vtx);
            }
        }
        return vertexList;
    }
    
    
    /**
     * This is the version 2 of k-means clustering, with some bug fixed.
     * @param Points
     * @param k 
     */
    private void kmeansClust(Graph input, int k, Param p){
        /* Init the cluster number of the vertices. */
        for(Vertex vtx: input.getVertices())
            vtx.setClustNum(-1);
        /* Randomly choose k points as k centroids. */
        ArrayList<Integer> vtxIndexes = new ArrayList<>();
        /* Init the coords array for centroids. */
        float[][] centroids = new float[k][p.getDim()];
        /* Create an array to record the sizes of all clusters. */
        int[] clusterSizes = new int[k];
        for(int i=0;i<clusterSizes.length;i++){
            clusterSizes[i] = 0;
        }
        /* Init the coordinates of centroids. */
        for (float[] cen : centroids){
            for (int j = 0; j<p.getDim(); j++){
                cen[j] = -1;
            }
        }
        /* Randomly pick up k nodes as the initial k centroids. */
        for(int i=0;i<input.vertexCount();i++){
            vtxIndexes.add(i);
        }
        vtxIndexes.trimToSize();

        for(int i =0 ;i<k;i++){
            int idx = (int)(Math.random()*vtxIndexes.size());
            /* Create the coordinate array, and copy the coordinates*/
            System.arraycopy(input.getVertices().get(i).getCoords(), 0, 
                    centroids[i], 0, p.getDim());
            vtxIndexes.remove(idx);    
        }
        
        boolean isConverged = false;
        while(!isConverged){
            isConverged = true;
            /* Assign the points to closest centroid. */
            for(Vertex vtx:input.getVertices()){
                float minDist = Float.MAX_VALUE;
                int closestCentroidIdx = -1;
                for(int i=0;i<centroids.length;i++){
                    float dist = euclidDist(vtx,centroids[i],p);
                    if(dist<minDist){
                        minDist = dist;
                        closestCentroidIdx = i;
                    }
                }
                
                /* Check the validity of closestCentroidIdx, minDist. */
                if(closestCentroidIdx == -1)
                    throw new IllegalArgumentException("(BiForceOnGraph4 kmeansClust) "
                            + "Nearest centroid idx cannot be -1:  "+vtx.getValue());
               /* Check if any change is made. */ 
               if(vtx.getClustNum() != closestCentroidIdx)
                   isConverged = false;
               vtx.setClustNum(closestCentroidIdx);
               clusterSizes[closestCentroidIdx]++;
            }
            
            /* After assigning clusters, 
             * re-compute the coordinates for centroids. */
            for(int i=0;i<centroids.length;i++){
                /* Jump over the clusters with no points in it. */
                if(clusterSizes[i] == 0)
                    continue;
                /* For the cluster with point(s), 
                 * re-initialize the coordinates of centroids. */
                for(int j=0;j<p.getDim();j++)
                    centroids[i][j] = 0;
            }
            /* Re-compute the coordinates for centroids. */
            /* First compute addition. */
            for(Vertex vtx:input.getVertices()){
                for(int i=0;i<p.getDim();i++)
                    centroids[vtx.getClustNum()][i] += vtx.getCoords()[i];
            }
            /* Second compute the average. */
            for(int i=0;i<centroids.length;i++){
                /*If no points in this cluster then we do nothing. */
                if(clusterSizes[i] == 0){}
                else{
                    for(int j=0;j<p.getDim();j++)
                        centroids[i][j]/=clusterSizes[i];
                }
            }
        }
        
        /* Finally, check if there's any point unassigned. */
        for(Vertex vtx:input.getVertices()){
            if(vtx.getClustNum() == -1)
                throw new IllegalArgumentException("Not all points are assigned with clusters");
        }
    }
    
    
    /**
     * This is the main entrance of the algorithm, it runs Bi-Force on a given 
     * MatrixBipartiteGraph object.
     * @param graph
     * @param p
     * @param clType The type of single-linkage clustering: 1, normal sl. 2, chain sl.
     * @param isMultipleThresh If multiple thresholds are inputed.
     * @return
     * @throws IOException 
     */
    public Graph run(Graph graph, Param p, int clType, boolean isMultipleThresh) throws IOException{
        /* First we have to detract the threshold. */
        System.out.println("Detract the threshold.");
        if(!graph.isThreshDetracted()){
            if(isMultipleThresh) // If in this compute we use multiple threshold.
                graph.detractThresh(p.getThreshArray());
            else graph.detractThresh(p.getThresh());
        }
        System.out.println("Threshold detracted.");
        /* Compute the intial layout. */
        System.out.println("Compute the initial layout.");
        initLayout(graph, p);
        System.out.println("Compute the displacement and update the nodes position.");
        graph.updateDist();
        /* Write to log file:
        1. The vertex info.
        2. The distance info.
        3. The edge weight info.
        */
        /*
        graph.writeVertexInfo(Setting.VERTEX_LOG+"_start");
        graph.writeDistanceMatrix(Setting.DIST_LOG+"_start");
        for(int i=0;i<graph.vertexSetCount()-1;i++){
            graph.writeIntraEwMatrix(Setting.INTRA_EW_LOG+i+"_start", i);
            graph.writerInterEwMatrix(Setting.INTER_EW_LOG+i+"_start", i);
        }
        graph.writeIntraEwMatrix(Setting.INTRA_EW_LOG+(graph.vertexSetCount()-1), graph.vertexSetCount()-1);
        */
            
        /* For a certain number of iterations, we compute the displacement 
         * vector and update the vertex positions. */   
        
        for(int i=0;i< p.getMaxIter();i++){
            /*
            if(i == p.getMaxIter()/2){
                graph.writeVertexInfo(Setting.VERTEX_LOG+"_mid");
                graph.writeDistanceMatrix(Setting.DIST_LOG+"_mid");
                for(int idx=0;idx<graph.vertexSetCount()-1;idx++){
                    graph.writeIntraEwMatrix(Setting.INTRA_EW_LOG+idx+"_mid", idx);
                    graph.writerInterEwMatrix(Setting.INTER_EW_LOG+idx+"_mid", idx);
                }
                graph.writeIntraEwMatrix(Setting.INTRA_EW_LOG+(graph.vertexSetCount()-1)+"_mid"
                    , graph.vertexSetCount()-1);
            }
                */
            graph.updatePos(displace(graph,p,i));
            graph.updateDist();
            if(i % 20==0)
                System.out.println("Iteration: "+i);
        }
        System.out.println("Displacement completed.");
        
        /*Starting from the lower bound of the threshold, 
         *until the upper bound of the threshold.We try to find the dist 
         *threhold resulting in the smallest editing cost for test, 
         *we record the best DistThr. */
        if(clType == 1){
            normalPartition(graph, p);
        }
        else if(clType == 2){
            chainPartition(graph, p);
        }
        else if(clType == 3){
            chainPartition2(graph,p);
        }
        
        
        System.out.println("Assigning actions.");
        // Assign actions based on the current clusters
        assignActions(graph);
        // Carry out all actions.
        graph.takeActions();
        // Final info output.
        System.out.println("Outputting the results.");
        /*
        graph.writeVertexInfo(Setting.VERTEX_LOG+"_final");
        graph.writeDistanceMatrix(Setting.DIST_LOG+"_final");
        for(int idx=0;idx<graph.vertexSetCount()-1;idx++){
            graph.writeIntraEwMatrix(Setting.INTRA_EW_LOG+idx+"_final", idx);
            graph.writerInterEwMatrix(Setting.INTER_EW_LOG+idx+"_final", idx);
        }
        graph.writeIntraEwMatrix(Setting.INTRA_EW_LOG+(graph.vertexSetCount()-1)+"_final"
            , graph.vertexSetCount()-1);
        graph.writeIntraEwMatrix(Setting.INTRA_EW_LOG+(graph.vertexSetCount()-1), graph.vertexSetCount()-1);
        */
        return graph;
    }
    
    
    
    /**
     * This method implements normal partition after displacement.
     * @param graph
     * @param p 
     */
    public void normalPartition(Graph graph, Param p ){
        System.out.println("Partition the nodes. ");
        float minCostSL = Float.MAX_VALUE;
        float costAti;
        float minCostThresh=0;
        System.out.println("Start single-linkage partitioning.");
        int[] slClusters = new int[graph.getVertices().size()];
        // Normal sl clustering
        
        for(float i = p.getLowerth(); i<= p.getUpperth();i+= p.getStep()){
        singleLinkageClust(graph,i);
        costAti = computeCost(graph);
        if(costAti< minCostSL){
            minCostSL = costAti;
            minCostThresh = i;
            //record the cluster assignment
            for(int j=0;j<graph.getVertices().size();j++)
                slClusters[j] = graph.getVertices().get(j).getClustNum(); 
            }
        }
        
        
        
        float minCostKmeans = Float.MAX_VALUE;
        int minK = 0;
        float costAtk;
        int[] kmeansClusters = new int[graph.getVertices().size()];
        System.out.println("Start k-mean partitioning.");
        //in case the initial k=2 is larger than or equal to VertexList.size()/3

        if(graph.getVertices().size() < 9){
            for(int k=2;k<=4;k++){
                kmeansClust(graph,k,p);
                costAtk = computeCost(graph);
                if(minCostKmeans == -1 || costAtk < minCostKmeans){
                    minCostKmeans = costAtk;
                    minK = k;
                    //record the cluster assignment 
                    for(int i=0;i<graph.getVertices().size();i++)
                        kmeansClusters[i] = graph.getVertices().get(i).getClustNum();
                }
            }
        }
        else if(graph.getVertices().size()<200){
            for(int k=2;k<graph.getVertices().size()/4;k++){
                kmeansClust(graph,k,p);
                costAtk = computeCost(graph);
                if(minCostKmeans == -1 || costAtk < minCostKmeans){
                    minCostKmeans = costAtk;
                    minK = k;
                    //record the cluster assignment 
                    for(int i=0;i<graph.getVertices().size();i++)
                        kmeansClusters[i] = graph.getVertices().get(i).getClustNum();
                }
            }
        }
        else{
             for(int k=2;k<20;k++){
                kmeansClust(graph,k,p);
                costAtk = computeCost(graph);
                if(minCostKmeans == -1 || costAtk < minCostKmeans){
                    minCostKmeans = costAtk;
                    minK = k;
                    //record the cluster assignment 
                    for(int i=0;i<graph.getVertices().size();i++)
                        kmeansClusters[i] = graph.getVertices().get(i).getClustNum();
                }
            }
        }

        
        // Check whether single linkage or kmeans give the better result
        if(minCostKmeans <= minCostSL){
            System.out.println("K means is better.");
            // System.out.println("Best k is "+minK);
            // Restore the cluster assignment of kmeans
            for(int i=0;i<graph.getVertices().size();i++)
                graph.getVertices().get(i).setClustNum(kmeansClusters[i]);
        }
        else{
            System.out.println("Sl is better.");
            // Restore the cluster assignments of singlelinkage clustering
            for(int i=0;i<graph.getVertices().size();i++)
                graph.getVertices().get(i).setClustNum(slClusters[i]);
        }
        //Assign the clusters
        assignClusters(graph);
        
        // For test.
        graph.writeClusterTo("./clusters_before_post.txt", true);
        System.out.println("Partitioning completed.");
        // Post-processing
        // Step 1, merge Clusters
        System.out.println("Start post-processing: 1. Merging Clusters.");
        postProMerge(graph);
        // Step 2. Moving vertex.
        System.out.println("Post-processing: 2. Moving vertex. ");
        postProMove(graph);
        System.out.println("Post-processing complete.");
        // For test
        graph.writeClusterTo("./clusters_after_post.txt", true);
        
        
    }
    
    
    /**
     * This method implements chain partition after displacement.
     * @param graph
     * @param p 
     */
    public void chainPartition(Graph graph, Param p){
        System.out.println("Partition the nodes. ");
        float minCostSL = Float.MAX_VALUE;
        float costAti;
        float minCostThresh=0;
        System.out.println("Start single-linkage partitioning.");
        int[] slClusters = new int[graph.getVertices().size()];
        for(float i = p.getLowerth(); i<= p.getUpperth();i+= p.getStep()){
                singleLinkageClustChain2(graph,i);
                costAti = computeCostChain(graph);
                if(costAti< minCostSL){
                    minCostSL = costAti;
                    minCostThresh = i;
                    //record the cluster assignment
                    for(int j=0;j<graph.getVertices().size();j++)
                        slClusters[j] = graph.getVertices().get(j).getClustNum(); 
                }
            }
        float minCostKmeans = Float.MAX_VALUE;
        int minK = 0;
        float costAtk;
        int[] kmeansClusters = new int[graph.getVertices().size()];
        System.out.println("Start k-mean partitioning.");
        if(graph.getVertices().size() < 9){
            for(int k=2;k<=4;k++){
                kmeansClustChain(graph,k,p);
                costAtk = computeCostChain(graph);
                if(minCostKmeans == -1 || costAtk < minCostKmeans){
                    minCostKmeans = costAtk;
                    minK = k;
                    //record the cluster assignment 
                    for(int i=0;i<graph.getVertices().size();i++)
                        kmeansClusters[i] = graph.getVertices().get(i).getClustNum();
                    }
                }
            }
            else if(graph.getVertices().size()<200){
                for(int k=2;k<graph.getVertices().size()/4;k++){
                    kmeansClustChain(graph,k,p);
                    costAtk = computeCostChain(graph);
                    if(minCostKmeans == -1 || costAtk < minCostKmeans){
                        minCostKmeans = costAtk;
                        minK = k;
                        //record the cluster assignment 
                        for(int i=0;i<graph.getVertices().size();i++)
                            kmeansClusters[i] = graph.getVertices().get(i).getClustNum();
                    }
                }
            }
            else{
                 for(int k=2;k<20;k++){
                    kmeansClustChain(graph,k,p);
                    costAtk = computeCostChain(graph);
                    if(minCostKmeans == -1 || costAtk < minCostKmeans){
                        minCostKmeans = costAtk;
                        minK = k;
                        //record the cluster assignment 
                        for(int i=0;i<graph.getVertices().size();i++)
                            kmeansClusters[i] = graph.getVertices().get(i).getClustNum();
                    }
                }
            }
        // Check whether single linkage or kmeans give the better result
        if(minCostKmeans <= minCostSL){
            System.out.println("K means is better.");
            // System.out.println("Best k is "+minK);
            // Restore the cluster assignment of kmeans
            for(int i=0;i<graph.getVertices().size();i++)
                graph.getVertices().get(i).setClustNum(kmeansClusters[i]);
        }
        else{
            System.out.println("Sl is better.");
            // Restore the cluster assignments of singlelinkage clustering
            for(int i=0;i<graph.getVertices().size();i++)
                graph.getVertices().get(i).setClustNum(slClusters[i]);
        }
        // If chain-clustering, then we need to assign clusters before chainClustering and chain assigning
        assignClustersChainBeforePostPro(graph);
        // For test.
        graph.writeClusterTo("./clusters_before_post.txt", true);
        System.out.println("Partitioning completed.");
        // Post-processing
        // Step 1, merge Clusters
        System.out.println("Start post-processing: 1. Merging Clusters.");
        postProMerge(graph);
        // Step 2. Moving vertex.
        System.out.println("Post-processing: 2. Moving vertex. ");
        postProMove(graph);
        System.out.println("Post-processing complete.");
        // For test
        graph.writeClusterTo("./clusters_after_post.txt", true);
        
        // If we are doing a chain clustering, then we do chain assigning. 
        chainAssign(graph);
        // Assign the clusters
        assignClusters(graph);
        
    }
    
    
    public void chainPartition2(Graph graph, Param p){
        System.out.println("Partition the nodes. ");
        float minCostSL = Float.MAX_VALUE;
        float costAti;
        float minCostThresh=0;
        System.out.println("Start single-linkage partitioning.");
        int[] slClusters = new int[graph.getVertices().size()];
        for(float i = p.getLowerth(); i<= p.getUpperth();i+= p.getStep()){
                singleLinkageClustChain2(graph,i);
                costAti = computeCostChain(graph);
                if(costAti< minCostSL){
                    minCostSL = costAti;
                    minCostThresh = i;
                    //record the cluster assignment
                    for(int j=0;j<graph.getVertices().size();j++)
                        slClusters[j] = graph.getVertices().get(j).getClustNum(); 
                }
            }
        float minCostKmeans = Float.MAX_VALUE;
        int minK = 0;
        float costAtk;
        int[] kmeansClusters = new int[graph.getVertices().size()];
        System.out.println("Start k-mean partitioning.");
        if(graph.getVertices().size() < 9){
            for(int k=2;k<=4;k++){
                kmeansClustChain(graph,k,p);
                costAtk = computeCostChain(graph);
                if(minCostKmeans == -1 || costAtk < minCostKmeans){
                    minCostKmeans = costAtk;
                    minK = k;
                    //record the cluster assignment 
                    for(int i=0;i<graph.getVertices().size();i++)
                        kmeansClusters[i] = graph.getVertices().get(i).getClustNum();
                    }
                }
            }
            else if(graph.getVertices().size()<200){
                for(int k=2;k<graph.getVertices().size()/4;k++){
                    kmeansClustChain(graph,k,p);
                    costAtk = computeCostChain(graph);
                    if(minCostKmeans == -1 || costAtk < minCostKmeans){
                        minCostKmeans = costAtk;
                        minK = k;
                        //record the cluster assignment 
                        for(int i=0;i<graph.getVertices().size();i++)
                            kmeansClusters[i] = graph.getVertices().get(i).getClustNum();
                    }
                }
            }
            else{
                 for(int k=2;k<20;k++){
                    kmeansClustChain(graph,k,p);
                    costAtk = computeCostChain(graph);
                    if(minCostKmeans == -1 || costAtk < minCostKmeans){
                        minCostKmeans = costAtk;
                        minK = k;
                        //record the cluster assignment 
                        for(int i=0;i<graph.getVertices().size();i++)
                            kmeansClusters[i] = graph.getVertices().get(i).getClustNum();
                    }
                }
            }
        // Check whether single linkage or kmeans give the better result
        if(minCostKmeans <= minCostSL){
            System.out.println("K means is better.");
            // System.out.println("Best k is "+minK);
            // Restore the cluster assignment of kmeans
            for(int i=0;i<graph.getVertices().size();i++)
                graph.getVertices().get(i).setClustNum(kmeansClusters[i]);
        }
        else{
            System.out.println("Sl is better.");
            // Restore the cluster assignments of singlelinkage clustering
            for(int i=0;i<graph.getVertices().size();i++)
                graph.getVertices().get(i).setClustNum(slClusters[i]);
        }
        // If chain-clustering, then we need to assign clusters before chainClustering and chain assigning
        assignClustersChainBeforePostPro(graph);
        // For test.
        graph.writeClusterTo("./clusters_before_post.txt", true);
        System.out.println("Partitioning completed.");
        // Post-processing
        // Step 1, merge Clusters
        System.out.println("Start post-processing: 1. Merging Clusters.");
        postProMerge(graph);
        // Step 2. Moving vertex.
        System.out.println("Post-processing: 2. Moving vertex. ");
        postProMove(graph);
        System.out.println("Post-processing complete.");
        // For test
        graph.writeClusterTo("./clusters_after_post.txt", true);
        System.out.println("Start limit dist processing. ");
        // If we are doing a chain clustering, then we do chain assigning. 
        float minCost = Float.MAX_VALUE;
        for(float limitDist = 150;limitDist <200;limitDist+=2){
            System.out.println(limitDist/20*100+"% is finished.");
            chainAssign2(graph, limitDist);
            float cost = computeCost(graph);
            if(cost < minCost){
                for(int j=0;j<graph.getVertices().size();j++)
                        slClusters[j] = graph.getVertices().get(j).getClustNum(); 
            }
        }
        // Restore the cluster assignments of best limit dist.
        for(int i=0;i<graph.getVertices().size();i++)
            graph.getVertices().get(i).setClustNum(slClusters[i]);
        // Assign the clusters
        assignClusters(graph);
    }
    
    /**
     * This method is used after singleLinkageClustChain2() to assing clusters to 
     * the vertices other than the vertices in set 0.
     * @param graph 
     * @param limitDist 
     */
    public void chainAssign2(Graph graph, float limitDist){
        // Get the max cluster index.
        int maxClusterIdx = 0;
        for(int i=0;i<graph.vertexCount();i++)
            if(maxClusterIdx < graph.getVertices().get(i).getClustNum())
                maxClusterIdx = graph.getVertices().get(i).getClustNum();
        // Assign the clust num to the vertices in other sets.
        for(int i=1;i<graph.vertexSetCount();i++){
            ArrayList<Vertex> vertices = graph.getVertices();
            for(Vertex vtx: vertices){
                if(vtx.getVtxSet() != i)
                    continue;
                int idx = getClosestClustNum2(graph,vtx,limitDist);
                if(idx == -1)
                    vtx.setClustNum(maxClusterIdx++);
                else  vtx.setClustNum(getClosestClustNum(graph,vtx));
            }
        }
    }
    /**
     * Kmeans for chain clustering.
     * @param input
     * @param k
     * @param p 
     */
    private void kmeansClustChain(Graph input, int k, Param p){
        /* Init the cluster number of the vertices. */
        for(Vertex vtx: input.getVertices())
            vtx.setClustNum(-1);
        /* Randomly choose k points as k centroids. */
        ArrayList<Integer> vtxIndexes = new ArrayList<>();
        /* Init the coords array for centroids. */
        float[][] centroids = new float[k][p.getDim()];
        /* Create an array to record the sizes of all clusters. */
        int[] clusterSizes = new int[k];
        for(int i=0;i<clusterSizes.length;i++){
            clusterSizes[i] = 0;
        }
        /* Init the coordinates of centroids. */
        for (float[] cen : centroids){
            for (int j = 0; j<p.getDim(); j++){
                cen[j] = -1;
            }
        }
        /* Randomly pick up k nodes as the initial k centroids. */
        for(int i=0;i<input.vertexCount();i++){
            vtxIndexes.add(i);
        }
        vtxIndexes.trimToSize();

        for(int i =0 ;i<k;i++){
            int idx = (int)(Math.random()*vtxIndexes.size());
            /* Create the coordinate array, and copy the coordinates*/
            System.arraycopy(input.getVertices().get(i).getCoords(), 0, 
                    centroids[i], 0, p.getDim());
            vtxIndexes.remove(idx);    
        }
        
        boolean isConverged = false;
        while(!isConverged){
            isConverged = true;
            /* Assign the points to closest centroid. */
            for(Vertex vtx:input.getVertices()){
                if(vtx.getVtxSet() != 0)
                    continue;
                float minDist = Float.MAX_VALUE;
                int closestCentroidIdx = -1;
                for(int i=0;i<centroids.length;i++){
                    float dist = euclidDist(vtx,centroids[i],p);
                    if(dist<minDist){
                        minDist = dist;
                        closestCentroidIdx = i;
                    }
                }
                
                /* Check the validity of closestCentroidIdx, minDist. */
                if(closestCentroidIdx == -1)
                    throw new IllegalArgumentException("(BiForceOnGraph4 kmeansClust) "
                            + "Nearest centroid idx cannot be -1:  "+vtx.getValue());
               /* Check if any change is made. */ 
               if(vtx.getClustNum() != closestCentroidIdx)
                   isConverged = false;
               vtx.setClustNum(closestCentroidIdx);
               clusterSizes[closestCentroidIdx]++;
            }
            
            /* After assigning clusters, 
             * re-compute the coordinates for centroids. */
            for(int i=0;i<centroids.length;i++){
                /* Jump over the clusters with no points in it. */
                if(clusterSizes[i] == 0)
                    continue;
                /* For the cluster with point(s), 
                 * re-initialize the coordinates of centroids. */
                for(int j=0;j<p.getDim();j++)
                    centroids[i][j] = 0;
            }
            /* Re-compute the coordinates for centroids. */
            /* First compute addition. */
            for(Vertex vtx:input.getVertices()){
                if(vtx.getVtxSet() != 0 )
                    continue; // Added for chain clustering.
                for(int i=0;i<p.getDim();i++)
                    centroids[vtx.getClustNum()][i] += vtx.getCoords()[i];
            }
            /* Second compute the average. */
            for(int i=0;i<centroids.length;i++){
                /*If no points in this cluster then we do nothing. */
                if(clusterSizes[i] == 0){}
                else{
                    for(int j=0;j<p.getDim();j++)
                        centroids[i][j]/=clusterSizes[i];
                }
            }
        }
        
        /* Finally, check if there's any point unassigned. */
        for(Vertex vtx:input.getVertices()){
            if(vtx.getVtxSet() != 0) // For vertices only in set 0.
                continue;
            if(vtx.getClustNum() == -1)
                throw new IllegalArgumentException("Not all points are assigned with clusters");
        }
        
        /* Chain clustering. */
        // Assign the clust num to the vertices in other sets.
        for(int i=1;i<input.vertexSetCount();i++){
            ArrayList<Vertex> vertices = input.getVertices();
            for(Vertex vtx: vertices){
                if(vtx.getVtxSet() != i)
                    continue;
                vtx.setClustNum(getClosestClustNum(input,vtx));
            }
        }
    }
    
    
      /**
     * This method is used after singleLinkageClustChain2() to assing clusters to 
     * the vertices other than the vertices in set 0.
     * @param graph 
     */
    public void chainAssign(Graph graph){
            
        // Assign the clust num to the vertices in other sets.
        for(int i=1;i<graph.vertexSetCount();i++){
            ArrayList<Vertex> vertices = graph.getVertices();
            for(Vertex vtx: vertices){
                if(vtx.getVtxSet() != i)
                    continue;
                vtx.setClustNum(getClosestClustNum(graph,vtx));
            }
        }
    }
    
    /**
     * This method assign a chain of vertex with the currentClustIdx, and return the first
     * unassigned vertex.
     * @param vertices
     * @return 
     */
    private Vertex getFirstUnassignedChain(Graph graph, int currentClustIdx){
        Vertex firstUnassigned = null;
        // Get the first unassigned vertex in the smallest level.
        for(int lvl = 0; lvl<graph.vertexSetCount();lvl++){
            for(Vertex vtx: graph.getVertices()){
                if(vtx.getVtxSet() == lvl && vtx.getClustNum() == -1){
                    firstUnassigned = vtx;
                    break;
                }
                else{}
            }
            if(firstUnassigned != null)
                break;
        }
        if(firstUnassigned == null)
            return null;
        firstUnassigned.setClustNum(currentClustIdx);
        Vertex chainSeed = firstUnassigned;
        // Get the chain
        for(int i= firstUnassigned.getVtxSet()+1;i<graph.vertexSetCount();i++){
            Vertex minDistVtx = null;
            float minDist = Float.MAX_VALUE;
            for(Vertex vtx: graph.getVertices()){
                float dist = graph.dist(chainSeed, vtx); // Compute the dist.
                if(vtx.getVtxSet() == i && dist<minDist){ // Get the vertex with minimum distance.
                    minDistVtx = vtx;
                    minDist = dist;
                }   
            }
            if(minDistVtx == null){
                System.err.println("(BiForceOnGraph4.getFirstUnassignedChain) minDistVtx is null:  "+chainSeed.getValue());
                return null;
            }
            minDistVtx.setClustNum(currentClustIdx);
            chainSeed = minDistVtx;   
        }
        return firstUnassigned;
    }
    
    /**
     * Get the first unassigned vertex in the first vtx set.
     * @param graph
     * @param currentClustIdx 
     */
    private Vertex getFirstUnassignedChain2(Graph graph, int currentClustIdx){
        for(Vertex vtx: graph.getVertices()){
            if(vtx.getClustNum() == -1&& vtx.getVtxSet() == 0){
                vtx.setClustNum(currentClustIdx);
                return vtx;
            }
                
        }
        return null;
    }
    
    
    /**
     * This is the "chain" version of single linkage clustering.
     * Step 1. Cluster set 0 
     * Step 2. post-processing
     * Step 3. knn assigning to other vertices.
     * @param graph
     * @param distThresh 
     */
    private void singleLinkageClustChain2(Graph graph, float distThresh){
        // For test
        try{
            FileWriter fw =new FileWriter("./test_sl_clusters.txt",true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("##############\t"+distThresh+"\n");
            bw.flush();
            bw.close();
            fw.close();
        }catch(IOException e){
            System.err.println("(BiForceOnGraph4.singleLinageClust) File writer init error.");
            return;
        }
        //init all the clust num to be -1
        for(Vertex vtx:graph.getVertices())
            vtx.setClustNum(-1);
        int currentClustIdx = 0;
        Vertex noClustVtx;
        
        // First cluster the first set. 
        while((noClustVtx = getFirstUnassignedChain2(graph,currentClustIdx))!= null){
            //check if all the vertex are assigned with some cluster number
            ArrayList<Vertex> forTest = new ArrayList<>();
            forTest.add(noClustVtx); // For test.
            //check if all the vertex are assigned with some cluster number
            Stack<Vertex> verticesToVisit = new Stack<>();
            verticesToVisit.add(noClustVtx);
            while(!verticesToVisit.isEmpty()){
                Vertex seed = verticesToVisit.pop();
                /* For all the other unassigned vertices in the VertexList. */
                for(Vertex vtx: graph.getVertices()){
                    if(vtx.getClustNum() != -1)
                        continue;

                    if( graph.dist(vtx,seed) < distThresh && vtx.getVtxSet() == 0){
                        vtx.setClustNum(currentClustIdx);
                        verticesToVisit.add(vtx);
                        forTest.add(vtx); // For test.
                    }
                }
            }
           
            testWriteSlClusters(forTest,currentClustIdx,"./test_sl_clusters.txt");
            forTest.clear();
            currentClustIdx++;
        }
        
    }
    
    /**
     * This method performs the post-processing step 1 moving vertex.
     * @param graph 
     */
    private void postProMerge(Graph graph){
        boolean next;   
        ArrayList<Cluster> clusters = graph.getClusters();
        do{
            next = false;
            Collections.sort(clusters);
            for(int i=0;i<clusters.size();i++)
                for(int j=i+1;j<clusters.size();j++){
                    //if this merge can really reduce the cost
                    if(computeMergeCost(graph, clusters.get(i),clusters.get(j))<0){
                        //then we merge the two clusters, assigning the cluster number to be the smaller one
                        clusters.get(i).addCluster(clusters.get(j));
                        
                        //remove cluster j
                        clusters.remove(j);
                        j--;//since the index j object is removed, all subsequent objects are shifted left, thus we have to 
                        // minus 1 from j.
                        //proceed to another round
                        next = true;
                    }
                }
            graph.setClusters(clusters);
            
        }while(next);
    }
    
    /**
     * This method performs the post-processing step 2 moving vertex.
     * @param graph 
     */
    private void postProMove(Graph graph){
        boolean next;   
        ArrayList<Cluster> clusters = graph.getClusters();
        // Step 2. Moving vertex.
        do{
            next = false;
            Collections.sort(clusters);
            for(int i=0;i<clusters.size();i++){
                ArrayList<Vertex> VerticesClusteri = clusters.get(i).getVertices();
                Iterator<Vertex> VtxIter = VerticesClusteri.iterator();
                while(VtxIter.hasNext()){
                    Vertex vtx = VtxIter.next();
                    for(int j=i+1;j<clusters.size();j++){
                        //if this merge can really reduce the cost
                        if(computeMoveCost(graph, vtx,clusters.get(i), clusters.get(j))<0){
                            //then we assign new cluster number to the current point
                            Cluster.movePoint(vtx, clusters.get(i), clusters.get(j));
                            next = true;
                            break;
                        }
                    }
                    if(next == true)
                        break;
                }
            }
        }while(next);
    }
    
    /**
     * This method performs the complete linkage chain clustering.
     * 
     * cluster.dist = max(c1,c2);
     * First assign single-node clusters. then check all pairs. 
     * If clusters.dist < thresh. Then merge.
     * @param graph
     * @param distThresh 
     */
    public void completeLinkageClust(Graph graph, float distThresh){
        ArrayList<Vertex> vertices = graph.getVertices();
        ArrayList<Cluster> clusters = new ArrayList<>();
        for(int i=0;i<vertices.size();i++){
            vertices.get(i).setClustNum(i);
            Cluster clust = new Cluster();
            clust.addVertex(vertices.get(i));
            clusters.add(clust);
        }
        boolean merged = true;
        while(merged){
            merged = false;
            // Check all pairs and merge possible pairs.
            for(int i=0;i<clusters.size();i++)
                for(int j=i+1;j<clusters.size();j++){
                    if(Cluster.dist(clusters.get(i), clusters.get(j), graph, 2)<distThresh){
                        clusters.get(i).addCluster(clusters.get(j));
                        clusters.remove(j);
                        j--;
                        merged = true;
                    }
                }
            
        }
            
    }
    
    /**
     * This method computes the complete linkage cluster for chain clustering.
     * @param graph
     * @param distThresh 
     */
    public void completeLinkageClustChain(Graph graph, float distThresh){
        ArrayList<Vertex> vertices = new ArrayList<>();
        for(int i=0;i<graph.vertexCount();i++)
            if(graph.getVertices().get(i).getVtxSet() == 0)
                vertices.add(graph.getVertices().get(i));
        
        ArrayList<Cluster> clusters = new ArrayList<>();
        for(int i=0;i<vertices.size();i++){
            vertices.get(i).setClustNum(i);
            Cluster clust = new Cluster();
            clust.addVertex(vertices.get(i));
            clusters.add(clust);
        }
        boolean merged = true;
        while(merged){
            merged = false;
            // Check all pairs and merge possible pairs.
            for(int i=0;i<clusters.size();i++)
                for(int j=i+1;j<clusters.size();j++){
                    if(Cluster.dist(clusters.get(i), clusters.get(j), graph, 2)<distThresh){
                        clusters.get(i).addCluster(clusters.get(j));
                        clusters.remove(j);
                        j--;
                        merged = true;
                    }
                }
            
        }
    }
    
    /**
     * Get the clust number of the nearst vertex in the upper layer.
     * @param graph
     * @param vtx
     * @return 
     */
    public int getClosestClustNum(Graph graph, Vertex vtx){
        if(vtx.getVtxSet() == 0)
            throw new IllegalArgumentException("(BiForceOnGraph4.getNearestClustNum) Vtx cannot be in the first vertex set.");
        
        ArrayList<Vertex> vertices = graph.getVertices();
        float minDist = Float.MAX_VALUE;
        int minDistClustNum = -1;
        for(Vertex v: vertices){
            //if(v.getVtxSet() != vtx.getVtxSet()-1)
            if(v.getVtxSet() != 0)
                continue;
            float dist = graph.dist(v, vtx);
            if(dist < minDist){
                minDistClustNum = v.getClustNum();
                minDist = dist;
            }
        }
        if(minDistClustNum == -1)
            throw new IllegalStateException("(BiForceOnGraph4.getNearestClustNum) Nearest vtx is not found.");
        return minDistClustNum;
    }
    
    
    public int getClosestClustNum2(Graph graph, Vertex vtx, float limitDist){
        if(vtx.getVtxSet() == 0)
            throw new IllegalArgumentException("(BiForceOnGraph4.getNearestClustNum) Vtx cannot be in the first vertex set.");
        
        ArrayList<Vertex> vertices = graph.getVertices();
        float minDist = Float.MAX_VALUE;
        int minDistClustNum = -1;
        for(Vertex v: vertices){
            //if(v.getVtxSet() != vtx.getVtxSet()-1)
            if(v.getVtxSet() != 0)
                continue;
            float dist = graph.dist(v, vtx);
            if(dist < minDist){
                minDistClustNum = v.getClustNum();
                minDist = dist;
            }
        }
        if(minDist> limitDist)
            return -1;
        else return minDistClustNum;
    }
    /**
     * This method performs single-linkage cluster.
     * @param VertexList
     * @param distThresh 
     */
    private void singleLinkageClust(Graph graph, float distThresh){
        // For test
        try{
            FileWriter fw =new FileWriter("./test_sl_clusters.txt",true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("##############\t"+distThresh+"\n");
            bw.flush();
            bw.close();
            fw.close();
        }catch(IOException e){
            System.err.println("(BiForceOnGraph4.singleLinageClust) File writer init error.");
            return;
        }
        //init all the clust num to be -1
        for(Vertex vtx:graph.getVertices())
            vtx.setClustNum(-1);
        int currentClustIdx = 0;
        Vertex noClustVtx;
        
        while((noClustVtx = getFirstUnassignedVertex(graph.getVertices()))!= null){
            noClustVtx.setClustNum(currentClustIdx);
            //check if all the vertex are assigned with some cluster number
            ArrayList<Vertex> forTest = new ArrayList<>();
            forTest.add(noClustVtx); // For test.
            
            Stack<Vertex> verticesToVisit = new Stack<>();
            verticesToVisit.add(noClustVtx);
            while(!verticesToVisit.isEmpty()){
                Vertex seed = verticesToVisit.pop();
                /* For all the other unassigned vertices in the VertexList. */
                for(Vertex vtx: graph.getVertices()){
                    if(vtx.getClustNum() != -1)
                        continue;

                    if( graph.dist(vtx,seed) < distThresh){
                        vtx.setClustNum(currentClustIdx);
                        verticesToVisit.add(vtx);
                        forTest.add(vtx); // For test.
                    }
                }
            }
            testWriteSlClusters(forTest,currentClustIdx,"./test_sl_clusters.txt");
            forTest.clear();
            /* Increase the cluster number by 1. */
            currentClustIdx++;
        }
    }
    
    /**
     * 
     * @param vtx
     * @param coords
     * @param p
     * @return 
     */
    private float euclidDist(Vertex vtx, float[] coords, Param p){
        //check if they have the same dimension
        //System.out.println(pt.getCoordinates().length);
        //System.out.println(coords.length);
        if(vtx.getCoords().length !=coords.length )
        {
            System.out.println(vtx.getCoords().length);
            System.out.println(coords.length);
            throw new IllegalArgumentException("Point 1 and the given coordinates have different dimension");
        }
        float Dist= 0;
        for(int i=0;i<p.getDim();i++)
        {
            Dist+= (vtx.getCoords()[i]-coords[i])*
                    (vtx.getCoords()[i]-coords[i]);
        }
        Dist = (float)Math.sqrt(Dist);
        return Dist;
        
    }
    
    
    public void testWriteSlClusters(ArrayList<Vertex> clusters, int clustIdx, String output){
        try{
        FileWriter fw = new FileWriter(output,true);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(String.valueOf(clustIdx));
        for(Vertex vtx: clusters)
            bw.write("\t"+vtx.getValue());
        bw.write("\n");
        bw.flush();
        bw.close();
        fw.close();
        }catch(IOException e){
            System.err.println("(BiForceOnGraph4.testWriterSlClusters) Writer init error.");
        }
    }

}
