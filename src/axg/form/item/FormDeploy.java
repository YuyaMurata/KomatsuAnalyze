/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package axg.form.item;

import creator.form.SyaryoObjectFormatting;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author ZZ17807
 */
public class FormDeploy {

    public static Map form(Map<String, List<String>> deploy, String pdate, String name) {
        Map<String, List<String>> map = new TreeMap();
        String id = name.split("-")[0] + "-" + name.split("-")[2];

        if (deploy != null) {
            if (deploy.containsKey("")) {
                map.put(pdate, Arrays.asList(new String[]{pdate}));
            } else {
                map.putAll(deploy);
            }
        } else {
            map.put(pdate, Arrays.asList(new String[]{pdate}));
        }

        return map;
    }
}
