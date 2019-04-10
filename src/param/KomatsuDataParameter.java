/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package param;

import file.ListToCSV;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import file.MapToJSON;

/**
 *
 * @author zz17390
 */
public interface KomatsuDataParameter {

    public static String[] KISY_LIST = new String[]{
        //"PC138US",
        //"PC78US",
        "PC200"
    //"PC200LC", "PC210", "PC210LC",
    //"HB205", "HB205LC", "HB215", "HB215LC",
    //"PC228US", "PC228USLC",
    //"WA100",
    //"WA470",
    //"PC10MR", "PC10UU", "PC18MR", "PC20MR", "PC20UU"
    };

    public static Map joinMap = new HashMap() {
        {
            put("PC200", new String[]{"8", "8N1", "10"});
            put("PC138US", new String[]{"2", "8", "10"});
        }
    };
	
    //Create Syaryo Object
    public static String TEMPLATE_PATH = "template\\";
    public static String MIDDLEDATA_PATH = "middle\\";
    public static String OBJECT_PATH = "syaryo\\";
    public static String GEN_PATH = "settings\\generator\\";
    public static String GEN_INDEX_PATH = GEN_PATH+"index\\";
    public static String SHUFFLE_FORMAT_PATH = GEN_INDEX_PATH+"shuffle_format.json";
    public static String SETTING_GETDATA_PATH = GEN_INDEX_PATH+"syaryo_data_index.csv";
    public static String SETTING_GECUSTTDATA_PATH = GEN_INDEX_PATH+"customer_data_index.csv";
    public static String CUSTOMER_INDEX_PATH = GEN_INDEX_PATH+"customer_data_index.json";
    public static String HONSYA_INDEX_PATH = GEN_INDEX_PATH+"honsya_index.json";
    public static String SUMMARY_PATH = "summary\\";
    public static String PRODUCT_SOURCE = GEN_INDEX_PATH+"product_201703.csv";
    
    //Formalize
    public static String PRODUCT_INDEXPATH = GEN_INDEX_PATH+"product_index.json";
    public static String DATE_FORMAT = "yyyyMMdd";
    
    //Layout
    //public static Map<String, List> DATALAYOUT_INDEX = SyaryoObjectElementsIndex.getInstance().getIndex();
    public static String LAYOUT_FORMAT_PATH = GEN_INDEX_PATH+"obj_layout_format.json";
    
    //KUECコード
    public static List KUEC_LIST = ListToCSV.toList(GEN_PATH+"define\\kuec_custercd.csv");
    
    //車両分析用
    public static String AZ_PATH = "settings\\analizer\\";
    
    public static String SYARYOOBJECT_FDPATH = "syaryo\\";

    //Exporter
    public static String EXPORT_PATH = "export\\";

    //R
    public static String R_FUNC_PATH = "R\\KMRFunction.R";

    //エラー紐付け処理時のデータソース
    public static String[] ERR_SOURCE = new String[]{"kom_order", "parts", "work_info", "service"};
    public static String ERR_DATAPROCESS_PATH = "error_proc\\";

    //認証関連
    public static String AUTH_PATH = AZ_PATH+"index\\autholize.json";

    //サブキーの変換が必要なものの対応表
    public static List TRANS_DATE = Arrays.asList(new String[]{"受注", "作業", "部品"});

    //名称定義
    public static Map PC_KMERR_EDEFNAME = new MapToJSON().toMap(AZ_PATH+"define\\PC200_Komtrax_Error_name.json");
    public static Map PC_PID_SYSDEFNAME = new MapToJSON().toMap(AZ_PATH+"define\\PC200_PartsID_SystemDefine_name.json");
    public static String KOMTRAX_FILE_DEFINE = AZ_PATH+"define\\komtrax_data_define.csv";

    //PC200 KR
    public static Map PC_KR_SMASTER = new MapToJSON().toMap(GEN_PATH+"define\\PC200_KR_SyaryoMaster.json");
    
    //サブディーラ
    public static List<String> DEALER_REJECT_LIST = ListToCSV.toList(GEN_PATH+"define\\reject_サブディーラ担当コード.csv");
}
