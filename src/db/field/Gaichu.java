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
public class Gaichu {

    //Gaichu
    public enum _Gaichu implements Fields {
        KSYCD("KSYCD"),
        HACYNO("HACYNO"),
        AKAKURO_KBN("AKAKURO_KBN"),
        SBN("SBN"),
        HCDAY("HCDAY"),
        HACYU_PNTCD("HACYU_PNTCD"),
        HACYU_TNTCD("HACYU_TNTCD"),
        HACYU_TNTNM("HACYU_TNTNM"),
        KNOKI("KNOKI"),
        HACYU_NHDAY("HACYU_NHDAY"),
        HACYU_ISDAY("HACYU_ISDAY"),
        KISY("KISY"),
        TYP("TYP"),
        SYHK("SYHK"),
        KIBAN("KIBAN"),
        KISY_CMT("KISY_CMT"),
        SVC_MTR_KBN("SVC_MTR_KBN"),
        INP_SVC_MTR("INP_SVC_MTR"),
        SVC_MTR("SVC_MTR"),
        SVC_MTR_UPDAY("SVC_MTR_UPDAY"),
        KKYKCD("KKYKCD"),
        KYKNM_1("KYKNM_1"),
        KYKNM_2("KYKNM_2"),
        KKYK_HJN_KBN("KKYK_HJN_KBN"),
        IRIS_CMT("IRIS_CMT"),
        DKNSHR_KBN("DKNSHR_KBN"),
        DKNSHR_KBN_NM("DKNSHR_KBN_NM"),
        DKNSHR_CMT("DKNSHR_CMT"),
        GAIYO_1("GAIYO_1"),
        GAIYO_2("GAIYO_2"),
        GTSCD("GTSCD"),
        GTS_NM_1("GTS_NM_1"),
        GTS_NM_2("GTS_NM_2"),
        GTS_ZIP("GTS_ZIP"),
        GTS_ADR_PREF("GTS_ADR_PREF"),
        GTS_ADR_1("GTS_ADR_1"),
        GTS_ADR_2("GTS_ADR_2"),
        GTS_ADR_3("GTS_ADR_3"),
        GTS_TELNO("GTS_TELNO"),
        GTS_FAXNO("GTS_FAXNO"),
        SZR("SZR"),
        SGBS_CMT("SGBS_CMT"),
        HACYU_STS("HACYU_STS"),
        HACYUS_HAKO_STS("HACYUS_HAKO_STS"),
        KRDN_HACYNO("KRDN_HACYNO"),
        AKDN_HACYNO("AKDN_HACYNO"),
        YTGK_GKKG("YTGK_GKKG"),
        KTKG_GKKG("KTKG_GKKG"),
        ODKG_SUM("ODKG_SUM"),
        HACYU_SHZG_GKKG("HACYU_SHZG_GKKG"),
        UIKG_SUM("UIKG_SUM"),
        UIR_SHZG_SUM("UIR_SHZG_SUM"),
        HJN_KBN("HJN_KBN"),
        KKYK_TELNO("KKYK_TELNO"),
        KKYK_FAXNO("KKYK_FAXNO"),
        PNTNM("PNTNM"),
        PNT_ZIP("PNT_ZIP"),
        PREF_NM("PREF_NM"),
        PNT_ADR_1("PNT_ADR_1"),
        PNT_ADR_2("PNT_ADR_2"),
        PNT_ADR_3("PNT_ADR_3"),
        PNT_TELNO("PNT_TELNO"),
        PNT_FAXNO("PNT_FAXNO"),
        STN_BMP_FILE("STN_BMP_FILE"),
        PAY_JKN("PAY_JKN"),
        TRK_USERID("TRK_USERID"),
        TRK_PGMID("TRK_PGMID"),
        INP_DAYT("INP_DAYT"),
        SIS_KS_USERID("SIS_KS_USERID"),
        SIS_KS_PGMID("SIS_KS_PGMID"),
        LAST_UPD_DAYT("LAST_UPD_DAYT"),
        HACYU_CMT("HACYU_CMT"),
        CHK_LIST_FLG("CHK_LIST_FLG");

        private final String text;

        private _Gaichu(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

    //Work
    public enum Work implements Fields {
        KSYCD("KSYCD"),
        SGYO_NKA_JSK_NO("SGYO_NKA_JSK_NO"),
        GNO("GNO"),
        AKAKURO_KBN("AKAKURO_KBN"),
        HACYNO("HACYNO"),
        SGYO_MSINO("SGYO_MSINO"),
        GTUIR_MSINO("GTUIR_MSINO"),
        SGYOCD("SGYOCD"),
        HNBN_FTKCD("HNBN_FTKCD"),
        SIR_SGYO_HNM("SIR_SGYO_HNM"),
        ODR_PNTCD("ODR_PNTCD"),
        SISCD("SISCD"),
        SGBS_NM("SGBS_NM"),
        KNOKI("KNOKI"),
        KZI_KBN("KZI_KBN"),
        YTI_COMIT_FLG("YTI_COMIT_FLG"),
        HACYU_NHDAY("HACYU_NHDAY"),
        HACYU_KOS("HACYU_KOS"),
        YTGK("YTGK"),
        HACYU_KGK("HACYU_KGK"),
        UISU("UISU"),
        HJUN_TNK("HJUN_TNK"),
        ZNK_UIKG("ZNK_UIKG"),
        SHZG("SHZG"),
        SIR_KJZM_FLG("SIR_KJZM_FLG"),
        SIR_KEDAY("SIR_KEDAY"),
        ISDAY_MS("ISDAY_MS"),
        TRK_USERID("TRK_USERID"),
        TRK_PGMID("TRK_PGMID"),
        INP_DAYT("INP_DAYT"),
        SIS_KS_USERID("SIS_KS_USERID"),
        SIS_KS_PGMID("SIS_KS_PGMID"),
        LAST_UPD_DAYT("LAST_UPD_DAYT");

        private final String text;

        private Work(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

    //Work_Info
    public enum Work_Info implements Fields {
        KSYCD("KSYCD"),
        HACYNO("HACYNO"),
        POHT_MSINO("POHT_MSINO"),
        SBN("SBN"),
        SGYO_MSINO("SGYO_MSINO"),
        SGYOCD("SGYOCD"),
        HACYU_SU("HACYU_SU"),
        HACYU_KOS("HACYU_KOS"),
        YTGK("YTGK"),
        HACYU_KGK("HACYU_KGK"),
        UIKG_ZNKG("UIKG_ZNKG"),
        UIKG_ZKKG("UIKG_ZKKG"),
        SHZG("SHZG"),
        KZI_KBN("KZI_KBN"),
        YKFLG("YKFLG"),
        TRK_USERID("TRK_USERID"),
        TRK_PGMID("TRK_PGMID"),
        INP_DAYT("INP_DAYT"),
        SIS_KS_USERID("SIS_KS_USERID"),
        SIS_KS_PGMID("SIS_KS_PGMID"),
        LAST_UPD_DAYT("LAST_UPD_DAYT");

        private final String text;

        private Work_Info(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }
}
