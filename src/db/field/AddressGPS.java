/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db.field;

/**
 *
 * @author kaeru_yuya
 */
public class AddressGPS {
    public enum _AddressGPS implements Fields{
        prefectures_nm("prefectures_nm"),
        city_nm("city_nm"),
        oaza_nm("oaza_nm"),
        block_no("block_no"),
        coordinate_no("coordinate_no"),
        x_coordinate("x_coordinate"),
        y_coordinate("y_coordinate"),
        latitude("latitude"),
        longitude("longitude"),
        displayed_address_flg("displayed_address_flg"),
        representative_flg("representative_flg"),
        before_history_flg("before_history_flg"),
        after_history_flg("after_history_flg");
        
        private final String text;
        private _AddressGPS(final String text){
            this.text = text;
        }
        
        @Override
        public String get() {
            return this.text;
        }
        
    } 
}
