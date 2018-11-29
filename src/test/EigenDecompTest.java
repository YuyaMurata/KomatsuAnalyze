/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;

/**
 *
 * @author ZZ17390
 */
public class EigenDecompTest {

    public static void main(String[] args) {
        //create points in a double array
        
        //create real matrix
        RealMatrix realMatrix = MatrixUtils.createRealMatrix(iris);

        //create covariance matrix of points, then find eigen vectors
        //see https://stats.stackexchange.com/questions/2691/making-sense-of-principal-component-analysis-eigenvectors-eigenvalues
        Covariance covariance = new Covariance(realMatrix);
        RealMatrix covarianceMatrix = covariance.getCovarianceMatrix();
        EigenDecomposition ed = new EigenDecomposition(covarianceMatrix);
        
        //固有値
        for(int i=0; i < 4; i++)
            System.out.println(ed.getRealEigenvalue(i));
        
        //固有値ベクトル
        RealMatrix sv = ed.getV().getSubMatrix(0, 3, 0, 1);
        RealMatrix mv = getDeviation(realMatrix);
        double[][] result = mv.multiply(sv).getData();
        
        for(int i=0; i < result.length; i++){
            for(int j=0; j < result[i].length; j++)
                System.out.print(result[i][j]+" ");
            System.out.println("");
        }
    }
    
    public static RealMatrix getDeviation(RealMatrix m){
        double[][] temp = m.transpose().getData();
        double[][] avgs = new double[temp.length][temp[0].length];
        double[][] dev = new double[temp.length][temp[0].length];
            
        //平均
        for(int i=0; i < temp.length; i++){
            final int idx = i;
            double avg = IntStream.range(0, temp[idx].length).mapToDouble(j -> temp[idx][j]).average().getAsDouble();
            for(int j=0; j < temp[i].length; j++)
                avgs[i][j] = avg;
        }
        
        //偏差
        RealMatrix result = m.subtract(MatrixUtils.createRealMatrix(avgs).transpose());
        
        return result;
    }

