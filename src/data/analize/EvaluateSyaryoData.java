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
import file.SyaryoToCompress;
import obj.SyaryoObject;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class EvaluateSyaryoData {

    private Map<String, List> mainte = KomatsuDataParameter.PERIOD_MAINTE;
    private Map<String, List> layout = KomatsuDataParameter.DATALAYOUT_INDEX;
    private Map<String, Integer> evalMap = new LinkedHashMap<>();
    private String name, company;
    private Integer day, smr, rent;

    public Map<String, List> results = new HashMap<>();

    public EvaluateSyaryoData(SyaryoObject syaryo) {
        try (SyaryoAnalizer analize = new SyaryoAnalizer(syaryo)) {
            this.name = analize.get().name;
            this.company = analize.mcompany;
            this.rent = analize.rent?1:0;
            this.day = analize.currentAge_day;
            this.smr = analize.maxSMR[1];

            //evalMap.putAll(useData(syaryo));
            //evalMap.putAll(agingSMRData(syaryo));
            evalMap.putAll(mainteData(analize));
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(syaryo.dump());
            System.exit(0);
        }

        results = getResults();
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
                String sg = service.get(layout.get("受注").indexOf("SGYO_KTICD"));
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
                        String p = parts.get(layout.get("部品").indexOf("HNBN"));
                        Object key = mainteIndex.get(p);
                        if (map.get(key) != null && flgMap.get(key)) {
                            map.put(key, map.get(key) + 1);
                        }

                        //エンジンオイル
                        String pn = parts.get(layout.get("部品").indexOf("BHN_NM"));
                        if (((p.contains("SYEO-") && !p.contains("SYEO-T")) || (p.contains("NYEO-") && !p.contains("NYEO-T"))) && flgMap.get("エンジンオイル")) {
                            /*if(syaryo.no.equals("452771")){
                                System.out.println(p+":"+pn);
                            }*/
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

    public List<String> getHeader() {
        List<String> sb = new ArrayList();
        sb.add("sid");
        sb.add("会社");
        sb.add("レンタル");
        sb.add("経年");
        sb.add("SMR");
        for (String k : evalMap.keySet()) {
            sb.add(k);
            //sb.add(",回数");
        }

        return sb;
    }

    private Map<String, List> getResults() {
        List info = new ArrayList();
        info.add(name);
        info.add(company);
        info.add(String.valueOf(rent));
        info.add(String.valueOf(day / 365d));
        info.add(String.valueOf(smr));

        List data = new ArrayList();
        for (String k : evalMap.keySet()) {
            data.add(MainteEvaluate.eval(k, evalMap.get(k), smr, day / 365d));
            //sb.add(evalMap.get(k));
        }

        Map map = new HashMap();
        map.put("info", info);
        map.put("data", data);
        return map;
    }

    private static String PATH = KomatsuDataParameter.SYARYOOBJECT_FDPATH;
    private static String KISY = "PC200";
    static String filename = PATH + "syaryo_obj_" + KISY + "_km_form.bz2";

    public static void main(String[] args) {
        Map<String, SyaryoObject> map = new SyaryoToCompress().read(filename);
        SyaryoObject test = map.values().stream().findFirst().get();
        EvaluateSyaryoData ev = new EvaluateSyaryoData(test);

        //メンテナンス評価
        Map<String, List> info = new TreeMap();
        Map<String, List> data = new HashMap();
        Map<String, List> zerodata = new HashMap();
        for (SyaryoObject syaryo : map.values()) {
            System.out.println(syaryo.name);
            EvaluateSyaryoData eval = new EvaluateSyaryoData(syaryo);

            //レンタル車両除く
            if(eval.results.get("info").get(2).equals("1"))
                continue;
            
            info.put(syaryo.name, eval.results.get("info"));
            
            
            //評価値が全て0のものは分ける
            if(eval.results.get("data").stream()
                                    .mapToDouble(d -> Double.valueOf(d.toString()))
                                    .filter(d -> d > 0)
                                    .findFirst().isPresent())
                data.put(syaryo.name, eval.results.get("data"));
            else
                zerodata.put(syaryo.name, eval.results.get("data"));
        }
        
        //クラスタリング
        KMeansPP km = new KMeansPP();
        km.set(3, data);
        Map<String, Integer> kmresult = km.execute();
        
        //出力
        try (PrintWriter pw = CSVFileReadWrite.writer(KISY + "_syaryo_eval_mainte.csv")) {
            pw.println(String.join(",", ev.getHeader())+",AVG,CID");
            for(String key : info.keySet()){
                pw.print(info.get(key).stream().map(s -> s.toString()).collect(Collectors.joining(","))+",");
                if(data.get(key) != null){
                    double avg = data.get(key).stream().mapToDouble(d -> (Double)d).average().getAsDouble();
                    pw.println(data.get(key).stream().map(s -> s.toString()).collect(Collectors.joining(","))+","+avg+","+kmresult.get(key));
                }else
                    pw.println(zerodata.get(key).stream().map(s -> s.toString()).collect(Collectors.joining(","))+",0,0");
            }
        }
    }
}
