package com.tongji.bruno.gfdl;

/**
 * Created by 秦博 on 2017/4/14.
 */
public class Constants {

    public static final String ROOT_PATH = "/BIGDATA1/iocas_mmu_2/PPSO/";
    public static final String RESOURCE_PATH = ROOT_PATH + "record/";
    public static final String DATA_PATH = RESOURCE_PATH + "data/";

    public static final int SWARM_COUNT = 60;
    public static final int MODEL_COUNT = 10;
    public static final int PCA_COUNT = 165;
    public static final int CONSTRAINT = 500;
    public static final double SPEED_RADIO = 0.02;
    public static final int YEAR = 400;
    public static final int STEP = 20;
    public static final int START_MONTH = 0;
    public static final int ROW = 756000;

    public static final int PER_MINLON = 40;
    public static final int PER_MAXLON = 200;
    public static final int PER_MINLAT = 50;
    public static final int PER_MAXLAT = 140;
    public static final int PER_LEVEL = 21;

    public static final int ADA_MINLON = 129;
    public static final int ADA_MAXLON = 189;
    public static final int ADA_MINLAT = 62;
    public static final int ADA_MAXLAT = 130;
    public static final int ADA_MONTH = 11;

    public static final String PCA_PATH = "/BIGDATA1/iocas_mmu_2/GFDL-CM2p1/output/ta400Y_Lev21.nc";
    public static final String PCA_PARAMETER = "ta_jan";
    public static final int PCA_MINLON = 0;
    public static final int PCA_MAXLON = 160;
    public static final int PCA_MINLAT = 20;
    public static final int PCA_MAXLAT = 170;
    public static final int PCA_LEVEL = 10;
    public static final String PCA_U = "u.cp.txt";
    public static final String PCA_S = "s.cp.txt";
    
}
