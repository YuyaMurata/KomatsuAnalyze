/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package check;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import json.JsonToSyaryoObj;
import json.JsonToSyaryoTemplate;
import obj.SyaryoObject;
import creator.template.SyaryoTemplate;

/**
 *
 * @author ZZ17390
 */
public class SyaryoTemplateCheck {
    private static final String path = "C:\\Users\\zz17390\\Documents\\NetBeansProjects\\KomatsuData\\車両テンプレート";
    private static Map<String, SyaryoTemplate> syaryoMap;
    
    public static void main(String[] args) {
        JsonToSyaryoTemplate temp = new JsonToSyaryoTemplate();
        syaryoMap = temp.reader(path+"\\syaryo_history_template_service1123.json");
        
        count();
        
        //Check 1:
        //randomSampling(10, syaryoMap);
    }
    
    public static void count(){
        System.out.println("車両数:"+syaryoMap.size());
        int n=0;
        for(SyaryoTemplate temp : syaryoMap.values()){
            if(temp.get("受注") == null)
                System.out.println(temp);
            else
                n += temp.get("受注").split("\n").length;
        }
        System.out.println("受注数:"+n);
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
