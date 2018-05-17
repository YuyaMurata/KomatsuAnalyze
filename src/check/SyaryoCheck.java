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
import obj.SyaryoElements;
import obj.SyaryoObject1;
import creator.template.SyaryoTemplate;

/**
 *
 * @author ZZ17390
 */
public class SyaryoCheck {

    private static final String kisy = "WA470";
    private static Map<String, SyaryoObject1> syaryoMap;

    public static void main(String[] args) {
        JsonToSyaryoObj obj = new JsonToSyaryoObj();
        syaryoMap = obj.reader("json\\syaryo_obj_" + kisy + "_form.json");
        System.out.println(syaryoMap.size());

        //count();
        //smr("PC200-10-450972", syaryoMap);
        //smrCheck(syaryoMap);
        //komtraxCheck(syaryoMap);
        //customerCheck(syaryoMap);

        //nullCheck();
        //Check 1:
        //randomSampling(10, syaryoMap);
    }
    
    public static void count() {
        List<String> typs = syaryoMap.values().stream()
            .map(s -> s.getType())
            .distinct()
            .collect(Collectors.toList());

        for (String typ : typs) {
            long cnt = 0L;//syaryoMap.keySet().stream().filter(s -> s.split("-")[1].contains(typ)).filter(s -> syaryoMap.get(s).getKomtrax()).count();
            for (SyaryoObject1 syaryo : syaryoMap.values()) {
                if (!syaryo.getType().equals(typ)) {
                    continue;
                }
                for (List smr : syaryo.getSMR().values()) {
                    if (smr.get(1).toString().contains("komtrax")) {
                        cnt = cnt + 1;
                        if (typ.equals("8")) {
                            System.out.println(cnt + " - " + syaryo.getName());
                        }
                        break;
                    }
                }
            }

            System.out.println(typ + "=" + cnt);
        }
    }

    public static void smr(String name, Map<String, SyaryoObject1> map) {
        map.get(name).getSMR().entrySet().stream().forEach(e -> System.out.println(e.getKey() + ":" + e.getValue()));
    }

    public static void smrCheck(Map<String, SyaryoObject1> map) {
        for (String name : map.keySet()) {
            String before = "";
            String before0 = "";
            for (String date : map.get(name).getSMR().keySet()) {
                if (!before0.equals("")) {
                    Integer smr0 = Integer.valueOf(map.get(name).getSMR().get(before0).get(SyaryoElements.SMR._SMR.getNo()).toString());
                    Integer smr1 = Integer.valueOf(map.get(name).getSMR().get(before).get(SyaryoElements.SMR._SMR.getNo()).toString());
                    Integer smr2 = Integer.valueOf(map.get(name).getSMR().get(date).get(SyaryoElements.SMR._SMR.getNo()).toString());
                    if (smr1.compareTo(smr0) < 0) {
                        System.out.println(name + ":" + "before(" + before0 + "," + smr0 + ") before(" + before + "," + smr1 + ") after(" + date + "," + smr2 + ")");
                    }
                }
                before0 = before;
                before = date;
            }
        }
    }

    public static void komtraxCheck(Map<String, SyaryoObject1> map) {
        List<String> km0 = map.entrySet().stream()
            .filter(s -> !s.getValue().getKomtrax()).map(s -> s.getKey())
            .collect(Collectors.toList());
        List<String> km1 = map.entrySet().stream()
            .filter(s -> s.getValue().getKomtrax()).map(s -> s.getKey())
            .collect(Collectors.toList());
        System.out.println("Komtrax未装着:");
        System.out.println(km0);
        System.out.println("");
        System.out.println("Komtrax装着:");
        System.out.println(km1);
    }
    
    public static void customerCheck(Map<String, SyaryoObject1> map){
        for(String name : map.keySet()){
            Map<String, List> owner = map.get(name).getOwner();
            if(owner.isEmpty())
                System.out.println(name+": is Empty!");
            else{
                for(String date : owner.keySet()){
                    List list = owner.get(date);
                    if(list.get(SyaryoElements.Customer.ID.getNo()).toString().length() < 8)
                        System.out.println("id:"+list);
                    else if(list.get(SyaryoElements.Customer.Name.getNo()).toString().contains("コマツ")){
                        System.out.println("name1:"+list);
                    }else if(list.get(SyaryoElements.Customer.Name.getNo()).toString().contains("小松")){
                        System.out.println("name2:"+list);
                    }
                }
            }
        }
    }

    public static void nullCheck() {
        for (SyaryoObject1 syaryo : syaryoMap.values()) {
            if (syaryo.getNew() == null) {
                System.out.println(syaryo.getName());
            }
        }
    }

    public static void randomSampling(int size, Map<String, SyaryoTemplate> map) {
        Random rand = new Random();
        List<String> sampling = rand.ints(0, map.size())
            .distinct()
            .limit(size)
            .mapToObj(new ArrayList<String>(map.keySet())::get)
            .collect(Collectors.toList());

        //File[] flist = (new File(path)).listFiles();
        System.out.println("sampling:" + sampling);
    }
}
