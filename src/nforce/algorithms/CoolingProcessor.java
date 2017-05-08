/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nforce.algorithms;

/**
 *
 * @author GGTTF
 */
public class CoolingProcessor {

    /**
     * This method implements the cooling process.
     * @param M0
     * @param iter
     * @return
     */
    protected static float cooling(float M0, int iter) {
        if (iter == 1) {
            return M0;
        } else if (iter == 2) {
            return M0 / 3.0F * 2.0F;
        } else {
            return M0 / iter * 3;
        }
    }
    
}
