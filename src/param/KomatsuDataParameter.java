/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package param;

import java.util.Arrays;
import java.util.List;

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

    public static String TEMPLATE_PATH = "template\\";
    public static String MIDDLEDATA_PATH = "middle\\";
    public static String OBJECT_PATH = "syaryo\\";
    public static String SHUFFLE_FORMAT_PATH = "index\\shuffle_format.json";
    public static String SETTING_GETDATA_PATH = "index\\syaryo_data_index.csv";
    public static String SETTING_GECUSTTDATA_PATH = "index\\customer_data_index.csv";
    public static String CUSTOMER_INDEX_PATH = "index\\customer_data_index.json";
    public static String HONSYA_INDEX_PATH = "index\\honsya_index.json";
    public static String SUMMARY_PATH = "summary\\";
    public static String PRODUCT_SOURCE = "index\\product_201703.csv";
    
    //EasyViewer
    public static String SYARYOOBJECT_FDPATH = "syaryo\\";
    public static String SETTING_DATAFILETER_PATH = "index\\easyviwer_datafilter.csv";
    public static String GRAPH_PY = "py\\smr_date_graph.py";
    public static String GRAPH_TEMP_FILE = "py\\csv\\graph_temp.csv";

    //Formalize
    public static String PRODUCT_INDEXPATH = "index\\product_index.json";
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
        "KOMTRAX_ATT",
        "KOMTRAX_DIG",
        "KOMTRAX_HOIST",
        "KOMTRAX_BREAKER",
        "KOMTRAX_RELIEF",
        "KOMTRAX_PUMP",
        "KOMTRAX_RUNNING",
        "KOMTRAX_ODOMETER",
        "KOMTRAX_SMR",
        "KOMTRAX_ACT_DATA",
        "KOMTRAX_GPS",
        "KOMTRAX_FUEL_CONSUME",
        "KOMTRAX_ERROR",
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
}
