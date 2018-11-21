/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

import index.SyaryoObjectElementsIndex;
import java.util.List;
import java.util.Map;
import obj.SyaryoObject4;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class ExportCustomerRent {
    //レイアウト
    private static Map<String, List> dataIndex = SyaryoObjectElementsIndex.getInstance().getIndex();
    private static String PATH = KomatsuDataParameter.SYARYOOBJECT_FDPATH;
    private static String KISY = "PC200";
    private static Map<String, SyaryoObject4> map;
    
    private static String syaryofilename = PATH + "syaryo_obj_" + KISY + "_sv_form.bz2";
    private static String filename = "ExportData_"+KISY;
    
    public static void main(String[] args) {
        
    }
}
