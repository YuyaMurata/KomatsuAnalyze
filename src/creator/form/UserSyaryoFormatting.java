/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.form;

import file.SyaryoToCompress;
import java.util.Map;
import obj.LoadSyaryoObject;
import obj.SyaryoObject;
import param.KomatsuDataParameter;
import param.KomatsuUserParameter;

/**
 * PC200Only ユーザーが定義するルールに従ってデータを削除
 * @author ZZ17390
 */
public class UserSyaryoFormatting {
    private static LoadSyaryoObject LOADER = KomatsuDataParameter.LOADER;
    public static void main(String[] args) {
        form();
    }
    
    public static void form(){
        LOADER.setFile("PC200_sv_form");
        Map<String, SyaryoObject> map = LOADER.getSyaryoMap();
        
        for(String name : KomatsuUserParameter.PC200_REJECT_LIST){
            System.out.println("reject sid = "+name);
            map.remove(name);
        }
        
        //Save
        LOADER.close();
        new SyaryoToCompress().write(LOADER.getFilePath(), map);
        System.out.println(LOADER.getFilePath());
    }
}
