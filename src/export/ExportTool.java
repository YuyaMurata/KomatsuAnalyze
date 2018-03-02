/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import obj.SyaryoElements;
import obj.SyaryoObject2;

/**
 *
 * @author zz17390
 */
public class ExportTool {
    public static String nullCheck(Object value) {
        if (value == null) {
            return "0";
        } else {
            return value.toString();
        }
    }
    
    public static String extractCompany(SyaryoObject2 syaryo) {
        Optional company = syaryo.getSMR().values().stream()
            .map(f -> f.get(SyaryoElements.SMR.Company.getNo()))
            .filter(com -> !com.equals("??"))
            .findFirst();

        if (!company.isPresent()) {
            company = syaryo.getOwner().values().stream()
                .map(f -> f.get(SyaryoElements.SMR.Company.getNo()))
                .filter(com -> !com.equals("??"))
                .findFirst();

            if (!company.isPresent()) {
                System.out.println(syaryo.getName());
                return null;
            }
        }

        return company.get().toString();
    }
    
    public static List<String> extractOwner(SyaryoObject2 syaryo){
        List owner = syaryo.getOwner().values().stream()
            .map(f -> f.get(SyaryoElements.Customer.Code.getNo()))
            .collect(Collectors.toList());
        
        return owner;
    }
}
