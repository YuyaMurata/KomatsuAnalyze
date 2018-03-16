/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package check;

import creator.template.SyaryoTemplate;
import java.util.Map;
import java.util.stream.Collectors;
import json.SyaryoToZip;

/**
 *
 * @author ZZ17390
 */
public class MiddleDataCheck {
    public static void main(String[] args) {
        String kisy = "PC138US";
        String path = "..\\KomatsuData\\中間データ\\";
		//String outpath = "middle\\";
        String FILENAME = path+"syaryo_mid_" + kisy;
        
        Map<String, SyaryoTemplate> syaryoMap = new SyaryoToZip().readTemplate(FILENAME);
        System.out.println(syaryoMap.keySet().stream().filter(s -> !s.equals("_summary")).map(s -> s.split("-")[1]).distinct().collect(Collectors.toList()));
        
        /*for(String name : syaryoMap.keySet()){
            SyaryoTemplate syaryo = syaryoMap.get(name);
            System.out.println(name);
            syaryo.decompress();
            System.out.println("\t"+syaryo.toString());
            syaryo.compress(false);
        }*/
    }
}
