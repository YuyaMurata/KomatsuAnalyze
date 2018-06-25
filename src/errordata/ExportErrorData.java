/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package errordata;

import static creator.create.TemplateCreate.index;
import creator.template.SimpleTemplate;
import db.HiveDB;
import db.field.EQP;
import file.CSVFileReadWrite;
import java.io.File;
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
import json.SyaryoTemplateToJson;
import param.KomatsuDataParameter;

/**
 * エラーデータ(機種・機番が存在しない)を出力
 *
 * @author ZZ17390
 */
public class ExportErrorData {

    private static String[] ERR_SOURCE = KomatsuDataParameter.ERR_SOURCE;
    private static String OUTPATH = KomatsuDataParameter.ERR_DATAPROCESS_PATH;

    public static void main(String[] args) {
        //Create Folder
        if (!(new File(OUTPATH)).exists()) {
            (new File(OUTPATH)).mkdir();
        }

        //Layout Index
        Map<String, List> index = index();
        System.out.println("Layout:");
        index.entrySet().stream()
            .filter(e -> Arrays.asList(ERR_SOURCE).contains(e.getKey()))
            .map(e -> "  " + e.getKey() + ":" + e.getValue())
            .forEach(System.out::println);
        //File
        SyaryoTemplateToJson json = new SyaryoTemplateToJson();
        
        //SyaryoIndex
        Map allsyaryo = syaryoindex(json);
        
        for (String errsource : ERR_SOURCE) {
            String filename = OUTPATH + errsource + "_.json";
            json.write(
                filename,
                outError(allsyaryo, index, errsource)
            );
        }
    }

    //Set Syaryo Index
    private static Map syaryoindex(SyaryoTemplateToJson json) {
        Map index = new HashMap();
        File file = new File(OUTPATH + "allsyaryo_index.json");
        if (file.exists()) {
            return json.reader(file.getAbsolutePath());
        }

        try (Connection con = HiveDB.getConnection()) {
            Statement stmt = con.createStatement();
            String temp_sql = "select e.kisy, e.typ, e.syhk, e.kiban "
                + "from EQP_SYARYO e "
                + "join SYARYO s on (e.kisy=s.kisy and e.kiban=s.kiban)";
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

    //エラーデータ出力
    private static Map outError(Map all, Map<String, List> layoutIndex, String errsource) {
        Map<String, SimpleTemplate> map = new HashMap();

        try (Connection con = HiveDB.getConnection()) {
            List<String> code = layoutIndex.get(errsource);
            String join = code.get(code.size() - 1);
            code = code.subList(0, code.size() - 1);

            Statement stmt = con.createStatement();
            String sql = "";
            if (join.equals("None")) {
                sql = createSQL(errsource, code, "");
            } else {
                sql = createSQL(errsource, code, "", join);
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
                    if (str == null) {
                        str = "None";
                    } else if (str.equals("")) {
                        str = "None";
                    }
                    content.add(str);
                }

                //Syaryo Indexに存在するか確認
                String name = SimpleTemplate.check(content.get(code.indexOf("KISY")), content.get(code.indexOf("KIBAN")));

                //エラーが発生していないデータは除外
                if (name != null) {
                    //Exists Syaryo!
                    continue;
                }

                name = content.get(code.indexOf("KISY")) + "-" + content.get(code.indexOf("KIBAN"));

                //エラーデータ出力
                SimpleTemplate syaryo = map.get(name);
                if (syaryo == null) {
                    syaryo = new SimpleTemplate(name);
                }
                syaryo.add(errsource, String.join(",", content));
                //csv.println(String.join(",", content));
                //System.out.println(String.join(",", content));

                map.put(name, syaryo);

                n++;
                if (n % 10000 == 0) {
                    System.out.println(n + " 処理");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        return map;
    }

    //SQL
    private static String createSQL(String table, List<String> code, String kisy) {
        StringBuilder sb = new StringBuilder("select ");
        for (String c : code) {
            sb.append(c).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(" from ");
        sb.append(table).append(" ");
        /*if (kisy.length() > 0) {
            sb.append("where kisy='").append(kisy).append("'");
        }*/
        return sb.toString();
    }

    //JOIN SQL
    private static String createSQL(String table, List<String> code, String kisy, String join) {
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

        /*if (kisy.length() > 0) {
            sb.append(" where b.kisy='").append(kisy).append("'");
        }*/
        return sb.toString();
    }
}
