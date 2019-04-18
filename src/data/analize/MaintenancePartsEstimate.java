/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analize;

import data.code.PartsCodeConv;
import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import obj.SyaryoLoader;
import obj.SyaryoObject;
import param.KomatsuUserParameter;

/**
 *
 * @author ZZ17807
 */
public class MaintenancePartsEstimate {

    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();
    private static Map<String, String> PARTS_DEF = KomatsuUserParameter.PC200_MAINTEPARTS_DEFNAME;

    public static void main(String[] args) {
        LOADER.setFile("PC200_form");

        String def = "M008";
        String f = PARTS_DEF.get(def);

        try (PrintWriter pw = CSVFileReadWrite.writerSJIS("PC200_"+f+".csv")) {
            //Header
            pw.println("SID,部品.会社,部品.作番,部品.品番,部品.品名,部品.数量,部品.金額");
            
            LOADER.getSyaryoMap().values().stream().filter(s -> s.get("部品")!=null).forEach(s -> {
                List<String> l = getTargetParts(s, def);
                l.stream().forEach(pw::println);
            });
        }

    }

    private static List<String> getTargetParts(SyaryoObject s, String def) {
        s.startHighPerformaceAccess();

        List<String> l;
        String sid = s.name;
        System.out.println(sid);

        //部品情報取得
        l = s.get("部品").entrySet().stream()
                .filter(p -> PartsCodeConv.partsConv(LOADER, sid, p.getKey(), p.getValue()).equals(def))
                .map(p
                        -> sid + ","
                + p.getValue().get(LOADER.index("部品", "会社CD")) + ","
                + p.getKey() + ","
                + p.getValue().get(LOADER.index("部品", "HNBN")) + ","
                + p.getValue().get(LOADER.index("部品", "BHN_NM")) + ","
                + p.getValue().get(LOADER.index("部品", "JISI_SU")) + ","
                + p.getValue().get(LOADER.index("部品", "SKKG"))
                ).collect(Collectors.toList());

        s.stopHighPerformaceAccess();

        return l;
    }
}
