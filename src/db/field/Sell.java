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
public class Sell {

    //Sell
    public enum _Sell implements Fields {
        KSYCD("KSYCD"),
        HT_TR_NO("HT_TR_NO"),
        CO_URI_SEQ("CO_URI_SEQ"),
        CO_URI_ACT_KBN("CO_URI_ACT_KBN"),
        MKM_ACT_KBN("MKM_ACT_KBN"),
        HT_AK_KBN("HT_AK_KBN"),
        SP_MTO_SK_KBN("SP_MTO_SK_KBN"),
        HT_URI_NO("HT_URI_NO"),
        HT_TR_VER("HT_TR_VER"),
        HT_TR_STS("HT_TR_STS"),
        HT_AN_TR_S_KBN("HT_AN_TR_S_KBN"),
        TSO_CO_DAY("TSO_CO_DAY"),
        TSO_URI_DAY("TSO_URI_DAY"),
        CO_URI_PRO_DAY("CO_URI_PRO_DAY"),
        CO_URI_PRO_YM("CO_URI_PRO_YM"),
        CO_DAY("CO_DAY"),
        NOU_YTI_DAY("NOU_YTI_DAY"),
        URI_DAY("URI_DAY"),
        CMS_UM_FLG("CMS_UM_FLG"),
        HB_KR_KBN("HB_KR_KBN"),
        NNBS_NM("NNBS_NM"),
        KYS_KKYK_KBN("KYS_KKYK_KBN"),
        KYSCD("KYSCD"),
        KYS_NM_1("KYS_NM_1"),
        KYS_NM_2("KYS_NM_2"),
        KYS_HNSCD("KYS_HNSCD"),
        KYS_TRSGN_KBN("KYS_TRSGN_KBN"),
        KYS_GYSCD("KYS_GYSCD"),
        NOU_KKYK_KBN("NOU_KKYK_KBN"),
        NNSCD("NNSCD"),
        NNSK_NM_1("NNSK_NM_1"),
        NNSK_NM_2("NNSK_NM_2"),
        NOU_HNSCD("NOU_HNSCD"),
        NOU_TRSGN_KBN("NOU_TRSGN_KBN"),
        NOU_GYSCD("NOU_GYSCD"),
        NOU_USSB_SGO("NOU_USSB_SGO"),
        NOU_SO_SKN_ZNGK("NOU_SO_SKN_ZNGK"),
        HT_AN_TNT_PNTCD("HT_AN_TNT_PNTCD"),
        HT_AN_TNT_PNTNM("HT_AN_TNT_PNTNM"),
        HT_AN_TNTCD("HT_AN_TNTCD"),
        HT_AN_TNTNM("HT_AN_TNTNM"),
        SPSK_PNTCD("SPSK_PNTCD"),
        SPSK_PNTNM("SPSK_PNTNM"),
        SPSK_TNTCD("SPSK_TNTCD"),
        SPSK_TNTNM("SPSK_TNTNM"),
        SP_CMT("SP_CMT"),
        NU_KBN("NU_KBN"),
        TH_KBN("TH_KBN"),
        SHNKBN("SHNKBN"),
        SEIHIN_NM("SEIHIN_NM"),
        KISY("KISY"),
        TYP("TYP"),
        SYHK("SYHK"),
        RECS_SY_PTCD("RECS_SY_PTCD"),
        KIBAN("KIBAN"),
        SEHN_BNR_CD_A("SEHN_BNR_CD_A"),
        SEHN_BNR_CD_B("SEHN_BNR_CD_B"),
        RP_OUT_MAKR_NM("RP_OUT_MAKR_NM"),
        RP_OUT_KISY_NM("RP_OUT_KISY_NM"),
        SRO_KRK_KBN("SRO_KRK_KBN"),
        KTO_KBN("KTO_KBN"),
        SEKN_KBN("SEKN_KBN"),
        SVC_MTR("SVC_MTR"),
        MAKE_YM("MAKE_YM"),
        KEIKA_YM("KEIKA_YM"),
        NOU_TIT_KKU("NOU_TIT_KKU"),
        UD_HS_HSO_KBN("UD_HS_HSO_KBN"),
        HS_RANK("HS_RANK"),
        KIK_SIYMKTCD("KIK_SIYMKTCD"),
        JYO_KTI_KBN("JYO_KTI_KBN"),
        KNDK_KBN("KNDK_KBN"),
        SHIN_RSNCD_1("SHIN_RSNCD_1"),
        SHIN_RSNCD_2("SHIN_RSNCD_2"),
        SYK_STK_KBN("SYK_STK_KBN"),
        HT_DAISU("HT_DAISU"),
        HT_HSSI_KBN("HT_HSSI_KBN"),
        SD_UM_FLG("SD_UM_FLG"),
        SD_KTR_KISY_1("SD_KTR_KISY_1"),
        SD_KAI_HT_DAISU_1("SD_KAI_HT_DAISU_1"),
        MAKR_KBN_1("MAKR_KBN_1"),
        SEHN_BNR_CD_B_1("SEHN_BNR_CD_B_1"),
        SD_KTR_KISY_2("SD_KTR_KISY_2"),
        SD_KAI_HT_DAISU_2("SD_KAI_HT_DAISU_2"),
        MAKR_KBN_2("MAKR_KBN_2"),
        SEHN_BNR_CD_B_2("SEHN_BNR_CD_B_2"),
        SD_KTR_KISY_3("SD_KTR_KISY_3"),
        SD_KAI_HT_DAISU_3("SD_KAI_HT_DAISU_3"),
        MAKR_KBN_3("MAKR_KBN_3"),
        SEHN_BNR_CD_B_3("SEHN_BNR_CD_B_3"),
        SD_KAIHT_DAISU_SUM("SD_KAIHT_DAISU_SUM"),
        SD_KKU_SUM("SD_KKU_SUM"),
        HSSIJI_UKE_KKU_SUM("HSSIJI_UKE_KKU_SUM"),
        TOKU_HSO_UM_FLG("TOKU_HSO_UM_FLG"),
        TOKU_HSO_JI_UM_FLG("TOKU_HSO_JI_UM_FLG"),
        KM_HOSY_KBN("KM_HOSY_KBN"),
        KM_HSO_CMT("KM_HSO_CMT"),
        KM_YM("KM_YM"),
        KM_KN("KM_KN"),
        KM_TIT_KKU("KM_TIT_KKU"),
        KM_TIT_RA("KM_TIT_RA"),
        SVC_HOSY_UMFLG("SVC_HOSY_UMFLG"),
        SVC_HSO_CMT("SVC_HSO_CMT"),
        DSV_HSO_CMT("DSV_HSO_CMT"),
        ALSPT_PLN_NM("ALSPT_PLN_NM"),
        HSSO("HSSO"),
        MNT_RYO("MNT_RYO"),
        KGO_ST_KBN("KGO_ST_KBN"),
        TJ_HAN_TSR_UM_FLG("TJ_HAN_TSR_UM_FLG"),
        TA_HAN_TSR_UM_FLG("TA_HAN_TSR_UM_FLG"),
        HT_IN_CD_1("HT_IN_CD_1"),
        INCNTV_KGK_1("INCNTV_KGK_1"),
        HT_IN_CD_2("HT_IN_CD_2"),
        INCNTV_KGK_2("INCNTV_KGK_2"),
        HT_IN_CD_3("HT_IN_CD_3"),
        INCNTV_KGK_3("INCNTV_KGK_3"),
        KGO_KISY_1("KGO_KISY_1"),
        JISSHITSU_KKU_A_1("JISSHITSU_KKU_A_1"),
        KGO_KISY_2("KGO_KISY_2"),
        JISSHITSU_KKU_A_2("JISSHITSU_KKU_A_2"),
        KGO_KISY_3("KGO_KISY_3"),
        JISSHITSU_KKU_A_3("JISSHITSU_KKU_A_3"),
        DBD_CD_1("DBD_CD_1"),
        DBD_CD_2("DBD_CD_2"),
        DBD_CD_3("DBD_CD_3"),
        DBD_KN_3("DBD_KN_3"),
        DBD_CD_4("DBD_CD_4"),
        DBD_KN_4("DBD_KN_4"),
        HAN_TSR_SOU_KN_SUM("HAN_TSR_SOU_KN_SUM"),
        SD_SOU_KN_SUM("SD_SOU_KN_SUM"),
        TA_SOU_KN_SUM("TA_SOU_KN_SUM"),
        EY_PAY_HAK_KBN("EY_PAY_HAK_KBN"),
        EY_END_H_YM("EY_END_H_YM"),
        EY_PAY_KKN("EY_PAY_KKN"),
        HM_URI_KN("HM_URI_KN"),
        HM_URI_KN_SZ("HM_URI_KN_SZ"),
        KP_TSR("KP_TSR"),
        UKKN_KJ_GK("UKKN_KJ_GK"),
        SD_SASN_SUM("SD_SASN_SUM"),
        RL_URI_KN("RL_URI_KN"),
        ATT_JUN_HAN_SUM("ATT_JUN_HAN_SUM"),
        ATT_NAI_MT_HAN_SUM("ATT_NAI_MT_HAN_SUM"),
        ATT_NAI_TG_HAN_SUM("ATT_NAI_TG_HAN_SUM"),
        ATT_NAI_GM_HAN_SUM("ATT_NAI_GM_HAN_SUM"),
        ATT_GAI_HAN_SUM("ATT_GAI_HAN_SUM"),
        SGT_HAN_KKU_SUM("SGT_HAN_KKU_SUM"),
        STD_SY_KKU("STD_SY_KKU"),
        DBKNR_GNKA("DBKNR_GNKA"),
        KTR_RS_KN_SUM("KTR_RS_KN_SUM"),
        ATT_JUN_PO_SUM("ATT_JUN_PO_SUM"),
        ATT_NAI_MT_PO_SUM("ATT_NAI_MT_PO_SUM"),
        ATT_NAI_TG_PO_SUM("ATT_NAI_TG_PO_SUM"),
        ATT_NAI_GM_PO_SUM("ATT_NAI_GM_PO_SUM"),
        ATT_GAI_PO_SUM("ATT_GAI_PO_SUM"),
        SGT_PO_KKU_SUM("SGT_PO_KKU_SUM"),
        KNR_GK_SUM("KNR_GK_SUM"),
        SD_KAI_USH_YTI_SUM("SD_KAI_USH_YTI_SUM"),
        SD_KAI_BNK_YTI_SUM("SD_KAI_BNK_YTI_SUM"),
        HKR_USH_HKA_YTISUM("HKR_USH_HKA_YTISUM"),
        KKS_HI_MKR_SUM("KKS_HI_MKR_SUM"),
        KNR_ARRI("KNR_ARRI"),
        KNR_ARRI_RA("KNR_ARRI_RA"),
        INCNTV_KGK_SUM("INCNTV_KGK_SUM"),
        HT_KN_SUM("HT_KN_SUM"),
        HT_KN_GSUM("HT_KN_GSUM"),
        HT_KNR_ARRI("HT_KNR_ARRI"),
        HT_KNR_ARRI_RA("HT_KNR_ARRI_RA"),
        HAN_TSR_KN_SUM("HAN_TSR_KN_SUM"),
        FUHI_NUSH_SUM("FUHI_NUSH_SUM"),
        FUHI_NOU_SHI_SUM("FUHI_NOU_SHI_SUM"),
        FUHI_TA_SUM("FUHI_TA_SUM"),
        FUHI_SUM("FUHI_SUM"),
        HHI_SUM("HHI_SUM"),
        KNR_GKI_ARRI("KNR_GKI_ARRI"),
        KNR_GKI_ARRI_RA("KNR_GKI_ARRI_RA"),
        DB_HBIK("DB_HBIK"),
        DB_HAN_KKU_IZI_RA("DB_HAN_KKU_IZI_RA"),
        DB_HAN_KKU_IZI_SAI("DB_HAN_KKU_IZI_SAI"),
        HT_BK("HT_BK"),
        ATT_JUN_BK_SUM("ATT_JUN_BK_SUM"),
        ATT_JUN_MT_BK_SUM("ATT_JUN_MT_BK_SUM"),
        ATT_JUN_TBK_BK_SUM("ATT_JUN_TBK_BK_SUM"),
        ATT_JUN_GK_BK_SUM("ATT_JUN_GK_BK_SUM"),
        ATT_GAI_GEN_BK_SUM("ATT_GAI_GEN_BK_SUM"),
        ATT_BK_TR_SUM("ATT_BK_TR_SUM"),
        SD_KAI_USH_TR_SUM("SD_KAI_USH_TR_SUM"),
        SD_KAI_BNK_TR_SUM("SD_KAI_BNK_TR_SUM"),
        HKR_USH_HKA_TR_SUM("HKR_USH_HKA_TR_SUM"),
        KKS_HI_TR_SUM("KKS_HI_TR_SUM"),
        ZM_GK_SUM("ZM_GK_SUM"),
        ZM_ARRI("ZM_ARRI"),
        ZM_ARRI_RA("ZM_ARRI_RA"),
        HT_ZM_ARRI("HT_ZM_ARRI"),
        HT_ZM_ARRI_RA("HT_ZM_ARRI_RA"),
        ZM_GKI_ARRI("ZM_GKI_ARRI"),
        ZM_GKI_ARRI_RA("ZM_GKI_ARRI_RA"),
        KP_KJN_KRI_KN("KP_KJN_KRI_KN"),
        KP_KJN_KRI_RA("KP_KJN_KRI_RA"),
        KP_UKTR_KRI_RA("KP_UKTR_KRI_RA"),
        KP_GANKIN("KP_GANKIN"),
        KP_NBTSU("KP_NBTSU"),
        KP_NBKK("KP_NBKK"),
        REKTNK("REKTNK"),
        DB_KJN_KTKK("DB_KJN_KTKK"),
        KYSH_COC_KBN("KYSH_COC_KBN"),
        RTI_HSO_UM_FLG("RTI_HSO_UM_FLG"),
        KE_MK("KE_MK"),
        AN_IS_RSN_KBN("AN_IS_RSN_KBN"),
        SP_PNTCD("SP_PNTCD"),
        SP_PNTNM("SP_PNTNM"),
        SP_TNTCD("SP_TNTCD"),
        SP_TNTNM("SP_TNTNM"),
        SP_HT_DAISU("SP_HT_DAISU"),
        SP_HM_URI_KN("SP_HM_URI_KN"),
        SP_RL_URI_KN("SP_RL_URI_KN"),
        SP_URI_KNR_ARRI("SP_URI_KNR_ARRI"),
        SP_URI_ZM_ARRI("SP_URI_ZM_ARRI"),
        CO_URI_LA_FLG("CO_URI_LA_FLG"),
        LGC_FLG("LGC_FLG"),
        TRK_USERID("TRK_USERID"),
        TRK_PGMID("TRK_PGMID"),
        INP_DAYT("INP_DAYT"),
        SIS_KS_USERID("SIS_KS_USERID"),
        SIS_KS_PGMID("SIS_KS_PGMID"),
        LAST_UPD_DAYT("LAST_UPD_DAYT");

