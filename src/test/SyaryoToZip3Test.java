/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.File;
import java.util.Map;
import json.SyaryoToZip3;
import obj.SyaryoObject3;

/**
 *
 * @author kaeru_yuya
 */
public class SyaryoToZip3Test {
    public static void main(String[] args) {
        Map<String, SyaryoObject3> map = new SyaryoToZip3().read("json\\syaryo_obj_PC138US.bz2");
        System.out.println(map);
        
        for(int i=0; i < 20; i++){
            System.out.println(i+"回目");
            map.values().parallelStream().forEach(s ->{
                s.decompress();
                s.compress(true);
            });
        }
    }
}
