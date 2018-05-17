/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package check;

import java.util.Map;
import json.JsonToSyaryoObj;
import obj.SyaryoObject1;

/**
 *
 * @author ZZ17390
 */
public class SummaryCheck {
    public static void main(String[] args) {
        Map<String, SyaryoObject1> syaryo = new JsonToSyaryoObj().reader("syaryo_obj_WA500.json");
        System.out.println(syaryo.entrySet().parallelStream()
                                    .filter(s -> s.getKey().equals("_summary"))
                                    .map(sum -> sum.getValue().getName())
                                    .findFirst().get());
    }
}
