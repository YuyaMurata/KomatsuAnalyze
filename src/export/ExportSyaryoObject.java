/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

import file.SyaryoToCompress;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import obj.SyaryoLoader;

/**
 *
 * @author kaeru
 */
public class ExportSyaryoObject {
    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();
    public static void main(String[] args) {
        LOADER.setFile("PC200_form");
        Map map = existsData("LOADMAP_SMR");
        LOADER.close();
        new SyaryoToCompress().write("syaryo_obj_PC200_loadmap.bz2", map);
    }
    
    private static Map existsData(String key){
        return LOADER.getSyaryoMapWithHeader().values().stream()
                    .filter(s -> s.get(key) != null)
                    .collect(Collectors.toMap(s -> s.name, s -> s, (e1, e2) -> e1, TreeMap::new));
    }
}
