/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.cluster;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.converters.ConverterUtils;

/**
 *
 * @author ZZ17807
 */
public class WEKAClustering {
    
    private int k;
    private String file;
    private List<String> sid;
    
    public void set(String file, int k, List<String> sid) {
        this.file = file;
        this.k = k;
        this.sid = sid;
    }
    
    public Map<String, Integer> clustering() {
        Map<String, Integer> map = new TreeMap<>();
        
        try {
            SimpleKMeans kmeans = new SimpleKMeans();
            kmeans.setSeed(10);
            kmeans.setPreserveInstancesOrder(true);
            kmeans.setNumClusters(k);
            kmeans.setInitializationMethod(new SelectedTag(SimpleKMeans.KMEANS_PLUS_PLUS, SimpleKMeans.TAGS_SELECTION));
            
            Instances data = ConverterUtils.DataSource.read(this.file);
            
            kmeans.buildClusterer(data);
            
            int[] assignments = kmeans.getAssignments();
            
            int i = 0;
            for (int clusterNum : assignments) {
                //System.out.printf("Instance %d -> Cluster %d \n", i, clusterNum);
                map.put(sid.get(i), clusterNum);
                i++;   
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        
        return map;
    }
}
