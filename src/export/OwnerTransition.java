/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

import static export.LifeOrderPrice.extractMaxOrder;
import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import json.JsonToSyaryoObj;
import json.MapIndexToJSON;
import obj.SyaryoElements;
import obj.SyaryoObject2;

/**
 *
 * @author ZZ17390
 */
public class OwnerTransition {

    private static String kisy = "PC200";

    public static void main(String[] args) {
        String filename = "json\\syaryo_obj_" + kisy + "_form.json";
        Map<String, SyaryoObject2> syaryoMap = new JsonToSyaryoObj().reader3(filename);

        String outputname = "life_owner_trans_" + kisy + ".csv";
        try (PrintWriter csv = CSVFileReadWrite.writer(outputname)) {
            extractOwnerTransitions(syaryoMap, csv);
        }
    }

    public static void extractOwnerTransitions(Map<String, SyaryoObject2> syaryoMap, PrintWriter csv) {
        //Customer Index
        Map index = new MapIndexToJSON().reader("index\\customer_index.json");
        
        int cnt = 0;

        csv.println("Company,ID,Kisy,Type,Times,Owners, Date");
        for (SyaryoObject2 syaryo : syaryoMap.values()) {
            cnt++;

            StringBuilder sb = new StringBuilder();
            String company = ExportTool.extractCompany(syaryo);
            if (company == null) {
                continue;
            }

            sb.append(company);
            sb.append(",");
            sb.append(syaryo.getName());
            sb.append(",");
            sb.append(syaryo.getMachine());
            sb.append(",");
            sb.append(syaryo.getType());
            sb.append(",");
            
            if (syaryo.getOwner() != null) {
                sb.append(syaryo.getOwner().size());
                sb.append(",");
                
                for (List owner : syaryo.getOwner().values()) {
                    String cust_company = (String) owner.get(SyaryoElements.Customer.Company.getNo());
                    String id = (String) owner.get(SyaryoElements.Customer.ID.getNo());
                    if(index.get(cust_company+"_"+id)!= null)
                        sb.append(index.get(cust_company+"_"+id));
                    else
                        sb.append(owner.get(SyaryoElements.Customer.ID.getNo()));
                    sb.append("_");
                    sb.append(owner.get(SyaryoElements.Customer.Name.getNo()));
                    sb.append(",");
                    sb.append(owner.get(SyaryoElements.Customer.Date.getNo()));
                    sb.append(",");
                }
            }else
                sb.append("0");

            sb.deleteCharAt(sb.lastIndexOf(","));

            csv.println(sb.toString());

            if (cnt % 1000 == 0) {
                System.out.println(cnt + "台");
            }
        }
    }

}
