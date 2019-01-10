/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analize;

import analizer.SyaryoAnalizer;
import data.cluster.KMeansPP;
import data.eval.MainteEvaluate;
import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import obj.LoadSyaryoObject;
import obj.SyaryoObject;
import param.KomatsuDataParameter;
import rmi.SyaryoObjectClient;

/**
 *
 * @author ZZ17390
 */
public class EvaluateSyaryoData {
    private static LoadSyaryoObject LOADER;
    private static SyaryoObjectClient CLIENT = SyaryoObjectClient.getInstance();
    private Map<String, List> mainte = KomatsuDataParameter.PERIOD_MAINTE;
    private Map<String, Integer> evalMainte = new LinkedHashMap<>();
    private Map<String, Integer> evalAgeSMR = new LinkedHashMap<>();
    private Map<String, Integer> evalUsage = new LinkedHashMap<>();
    public String name, company;
    public Integer day, smr, rent;

    public EvaluateSyaryoData(SyaryoObject syaryo) {
        try (SyaryoAnalizer analize = new SyaryoAnalizer(syaryo)) {
            this.name = analize.get().name;
            this.company = analize.mcompany;
            this.rent = analize.rent?1:0;
            this.day = analize.currentAge_day;
            this.smr = analize.maxSMR[1];

            evalAgeSMR.putAll(useData(syaryo));
            evalUsage.putAll(agingSMRData(syaryo));
            evalMainte.putAll(mainteData(analize));
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(syaryo.dump());
            System.exit(0);
        }
    }

    /**
     * 使われ方のデータ 定義 : 累積負荷 / SMR
     *
     */
    private Map useData(SyaryoObject syaryo) {
        return null;
    }

    /**
     * 経年/SMRのデータ 定義 : v[経年, ACT_SMR]
     */
    private Map agingSMRData(SyaryoObject syaryo) {
        Map agesmr = new TreeMap();
        return null;
    }

    /**
     * メンテナンスデータ 定義 : v[エンジンメンテ, 油圧メンテ, 定期メンテ] エンジンメンテ = エンジンOIL交換回数 / (SMR /
     * インターバル) 油圧メンテ = 作動機オイル交換 / (SMR / インターバル) 定期メンテ = 作業形態回数 / (経年 / インターバル)
     */
    private Map mainteIndex = KomatsuDataParameter.PC_PID_DEFNAME;
    private Map mainteData(SyaryoAnalizer syaryo) {
        Map<Object, Integer> map = new LinkedHashMap<>();

        //初期化
        for (Object p : mainteIndex.keySet()) {
            map.put(mainteIndex.get(p), 0);
        }

        //定期点検
        if (syaryo.get().get("受注") != null) {
            for (List<String> service : syaryo.get().get("受注").values()) {
                //作業形態カウント
                String sg = service.get(LOADER.index("受注", "SGYO_KTICD"));
                Object key = mainteIndex.get(sg);
                if (key != null) {
                    map.put(key, map.get(key) + 1);
                }
            }
        }

        //定期交換品
        if (syaryo.get().get("部品") != null) {
            List<String> sbns = new ArrayList<>(syaryo.get().get("受注").keySet());
            for (String sbn : sbns) {
                Map<Object, Boolean> flgMap = map.keySet().stream().collect(Collectors.toMap(k -> k, k -> true));

                if (syaryo.getSBNParts(sbn) != null) {
                    for (List<String> parts : syaryo.getSBNParts(sbn).values()) {

                        //品番カウント
                        String p = parts.get(LOADER.index("部品", "HNBN"));
                        Object key = mainteIndex.get(p);
                        if (map.get(key) != null && flgMap.get(key)) {
                            map.put(key, map.get(key) + 1);
                        }

                        //エンジンオイル
                        String pn = parts.get(LOADER.index("部品", "BHN_NM"));
                        if (((p.contains("SYEO-") && !p.contains("SYEO-T")) || (p.contains("NYEO-") && !p.contains("NYEO-T"))) && flgMap.get("エンジンオイル")) {
                            p = "エンジンオイル";
                            map.put(p, map.get(p) + 1);
                        }

                        //パワーラインオイル
                        if ((p.contains("SYEO-T") || p.contains("NYEO-T")) && flgMap.get("パワーラインオイル")) {
                            p = "パワーラインオイル";
                            map.put(p, map.get(p) + 1);
                        }
                        //Check
                        if (flgMap.get(p) != null) {
                            flgMap.put(p, false);
                        }

                    }
                }
            }
        }

        return map;
    }

    public List<String> getMainteHeader() {
        List<String> sb = new ArrayList();
        sb.add("sid");
        sb.add("会社");
        sb.add("レンタル");
        sb.add("経年");
        sb.add("SMR");
        for (String k : evalMainte.keySet()) {
            sb.add(k);
            //sb.add(",回数");
        }

        return sb;
    }

    public Map<String, List> getMainteResults() {
        List info = new ArrayList();
        info.add(name);
        info.add(company);
        info.add(String.valueOf(rent));
        info.add(String.valueOf(day / 365d));
        info.add(String.valueOf(smr));

        List<Double> data = new ArrayList();
        for (String k : evalMainte.keySet()) {
            data.add(MainteEvaluate.eval(k, evalMainte.get(k), smr, day / 365d));
            //sb.add(evalMap.get(k));
        }

        Map map = new HashMap();
        map.put("info", info);
        map.put("data", data);
        return map;
    }
    
    private static String KISY = "PC200";

    public static void main(String[] args) {
        CLIENT.setLoadFile(KISY+"_km_form");
        LOADER = CLIENT.getLoader();
        
        Map<String, SyaryoObject> map = LOADER.getSyaryoMap();
        
        //メンテナンス評価
        Map<String, EvaluateSyaryoData> data = new HashMap();
        for (SyaryoObject syaryo : map.values()) {
            System.out.println(syaryo.name);
            data.put(syaryo.name, new EvaluateSyaryoData(syaryo));
        }
        
        //クラスタリング
        KMeansPP km = new KMeansPP();
        km.setEvalSyaryo(3, data);
        Map<String, Integer> kmresult = km.execute();
        
        //出力
        /*try (PrintWriter pw = CSVFileReadWrite.writer(KISY + "_syaryo_eval_mainte.csv")) {
            pw.println(String.join(",", ev.getHeader())+",AVG,CID");
            for(String key : info.keySet()){
                pw.print(info.get(key).stream().map(s -> s.toString()).collect(Collectors.joining(","))+",");
                if(data.get(key) != null){
                    double avg = data.get(key).stream().mapToDouble(d -> (Double)d).average().getAsDouble();
                    pw.println(data.get(key).stream().map(s -> s.toString()).collect(Collectors.joining(","))+","+avg+","+kmresult.get(key));
                }else
                    pw.println(zerodata.get(key).stream().map(s -> s.toString()).collect(Collectors.joining(","))+",0,0");
            }
        }*/
    }
}
