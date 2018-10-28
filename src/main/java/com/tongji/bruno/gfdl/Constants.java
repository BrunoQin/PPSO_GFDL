package com.tongji.bruno.gfdl;

/**
 * Created by 秦博 on 2017/4/14.
 */
public class Constants {

    public static final String ROOT_PATH = "/BIGDATA1/iocas_mmu_2/PPSO/";
    public static final String RESOURCE_PATH = ROOT_PATH + "record/";
    public static final String DATA_PATH = RESOURCE_PATH + "data/";

    public static final String EXP_NAME = "cp";

    private static int MINLON, MAXLON, MINLAT, MAXLAT, LEVEL;
    static{
        if(EXP_NAME == "cp"){
            MINLON = 0;
            MAXLON = 160;
            MINLAT = 20;
            MAXLAT = 170;
            LEVEL = 10;
        } else if (EXP_NAME == "ep"){
            MINLON = 0;
            MAXLON = 160;
            MINLAT = 50;
            MAXLAT = 140;
            LEVEL = 20;
        } else {
            System.out.println("ERROR!");
            System.exit(0);
        }
    }

    public static final String PCA_PATH = "/BIGDATA1/iocas_mmu_2/GFDL-CM2p1/output/ta400Y_Lev21.nc";
    public static final String PCA_PARAMETER = "ta_jan";
    public static final int YEAR = 400;
    public static final int PCA_MINLON = MINLON;
    public static final int PCA_MAXLON = MAXLON;
    public static final int PCA_MINLAT = MINLAT;
    public static final int PCA_MAXLAT = MAXLAT;
    public static final int PCA_LEVEL = LEVEL;
    public static final String PCA_U = "u." + EXP_NAME + ".txt";
    public static final String PCA_S = "s." + EXP_NAME + ".txt";

    public static final int PER_MINLON = MINLON + 40;
    public static final int PER_MAXLON = MAXLON + 40;
    public static final int PER_MINLAT = MINLAT;
    public static final int PER_MAXLAT = MAXLAT;
    public static final int PER_LEVEL = LEVEL;
    public static final int PER_ROW = PER_MAXLAT - PER_MINLAT + 1;
    public static final int PER_COL = PER_MAXLON - PER_MINLON + 1;
    public static final int PER_HIGHT = PER_LEVEL + 1;

    public static final int ADA_MINLON = 129;
    public static final int ADA_MAXLON = 189;
    public static final int ADA_MINLAT = 62;
    public static final int ADA_MAXLAT = 130;
    public static final int ADA_MONTH = 11;

    public static final int SWARM_COUNT = 60;
    public static final int MODEL_COUNT = 10;
    public static final int PCA_COUNT = 165;
    public static final int CONSTRAINT = 500;
    public static final double SPEED_RADIO = 0.02;
    public static final int STEP = 20;
    public static final int START_MONTH = 0;
    public static final int ROW = PER_HIGHT * PER_ROW * PER_COL;
}
