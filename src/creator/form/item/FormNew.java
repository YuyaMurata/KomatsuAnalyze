/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.form.item;

import analizer.SyaryoAnalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author ZZ17807
 */
public class FormNew {
    public static Map form(Map<String, List<String>> news, Map<String, List<String>> born, Map<String, List<String>> deploy, List indexList) {
        Map<String, List<String>> map = new TreeMap();

        Integer deff = 0;

        if (news != null && deploy != null) {
            try{
            //なぜか空が存在した場合
            if (news.isEmpty()) {
                news = null;
            } else {
                deff = Math.abs(SyaryoAnalizer.time(news.keySet().stream().findFirst().get(), deploy.keySet().stream().findFirst().get())) / 30;
            }
            }catch(Exception e){
                System.err.println(news);
                System.err.println(deploy);
                System.exit(0);
            }

        }

        if (news == null || deff > 6) {
            //出荷情報を取得する処理
            String date = "";
            if (deploy != null) {
                date = deploy.keySet().stream().findFirst().get();
            }
            if (date.equals("") || date.equals("None")) {
                date = born.keySet().stream().findFirst().get();
            }
            List list = new ArrayList();
            for (Object s : indexList) {
                list.add("None");
            }
            map.put(date.split("#")[0], list);
            return map;
        }

        //List price index
        int hyomen = indexList.indexOf("HM_URI_KN");
        int jitsu = indexList.indexOf("RL_URI_KN");
        int hyojun = indexList.indexOf("STD_SY_KKU");

        //修正しない
        if (news.size() == 1) {
            List<String> list = news.values().stream().findFirst().get();
            if (list.get(hyomen).contains("+")) {
                for (int i = hyomen; i < list.size(); i++) {
                    list.set(i, String.valueOf(Double.valueOf(list.get(i)).intValue()));
                }
            }
            return news;
        }

        //複数存在するときの処理
        List list = new ArrayList();
        String key = "";
        String[] price = new String[3];
        Boolean flg = true;
        for (String date : news.keySet()) {
            list = news.get(date);
            if (flg) {
                key = date.split("#")[0];
                price[0] = list.get(hyomen).toString();
                price[1] = list.get(jitsu).toString();
                price[2] = list.get(hyojun).toString();
                flg = false;
            } else {
                if (list.get(hyomen) != "None") {
                    price[0] = list.get(hyomen).toString();
                }
                if (list.get(jitsu) != "None") {
                    price[1] = list.get(jitsu).toString();
                }
                if (list.get(hyojun) != "None") {
                    price[2] = list.get(hyojun).toString();
                }
            }
        }

        //List整形
        int i = 0;
        for (String s : price) {
            if (!s.equals("None")) {
                list.set(hyomen + i, String.valueOf(Double.valueOf(s).intValue()));
            }
        }

        map.put(key, list);

        return map;
    }
}
