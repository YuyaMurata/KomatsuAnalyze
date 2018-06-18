/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analizer;

import index.SyaryoObjectElementsIndex;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import obj.SyaryoObject4;

/**
 *
 * @author ZZ17390
 */
public class SyaryoAnalizer {
    public SyaryoObject4 syaryo;
    public String kind = "";
    public String type = "";
    public String no = "";
    public Boolean used = false;
    public Boolean komtrax = false;
    public Boolean allsupport = false;
    public Boolean dead = false;
    public String lifestart = "";
    public String lifestop = "";
    public String currentLife = "";
    public Integer currentAge_day = -1;
    public String[] usedlife = null;
    public Integer numOwners = -1;
    public Integer numOrders = -1;
    public Integer numParts = -1;
    public Integer numWorks = -1;
    public Integer maxSMR = -1;
    
    public SyaryoAnalizer(SyaryoObject4 syaryo){
        this.syaryo = syaryo;
        settings(syaryo);
    }
    
    private void settings(SyaryoObject4 syaryo){
        syaryo.decompress();
        
        //Name
        this.kind = syaryo.getName().split("-")[0];
        this.type = syaryo.getName().split("-")[1];
        this.no = syaryo.getName().split("-")[2];
        
        //Status
        if(syaryo.get("KOMTRAX_SMR") != null)
            komtrax = true;
        if(syaryo.get("中古車") != null){
            used = true;
            StringBuilder sb = new StringBuilder();
            for(String date : syaryo.get("中古車").keySet()){
                sb.append(date);
                sb.append(",");
            }
            sb.deleteCharAt(sb.length()-1);
            usedlife = sb.toString().split(",");
        }
        if(syaryo.get("オールサポート") != null)
            allsupport = true;
        if(syaryo.get("廃車") != null){
            dead = true;
            lifestop = syaryo.get("廃車").keySet().stream().findFirst().get();
        }
        
        //Life
        lifestart = syaryo.get("新車").keySet().stream().findFirst().get();
        currentLife = getValue("受注", "ODDAY", true).get(numOrders-1);
        currentAge_day = 0;
        
        syaryo.compress(true);
    }
    
    public List<String> getValue(String key, String index, Boolean sorted){
        int idx = SyaryoObjectElementsIndex.getInstance().getIndex(key).indexOf(index);
        List list = syaryo.get(key).values().stream().map(l -> l.get(idx)).collect(Collectors.toList());
        
        if(sorted)
            list.sort(Comparator.naturalOrder());
        
        return list;
    }
    
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    public Integer age(String start, String stop){
        try {
            Date st = sdf.parse(start);
            Date sp = sdf.parse(stop);
            Long age = (sp.getTime() - st.getTime()) / (1000 * 60 * 60 * 24);
            return age.intValue();
        } catch (ParseException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
