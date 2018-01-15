/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author ZZ17390
 */
public class DateTest {
    public static void main(String[] args) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String date = "2010/10/10 5:00:21";
        
		System.out.println(sdf.parse(date).getTime());
		
        System.out.println(date.compareTo("2017/01/01"));
        System.out.println(date.compareTo("2010/10/11"));
        System.out.println(date.compareTo("2010/10/10"));
        System.out.println(date.compareTo("2010/10/09"));
        System.out.println(date.compareTo("2010/01/01"));
        System.out.println(date.compareTo("2009/10/10"));
    }
}
