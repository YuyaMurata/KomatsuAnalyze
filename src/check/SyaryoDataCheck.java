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
		String kisy = "PC138US";
		Map<String, SyaryoObject2> map = new SyaryoToZip().readObject("json\\syaryo_obj_"+kisy);
		
		syaryoCheck(100, map);
        //syaryoCheck("29888", "KMSMR", map);
        //nullCheck("最終更新日", map);
        //komtraxCheck(map);
	}
	
	private static void syaryoCheck(int num, Map<String, SyaryoObject2> syaryos){
		int n = 0;
		for(SyaryoObject2 syaryo : syaryos.values()){
			syaryo.decompress();
			
			System.out.println(syaryo.dump());
			
            syaryo.compress(true);
            
			if(num < n++)
				break;
		}
	}
    
    private static void syaryoCheck(String kiban, String field, Map<String, SyaryoObject2> syaryos){
		SyaryoObject2 syaryo = syaryos.values().stream().filter(s -> s.getName().contains(kiban)).findFirst().get();
        syaryo.decompress();
        System.out.println(syaryo.dump());
	}
    
    private static void nullCheck(String field, Map<String, SyaryoObject2> syaryos){
        for(SyaryoObject2 syaryo : syaryos.values()){
			syaryo.decompress();
			if(syaryo.get(field) == null)
                System.out.println(syaryo.getName());
            
            syaryo.compress(true);
		}
    }
    
    private static void komtraxCheck(Map<String, SyaryoObject2> syaryos){
		int n= 0;
        int km = 0;
		for(SyaryoObject2 syaryo : syaryos.values()){
			syaryo.decompress();
			
            if(syaryo.getKomtrax())
                n++;
            if(syaryo.getSMR() != null)
                km++;
            
            syaryo.compress(true);
		}
        
        System.out.println("kmflg="+n+", km="+km+" / " + syaryos.size());
	}
}
