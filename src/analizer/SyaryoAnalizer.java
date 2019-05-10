/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analizer;

import file.CSVFileReadWrite;
import java.io.PrintWriter;
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
import java.util.AbstractMap;
import obj.SyaryoLoader;
import obj.SyaryoObject;
import param.KomatsuUserParameter;
import param.obj.UserPartsObject;

/**
 *
 * @author ZZ17390
 */
public class SyaryoAnalizer implements AutoCloseable {

    public SyaryoObject syaryo;
    public String name = "";
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
    public List<String> usedlife = null;
    public Integer numOwners = -1;
    public Integer numOrders = -1;
    public Integer numParts = -1;
    public Integer numWorks = -1;
    public Integer acmLCC = -1;
    public Long numAccident = 0L;
    public Integer acmAccidentPrice = 0;
    public Integer[] cluster = new Integer[3];
    public Integer[] maxSMR = new Integer[]{-1, -1, -1, -1, -1};
    public Map<String, Integer> odrKind = new HashMap<>();
    public Map<String, Integer> workKind = new HashMap<>();
    public TreeMap<String, Map.Entry<Integer, Integer>> ageSMR = new TreeMap<>();
    public TreeMap<Integer, String> smrDate = new TreeMap<>();
    private int D_DATE = 365;
    private int D_SMR = 10;
    private List<String[]> termAllSupport;

    public static Boolean DISP_COUNT = true;
    private static String DATE_FORMAT = KomatsuUserParameter.DATE_FORMAT;
    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();
    private static Map<String, String> POWERLINE_CHECK = KomatsuUserParameter.POWERLINE;
    private static Map<String, String> PC200_KR_MASTER = KomatsuUserParameter.PC_KR_SMASTER;
    private static Map<String, String> PC200_ACCIDENT = KomatsuUserParameter.PC200_ACCIDENT;
    private static Map<String, String> PC200_ATTACHEMENT = KomatsuUserParameter.PC200_ATTACHEMENT;
    private static UserPartsObject PARTS = KomatsuUserParameter.PC200_USERPARTS_DEF;

    private static int CNT = 0;

    public SyaryoAnalizer(SyaryoObject syaryo, Boolean flg) {
        CNT++;
        this.syaryo = syaryo;
        this.name = syaryo.name;
        this.syaryo.startHighPerformaceAccess();

        //設定の詳細さを選択
        simpleSettings(syaryo);
        if (flg) {
            complexSettings(syaryo);
        }

        if (CNT % 1000 == 0 && DISP_COUNT) {
            System.out.println(CNT + " Trans SyaryoAnalizer");
        }
    }

    public SyaryoObject get() {
        SyaryoObject s = syaryo;
        return s;
    }

    public Map<String, List<String>> get(String key) {
        return syaryo.get(key);
    }

    private List<String> check() {
        List<String> enable = new ArrayList<>();
        for (String d : KomatsuUserParameter.DATA_ORDER) {
            if (get(d) != null) {
                enable.add(d);
            }
        }
        return enable;
    }

