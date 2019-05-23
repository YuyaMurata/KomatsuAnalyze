/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.form.item;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author ZZ17807
 */
public class FormDead {
    public static Map form(Map<String, List<String>> dead, String leastdate, List indexList) {
        if (dead == null) {
            //System.out.println("Not found Dead");
            return null;
        }

        Map<String, List<String>> map = new TreeMap();
        String date = "0";
        for (String d : dead.keySet()) {
            if (!d.equals("None")) {
                //System.out.println(d);
                if (Integer.valueOf(date) < Integer.valueOf(d)) {
                    date = d;
                }
            }
        }

        if (!date.equals("0") && Integer.valueOf(leastdate) <= Integer.valueOf(date)) {
            map.put(date, dead.get(date));
            return map;
        } else {
            return null;
        }
    }
}
