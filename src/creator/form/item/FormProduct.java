/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.form.item;

import creator.form.SyaryoObjectFormatting;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author ZZ17807
 */
public class FormProduct {

    public static Map form(Map<String, List<String>> product, Map<String, String> index, String name) {
        Map<String, List<String>> map = new TreeMap();
        String id = name.split("-")[0] + "-" + name.split("-")[2];
        String date = product.keySet().stream().findFirst().get();
        String pdate = index.get(id);
        if (pdate != null) {
            if (Integer.valueOf(date) < Integer.valueOf(pdate)) {
                map.put(pdate, product.get(date));
                System.out.println(SyaryoObjectFormatting.currentKey + ":生産-" + pdate + " 異常:" + date);
            } else {
                map.put(date, product.get(date));
            }
        } else {
            map.put(date, product.get(date));
        }

        return map;
    }
}
