/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

import analizer.SyaryoAnalizer;
import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.List;
import obj.SyaryoLoader;

/**
 *
 * @author zz17807
 */
public class RejectCheck {

    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();

    public static void main(String[] args) {
        LOADER.setFile("PC200_form");

        try (PrintWriter pw = CSVFileReadWrite.writerSJIS("PC200_メンテナンス排除データ.csv")) {
            //header
            pw.println("SID,KEY," + String.join(",", LOADER.indexes("受注")));

            LOADER.getSyaryoMap().values().forEach(s -> {
                try (SyaryoAnalizer a = new SyaryoAnalizer(s, true)) {
                    if (a.numAccident == 0) {
                        //List<String> r = a.rejectManiteData();
                        //r.stream().forEach(pw::println);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.exit(0);
                }
            });
        }
    }
}
