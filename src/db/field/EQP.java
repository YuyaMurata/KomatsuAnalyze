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
public class EQP{

    //Syaryo
    public enum Syaryo implements Fields {
        KISY("KISY"),
        TYP("TYP"),
        SYHK("SYHK"),
        KIBAN("KIBAN"),
        MNF_DATE("MNF_DATE"),
        PLANT("PLANT"),
        SHIP_DATE("SHIP_DATE"),
        NEW_DELI_DATE("NEW_DELI_DATE"),
        DELI_CTG("DELI_CTG"),
        LTST_DELI_DATE("LTST_DELI_DATE"),
        SCRAP_DATE("SCRAP_DATE"),
        CNTRY_CD("CNTRY_CD"),
        CUST_CD("CUST_CD"),
        CUST_NM("CUST_NM"),
        DB("DB"),
        LTST_SMR_DATE("LTST_SMR_DATE"),
        LTST_SMR("LTST_SMR"),
        DLY_SMR("DLY_SMR"),
        VHMS_APP_CTG("VHMS_APP_CTG"),
        KMTRX_APP_CTG("KMTRX_APP_CTG"),
        B01_PLANT("B01_PLANT"),
        SUB_CD("SUB_CD");

        private final String text;

        private Syaryo(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

    //Keireki
    public enum Keireki implements Fields {
        KISY("KISY"),
        TYP("TYP"),
        SYHK("SYHK"),
        KIBAN("KIBAN"),
        HIS_DATE("HIS_DATE"),
        HIS_INFO_CD("HIS_INFO_CD"),
        HIS_SMR("HIS_SMR"),
        CNTRY("CNTRY"),
        CUST_CD("CUST_CD"),
        CUST_NM("CUST_NM"),
        DB("DB");

        private final String text;

        private Keireki(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

    //Spec
    public enum Spec implements Fields {
        KISY("KISY"),
        TYP("TYP"),
        SYHK("SYHK"),
        KIBAN("KIBAN"),
        SALES_UNIT_CD("SALES_UNIT_CD"),
        STD_OPT_CTG("STD_OPT_CTG"),
        QTY("QTY");

        private final String text;

        private Spec(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

    //EQP_Hanbai
    //Hanbai
    public enum Hanbai implements Fields {
        SUB_CD("SUB_CD"),
        KISY("KISY"),
        TYP("TYP"),
        SALES_UNIT_CD("SALES_UNIT_CD"),
        CTG_M_CD("CTG_M_CD"),
        CTG_S_CD("CTG_S_CD"),
        CTG_M_KNM("CTG_M_KNM"),
        CTG_M_NM("CTG_M_NM"),
        CTG_S_KNM("CTG_S_KNM"),
        CTG_S_NM("CTG_S_NM");

        private final String text;

        private Hanbai(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }
}
