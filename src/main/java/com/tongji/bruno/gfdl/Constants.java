package com.tongji.bruno.gfdl;

/**
 * Created by 秦博 on 2017/4/14.
 */
public class Constants {

    // 实验基础路径设置
    public static final String ROOT_PATH            =   "/BIGDATA1/iocas_mmu_2/PPSO/";
    public static final String RESOURCE_PATH        =   ROOT_PATH + "record/";
    public static final String DATA_PATH            =   RESOURCE_PATH + "data/";

    // 实验类型设置(重要)
    public static final String EXP_NAME             =   "ep";

    // 基础实验方案设置
    private static int MINLON, MAXLON, MINLAT, MAXLAT, LEVEL;
    private static int CAL_MINLON, CAL_MAXLON, CAL_MINLAT, CAL_MAXLAT, CAL_MONTH;
    private static int MAGNITUDE;
    static{
        if(EXP_NAME == "cp"){
            MINLON          =       0;
            MAXLON          =       160;
            MINLAT          =       20;
            MAXLAT          =       170;
            LEVEL           =       10;
            CAL_MINLON      =       79;
            CAL_MAXLON      =       129;
            CAL_MINLAT      =       62;
            CAL_MAXLAT      =       129;
            CAL_MONTH       =       11;
            MAGNITUDE       =       500;
        } else if (EXP_NAME == "ep"){
            MINLON          =       0;
            MAXLON          =       160;
            MINLAT          =       50;
            MAXLAT          =       140;
            LEVEL           =       20;
            CAL_MINLON      =       129;
            CAL_MAXLON      =       189;
            CAL_MINLAT      =       62;
            CAL_MAXLAT      =       129;
            CAL_MONTH       =       11;
            MAGNITUDE       =       270;
        } else {
            System.out.println("ERROR!");
            System.exit(0);
        }
    }

    // PCA相关参数设置
    public static final String PCA_PATH             =   DATA_PATH + "ta400Y_Lev21.nc";
    public static final String PCA_PARAMETER        =   "ta_jan";
    public static final int YEAR                    =   400;
    public static final int PCA_MINLON              =   MINLON;
    public static final int PCA_MAXLON              =   MAXLON;
    public static final int PCA_MINLAT              =   MINLAT;
    public static final int PCA_MAXLAT              =   MAXLAT;
    public static final int PCA_LEVEL               =   LEVEL;
    public static final String PCA_U                =   "u." + EXP_NAME + ".txt";
    public static final String PCA_S                =   "s." + EXP_NAME + ".txt";

    // 添加扰动相关参数设置
    public static final int PER_MINLON              =   MINLON + 40;
    public static final int PER_MAXLON              =   MAXLON + 40;
    public static final int PER_MINLAT              =   MINLAT;
    public static final int PER_MAXLAT              =   MAXLAT;
    public static final int PER_LEVEL               =   LEVEL;
    public static final int PER_ROW                 =   PER_MAXLAT - PER_MINLAT + 1;
    public static final int PER_COL                 =   PER_MAXLON - PER_MINLON + 1;
    public static final int PER_HEIGHT              =   PER_LEVEL + 1;

    // 计算适应度值相关参数设置
    public static final int ADA_MINLON              =   CAL_MINLON;
    public static final int ADA_MAXLON              =   CAL_MAXLON;
    public static final int ADA_MINLAT              =   CAL_MINLAT;
    public static final int ADA_MAXLAT              =   CAL_MAXLAT;
    public static final int ADA_MONTH               =   CAL_MONTH;

    // 算法主体相关参数设置
    public static final String STANDARD_FILENAME    =   DATA_PATH + "167.nc";
    public static final String STD_FILENAME         =   DATA_PATH + "std." + EXP_NAME + ".nc";
    public static final int SWARM_COUNT             =   100;
    public static final int MODEL_COUNT             =   10;
    public static final int PCA_COUNT               =   330;        // 95%
    public static final int CONSTRAINT              =   MAGNITUDE;
    public static final double SPEED_RADIO          =   0.02;
    public static final int STEP                    =   20;
    public static final int START_MONTH             =   0;
    public static final int ROW                     =   PER_HEIGHT * PER_ROW * PER_COL;
}
