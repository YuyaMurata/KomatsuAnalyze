/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.form.item;

import creator.form.DataRejectRule;
import creator.form.SyaryoObjectFormatting;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17807
 */
public class FormOwner {

    public static Map form(Map<String, List<String>> owner, List indexList, Map<String, String> honsyIndex, Map<String, List<String>> history, List indexHisList, DataRejectRule reject) {
        if (owner == null || history == null) {
            //System.out.println("Not found owner!");
            return null;
        }

        Integer company = indexList.indexOf("会社CD");
        Integer ownerID = indexList.indexOf("顧客CD");
        Integer ownerName = indexList.indexOf("顧客名");

        Integer hist_cid = indexHisList.indexOf("顧客CD");
        Integer syareki = indexHisList.indexOf("車歴区分");

        //日付データ揃え
        owner = SyaryoObjectFormatting.dateFormalize(owner);

        //本社コード揃え
        for (String d : owner.keySet()) {
            List list = owner.get(d);
            String com = list.get(company).toString();
            String id = list.get(ownerID).toString();

            if (honsyIndex.get(com + "_" + id) != null) {
                id = honsyIndex.get(com + "_" + id).split("_")[0];
            }

            reject.addKUEC(id, d.split("#")[0]); //KUECを登録

            list.set(ownerID, id);
        }

        for (String d : history.keySet()) {
            List list = history.get(d);
            String com = list.get(company).toString();
            String id = list.get(hist_cid).toString();

            if (honsyIndex.get(com + "_" + id) != null) {
                id = honsyIndex.get(com + "_" + id).split("_")[0];
            }

            list.set(hist_cid, id);
        }

        //ID重複排除 ##排除
        //System.out.println(owner.values().stream().map(l -> l.get(ownerID)).collect(Collectors.toList()));
        List owners = owner.values().stream()
                .map(l -> l.get(ownerID))
                .filter(id -> !id.toString().contains("##")) //工場IDが振られていない
                .filter(id -> !id.toString().equals("None")) //IDが存在する
                .filter(id -> !KomatsuDataParameter.KUEC_LIST.contains(id)) //KUECでない
                .collect(Collectors.toList());
        //System.out.println(owners);
        owners = SyaryoObjectFormatting.exSeqDuplicate(owners);

        if (owners.isEmpty()) {
            //System.out.println("使用顧客が存在しない車両(後で削除)");
            return null;
        }

        Map<String, List<String>> map = new TreeMap();
        int i = 0;
        for (String date : owner.keySet()) {
            if (date.length() >= 8) {
                String id = owner.get(date).get(ownerID).toString();
                if (id.equals(owners.get(i))) {
                    map.put(date, owner.get(date));
                    i++;
                    if (owners.size() <= i) {
                        break;
                    }
                }
            }
        }

        return map;
    }
}
