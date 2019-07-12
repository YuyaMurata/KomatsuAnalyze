/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package axg.form.item;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author ZZ17807
 */
public class FormAllSurpport {
    //オールサポートの整形　(解約日を終了日とする)
    public static Map form(Map<String, List<String>> as, List indexList) {
        if (as == null) {
            return null;
        }

        int kaiyaku = indexList.indexOf("満了日");

        Map newMap = new TreeMap();
        for (String date : as.keySet()) {
            String kiyk = as.get(date).get(kaiyaku);
            if (Integer.valueOf(date.split("#")[0]) <= Integer.valueOf(kiyk)) {
                newMap.put(date.split("#")[0], as.get(date));
            }
        }

        if (newMap.isEmpty()) {
            return null;
        }

        return newMap;
    }
}