        private final String text;

        private _Sell(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

    //ATTSpec
    public enum ATTSpec implements Fields {
        KSYCD("KSYCD"),
        HT_TR_NO("HT_TR_NO"),
        HT_TR_VER("HT_TR_VER"),
        HAN_ATT_SY_SEQ("HAN_ATT_SY_SEQ"),
        THI_KBN("THI_KBN"),
        ATT_CT_KBN("ATT_CT_KBN"),
        GEN_KUMI_UM_FLG("GEN_KUMI_UM_FLG"),
        SY_PTN_OP_ATT_FLG("SY_PTN_OP_ATT_FLG"),
        SGT_CD("SGT_CD"),
        SGT_NM("SGT_NM"),
        ATT_SU("ATT_SU"),
        ATT_KJK("ATT_KJK"),
        ATT_KRA("ATT_KRA"),
        GEN_HAN_KRA("GEN_HAN_KRA"),
        SGT_HAN_KKU("SGT_HAN_KKU"),
        SGT_PO_KKU("SGT_PO_KKU"),
        SISCD("SISCD"),
        SIS_NM_1("SIS_NM_1"),
        SIS_NM_2("SIS_NM_2"),
        SGYO_IRSK_PNTCD("SGYO_IRSK_PNTCD"),
        SGYO_IRSK_PNTNM("SGYO_IRSK_PNTNM"),
        BSA_MT_NO("BSA_MT_NO"),
        BSA_MT_NO_MNO("BSA_MT_NO_MNO"),
        SBN("SBN"),
        KISY("KISY"),
        TYP("TYP"),
        SYHK("SYHK"),
        SYT_ZK_KNR_KIBAN("SYT_ZK_KNR_KIBAN"),
        LGC_FLG("LGC_FLG"),
        TRK_USERID("TRK_USERID"),
        TRK_PGMID("TRK_PGMID"),
        INP_DAYT("INP_DAYT"),
        SIS_KS_USERID("SIS_KS_USERID"),
        SIS_KS_PGMID("SIS_KS_PGMID"),
        LAST_UPD_DAYT("LAST_UPD_DAYT");

        private final String text;

        private ATTSpec(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

    //Spec
    public enum Spec implements Fields {
        KSYCD("KSYCD"),
        HT_TR_NO("HT_TR_NO"),
        HT_TR_VER("HT_TR_VER"),
        NU_KBN("NU_KBN"),
        TH_KBN("TH_KBN"),
        KISY("KISY"),
        TYP("TYP"),
        SYHK("SYHK"),
        RECS_SY_PTCD("RECS_SY_PTCD"),
        KIBAN("KIBAN"),
        SHNKBN("SHNKBN"),
        MAKR_NM("MAKR_NM"),
        HAN_SY_CMT("HAN_SY_CMT"),
        RP_OUT_MAKR_NM("RP_OUT_MAKR_NM"),
        RP_OUT_KISY_NM("RP_OUT_KISY_NM"),
        SRO_KRK_KBN("SRO_KRK_KBN"),
        KTO_KBN("KTO_KBN"),
        SEKN_KBN("SEKN_KBN"),
        SVC_MTR("SVC_MTR"),
        MAKE_YM("MAKE_YM"),
        KEIKA_YM("KEIKA_YM"),
        NOU_TIT_KKU("NOU_TIT_KKU"),
        SD_KAI_USH_YTI("SD_KAI_USH_YTI"),
        SD_KAI_BNKHI_YTI("SD_KAI_BNKHI_YTI"),
        SEHN_BNR_CD_A("SEHN_BNR_CD_A"),
        SEHN_BNR_CD_B("SEHN_BNR_CD_B"),
        HT_DAISU("HT_DAISU"),
        UD_HS_HSO_KBN("UD_HS_HSO_KBN"),
        HS_RANK("HS_RANK"),
        SYK_STK_KBN("SYK_STK_KBN"),
        HM_URI_KN("HM_URI_KN"),
        DBKNR_GNKA("DBKNR_GNKA"),
        SZR("SZR"),
        HM_URI_KN_SZ("HM_URI_KN_SZ"),
        HM_URDK_SZAJST("HM_URDK_SZAJST"),
        KZI_KBN("KZI_KBN"),
        KP_TSR("KP_TSR"),
        KP_SOI_KRI_RA("KP_SOI_KRI_RA"),
        KP_KJN_KRI_RA("KP_KJN_KRI_RA"),
        KP_KJN_KRI_KN("KP_KJN_KRI_KN"),
        UKKN_KJ_GK("UKKN_KJ_GK"),
        RL_URI_KN("RL_URI_KN"),
        KRKS_CHK_ERR_CD("KRKS_CHK_ERR_CD"),
        HNS_NY_YTI_ZKBS_CD("HNS_NY_YTI_ZKBS_CD"),
        HNS_NY_YTI_ZKBS_NM("HNS_NY_YTI_ZKBS_NM"),
        HNS_NY_YTI_DAY("HNS_NY_YTI_DAY"),
        LGC_FLG("LGC_FLG"),
        TRK_USERID("TRK_USERID"),
        TRK_PGMID("TRK_PGMID"),
        INP_DAYT("INP_DAYT"),
        SIS_KS_USERID("SIS_KS_USERID"),
        SIS_KS_PGMID("SIS_KS_PGMID"),
        LAST_UPD_DAYT("LAST_UPD_DAYT");

        private final String text;

        private Spec(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

    //Trade
    public enum Trade implements Fields {
        KSYCD("KSYCD"),
        HT_TR_NO("HT_TR_NO"),
        HT_TR_VER("HT_TR_VER"),
        HT_TR_STS("HT_TR_STS"),
        HT_TR_ARC_KBN("HT_TR_ARC_KBN"),
        HT_AN_TR_S_KBN("HT_AN_TR_S_KBN"),
        URI_ST_KBN("URI_ST_KBN"),
        HT_TR_SER_KBN("HT_TR_SER_KBN"),
        TSO_NOU_YTI_DAY("TSO_NOU_YTI_DAY"),
        NOU_YTI_DAY("NOU_YTI_DAY"),
        NNBS_NM("NNBS_NM"),
        TSO_URI_DAY("TSO_URI_DAY"),
        HB_KR_KBN("HB_KR_KBN"),
        KIK_SIYMKTCD("KIK_SIYMKTCD"),
        JYO_KTI_KBN("JYO_KTI_KBN"),
        KNDK_KBN("KNDK_KBN"),
        SHIN_RSNCD_1("SHIN_RSNCD_1"),
        SHIN_RSNCD_2("SHIN_RSNCD_2"),
        LGC_FLG("LGC_FLG"),
        TRK_USERID("TRK_USERID"),
        TRK_PGMID("TRK_PGMID"),
        INP_DAYT("INP_DAYT"),
        SIS_KS_USERID("SIS_KS_USERID"),
        SIS_KS_PGMID("SIS_KS_PGMID"),
        LAST_UPD_DAYT("LAST_UPD_DAYT");

        private final String text;

        private Trade(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

    //Sell_Used
    public enum Used implements Fields {
        URI_STAFF("URI_STAFF"),
        KNR_JUN_KBN("KNR_JUN_KBN"),
        HANBAI_SYU("HANBAI_SYU"),
        DEN_HAK_DAY("DEN_HAK_DAY"),
        HAN_CRE_DAY("HAN_CRE_DAY"),
        CO_KT_DAY("CO_KT_DAY"),
        URI_DAY("URI_DAY"),
        KISY("KISY"),
        TYPE("TYPE"),
        S_TYPE("S_TYPE"),
        KIBAN("KIBAN"),
        DAISU("DAISU"),
        HT_ATT_KBN("HT_ATT_KBN"),
        CO_CODE("CO_CODE"),
        CO_CUST("CO_CUST"),
        NO_CUST("NO_CUST"),
        CO_KKU("CO_KKU"),
        SZ("SZ"),
        ST_ANS_DAY("ST_ANS_DAY"),
        ST_KKU("ST_KKU"),
        PO_SUM("PO_SUM"),
        USH_SUM("USH_SUM"),
        POS_HI_SUM("POS_HI_SUM"),
        PO_ID("PO_ID"),
        URI_TR_UN_SUM("URI_TR_UN_SUM"),
        URI_SH_SUM("URI_SH_SUM"),
        PRO_GK("PRO_GK"),
        SH_COM("SH_COM"),
        NO_UN("NO_UN"),
        NO_SHIK("NO_SHIK"),
        SHIP_SUM("SHIP_SUM"),
        SHIP_UN_SUM("SHIP_UN_SUM"),
        HAN_CH_SUM("HAN_CH_SUM"),
        SH_KRI("SH_KRI"),
        SP_UNS_SUM("SP_UNS_SUM"),
        CNT_HR_SUM("CNT_HR_SUM"),
        ARRI("ARRI"),
        ARRI_RA("ARRI_RA"),
        NK_YT_DAY("NK_YT_DAY"),
        NY_KBN("NY_KBN"),
        NNSK("NNSK"),
        PO_HR_MTR("PO_HR_MTR"),
        PO_MEMO("PO_MEMO"),
        URI_CTRY("URI_CTRY"),
        URI_AREA("URI_AREA"),
        URI_USR_KBN("URI_USR_KBN"),
        NO_CTRY("NO_CTRY"),
        NO_AREA("NO_AREA"),
        NO_USR_KBN("NO_USR_KBN"),
        MAKER("MAKER"),
        PRODUCT("PRODUCT"),
        PRODUCT_KBN("PRODUCT_KBN"),
        CLASS("CLASS"),
        SIZE("SIZE"),
        HK_PLACE("HK_PLACE"),
        ZK_KIKN("ZK_KIKN"),
        PO_STAFF("PO_STAFF"),
        PO_TO("PO_TO"),
        PO_USR_KBN("PO_USR_KBN"),
        PO_SUM_DAY("PO_SUM_DAY"),
        GV_HANBAI("GV_HANBAI"),
        PROJECT("PROJECT"),
        PRODUCT_CTRY("PRODUCT_CTRY"),
        PO_TR_ST("PO_TR_ST"),
        URI_TR_ST("URI_TR_ST"),
        J_ID("J_ID"),
        ID_NO("ID_NO");

        private final String text;

        private Used(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

    //Sell_Old
    public enum Old implements Fields {
        KISY("KISY"),
        TYP("TYP"),
        SYHK("SYHK"),
        KIBAN("KIBAN"),
        URI_DAY("URI_DAY"),
        URI_KNGK("URI_KNGK"),
        KSYCD("KSYCD"),
        KSYNM("KSYNM");

        private final String text;

        private Old(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }
}
