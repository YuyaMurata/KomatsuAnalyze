/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.ga;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ZZ17390
 */
public class Population {
    public List<Integer> g = new ArrayList();
    public Double f = 0d;

    public Population(List g, Double f) {
        this.g = new ArrayList(g);
        this.f = f;
    }
    
    public Double getFit(){
        return Math.abs(f);
    }
    
    public String toString() {
        return g+":"+f;
    }
}
