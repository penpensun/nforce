/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nforce.io;
import nforce.algorithms.Param;

/**
 *
 * @author penpen926
 */
public class Config {
    public String input = null;
    public String graphOutput = null;
    public String clusterOutput = null;
    public String editOutput = null;
    public boolean isXmlFormat = false;
    public boolean hasHeader = false;
    public Param p = null;
    public int graphType = -1;
    
    public Config(){
        
    }
    /**
     * This is the constructor
     * @param thresh
     * @param threshArray
     * @param input
     * @param graphOutput
     * @param clusterOutput
     * @param editOutput
     * @param isXmlFormat
     * @param hasHeader
     * @param p 
     * @param graphType 
     */
    public Config( 
            String input,
            String graphOutput,
            String clusterOutput,
            String editOutput,
            boolean isXmlFormat,
            boolean hasHeader,
            Param p,
            int graphType){
        this.input = input;
        this.graphOutput = graphOutput;
        this.clusterOutput = clusterOutput;
        this.editOutput = editOutput;
        this.isXmlFormat = isXmlFormat;
        this.hasHeader = hasHeader;
        this.graphType = graphType;
    }
    
    
}
