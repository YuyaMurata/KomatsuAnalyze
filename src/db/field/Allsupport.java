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
public class Allsupport {
    //Allsupport

    public enum _Allsupport implements Fields {
        KSYCD("KSYCD"),
        DB("DB"),
        KEIYAKU_NO("KEIYAKU_NO"),
        KKYKCD("KKYKCD"),
        KKYK_KANA("KKYK_KANA"),
        HAN_PCD("HAN_PCD"),
        TAN_CD("TAN_CD"),
        SIN_DATE("SIN_DATE"),
        KEI_DATE("KEI_DATE"),
        SYO_DATE("SYO_DATE"),
        KISY("KISY"),
        TYP("TYP"),
        SYHK("SYHK"),
        KIBAN("KIBAN"),
        NU_KBN("NU_KBN"),
        SYKN_KBN("SYKN_KBN"),
        NONYU("NONYU"),
        SEIZO("SEIZO"),
        HSY_ZEI("HSY_ZEI"),
        HT_BIK("HT_BIK"),
        PLAN("PLAN"),
        MK_ST_DATE("MK_ST_DATE"),
        MK_FIN_DATE("MK_FIN_DATE"),
        MK_KIKN("MK_KIKN"),
        MK_KIYK("MK_KIYK"),
        MK_HRKN("MK_HRKN"),
        MP_SY_UAGE("MP_SY_UAGE"),
        M_SY_ZEI("M_SY_ZEI"),
        M2_UAGE("M2_UAGE"),
        M2_ZEI("M2_ZEI"),
        M3_UAGE("M3_UAGE"),
        M3_ZEI("M3_ZEI"),
        M4_UAGE("M4_UAGE"),
        M4_ZEI("M4_ZEI"),
        M5_UAGE("M5_UAGE"),
        M5_ZEI("M5_ZEI"),
        M6_UAGE("M6_UAGE"),
        M6_ZEI("M6_ZEI"),
        M7_UAGE("M7_UAGE"),
        M7_ZEI("M7_ZEI"),
        M8_UAGE("M8_UAGE"),
        M8_ZEI("M8_ZEI"),
        M_MON_UAGE("M_MON_UAGE"),
        M_MON_ZEI("M_MON_ZEI"),
        M_AUTO_UAGE("M_AUTO_UAGE"),
        M_SHR_HO("M_SHR_HO"),
        MSEI_ST("MSEI_ST"),
        MSEI_KISU("MSEI_KISU"),
        MSEI_ZAN_KISU("MSEI_ZAN_KISU"),
        MENT_KEI_KN("MENT_KEI_KN"),
        RYK_SHR_HO("RYK_SHR_HO"),
        KEI_MTR("KEI_MTR"),
        HY_TIME("HY_TIME"),
        IN_KIKN("IN_KIKN"),
        TJ_KBN("TJ_KBN"),
        TJ_TNK("TJ_TNK"),
        TJ_KISU("TJ_KISU"),
        KIND_KBN("KIND_KBN"),
        KKYK_KJ("KKYK_KJ"),
        KKYK_ADD_KJ("KKYK_ADD_KJ"),
        KKYK_YBN("KKYK_YBN"),
        KKYK_TEL("KKYK_TEL"),
        TNT_KJ("TNT_KJ"),
        TNT_KANA("TNT_KANA"),
        JK_HNK_KBN("JK_HNK_KBN"),
        METE_BEF_KBN("METE_BEF_KBN"),
        TJKN("TJKN"),
        PARTS("PARTS"),
        YSI("YSI"),
        SYUCHO("SYUCHO"),
        CLINIC("CLINIC"),
        MENTE_KEI_TNK("MENTE_KEI_TNK"),
        PRE_SMR("PRE_SMR"),
        TKSY_SP_KBN("TKSY_SP_KBN"),
        TKSY_SP_NAME("TKSY_SP_NAME"),
        YOBI("YOBI"),
        AS_REG_DATE("AS_REG_DATE"),
        M_MNRY_DATE("M_MNRY_DATE"),
        M_YUKO("M_YUKO");

        private final String text;

        private _Allsupport(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }
}
