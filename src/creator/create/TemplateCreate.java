/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.create;

import param.KomatsuDataParameter;
import creator.template.SimpleTemplate;
import db.HiveDB;
import db.field.EQP;
import file.CSVFileReadWrite;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import json.SyaryoTemplateToJson;

/**
 * 車両のテンプレートファイルを生成
 * 抽出データの定義ファイル(syaryo_data_index.csv)に従ってCentOSに構築されたDBからデータを抽出
 * @author ZZ17390
 */
public class TemplateCreate {
    private static String INDEX_PATH = KomatsuDataParameter.SETTING_GETDATA_PATH;
    private static String ROOTPATH = KomatsuDataParameter.TEMPLATE_PATH;
    private static String[] kisyList = KomatsuDataParameter.KISY_LIST;
    private static String OUTPATH;

    public static void main(String[] args) {
        for (String k : kisyList) {
            create(k);
        }
    }

    public static void create(String kisy) {
        //Folder
        OUTPATH = ROOTPATH + kisy + "\\";
        File path = new File(OUTPATH);
        if (!path.exists()) {
            path.mkdir();
        }

        //File
        SyaryoTemplateToJson json = new SyaryoTemplateToJson();

        //Layout Index
        Map<String, List> index = index();
        System.out.println("Layout:");
        index.entrySet().stream().map(e -> "  " + e.getKey() + ":" + e.getValue()).forEach(System.out::println);

        //Syaryo Index
        Map<String, SimpleTemplate> syaryoMap = syaryoindex(kisy, json);
        System.out.println("\n車両:");
        System.out.println(syaryoMap);
        
        if(syaryoMap.isEmpty()){
            System.err.println("Do not find kisy="+kisy);
            return ;
        }

        //Create Layout Template
        template(index, syaryoMap, json, kisy);

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

    //Set Syaryo Index
    private static Map syaryoindex(String kisy, SyaryoTemplateToJson json) {
        Map index = new HashMap();
        File file = new File(OUTPATH + "syaryo_" + kisy + "_index.json");
        if (file.exists()) {
            return json.reader(file.getAbsolutePath());
        }

        try (Connection con = HiveDB.getConnection()) {
            Statement stmt = con.createStatement();
            String temp_sql = "select e.kisy, e.typ, e.syhk, e.kiban "
                + "from EQP_SYARYO e "
                + "join SYARYO s on (e.kisy=s.kisy and e.kiban=s.kiban) "
                + "where e.kisy='" + kisy + "'";
            System.out.println("Running: " + temp_sql);
            ResultSet res = stmt.executeQuery(temp_sql);

            int n = 0;
            while (res.next()) {
                n++;

                //Name
                String skisy = res.getString(EQP.Syaryo.KISY.get());
                String type = res.getString(EQP.Syaryo.TYP.get());
                String s_type = res.getString(EQP.Syaryo.SYHK.get());
                String kiban = res.getString(EQP.Syaryo.KIBAN.get());

                //SimpleTemplate
                SimpleTemplate temp = new SimpleTemplate(skisy, type, s_type, kiban);
                index.put(temp.getName(), temp);

                if (n % 1000 == 0) {
                    System.out.println(n + " 台処理");
                }
            }
        } catch (SQLException ex) {
        }

        json.write(file.getAbsolutePath(), index);
        return index;
    }

    //Create Template
    private static void template(Map<String, List> layoutIndex, Map<String, SimpleTemplate> syaryoMap, SyaryoTemplateToJson json, String kisy) {
        //エラーフォルダ作成
        String err_path = OUTPATH + "error\\";
        File folder = new File(err_path);
        if (!folder.exists()) {
            folder.mkdir();
        }

        //JSONフォルダ作成
        String json_path = OUTPATH + "json\\";
        folder = new File(json_path);
        if (!folder.exists()) {
            folder.mkdir();
        }

        //テンプレート作成
        for (String table : layoutIndex.keySet()) {
            //File
            String filename = json_path + table + "_" + kisy + ".json";
            String errname = err_path + table + "_" + kisy + "_error.csv";

            //Check
            if ((new File(filename)).exists()) {
                System.out.println("> exists " + filename);
                continue;
            }

            //
            List<String> code = layoutIndex.get(table);
            json.write(
                filename,
                simpleTemplate(kisy, syaryoMap, table, code.subList(0, code.size() - 1), code.get(code.size() - 1), errname)
            );
        }
    }

        private static Map simpleTemplate(String kisy, Map<String, SimpleTemplate> syaryoMap, String table, List<String> code, String join, String err) {
            Map<String, SimpleTemplate> map = new HashMap();

            try (Connection con = HiveDB.getConnection();
                PrintWriter err_pw = CSVFileReadWrite.writer(err)) {
                Statement stmt = con.createStatement();
                String sql = "";
                if (join.equals("None")) {
                    sql = createSQL(table, code, kisy);
                } else {
                    sql = createSQL(table, code, kisy, join);
                    code.add(0, "KISY");
                    code.add(1, "KIBAN");
                }

                System.out.println("Running: " + sql);
                ResultSet res = stmt.executeQuery(sql);

                int n = 0;
                while (res.next()) {
                    List<String> content = new ArrayList<>();
                    for (String c : code) {
                        String str = res.getString(c);
                        if(str == null)
                            str = "None";
                        else if(str.equals(""))
                            str = "None";
                        content.add(str);
                    }

                    //Syaryo Indexに存在するか確認
                    String name = SimpleTemplate.check(content.get(code.indexOf("KISY")), content.get(code.indexOf("KIBAN")));

                    //エラー処理
                    if (name == null) {
                        err_pw.println(content);
                        continue;
                    }

                    //データ追加
                    SimpleTemplate syaryo = map.get(name);
                    if (syaryo == null) {
                        syaryo = new SimpleTemplate(syaryoMap.get(name).name);
                    }
                    syaryo.add(table, String.join(",", content));

                    map.put(syaryo.getName(), syaryo);

                    n++;
                    if(n%10000 == 0)
                        System.out.println(n+" 処理");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            return map;
        }

        public static String createSQL(String table, List<String> code, String kisy) {
            StringBuilder sb = new StringBuilder("select ");
            for (String c : code) {
                sb.append(c).append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(" from ");
            sb.append(table).append(" ");
            if (kisy.length() > 0) {
                sb.append("where kisy='").append(kisy).append("'");
            }
            return sb.toString();
        }

        public static String createSQL(String table, List<String> code, String kisy, String join) {
            StringBuilder sb = new StringBuilder("select ");
            sb.append("b.KISY,b.KIBAN,");
            for (String c : code) {
                sb.append("a.").append(c).append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(" from ");
            sb.append(table).append(" a");

            //JOIN
            String[] j = join.split(",");
            sb.append(" join ").append(j[1]).append(" b on (");
            for (int i = 2; i < j.length; i++) {
                sb.append("a.").append(j[i]).append("=")
                    .append("b.").append(j[i]).append(" and ");
            }
            sb.delete(sb.length() - 5, sb.length()).append(")");

            if (kisy.length() > 0) {
                sb.append(" where b.kisy='").append(kisy).append("'");
            }

            return sb.toString();
        }
}
