/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.create;

import creator.*;
import java.util.Map;
import java.util.TreeMap;
import creator.template.SyaryoTemplate;
import java.io.File;
import json.SyaryoToZip;
import json.SyaryoToZip2;
import json.SyaryoToZip3;
import obj.SyaryoObject2;
import obj.SyaryoObject3;

/**
 * 車両テンプレートから車両オブジェクトを作成
 *
 * @author ZZ17390
 */
public class TamplateToObjectCreate {
    private static String kisy = "PC200";
    private static String path = "..\\KomatsuData\\中間データ\\"+kisy+"\\mid\\";
    private static String outpath = "..\\KomatsuData\\中間データ\\"+kisy+"\\obj\\";
    //private static String path = "middle\\"+kisy+"\\mid\\";
    //private static String outpath = "middle\\"+kisy+"\\obj\\";
    
	public static void main(String[] args) {
        File[] flist = (new File(path)).listFiles();
        
        Boolean st = true;
        for(File f : flist){
            if(st){
                if(f.getName().contains("syaryo_mid_PC200_SMR"))
                    st = false;
                continue;
            }
            create(kisy, f);
        }
	}

	public static void create(String kisy, File file) {
        System.out.println(file.getName());
        
		String FILENAME = outpath+"syaryo_obj_" + kisy + "_" + file.getName().split("_")[3];
        
        //Folder
        File f = new File("json\\"+kisy);
        if (!f.exists()) {
            //フォルダ作成実行
            f.mkdirs();
        } else {
        }
        
		Map<String, SyaryoTemplate> templates = new SyaryoToZip2().readTemplate(file);
		TreeMap<String, SyaryoObject3> syaryoMap = new TreeMap();

		//int n = 0;
        //int en = 0;
		//オブジェクト化
		templates.entrySet().parallelStream().filter(s -> !s.getKey().equals("_summary"))
                                                        .map(s -> s.getValue()).forEach(s ->{
            s.decompress();
            
			//Map<String, String> template = syaryo.getAll();
            SyaryoObject3 syaryoObj = new SyaryoObject3(s.getName());
            int en = syaryoObj.add(s.getAll());
            
            syaryoObj.compress(true);
            s = null;
            
			syaryoMap.put(syaryoObj.getName(), syaryoObj);

			//if (n % 10000 == 0) {
			//	System.out.println(n + " 台");
			//}
            if (en > 0) 
                System.out.println("欠損データ=" + s.getName());
		});
        
        SyaryoObject3 syaryo = syaryoMap.values().stream().findFirst().get();
        syaryo.decompress();
        System.out.println(syaryo.dump());
        syaryo.compress(true);
		new SyaryoToZip3().write(FILENAME, syaryoMap);
        
		System.out.println(syaryoMap.size());
        //System.gc();
	}
}
