/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nforce.algorithms;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
/**
 *
 * @author Administrator
 */
public class Param {
    int maxIter = -1;
    float fatt = -1;
    float frep = -1;
    float M0 = -1;
    int dim = -1;
    float radius = -1;
    float thresh = Float.NaN;
    float[] threshArray = null;
    float upperth = -1;
    float lowerth = -1;
    float step=-1;
    
    /**
     * Init the param with different parameters
     * @param maxIter
     * @param fatt
     * @param frep
     * @param M0
     * @param dim
     * @param radius 
     * @param thresh
     * @param threshArray
     * @param upperth
     * @param lowerth
     * @param step
     */
    public Param(int maxIter, 
            float fatt, float frep, 
            float M0, int dim, float radius,
            float thresh,
            float[] threshArray,
            float upperth, float lowerth, float step)
    {
        this.maxIter = maxIter;
        this.fatt = fatt;
        this.frep = frep;
        this.M0=M0;
        this.dim=dim;
        this.radius=radius;
        this.thresh = thresh;
        this.upperth = upperth;
        this.lowerth = lowerth;
        this.step = step;
        this.threshArray = threshArray;
    }
    
    /**
     * Init with default values
     */
    public Param()
    {
        fatt = 2.4839598967501715f;
        frep = 1.3228008374592575f;
        M0=51.835535150936714f;
        radius = 112.46725298831082f;
        dim = 3;
        thresh = 0;
        threshArray = null;
        upperth = 200;
        lowerth = 0;
        step = 0.5f;
    }
    
    public Param(String parafile)
    {
        try{
        BufferedReader br = new BufferedReader(new FileReader(parafile));
        String line;
        while((line = br.readLine())!= null)
        {
            //jump the comment lines
            if(line.startsWith("#"))
                continue;
            String prefix = line.substring(0,line.indexOf('='));
            String value = line.substring(line.indexOf('=')+1);
            //
            try{
                switch (prefix) {
                    case "fatt":
                        fatt = Float.parseFloat(value);
                        break;
                    case "frep":
                        frep = Float.parseFloat(value);
                        break;
                    case "M0":
                        M0=Float.parseFloat(value);
                        break;
                    case "radius":
                        radius = Float.parseFloat(value);
                        break;
                    case "maxIter":
                        maxIter = Integer.parseInt(value);
                        break;
                    case "dim":
                        dim = Integer.parseInt(value);
                        break;
                    case "thresh":
                        String[] splits = value.split("\t");
                        if(splits.length >1){
                            threshArray = new float[splits.length];
                            for(int i=0;i<splits.length;i++)
                                threshArray[i] = Float.parseFloat(splits[i]);
                        }
                        else thresh= Float.parseFloat(value);
                        break;
                    case "upperth":
                        upperth = Float.parseFloat(value);
                        break;
                    case "lowerth":
                        lowerth= Float.parseFloat(value);
                        break;
                    case "step":
                        step= Float.parseFloat(value);
                        break;
                }
            }catch(NullPointerException | NumberFormatException e)
            {
                e.printStackTrace();
            }
        }
        //check if all parameters are initialized
        if(fatt ==-1 || frep == -1 ||M0 == -1 || radius == -1 || 
                maxIter == -1 || lowerth == -1 || upperth ==-1 || step ==-1)
            throw new IllegalArgumentException("Not all parameters are initialized");
        //check dim
        if(dim > 3 || dim <2)
            throw new IllegalArgumentException("Dimension must be 2 or 3");
        }
        catch(IOException e){
            System.out.println("(Param) IOException");
        }
    }
    
    public final float getFatt()
    {
        return fatt;
    }
    
    public final float getFrep()
    {
        return frep;
    }
    
    public final float getM0()
    {
        return M0;
    }
    
    public final float getRadius()
    {
        return radius;
    }
    
    public final int getMaxIter()
    {
        return maxIter;
    }
    
    
    public final int getDim()
    {
        return dim;
    }
    
    public final float getThresh()
    {
        return thresh;
    }
    
    public final float[] getThreshArray(){
        return threshArray;
    }
    
    public final float getLowerth()
    {
        return lowerth;
    }
    
    public final float getUpperth()
    {
        return upperth;
    }
    
    public final float getStep()
    {
        return step;
    }
    
    
    public final void setThresh(float thresh)
    {
        this.thresh = thresh;
    }
    
    public final void setThreshArray(float[] threshArray){
        this.threshArray = threshArray;
    }
    
    
    
    public static Param readParams(String paramfile){
        return new Param(paramfile);
    }
}
