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
        String kisy = "WA470";
		String path = "..\\KomatsuData\\車両テンプレート\\"+kisy+"系\\";
		//String path = "template\\"+kisy+"\\";
		String outpath = "..\\KomatsuData\\中間データ\\";
		//String outpath = "middle\\";
		String FILENAME = outpath+"syaryo_mid_" + kisy;
		File[] flist = (new File(path)).listFiles();

		Map<String, SyaryoTemplate> syaryoBase = new SyaryoToZip().readTemplate(path + "syaryo_"+kisy+"_template.gz");
		TreeMap<String, SyaryoTemplate> syaryoMap = new TreeMap();

		System.out.println(syaryoBase.keySet().stream().map(s -> s.split("-")[0]).distinct().collect(Collectors.toList()));
		//System.exit(0);
		int totalRecord = 0;
		for (File f : flist) {
			if (!f.getName().contains(".gz")) {
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
				if (!template.getName().contains(kisy)) {
					continue;
				}
                
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
                template.compress();
                syaryo.compress();
				syaryoMap.put(syaryo.getName(), syaryo);
                
				/*if (n % 10000 == 0) {
					System.out.println("n = " + n);
				}*/
			}
            
            System.out.println(numSyaryo + "," + numRecord);
		}

		System.out.println("データ件数, " + totalRecord);
		System.out.println("車両数, " + syaryoMap.size());

		syaryoMap.put("_summary", new SyaryoTemplate("データ件数, " + totalRecord, ",車両数, " + syaryoMap.size(), "", ""));

		new SyaryoToZip().write(FILENAME, syaryoMap);
	}
}
