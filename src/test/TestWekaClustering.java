/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import weka.clusterers.SimpleKMeans;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

/**
 *
 * @author ZZ17807
 */
public class TestWekaClustering {

    public static void main(String[] args) throws Exception {
        SimpleKMeans kmeans = new SimpleKMeans();
        kmeans.setPreserveInstancesOrder(true);
        kmeans.setNumClusters(5);

        Instances data = DataSource.read("PC200_mainte_eval.arff");

        kmeans.buildClusterer(data);

        int[] assignments = kmeans.getAssignments();

        int i = 0;
        for (int clusterNum : assignments) {
            System.out.printf("Instance %d -> Cluster %d \n", i, clusterNum);
            i++;

        }
    }
}
