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
public class Komtrax {
    
    // Komtrax 
    public enum TABLE implements Fields {
        CW_ACTUAL_OPERATION("komtrax_CW_ACTUAL_OPERATION", Komtrax.CW_ACTUAL_OPERATION.values()),
        CW_ACT_DATA("komtrax_CW_ACT_DATA", Komtrax.CW_ACT_DATA.values()),
        CW_ALTITUDE("komtrax_CW_ALTITUDE", Komtrax.CW_ALTITUDE.values()),
        CW_AREA("komtrax_CW_AREA", Komtrax.CW_AREA.values()),
        CW_CAUTION_DATA("komtrax_CW_CAUTION_DATA", Komtrax.CW_CAUTION_DATA.values()),
        CW_COMPO_LOG("komtrax_CW_COMPO_LOG", Komtrax.CW_COMPO_LOG.values()),
        CW_COMPO_STRUCTURE_ENGINE("komtrax_CW_COMPO_STRUCTURE_ENGINE", Komtrax.CW_COMPO_STRUCTURE_ENGINE.values()),
        CW_DAILY_ACT_FUEL_CONSUME("komtrax_CW_DAILY_ACT_FUEL_CONSUME", Komtrax.CW_DAILY_ACT_FUEL_CONSUME.values()),
        CW_DAILY_ACT_FUEL_EFF("komtrax_CW_DAILY_ACT_FUEL_EFF", Komtrax.CW_DAILY_ACT_FUEL_EFF.values()),
        CW_DAILY_ACT_OPERATION("komtrax_CW_DAILY_ACT_OPERATION", Komtrax.CW_DAILY_ACT_OPERATION.values()),
        CW_DAILY_AIS_COUNT("komtrax_CW_DAILY_AIS_COUNT", Komtrax.CW_DAILY_AIS_COUNT.values()),
        CW_DAILY_AIS_SETTING("komtrax_CW_DAILY_AIS_SETTING", Komtrax.CW_DAILY_AIS_SETTING.values()),
        CW_DAILY_AIS_TIME("komtrax_CW_DAILY_AIS_TIME", Komtrax.CW_DAILY_AIS_TIME.values()),
        CW_DAILY_ATM("komtrax_CW_DAILY_ATM", Komtrax.CW_DAILY_ATM.values()),
        CW_DAILY_ATT("komtrax_CW_DAILY_ATT", Komtrax.CW_DAILY_ATT.values()),
        CW_DAILY_BREAKER("komtrax_CW_DAILY_BREAKER", Komtrax.CW_DAILY_BREAKER.values()),
        CW_DAILY_DEF_CONSUME("komtrax_CW_DAILY_DEF_CONSUME", Komtrax.CW_DAILY_DEF_CONSUME.values()),
        CW_DAILY_DIG("komtrax_CW_DAILY_DIG", Komtrax.CW_DAILY_DIG.values()),
        CW_DAILY_ECO("komtrax_CW_DAILY_ECO", Komtrax.CW_DAILY_ECO.values()),
        CW_DAILY_EMODE("komtrax_CW_DAILY_EMODE", Komtrax.CW_DAILY_EMODE.values()),
        CW_DAILY_EMODE_FUEL_EFF("komtrax_CW_DAILY_EMODE_FUEL_EFF", Komtrax.CW_DAILY_EMODE_FUEL_EFF.values()),
        CW_DAILY_FS_SETTING("komtrax_CW_DAILY_FS_SETTING", Komtrax.CW_DAILY_FS_SETTING.values()),
        CW_DAILY_FUEL_CONSUME("komtrax_CW_DAILY_FUEL_CONSUME", Komtrax.CW_DAILY_FUEL_CONSUME.values()),
        CW_DAILY_FUEL_EFF("komtrax_CW_DAILY_FUEL_EFF", Komtrax.CW_DAILY_FUEL_EFF.values()),
        CW_DAILY_FUEL_SENSOR("komtrax_CW_DAILY_FUEL_SENSOR", Komtrax.CW_DAILY_FUEL_SENSOR.values()),
        CW_DAILY_GUIDANCE("komtrax_CW_DAILY_GUIDANCE", Komtrax.CW_DAILY_GUIDANCE.values()),
        CW_DAILY_HOIST("komtrax_CW_DAILY_HOIST", Komtrax.CW_DAILY_HOIST.values()),
        CW_DAILY_ICT_ACT("komtrax_CW_DAILY_ICT_ACT", Komtrax.CW_DAILY_ICT_ACT.values()),
        CW_DAILY_ICT_MODE("komtrax_CW_DAILY_ICT_MODE", Komtrax.CW_DAILY_ICT_MODE.values()),
        CW_DAILY_ICT_USAGE("komtrax_CW_DAILY_ICT_USAGE", Komtrax.CW_DAILY_ICT_USAGE.values()),
        CW_DAILY_LOADING("komtrax_CW_DAILY_LOADING", Komtrax.CW_DAILY_LOADING.values()),
        CW_DAILY_LOAD_NEW("komtrax_CW_DAILY_LOAD_NEW", Komtrax.CW_DAILY_LOAD_NEW.values()),
        CW_DAILY_MODE("komtrax_CW_DAILY_MODE", Komtrax.CW_DAILY_MODE.values()),
        CW_DAILY_MODE_NEW("komtrax_CW_DAILY_MODE_NEW", Komtrax.CW_DAILY_MODE_NEW.values()),
        CW_DAILY_OUTSIDE_TEMP("komtrax_CW_DAILY_OUTSIDE_TEMP", Komtrax.CW_DAILY_OUTSIDE_TEMP.values()),
        CW_DAILY_PUMP("komtrax_CW_DAILY_PUMP", Komtrax.CW_DAILY_PUMP.values()),
        CW_DAILY_RELIEF("komtrax_CW_DAILY_RELIEF", Komtrax.CW_DAILY_RELIEF.values()),
        CW_DAILY_RUNNING("komtrax_CW_DAILY_RUNNING", Komtrax.CW_DAILY_RUNNING.values()),
        CW_DAILY_SCR_GAUGE("komtrax_CW_DAILY_SCR_GAUGE", Komtrax.CW_DAILY_SCR_GAUGE.values()),
        CW_DAILY_SOOT("komtrax_CW_DAILY_SOOT", Komtrax.CW_DAILY_SOOT.values()),
        CW_DAILY_THROTTLE("komtrax_CW_DAILY_THROTTLE", Komtrax.CW_DAILY_THROTTLE.values()),
        CW_DAILY_TORQUE("komtrax_CW_DAILY_TORQUE", Komtrax.CW_DAILY_TORQUE.values()),
        CW_DAILY_USAGE("komtrax_CW_DAILY_USAGE", Komtrax.CW_DAILY_USAGE.values()),
        CW_DAILY_USAGE_EX("komtrax_CW_DAILY_USAGE_EX", Komtrax.CW_DAILY_USAGE_EX.values()),
        CW_DEF_SUPPLY("komtrax_CW_DEF_SUPPLY", Komtrax.CW_DEF_SUPPLY.values()),
        CW_ERROR("komtrax_CW_ERROR", Komtrax.CW_ERROR.values()),
        CW_GAUGE_FUEL_WTTEMP("komtrax_CW_GAUGE_FUEL_WTTEMP", Komtrax.CW_GAUGE_FUEL_WTTEMP.values()),
        CW_GPS("komtrax_CW_GPS", Komtrax.CW_GPS.values()),
        CW_INFORMATION("komtrax_CW_INFORMATION", Komtrax.CW_INFORMATION.values()),
        CW_KDPF_EXHAUST_TEMP("komtrax_CW_KDPF_EXHAUST_TEMP", Komtrax.CW_KDPF_EXHAUST_TEMP.values()),
        CW_KDPF_FORCED_COUNT("komtrax_CW_KDPF_FORCED_COUNT", Komtrax.CW_KDPF_FORCED_COUNT.values()),
        CW_KDPF_FORCED_INFO("komtrax_CW_KDPF_FORCED_INFO", Komtrax.CW_KDPF_FORCED_INFO.values()),
        CW_KDPF_MAINTENANCE_INFORM("komtrax_CW_KDPF_MAINTENANCE_INFORM", Komtrax.CW_KDPF_MAINTENANCE_INFORM.values()),
        CW_MAINTENANCE_INFORM("komtrax_CW_MAINTENANCE_INFORM", Komtrax.CW_MAINTENANCE_INFORM.values()),
        CW_MONTHLY_ACT_FUEL_CONSUME("komtrax_CW_MONTHLY_ACT_FUEL_CONSUME", Komtrax.CW_MONTHLY_ACT_FUEL_CONSUME.values()),
        CW_MONTHLY_ACT_FUEL_EFF("komtrax_CW_MONTHLY_ACT_FUEL_EFF", Komtrax.CW_MONTHLY_ACT_FUEL_EFF.values()),
        CW_MONTHLY_ACT_OPERATION("komtrax_CW_MONTHLY_ACT_OPERATION", Komtrax.CW_MONTHLY_ACT_OPERATION.values()),
        CW_MONTHLY_AIS_COUNT("komtrax_CW_MONTHLY_AIS_COUNT", Komtrax.CW_MONTHLY_AIS_COUNT.values()),
        CW_MONTHLY_AIS_TIME("komtrax_CW_MONTHLY_AIS_TIME", Komtrax.CW_MONTHLY_AIS_TIME.values()),
        CW_MONTHLY_ASH("komtrax_CW_MONTHLY_ASH", Komtrax.CW_MONTHLY_ASH.values()),
        CW_MONTHLY_ATT("komtrax_CW_MONTHLY_ATT", Komtrax.CW_MONTHLY_ATT.values()),
        CW_MONTHLY_BREAKER("komtrax_CW_MONTHLY_BREAKER", Komtrax.CW_MONTHLY_BREAKER.values()),
        CW_MONTHLY_DATA1("komtrax_CW_MONTHLY_DATA1", Komtrax.CW_MONTHLY_DATA1.values()),
        CW_MONTHLY_DATA2("komtrax_CW_MONTHLY_DATA2", Komtrax.CW_MONTHLY_DATA2.values()),
        CW_MONTHLY_DEF_CONSUME("komtrax_CW_MONTHLY_DEF_CONSUME", Komtrax.CW_MONTHLY_DEF_CONSUME.values()),
        CW_MONTHLY_DEF_DEFROST("komtrax_CW_MONTHLY_DEF_DEFROST", Komtrax.CW_MONTHLY_DEF_DEFROST.values()),
        CW_MONTHLY_DIG("komtrax_CW_MONTHLY_DIG", Komtrax.CW_MONTHLY_DIG.values()),
        CW_MONTHLY_ECO("komtrax_CW_MONTHLY_ECO", Komtrax.CW_MONTHLY_ECO.values()),
        CW_MONTHLY_EMODE_FUEL_EFF("komtrax_CW_MONTHLY_EMODE_FUEL_EFF", Komtrax.CW_MONTHLY_EMODE_FUEL_EFF.values()),
        CW_MONTHLY_FUEL_CONSUME("komtrax_CW_MONTHLY_FUEL_CONSUME", Komtrax.CW_MONTHLY_FUEL_CONSUME.values()),
        CW_MONTHLY_HOIST("komtrax_CW_MONTHLY_HOIST", Komtrax.CW_MONTHLY_HOIST.values()),
        CW_MONTHLY_ICT_ACT("komtrax_CW_MONTHLY_ICT_ACT", Komtrax.CW_MONTHLY_ICT_ACT.values()),
        CW_MONTHLY_ICT_MODE("komtrax_CW_MONTHLY_ICT_MODE", Komtrax.CW_MONTHLY_ICT_MODE.values()),
        CW_MONTHLY_ICT_USAGE("komtrax_CW_MONTHLY_ICT_USAGE", Komtrax.CW_MONTHLY_ICT_USAGE.values()),
        CW_MONTHLY_LOAD("komtrax_CW_MONTHLY_LOAD", Komtrax.CW_MONTHLY_LOAD.values()),
        CW_MONTHLY_LOAD_NEW("komtrax_CW_MONTHLY_LOAD_NEW", Komtrax.CW_MONTHLY_LOAD_NEW.values()),
        CW_MONTHLY_MODE("komtrax_CW_MONTHLY_MODE", Komtrax.CW_MONTHLY_MODE.values()),
        CW_MONTHLY_MODE_NEW("komtrax_CW_MONTHLY_MODE_NEW", Komtrax.CW_MONTHLY_MODE_NEW.values()),
        CW_MONTHLY_RELIEF("komtrax_CW_MONTHLY_RELIEF", Komtrax.CW_MONTHLY_RELIEF.values()),
        CW_MONTHLY_RUNNING("komtrax_CW_MONTHLY_RUNNING", Komtrax.CW_MONTHLY_RUNNING.values()),
        CW_MONTHLY_TM_RATIO("komtrax_CW_MONTHLY_TM_RATIO", Komtrax.CW_MONTHLY_TM_RATIO.values()),
        CW_MONTHLY_USAGE("komtrax_CW_MONTHLY_USAGE", Komtrax.CW_MONTHLY_USAGE.values()),
        CW_MONTHLY_USAGE_EX("komtrax_CW_MONTHLY_USAGE_EX", Komtrax.CW_MONTHLY_USAGE_EX.values()),
        CW_ODOMETER("komtrax_CW_ODOMETER", Komtrax.CW_ODOMETER.values()),
        CW_RECEIVE_MESSAGE("komtrax_CW_RECEIVE_MESSAGE", Komtrax.CW_RECEIVE_MESSAGE.values()),
        CW_REGENERATION_INFO("komtrax_CW_REGENERATION_INFO", Komtrax.CW_REGENERATION_INFO.values()),
        CW_SERVICE_METER("komtrax_CW_SERVICE_METER", Komtrax.CW_SERVICE_METER.values()),
        CW_TOTAL_TORQUE("komtrax_CW_TOTAL_TORQUE", Komtrax.CW_TOTAL_TORQUE.values());

