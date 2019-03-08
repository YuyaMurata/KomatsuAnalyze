/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.form;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import obj.SyaryoLoader;

/**
 *
 * @author kaeru
 */
public class PartsDataFormalize {
	private static SyaryoLoader LOADER = SyaryoLoader.getInstance();
	public static void main(String[] args) {
		LOADER.setFile("PC200_form_loadmap");
		
	}
	
	private static Map<String, List<String>> ToDefParts(String name, Map<String, List<String>> parts, int pid, int pname){
		Map<String, List<String>> defParts = new LinkedHashMap<>();
		
		parts.entrySet().stream().forEach(p->{
			
		});
		
		return null;
	}
	
	private static String toDefPID(String pid){
		
	}
}
