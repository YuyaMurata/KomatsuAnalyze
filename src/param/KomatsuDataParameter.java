/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package param;

import index.SyaryoObjectElementsIndex;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import json.MapIndexToJSON;

/**
 *
 * @author zz17390
 */
public interface KomatsuDataParameter {

    public static String[] KISY_LIST = new String[]{
        "PC138US",
        "PC78US",
        "PC200", "PC200LC", "PC210", "PC210LC",
        "HB205", "HB205LC", "HB215", "HB215LC",
        "PC228US", "PC228USLC",
        "WA100",
        "WA470",
        "PC10MR", "PC10UU", "PC18MR", "PC20MR", "PC20UU"
    };
    
    public static Map joinMap = new HashMap(){{
        put("PC200", new String[]{"8", "8N1", "10"});
        put("PC138US", new String[]{"2", "8", "10"});
    }};

    //Create Syaryo Object
    public static String TEMPLATE_PATH = "template\\";
    public static String MIDDLEDATA_PATH = "middle\\";
    public static String OBJECT_PATH = "syaryo\\";
    public static String INDEX_PATH = "index\\";
    public static String SHUFFLE_FORMAT_PATH = "index\\shuffle_format.json";
    public static String SETTING_GETDATA_PATH = "index\\syaryo_data_index.csv";
    public static String SETTING_GECUSTTDATA_PATH = "index\\customer_data_index.csv";
    public static String CUSTOMER_INDEX_PATH = "index\\customer_data_index.json";
    public static String PC_ERRORFLG_INDEX_PATH = "index\\pc_errorflg_index.json";
    public static String HONSYA_INDEX_PATH = "index\\honsya_index.json";
    public static String SUMMARY_PATH = "summary\\";
    public static String PRODUCT_SOURCE = "index\\product_201703.csv";
    
    //EasyViewer
    public static String SYARYOOBJECT_FDPATH = "syaryo\\";
    public static String SETTING_DATAFILETER_PATH = "index\\easyviwer_datafilter.csv";
    public static String GRAPH_PY = "py\\smr_date_graph.py";
    public static String GRAPH_TEMP_FILE = "py\\csv\\graph_temp.csv";

    //Layout
    public static Map<String, List> DATALAYOUT_INDEX = SyaryoObjectElementsIndex.getInstance().getIndex();
    
    //Formalize
    public static String PRODUCT_INDEXPATH = "index\\product_index.json";
    public static String DATE_FORMAT = "yyyyMMdd";
    
    //データ順序
    public static List<String> DATA_ORDER = Arrays.asList(new String[]{
        "生産",
        "仕様",
        "詳細",
        "出荷",
        "新車",
        "中古車",
        "顧客",
        "SMR",
        "受注",
        "作業",
        "部品",
        "コマツケア",
        "コマツケア前受け金",
        "オールサポート",
        "KOMTRAX_ACT_DATA",
        "KOMTRAX_SMR",
        "KOMTRAX_GPS",
        "KOMTRAX_FUEL_CONSUME",
        "KOMTRAX_ERROR",
        "KOMTRAX_ATT",
        "KOMTRAX_DIG",
        "KOMTRAX_HOIST",
        "KOMTRAX_BREAKER",
        "KOMTRAX_RELIEF",
        "KOMTRAX_PUMP",
        "KOMTRAX_RUNNING",
        "KOMTRAX_ODOMETER",
        "廃車",
        "経歴"
    });
    //KUECコード
    public static List KUEC_LIST = Arrays.asList(new String[]{
        "C4410486",
        "E4800520",
        "E9120014",
        "EKUE0010",
        "G5720208",
        "G6300229",
        "N0127358",
        "OKH00500",
        "S0500150",
        "S3111867",
        "S6110720",
        "T5160826",
        "T5160827",
        "U3243614",
        "UJ132824"
    });
    
    //R
    public static String R_FUNC_PATH = "R\\KMRFunction.R";
    
    //エラー紐付け処理時のデータソース
    public static String[] ERR_SOURCE = new String[]{"kom_order", "parts", "work_info", "service"};
    public static String ERR_DATAPROCESS_PATH = "error_proc\\";
    
    //認証関連
    public static String AUTH_PATH = "index\\autholize.json";
    
    //分析用 (将来的にはユーザーが定義できるようにする)
    //オールサポート対象 パワーライン
    public static Map POWERLINE = new MapIndexToJSON().reader("index\\allsupport_index.json");
    
    //定期メンテナンスの定義
    public static Map<String, List> PERIOD_MAINTE = new HashMap(){{
        put("受注.SGYO_KTICD", Arrays.asList(new String[]{"AA","AB","AS","BF","BJ","BL"}));
        put("作業.SGYOCD", Arrays.asList(new String[]{"B@BBB4"}));
    }};
    
    //KOMTRAXエラーコードのフラグの定義
    public static Map PC_ERROR = new MapIndexToJSON().reader(PC_ERRORFLG_INDEX_PATH);
    
    //サブキーの変換が必要なものの対応表
    public static List TRANS_DATE = Arrays.asList(new String[]{"受注", "作業", "部品"});
    
    //部品コードの再定義
    public static String PC_PARTS_REDEF_INDEX_PATH = "index\\pc200_parts_redefine.json";
    public static Map PC_PARTS_REDEF = new MapIndexToJSON().reader(PC_PARTS_REDEF_INDEX_PATH);
    
    //名称定義
    public static String PC_KMERR_DEFNAME_INDEX_PATH = "define\\PC200_Komtrax_Error_name.json";
    public static Map PC_KMERR_EDEFNAME = new MapIndexToJSON().reader(PC_KMERR_DEFNAME_INDEX_PATH);
    public static String PC_PARTS_DEFNAME_INDEX_PATH = "define\\PC200_Parts_Redefine_name.json";
    public static Map PC_PARTS_EDEFNAME = new MapIndexToJSON().reader(PC_PARTS_DEFNAME_INDEX_PATH);
}
