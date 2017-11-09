/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author ZZ17390
 */
public class DateTest {
    public static void main(String[] args) {
        Date date = new Date();
        System.out.println(date);
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HHMMSS ");
        System.out.println(sdf.format(date));
    }
}