    private void simpleSettings(SyaryoObject syaryo) {
        //Name
        this.kind = syaryo.getName().split("-")[0];
        this.type = syaryo.getName().split("-")[1];
        this.no = syaryo.getName().split("-")[2];

        //データ検証
        List<String> enableSet = check();
        List<String> dates = new ArrayList<>();

        //Status
        for (String key : enableSet) {
            switch (key) {
                case "SMR":
                    maxSMR[0] = Integer.valueOf(getValue("SMR", "-1", true).get(getValue("SMR", "-1", true).size() - 1));
                    maxSMR[1] = Integer.valueOf(getValue("SMR", "VALUE", true).get(getValue("SMR", "VALUE", true).size() - 1));
                    dates.addAll(getValue("SMR", "-1", false));
                    break;
                case "KOMTRAX_ACT_DATA":
                    maxSMR[2] = Integer.valueOf(getValue("KOMTRAX_ACT_DATA", "-1", true).get(getValue("KOMTRAX_ACT_DATA", "-1", true).size() - 1));
                    maxSMR[3] = Double.valueOf(getValue("KOMTRAX_ACT_DATA", "VALUE", true).get(getValue("KOMTRAX_ACT_DATA", "VALUE", true).size() - 1)).intValue();
                    dates.addAll(getValue("KOMTRAX_ACT_DATA", "-1", false));
                    komtrax = komtrax || true;
                    break;
                case "仕様":
                    komtrax = komtrax || get("仕様").get("1").get(0).equals("1");
                    break;
                case "中古車":
                    used = true;
                    usedlife = new ArrayList<>(get("中古車").keySet());
                    dates.addAll(usedlife);
                    break;
                case "廃車":
                    dead = true;
                    dates.addAll(new ArrayList(get("廃車").keySet()));
                    break;
                case "受注":
                    int dayIdx = LOADER.index("受注", "ODDAY");
                    
                    //日付と作番の相互変換
                    get("受注").entrySet().stream().forEach(odr -> {
                        String sbn = odr.getKey();
                        String date = get("受注").get(sbn).get(dayIdx);
                        sbnDate.put(sbn, date);
                        if (dateSBN.get(date) == null) {
                            dateSBN.put(date, sbn);
                        } else {
                            dateSBN.put(date, dateSBN.get(date) + "," + sbn);
                        }
                        dates.add(date);
                    });
                    
                    rejectAttachement();
                    
                    numOrders = get("受注").size();
                case "顧客":
                    List custv = getValue("顧客", "顧客CD", false);
                    numOwners = custv == null ? -1 : ((Long) custv.stream().distinct().count()).intValue();
                    break;
                case "部品":
                    numParts = get("部品").size();
                    break;
                case "作業":
                    numWorks = get("作業").size();
                    break;
                case "新車":
                    dates.addAll(new ArrayList(get("新車").keySet()));
                    mcompany = get("仕様").get("0").get(0);
                    break;
            }
        }

        //データ上の日付を計算
        List<Integer> datesInt = dates.stream().map(d -> Integer.valueOf(d.split("#")[0])).sorted().collect(Collectors.toList());
        lifestart = String.valueOf(datesInt.get(0)); //データ上の最初
        lifestop = String.valueOf(datesInt.get(datesInt.size() - 1)); //データ上の最終
    }

    private void complexSettings(SyaryoObject syaryo) {
        //データ検証
        List<String> enableSet = check();
        //int cm = KomatsuDataParameter.MAINTE_CLUSTER.get(syaryo.name) != null ? Integer.valueOf(KomatsuDataParameter.MAINTE_CLUSTER.get(syaryo.name)) : -1;
        //cluster = new Integer[]{-1, -1, cm};

        //Status
        for (String key : enableSet) {
            switch (key) {
                case "KOMTRAX_ACT_DATA":
                    if (get("KOMTRAX_ACT_DATA") != null) {
                        setAgeSMR(get("KOMTRAX_ACT_DATA"));
                    }
                    break;
                case "オールサポート":
                    allsupport = true;
                    //期間計算
                    termAllSupport = new ArrayList<>();
                    for (String st : get("オールサポート").keySet()) {
                        LocalDate start = LocalDate.parse(st, DateTimeFormatter.ofPattern(DATE_FORMAT));
                        LocalDate end = LocalDate.parse((String) get("オールサポート").get(st).get(LOADER.index("オールサポート", "満了日")), DateTimeFormatter.ofPattern(DATE_FORMAT));
                        termAllSupport.add(new String[]{start.toString(), end.toString()});
                    }
                    break;
                case "受注":
                    List<String> odrv = getValue("受注", "ODDAY", true);
                    currentLife = odrv == null ? "NA" : odrv.get(numOrders - 1);
                    int dayIdx = LOADER.index("受注", "ODDAY");
                    int priceIdx = LOADER.index("受注", "SKKG");
                    int sgktIdx = LOADER.index("受注", "SGYO_KTICD");
                    int odrIdx = LOADER.index("受注", "UAGE_KBN_1");
                    acmLCC = 0;

                    for (String sbn : get("受注").keySet()) {
                        //ライフサイクルコスト計算
                        Double price = Double.valueOf(get("受注").get(sbn).get(priceIdx));
                        acmLCC += price.intValue();

                        //作業形態カウント
                        String sgkt = get("受注").get(sbn).get(sgktIdx);
                        if (workKind.get(sgkt) == null) {
                            workKind.put(sgkt, 0);
                        }
                        workKind.put(sgkt, workKind.get(sgkt) + 1);

                        //売上区分カウント
                        String odr = get("受注").get(sbn).get(odrIdx)
                                + get("受注").get(sbn).get(odrIdx + 1)
                                + get("受注").get(sbn).get(odrIdx + 2);
                        if (odrKind.get(odr) == null) {
                            odrKind.put(odr, 0);
                        }
                        odrKind.put(odr, odrKind.get(odr) + 1);
                    }

                    //事故カウント
                    numAccident = PC200_ACCIDENT.values().stream().filter(a -> name.equals(a)).count();

                    break;
                case "顧客":
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
            }
        }
    }

