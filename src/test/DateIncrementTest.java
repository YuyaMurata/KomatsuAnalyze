/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author murata
 */
public class DateIncrementTest {

    public static void main(String[] args) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        try {
            Date date = sdf.parse("2017/12/01");
            System.out.println(date.toString());

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            for (int i = 0; i < 60; i++) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                System.out.println(calendar.get(Calendar.MONTH)+"/"+calendar.get(Calendar.DAY_OF_MONTH));
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
