/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csv;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import json.JsonToSyaryoObj;
import obj.SyaryoObject;

/**
 *
 * @author kaeru
 */
public class SyaryoToCSV {
	public static void main(String[] args) {
		String path = "分析結果\\";
		String kisy = "WA470";
		Map<String, SyaryoObject> syaryoMap = new JsonToSyaryoObj().reader("syaryo_obj_" + kisy + "_form.json");
		
		String name = "WA470-7-10180";
		SyaryoObject syaryo = syaryoMap.get(name);
		
		komtrax(syaryo);
	}
	
	public static void komtrax(SyaryoObject syaryo){
		try {
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(syaryo.getName() + "_komtrax_data.csv"))));
			//SMR
			for(String date : syaryo.getSMR().keySet())
				pw.println(date+","+"smr"+syaryo.getSMR().get(date).get(0));
			
			//SMR
			for(String date : syaryo.getGPS().keySet())
				pw.println(date+","+"smr"+syaryo.getSMR().get(date).get(0));
			
		} catch (IOException ex) {
		}
	}
}
