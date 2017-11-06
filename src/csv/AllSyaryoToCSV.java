/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csv;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import json.JsonToSyaryoObj;
import obj.SyaryoObject;

/**
 *
 * @author ZZ17390
 */
public class AllSyaryoToCSV {

    public static void main(String[] args) {
        String path = "..\\KomatsuData\\分析結果\\";
        String kisy = "PC200";
        Map<String, SyaryoObject> syaryoMap = new JsonToSyaryoObj().reader("json\\syaryo_obj_" + kisy + "_form.json");

        //service(path + kisy, syaryoMap);
        //order(path+kisy, syaryoMap);
        //komtrax(path+kisy, syaryoMap);
        //komtraxError(path+kisy, syaryoMap);
        //orderDataCount(path+kisy, syaryoMap, "KDPF");
        //workDataCount(kisy, syaryoMap, "01");
        workDataCount2(kisy, syaryoMap, "01");
    }

    public static void order(String filename, Map<String, SyaryoObject> syaryoMap) {
        PrintWriter pw;

        try {
            pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(filename + "_order.csv"))));
            for (SyaryoObject syaryo : syaryoMap.values()) {
                if (syaryo.getOrder() != null) {
                    for (String date : syaryo.getOrder().keySet()) {
                        List order = syaryo.getOrder(date);
                        //System.out.println(order);
                        pw.println(syaryo.getName() + "," + syaryo.getType() + "," + order.stream().collect(Collectors.joining(",")));
                    }
                }
            }

            pw.close();
        } catch (IOException ex) {
        }
    }

    public static void komtrax(String filename, Map<String, SyaryoObject> syaryoMap) {
        PrintWriter pw1, pw2;

        try {
            pw1 = new PrintWriter(new BufferedWriter(new FileWriter(new File(filename + "_komtrax_syaryoDB.csv"))));
            pw2 = new PrintWriter(new BufferedWriter(new FileWriter(new File(filename + "_komtrax_komtraxDB.csv"))));

            List<SyaryoObject> syaryos = syaryoMap.values().stream()
                    .filter(s -> s.getType().equals("8"))
                    .collect(Collectors.toList());

            for (SyaryoObject syaryo : syaryos) {
                String[] name = syaryo.getName().split("-");
                if (syaryo.getKomtrax()) {
                    pw1.println(name[0] + "," + name[1] + "," + name[2] + "," + syaryo.getKomtrax());
                }
                if (syaryo.getSMR() != null) {
                    for (String date : syaryo.getSMR().keySet()) {
                        if (syaryo.getSMR().get(date).get(1).toString().contains("komtrax")) {
                            pw2.println(name[0] + "," + name[1] + "," + name[2] + "," + syaryo.getKomtrax());
                            break;
                        }
                    }
                }
            }

            pw1.close();
            pw2.close();
        } catch (IOException ex) {
        }
    }

    public static void service(String filename, Map<String, SyaryoObject> syaryoMap) {
        PrintWriter pw;

        try {
            pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(filename + "_service_num.csv"))));

            List<String> typs = syaryoMap.values().stream()
                    .map(s -> s.getType())
                    .distinct()
                    .collect(Collectors.toList());

            Map<String, Integer> map = new TreeMap();
            for (String typ : typs) {
                List<SyaryoObject> syaryos = syaryoMap.values().stream()
                        .filter(s -> s.getType().toString().equals(typ))
                        .collect(Collectors.toList());

                for (SyaryoObject syaryo : syaryos) {
                    for (String date : syaryo.getHistory().keySet()) {
                        List history = syaryo.getHistory().get(date);
                        if (!history.get(1).toString().contains("service")) {
                            continue;
                        }
                        String d = date.split("#")[0];
                        if (map.get(d + "," + typ) == null) {
                            map.put(d + "," + typ, 1);
                        } else {
                            map.put(d + "," + typ, map.get(d + "," + typ) + 1);
                        }
                    }
                }
            }

            for (String key : map.keySet()) {
                pw.println(key + "," + map.get(key));
            }

            pw.close();
        } catch (IOException ex) {
        }
    }

    public static void komtraxError(String filename, Map<String, SyaryoObject> syaryoMap) {
        PrintWriter pw;

        try {
            pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(filename + "_error_num.csv"))));

            Map<String, Map<String, Integer>> map = new TreeMap();
            List<String> header = new ArrayList<>();
            for (SyaryoObject syaryo : syaryoMap.values()) {
                if (syaryo.getError() == null) {
                    continue;
                }

                TreeMap<String, Integer> errCode = new TreeMap<>();
                map.put(syaryo.getName(), errCode);

                for (List err : syaryo.getError().values()) {
                    errCode.put((String) err.get(0), Integer.valueOf((String) err.get(1)));
                    header.add((String) err.get(0));
                }
            }

            header = header.stream().distinct().collect(Collectors.toList());
            pw.println("," + header.stream().collect(Collectors.joining(",")));
            for (String name : map.keySet()) {
                List err = new ArrayList();
                for (String code : header) {
                    if (map.get(name).get(code) == null) {
                        err.add(0);
                    } else {
                        err.add(map.get(name).get(code));
                    }
                }
                pw.println(name + "," + err.stream().map(s -> s.toString()).collect(Collectors.joining(",")));
            }

            pw.close();
        } catch (IOException ex) {
        }
    }

    public static void orderDataCount(String filename, Map<String, SyaryoObject> syaryoMap, String rule) {
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(filename + "_extract_ordercnt_r" + rule + ".csv"))));
            pw.println("name,customer," + rule + "_CNT");

            for (String name : syaryoMap.keySet()) {
                List data = new ArrayList();
                data.add(name);
                data.add(syaryoMap.get(name).getOwner().values().stream().map(own -> own.get(0)).findFirst().get());
                Long cnt = 0L;
                if (syaryoMap.get(name).getOrder() != null) {
                    for (List order : syaryoMap.get(name).getOrder().values()) {
                        if (order.get(20).toString().contains(rule)) {
                            cnt++;
                        }
                    }
                }

                data.add(cnt.toString());
                pw.println(data.stream().collect(Collectors.joining(",")));
            }

            pw.close();
        } catch (IOException ex) {
        }

    }

    public static void workDataCount(String filename, Map<String, SyaryoObject> syaryoMap, String rule) {
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(filename + "_extract_workcnt_r" + rule + ".csv"))));
            pw.println("type," + rule + "_CNT, inv");

            Map<String, Integer> map = new TreeMap();
            Map<String, Integer> map2 = new TreeMap();
            for (String name : syaryoMap.keySet()) {
                if (map.get(syaryoMap.get(name).getType()) == null) {
                    map.put(syaryoMap.get(name).getType(), 0);
                    map2.put(syaryoMap.get(name).getType(), 0);
                }
                if (syaryoMap.get(name).getWork() != null) {
                    Boolean flg = true;
                    for (List work : syaryoMap.get(name).getWork().values()) {
                        if (work.get(4).toString().length() < 2) {
                            continue;
                        }
                        if (work.get(4).toString().substring(0, 2).equals(rule)) {
                            map.put(syaryoMap.get(name).getType(), map.get(syaryoMap.get(name).getType()) + 1);
                            flg = false;
                            break;
                        }
                    }
                    if (flg) {
                        map2.put(syaryoMap.get(name).getType(), map2.get(syaryoMap.get(name).getType()) + 1);
                    }
                }
            }

            for (String typ : map.keySet()) {
                pw.println(typ + "," + map.get(typ) + "," + map2.get(typ));
            }

            pw.close();
        } catch (IOException ex) {
        }

    }

    public static void workDataCount2(String filename, Map<String, SyaryoObject> syaryoMap, String rule) {
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(filename + "_workcnt_r" + rule + ".csv"))));
            pw.println("name,type," + rule + "_CNT, 金額, 会社, 作番, SMR, 経過年");

            for (String name : syaryoMap.keySet()) {
                Integer cnt = 0;
                Integer price = -1;
                String sbn = "";
                String smr = "";
                String comp = "";
                Integer year = -1;
                if (syaryoMap.get(name).getWork() != null) {
                    for (String date : syaryoMap.get(name).getWork().keySet()) {
                        List work = syaryoMap.get(name).getWork().get(date);
                        if (work.get(4).toString().length() < 2) {
                            continue;
                        }
                        if (work.get(4).toString().substring(0, 2).equals(rule)) {
                            cnt++;
                            price = Integer.valueOf(work.get(8).toString());
                            sbn = work.get(0).toString();
                            
                            String d = date.substring(0,4)+"/"+date.substring(4,6)+"/"+date.substring(6,8);
                            if(syaryoMap.get(name).getSMR() != null)
                                try{
                                smr = (String) syaryoMap.get(name).getSMR().entrySet().stream()
                                                                        .filter(s -> s.getKey().contains(d))
                                                                        .findFirst()
                                                                        .map(s -> s.getValue().get(0)).get();
                                }catch(Exception e){
                                    
                                }
                            comp = work.get(15).toString();
                            System.out.println(name +":" + date);
                            year = Integer.valueOf(date.substring(0,4)) - Integer.valueOf(syaryoMap.get(name).getOwner().keySet().stream().findFirst().get().substring(0,4));
                        }
                    }
                }
                pw.println(name+","+syaryoMap.get(name).getType()+","+cnt+","+price+","+comp+","+sbn+","+smr+","+year);
            }

            pw.close();
        } catch (IOException ex) {
        }

    }
}