    static double[][] iris = new double[][]{
        new double[]{5.1, 3.5, 1.4, 0.2},
        new double[]{4.9, 3.0, 1.4, 0.2},
        new double[]{4.7, 3.2, 1.3, 0.2},
        new double[]{4.6, 3.1, 1.5, 0.2},
        new double[]{5.0, 3.6, 1.4, 0.2},
        new double[]{5.4, 3.9, 1.7, 0.4},
        new double[]{4.6, 3.4, 1.4, 0.3},
        new double[]{5.0, 3.4, 1.5, 0.2},
        new double[]{4.4, 2.9, 1.4, 0.2},
        new double[]{4.9, 3.1, 1.5, 0.1},
        new double[]{5.4, 3.7, 1.5, 0.2},
        new double[]{4.8, 3.4, 1.6, 0.2},
        new double[]{4.8, 3.0, 1.4, 0.1},
        new double[]{4.3, 3.0, 1.1, 0.1},
        new double[]{5.8, 4.0, 1.2, 0.2},
        new double[]{5.7, 4.4, 1.5, 0.4},
        new double[]{5.4, 3.9, 1.3, 0.4},
        new double[]{5.1, 3.5, 1.4, 0.3},
        new double[]{5.7, 3.8, 1.7, 0.3},
        new double[]{5.1, 3.8, 1.5, 0.3},
        new double[]{5.4, 3.4, 1.7, 0.2},
        new double[]{5.1, 3.7, 1.5, 0.4},
        new double[]{4.6, 3.6, 1.0, 0.2},
        new double[]{5.1, 3.3, 1.7, 0.5},
        new double[]{4.8, 3.4, 1.9, 0.2},
        new double[]{5.0, 3.0, 1.6, 0.2},
        new double[]{5.0, 3.4, 1.6, 0.4},
        new double[]{5.2, 3.5, 1.5, 0.2},
        new double[]{5.2, 3.4, 1.4, 0.2},
        new double[]{4.7, 3.2, 1.6, 0.2},
        new double[]{4.8, 3.1, 1.6, 0.2},
        new double[]{5.4, 3.4, 1.5, 0.4},
        new double[]{5.2, 4.1, 1.5, 0.1},
        new double[]{5.5, 4.2, 1.4, 0.2},
        new double[]{4.9, 3.1, 1.5, 0.2},
        new double[]{5.0, 3.2, 1.2, 0.2},
        new double[]{5.5, 3.5, 1.3, 0.2},
        new double[]{4.9, 3.6, 1.4, 0.1},
        new double[]{4.4, 3.0, 1.3, 0.2},
        new double[]{5.1, 3.4, 1.5, 0.2},
        new double[]{5.0, 3.5, 1.3, 0.3},
        new double[]{4.5, 2.3, 1.3, 0.3},
        new double[]{4.4, 3.2, 1.3, 0.2},
        new double[]{5.0, 3.5, 1.6, 0.6},
        new double[]{5.1, 3.8, 1.9, 0.4},
        new double[]{4.8, 3.0, 1.4, 0.3},
        new double[]{5.1, 3.8, 1.6, 0.2},
        new double[]{4.6, 3.2, 1.4, 0.2},
        new double[]{5.3, 3.7, 1.5, 0.2},
        new double[]{5.0, 3.3, 1.4, 0.2},
        new double[]{7.0, 3.2, 4.7, 1.4},
        new double[]{6.4, 3.2, 4.5, 1.5},
        new double[]{6.9, 3.1, 4.9, 1.5},
        new double[]{5.5, 2.3, 4.0, 1.3},
        new double[]{6.5, 2.8, 4.6, 1.5},
        new double[]{5.7, 2.8, 4.5, 1.3},
        new double[]{6.3, 3.3, 4.7, 1.6},
        new double[]{4.9, 2.4, 3.3, 1.0},
        new double[]{6.6, 2.9, 4.6, 1.3},
        new double[]{5.2, 2.7, 3.9, 1.4},
        new double[]{5.0, 2.0, 3.5, 1.0},
        new double[]{5.9, 3.0, 4.2, 1.5},
        new double[]{6.0, 2.2, 4.0, 1.0},
        new double[]{6.1, 2.9, 4.7, 1.4},
        new double[]{5.6, 2.9, 3.6, 1.3},
        new double[]{6.7, 3.1, 4.4, 1.4},
        new double[]{5.6, 3.0, 4.5, 1.5},
        new double[]{5.8, 2.7, 4.1, 1.0},
        new double[]{6.2, 2.2, 4.5, 1.5},
        new double[]{5.6, 2.5, 3.9, 1.1},
        new double[]{5.9, 3.2, 4.8, 1.8},
        new double[]{6.1, 2.8, 4.0, 1.3},
        new double[]{6.3, 2.5, 4.9, 1.5},
        new double[]{6.1, 2.8, 4.7, 1.2},
        new double[]{6.4, 2.9, 4.3, 1.3},
        new double[]{6.6, 3.0, 4.4, 1.4},
        new double[]{6.8, 2.8, 4.8, 1.4},
        new double[]{6.7, 3.0, 5.0, 1.7},
        new double[]{6.0, 2.9, 4.5, 1.5},
        new double[]{5.7, 2.6, 3.5, 1.0},
        new double[]{5.5, 2.4, 3.8, 1.1},
        new double[]{5.5, 2.4, 3.7, 1.0},
        new double[]{5.8, 2.7, 3.9, 1.2},
        new double[]{6.0, 2.7, 5.1, 1.6},
        new double[]{5.4, 3.0, 4.5, 1.5},
        new double[]{6.0, 3.4, 4.5, 1.6},
        new double[]{6.7, 3.1, 4.7, 1.5},
        new double[]{6.3, 2.3, 4.4, 1.3},
        new double[]{5.6, 3.0, 4.1, 1.3},
        new double[]{5.5, 2.5, 4.0, 1.3},
        new double[]{5.5, 2.6, 4.4, 1.2},
        new double[]{6.1, 3.0, 4.6, 1.4},
        new double[]{5.8, 2.6, 4.0, 1.2},
        new double[]{5.0, 2.3, 3.3, 1.0},
        new double[]{5.6, 2.7, 4.2, 1.3},
        new double[]{5.7, 3.0, 4.2, 1.2},
        new double[]{5.7, 2.9, 4.2, 1.3},
        new double[]{6.2, 2.9, 4.3, 1.3},
        new double[]{5.1, 2.5, 3.0, 1.1},
        new double[]{5.7, 2.8, 4.1, 1.3},
        new double[]{6.3, 3.3, 6.0, 2.5},
        new double[]{5.8, 2.7, 5.1, 1.9},
        new double[]{7.1, 3.0, 5.9, 2.1},
        new double[]{6.3, 2.9, 5.6, 1.8},
        new double[]{6.5, 3.0, 5.8, 2.2},
        new double[]{7.6, 3.0, 6.6, 2.1},
        new double[]{4.9, 2.5, 4.5, 1.7},
        new double[]{7.3, 2.9, 6.3, 1.8},
        new double[]{6.7, 2.5, 5.8, 1.8},
        new double[]{7.2, 3.6, 6.1, 2.5},
        new double[]{6.5, 3.2, 5.1, 2.0},
        new double[]{6.4, 2.7, 5.3, 1.9},
        new double[]{6.8, 3.0, 5.5, 2.1},
        new double[]{5.7, 2.5, 5.0, 2.0},
        new double[]{5.8, 2.8, 5.1, 2.4},
        new double[]{6.4, 3.2, 5.3, 2.3},
        new double[]{6.5, 3.0, 5.5, 1.8},
        new double[]{7.7, 3.8, 6.7, 2.2},
        new double[]{7.7, 2.6, 6.9, 2.3},
        new double[]{6.0, 2.2, 5.0, 1.5},
        new double[]{6.9, 3.2, 5.7, 2.3},
        new double[]{5.6, 2.8, 4.9, 2.0},
        new double[]{7.7, 2.8, 6.7, 2.0},
        new double[]{6.3, 2.7, 4.9, 1.8},
        new double[]{6.7, 3.3, 5.7, 2.1},
        new double[]{7.2, 3.2, 6.0, 1.8},
        new double[]{6.2, 2.8, 4.8, 1.8},
        new double[]{6.1, 3.0, 4.9, 1.8},
        new double[]{6.4, 2.8, 5.6, 2.1},
        new double[]{7.2, 3.0, 5.8, 1.6},
        new double[]{7.4, 2.8, 6.1, 1.9},
        new double[]{7.9, 3.8, 6.4, 2.0},
        new double[]{6.4, 2.8, 5.6, 2.2},
        new double[]{6.3, 2.8, 5.1, 1.5},
        new double[]{6.1, 2.6, 5.6, 1.4},
        new double[]{7.7, 3.0, 6.1, 2.3},
        new double[]{6.3, 3.4, 5.6, 2.4},
        new double[]{6.4, 3.1, 5.5, 1.8},
        new double[]{6.0, 3.0, 4.8, 1.8},
        new double[]{6.9, 3.1, 5.4, 2.1},
        new double[]{6.7, 3.1, 5.6, 2.4},
        new double[]{6.9, 3.1, 5.1, 2.3},
        new double[]{5.8, 2.7, 5.1, 1.9},
        new double[]{6.8, 3.2, 5.9, 2.3},
        new double[]{6.7, 3.3, 5.7, 2.5},
        new double[]{6.7, 3.0, 5.2, 2.3},
        new double[]{6.3, 2.5, 5.0, 1.9},
        new double[]{6.5, 3.0, 5.2, 2.0},
        new double[]{6.2, 3.4, 5.4, 2.3},
        new double[]{5.9, 3.0, 5.1, 1.8}
    };
}
