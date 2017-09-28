/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package check;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import json.JsonToSyaryoObj;
import json.JsonToSyaryoTemplate;
import obj.SyaryoObject;
import obj.SyaryoTemplate;
import org.apache.hadoop.metrics2.sink.FileSink;

/**
 *
 * @author ZZ17390
 */
public class SyaryoCheck {
    private static final String filename = "syaryo_history_template.json";
    private static final String path = "車両テンプレート";
    
    public static void main(String[] args) {
        JsonToSyaryoTemplate obj = new JsonToSyaryoTemplate();
        Map<String, SyaryoTemplate> syaryoMap = obj.reader(path+"\\"+filename);
        
        extractRule(syaryoMap);
        
        //Check 1:
        //randomSampling(10, syaryoMap);
    }
    
    public static void extractRule(Map<String, SyaryoTemplate> map){
        //check
        String fname = path+"\\"+"syaryo_history_template_service2.json";
        JsonToSyaryoTemplate obj = new JsonToSyaryoTemplate();
        Map<String, SyaryoTemplate> syaryoMap = obj.reader(fname);
        
        System.out.println(syaryoMap.values().stream().max(Comparator.comparing(v -> v.get("最終更新日").split("\\n").length)).get().getName());
    }
    
    public static void randomSampling(int size, Map<String, SyaryoTemplate> map){
        Random rand = new Random();
        List<String> sampling = rand.ints(0, map.size())
                                    .distinct()
                                    .limit(size)
                                    .mapToObj(new ArrayList<String>(map.keySet())::get)
                                    .collect(Collectors.toList());
        
        //File[] flist = (new File(path)).listFiles();
        System.out.println("sampling:"+sampling);
    }
}
