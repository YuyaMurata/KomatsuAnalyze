/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analize;

import java.util.List;
import obj.SyaryoLoader;
import obj.SyaryoObject;

/**
 *
 * @author kaeru
 */
public class MaintenanceTimeSeries {
	private static SyaryoLoader LOADER = SyaryoLoader.getInstance();
	public static void main(String[] args) {
		LOADER.setFile("PC200_loadmap");
		SyaryoObject syaryo = LOADER.getSyaryoMap().get("PC200-10-450635");
		
		toTimeSeiries(syaryo);
	}
	
	private static List<Integer> toTimeSeiries(SyaryoObject syaryo){
		
		
		return null;
	}
}
