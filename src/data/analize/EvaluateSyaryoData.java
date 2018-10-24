/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analize;

import analizer.SyaryoAnalizer;
import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import json.SyaryoToZip3;
import obj.SyaryoObject4;
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
    private Integer day, smr;

    public EvaluateSyaryoData(SyaryoObject4 syaryo) {
        try (SyaryoAnalizer analize = new SyaryoAnalizer(syaryo)) {
            this.name = analize.get().name;
            this.company = analize.mcompany;
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
    }

    /**
     * 使われ方のデータ 定義 : 累積負荷 / SMR
     *
     */
    private Map useData(SyaryoObject4 syaryo) {
        return null;
    }

    /**
     * 経年/SMRのデータ 定義 : v[経年, ACT_SMR]
     */
    private Map agingSMRData(SyaryoObject4 syaryo) {
        Map agesmr = new TreeMap();
        return null;
    }

    /**
     * メンテナンスデータ 定義 : v[エンジンメンテ, 油圧メンテ, 定期メンテ] エンジンメンテ = エンジンOIL交換回数 / (SMR /
     * インターバル) 油圧メンテ = 作動機オイル交換 / (SMR / インターバル) 定期メンテ = 作業形態回数 / (経年 / インターバル)
     */
    private Map mainteData(SyaryoAnalizer syaryo) {
        Map<String, Integer> map = new LinkedHashMap<>();

        //初期化
        for (Object sg : mainte.get("受注.SGYO_KTICD")) {
            map.put(sg.toString(), 0);
        }
        for (Object p : mainte.get("部品.HNBN")) {
            map.put(p.toString(), 0);
        }
        map.put("エンジンオイル", 0);
        //map.put("パワーラインオイル", 0);

        //定期点検
        if (syaryo.get().get("受注") != null) {
            for (List<String> service : syaryo.get().get("受注").values()) {
                //作業形態カウント
                String sg = service.get(layout.get("受注").indexOf("SGYO_KTICD"));
                if (mainte.get("受注.SGYO_KTICD").contains(sg)) {
                    map.put(sg, map.get(sg) + 1);
                }
            }
        }

        //定期交換品
        if (syaryo.get().get("部品") != null) {
            List<String> sbns = new ArrayList<>(syaryo.get().get("受注").keySet());
            for (String sbn : sbns) {
                Map<String, Boolean> flgMap = map.keySet().stream().collect(Collectors.toMap(k -> k, k -> true));

                if (syaryo.getSBNParts(sbn) != null) {
                    for (List<String> parts : syaryo.getSBNParts(sbn).values()) {

                        //品番カウント
                        String p = parts.get(layout.get("部品").indexOf("HNBN"));
                        if (mainte.get("部品.HNBN").contains(p) && flgMap.get(p)) {
                            map.put(p, map.get(p) + 1);
                        }

                        //エンジンオイル
                        String pn = parts.get(layout.get("部品").indexOf("BHN_NM"));
                        if (((p.contains("SYEO-") && !p.contains("SYEO-T")) || (p.contains("NYEO-") && !p.contains("NYEO-T"))) && flgMap.get("エンジンオイル")) {
                            p = "エンジンオイル";
                            map.put(p, map.get(p) + 1);
                        }

                        //パワーラインオイル
                        /*if (pn.contains("パワーラインオイル") && flgMap.get("パワーラインオイル")) {
                            p = "パワーラインオイル";
                            map.put(p, map.get(p) + 1);
                        }*/
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

    public String getHeader() {
        StringBuilder sb = new StringBuilder("sid");
        sb.append(",");
        sb.append("会社");
        sb.append(",");
        sb.append("year");
        sb.append(",");
        sb.append("SMR");
        for (String k : evalMap.keySet()) {
            sb.append(",");
            sb.append(k);
        }

        return sb.toString();
    }

    public String print() {
        StringBuilder sb = new StringBuilder(name);
        sb.append(",");
        sb.append(company);
        sb.append(",");
        sb.append(day / 365);
        sb.append(",");
        sb.append(smr);
        for (String k : evalMap.keySet()) {
            sb.append(",");
            sb.append(evalMap.get(k));
        }

        return sb.toString();
    }

    private static String PATH = KomatsuDataParameter.SYARYOOBJECT_FDPATH;
    private static String KISY = "PC200";
    static String filename = PATH + "syaryo_obj_" + KISY + "_km_form.bz2";

    public static void main(String[] args) {
        Map<String, SyaryoObject4> map = new SyaryoToZip3().read(filename);
        SyaryoObject4 test = map.values().stream().findFirst().get();
        EvaluateSyaryoData ev = new EvaluateSyaryoData(test);
        try (PrintWriter pw = CSVFileReadWrite.writer(KISY + "_syaryo_eval_mainte.csv")) {
            pw.println(ev.getHeader());
            for (SyaryoObject4 syaryo : map.values()) {
                System.out.println(syaryo.name);
                EvaluateSyaryoData eval = new EvaluateSyaryoData(syaryo);
                pw.println(eval.print());
            }
        }
    }
}
