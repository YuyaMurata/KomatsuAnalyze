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
public class Service {
    //Service
    public enum _Service  implements Fields{
	KSYCD("KSYCD"),
	KISY("KISY"),
	KIBAN("KIBAN"),
	JSDAY("JSDAY"),
	SVCKR_KNRNO("SVCKR_KNRNO"),
	SGYO_MSINO("SGYO_MSINO"),
	UPD_RRK_MNO("UPD_RRK_MNO"),
	SVC_MTR("SVC_MTR"),
	SVC_MTR_KBN("SVC_MTR_KBN"),
	HY_KKYKCD("HY_KKYKCD"),
	KYKNM("KYKNM"),
	TYP("TYP"),
	SYHK("SYHK"),
	KISY_CMT("KISY_CMT"),
	HASSEI_KBN("HASSEI_KBN"),
	ODR_KBN("ODR_KBN"),
	UAGE_KBN_1("UAGE_KBN_1"),
	UAGE_KBN_2("UAGE_KBN_2"),
	UAGE_KBN_3("UAGE_KBN_3"),
	SGYO_KTICD("SGYO_KTICD"),
	SGYOCD("SGYOCD"),
	SGYO_NM("SGYO_NM"),
	DIHY_SGYO_FLG("DIHY_SGYO_FLG"),
	KTD_NIYOCD("KTD_NIYOCD"),
	MNT_NAIYOCD("MNT_NAIYOCD"),
	JKOS("JKOS"),
	JISI_SU("JISI_SU"),
	GAIYO("GAIYO"),
	SKKG("SKKG"),
	HNBN("HNBN"),
	BHN_NM("BHN_NM"),
	AKAKURO_KBN("AKAKURO_KBN"),
	TRK_USERID("TRK_USERID"),
	TRK_PGMID("TRK_PGMID"),
	INP_DAYT("INP_DAYT"),
	SIS_KS_USERID("SIS_KS_USERID"),
	SIS_KS_PGMID("SIS_KS_PGMID"),
	LAST_UPD_DAYT("LAST_UPD_DAYT");

	private final String text;

	private _Service(final String text) {
		this.text = text;
	}

	public String get(){
		return this.text;
	}
    }
}
