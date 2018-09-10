/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package check;

import index.SyaryoObjectElementsIndex;
import java.util.List;
import java.util.Map;
import json.SyaryoToZip0;
import json.SyaryoToZip3;
import obj.SyaryoObject2;
import obj.SyaryoObject4;
import param.KomatsuDataParameter;

/**
 *
 * @author kaeru
 */
public class SyaryoDataCheck {
    //レイアウト
    private static Map<String, List> dataIndex = SyaryoObjectElementsIndex.getInstance().getIndex();
    private static String PATH = KomatsuDataParameter.SYARYOOBJECT_FDPATH;
    private static String KISY = "PC200";
    private static Map<String, SyaryoObject4> map;
    
	public static void main(String[] args) {
		map = new SyaryoToZip3().read(PATH + "syaryo_obj_" + KISY + "_sv_form.bz2");
		
        syaryoCheck("451462");
		//syaryoCheck(1000, "オールサポート", map);
        //syaryoCheck("24709", "SMR", map);
        //syaryoCheck("33407", "受注", map);
        //nullCheck("最終更新日", map);
        //komtraxCheck(map);
	}
	
    private static void syaryoCheck(String kiban){
        SyaryoObject4 syaryo = map.values().parallelStream()
                                    .filter(s -> s.getName().contains(kiban))
                                    .findFirst().get();
        
        for(String dkey : syaryo.getMap().keySet()){
            System.out.println(dkey+":"+syaryo.get(dkey).size());
        }
    }
    
	private static void syaryoCheck(int num, String field, Map<String, SyaryoObject2> syaryos){
		int n = 0;
		for(SyaryoObject2 syaryo : syaryos.values()){
			syaryo.decompress();
			if(syaryo.map.get(field) == null){
                syaryo.compress(false);
                continue;
            }
            
            System.out.println(syaryo.name);
			System.out.println(" "+syaryo.map.get(field));
			
            syaryo.compress(false);
            
			if(num < n++)
				break;
		}
        System.out.println(n);
	}
    
    private static void syaryoCheck(String kiban, String field, Map<String, SyaryoObject2> syaryos){
		SyaryoObject2 syaryo = syaryos.values().stream().filter(s -> s.getName().contains(kiban)).findFirst().get();
        syaryo.decompress();
        for(Object key : syaryo.get(field).keySet()){
            System.out.println(syaryo.get(field).get(key));
        }
        syaryo.compress(true);
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
