/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.create;

import creator.template.SimpleTemplate;
import java.util.Map;
import java.util.TreeMap;
import java.io.File;
import java.util.List;
import json.SyaryoTemplateToJson;
import json.SyaryoToZip3;
import obj.SyaryoObject4;

/**
 * 車両テンプレートから車両オブジェクトを作成
 *
 * @author ZZ17390
 */
public class TamplateToObjectCreate {

    private static String[] kisyList = new String[]{"PC138US", "PC200", "PC200LC", "PC200SC", "PC78US", "WA470", "WA100", "PC210", "PC210LC"};
    private static String FILEPATH = "template\\";
    private static String OUTPATH = "middle\\";
    //private static String path = "middle\\"+kisy+"\\mid\\";
    //private static String outpath = "middle\\"+kisy+"\\obj\\";

    public static void main(String[] args) {
        for(String kisy : kisyList)
            create(kisy);
    }

    public static void create(String kisy) {
        String fpath = FILEPATH + kisy + "\\json\\";
        String opath= OUTPATH  + kisy + "\\obj\\";
        
        //Layout
        Map<String, List> index = TemplateCreate.index();
        
        if (!(new File(opath)).exists()) {
            (new File(opath)).mkdirs();
        }
        
        //File
        SyaryoTemplateToJson json = new SyaryoTemplateToJson();

        File[] flist = (new File(fpath)).listFiles();
        for (File f : flist) {
            String table = f.getName().substring(0, f.getName().lastIndexOf("_"));
            String FILENAME = opath + "syaryo_mid_" + kisy + "_" + table+".bz2";
            int fieldLen = index.get(table).size()-1;
            System.out.println(table+":"+fieldLen);
            
            //Folder
            File objf = new File(FILENAME);
            if (objf.exists()) {
                System.out.println("exists " + objf.getName());
                continue;
            }

            System.out.println(f.getName());

            Map<String, SimpleTemplate> templats = json.reader(f.getAbsolutePath());
            TreeMap<String, SyaryoObject4> syaryoMap = new TreeMap();

            //int n = 0;
            //int en = 0;
            //オブジェクト化
            templats.entrySet().stream()
                .map(s -> s.getValue()).forEach(s -> {

                SyaryoObject4 syaryoObj = new SyaryoObject4(s.getName());
                syaryoObj.add(s.temp, fieldLen);
                
                //System.out.println(syaryoObj.dump());
                syaryoObj.compress(true);

                syaryoMap.put(syaryoObj.getName(), syaryoObj);

                s = null;
            });

            SyaryoObject4 syaryo = syaryoMap.values().stream().findFirst().get();
            syaryo.decompress();
            System.out.println(syaryo.dump());
            syaryo.compress(true);
            new SyaryoToZip3().write(FILENAME, syaryoMap);

            System.out.println(syaryoMap.size());
            //System.gc();
        }
    }
}
