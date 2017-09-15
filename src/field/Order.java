/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package field;

/**
 *
 * @author ZZ17390
 */
public class Order {
    //Order
    public enum _Order implements Fields{
	KSYCD("KSYCD"),
	SBN("SBN"),
	SBN_KBN("SBN_KBN"),
	ODR_KBN("ODR_KBN"),
	ODDAY("ODDAY"),
	ODR_PNTCD("ODR_PNTCD"),
	ODR_PNTNM("ODR_PNTNM"),
	ODR_TNTCD("ODR_TNTCD"),
	ODR_TNTNM("ODR_TNTNM"),
	KKYKCD("KKYKCD"),
	KYKNM_1("KYKNM_1"),
	KYKNM_2("KYKNM_2"),
	KKYK_ZIP("KKYK_ZIP"),
	KKYK_PREF_NM("KKYK_PREF_NM"),
	KKYK_ADR_1("KKYK_ADR_1"),
	KKYK_ADR_2("KKYK_ADR_2"),
	KKYK_ADR_3("KKYK_ADR_3"),
	KKYK_TELNO("KKYK_TELNO"),
	KKYK_FAXNO("KKYK_FAXNO"),
	KKYK_HJN_KBN("KKYK_HJN_KBN"),
	IRIS_CMT("IRIS_CMT"),
	SKSK_KBN("SKSK_KBN"),
	KKYK_TYMNNO("KKYK_TYMNNO"),
	SKSK_KKYKCD("SKSK_KKYKCD"),
	SKSK_KYKNM_1("SKSK_KYKNM_1"),
	SKSK_KYKNM_2("SKSK_KYKNM_2"),
	SKSK_ZIP("SKSK_ZIP"),
	SKSK_PREF_NM("SKSK_PREF_NM"),
	SKSK_ADR_1("SKSK_ADR_1"),
	SKSK_ADR_2("SKSK_ADR_2"),
	SKSK_ADR_3("SKSK_ADR_3"),
	SKSK_KKYK_TELNO("SKSK_KKYK_TELNO"),
	SKSK_FAXNO("SKSK_FAXNO"),
	SKSK_HJN_KBN("SKSK_HJN_KBN"),
	SKSK_TRHK_KHI_KBN("SKSK_TRHK_KHI_KBN"),
	SKSK_TRSGN_KBN("SKSK_TRSGN_KBN"),
	SKSK_KEIE_STA_KBN("SKSK_KEIE_STA_KBN"),
	SKSK_ALLSYHN_YWKG("SKSK_ALLSYHN_YWKG"),
	SKSK_BSA_YWKG("SKSK_BSA_YWKG"),
	SKSK_ALS_TRY_UKZDK("SKSK_ALS_TRY_UKZDK"),
	SKSK_BSA_TRY_UKZDK("SKSK_BSA_TRY_UKZDK"),
	SKSK_ALLSYHN_TMSU("SKSK_ALLSYHN_TMSU"),
	SKSK_BSA_TMSU("SKSK_BSA_TMSU"),
	SKSK_S_SKKG("SKSK_S_SKKG"),
	SKSK_NEW_JKODAY("SKSK_NEW_JKODAY"),
	SKSK_NEW_JKCMT("SKSK_NEW_JKCMT"),
	SKSK_IRIS_CMT("SKSK_IRIS_CMT"),
	TKKK_TYFLG("TKKK_TYFLG"),
	SYOZ_KSN_HOHO_KBN("SYOZ_KSN_HOHO_KBN"),
	KISY("KISY"),
	TYP("TYP"),
	SYHK("SYHK"),
	KIBAN("KIBAN"),
	KISY_CMT("KISY_CMT"),
	SEHN_BNR_CD_A("SEHN_BNR_CD_A"),
	SEHN_BNR_CD_B("SEHN_BNR_CD_B"),
	SVC_MTR_KBN("SVC_MTR_KBN"),
	INP_SVC_MTR("INP_SVC_MTR"),
	SVC_MTR("SVC_MTR"),
	SVC_MTR_UPDAY("SVC_MTR_UPDAY"),
	KKYK_KNRNO("KKYK_KNRNO"),
	HY_KKYKCD("HY_KKYKCD"),
	HY_KYKNM_1("HY_KYKNM_1"),
	HY_KYKNM_2("HY_KYKNM_2"),
	NU_KBN("NU_KBN"),
	KIZKYOK_SSI_UMFLG("KIZKYOK_SSI_UMFLG"),
	KIZKYOK_ISDAY("KIZKYOK_ISDAY"),
	KIZKYOK_SNDAY("KIZKYOK_SNDAY"),
	ZZKSNO("ZZKSNO"),
	TJK_HYSYNO("TJK_HYSYNO"),
	AF_TJK_HYSYNO("AF_TJK_HYSYNO"),
	TJK_YKDAY("TJK_YKDAY"),
	SYAK_YKDAY("SYAK_YKDAY"),
	KYK_KNRNO("KYK_KNRNO"),
	KYK_SYBCD("KYK_SYBCD"),
	KYKNO("KYKNO"),
	KYK_SVC_KKU_TYFLG("KYK_SVC_KKU_TYFLG"),
	MNT_KEIK_DEL_FLG("MNT_KEIK_DEL_FLG"),
	MUKN_FRKFLG("MUKN_FRKFLG"),
	KYK_MNT_TGFLG("KYK_MNT_TGFLG"),
	BHN_SFSK_ZIP("BHN_SFSK_ZIP"),
	BHN_SFSK_ADR("BHN_SFSK_ADR"),
	BHN_SFSK_TEL("BHN_SFSK_TEL"),
	BHN_SFSK_FAX("BHN_SFSK_FAX"),
	BHN_SFSK_BSYNM("BHN_SFSK_BSYNM"),
	BHN_SFSK_TNTNM("BHN_SFSK_TNTNM"),
	DKNSHR_KBN("DKNSHR_KBN"),
	DUKJKN_IKHI("DUKJKN_IKHI"),
	DKNSHR_CMT("DKNSHR_CMT"),
	SGYO_KTICD("SGYO_KTICD"),
	SGKT_NM("SGKT_NM"),
	UAGE_KBN_1("UAGE_KBN_1"),
	UAGE_KBN_2("UAGE_KBN_2"),
	UAGE_KBN_3("UAGE_KBN_3"),
	SYAN_SYAG_KBN("SYAN_SYAG_KBN"),
	SGYO_BASY_KBN("SGYO_BASY_KBN"),
	SGBS_KISU("SGBS_KISU"),
	SGBS_CMT("SGBS_CMT"),
	GAIYO_1("GAIYO_1"),
	GAIYO_2("GAIYO_2"),
	KTISSN_NO("KTISSN_NO"),
	SBN_HKDAY("SBN_HKDAY"),
	SBN_STS("SBN_STS"),
	SBN_CLDAY("SBN_CLDAY"),
	SBN_CLOS_RSNCD("SBN_CLOS_RSNCD"),
	SBN_CLSE_BKDAY("SBN_CLSE_BKDAY"),
	SGYO_JISI_YTDAY("SGYO_JISI_YTDAY"),
	KNOKI("KNOKI"),
	SJSHO_HAKO_KBN("SJSHO_HAKO_KBN"),
	SJSHO_HKDAY("SJSHO_HKDAY"),
	SJSHO_LAST_HKDAY("SJSHO_LAST_HKDAY"),
	SGJSK_INP_UMFLG("SGJSK_INP_UMFLG"),
	JRKOS("JRKOS"),
	SIJI_RKI_KOS("SIJI_RKI_KOS"),
	SGYO_GT_KENS_STS("SGYO_GT_KENS_STS"),
	SGYO_KNRO_KNFLG("SGYO_KNRO_KNFLG"),
	ZGYO_JKRFLG("ZGYO_JKRFLG"),
	ZGYO_KRDAY("ZGYO_KRDAY"),
	KNRO_TRFLG("KNRO_TRFLG"),
	SGYO_KRDAY("SGYO_KRDAY"),
	SGYO_KNRO_KNDAY("SGYO_KNRO_KNDAY"),
	SGYO_KNRO_LOTNO("SGYO_KNRO_LOTNO"),
	SGYO_KRDAY_STS("SGYO_KRDAY_STS"),
	KKYK_KSIDAY("KKYK_KSIDAY"),
	GNKA_KNFLG("GNKA_KNFLG"),
	GNKKJ_KNDAY("GNKKJ_KNDAY"),
	GNKKJ_SNDAY("GNKKJ_SNDAY"),
	UAGE_KJFLG("UAGE_KJFLG"),
	URDAY("URDAY"),
	BIGIN_URDAY("BIGIN_URDAY"),
	SKSHO_HKFLG("SKSHO_HKFLG"),
	SKSHO_HKDAY("SKSHO_HKDAY"),
	NHSNO_HKFLG("NHSNO_HKFLG"),
	NHSNO_HKDAY("NHSNO_HKDAY"),
	MIT_INV_KISU("MIT_INV_KISU"),
	SIJI_KISU("SIJI_KISU"),
	DIHY_SGYO_MSINO("DIHY_SGYO_MSINO"),
	ANKNO("ANKNO"),
	MITNO("MITNO"),
	MITNO_MNO("MITNO_MNO"),
	SEISAN_MITNO("SEISAN_MITNO"),
	SEISAN_MITNO_MNO("SEISAN_MITNO_MNO"),
	LK_SBN("LK_SBN"),
	STBCD_1("STBCD_1"),
	STBCD_2("STBCD_2"),
	STBCD_3("STBCD_3"),
	STBCD_4("STBCD_4"),
	STBCD_5("STBCD_5"),
	BHN_TYKS_KBN("BHN_TYKS_KBN"),
	BHN_HASO_SYB("BHN_HASO_SYB"),
	HAT_MOTO_SBN("HAT_MOTO_SBN"),
	SHKDNP_OUT_YTDAY("SHKDNP_OUT_YTDAY"),
	SHKDNP_CMT("SHKDNP_CMT"),
	SITI_FRAT_PNTCD("SITI_FRAT_PNTCD"),
	HACYUZN_HATFLG("HACYUZN_HATFLG"),
	MTEHI_MKFLG("MTEHI_MKFLG"),
	HKYS_TKAI_KBN("HKYS_TKAI_KBN"),
	HKYS_TKHOHO_KBN("HKYS_TKHOHO_KBN"),
	SKS_SYRY_KBN("SKS_SYRY_KBN"),
	SKSIS_OUT_CMT("SKSIS_OUT_CMT"),
	SJSHO_SGDLPUMU("SJSHO_SGDLPUMU"),
	SGDTL_PTUMU("SGDTL_PTUMU"),
	TGBHN_PTUMU("TGBHN_PTUMU"),
	BIKO_1("BIKO_1"),
	BIKO_2("BIKO_2"),
	SBN_FILE("SBN_FILE"),
	SBN_WF_STS("SBN_WF_STS"),
	SBN_WF_SYB_KB("SBN_WF_SYB_KB"),
	CORE_NZFLG("CORE_NZFLG"),
	CORE_HNKYK_UMFLG("CORE_HNKYK_UMFLG"),
	H_IRFLG("H_IRFLG"),
	TMSHO_HSFLG("TMSHO_HSFLG"),
	KMT_SUAGE_JKOS("KMT_SUAGE_JKOS"),
	KMT_SUAGE_OKOS("KMT_SUAGE_OKOS"),
	KMT_SUAGE_GJRY("KMT_SUAGE_GJRY"),
	KMT_SUAGE_GTHYO("KMT_SUAGE_GTHYO"),
	KMT_SUAGE_BHHYO("KMT_SUAGE_BHHYO"),
	KMT_SUAGE_YHHYO("KMT_SUAGE_YHHYO"),
	KMT_SUAGE_ZRHYO("KMT_SUAGE_ZRHYO"),
	KMT_SUAGE_FTHYO("KMT_SUAGE_FTHYO"),
	KMT_SUAGE_GKKG("KMT_SUAGE_GKKG"),
	KGIFLG("KGIFLG"),
	HJUN_KKU_GKKG("HJUN_KKU_GKKG"),
	SYAN_HYJK_GKKG("SYAN_HYJK_GKKG"),
	GNKA_GKKG("GNKA_GKKG"),
	JKN_NBK_GKKG("JKN_NBK_GKKG"),
	TBNB_GKKG("TBNB_GKKG"),
	INV_KKU_GKKG("INV_KKU_GKKG"),
	KKIJ_GKR("KKIJ_GKR"),
	SVM_GKKG("SVM_GKKG"),
	SVM_GKR("SVM_GKR"),
	ARRI_GKKG("ARRI_GKKG"),
	ARRI_GKR("ARRI_GKR"),
	TBNB_GKR("TBNB_GKR"),
	TYK_GNKA_GKKG("TYK_GNKA_GKKG"),
	HKKG("HKKG"),
	SHZG("SHZG"),
	SZR("SZR"),
	ZNK_SKKG("ZNK_SKKG"),
	KZI_KBN("KZI_KBN"),
	SKKG("SKKG"),
	TSRY_KRK_SKKG("TSRY_KRK_SKKG"),
	SVCKR_SUM_SKBN("SVCKR_SUM_SKBN"),
	MNT_JSK_SUM_SKBN("MNT_JSK_SUM_SKBN"),
	COL_NBK_UMFLG("COL_NBK_UMFLG"),
	NEW_KKU_SUDAY("NEW_KKU_SUDAY"),
	BHN_PRMTK_TSFLG("BHN_PRMTK_TSFLG"),
	BHN_NBK_KBN("BHN_NBK_KBN"),
	TRK_USERID("TRK_USERID"),
	TRK_PGMID("TRK_PGMID"),
	INP_DAYT("INP_DAYT"),
	SIS_KS_USERID("SIS_KS_USERID"),
	SIS_KS_PGMID("SIS_KS_PGMID"),
	LAST_UPD_DAYT("LAST_UPD_DAYT"),
	TOKKIJ_NIY("TOKKIJ_NIY");

