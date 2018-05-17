/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.index;

import java.util.Map;
import json.MapIndexToJSON;

/**
 *
 * @author zz17390
 */
public class ShuffleIndex {

    public static void main(String[] args) {
        Map kisyMap = createKisyMap();
        new MapIndexToJSON().write("index\\shuffle_format.json", kisyMap);

    }
}
