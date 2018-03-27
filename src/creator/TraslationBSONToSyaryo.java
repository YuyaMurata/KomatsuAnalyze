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
        String kisy = "PC138US";
        //String path = "..\\KomatsuData\\車両テンプレート\\" + kisy + "系\\gz\\";
        String path = "json\\";
        //String filename = path+"syaryo_"+kisy+"_template_allsupport";
        String filename = path+"syaryo_obj_"+kisy;
        Map<String, SyaryoTemplate> syaryoTemplates = new SyaryoToZip().readTemplate(filename);
        
        for(String name : syaryoTemplates.keySet()){
            syaryoTemplates.get(name).decompress();
            System.out.println(name);
            System.out.println(syaryoTemplates.get(name).map);
            
            System.exit(0);
        }
    }
}
