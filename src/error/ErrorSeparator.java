/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package error;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import json.JsonToSyaryoObj;
import json.JsonToSyaryoTemplate;
import creator.template.SyaryoTemplate;

/**
 *
 * @author ZZ17390
 */
public class ErrorSeparator {

    public static void main(String[] args) throws IOException {
        //List<String> kisyList = Files.lines(Paths.get("syaryo_kisy_list.txt")).collect(Collectors.toList());

        Map<String, SyaryoTemplate> syaryo = new JsonToSyaryoTemplate().reader("syaryo_history_template.json");
        List<String> templateKisy = syaryo.keySet().stream().map(s -> s.split("-")[0]).distinct().collect(Collectors.toList());

        //System.out.println(kisyList.size());
        System.out.println("車両テンプレート機種数＝" + templateKisy.size());

        String path = "テスト";
        File[] flist = (new File(path)).listFiles();
        for (File f : flist) {
            if (!f.getName().contains("error")) {
                continue;
            }

            System.out.println(f.getName());

            PrintWriter kom_pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(f.getName().replace("syaryo_history", "komatsu_syaryo_history")))));
            PrintWriter oth_pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(f.getName().replace("syaryo_history", "other_syaryo_history")))));
            PrintWriter son_pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(f.getName().replace("syaryo_history", "son_syaryo_history")))));

            List<String> lines = Files.lines(Paths.get(f.toString())).collect(Collectors.toList());
            for (String line : lines) {
                String[] name = line.split(",")[1].split("-");

                try {
                    if(name.length == 0){
                        oth_pw.println(line);
                        System.out.println("other kisy=" + name[0]);
                        continue;
                    }else if (name.length > 2) {
                        if (!name[2].matches("[0-9]+")) {
                            if(templateKisy.contains(name[2])) kom_pw.println(line);
                            else oth_pw.println(line);
                            //System.out.println("other kisy=" + name[0]);
                            continue;
                        }
                    } else if (templateKisy.contains(name[0])) {
                        kom_pw.println(line);
                        continue;
                        //System.out.println("komatsu kisy=" + name[0]);
                    } else if (name[0].contains("SON") || name[0].contains("ソノタ") || name[0].equals("S") || name[0].equals("*")) {
                        son_pw.println(line);
                        continue;
                        //System.out.println("son kisy="+name[0]);
                    }
                    
                    oth_pw.println(line);
                    //System.out.println("other="+name[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(line);
                    System.exit(0);
                }
            }

            kom_pw.close();
            oth_pw.close();
            son_pw.close();
        }
    }
}
