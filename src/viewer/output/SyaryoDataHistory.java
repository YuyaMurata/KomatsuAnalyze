/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package viewer.output;

import analizer.SyaryoAnalizer;
import file.CSVFileReadWrite;
import java.io.PrintWriter;
import obj.SyaryoLoader;
import obj.SyaryoObject;

/**
 *
 * @author kaeru
 */
public class SyaryoDataHistory {
    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();
    
    public static void output(String filename, SyaryoObject syaryo){
        try (PrintWriter pw = CSVFileReadWrite.writerSJIS(filename)) {
            try (SyaryoAnalizer a = new SyaryoAnalizer(syaryo, false)) {
                //基本情報
                pw.println(a.get().name);
                pw.println("会社CD," + a.mcompany + ",新車/生産," + a.lifestart+",最終SMR,"+a.maxSMR[2]+","+a.maxSMR[3]+",KOMTRAX_SMR,"+(a.get().get("KOMTRAX_SMR")!=null?a.get().get("KOMTRAX_SMR").size():"-1"));
                
                //顧客情報
                pw.println("\n顧客情報");
                LOADER.getHeader().get("顧客").values().stream().map(s -> String.join(",", s)).forEach(pw::println);
                a.get().get("顧客").values().stream().distinct().map(s -> String.join(",", s)).forEach(pw::println);

                //サービス情報
                pw.println("\nサービス情報");
                if (a.get().get("受注") != null) {
                    LOADER.getHeader().get("受注").values().stream().map(s -> "作番," + String.join(",", s)).forEach(pw::println);
                    a.get().get("受注").entrySet().stream().map(s -> s.getKey() + "," + String.join(",", s.getValue())).forEach(pw::println);
                } else {
                    pw.println("None");
                }

                //作業明細
                pw.println("\n作業明細");
                if (a.get().get("作業") != null) {
                    LOADER.getHeader().get("作業").values().stream().map(s -> "作番," + String.join(",", s)).forEach(pw::println);
                    a.get().get("作業").entrySet().stream().map(s -> s.getKey() + "," + String.join(",", s.getValue())).forEach(pw::println);
                } else {
                    pw.println("None");
                }

                //部品明細
                pw.println("\n部品明細");
                if (a.get().get("部品") != null) {
                    LOADER.getHeader().get("部品").values().stream().map(s -> "作番," + String.join(",", s)).forEach(pw::println);
                    a.get().get("部品").entrySet().stream().map(s -> s.getKey() + "," + String.join(",", s.getValue())).forEach(pw::println);
                } else {
                    pw.println("None");
                }
                
                System.out.println(syaryo.name+" CSV DL Done!");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
