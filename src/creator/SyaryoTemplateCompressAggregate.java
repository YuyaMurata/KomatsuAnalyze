/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;
import creator.template.SyaryoTemplate;
import java.util.stream.Collectors;
import json.SyaryoToZip;

/**
 *
 * @author ZZ17390
 */
public class SyaryoTemplateCompressAggregate {
    public static void main(String[] args) {
        SyaryoTemplateCompressAggregate.agrregate("WA470");
        //SyaryoTemplateCompressAggregate.agrregate("PC200");
        //SyaryoTemplateCompressAggregate.agrregate("PC200");
        //SyaryoTemplateCompressAggregate.agrregate("PC200");
    }
    
	public static void agrregate(String kisy) {
		String path = "..\\KomatsuData\\車両テンプレート\\"+kisy+"系\\";
		String outpath = "..\\KomatsuData\\中間データ\\";
        //String path = "template\\"+kisy+"\\";
		//String outpath = "middle\\";
		String FILENAME = outpath+"syaryo_mid_" + kisy;
		File[] flist = (new File(path)).listFiles();

		TreeMap<String, SyaryoTemplate> syaryoBase = new TreeMap(new SyaryoToZip().readTemplate(path + "syaryo_"+kisy+"_template.gz"));
		//TreeMap<String, SyaryoTemplate> syaryoMap = new TreeMap();

		System.out.println(syaryoBase.keySet().stream().map(s -> s.split("-")[0]).distinct().collect(Collectors.toList()));
		//System.exit(0);
		int totalRecord = 0;
		for (File f : flist) {
			if (!f.getName().contains(".gz") || f.getName().contains("_komtrax_actdata")
                 || f.getName().contains("_komtrax_caution") || f.getName().contains("_komtrax_engine")) {
				continue;
			}
			System.out.print(f.getName()+",");

			Map<String, SyaryoTemplate> syaryoTemplates = new SyaryoToZip().readTemplate(f.getPath());
            if(syaryoTemplates == null){
                System.out.println("SyaryoTemplate is NULL!");
                continue;
            }

            int numRecord = 0;
            int numSyaryo = 0;
			for (SyaryoTemplate template : syaryoTemplates.values()) {
                numSyaryo++;
				SyaryoTemplate syaryo = syaryoBase.get(template.getName());
                
                //車両テンプレート展開
                template.decompress();
                syaryo.decompress();
				
                for (String key : template.getAll().keySet()) {
					int index = 0;
					Boolean header = true;
					for (String str : template.getAll().get(key).split("\n")) {
						if (header) {
							index = str.split(",").length;
							header = false;
							continue;
						}

						if (str.replace(" ", "").split(",").length < index) {
							str += "?";
						}
						syaryo.add(key, str.trim().split(","));
                        
                        numRecord++;
                        totalRecord++;
					}
				}
                
                //車両テンプレート圧縮
                template = null;
                syaryo.compress(true);
				//syaryoMap.put(syaryo.getName(), syaryo);
                
				/*if (numSyaryo % 1000 == 0) {
					System.out.println("n = " + numSyaryo);
				}*/
			}
            
            System.out.println(numSyaryo + "," + numRecord);
		}

		System.out.println("データ件数, " + totalRecord);
		System.out.println("車両数, " + syaryoBase.size());

		syaryoBase.put("_summary", new SyaryoTemplate("データ件数, " + totalRecord, ",車両数, " + syaryoBase.size(), "", ""));

		new SyaryoToZip().write(FILENAME, syaryoBase);
	}
}
