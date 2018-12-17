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
import file.SyaryoTemplateToJSON;
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
        SyaryoTemplateToJSON json = new SyaryoTemplateToJSON();
        
        //SyaryoIndex
        Map allsyaryo = syaryoindex(json);
        Map othersyaryo = othersyaryoindex(json);
        comparisonSyaryo(allsyaryo, othersyaryo);
        
        for (String errsource : ERR_SOURCE) {
            String filename = OUTPATH + errsource + "_error.json";
            json.write(
                filename,
                outError(allsyaryo, index, errsource)
            );
        }
    }

    //Set Syaryo Index
    private static Map syaryoindex(SyaryoTemplateToJSON json) {
        Map index = new HashMap();
        File file = new File(OUTPATH + "allsyaryo_index.json");
        if (file.exists()) {
            return json.reader(file.getAbsolutePath());
        }

        try (Connection con = HiveDB.getConnection()) {
            Statement stmt = con.createStatement();
            String temp_sql = "select e.kisy, e.typ, e.syhk, e.kiban "
                + "from EQP_SYARYO e ";
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
        
        if((new File(OUTPATH+errsource+"_error.csv")).exists())
            return null;
        
        try (PrintWriter csv = CSVFileReadWrite.writer(OUTPATH+errsource+"_error.csv");
            Connection con = HiveDB.getConnection()) {
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
                
                //エラー機種用テンプレート
                if(errsource.equals("kom_order")){
                    //Name
                    String skisy = res.getString(EQP.Syaryo.KISY.get());
                    String type = res.getString(EQP.Syaryo.TYP.get());
                    String s_type = res.getString(EQP.Syaryo.SYHK.get());
                    String kiban = res.getString(EQP.Syaryo.KIBAN.get());
                    
                    name = skisy+"-"+type+"-"+s_type+"-"+kiban;
                    
                    SimpleTemplate temp = map.get(name);
                    if(temp == null)
                        map.put(name, new SimpleTemplate("1"));
                    else
                        temp.name.set(0, String.valueOf(Integer.valueOf(temp.name.get(0))+1));
                }else{
                    //Name
                    String skisy = res.getString(EQP.Syaryo.KISY.get());
                    String kiban = res.getString(EQP.Syaryo.KIBAN.get());
                    
                    name = skisy+"-"+kiban;
                    
                    SimpleTemplate temp = map.get(name);
                    if(temp == null)
                        map.put(name, new SimpleTemplate("1"));
                    else
                        temp.name.set(0, String.valueOf(Integer.valueOf(temp.name.get(0))+1));
                }
                
                //エラーデータ出力
                csv.println(String.join(",", content));
                //System.out.println(String.join(",", content));

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
    
    private static void comparisonSyaryo(Map all, Map other){
        System.out.println("Comparison all and others.");
        System.out.println("KOMPAS登録名,工場登録名");
        for(Object name : all.keySet()){
            SimpleTemplate simple = (SimpleTemplate) all.get(name);
            String n = SimpleTemplate.check(simple.getShortName().split("-")[0], simple.getShortName().split("-")[1]);
            if(other.get(n) != null)
                System.out.println(n+","+name);
        }
    }
    
    //Set Other Corp. Syaryo Index
    private static Map othersyaryoindex(SyaryoTemplateToJSON json) {
        Map index = new HashMap();
        File file = new File(OUTPATH + "othersyaryo_index.json");
        if (file.exists()) {
            return json.reader(file.getAbsolutePath());
        }

        try (Connection con = HiveDB.getConnection()) {
            Statement stmt = con.createStatement();
            String temp_sql = "select e.kisy, e.typ, e.syhk, e.kiban "
                + "from SYARYO e where MAKR_KBN not rlike '^A.*'";
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

        System.out.println("Other Syaryo:" + index.size());
        json.write(file.getAbsolutePath(), index);
        return index;
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
