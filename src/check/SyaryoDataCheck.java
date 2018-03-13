/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package check;

import java.util.Map;
import json.SyaryoToZip;
import obj.SyaryoObject2;

/**
 *
 * @author kaeru
 */
public class SyaryoDataCheck {
	public static void main(String[] args) {
		String kisy = "WA470";
		Map<String, SyaryoObject2> map = new SyaryoToZip().readObject("json\\syaryo_obj_"+kisy+"_form");
		
		syaryoCheck(1, "部品", map);
	}
	
	private static void syaryoCheck(int num, String field, Map<String, SyaryoObject2> syaryos){
		int n= 0;
		for(SyaryoObject2 syaryo : syaryos.values()){
			syaryo.decompress();
			
			System.out.println(syaryo.getName());
			System.out.println("\t"+syaryo.get(field));
			
			if(num >= n++)
				break;
		}
	}
}
