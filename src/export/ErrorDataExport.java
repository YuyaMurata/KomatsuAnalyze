/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

import data.filter.MainteFilter;
import file.CSVFileReadWrite;
import file.ListToCSV;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class ErrorDataExport {

    private static String ERROR = KomatsuDataParameter.ERR_DATAPROCESS_PATH;
    private static String order = "kom_order_error.csv";
    private static String service = "service_error.csv";
    private static String parts = "parts_error.csv";
    private static String work = "work_info_error.csv";

    public static void main(String[] args) {
        List<String> cids = ListToCSV.toList("error_proc\\cust\\PC200_exp_errorcustid.csv");
        System.out.println("Company+CustID:" + cids.size());

        exportCustomer(cids);
        exportMainte();
    }

    private static void exportCustomer(List<String> cids) {
        Map<String, String> sbns = new HashMap<>();
        List<String> errp_cids = new ArrayList<>();

        //受注
        int total = 0;
        int cnt = 0;
        try (PrintWriter pw = CSVFileReadWrite.writer(ERROR + "cust\\c_" + order)) {
            try (BufferedReader br = CSVFileReadWrite.readerSJIS(ERROR + order)) {
                String line;
                while ((line = br.readLine()) != null) {
                    String cid = line.split(",")[0] + "_" + line.split(",")[7];
                    total++;
                    if (!cids.contains(cid)) {
                        continue;
                    }
                    errp_cids.add(cid);
                    cnt++;

                    String sbn = line.split(",")[0] + "_" + line.split(",")[1];
                    sbns.put(sbn, "1");

                    pw.println(line);

                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        System.out.println("受注テーブル完了:" + cnt + "/" + total);

        //サービス
        total = 0;
        cnt = 0;
        try (PrintWriter pw = CSVFileReadWrite.writer(ERROR + "cust\\c_" + service)) {
            try (BufferedReader br = CSVFileReadWrite.readerSJIS(ERROR + service)) {
                String line;
                while ((line = br.readLine()) != null) {
                    String cid = line.split(",")[0] + "_" + line.split(",")[6];
                    total++;
                    if (!cids.contains(cid)) {
                        continue;
                    }
                    errp_cids.add(cid);
                    cnt++;

                    String sbn = line.split(",")[0] + "_" + line.split(",")[1];
                    sbns.put(sbn, "1");

                    pw.println(line);

                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        System.out.println("サービス経歴テーブル完了:" + cnt + "/" + total);

        //該当顧客
        System.out.println("顧客:" + errp_cids.stream().distinct().count());

        //作番重複除去
        System.out.println("作番:" + sbns.size());

        //部品
        total = 0;
        cnt = 0;
        try (PrintWriter pw = CSVFileReadWrite.writer(ERROR + "cust\\c_" + parts)) {
            try (BufferedReader br = CSVFileReadWrite.readerSJIS(ERROR + parts)) {
                String line;
                while ((line = br.readLine()) != null) {
                    String sbn = line.split(",")[2] + "_" + line.split(",")[3];
                    total++;
                    if (!sbns.containsKey(sbn)) {
                        continue;
                    }
                    cnt++;

                    pw.println(line);

                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        System.out.println("部品明細テーブル完了:" + cnt + "/" + total);

        //作業
        total = 0;
        cnt = 0;
        try (PrintWriter pw = CSVFileReadWrite.writer(ERROR + "cust\\c_" + work)) {
            try (BufferedReader br = CSVFileReadWrite.readerSJIS(ERROR + work)) {
                String line;
                while ((line = br.readLine()) != null) {
                    String sbn = line.split(",")[2] + "_" + line.split(",")[3];
                    total++;
                    if (!sbns.containsKey(sbn)) {
                        continue;
                    }
                    cnt++;

                    pw.println(line);

                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        System.out.println("作業明細テーブル完了:" + cnt + "/" + total);
    }

    private static void exportMainte() {
        Map<String, String> sbns = new HashMap<>();
        Map<String, Integer> errp_cids = new HashMap<>();

        //部品
        int total = 0;
        int cnt = 0;
        try (PrintWriter pw = CSVFileReadWrite.writer(ERROR + "cust\\mainte\\m_eg_c_" + parts)) {
            try (BufferedReader br = CSVFileReadWrite.reader(ERROR + "cust\\c_" + parts)) {
                String line;
                while ((line = br.readLine()) != null) {
                    String sbn = line.split(",")[2] + "_" + line.split(",")[3];
                    String hnbn = line.split(",")[5];
                    total++;
                    if (!MainteFilter.egoilDetect(hnbn)) {
                        continue;
                    }
                    cnt++;

                    sbns.put(sbn, "1");

                    pw.println(line);

                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        System.out.println("部品明細テーブル完了:" + cnt + "/" + total);

        total = 0;
        cnt = 0;
        try (PrintWriter pw = CSVFileReadWrite.writer(ERROR + "cust\\mainte\\m_eg_c_" + order)) {
            try (BufferedReader br = CSVFileReadWrite.reader(ERROR + "cust\\c_" + order)) {
                String line;
                while ((line = br.readLine()) != null) {
                    String sbn = line.split(",")[0] + "_" + line.split(",")[1];
                    String cid = line.split(",")[0] + "_" + line.split(",")[7];

                    total++;
                    if (!sbns.containsKey(sbn)) {
                        continue;
                    }
                    if(errp_cids.get(cid) == null)
                        errp_cids.put(cid, 0);
                    errp_cids.put(cid, errp_cids.get(cid)+1);
                    cnt++;

                    sbns.put(sbn, "1");
                    pw.println(line);

                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        System.out.println("受注テーブル完了:" + cnt + "/" + total);

        //該当顧客
        System.out.println("顧客:" + errp_cids.size());

        //作番重複除去
        System.out.println("作番:" + sbns.size());
        
        //出力
        ListToCSV.toCSV("PC200_err_proc_cidrelate.csv", errp_cids.entrySet().stream().map(c -> c.getKey()+","+c.getValue()).collect(Collectors.toList()));

    }
}