	private final String text;

	private _Order(final String text) {
		this.text = text;
	}

	public String get(){
		return this.text;
	}
    }

    //Parts
    public enum Parts implements Fields {
	KSYCD("KSYCD"),
	SBN("SBN"),
	BHN_MSINO("BHN_MSINO"),
	BHN_MSINO_MNO("BHN_MSINO_MNO"),
	MIDAY("MIDAY"),
	JOIN_GNO("JOIN_GNO"),
	TEGK_MSYO_KBN("TEGK_MSYO_KBN"),
	GAIT_B_KBN("GAIT_B_KBN"),
	HACYNO("HACYNO"),
	GTUIR_MSINO("GTUIR_MSINO"),
	HNBN("HNBN"),
	TEHI_HNBN("TEHI_HNBN"),
	BHN_NM("BHN_NM"),
	BKNR_KBN("BKNR_KBN"),
	BHN_KIND_KBN("BHN_KIND_KBN"),
	BHN_KBN("BHN_KBN"),
	BHN_MAKR_KBN("BHN_MAKR_KBN"),
	BHN_TKKK_TKUMU("BHN_TKKK_TKUMU"),
	BHN_TKKK_TMTNK("BHN_TKKK_TMTNK"),
	BHN_TKKK_KKR("BHN_TKKK_KKR"),
	HJUN_TNK("HJUN_TNK"),
	SKTNK("SKTNK"),
	CYHY_SKTNK("CYHY_SKTNK"),
	KKYK_STI_SKTNK("KKYK_STI_SKTNK"),
	TKY_NBR("TKY_NBR"),
	YTGK_TNK("YTGK_TNK"),
	DMBHN_KRTU("DMBHN_KRTU"),
	ODR_SU("ODR_SU"),
	ODR_SURY_TNI("ODR_SURY_TNI"),
	MTHSU("MTHSU"),
	CANSL_SU("CANSL_SU"),
	ZAIK_FATSU("ZAIK_FATSU"),
	SKKR_FATSU("SKKR_FATSU"),
	SHKDNP_SRSU("SHKDNP_SRSU"),
	SHK_KRSU("SHK_KRSU"),
	SYKA_KRSU("SYKA_KRSU"),
	HJUN_KKU("HJUN_KKU"),
	SYAN_HYJK("SYAN_HYJK"),
	JKN_NBKG("JKN_NBKG"),
	TBNB_KGK("TBNB_KGK"),
	YTI_GNKA_KGK("YTI_GNKA_KGK"),
	GNKA_KGK("GNKA_KGK"),
	CORE_HK_GKKG("CORE_HK_GKKG"),
	SKKG("SKKG"),
	SYAN_SKKG("SYAN_SKKG"),
	SHZG("SHZG"),
	SZR("SZR"),
	KKIJ_RIT("KKIJ_RIT"),
	ARRI_KGK("ARRI_KGK"),
	ARR("ARR"),
	CMPKK_UMFLG("CMPKK_UMFLG"),
	CMP_TKY_UMFLG("CMP_TKY_UMFLG"),
	CMP_NM("CMP_NM"),
	KMT_INV_BHN_SYTIKB("KMT_INV_BHN_SYTIKB"),
	KMT_INV_SYUFLG("KMT_INV_SYUFLG"),
	INCNTV_KTKG("INCNTV_KTKG"),
	INCNTV_NBK_YTTNK("INCNTV_NBK_YTTNK"),
	GTKFLG("GTKFLG"),
	LGC_FLG("LGC_FLG"),
	BHN_PRMTK_TSFLG("BHN_PRMTK_TSFLG"),
	BHN_NBK_KBN("BHN_NBK_KBN"),
	TRK_USERID("TRK_USERID"),
	TRK_PGMID("TRK_PGMID"),
	INP_DAYT("INP_DAYT"),
	SIS_KS_USERID("SIS_KS_USERID"),
	SIS_KS_PGMID("SIS_KS_PGMID"),
	LAST_UPD_DAYT("LAST_UPD_DAYT");

	private final String text;

	private Parts(final String text) {
		this.text = text;
	}

	public String get(){
		return this.text;
	}
    }
}
