/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import index.SyaryoObjectElementsIndex;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author ZZ17390
 */
public class BetweenDateTest {
    static final private String DATE_FORMAT = "yyyyMMdd";
    private static LocalDate startDate;
    private static LocalDate endDate;
    
    public static void main(String[] args) {
        SyaryoObjectElementsIndex.getInstance().getIndex();
        
        startDate = LocalDate.parse("20110101", DateTimeFormatter.ofPattern(DATE_FORMAT));
        endDate = LocalDate.parse("20130101", DateTimeFormatter.ofPattern(DATE_FORMAT));
        
        String date = "20120831";
        System.out.println(date+":"+between(date));
        date = "20170831";
        System.out.println(date+":"+between(date));
    }
    
    private static boolean between(String d){
        LocalDate date = LocalDate.parse(d, DateTimeFormatter.ofPattern(DATE_FORMAT));
        return !(startDate.isAfter(date) || endDate.isBefore(date));
    }
}
