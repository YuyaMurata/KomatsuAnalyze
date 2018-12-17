/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package errordata;

import param.KomatsuDataParameter;
import creator.template.SimpleTemplate;
import file.CSVFileReadWrite;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import file.SyaryoTemplateToJSON;

/**
 *
 * @author ZZ17390
 */
public class ErrorTemplateCreate {

    private static String INDEX_PATH = KomatsuDataParameter.SETTING_GETDATA_PATH;
    private static String[] ERR_SOURCE = KomatsuDataParameter.ERR_SOURCE;
    private static String ROOTPATH = KomatsuDataParameter.ERR_DATAPROCESS_PATH;
    private static String INPUTPATH = KomatsuDataParameter.ERR_DATAPROCESS_PATH + "csv\\";
    private static String OUTPATH = ROOTPATH;

    public static void main(String[] args) {
        create();
    }

    public static void create() {
        //Folder
        File path = new File(OUTPATH);
        if (!path.exists()) {
            path.mkdir();
        }

        //File
        SyaryoTemplateToJSON json = new SyaryoTemplateToJSON();

        //Layout Index
        Map<String, List> index = index();
        System.out.println("Layout:");
        index.entrySet().stream().map(e -> "  " + e.getKey() + ":" + e.getValue()).forEach(System.out::println);

        //Create Layout Template
        template(index, ERR_SOURCE, json);

    }

    //Set Layout Index
    public static Map index() {
        try (BufferedReader br = CSVFileReadWrite.readerSJIS(INDEX_PATH)) {
            String line;
            Map index = new HashMap();
            while ((line = br.readLine()) != null) {
                String table = line;
                String number = br.readLine();
                String name = br.readLine();
                String[] code = br.readLine().split(",");
                String[] select = br.readLine().split(",");
                String[] joinTo = br.readLine().split(",");
                br.readLine();

                List layout = new ArrayList();
                for (int i = 1; i < select.length; i++) {
                    if (select[i].equals("1")) {
                        layout.add(code[i]);
                    }
                }

                if (layout.isEmpty()) {
                    continue;
                }

                if (joinTo.length > 1) {
                    layout.add(Arrays.asList(joinTo).stream().filter(s -> s.length() > 0).collect(Collectors.joining(",")));
                } else {
                    layout.add("None");
                }

                System.out.println(table + ":" + layout);

                index.put(table.split(",")[1], layout);

            }

            return index;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    //Create Template
    private static void template(Map<String, List> layoutIndex, String[] errSource, SyaryoTemplateToJSON json) {
        //JSONフォルダ作成
        String json_path = OUTPATH + "json\\";
        File folder = new File(json_path);
        if (!folder.exists()) {
            folder.mkdir();
        }

        Map syaryoMap = new TreeMap();

        //テンプレート作成
        for (String table : errSource) {
            //File
            String filename = json_path + table + "_error.json";

            //Check
            if ((new File(filename)).exists()) {
                System.out.println("> exists " + filename);
                continue;
            }

            //
            List<String> code = layoutIndex.get(table);
            json.write(
                    filename,
                    simpleTemplate(syaryoMap, table, code.subList(0, code.size() - 1), code.get(code.size() - 1))
            );
        }
    }

    private static Map simpleTemplate(Map<String, SimpleTemplate> syaryoMap, String table, List<String> code, String join) {
        Map<String, SimpleTemplate> map = new HashMap();
        try (BufferedReader csv = CSVFileReadWrite.reader(INPUTPATH + table + "_error.csv")) {
            String line;
            int n = 0;
            while ((line = csv.readLine()) != null) {
                List<String> content = new ArrayList<>();
                for (String c : line.split(",")) {
                    String str = c;
                    if (str == null) {
                        str = "None";
                    } else if (str.equals("")) {
                        str = "None";
                    }
                    content.add(str);
                }

                //Syaryo Indexに存在するか確認
                String kisy = content.get(code.indexOf("KISY"));
                String typ = "";
                String syhk = "";
                if (table.equals("kom_order")) {
                    typ = content.get(code.indexOf("TYP"));
                    syhk = content.get(code.indexOf("SYHK"));
                }
                String kiban = content.get(code.indexOf("KIBAN"));
                String name = SimpleTemplate.check(kisy, kiban);

                //データ追加
                SimpleTemplate syaryo;
                if (name == null) {
                    syaryo = new SimpleTemplate(kisy, typ, syhk, kiban);
                } else {
                    syaryo = map.get(name);
                    if (syaryo == null) {
                        syaryo = new SimpleTemplate(kisy, typ, syhk, kiban);
                    }
                }
                syaryo.add(table, String.join(",", content));

                map.put(syaryo.getName(), syaryo);

                n++;
                if (n % 10000 == 0) {
                    System.out.println(n + " 処理");
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return map;
    }
}