        private final String text;
        private final Fields[] header;

        private TABLE(final String text, Fields[] header) {
            this.text = text;
            this.header = header;
        }

        public String get() {
            return this.text;
        }
    }
    
    //CW_ACTUAL_OPERATION
    public enum CW_ACTUAL_OPERATION implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        ACL_DATE("ACL_DATE"),
        T_TIME("T_TIME"),
        S_TIME("S_TIME"),
        ACL_SMR_VALUE("ACL_SMR_VALUE"),
        ATT_SMR_VALUE("ATT_SMR_VALUE"),
        DL_FLAG("DL_FLAG");

        private final String text;

        private CW_ACTUAL_OPERATION(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_ACT_DATA
    public enum CW_ACT_DATA implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        ACT_DATE("ACT_DATE"),
        T_TIME("T_TIME"),
        S_TIME("S_TIME"),
        ACT_COUNT("ACT_COUNT"),
        DAILY_UNIT("DAILY_UNIT"),
        ACT_UNIT("ACT_UNIT"),
        ACT_MAP0("ACT_MAP0"),
        ACT_MAP1("ACT_MAP1"),
        ACT_MAP2("ACT_MAP2"),
        ACT_MAP3("ACT_MAP3"),
        INVALID("INVALID"),
        DL_FLAG("DL_FLAG");

        private final String text;

        private CW_ACT_DATA(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_ALTITUDE
    public enum CW_ALTITUDE implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        TIME_FLAG("TIME_FLAG"),
        ALT_TIME("ALT_TIME"),
        S_TIME("S_TIME"),
        T_TIME("T_TIME"),
        ALTITUDE("ALTITUDE"),
        DL_FLAG("DL_FLAG"),
        MEASURE_TIME("MEASURE_TIME");

        private final String text;

        private CW_ALTITUDE(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_AREA
    public enum CW_AREA implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        TIME_FLAG("TIME_FLAG"),
        T_TIME("T_TIME"),
        S_TIME("S_TIME"),
        AREA_TIME("AREA_TIME"),
        NOW_AREA_ID("NOW_AREA_ID"),
        PREV_AREA_ID("PREV_AREA_ID"),
        DL_FLAG("DL_FLAG"),
        SMR_VALUE("SMR_VALUE");

        private final String text;

        private CW_AREA(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_CAUTION_DATA
    public enum CW_CAUTION_DATA implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        CAUTION_DATE("CAUTION_DATE"),
        CAUTION_UNIT("CAUTION_UNIT"),
        ICON_CODE("ICON_CODE"),
        CAUTION_MAP("CAUTION_MAP"),
        CAUTION_COUNT("CAUTION_COUNT"),
        S_TIME("S_TIME"),
        T_TIME("T_TIME"),
        INVALID("INVALID"),
        DL_FLAG("DL_FLAG");

        private final String text;

        private CW_CAUTION_DATA(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_COMPO_LOG
    public enum CW_COMPO_LOG implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        T_TIME("T_TIME"),
        S_TIME("S_TIME"),
        FILE_ID("FILE_ID"),
        VERSION("VERSION"),
        LOG_KIND("LOG_KIND"),
        RPC_NO("RPC_NO"),
        KM_CONDITION("KM_CONDITION"),
        KM_INTERVAL("KM_INTERVAL"),
        KM_UNIT("KM_UNIT"),
        KM_NUM("KM_NUM"),
        THRESH1("THRESH1"),
        THRESH2("THRESH2"),
        THRESH3("THRESH3"),
        THRESH4("THRESH4"),
        THRESH5("THRESH5"),
        THRESH6("THRESH6"),
        THRESH7("THRESH7"),
        THRESH8("THRESH8"),
        THRESH9("THRESH9"),
        THRESH10("THRESH10"),
        THRESH11("THRESH11"),
        THRESH12("THRESH12"),
        THRESH13("THRESH13"),
        THRESH14("THRESH14"),
        THRESH15("THRESH15"),
        DATA_1("DATA_1"),
        DATA_2("DATA_2"),
        DATA_3("DATA_3"),
        DATA_4("DATA_4"),
        DATA_5("DATA_5"),
        DATA_6("DATA_6"),
        DATA_7("DATA_7"),
        DATA_8("DATA_8"),
        DATA_9("DATA_9"),
        DATA_10("DATA_10"),
        DATA_11("DATA_11"),
        DATA_12("DATA_12"),
        DATA_13("DATA_13"),
        DATA_14("DATA_14"),
        DATA_15("DATA_15"),
        DATA_16("DATA_16"),
        SMR_VALUE("SMR_VALUE"),
        COMPO_TIME("COMPO_TIME");

        private final String text;

        private CW_COMPO_LOG(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_COMPO_STRUCTURE_ENGINE
    public enum CW_COMPO_STRUCTURE_ENGINE implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        UPDATE_TIME("UPDATE_TIME"),
        DAILY_DEF_CONSUME_FLAG("DAILY_DEF_CONSUME_FLAG"),
        DAILY_DEF_CONSUME_INFO("DAILY_DEF_CONSUME_INFO"),
        MONTHLY_DEF_CONSUME_FLAG("MONTHLY_DEF_CONSUME_FLAG"),
        MONTHLY_DEF_CONSUME_INFO("MONTHLY_DEF_CONSUME_INFO"),
        DAILY_SOOT_FLAG("DAILY_SOOT_FLAG"),
        DAILY_SOOT_INFO("DAILY_SOOT_INFO"),
        MONTHLY_ASH_FLAG("MONTHLY_ASH_FLAG"),
        MONTHLY_ASH_INFO("MONTHLY_ASH_INFO"),
        REGENERATION_INFO_FLAG("REGENERATION_INFO_FLAG"),
        REGENERATION_INFO_INFO("REGENERATION_INFO_INFO");

        private final String text;

        private CW_COMPO_STRUCTURE_ENGINE(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_DAILY_ACT_FUEL_CONSUME
    public enum CW_DAILY_ACT_FUEL_CONSUME implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        T_TIME("T_TIME"),
        S_TIME("S_TIME"),
        DL_FLAG("DL_FLAG"),
        INVALID("INVALID"),
        CONSUME_DATE("CONSUME_DATE"),
        CONSUME_VALUE("CONSUME_VALUE");

        private final String text;

        private CW_DAILY_ACT_FUEL_CONSUME(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_DAILY_ACT_FUEL_EFF
    public enum CW_DAILY_ACT_FUEL_EFF implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        T_TIME("T_TIME"),
        S_TIME("S_TIME"),
        DL_FLAG("DL_FLAG"),
        INVALID("INVALID"),
        EFF_DATE("EFF_DATE"),
        EFF_VALUE("EFF_VALUE");

        private final String text;

        private CW_DAILY_ACT_FUEL_EFF(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_DAILY_ACT_OPERATION
    public enum CW_DAILY_ACT_OPERATION implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        ACTUAL_DATE("ACTUAL_DATE"),
        S_TIME("S_TIME"),
        T_TIME("T_TIME"),
        ACTUAL_COUNT("ACTUAL_COUNT"),
        DAILY_UNIT("DAILY_UNIT"),
        INVALID("INVALID"),
        DL_FLAG("DL_FLAG");

        private final String text;

        private CW_DAILY_ACT_OPERATION(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_DAILY_AIS_COUNT
    public enum CW_DAILY_AIS_COUNT implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        T_TIME("T_TIME"),
        S_TIME("S_TIME"),
        DL_FLAG("DL_FLAG"),
        INVALID("INVALID"),
        AIS_COUNT_DATE("AIS_COUNT_DATE"),
        AIS_COUNT("AIS_COUNT");

        private final String text;

        private CW_DAILY_AIS_COUNT(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_DAILY_AIS_SETTING
    public enum CW_DAILY_AIS_SETTING implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        T_TIME("T_TIME"),
        S_TIME("S_TIME"),
        DL_FLAG("DL_FLAG"),
        INVALID("INVALID"),
        AIS_SETTING_DATE("AIS_SETTING_DATE"),
        AIS_SETTING("AIS_SETTING");

        private final String text;

        private CW_DAILY_AIS_SETTING(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_DAILY_AIS_TIME
    public enum CW_DAILY_AIS_TIME implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        T_TIME("T_TIME"),
        S_TIME("S_TIME"),
        DL_FLAG("DL_FLAG"),
        INVALID("INVALID"),
        AIS_TIME_DATE("AIS_TIME_DATE"),
        AIS_TIME("AIS_TIME");

        private final String text;

        private CW_DAILY_AIS_TIME(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_DAILY_ATM
    public enum CW_DAILY_ATM implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        T_TIME("T_TIME"),
        S_TIME("S_TIME"),
        DL_FLAG("DL_FLAG"),
        INVALID("INVALID"),
        ATM_DATE("ATM_DATE"),
        MAX_TEMP("MAX_TEMP"),
        MIN_TEMP("MIN_TEMP"),
        PRESSURE("PRESSURE");

        private final String text;

        private CW_DAILY_ATM(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_DAILY_ATT
    public enum CW_DAILY_ATT implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        ATT_DATE("ATT_DATE"),
        S_TIME("S_TIME"),
        T_TIME("T_TIME"),
        ATT_COUNT("ATT_COUNT"),
        DAILY_UNIT("DAILY_UNIT"),
        INVALID("INVALID"),
        DL_FLAG("DL_FLAG");

        private final String text;

        private CW_DAILY_ATT(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_DAILY_BREAKER
    public enum CW_DAILY_BREAKER implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        BREAKER_DATE("BREAKER_DATE"),
        S_TIME("S_TIME"),
        T_TIME("T_TIME"),
        BREAKER_COUNT("BREAKER_COUNT"),
        DAILY_UNIT("DAILY_UNIT"),
        INVALID("INVALID"),
        DL_FLAG("DL_FLAG");

        private final String text;

        private CW_DAILY_BREAKER(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_DAILY_DEF_CONSUME
    public enum CW_DAILY_DEF_CONSUME implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        CONSUME_DATE("CONSUME_DATE"),
        T_TIME("T_TIME"),
        S_TIME("S_TIME"),
        DL_FLAG("DL_FLAG"),
        INVALID("INVALID"),
        CONSUME_COUNT("CONSUME_COUNT"),
        NW1_C1_CONSUME_COUNT("NW1_C1_CONSUME_COUNT");

        private final String text;

        private CW_DAILY_DEF_CONSUME(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_DAILY_DIG
    public enum CW_DAILY_DIG implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        DIG_DATE("DIG_DATE"),
        S_TIME("S_TIME"),
        T_TIME("T_TIME"),
        DIG_COUNT("DIG_COUNT"),
        DAILY_UNIT("DAILY_UNIT"),
        INVALID("INVALID"),
        DL_FLAG("DL_FLAG");

        private final String text;

        private CW_DAILY_DIG(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_DAILY_ECO
    public enum CW_DAILY_ECO implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        ECO_DATE("ECO_DATE"),
        S_TIME("S_TIME"),
        T_TIME("T_TIME"),
        ECO_COUNT("ECO_COUNT"),
        DAILY_UNIT("DAILY_UNIT"),
        INVALID("INVALID"),
        DL_FLAG("DL_FLAG");

        private final String text;

        private CW_DAILY_ECO(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_DAILY_EMODE
    public enum CW_DAILY_EMODE implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        T_TIME("T_TIME"),
        S_TIME("S_TIME"),
        DL_FLAG("DL_FLAG"),
        INVALID("INVALID"),
        EMODE_DATE("EMODE_DATE"),
        RATE_BY_SMR("RATE_BY_SMR"),
        RATE_BY_ACT("RATE_BY_ACT");

        private final String text;

        private CW_DAILY_EMODE(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_DAILY_EMODE_FUEL_EFF
    public enum CW_DAILY_EMODE_FUEL_EFF implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        T_TIME("T_TIME"),
        S_TIME("S_TIME"),
        DL_FLAG("DL_FLAG"),
        INVALID("INVALID"),
        EFF_DATE("EFF_DATE"),
        EFF_VALUE("EFF_VALUE");

        private final String text;

        private CW_DAILY_EMODE_FUEL_EFF(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_DAILY_FS_SETTING
    public enum CW_DAILY_FS_SETTING implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        T_TIME("T_TIME"),
        S_TIME("S_TIME"),
        DL_FLAG("DL_FLAG"),
        INVALID("INVALID"),
        FS_DATE("FS_DATE"),
        FS_STATUS("FS_STATUS");

        private final String text;

        private CW_DAILY_FS_SETTING(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_DAILY_FUEL_CONSUME
    public enum CW_DAILY_FUEL_CONSUME implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        CONSUME_DATE("CONSUME_DATE"),
        S_TIME("S_TIME"),
        T_TIME("T_TIME"),
        CONSUME_COUNT("CONSUME_COUNT"),
        INVALID("INVALID"),
        DL_FLAG("DL_FLAG");

        private final String text;

        private CW_DAILY_FUEL_CONSUME(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_DAILY_FUEL_EFF
    public enum CW_DAILY_FUEL_EFF implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        EFF_DATE("EFF_DATE"),
        S_TIME("S_TIME"),
        T_TIME("T_TIME"),
        EFF_COUNT("EFF_COUNT"),
        INVALID("INVALID"),
        DL_FLAG("DL_FLAG");

        private final String text;

        private CW_DAILY_FUEL_EFF(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_DAILY_FUEL_SENSOR
    public enum CW_DAILY_FUEL_SENSOR implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        T_TIME("T_TIME"),
        S_TIME("S_TIME"),
        DL_FLAG("DL_FLAG"),
        INVALID("INVALID"),
        SENSOR_DATE("SENSOR_DATE"),
        SENSOR_VALUE("SENSOR_VALUE");

        private final String text;

        private CW_DAILY_FUEL_SENSOR(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_DAILY_GUIDANCE
    public enum CW_DAILY_GUIDANCE implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        T_TIME("T_TIME"),
        S_TIME("S_TIME"),
        DL_FLAG("DL_FLAG"),
        INVALID("INVALID"),
        GUIDE_DATE("GUIDE_DATE"),
        GUIDE_COUNT00("GUIDE_COUNT00"),
        GUIDE_COUNT01("GUIDE_COUNT01"),
        GUIDE_COUNT02("GUIDE_COUNT02"),
        GUIDE_COUNT03("GUIDE_COUNT03"),
        GUIDE_COUNT04("GUIDE_COUNT04"),
        GUIDE_COUNT05("GUIDE_COUNT05"),
        GUIDE_COUNT06("GUIDE_COUNT06"),
        GUIDE_COUNT07("GUIDE_COUNT07"),
        GUIDE_COUNT08("GUIDE_COUNT08"),
        GUIDE_COUNT09("GUIDE_COUNT09"),
        GUIDE_COUNT10("GUIDE_COUNT10"),
        GUIDE_COUNT11("GUIDE_COUNT11"),
        GUIDE_COUNT12("GUIDE_COUNT12"),
        GUIDE_COUNT13("GUIDE_COUNT13"),
        GUIDE_COUNT14("GUIDE_COUNT14"),
        GUIDE_COUNT15("GUIDE_COUNT15"),
        GUIDE_COUNT16("GUIDE_COUNT16"),
        GUIDE_COUNT17("GUIDE_COUNT17"),
        GUIDE_COUNT18("GUIDE_COUNT18"),
        GUIDE_COUNT19("GUIDE_COUNT19"),
        GUIDE_COUNT20("GUIDE_COUNT20"),
        GUIDE_COUNT21("GUIDE_COUNT21"),
        GUIDE_COUNT22("GUIDE_COUNT22"),
        GUIDE_COUNT23("GUIDE_COUNT23"),
        GUIDE_COUNT24("GUIDE_COUNT24"),
        GUIDE_COUNT25("GUIDE_COUNT25"),
        GUIDE_COUNT26("GUIDE_COUNT26"),
        GUIDE_COUNT27("GUIDE_COUNT27"),
        GUIDE_COUNT28("GUIDE_COUNT28"),
        GUIDE_COUNT29("GUIDE_COUNT29"),
        GUIDE_COUNT30("GUIDE_COUNT30"),
        GUIDE_COUNT31("GUIDE_COUNT31");

        private final String text;

        private CW_DAILY_GUIDANCE(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_DAILY_HOIST
    public enum CW_DAILY_HOIST implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        HOIST_DATE("HOIST_DATE"),
        S_TIME("S_TIME"),
        T_TIME("T_TIME"),
        HOIST_COUNT("HOIST_COUNT"),
        DAILY_UNIT("DAILY_UNIT"),
        INVALID("INVALID"),
        DL_FLAG("DL_FLAG");

        private final String text;

        private CW_DAILY_HOIST(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_DAILY_ICT_ACT
    public enum CW_DAILY_ICT_ACT implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        T_TIME("T_TIME"),
        S_TIME("S_TIME"),
        DL_FLAG("DL_FLAG"),
        INVALID("INVALID"),
        ACT_DATE("ACT_DATE"),
        ICT_ON_TIME("ICT_ON_TIME"),
        MC_WORKING_TIME("MC_WORKING_TIME");

        private final String text;

        private CW_DAILY_ICT_ACT(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_DAILY_ICT_MODE
    public enum CW_DAILY_ICT_MODE implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        T_TIME("T_TIME"),
        S_TIME("S_TIME"),
        DL_FLAG("DL_FLAG"),
        INVALID("INVALID"),
        MODE_DATE("MODE_DATE"),
        MTIME_1("MTIME_1"),
        MTIME_2("MTIME_2"),
        MTIME_3("MTIME_3"),
        MTIME_4("MTIME_4");

        private final String text;

        private CW_DAILY_ICT_MODE(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_DAILY_ICT_USAGE
    public enum CW_DAILY_ICT_USAGE implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        T_TIME("T_TIME"),
        S_TIME("S_TIME"),
        DL_FLAG("DL_FLAG"),
        INVALID("INVALID"),
        USAGE_DATE("USAGE_DATE"),
        USAGE_VALUE01("USAGE_VALUE01"),
        USAGE_VALUE02("USAGE_VALUE02"),
        USAGE_VALUE03("USAGE_VALUE03");

        private final String text;

        private CW_DAILY_ICT_USAGE(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_DAILY_LOADING
    public enum CW_DAILY_LOADING implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        LOADING_DATE("LOADING_DATE"),
        S_TIME("S_TIME"),
        T_TIME("T_TIME"),
        LOADING_COUNT("LOADING_COUNT"),
        INVALID("INVALID"),
        DL_FLAG("DL_FLAG");

        private final String text;

        private CW_DAILY_LOADING(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_DAILY_LOAD_NEW
    public enum CW_DAILY_LOAD_NEW implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        T_TIME("T_TIME"),
        S_TIME("S_TIME"),
        DL_FLAG("DL_FLAG"),
        INVALID("INVALID"),
        LOAD_DATE("LOAD_DATE"),
        LOAD_VALUE("LOAD_VALUE");

        private final String text;

        private CW_DAILY_LOAD_NEW(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_DAILY_MODE
    public enum CW_DAILY_MODE implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        MODE_DATE("MODE_DATE"),
        S_TIME("S_TIME"),
        T_TIME("T_TIME"),
        MODE_P_COUNT("MODE_P_COUNT"),
        MODE_E_COUNT("MODE_E_COUNT"),
        MODE_B_COUNT("MODE_B_COUNT"),
        MODE_L_COUNT("MODE_L_COUNT"),
        DAILY_UNIT("DAILY_UNIT"),
        INVALID("INVALID"),
        DL_FLAG("DL_FLAG");

        private final String text;

        private CW_DAILY_MODE(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_DAILY_MODE_NEW
    public enum CW_DAILY_MODE_NEW implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        T_TIME("T_TIME"),
        S_TIME("S_TIME"),
        DL_FLAG("DL_FLAG"),
        INVALID("INVALID"),
        MODE_DATE("MODE_DATE"),
        WTIME_1("WTIME_1"),
        WTIME_2("WTIME_2"),
        WTIME_3("WTIME_3"),
        WTIME_4("WTIME_4"),
        WTIME_5("WTIME_5"),
        WTIME_6("WTIME_6"),
        WTIME_7("WTIME_7"),
        WTIME_8("WTIME_8"),
        MTIME_1("MTIME_1"),
        MTIME_2("MTIME_2"),
        MTIME_3("MTIME_3"),
        MTIME_4("MTIME_4"),
        MTIME_5("MTIME_5"),
        MTIME_6("MTIME_6"),
        MTIME_7("MTIME_7"),
        MTIME_8("MTIME_8"),
        DAILY_UNIT("DAILY_UNIT"),
        DM_FLAG("DM_FLAG");

        private final String text;

        private CW_DAILY_MODE_NEW(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_DAILY_OUTSIDE_TEMP
    public enum CW_DAILY_OUTSIDE_TEMP implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        T_TIME("T_TIME"),
        S_TIME("S_TIME"),
        DL_FLAG("DL_FLAG"),
        INVALID("INVALID"),
        TEMP_DATE("TEMP_DATE"),
        MAX_TEMP("MAX_TEMP"),
        MIN_TEMP("MIN_TEMP");

        private final String text;

        private CW_DAILY_OUTSIDE_TEMP(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_DAILY_PUMP
    public enum CW_DAILY_PUMP implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        PUMP_DATE("PUMP_DATE"),
        S_TIME("S_TIME"),
        T_TIME("T_TIME"),
        PUMP_COUNT("PUMP_COUNT"),
        INVALID("INVALID"),
        DL_FLAG("DL_FLAG");

        private final String text;

        private CW_DAILY_PUMP(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_DAILY_RELIEF
    public enum CW_DAILY_RELIEF implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        RELIEF_DATE("RELIEF_DATE"),
        S_TIME("S_TIME"),
        T_TIME("T_TIME"),
        RELIEF_COUNT("RELIEF_COUNT"),
        DAILY_UNIT("DAILY_UNIT"),
        INVALID("INVALID"),
        DL_FLAG("DL_FLAG");

        private final String text;

        private CW_DAILY_RELIEF(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_DAILY_RUNNING
    public enum CW_DAILY_RUNNING implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        RUNNING_DATE("RUNNING_DATE"),
        S_TIME("S_TIME"),
        T_TIME("T_TIME"),
        RUNNING_COUNT("RUNNING_COUNT"),
        DAILY_UNIT("DAILY_UNIT"),
        INVALID("INVALID"),
        DL_FLAG("DL_FLAG");

        private final String text;

        private CW_DAILY_RUNNING(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_DAILY_SCR_GAUGE
    public enum CW_DAILY_SCR_GAUGE implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        GAUGE_DATE("GAUGE_DATE"),
        S_TIME("S_TIME"),
        T_TIME("T_TIME"),
        GAUGE_DEF("GAUGE_DEF"),
        GAUGE_DEF_MAX("GAUGE_DEF_MAX"),
        THRESH_DEF("THRESH_DEF"),
        INVALID("INVALID"),
        DL_FLAG("DL_FLAG");

        private final String text;

        private CW_DAILY_SCR_GAUGE(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_DAILY_SOOT
    public enum CW_DAILY_SOOT implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        T_TIME("T_TIME"),
        S_TIME("S_TIME"),
        DL_FLAG("DL_FLAG"),
        INVALID("INVALID"),
        SOOT_DATE("SOOT_DATE"),
        SOOT_VALUE("SOOT_VALUE"),
        NW1_C1_SOOT_VALUE("NW1_C1_SOOT_VALUE");

        private final String text;

        private CW_DAILY_SOOT(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_DAILY_THROTTLE
    public enum CW_DAILY_THROTTLE implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        T_TIME("T_TIME"),
        S_TIME("S_TIME"),
        DL_FLAG("DL_FLAG"),
        INVALID("INVALID"),
        THROTTLE_DATE("THROTTLE_DATE"),
        AVE_THROTTLE_RATE("AVE_THROTTLE_RATE"),
        FRATE_BY_ACT("FRATE_BY_ACT");

        private final String text;

        private CW_DAILY_THROTTLE(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_DAILY_TORQUE
    public enum CW_DAILY_TORQUE implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        TORQUE_DATE("TORQUE_DATE"),
        S_TIME("S_TIME"),
        T_TIME("T_TIME"),
        TORQUE1_X("TORQUE1_X"),
        TORQUE1_Y("TORQUE1_Y"),
        TORQUE1_COUNT("TORQUE1_COUNT"),
        TORQUE2_X("TORQUE2_X"),
        TORQUE2_Y("TORQUE2_Y"),
        TORQUE2_COUNT("TORQUE2_COUNT"),
        TORQUE3_X("TORQUE3_X"),
        TORQUE3_Y("TORQUE3_Y"),
        TORQUE3_COUNT("TORQUE3_COUNT"),
        TORQUE4_X("TORQUE4_X"),
        TORQUE4_Y("TORQUE4_Y"),
        TORQUE4_COUNT("TORQUE4_COUNT"),
        TORQUE5_X("TORQUE5_X"),
        TORQUE5_Y("TORQUE5_Y"),
        TORQUE5_COUNT("TORQUE5_COUNT"),
        TORQUE6_X("TORQUE6_X"),
        TORQUE6_Y("TORQUE6_Y"),
        TORQUE6_COUNT("TORQUE6_COUNT"),
        TORQUE7_X("TORQUE7_X"),
        TORQUE7_Y("TORQUE7_Y"),
        TORQUE7_COUNT("TORQUE7_COUNT"),
        TORQUE8_X("TORQUE8_X"),
        TORQUE8_Y("TORQUE8_Y"),
        TORQUE8_COUNT("TORQUE8_COUNT"),
        AXIS1("AXIS1"),
        AXIS2("AXIS2"),
        AXIS3("AXIS3"),
        AXIS4("AXIS4"),
        AXIS5("AXIS5"),
        AXIS6("AXIS6"),
        AXIS7("AXIS7"),
        AXIS8("AXIS8"),
        INVALID("INVALID"),
        DL_FLAG("DL_FLAG");

        private final String text;

        private CW_DAILY_TORQUE(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_DAILY_USAGE
    public enum CW_DAILY_USAGE implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        T_TIME("T_TIME"),
        S_TIME("S_TIME"),
        DL_FLAG("DL_FLAG"),
        INVALID("INVALID"),
        USAGE_DATE("USAGE_DATE"),
        USAGE_VALUE01("USAGE_VALUE01"),
        USAGE_VALUE02("USAGE_VALUE02"),
        USAGE_VALUE03("USAGE_VALUE03"),
        USAGE_VALUE04("USAGE_VALUE04"),
        USAGE_VALUE05("USAGE_VALUE05"),
        USAGE_VALUE06("USAGE_VALUE06"),
        USAGE_VALUE07("USAGE_VALUE07"),
        USAGE_VALUE08("USAGE_VALUE08"),
        USAGE_VALUE09("USAGE_VALUE09"),
        USAGE_VALUE10("USAGE_VALUE10"),
        USAGE_VALUE11("USAGE_VALUE11");

        private final String text;

        private CW_DAILY_USAGE(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_DAILY_USAGE_EX
    public enum CW_DAILY_USAGE_EX implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        T_TIME("T_TIME"),
        S_TIME("S_TIME"),
        DL_FLAG("DL_FLAG"),
        INVALID("INVALID"),
        USAGE_EX_DATE("USAGE_EX_DATE"),
        USAGE_VALUE12("USAGE_VALUE12"),
        USAGE_VALUE13("USAGE_VALUE13");

        private final String text;

        private CW_DAILY_USAGE_EX(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_DEF_SUPPLY
    public enum CW_DEF_SUPPLY implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        T_TIME("T_TIME"),
        S_TIME("S_TIME"),
        DL_FLAG("DL_FLAG"),
        SUPPLY_TIME("SUPPLY_TIME"),
        SUPPLY_COUNT_KEYOFF("SUPPLY_COUNT_KEYOFF"),
        SUPPLY_COUNT_KEYON("SUPPLY_COUNT_KEYON"),
        SMR_VALUE("SMR_VALUE");

        private final String text;

        private CW_DEF_SUPPLY(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_ERROR
    public enum CW_ERROR implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        ERROR_CODE("ERROR_CODE"),
        ERROR_TIME("ERROR_TIME"),
        LOGICAL_NAME("LOGICAL_NAME"),
        INSTANCE("INSTANCE"),
        S_TIME("S_TIME"),
        T_TIME("T_TIME"),
        COUNT("COUNT"),
        ENGINE_TIME("ENGINE_TIME"),
        ERROR_KIND("ERROR_KIND"),
        DISPLAY("DISPLAY"),
        KM_CALL("KM_CALL"),
        ACTION_CODE("ACTION_CODE"),
        SMR_VALUE("SMR_VALUE"),
        INVALID("INVALID"),
        DL_FLAG("DL_FLAG"),
        SEQ_NO("SEQ_NO"),
        MON_INDEX("MON_INDEX"),
        SERVICE("SERVICE"),
        ID_TYPE("ID_TYPE"),
        KM_CODE("KM_CODE"),
        KBA_GROUP_ID("KBA_GROUP_ID"),
        KBA_CUSTOMER_ID("KBA_CUSTOMER_ID"),
        OPE_ID("OPE_ID");

        private final String text;

        private CW_ERROR(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_GAUGE_FUEL_WTTEMP
    public enum CW_GAUGE_FUEL_WTTEMP implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        GAUGE_DATE("GAUGE_DATE"),
        S_TIME("S_TIME"),
        T_TIME("T_TIME"),
        GAUGE_FUEL("GAUGE_FUEL"),
        GAUGE_FUEL2("GAUGE_FUEL2"),
        GAUGE_FUEL_MAX("GAUGE_FUEL_MAX"),
        GAUGE_WTTEMP("GAUGE_WTTEMP"),
        GAUGE_WTTEMP_MAX("GAUGE_WTTEMP_MAX"),
        THRESH_LEVEL1("THRESH_LEVEL1"),
        THRESH_LEVEL2("THRESH_LEVEL2"),
        INVALID("INVALID"),
        DL_FLAG("DL_FLAG");

        private final String text;

        private CW_GAUGE_FUEL_WTTEMP(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_GPS
    public enum CW_GPS implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        TIME_FLAG("TIME_FLAG"),
        T_TIME("T_TIME"),
        S_TIME("S_TIME"),
        GPS_TIME("GPS_TIME"),
        MEASURE_TIME("MEASURE_TIME"),
        LATITUDE("LATITUDE"),
        LONGITUDE("LONGITUDE"),
        PLACE("PLACE"),
        ENGINE("ENGINE"),
        OLD("OLD"),
        EVENT("EVENT"),
        DL_FLAG("DL_FLAG"),
        SD_FLAG("SD_FLAG"),
        SD_PLACE("SD_PLACE");

        private final String text;

        private CW_GPS(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_INFORMATION
    public enum CW_INFORMATION implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        ENTRY_TIME("ENTRY_TIME"),
        T_TIME("T_TIME"),
        S_TIME("S_TIME"),
        EVENT_CODE("EVENT_CODE"),
        COMPLEMENT("COMPLEMENT"),
        DL_FLAG("DL_FLAG"),
        INVALID("INVALID");

        private final String text;

        private CW_INFORMATION(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_KDPF_EXHAUST_TEMP
    public enum CW_KDPF_EXHAUST_TEMP implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        T_TIME("T_TIME"),
        S_TIME("S_TIME"),
        DL_FLAG("DL_FLAG"),
        KDPF_TIME("KDPF_TIME"),
        KDPF_EXHAUST_TEMP1("KDPF_EXHAUST_TEMP1"),
        KDPF_EXHAUST_TEMP2("KDPF_EXHAUST_TEMP2"),
        KDOC_EXHAUST_TEMP1("KDOC_EXHAUST_TEMP1"),
        KDOC_EXHAUST_TEMP2("KDOC_EXHAUST_TEMP2"),
        SMR_VALUE("SMR_VALUE");

        private final String text;

        private CW_KDPF_EXHAUST_TEMP(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_KDPF_FORCED_COUNT
    public enum CW_KDPF_FORCED_COUNT implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        T_TIME("T_TIME"),
        S_TIME("S_TIME"),
        DL_FLAG("DL_FLAG"),
        KDPF_TIME("KDPF_TIME"),
        FORCED_COUNT("FORCED_COUNT"),
        INTERRUPT_COUNT("INTERRUPT_COUNT"),
        COMPLETE_COUNT("COMPLETE_COUNT"),
        SMR_VALUE("SMR_VALUE");

        private final String text;

        private CW_KDPF_FORCED_COUNT(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_KDPF_FORCED_INFO
    public enum CW_KDPF_FORCED_INFO implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        T_TIME("T_TIME"),
        S_TIME("S_TIME"),
        DL_FLAG("DL_FLAG"),
        KDPF_TIME("KDPF_TIME"),
        FORCED_SMR("FORCED_SMR"),
        FORCED_TIME("FORCED_TIME"),
        FORCED_FUEL("FORCED_FUEL"),
        FORCED_FUEL_UNIT("FORCED_FUEL_UNIT");

        private final String text;

        private CW_KDPF_FORCED_INFO(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_KDPF_MAINTENANCE_INFORM
    public enum CW_KDPF_MAINTENANCE_INFORM implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        T_TIME("T_TIME"),
        S_TIME("S_TIME"),
        DL_FLAG("DL_FLAG"),
        KDPF_TIME("KDPF_TIME"),
        KDPF_EVENT("KDPF_EVENT"),
        SMR_VALUE("SMR_VALUE");

        private final String text;

        private CW_KDPF_MAINTENANCE_INFORM(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_MAINTENANCE_INFORM
    public enum CW_MAINTENANCE_INFORM implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        TIME_FLAG("TIME_FLAG"),
        S_TIME("S_TIME"),
        T_TIME("T_TIME"),
        MAINTE_TIME("MAINTE_TIME"),
        INFORM_CODE("INFORM_CODE"),
        CHANGE_TIME("CHANGE_TIME"),
        SMR_VALUE("SMR_VALUE"),
        MONITOR_RESET("MONITOR_RESET"),
        DL_FLAG("DL_FLAG"),
        EVENT_CODE("EVENT_CODE"),
        KM_INTERVAL("KM_INTERVAL"),
        THRESH("THRESH"),
        AVAILABLE_FLAG("AVAILABLE_FLAG");

        private final String text;

        private CW_MAINTENANCE_INFORM(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_MONTHLY_ACT_FUEL_CONSUME
    public enum CW_MONTHLY_ACT_FUEL_CONSUME implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        T_TIME("T_TIME"),
        S_TIME("S_TIME"),
        DL_FLAG("DL_FLAG"),
        INVALID("INVALID"),
        CONSUME_DATE("CONSUME_DATE"),
        CONSUME_VALUE("CONSUME_VALUE");

        private final String text;

        private CW_MONTHLY_ACT_FUEL_CONSUME(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_MONTHLY_ACT_FUEL_EFF
    public enum CW_MONTHLY_ACT_FUEL_EFF implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        T_TIME("T_TIME"),
        S_TIME("S_TIME"),
        DL_FLAG("DL_FLAG"),
        INVALID("INVALID"),
        EFF_DATE("EFF_DATE"),
        EFF_VALUE("EFF_VALUE");

        private final String text;

        private CW_MONTHLY_ACT_FUEL_EFF(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_MONTHLY_ACT_OPERATION
    public enum CW_MONTHLY_ACT_OPERATION implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        ACTUAL_DATE("ACTUAL_DATE"),
        S_TIME("S_TIME"),
        T_TIME("T_TIME"),
        ACTUAL_VALUE("ACTUAL_VALUE"),
        INVALID("INVALID"),
        DL_FLAG("DL_FLAG");

        private final String text;

        private CW_MONTHLY_ACT_OPERATION(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_MONTHLY_AIS_COUNT
    public enum CW_MONTHLY_AIS_COUNT implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        T_TIME("T_TIME"),
        S_TIME("S_TIME"),
        DL_FLAG("DL_FLAG"),
        INVALID("INVALID"),
        AIS_COUNT_DATE("AIS_COUNT_DATE"),
        AIS_COUNT("AIS_COUNT");

        private final String text;

        private CW_MONTHLY_AIS_COUNT(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_MONTHLY_AIS_TIME
    public enum CW_MONTHLY_AIS_TIME implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        T_TIME("T_TIME"),
        S_TIME("S_TIME"),
        DL_FLAG("DL_FLAG"),
        INVALID("INVALID"),
        AIS_TIME_DATE("AIS_TIME_DATE"),
        AIS_TIME("AIS_TIME");

        private final String text;

        private CW_MONTHLY_AIS_TIME(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_MONTHLY_ASH
    public enum CW_MONTHLY_ASH implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        T_TIME("T_TIME"),
        S_TIME("S_TIME"),
        DL_FLAG("DL_FLAG"),
        INVALID("INVALID"),
        ASH_DATE("ASH_DATE"),
        ASH_VALUE("ASH_VALUE"),
        NW1_C1_ASH_VALUE("NW1_C1_ASH_VALUE");

        private final String text;

        private CW_MONTHLY_ASH(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_MONTHLY_ATT
    public enum CW_MONTHLY_ATT implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        ATT_DATE("ATT_DATE"),
        S_TIME("S_TIME"),
        T_TIME("T_TIME"),
        ATT_VALUE("ATT_VALUE"),
        INVALID("INVALID"),
        DL_FLAG("DL_FLAG");

        private final String text;

        private CW_MONTHLY_ATT(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_MONTHLY_BREAKER
    public enum CW_MONTHLY_BREAKER implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        BREAKER_DATE("BREAKER_DATE"),
        S_TIME("S_TIME"),
        T_TIME("T_TIME"),
        BREAKER_VALUE("BREAKER_VALUE"),
        INVALID("INVALID"),
        DL_FLAG("DL_FLAG");

        private final String text;

        private CW_MONTHLY_BREAKER(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_MONTHLY_DATA1
    public enum CW_MONTHLY_DATA1 implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        CALCULATE_DATE("CALCULATE_DATE"),
        ACT_MAP("ACT_MAP"),
        ACT_DAYS("ACT_DAYS"),
        MOV_MAP("MOV_MAP"),
        MOV_DAYS("MOV_DAYS"),
        SMR_TOTAL_VALUE("SMR_TOTAL_VALUE"),
        SMR_AVERAGE_VALUE("SMR_AVERAGE_VALUE"),
        INVALID("INVALID"),
        DL_FLAG("DL_FLAG"),
        T_TIME("T_TIME"),
        S_TIME("S_TIME"),
        SMR_TIME("SMR_TIME"),
        SMR_VALUE("SMR_VALUE");

        private final String text;

        private CW_MONTHLY_DATA1(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_MONTHLY_DATA2
    public enum CW_MONTHLY_DATA2 implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        CALCULATE_DATE("CALCULATE_DATE"),
        ACL_SMR_RATE("ACL_SMR_RATE"),
        ACL_SMR_VALUE("ACL_SMR_VALUE"),
        ATT_SMR_RATE("ATT_SMR_RATE"),
        ATT_SMR_VALUE("ATT_SMR_VALUE"),
        LOAD_RATE1("LOAD_RATE1"),
        LOAD_RATE2("LOAD_RATE2"),
        LOAD_RATE3("LOAD_RATE3"),
        LOAD_RATE4("LOAD_RATE4"),
        ERROR_MAP("ERROR_MAP"),
        INVALID("INVALID"),
        DL_FLAG("DL_FLAG"),
        T_TIME("T_TIME"),
        S_TIME("S_TIME");

        private final String text;

        private CW_MONTHLY_DATA2(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_MONTHLY_DEF_CONSUME
    public enum CW_MONTHLY_DEF_CONSUME implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        CONSUME_DATE("CONSUME_DATE"),
        T_TIME("T_TIME"),
        S_TIME("S_TIME"),
        DL_FLAG("DL_FLAG"),
        INVALID("INVALID"),
        CONSUME_COUNT("CONSUME_COUNT"),
        NW1_C1_CONSUME_COUNT("NW1_C1_CONSUME_COUNT");

        private final String text;

        private CW_MONTHLY_DEF_CONSUME(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_MONTHLY_DEF_DEFROST
    public enum CW_MONTHLY_DEF_DEFROST implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        T_TIME("T_TIME"),
        S_TIME("S_TIME"),
        DL_FLAG("DL_FLAG"),
        INVALID("INVALID"),
        DEFROST_DATE("DEFROST_DATE"),
        DEFROST_COUNT("DEFROST_COUNT");

        private final String text;

        private CW_MONTHLY_DEF_DEFROST(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_MONTHLY_DIG
    public enum CW_MONTHLY_DIG implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        DIG_DATE("DIG_DATE"),
        S_TIME("S_TIME"),
        T_TIME("T_TIME"),
        DIG_VALUE("DIG_VALUE"),
        INVALID("INVALID"),
        DL_FLAG("DL_FLAG");

        private final String text;

        private CW_MONTHLY_DIG(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_MONTHLY_ECO
    public enum CW_MONTHLY_ECO implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        ECO_DATE("ECO_DATE"),
        S_TIME("S_TIME"),
        T_TIME("T_TIME"),
        ECO_VALUE("ECO_VALUE"),
        INVALID("INVALID"),
        DL_FLAG("DL_FLAG");

        private final String text;

        private CW_MONTHLY_ECO(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_MONTHLY_EMODE_FUEL_EFF
    public enum CW_MONTHLY_EMODE_FUEL_EFF implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        T_TIME("T_TIME"),
        S_TIME("S_TIME"),
        DL_FLAG("DL_FLAG"),
        INVALID("INVALID"),
        EFF_DATE("EFF_DATE"),
        EFF_VALUE("EFF_VALUE");

        private final String text;

        private CW_MONTHLY_EMODE_FUEL_EFF(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_MONTHLY_FUEL_CONSUME
    public enum CW_MONTHLY_FUEL_CONSUME implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        T_TIME("T_TIME"),
        S_TIME("S_TIME"),
        DL_FLAG("DL_FLAG"),
        INVALID("INVALID"),
        CONSUME_DATE("CONSUME_DATE"),
        CONSUME_VALUE("CONSUME_VALUE");

        private final String text;

        private CW_MONTHLY_FUEL_CONSUME(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_MONTHLY_HOIST
    public enum CW_MONTHLY_HOIST implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        HOIST_DATE("HOIST_DATE"),
        S_TIME("S_TIME"),
        T_TIME("T_TIME"),
        HOIST_VALUE("HOIST_VALUE"),
        INVALID("INVALID"),
        DL_FLAG("DL_FLAG");

        private final String text;

        private CW_MONTHLY_HOIST(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_MONTHLY_ICT_ACT
    public enum CW_MONTHLY_ICT_ACT implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        T_TIME("T_TIME"),
        S_TIME("S_TIME"),
        DL_FLAG("DL_FLAG"),
        INVALID("INVALID"),
        ACT_DATE("ACT_DATE"),
        ICT_ON_TIME("ICT_ON_TIME"),
        MC_WORKING_TIME("MC_WORKING_TIME");

        private final String text;

        private CW_MONTHLY_ICT_ACT(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_MONTHLY_ICT_MODE
    public enum CW_MONTHLY_ICT_MODE implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        T_TIME("T_TIME"),
        S_TIME("S_TIME"),
        DL_FLAG("DL_FLAG"),
        INVALID("INVALID"),
        MODE_DATE("MODE_DATE"),
        WTIME_1("WTIME_1"),
        WTIME_2("WTIME_2"),
        WTIME_3("WTIME_3"),
        WTIME_4("WTIME_4");

        private final String text;

        private CW_MONTHLY_ICT_MODE(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_MONTHLY_ICT_USAGE
    public enum CW_MONTHLY_ICT_USAGE implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        T_TIME("T_TIME"),
        S_TIME("S_TIME"),
        DL_FLAG("DL_FLAG"),
        INVALID("INVALID"),
        USAGE_DATE("USAGE_DATE"),
        USAGE_VALUE01("USAGE_VALUE01"),
        USAGE_VALUE02("USAGE_VALUE02"),
        USAGE_VALUE03("USAGE_VALUE03"),
        USAGE_VALUE04("USAGE_VALUE04"),
        USAGE_VALUE05("USAGE_VALUE05"),
        USAGE_VALUE06("USAGE_VALUE06"),
        USAGE_VALUE07("USAGE_VALUE07"),
        USAGE_VALUE08("USAGE_VALUE08"),
        USAGE_VALUE09("USAGE_VALUE09"),
        USAGE_VALUE10("USAGE_VALUE10"),
        USAGE_VALUE11("USAGE_VALUE11"),
        USAGE_VALUE12("USAGE_VALUE12"),
        USAGE_VALUE13("USAGE_VALUE13"),
        USAGE_VALUE14("USAGE_VALUE14");

        private final String text;

        private CW_MONTHLY_ICT_USAGE(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_MONTHLY_LOAD
    public enum CW_MONTHLY_LOAD implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        LOAD_DATE("LOAD_DATE"),
        S_TIME("S_TIME"),
        T_TIME("T_TIME"),
        LOAD1_VALUE("LOAD1_VALUE"),
        LOAD2_VALUE("LOAD2_VALUE"),
        LOAD3_VALUE("LOAD3_VALUE"),
        LOAD4_VALUE("LOAD4_VALUE"),
        INVALID("INVALID"),
        DL_FLAG("DL_FLAG");

        private final String text;

        private CW_MONTHLY_LOAD(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_MONTHLY_LOAD_NEW
    public enum CW_MONTHLY_LOAD_NEW implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        T_TIME("T_TIME"),
        S_TIME("S_TIME"),
        DL_FLAG("DL_FLAG"),
        INVALID("INVALID"),
        LOAD_DATE("LOAD_DATE"),
        LOAD_VALUE("LOAD_VALUE");

        private final String text;

        private CW_MONTHLY_LOAD_NEW(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_MONTHLY_MODE
    public enum CW_MONTHLY_MODE implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        MODE_DATE("MODE_DATE"),
        S_TIME("S_TIME"),
        T_TIME("T_TIME"),
        MODE_P_VALUE("MODE_P_VALUE"),
        MODE_E_VALUE("MODE_E_VALUE"),
        MODE_B_VALUE("MODE_B_VALUE"),
        MODE_L_VALUE("MODE_L_VALUE"),
        INVALID("INVALID"),
        DL_FLAG("DL_FLAG");

        private final String text;

        private CW_MONTHLY_MODE(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_MONTHLY_MODE_NEW
    public enum CW_MONTHLY_MODE_NEW implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        T_TIME("T_TIME"),
        S_TIME("S_TIME"),
        DL_FLAG("DL_FLAG"),
        INVALID("INVALID"),
        MODE_DATE("MODE_DATE"),
        WTIME_1("WTIME_1"),
        WTIME_2("WTIME_2"),
        WTIME_3("WTIME_3"),
        WTIME_4("WTIME_4"),
        WTIME_5("WTIME_5"),
        WTIME_6("WTIME_6"),
        WTIME_7("WTIME_7"),
        WTIME_8("WTIME_8"),
        MTIME_1("MTIME_1"),
        MTIME_2("MTIME_2"),
        MTIME_3("MTIME_3"),
        MTIME_4("MTIME_4"),
        MTIME_5("MTIME_5"),
        MTIME_6("MTIME_6"),
        MTIME_7("MTIME_7"),
        MTIME_8("MTIME_8");

        private final String text;

        private CW_MONTHLY_MODE_NEW(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_MONTHLY_RELIEF
    public enum CW_MONTHLY_RELIEF implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        RELIEF_DATE("RELIEF_DATE"),
        S_TIME("S_TIME"),
        T_TIME("T_TIME"),
        RELIEF_VALUE("RELIEF_VALUE"),
        INVALID("INVALID"),
        DL_FLAG("DL_FLAG");

        private final String text;

        private CW_MONTHLY_RELIEF(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_MONTHLY_RUNNING
    public enum CW_MONTHLY_RUNNING implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        RUNNING_DATE("RUNNING_DATE"),
        S_TIME("S_TIME"),
        T_TIME("T_TIME"),
        RUNNING_VALUE("RUNNING_VALUE"),
        INVALID("INVALID"),
        DL_FLAG("DL_FLAG");

        private final String text;

        private CW_MONTHLY_RUNNING(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_MONTHLY_TM_RATIO
    public enum CW_MONTHLY_TM_RATIO implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        T_TIME("T_TIME"),
        S_TIME("S_TIME"),
        DL_FLAG("DL_FLAG"),
        INVALID("INVALID"),
        RATIO_DATE("RATIO_DATE"),
        FTIME_1("FTIME_1"),
        FTIME_2("FTIME_2"),
        FTIME_3("FTIME_3"),
        FTIME_4("FTIME_4"),
        FTIME_5("FTIME_5"),
        FTIME_6("FTIME_6"),
        FTIME_7("FTIME_7"),
        FTIME_8("FTIME_8"),
        RTIME_1("RTIME_1"),
        RTIME_2("RTIME_2"),
        RTIME_3("RTIME_3"),
        RTIME_4("RTIME_4"),
        RTIME_5("RTIME_5"),
        RTIME_6("RTIME_6"),
        RTIME_7("RTIME_7"),
        RTIME_8("RTIME_8"),
        NTIME("NTIME");

        private final String text;

        private CW_MONTHLY_TM_RATIO(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_MONTHLY_USAGE
    public enum CW_MONTHLY_USAGE implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        T_TIME("T_TIME"),
        S_TIME("S_TIME"),
        DL_FLAG("DL_FLAG"),
        INVALID("INVALID"),
        USAGE_DATE("USAGE_DATE"),
        USAGE_VALUE01("USAGE_VALUE01"),
        USAGE_VALUE02("USAGE_VALUE02"),
        USAGE_VALUE03("USAGE_VALUE03"),
        USAGE_VALUE04("USAGE_VALUE04"),
        USAGE_VALUE05("USAGE_VALUE05"),
        USAGE_VALUE06("USAGE_VALUE06"),
        USAGE_VALUE07("USAGE_VALUE07"),
        USAGE_VALUE08("USAGE_VALUE08"),
        USAGE_VALUE09("USAGE_VALUE09"),
        USAGE_VALUE10("USAGE_VALUE10"),
        USAGE_VALUE11("USAGE_VALUE11");

        private final String text;

        private CW_MONTHLY_USAGE(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_MONTHLY_USAGE_EX
    public enum CW_MONTHLY_USAGE_EX implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        T_TIME("T_TIME"),
        S_TIME("S_TIME"),
        DL_FLAG("DL_FLAG"),
        INVALID("INVALID"),
        USAGE_EX_DATE("USAGE_EX_DATE"),
        USAGE_VALUE12("USAGE_VALUE12"),
        USAGE_VALUE13("USAGE_VALUE13");

        private final String text;

        private CW_MONTHLY_USAGE_EX(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_ODOMETER
    public enum CW_ODOMETER implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        T_TIME("T_TIME"),
        S_TIME("S_TIME"),
        DL_FLAG("DL_FLAG"),
        ODO_TIME("ODO_TIME"),
        ODO_VALUE("ODO_VALUE"),
        STATUS("STATUS");

        private final String text;

        private CW_ODOMETER(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_RECEIVE_MESSAGE
    public enum CW_RECEIVE_MESSAGE implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        PART_NUMBER("PART_NUMBER"),
        SERIAL_NUMBER("SERIAL_NUMBER"),
        MON_SQ("MON_SQ"),
        WRITE_TIME("WRITE_TIME"),
        TIME_FLAG("TIME_FLAG"),
        REPLY_TIME("REPLY_TIME"),
        S_TIME("S_TIME"),
        T_TIME("T_TIME"),
        ACK_OR_REPLY("ACK_OR_REPLY"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        EXP_DATE("EXP_DATE"),
        NOT_ACCORD("NOT_ACCORD"),
        MSG_KIND("MSG_KIND"),
        SERVER_CHARSET("SERVER_CHARSET"),
        KEY_REQUEST("KEY_REQUEST"),
        DISP_NUM("DISP_NUM"),
        DISP_TERMS("DISP_TERMS"),
        GPS_REQUEST("GPS_REQUEST"),
        SMR_REQUEST("SMR_REQUEST"),
        GPS_TIME("GPS_TIME"),
        GPS_MEASURE_TIME("GPS_MEASURE_TIME"),
        GPS_PLACE("GPS_PLACE"),
        GPS_LATITUDE("GPS_LATITUDE"),
        GPS_LONGITUDE("GPS_LONGITUDE"),
        GPS_ENGINE("GPS_ENGINE"),
        GPS_OLD("GPS_OLD"),
        GPS_EVENT("GPS_EVENT"),
        SMR_TIME("SMR_TIME"),
        SMR_VALUE("SMR_VALUE"),
        MSG_LEN("MSG_LEN"),
        REPLY("REPLY"),
        DL_FLAG("DL_FLAG");

        private final String text;

        private CW_RECEIVE_MESSAGE(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_REGENERATION_INFO
    public enum CW_REGENERATION_INFO implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        T_TIME("T_TIME"),
        S_TIME("S_TIME"),
        DL_FLAG("DL_FLAG"),
        REGENERATION_END_TIME("REGENERATION_END_TIME"),
        NEEDED_COUNT("NEEDED_COUNT"),
        INTERRUPTED_COUNT("INTERRUPTED_COUNT"),
        COMPLETED_COUNT("COMPLETED_COUNT"),
        DIVIDED_COUNT("DIVIDED_COUNT"),
        CAUSE_CODE("CAUSE_CODE"),
        REGENERATION_TIME("REGENERATION_TIME"),
        FUEL_CONSUMPTION("FUEL_CONSUMPTION"),
        WARMING_EFFICIENCY("WARMING_EFFICIENCY"),
        FUEL_EFFICIENCY("FUEL_EFFICIENCY"),
        SMR_VALUE("SMR_VALUE"),
        NW1_C1_FUEL_CONSUMPTION("NW1_C1_FUEL_CONSUMPTION"),
        NW1_C1_WARMING_EFFICIENCY("NW1_C1_WARMING_EFFICIENCY"),
        NW1_C1_FUEL_EFFICIENCY("NW1_C1_FUEL_EFFICIENCY");

        private final String text;

        private CW_REGENERATION_INFO(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_SERVICE_METER
    public enum CW_SERVICE_METER implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        TIME_FLAG("TIME_FLAG"),
        T_TIME("T_TIME"),
        S_TIME("S_TIME"),
        SMR_TIME("SMR_TIME"),
        SMR_VALUE("SMR_VALUE"),
        STATUS("STATUS"),
        DL_FLAG("DL_FLAG");

        private final String text;

        private CW_SERVICE_METER(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }

//CW_TOTAL_TORQUE
    public enum CW_TOTAL_TORQUE implements Fields {
        DWH_INSERT_TIME("DWH_INSERT_TIME"),
        DWH_UPDATE_TIME("DWH_UPDATE_TIME"),
        DWH_SCRAP_TIME("DWH_SCRAP_TIME"),
        MAKER_CODE("MAKER_CODE"),
        KIND("KIND"),
        TYPE("TYPE"),
        MACHINE_NUMBER("MACHINE_NUMBER"),
        TIME_FLAG("TIME_FLAG"),
        TORQUE_TIME("TORQUE_TIME"),
        S_TIME("S_TIME"),
        T_TIME("T_TIME"),
        TORQUE_ID("TORQUE_ID"),
        VALUE00("VALUE00"),
        VALUE01("VALUE01"),
        VALUE02("VALUE02"),
        VALUE03("VALUE03"),
        VALUE04("VALUE04"),
        VALUE05("VALUE05"),
        VALUE06("VALUE06"),
        VALUE07("VALUE07"),
        VALUE10("VALUE10"),
        VALUE11("VALUE11"),
        VALUE12("VALUE12"),
        VALUE13("VALUE13"),
        VALUE14("VALUE14"),
        VALUE15("VALUE15"),
        VALUE16("VALUE16"),
        VALUE17("VALUE17"),
        VALUE20("VALUE20"),
        VALUE21("VALUE21"),
        VALUE22("VALUE22"),
        VALUE23("VALUE23"),
        VALUE24("VALUE24"),
        VALUE25("VALUE25"),
        VALUE26("VALUE26"),
        VALUE27("VALUE27"),
        VALUE30("VALUE30"),
        VALUE31("VALUE31"),
        VALUE32("VALUE32"),
        VALUE33("VALUE33"),
        VALUE34("VALUE34"),
        VALUE35("VALUE35"),
        VALUE36("VALUE36"),
        VALUE37("VALUE37"),
        VALUE40("VALUE40"),
        VALUE41("VALUE41"),
        VALUE42("VALUE42"),
        VALUE43("VALUE43"),
        VALUE44("VALUE44"),
        VALUE45("VALUE45"),
        VALUE46("VALUE46"),
        VALUE47("VALUE47"),
        VALUE50("VALUE50"),
        VALUE51("VALUE51"),
        VALUE52("VALUE52"),
        VALUE53("VALUE53"),
        VALUE54("VALUE54"),
        VALUE55("VALUE55"),
        VALUE56("VALUE56"),
        VALUE57("VALUE57"),
        VALUE60("VALUE60"),
        VALUE61("VALUE61"),
        VALUE62("VALUE62"),
        VALUE63("VALUE63"),
        VALUE64("VALUE64"),
        VALUE65("VALUE65"),
        VALUE66("VALUE66"),
        VALUE67("VALUE67"),
        DL_FLAG("DL_FLAG");

        private final String text;

        private CW_TOTAL_TORQUE(final String text) {
            this.text = text;
        }

        public String get() {
            return this.text;
        }
    }
}
