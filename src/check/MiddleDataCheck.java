/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package check;

import creator.template.SyaryoTemplate;
import java.util.Map;
import json.SyaryoToZip;

/**
 *
 * @author ZZ17390
 */
public class MiddleDataCheck {
    public static void main(String[] args) {
        String kisy = "PC138US";
        String path = "..\\KomatsuData\\中間データ\\"+kisy+"\\";
		//String outpath = "middle\\";
        String FILENAME = path+"syaryo_mid_" + kisy+"_KMACT";
        
        Map<String, SyaryoTemplate> syaryoMap = new SyaryoToZip().readTemplate(FILENAME);
        
        for(String name : syaryoMap.keySet()){
            SyaryoTemplate syaryo = syaryoMap.get(name);
            syaryo.decompress();
            System.out.println(syaryo.map);
            syaryo.compress(false);
        }
    }
}
