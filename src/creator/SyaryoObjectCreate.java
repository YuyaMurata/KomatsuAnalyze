/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator;

import java.util.Map;
import java.util.TreeMap;
import json.SyaryoObjToJson;
import creator.template.SyaryoTemplate;
import json.SyaryoToZip;
import obj.SyaryoObject2;

/**
 * 車両テンプレートから車両オブジェクトを作成
 *
 * @author ZZ17390
 */
public class SyaryoObjectCreate {

	public static void main(String[] args) {
		new SyaryoObjectCreate().create("WA470");
	}

	public void create(String kisy) {
		String midtemp = "..\\KomatsuData\\中間データ\\syaryo_mid_" + kisy;
		//String midtemp = "middle\\syaryo_mid_" + kisy + ".zip";
		String FILENAME = "json\\syaryo_obj_" + kisy + ".json";

		Map<String, SyaryoTemplate> syaryoTemplates = new SyaryoToZip().readTemplate(midtemp);
		TreeMap<String, SyaryoObject2> syaryoMap = new TreeMap();

		int n = 0;
        int en = 0;
		//車両のオブジェクト化
		for (String syaryoName : syaryoTemplates.keySet()) {
			//
			SyaryoTemplate syaryo = syaryoTemplates.get(syaryoName);
            syaryo.decompress();

			Map<String, String> template = syaryo.getAll();

			SyaryoObject2 syaryoObj = new SyaryoObject2(syaryo.getName());

			//Summary
			if (syaryoName.equals("_summary")) {
				syaryoMap.put(syaryoName, syaryoObj);
				continue;
			}

			n++;

			en += syaryoObj.add(template);

			//System.out.println(syaryoObj.dump());
			syaryoMap.put(syaryoObj.getName(), syaryoObj);

			if (n % 10000 == 0) {
				System.out.println(n + " 台");
			}
            
            syaryo.compress(false);
		}
        
        if (en > 0) 
			System.out.println("欠損データ=" + en);

		SyaryoObjToJson json = new SyaryoObjToJson();
		json.write(FILENAME, syaryoMap);

		System.out.println(syaryoMap.size());
	}
}
