/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db.field;

/**
 *
 * @author ZZ17390
 */
public class Care {
    //Care
    public enum _Care implements Fields {
        CREATE("CREATE"),
        TR_NO("TR_NO"),
        KISY("KISY"),
        TYP("TYP"),
        SYHK("SYHK"),
        KIBAN("KIBAN"),
        HT_DATE("HT_DATE"),
        DEP_CD("DEP_CD"),
        HSY_KIND("HSY_KIND"),
        GNKKJ_KIND("GNKKJ_KIND"),
        TNK_DATE("TNK_DATE"),
        PRICE("PRICE"),
        DAIRI_CD("DAIRI_CD"),
        CRM_NO("CRM_NO"),
        CRM_NO_AD("CRM_NO_AD"),
        SMR("SMR"),
        FC_NO("FC_NO"),
        TR_NO_TRM("TR_NO_TRM"),
        SY_TR("SY_TR"),
        KAISU("KAISU"),
        SMR2("SMR2");

        private final String text;

        private _Care(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }
}
