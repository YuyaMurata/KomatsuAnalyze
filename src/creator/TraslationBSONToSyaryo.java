/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator;

import creator.template.SyaryoTemplate;
import java.io.IOException;
import java.util.Map;
import json.SyaryoToZip;

/**
 *
 * @author ZZ17390
 */
public class TraslationBSONToSyaryo {
    public static void main(String[] args) throws IOException {
        String kisy = "PC200";
        String path = "..\\KomatsuData\\車両テンプレート\\" + kisy + "系\\";
        Map<String, SyaryoTemplate> syaryoTemplates = new SyaryoToZip().readTemplate(path+"syaryo_PC200_template_komtrax_gps.gz");
        
        for(String name : syaryoTemplates.keySet()){
            System.out.println(name);
            System.out.println(syaryoTemplates.get(name).map);
            syaryoTemplates.get(name).decompress();
            System.out.println(syaryoTemplates.get(name).map);
            
            System.exit(0);
        }
    }
}
