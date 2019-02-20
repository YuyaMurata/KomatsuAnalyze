/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package viewer.fanction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.scene.control.ToggleButton;

/**
 *
 * @author kaeru
 */
public class SyaryoIDOrder {
    public static List<String> order(String filter, ToggleButton btn, Map<String, Integer> map){
        btnDisp(btn.isSelected(), btn);
        
        if (map == null) {
            return new ArrayList<>();
        }else if(filter.equals("ALL")) {
            return all(btn.isSelected(), map);
        } else{
            return data(btn.isSelected(), map);
        }
    }
    
    private static void btnDisp(Boolean enable, ToggleButton btn){
        if(enable)
            btn.setText("D");
        else
            btn.setText("A");
    }

    private static List<String> all(Boolean enable, Map<String, Integer> map){
        if(enable)
            return map.keySet().stream()
                            .sorted(Comparator.reverseOrder())
                            .collect(Collectors.toList());
        else
            return map.keySet().stream()
                            .sorted(Comparator.naturalOrder())
                            .collect(Collectors.toList());
    }
    
    private static List<String> data(Boolean enable, Map<String, Integer> map){
        if(enable)
            return map.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .map(e -> e.getKey())
                        .collect(Collectors.toList());
        else
            return map.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue())
                        .map(e -> e.getKey())
                        .collect(Collectors.toList());
    }
}
