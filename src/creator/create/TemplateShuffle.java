/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.create;

import creator.template.SyaryoTemplate;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import json.SyaryoToZip;
import json.SyaryoToZip2;

/**
 *
 * @author ZZ17390
 */
public class TemplateShuffle {
    public static void main(String[] args) {
        shuffle("PC200");
    }
    
    public static void shuffle(String kisy){
        String path = "..\\KomatsuData\\車両テンプレート\\"+kisy+"系\\gz\\";
		String outpath = "..\\KomatsuData\\中間データ\\"+kisy+"\\";
        
        String FILENAME = outpath+"syaryo_mid_" + kisy+"_";
		File[] flist = (new File(path)).listFiles();
        
        //フォルダ作成実行
        File fd = new File(outpath);
        if(!fd.exists()){
            fd.mkdir();
        }
        
        //ベースファイル
        File basefile = new File(path + "syaryo_"+kisy+"_template.gz");
        Boolean stflg = false;
        
        for (File f : flist) {
            System.out.println(f.getName());
            if(!stflg)
                continue;
            if(f.getName().contains("sell_old"))
                stflg = true;
            
            if(f.getName().equals(basefile.getName()))
                continue;
            
            Map<String, SyaryoTemplate> syaryoTemplates = new SyaryoToZip2().readTemplate(f);
            if(syaryoTemplates == null){
                System.out.println("SyaryoTemplate is NULL!");
                continue;
            }
            if(syaryoTemplates.isEmpty()){
                System.out.println("SyaryoTemplate is Empty!");
                continue;
            }
            
            //マップキーの最大数を取得
            String id = null;
            int max = 0;
            for(SyaryoTemplate template : syaryoTemplates.values()){
                template.decompress();
                if(max < template.map.size()){
                    max = template.map.size();
                    id = template.getName();
                }
                template.compress(false);
            }
            
            //出力用のMapを作成
            SyaryoTemplate field = syaryoTemplates.get(id);
            field.decompress();
            Map<Object, Map<String, SyaryoTemplate>> fieldMap = new HashMap();
            for(Object key : field.map.keySet()){
                File midfile = new File(FILENAME+key+".gz");
                if(midfile.exists()){
                    fieldMap.put(key, new SyaryoToZip2().readTemplate(midfile));
                }else
                    fieldMap.put(key, new SyaryoToZip2().readTemplate(basefile));
                
                for(SyaryoTemplate template : syaryoTemplates.values()){
                    int index = 0;
                    Boolean header = true;
                    template.decompress();
                    SyaryoTemplate fieldSyaryo = fieldMap.get(key).get(template.getName());
                    fieldSyaryo.decompress();
                    String errstr = null;
                    try{
                        
                    for (String str : template.get(key.toString()).split("\n")) {
                        if (header) {
							index = str.split(",").length;
							header = false;
							continue;
						}
                        errstr = str;
                        if (str.replace(" ", "").split(",").length < index) {
							str += "?";
						}
                        fieldSyaryo.add(key.toString(), str.trim().split(","));
                    }
                    }catch(Exception e){
                        System.out.println(key);
                        System.out.println(template.getName());
                        System.out.println(errstr);
                    }
                    
                    fieldSyaryo.compress(true);
                    template.compress(false);
                }
            }
            
            //Mapを出力
            for(Object key : fieldMap.keySet()){
                new SyaryoToZip().write(FILENAME+key, fieldMap.get(key));
            }
        }
    }
}
