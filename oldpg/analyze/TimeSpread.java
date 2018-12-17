/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyze;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 * @author zz17390
 */
public class TimeSpread {
    private TreeMap<Integer, Double> timeData;
    public TimeSpread() {
        timeData = new TreeMap<>();
    }
    
    public void set(int time, double value){
        timeData.put(time, value);
    }
    
    public Map divide(int t1, int t2){
        return timeData.subMap(t1, t2);
    }
    
    public Double get(int t){
        if(timeData.get(t) != null)
            return timeData.get(t);
        else
            return 0d;
    }
    
    public void check(){
        for(int i=0; i < timeData.lastKey(); i++){
            if(timeData.get(i) == null){
                System.out.println("time="+i);
                timeData.put(i, 0d);
            }
        }
    }
    
    public Integer size(){
        return timeData.size();
    }
    
    public Double[] getSpread(){
        return timeData.values().toArray(new Double[timeData.size()]);
    }
    
    public Integer[] getTime(){
        return timeData.keySet().toArray(new Integer[timeData.size()]);
    }
    
    public void dump(){
        System.out.println("t,value");
        for(Integer t : timeData.keySet())
            System.out.println(t+","+timeData.get(t));
    }
}
