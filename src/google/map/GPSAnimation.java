/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package google.map;

import com.lynden.gmapsfx.javascript.object.GoogleMap;
import java.util.List;
import java.util.TreeMap;

/**
 *
 * @author ZZ17390
 */
public class GPSAnimation extends Thread{
    private TreeMap timeToGPS;
    private Integer first, last;
    private Long animationTime = 10L;
    private GoogleMap map;
    
    public GPSAnimation(GoogleMap map, TreeMap<Integer, List> timeToGPS) {
        this.map = map;
        this.timeToGPS = timeToGPS;
        first = timeToGPS.firstKey();
        last = timeToGPS.lastKey();
    }
    
    @Override
    public void run(){
        
        for(int i=first; i < last+1; i++){
            
        }
        
        try {
            Thread.sleep(animationTime);
        } catch (InterruptedException ex) {
        }
    }
}
