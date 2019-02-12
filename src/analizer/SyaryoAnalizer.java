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
import java.util.TreeMap;
import java.util.stream.Collectors;
import file.SyaryoToCompress;
import java.util.AbstractMap;
import java.util.Calendar;
import java.util.Set;
import obj.SyaryoLoader;
import obj.SyaryoObject;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class SyaryoAnalizer implements AutoCloseable {

    public SyaryoObject syaryo;
    public String kind = "";
    public String type = "";
    public String no = "";
    public String mcompany = "";
    public Boolean used = false;
    public Boolean komtrax = false;
    public Boolean allsupport = false;
    public Boolean dead = false;
    public Integer rent = 0;
    public String lifestart = "";
    public String lifestop = "";
    public String currentLife = "";
    public Integer currentAge_day = -1;
    public String[] usedlife = null;
    public Integer numOwners = -1;
    public Integer numOrders = -1;
    public Integer numParts = -1;
    public Integer numWorks = -1;
    public Integer acmLCC = -1;
    public Integer numAccident = 0;
    public Integer acmAccidentPrice = 0;
    public Integer[] cluster = new Integer[3];
    public Integer[] maxSMR = new Integer[]{-1, -1, -1, -1};
    public Map<String, Integer> odrKind = new HashMap<>();
    public Map<String, Integer> workKind = new HashMap<>();
    public TreeMap<String, Map.Entry> ageSMR = new TreeMap<>();
    private List<String[]> termAllSupport;
    private static String DATE_FORMAT = KomatsuDataParameter.DATE_FORMAT;
    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();
    private static Map<String, String> POWERLINE_CHECK = KomatsuDataParameter.POWERLINE;
    private static List<String> ACCIDENT_WORDS = KomatsuDataParameter.ACCIDENT_WORDS;
    private static Map<String, String> PC200_KR_MASTER = KomatsuDataParameter.PC_KR_SMASTER;

    public SyaryoAnalizer(SyaryoObject syaryo) {
        this.syaryo = syaryo;
        this.syaryo.startHighPerformaceAccess();
        settings(syaryo);
        //this.syaryo.stopHighPerformaceAccess();
    }

    public SyaryoObject get() {
        SyaryoObject s = syaryo;
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

    private void settings(SyaryoObject syaryo) {
        //Name
        this.kind = syaryo.getName().split("-")[0];
        this.type = syaryo.getName().split("-")[1];
        this.no = syaryo.getName().split("-")[2];

        //データ検証
        List<String> enableSet = check();
        int cm = KomatsuDataParameter.MAINTE_CLUSTER.get(syaryo.name) != null ? Integer.valueOf(KomatsuDataParameter.MAINTE_CLUSTER.get(syaryo.name)) : -1;
        cluster = new Integer[]{-1, -1, cm};

        //Status
        for (String key : enableSet) {
            switch (key) {
                case "SMR":
                    if (syaryo.get("SMR") != null) {
                        maxSMR[0] = Integer.valueOf(getValue("SMR", "-1", true).get(getValue("SMR", "-1", true).size() - 1));
                        maxSMR[1] = Integer.valueOf(getValue("SMR", "VALUE", true).get(getValue("SMR", "VALUE", true).size() - 1));
                    }
                    if (syaryo.get("KOMTRAX_SMR") != null) {
                        maxSMR[2] = Integer.valueOf(getValue("KOMTRAX_SMR", "-1", true).get(getValue("KOMTRAX_SMR", "-1", true).size() - 1));
                        maxSMR[3] = Integer.valueOf(getValue("KOMTRAX_SMR", "VALUE", true).get(getValue("KOMTRAX_SMR", "VALUE", true).size() - 1));
                        //setAgeSMR(syaryo.get("KOMTRAX_ACT_DATA"), 30, 100);
                        setAgeSMR(syaryo.get("KOMTRAX_SMR"), 30, 100);
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
                        LocalDate end = LocalDate.parse((String) syaryo.get("オールサポート").get(st).get(LOADER.index("オールサポート", "満了日")), DateTimeFormatter.ofPattern(DATE_FORMAT));
                        termAllSupport.add(new String[]{start.toString(), end.toString()});
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
                    int dayIdx = LOADER.index("受注", "ODDAY");
                    int priceIdx = LOADER.index("受注", "SKKG");
                    int sgktIdx = LOADER.index("受注", "SGYO_KTICD");
                    int odrIdx = LOADER.index("受注", "UAGE_KBN_1");
                    int textIdx = LOADER.index("受注", "GAIYO_1");
                    int text2Idx = LOADER.index("受注", "GAIYO_2");
                    int textworkIdx = LOADER.index("作業", "SGYO_NM");
                    if (dayIdx > -1) {
                        acmLCC = 0;
                        for (String sbn : syaryo.get("受注").keySet()) {
                            String date = (String) syaryo.get("受注").get(sbn).get(dayIdx);
                            sbnDate.put(sbn, date);

                            //ライフサイクルコスト計算
                            Double price = Double.valueOf(syaryo.get("受注").get(sbn).get(priceIdx).toString());
                            acmLCC += price.intValue();

                            //作業形態カウント
                            String sgkt = syaryo.get("受注").get(sbn).get(sgktIdx).toString();
                            if (workKind.get(sgkt) == null) {
                                workKind.put(sgkt, 0);
                            }
                            workKind.put(sgkt, workKind.get(sgkt) + 1);

                            //売上区分カウント
                            String odr = syaryo.get("受注").get(sbn).get(odrIdx).toString()
                                + syaryo.get("受注").get(sbn).get(odrIdx + 1).toString()
                                + syaryo.get("受注").get(sbn).get(odrIdx + 2).toString();
                            if (odrKind.get(odr) == null) {
                                odrKind.put(odr, 0);
                            }
                            odrKind.put(odr, odrKind.get(odr) + 1);

                            //事故カウント
                            String text = syaryo.get("受注").get(sbn).get(textIdx).toString() + ","
                                + syaryo.get("受注").get(sbn).get(text2Idx).toString() + ","
                                + (getSBNWork(sbn) != null ? getSBNWork(sbn).values().stream().map(w -> w.get(textworkIdx)).collect(Collectors.joining(",")) : "");
                            //System.out.println(ACCIDENT_WORDS);
                            //System.out.println(text);
                            ACCIDENT_WORDS.stream().forEach(w -> {
                                if (text.contains(w)) {
                                    numAccident++;
                                    acmAccidentPrice += price.intValue();
                                }
                            });

                            //日付との相互変換用
                            if (dateSBN.get(date) == null) {
                                dateSBN.put(date, sbn);
                            } else {
                                dateSBN.put(date, dateSBN.get(date) + "," + sbn);
                            }
                        }
                    }
                    break;
                case "顧客":
                    List custv = getValue("顧客", "顧客CD", false);
                    numOwners = custv == null ? -1 : ((Long) custv.stream().distinct().count()).intValue();

                    //レンタル業者か判定
                    List<String> custKbn = getValue("顧客", "顧客区分", false);
                    List<String> custGycd = getValue("顧客", "業種CD", false);
                    if (custKbn.contains("0E") || custKbn.contains("0G") || custGycd.contains("17")) {
                        rent = 1;
                    }

                    //KR車両か判定
                    if (PC200_KR_MASTER.get(kind + "-" + no) != null) {
                        rent = 2;
                    }

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
            currentAge_day = lifestart.equals("") ? -1 : age("20170501"); //データ受領日(データによって数日ずれている)
        } else {
            currentAge_day = lifestop.equals("") ? -1 : age(lifestop); //廃車日
        }

    }
    
    private void setAgeSMR(Map<String, List> act_smr, Integer d, Integer s){
        //初期値
        ageSMR.put(lifestart, new AbstractMap.SimpleEntry<>(0, 0));
        
        // d刻みでSMRをsで丸める
        Integer span = 0;
        for(String date : act_smr.keySet()){
            Integer t = age(date) / d;
            Integer smr = Integer.valueOf(act_smr.get(date).get(0).toString()) / s;  //ACT_SMRの構成が変わるとエラー
            ageSMR.put(date, new AbstractMap.SimpleEntry<>(t, smr*s));
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

        int idx = LOADER.index(key, index);
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

    //納車されてからstopまでの経過日数
    public Map.Entry getAgeSMR(String current) {
        return ageSMR.floorEntry(current);
    }

    //startからstopまでの経過日数計算
    public static Integer time(String start, String stop) {
        try {
            Date st = sdf.parse(start);
            Date sp = sdf.parse(stop);
            Long age = (sp.getTime() - st.getTime()) / (1000 * 60 * 60 * 24);

            if (age == 0L) {
                age = 1L;
            }

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

        int idx = LOADER.index("受注", "ODDAY");
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
        for (String[] term : termAllSupport) {
            LocalDate ts = LocalDate.parse(term[0]);
            LocalDate tf = LocalDate.parse(term[1]);
            if (!(ts.isAfter(date) || tf.isBefore(date))) {
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

        int sg_idx = LOADER.index("作業", "SGYOCD");
        List<String> plSbns = syaryo.get("作業").entrySet().parallelStream()
            .filter(e -> checkPL(e.getValue().get(sg_idx).toString()))
            .map(e -> e.getKey())
            .collect(Collectors.toList());

        int idx = LOADER.index("受注", "ODDAY");
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

    public static String getHeader() {
        //基本情報
        String header = "機種,型/小変形,機番,会社,KOMTRAX,中古,廃車,オールサポート,レンタル,SMR最終更新日,SMR,KOMTRAX_SMR最終更新日,KOMTRAX_SMR,経過日,納入日,最終更新日,廃車日,中古日,";

        //保証情報
        header += "AS期間,";

        //事故情報
        header += "事故,事故受注費計,";

        //受注情報
        header += "顧客数,受注数,作業発注数,部品発注数,ライフサイクルコスト,受注情報1,受注情報2,";

        //評価情報
        header += "使われ方,経年/SMR,メンテナンス";

        return header;
    }

    public String toPrint() {
        List<String> data = new ArrayList<>();
        //基本情報
        data.add(kind);
        data.add(type);
        data.add(no);
        data.add(mcompany);
        data.add(komtrax ? "1" : "0");
        data.add(used ? "1" : "0");
        data.add(dead ? "1" : "0");
        data.add(allsupport ? "1" : "0");
        data.add(rent.toString());
        data.add(String.valueOf(maxSMR[0]));
        data.add(String.valueOf(maxSMR[1]));
        data.add(String.valueOf(maxSMR[2]));
        data.add(String.valueOf(maxSMR[3]));
        data.add(String.valueOf(currentAge_day));
        data.add(lifestart);
        data.add(currentLife);
        data.add(lifestop);
        if (used) {
            data.add(String.join("|", usedlife));
        } else {
            data.add("None");
        }

        //保証情報
        if (allsupport) {
            data.add(termAllSupport.stream().map(term -> String.join("_", term)).collect(Collectors.joining("|")));
        } else {
            data.add("None");
        }

        //事故情報
        data.add(String.valueOf(numAccident));
        data.add(String.valueOf(acmAccidentPrice));

        //受注情報
        data.add(String.valueOf(numOwners));
        data.add(String.valueOf(numOrders));
        data.add(String.valueOf(numWorks));
        data.add(String.valueOf(numParts));
        data.add(String.valueOf(acmLCC));
        data.add(workKind.keySet().stream().collect(Collectors.joining("|")));
        data.add(odrKind.keySet().stream().collect(Collectors.joining("|")));

        //評価情報
        data.add(String.valueOf(cluster[0]));
        data.add(String.valueOf(cluster[1]));
        data.add(String.valueOf(cluster[2]));

        return String.join(",", data);
    }

    public static void main(String[] args) {
        SyaryoLoader LOADER = SyaryoLoader.getInstance();
        LOADER.setFile("PC200_form");
        Map<String, SyaryoObject> syaryoMap = LOADER.getSyaryoMap();
        SyaryoAnalizer analize = new SyaryoAnalizer(syaryoMap.get("PC200-10-451215"));
        System.out.println(analize.toString());

        //Check
        //System.out.println("20180801:" + analize.checkAS("20180801"));
        //System.out.println("20340801:" + analize.checkAS("20340801"));
        
        System.out.println(analize.ageSMR);
        
        System.out.println(analize.getAgeSMR("20140514"));
        System.out.println(analize.getAgeSMR("20180801"));
    }

    @Override
    public void close() throws Exception {
        syaryo.stopHighPerformaceAccess();
    }
}
