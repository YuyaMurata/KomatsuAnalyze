/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analizer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import json.SyaryoToZip3;
import obj.SyaryoObject4;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class SyaryoAnalizer implements AutoCloseable {

    public SyaryoObject4 syaryo;
    public String kind = "";
    public String type = "";
    public String no = "";
    public String mcompany = "";
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
    public Integer rent = 0;
    public Integer[] maxSMR = new Integer[]{-1, -1, -1, -1};
    private List<LocalDate[]> termAllSupport;
    private static String DATE_FORMAT = KomatsuDataParameter.DATE_FORMAT;
    private static Map<String, List> DATA_INDEX = KomatsuDataParameter.DATALAYOUT_INDEX;
    private static Map<String, String> POWERLINE_CHECK = KomatsuDataParameter.POWERLINE;

    public SyaryoAnalizer(SyaryoObject4 syaryo) {
        this.syaryo = syaryo;
        this.syaryo.startHighPerformaceAccess();
        settings(syaryo);
        //this.syaryo.stopHighPerformaceAccess();
    }

    public SyaryoAnalizer(SyaryoObject4 syaryo, Map<String, List> header) {
        this.syaryo = syaryo;
        this.syaryo.startHighPerformaceAccess();
        DATA_INDEX = header;
        settings(syaryo);
        //this.syaryo.stopHighPerformaceAccess();
    }

    public SyaryoObject4 get() {
        SyaryoObject4 s = syaryo;
        return s;
    }

    private List<String> check() {
        List<String> enable = new ArrayList<>();
        for (String d : KomatsuDataParameter.DATA_ORDER) {
            if (syaryo.get(d) != null) {
                enable.add(d);
            }
        }
        return enable;
    }

    private void settings(SyaryoObject4 syaryo) {
        //Name
        this.kind = syaryo.getName().split("-")[0];
        this.type = syaryo.getName().split("-")[1];
        this.no = syaryo.getName().split("-")[2];

        //データ検証
        List<String> enableSet = check();

        //Status
        for (String key : enableSet) {
            switch (key) {
                case "SMR":
                    if (syaryo.get("SMR") != null) {
                        maxSMR[0] = Integer.valueOf(getValue("SMR", "-1", true).get(getValue("SMR", "-1", true).size() - 1));
                        maxSMR[1] = Integer.valueOf(getValue("SMR", "SVC_MTR", true).get(getValue("SMR", "SVC_MTR", true).size() - 1));
                    }
                    if (syaryo.get("KOMTRAX_SMR") != null) {
                        maxSMR[2] = Integer.valueOf(getValue("KOMTRAX_SMR", "-1", true).get(getValue("KOMTRAX_SMR", "-1", true).size() - 1));
                        maxSMR[3] = Integer.valueOf(getValue("KOMTRAX_SMR", "SMR_VALUE", true).get(getValue("KOMTRAX_SMR", "SMR_VALUE", true).size() - 1));
                        komtrax = komtrax || true;
                    }
                    break;
                case "仕様":
                    komtrax = komtrax || syaryo.get("仕様").get("1").get(0).equals("1");
                    break;
                case "中古車":
                    used = true;
                    StringBuilder sb = new StringBuilder();
                    for (String date : syaryo.get("中古車").keySet()) {
                        sb.append(date);
                        sb.append(",");
                    }
                    sb.deleteCharAt(sb.length() - 1);
                    usedlife = sb.toString().split(",");
                    break;
                case "オールサポート":
                    allsupport = true;
                    //期間計算
                    termAllSupport = new ArrayList<>();
                    for (String st : syaryo.get("オールサポート").keySet()) {
                        LocalDate start = LocalDate.parse(st, DateTimeFormatter.ofPattern(DATE_FORMAT));
                        LocalDate end = LocalDate.parse((String) syaryo.get("オールサポート").get(st).get(DATA_INDEX.get("オールサポート").indexOf("MK_KIYK")), DateTimeFormatter.ofPattern(DATE_FORMAT));
                        termAllSupport.add(new LocalDate[]{start, end});
                    }
                    break;
                case "廃車":
                    dead = true;
                    lifestop = syaryo.get("廃車").keySet().stream().findFirst().get();
                    break;
                case "受注":
                    numOrders = syaryo.get("受注").size();
                    List<String> odrv = getValue("受注", "ODDAY", true);
                    currentLife = odrv == null ? "NA" : odrv.get(numOrders - 1);
                    int dayIdx = DATA_INDEX.get("受注").indexOf("ODDAY");
                    if (dayIdx > -1) {
                        for (String sbn : syaryo.get("受注").keySet()) {
                            String date = (String) syaryo.get("受注").get(sbn).get(dayIdx);
                            sbnDate.put(sbn, date);

                            if (dateSBN.get(date) == null) {
                                dateSBN.put(date, sbn);
                            } else {
                                dateSBN.put(date, dateSBN.get(date) + "," + sbn);
                            }
                        }
                    }
                    break;
                case "顧客":
                    List custv = getValue("顧客", "KKYKCD", false);
                    numOwners = custv == null?-1:((Long) custv.stream().distinct().count()).intValue();
                    
                    //レンタル業者か判定
                    List<String> custKbn = getValue("顧客", "KKYK_KBN", false);
                    List<String> custGycd = getValue("顧客", "GYSCD", false);
                    if(custKbn.contains("0E") || custKbn.contains("0G") || custGycd.contains("17"))
                        rent = 1;
                    break;
                case "部品":
                    numParts = syaryo.get("部品").size();
                    break;
                case "作業":
                    numWorks = syaryo.get("作業").size();
                    break;
                case "新車":
                    lifestart = syaryo.get("新車").keySet().stream().findFirst().get();
                    
                    mcompany = syaryo.get("仕様").get("0").get(0).toString();
                    break;
            }
        }

        //Life
        if (!dead) {
            currentAge_day = lifestart.equals("")?-1:age("20170501"); //データ受領日(データによって数日ずれている)
        } else {
            currentAge_day = lifestop.equals("")?-1:age(lifestop); //廃車日
        }

    }

    //SMRをサービスSMRかKOMTRAX_SMRのどちらかに変換
    private String[] getSMR(SyaryoObject4 syaryo) {
        if (syaryo.get("KOMTRAX_SMR") != null) {
            return new String[]{"KOMTRAX_SMR", "SMR_VALUE"};
        } else if (syaryo.get("SMR") != null) {
            return new String[]{"SMR", "SVC_MTR"};
        } else {
            return new String[]{"NULL", "NULL"};
        }
    }

    //作番と日付をswで相互変換
    private Map<String, String> sbnDate = new HashMap<>();
    private Map<String, String> dateSBN = new HashMap<>();

    public String getSBNDate(String sbn, Boolean sw) {
        if (sw) {
            //SBN -> Date
            return sbnDate.get(sbn.split("#")[0]);
        } else {
            //Date -> SBN
            return dateSBN.get(sbn.split("#")[0]);
        }
    }

    //指定作番の作業を返す。
    public Map<String, List<String>> getSBNWork(String sbn) {
        return getSBNData("作業", sbn);
    }

    //指定作番の部品を返す。
    public Map<String, List<String>> getSBNParts(String sbn) {
        return getSBNData("部品", sbn);
    }

    //指定作番のデータを返す。
    private Map<String, List<String>> getSBNData(String key, String sbn) {
        if (syaryo.get(key) == null) {
            return null;
        }

        List<String> sbns = syaryo.get(key).keySet().stream()
            .filter(s -> s.split("#")[0].equals(sbn))
            .collect(Collectors.toList());

        Map map = new LinkedHashMap();
        for (String ksbn : sbns) {
            map.put(ksbn, syaryo.get(key).get(ksbn));
        }

        if (map.isEmpty()) {
            return null;
        }
        return map;
    }

    //選択
    public Map<String, List<String>> getValue(String key, Integer[] index) {
        //例外処理1
        if (syaryo.get(key) == null) {
            return null;
        }

        List<Integer> idxs = Arrays.asList(index);
        //例外処理2  Map size < Index size
        if (syaryo.get(key).values().stream().findFirst().get().size() < idxs.stream().mapToInt(idx -> idx).max().getAsInt()) {
            return null;
        }

        //指定列を抽出したKey-Valueデータを作成
        Map map = syaryo.get(key).entrySet().stream()
            .collect(Collectors.toMap(s -> s.getKey(), s -> idxs.stream()
            .map(i -> i < 0 ? s.getKey() : s.getValue().get(i))
            .collect(Collectors.toList())
            ));

        return map;
    }

    //列抽出:keyデータのindex列をsortedしてリストで返す
    public List<String> getValue(String key, String index, Boolean sorted) {
        //例外処理1
        if (syaryo.get(key) == null) {
            return null;
        }

        if (index.equals("-1")) {
            List list = syaryo.get(key).keySet().stream().map(s -> s.split("#")[0]).collect(Collectors.toList());
            return list;
        }

        int idx = DATA_INDEX.get(key).indexOf(index);
        //例外処理2
        if (idx == -1) {
            return null;
        }

        List list = syaryo.get(key).values().stream().map(l -> l.get(idx)).collect(Collectors.toList());

        if (sorted) {
            list = (List) list.stream().map(v -> Integer.valueOf(v.toString().split("#")[0])).sorted().map(v -> v.toString()).collect(Collectors.toList());
        }

        return list;
    }

    public Map export(Map<String, Integer[]> exportHeader) {
        Map<String, Map<String, List<String>>> exportMap = new TreeMap<>();

        //エクスポートヘッダで指定した要素の取得
        exportHeader.entrySet().stream()
            .filter(h -> syaryo.get(h.getKey()) != null)
            .forEach(h -> exportMap.put(h.getKey(), getValue(h.getKey(), h.getValue())));

        return exportMap;
    }

    private static SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

    //納車されてからstopまでの経過日数
    public Integer age(String stop) {
        String fstop = stop.split("#")[0];
        return time(lifestart, fstop);
    }

    //startからstopまでの経過日数計算
    public static Integer time(String start, String stop) {
        try {
            Date st = sdf.parse(start);
            Date sp = sdf.parse(stop);
            Long age = (sp.getTime() - st.getTime()) / (1000 * 60 * 60 * 24);
            
            if(age == 0L)
                age = 1L;
            
            return age.intValue();
        } catch (ParseException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    //オールサポート対象期間のサービスを返す
    public Map<String, List<String>> allsupportService() {
        if (!allsupport) {
            return null;
        }

        int idx = DATA_INDEX.get("受注").indexOf("ODDAY");
        Map<String, List<String>> map = new LinkedHashMap<>();
        Map<String, List> services = syaryo.get("受注");
        for (String sbn : services.keySet()) {
            String date = services.get(sbn).get(idx).toString();
            if (!checkAS(date)) {
                continue;
            }
            map.put(sbn, services.get(sbn));
        }

        if (map.isEmpty()) {
            return null;
        }
        return map;
    }

    //オールサポート対象期間か判定
    private Boolean checkAS(String d) {
        LocalDate date = LocalDate.parse(d, DateTimeFormatter.ofPattern(DATE_FORMAT));
        for (LocalDate[] term : termAllSupport) {
            if (!(term[0].isAfter(date) || term[1].isBefore(date))) {
                return true;
            }
        }
        return false;
    }

    //aschekc = true : オールサポート対象期間のパワーラインサービスを返す
    //aschekc = false: オールサポート対象のパワーラインサービスを返す
    public Map<String, List<String>> powerlineService(Boolean ascheck) {
        if (syaryo.get("作業") == null) {
            return null;
        }
        if (ascheck) {
            if (!allsupport) {
                return null;
            }
        }

        int sg_idx = DATA_INDEX.get("作業").indexOf("SGYOCD");
        List<String> plSbns = syaryo.get("作業").entrySet().parallelStream()
            .filter(e -> checkPL(e.getValue().get(sg_idx).toString()))
            .map(e -> e.getKey())
            .collect(Collectors.toList());

        int idx = DATA_INDEX.get("受注").indexOf("ODDAY");
        Map<String, List<String>> map = new LinkedHashMap<>();
        Map<String, List> services = syaryo.get("受注");
        for (String sbn : services.keySet()) {
            if (!plSbns.contains(sbn)) {
                continue;
            }
            if (ascheck) {
                String date = services.get(sbn).get(idx).toString();
                if (!checkAS(date)) {
                    continue;
                }
            }
            map.put(sbn, services.get(sbn));
        }

        if (map.isEmpty()) {
            return null;
        }
        return map;
    }

    //オールサポート対象のパワーライン修理か判定
    public Boolean checkPL(String workID) {
        if (workID.length() > 3) {
            String device4 = workID.substring(0, 4);
            if (POWERLINE_CHECK.get(device4) != null) {
                return true;
            }
        }

        if (workID.length() > 1) {
            String device2 = workID.substring(0, 2);
            if (POWERLINE_CHECK.get(device2) != null) {
                return true;
            }
        }

        return false;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("syaryo:" + syaryo.getName() + " Analize:\n");
        sb.append(" kind = " + kind + "\n");
        sb.append(" type = " + type + "\n");
        sb.append(" no = " + no + "\n");
        sb.append(" used = " + used + "\n");
        sb.append(" komtrax = " + komtrax + "\n");
        sb.append(" allsupport = " + allsupport + "\n");
        sb.append(" allsupport_term = " + (termAllSupport != null ? termAllSupport.stream().map(s -> Arrays.asList(s).toString()).collect(Collectors.joining(",")) : "[]") + "\n");
        sb.append(" dead = " + dead + "\n");
        sb.append(" lifestart = " + lifestart + "\n");
        sb.append(" lifestop = " + lifestop + "\n");
        sb.append(" currentLife = " + currentLife + "\n");
        sb.append(" currentAge_day = " + currentAge_day + "\n");
        sb.append(" usedlife = " + (usedlife != null ? Arrays.asList() : "[]") + "\n");
        sb.append(" numOwners = " + numOwners + "\n");
        sb.append(" numOrders = " + numOrders + "\n");
        sb.append(" numParts = " + numParts + "\n");
        sb.append(" numWorks = " + numWorks + "\n");
        sb.append(" maxSMR = " + Arrays.asList(maxSMR) + "\n");
        return sb.toString();
    }

    public static void main(String[] args) {
        Map<String, SyaryoObject4> syaryoMap = new SyaryoToZip3().read("syaryo\\syaryo_obj_PC138US_form.bz2");
        SyaryoAnalizer analize = new SyaryoAnalizer(syaryoMap.get("PC138US-2-5322"));
        System.out.println(analize.toString());

        //Check
        System.out.println("20110801:" + analize.checkAS("20110801"));
        System.out.println("20340801:" + analize.checkAS("20340801"));
    }

    @Override
    public void close() throws Exception {
        syaryo.stopHighPerformaceAccess();
    }
}
