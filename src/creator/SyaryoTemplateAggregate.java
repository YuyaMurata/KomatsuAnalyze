/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;
import json.JsonToSyaryoTemplate;
import json.SyaryoTemplateToJson;
import creator.template.SyaryoTemplate;
import java.util.stream.Collectors;

/**
 * 作業明細を確認 WA470-7-10200など
 *
 * @author ZZ17390
 */
public class SyaryoTemplateAggregate {

	public static void main(String[] args) {
        String kisy = "PC200";
		String path = "..\\KomatsuData\\車両テンプレート\\"+kisy+"系\\";
		//String path = "template\\"+kisy+"\\";
		String outpath = "..\\KomatsuData\\中間データ\\";
		//String outpath = "middle\\";
		String FILENAME = outpath+"syaryo_mid_" + kisy + ".json";
		File[] flist = (new File(path)).listFiles();

		Map<String, SyaryoTemplate> syaryoBase = new JsonToSyaryoTemplate().reader(path + "syaryo_"+kisy+"_template.json");
		TreeMap<String, SyaryoTemplate> syaryoMap = new TreeMap();

		System.out.println(syaryoBase.keySet().stream().map(s -> s.split("-")[0]).distinct().collect(Collectors.toList()));
		//System.exit(0);
		int totalRecord = 0;
		for (File f : flist) {
			if (f.getName().contains("error.csv") || f.getName().equals("komtrax")) {
				continue;
			}
			System.out.print(f.getName()+",");

			Map<String, SyaryoTemplate> syaryoTemplates = new JsonToSyaryoTemplate().reader(f.getPath());
            if(syaryoTemplates == null)
                continue;

            int numRecord = 0;
            int numSyaryo = 0;
			for (SyaryoTemplate template : syaryoTemplates.values()) {
				if (!template.getName().contains(kisy)) {
					continue;
				}
                
                numSyaryo++;
				SyaryoTemplate syaryo = syaryoBase.get(template.getName());
				for (String key : template.getAll().keySet()) {
					int index = 0;
					Boolean header = true;
					for (String str : template.getAll().get(key).split("\n")) {
						//System.out.println(str);
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

		new SyaryoTemplateToJson().write(FILENAME, syaryoMap);
	}
}
