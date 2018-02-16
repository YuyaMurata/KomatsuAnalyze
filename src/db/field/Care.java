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

    //PrePrice
    public enum PrePrice implements Fields {
        TR_NO("TR_NO"),
        KISY("KISY"),
        TYP("TYP"),
        SYHK("SYHK"),
        KIBAN("KIBAN"),
        HT_DATE("HT_DATE"),
        DEP_CD("DEP_CD"),
        HSY_KIND("HSY_KIND"),
        KJ_KIND("KJ_KIND"),
        TNK_DATE("TNK_DATE"),
        PRICE("PRICE"),
        AKKRO_KBN("AKKRO_KBN"),
        KYK_ST("KYK_ST"),
        TR_NO2("TR_NO2"),
        KISY_TYP("KISY_TYP"),
        KISY_KIBAN("KISY_KIBAN"),
        HT_DATE2("HT_DATE2"),
        HT_YEAR("HT_YEAR"),
        PRICE2("PRICE2"),
        YEAR("YEAR"),
        MONTH("MONTH");

        private final String text;

        private PrePrice(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }
}
