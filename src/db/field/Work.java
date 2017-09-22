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
public class Work {
    //Work
    public enum _Work implements Fields {
	KSYCD("KSYCD"),
	SBN("SBN"),
	SGYO_MSINO("SGYO_MSINO"),
	SGYO_JSDAY("SGYO_JSDAY"),
	SGYO_TNTCD("SGYO_TNTCD"),
	SGYO_JSK_MNO("SGYO_JSK_MNO"),
	AKAKURO_KBN("AKAKURO_KBN"),
	SGYOCD("SGYOCD"),
	SGYO_NM("SGYO_NM"),
	JKOS("JKOS"),
	SGYO_TNTNM("SGYO_TNTNM"),
	SGTNT_SHOZK_PNTCD("SGTNT_SHOZK_PNTCD"),
	KOTN_GKTNK("KOTN_GKTNK"),
	KOTN_GNKA("KOTN_GNKA"),
	SAP_SSNFLG("SAP_SSNFLG"),
	SGYOD_SUM_SKBN("SGYOD_SUM_SKBN"),
	TRK_USERID("TRK_USERID"),
	TRK_PGMID("TRK_PGMID"),
	INP_DAYT("INP_DAYT"),
	SIS_KS_USERID("SIS_KS_USERID"),
	SIS_KS_PGMID("SIS_KS_PGMID"),
	LAST_UPD_DAYT("LAST_UPD_DAYT");

	private final String text;

	private _Work(final String text) {
		this.text = text;
	}

	public String get(){
		return this.text;
	}
    }

    //Work_DET
    public enum Detail implements Fields {
	KSYCD("KSYCD"),
	SBN("SBN"),
	SGYO_MSINO("SGYO_MSINO"),
	SGYSSI_CD_MNO("SGYSSI_CD_MNO"),
	SGYSSI_CD("SGYSSI_CD"),
	SGDTL_NM("SGDTL_NM"),
	KISY("KISY"),
	TYP("TYP"),
	MST_HJUN_KOS("MST_HJUN_KOS"),
	HJUN_KOS("HJUN_KOS"),
	MST_SGYO_TNI_SU("MST_SGYO_TNI_SU"),
	SGYO_TNI_SU("SGYO_TNI_SU"),
	SOBI_SU("SOBI_SU"),
	DBL_DLFLG("DBL_DLFLG"),
	HYJN_KOS_SY_TS_KBN("HYJN_KOS_SY_TS_KBN"),
	SDO_DLFLG("SDO_DLFLG"),
	LGC_FLG("LGC_FLG"),
	TRK_USERID("TRK_USERID"),
	TRK_PGMID("TRK_PGMID"),
	INP_DAYT("INP_DAYT"),
	SIS_KS_USERID("SIS_KS_USERID"),
	SIS_KS_PGMID("SIS_KS_PGMID"),
	LAST_UPD_DAYT("LAST_UPD_DAYT");

	private final String text;

	private Detail(final String text) {
		this.text = text;
	}

	public String get(){
		return this.text;
	}
    }

    //Work_INF
    public enum Info implements Fields {
	KSYCD("KSYCD"),
	SBN("SBN"),
	SGYO_MSINO("SGYO_MSINO"),
	DBL_DLFLG("DBL_DLFLG"),
	SGYO_KBN("SGYO_KBN"),
	GT_SGYO_KBN("GT_SGYO_KBN"),
	GT_SGYO_KKR("GT_SGYO_KKR"),
	KISY("KISY"),
	TYP("TYP"),
	SGYOCD("SGYOCD"),
	YKKN_STDAY("YKKN_STDAY"),
	SGYO_NM("SGYO_NM"),
	SGYO_NM_HSK("SGYO_NM_HSK"),
	TEGK_SIY_PNTCD("TEGK_SIY_PNTCD"),
	TEGK_MNO("TEGK_MNO"),
	BHN_DKUMU("BHN_DKUMU"),
	HJUN_TNK("HJUN_TNK"),
	SKTNK("SKTNK"),
	CYHY_SKTNK("CYHY_SKTNK"),
	KKYK_STI_SKTNK("KKYK_STI_SKTNK"),
	TKY_NBR("TKY_NBR"),
	JKT_SUM_YTGK("JKT_SUM_YTGK"),
	GT_SUM_YTGK("GT_SUM_YTGK"),
	GKISU("GKISU"),
	SNTT_FUKA_KISU("SNTT_FUKA_KISU"),
	SYHN_SBT_KISU("SYHN_SBT_KISU"),
	INV_KISU("INV_KISU"),
	HJUN_KOS("HJUN_KOS"),
	INV_KOS("INV_KOS"),
	SIJI_KOS("SIJI_KOS"),
	JRKOS("JRKOS"),
	ODR_SU("ODR_SU"),
	HJUN_KKU("HJUN_KKU"),
	SYAN_HYJK("SYAN_HYJK"),
	JKN_NBKG("JKN_NBKG"),
	TBNB_KGK("TBNB_KGK"),
	SKKG("SKKG"),
	SYAN_SKKG("SYAN_SKKG"),
	GJRY_GT_UAGE_KBN("GJRY_GT_UAGE_KBN"),
	JKT_GKGK("JKT_GKGK"),
	GT_GKGK("GT_GKGK"),
	TYK_GNKA("TYK_GNKA"),
	SHZG("SHZG"),
	SZR("SZR"),
	KKIJ_RIT("KKIJ_RIT"),
	ARRI_KGK("ARRI_KGK"),
	ARR("ARR"),
	SGYO_SJDAY("SGYO_SJDAY"),
	SKK_UMFLG("SKK_UMFLG"),
	GTWT_UMFLG("GTWT_UMFLG"),
	SGYO_KRFLG("SGYO_KRFLG"),
	GTKFLG("GTKFLG"),
	SCHH_FLG("SCHH_FLG"),
	LGC_FLG("LGC_FLG"),
	TRK_USERID("TRK_USERID"),
	TRK_PGMID("TRK_PGMID"),
	INP_DAYT("INP_DAYT"),
	SIS_KS_USERID("SIS_KS_USERID"),
	SIS_KS_PGMID("SIS_KS_PGMID"),
	LAST_UPD_DAYT("LAST_UPD_DAYT");

	private final String text;

	private Info(final String text) {
		this.text = text;
	}

	public String get(){
		return this.text;
	}
    }
}