    private void setAgeSMR(Map<String, List<String>> act_smr) {
        //初期値
        ageSMR.put("0", new AbstractMap.SimpleEntry<>(0, 0));
        smrDate.put(0, lifestart);

        // d刻みでSMRをsで丸める
        for (String date : act_smr.keySet()) {
            Integer t = age(date) / D_DATE;
            Integer smr = (Double.valueOf(act_smr.get(date).get(0)).intValue() / D_SMR) * D_SMR;  //ACT_SMRの構成が変わるとエラー
            ageSMR.put(date, new AbstractMap.SimpleEntry<>(t, smr));

            if (smrDate.get(smr) == null) {
                smrDate.put(smr, date);
            }

            if (maxSMR[4] < smr) {
                maxSMR[4] = smr;
            }
        }

        //取得できていない箇所を手入力サービスメータから取得
        if (get("SMR") == null) {
            return;
        }

        List<String> l = new ArrayList<>(ageSMR.keySet());
        Integer last = Integer.valueOf(l.get(l.size() - 1));
        List<String> svsmr = get("SMR").keySet().stream()
                .filter(date -> last < Integer.valueOf(date.split("#")[0]))
                .collect(Collectors.toList());

        for (String date : svsmr) {
            Integer t = age(date) / D_DATE;
            Integer smr = (Double.valueOf(get("SMR").get(date).get(2)).intValue() / D_SMR) * D_SMR;  //SMRの構成が変わるとエラー
            ageSMR.put(date, new AbstractMap.SimpleEntry<>(t, smr));

            if (smrDate.get(smr) == null) {
                smrDate.put(smr, date);
            }

            if (maxSMR[4] < smr) {
                maxSMR[4] = smr;
            }
        }
    }

    public String getSMRToDate(Integer smr) {
        try {
            return smrDate.ceilingEntry(smr).getValue();
        } catch (NullPointerException ne) {
            return null;
        }
    }

    public Map.Entry<Integer, Integer> getDateToSMR(String date) {
        if (ageSMR.get(date) != null) {
            return ageSMR.get(date);
        } else {
            return forecast(date);
        }

        /*
        try{
            return ageSMR.floorEntry(date).getValue();
        }catch(NullPointerException ne){
            return forecast(date);
        }*/
    }

    //周辺2点から予測 精度低
    public Map.Entry<Integer, Integer> forecast(String date) {
        Integer t = age(date) / D_DATE;
        Integer smr = 0;
        Map.Entry<String, Map.Entry<Integer, Integer>> a1 = ageSMR.floorEntry(date);
        try {
            //区間点を予測
            Map.Entry<String, Map.Entry<Integer, Integer>> a2 = ageSMR.higherEntry(date);
            if (!a1.getKey().equals("0")) {
                Double a = (a2.getValue().getValue() - a1.getValue().getValue()) / time(a2.getKey(), a1.getKey()).doubleValue();
                smr = ((Double) (a1.getValue().getValue().doubleValue() + a * time(date, a1.getKey()))).intValue() / D_SMR * D_SMR;
            }
        } catch (NullPointerException ne) {
            try {
                //最終2点から未来を予測
                Map.Entry<String, Map.Entry<Integer, Integer>> a0 = ageSMR.lowerEntry(ageSMR.floorKey(date));
                Double a = (a1.getValue().getValue() - a0.getValue().getValue()) / time(a1.getKey(), a0.getKey()).doubleValue();
                smr = ((Double) (a1.getValue().getValue().doubleValue() + a * time(date, a1.getKey()))).intValue() / D_SMR * D_SMR;
            } catch (NullPointerException ne2) {
                System.out.println(syaryo.name);
                System.out.println("a0=" + ageSMR.lowerEntry(ageSMR.floorKey(date)) + " , a1=" + a1);
                ageSMR.entrySet().stream().map(a -> a.getKey() + "," + a.getValue()).forEach(System.out::println);
                ne2.printStackTrace();
                System.exit(0);
            }
        }

        return new AbstractMap.SimpleEntry<>(t, smr);
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
        if (get(key) == null) {
            return new HashMap<>();
        }

        List<String> sbns = get(key).keySet().stream()
                .filter(s -> s.split("#")[0].equals(sbn))
                .collect(Collectors.toList());

        Map map = new LinkedHashMap();
        for (String ksbn : sbns) {
            map.put(ksbn, get(key).get(ksbn));
        }

        return map;
    }

    //選択
    public Map<String, List<String>> getValue(String key, Integer[] index) {
        //例外処理1
        if (get(key) == null) {
            return null;
        }

        List<Integer> idxs = Arrays.asList(index);
        //例外処理2  Map size < Index size
        if (get(key).values().stream().findFirst().get().size() < idxs.stream().mapToInt(idx -> idx).max().getAsInt()) {
            return null;
        }

        //指定列を抽出したKey-Valueデータを作成
        Map map = get(key).entrySet().stream()
                .collect(Collectors.toMap(s -> s.getKey(), s -> idxs.stream()
                .map(i -> i < 0 ? s.getKey() : s.getValue().get(i))
                .collect(Collectors.toList())
                ));

        return map;
    }

