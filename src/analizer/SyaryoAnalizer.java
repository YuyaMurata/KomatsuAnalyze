/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analizer;

import index.SyaryoObjectElementsIndex;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import json.SyaryoToZip3;
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
        if(syaryo.get("KOMTRAX_SMR") != null || syaryo.get("仕様").get("1").get(0).equals("1"))
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
        
        //Number
        numOwners = ((Long)getValue("顧客", "KKYKCD", false).stream().distinct().count()).intValue();
        numOrders = syaryo.get("受注").size();
        numParts = syaryo.get("部品").size();
        numWorks = syaryo.get("作業").size();
        System.out.println(getValue(getSMR(syaryo)[0], getSMR(syaryo)[1], true));
        maxSMR = Integer.valueOf(getValue(getSMR(syaryo)[0], getSMR(syaryo)[1], true).get(getValue(getSMR(syaryo)[0], getSMR(syaryo)[1], true).size()-1));
        
        //Life
        lifestart = syaryo.get("新車").keySet().stream().findFirst().get();
        currentLife = getValue("受注", "ODDAY", true).get(numOrders-1);
        if(!dead)
            currentAge_day = age(lifestart, "20170501"); //データ受領日(データによって数日ずれている)
        else
            currentAge_day = age(lifestart, lifestop); //廃車日
        
        syaryo.compress(true);
    }
    
    public String[] getSMR(SyaryoObject4 syaryo){
        if(syaryo.get("KOMTRAX_SMR") != null)
            return new String[]{"KOMTRAX_SMR", "SMR_VALUE"};
        else if(syaryo.get("SMR") != null)
            return new String[]{"SMR", "SVC_MTR"};
        else
            return new String[]{"NULL", "NULL"};
    }
    
    public List<String> getValue(String key, String index, Boolean sorted){
        int idx = SyaryoObjectElementsIndex.getInstance().getIndex(key).indexOf(index);
        
        //例外処理
        if(idx == -1)
            return Arrays.asList(new String[]{"NaN", "NaN"});
        
        List list = syaryo.get(key).values().stream().map(l -> l.get(idx)).collect(Collectors.toList());
        
        if(sorted)
            list = (List) list.stream().map(v -> Integer.valueOf(v.toString())).sorted().map(v -> v.toString()).collect(Collectors.toList());
        
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
    
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("syaryo:"+syaryo.getName()+" Analize:\n");
        sb.append(" kind = "+kind+"\n");
        sb.append(" type = "+type+"\n");
        sb.append(" no = "+no+"\n");
        sb.append(" used = "+used+"\n");
        sb.append(" komtrax = "+komtrax+"\n");
        sb.append(" allsupport = "+allsupport+"\n");
        sb.append(" dead = "+dead+"\n");
        sb.append(" lifestart = "+lifestart+"\n");
        sb.append(" lifestop = "+lifestop+"\n");
        sb.append(" currentLife = "+currentLife+"\n");
        sb.append(" currentAge_day = "+currentAge_day+"\n");
        sb.append(" usedlife = "+(usedlife!=null?Arrays.asList():"[]")+"\n");
        sb.append(" numOwners = "+numOwners+"\n");
        sb.append(" numOrders = "+numOrders+"\n");
        sb.append(" numParts = "+numParts+"\n");
        sb.append(" numWorks = "+numWorks+"\n");
        sb.append(" maxSMR = "+maxSMR+"\n");
        return sb.toString();
    }
    
    public static void main(String[] args) {
        Map<String, SyaryoObject4> syaryoMap = new SyaryoToZip3().read("syaryo\\syaryo_obj_PC138US_form.bz2");
        SyaryoAnalizer analize = new SyaryoAnalizer(syaryoMap.get("PC138US-10-40651"));
        System.out.println(analize.toString());
    }
}
