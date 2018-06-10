/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.create;

import param.KomatsuDataParameter;
import creator.template.SimpleTemplate;
import java.util.Map;
import java.util.TreeMap;
import java.io.File;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import json.SyaryoTemplateToJson;
import json.SyaryoToZip3;
import obj.SyaryoObject4;

/**
 * 車両テンプレートから車両オブジェクトを作成
 *
 * @author ZZ17390
 */
public class TemplateToObjectCreate {

    private static String[] kisyList = KomatsuDataParameter.KISY_LIST;
    private static String FILEPATH = KomatsuDataParameter.TEMPLATE_PATH;
    private static String OUTPATH = KomatsuDataParameter.MIDDLEDATA_PATH;
    //private static String path = "middle\\"+kisy+"\\mid\\";
    //private static String outpath = "middle\\"+kisy+"\\obj\\";

    public static void main(String[] args) {
        for (String kisy : kisyList) {
            create(kisy);
        }
    }

    public static void create(String kisy) {
        String fpath = FILEPATH + kisy + "\\json\\";
        String opath = OUTPATH + kisy + "\\obj\\";

        //機種を取得しているかチェック
        if (!(new File(fpath)).exists()) {
            System.out.println("Do not get kisy=" + kisy + "!");
            return;
        }

        //Layout
        Map<String, List> index = TemplateCreate.index();

        if (!(new File(opath)).exists()) {
            (new File(opath)).mkdirs();
        }

        //File
        SyaryoTemplateToJson json = new SyaryoTemplateToJson();

        File[] flist = (new File(fpath)).listFiles();
        for (File f : flist) {
            String table = f.getName().substring(0, f.getName().lastIndexOf("_"));
            String FILENAME = opath + "syaryo_mid_" + kisy + "_" + table + ".bz2";

            //テーブル設定値の列数を取得
            final int fieldLen;
            if (index.get(table).contains("None")) {
                fieldLen = index.get(table).size() - 1;
            } else {
                fieldLen = index.get(table).size() + 2 - 1;
            }
            System.out.println(table + ":" + fieldLen);

            //Folder
            File objf = new File(FILENAME);
            if (objf.exists()) {
                System.out.println("exists " + objf.getName());
                continue;
            }

            System.out.println(f.getName());

            Map<String, SimpleTemplate> templats = json.reader(f.getAbsolutePath());
            Map<String, SyaryoObject4> syaryoMap = new ConcurrentHashMap<>();

            if (templats.isEmpty()) {
                System.out.println("No Data " + objf.getName());
                continue;
            }

            //int n = 0;
            //int en = 0;
            //オブジェクト化
            templats.entrySet().parallelStream()
                .map(s -> s.getValue()).forEach(s -> {

                SyaryoObject4 syaryoObj = new SyaryoObject4(s.getName());
                syaryoObj.add(s.temp, fieldLen);

                syaryoObj.compress(true);

                syaryoMap.put(syaryoObj.getName(), syaryoObj);
            });

            SyaryoObject4 syaryo = syaryoMap.values().stream().findFirst().get();
            syaryo.decompress();
            System.out.println(syaryo.dump());
            syaryo.compress(true);
            new SyaryoToZip3().write(FILENAME, syaryoMap);

            System.out.println(syaryoMap.size());
            
            System.gc();
        }
    }
}