    //列抽出:keyデータのindex列をsortedしてリストで返す
    public List<String> getValue(String key, String index, Boolean sorted) {
        //例外処理1
        if (get(key) == null) {
            return null;
        }

        if (index.equals("-1")) {
            List list = get(key).keySet().stream().map(s -> s.split("#")[0]).collect(Collectors.toList());
            return list;
        }

        int idx = LOADER.index(key, index);
        //例外処理2
        if (idx == -1) {
            return null;
        }

        List list = get(key).values().stream().map(l -> l.get(idx)).collect(Collectors.toList());

        if (sorted) {
            list = (List) list.stream().map(v -> Double.valueOf(v.toString().split("#")[0]).intValue()).sorted().map(v -> v.toString()).collect(Collectors.toList());
        }

        return list;
    }

    public Map export(Map<String, Integer[]> exportHeader) {
        Map<String, Map<String, List<String>>> exportMap = new TreeMap<>();

        //エクスポートヘッダで指定した要素の取得
        exportHeader.entrySet().stream()
                .filter(h -> get(h.getKey()) != null)
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
        Map<String, List<String>> services = get("受注");
        for (String sbn : services.keySet()) {
            String date = services.get(sbn).get(idx);
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
        if (get("作業") == null) {
            return null;
        }
        if (ascheck) {
            if (!allsupport) {
                return null;
            }
        }

        int sg_idx = LOADER.index("作業", "SGYOCD");
        List<String> plSbns = get("作業").entrySet().parallelStream()
                .filter(e -> checkPL(e.getValue().get(sg_idx)))
                .map(e -> e.getKey())
                .collect(Collectors.toList());

        int idx = LOADER.index("受注", "ODDAY");
        Map<String, List<String>> map = new LinkedHashMap<>();
        Map<String, List<String>> services = get("受注");
        for (String sbn : services.keySet()) {
            if (!plSbns.contains(sbn)) {
                continue;
            }
            if (ascheck) {
                String date = services.get(sbn).get(idx);
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

    private void rejectAttachement() {
        //アタッチメント修理の除外
        PC200_ATTACHEMENT.entrySet().stream()
                .filter(at -> at.getValue().equals(name))
                .map(at -> at.getKey().split("\\.")[1])
                .forEach(at -> {
                    this.syaryo.remove("受注", at);
                    getSBNParts(at).keySet().stream().forEach(sbn -> this.syaryo.remove("部品", sbn));
                    getSBNWork(at).keySet().stream().forEach(sbn -> this.syaryo.remove("作業", sbn));
                });
    }

    public List<String> rejectManiteData() {
        //メンテナンス定義
        List<String> sv = new ArrayList<>(KomatsuUserParameter.PC200_PERIODSERVICE.keySet());
        List<String> mainte = PARTS.getMainteSV(name);

        //メンテナンス排除作番リスト
        List<String> sbns = get("受注").entrySet().stream()
                .filter(o -> sv.contains(o.getValue().get(LOADER.index("受注", "SGYO_KTICD"))) || mainte.contains(o.getKey()))
                .map(o -> o.getKey())
                .collect(Collectors.toList());

        //排除サービスの詳細を調べる
        List<String> rejectData = sbns.stream()
                .map(sbn -> name + "," + sbn + "," + String.join(",", get("受注").get(sbn)))
                .collect(Collectors.toList());

        if (sbns.isEmpty()) {
            return rejectData;
        }

        //条件を揃える
        sbns.stream().forEach(sbn -> {
            this.syaryo.remove("受注", sbn);
            getSBNParts(sbn).keySet().stream().forEach(psbn -> this.syaryo.remove("部品", psbn));
            getSBNWork(sbn).keySet().stream().forEach(wsbn -> this.syaryo.remove("作業", wsbn));
        });

        //test
        return rejectData;
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
        try (PrintWriter pw = CSVFileReadWrite.writerSJIS("syaryo_analize_summary.csv")) {
            pw.println(getHeader());

            LOADER.getSyaryoMap().values().stream().forEach(syaryo -> {
                try (SyaryoAnalizer s = new SyaryoAnalizer(syaryo, true)) {
                    pw.println(s.toPrint());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }
    }

    @Override
    public void close() throws Exception {
        this.syaryo.stopHighPerformaceAccess();
    }
}
