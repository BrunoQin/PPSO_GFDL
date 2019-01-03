package com.tongji.bruno.gfdl;

import com.tongji.bruno.gfdl.pca.tool.CalculateHelper;
import ucar.ma2.Array;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;
import ucar.nc2.dataset.NetcdfDataset;

/**
 * Created by 秦博 on 2017/10/30.
 */
public class Constraint_test {

    double[] lat;
    double[][][] sigma;

    public static double[] getLat(String fileName){
        try{
            NetcdfFile ncfile = NetcdfFile.open(fileName);
            Variable lat = ncfile.findVariable("yt_ocean");
            double[] tem = (double[]) lat.read().copyToNDJavaArray();
            return tem;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static double[][][] getSigma(String fileName){
        double[][][] sigma = new double[Constants.PER_HEIGHT][Constants.PER_ROW][Constants.PER_COL];
        try {
            NetcdfFile ncfile = NetcdfDataset.open(fileName);

            Variable sst = ncfile.findVariable("std");
            Array part = sst.read("0:" + (Constants.PER_HEIGHT - 1) + ":1, 0:" + (Constants.PER_ROW - 1) + ":1, 0:" + (Constants.PER_COL - 1) + ":1");
            sigma = CalculateHelper.toNormalArray(part);

        } catch (Exception e){
            e.printStackTrace();
        }

        return sigma;

    }

    public void test() {
        try{
            String newFileName = Constants.TEST_FILENAME;
            String oldFileName = Constants.BASE_FILENAME;
            String stdFileName = Constants.STD_FILENAME;
            String latFileName = Constants.STANDARD_FILENAME;
            NetcdfFile oldNcfile = NetcdfFile.open(oldFileName);
            NetcdfFile newNcfile = NetcdfFile.open(newFileName);

            this.lat = getLat(latFileName);
            sigma = getSigma(stdFileName);

            Variable varBean_o = oldNcfile.findVariable(Constants.PSO_PARAMETER);
            Variable varBean_n = newNcfile.findVariable(Constants.PSO_PARAMETER);

            double [][] p = new double[Constants.ROW][1];

            double sum = 0.0;
            for(int i = Constants.PER_MINLAT; i <= Constants.PER_MAXLAT; i++){
                for(int j = Constants.PER_MINLON; j <= Constants.PER_MAXLON; j++){
                    for(int k = 0; k <= Constants.PER_LEVEL; k++){
                        double island = varBean_o.read(Constants.START_MONTH + ":" + Constants.START_MONTH+ ":1, " + (Constants.PER_LEVEL + 1) + ":" + (Constants.PER_LEVEL + 1) + ":1, " + i + ":" + i + ":1, " + j + ":" + j + ":1").getDouble(0);
                        if(island < 9E36 && island > -1E20 && this.sigma[k][i-Constants.PER_MINLAT][j-Constants.PER_MINLON] != 0){
                            Array tem_n = varBean_n.read("0:0:1, " + k + ":" + k + ":1, " + i + ":" + i + ":1, " + j + ":" + j + ":1");
                            Array tem_o = varBean_o.read("0:0:1, " + k + ":" + k + ":1, " + i + ":" + i + ":1, " + j + ":" + j + ":1");
                            double tn =  tem_n.getDouble(0);
                            double tm =  tem_o.getDouble(0);
                            sum += Math.pow(Math.cos(this.lat[i]) * (tn - tm) / this.sigma[k][i-Constants.PER_MINLAT][j-Constants.PER_MINLON], 2);
                        }
                    }
                }
            }

            System.out.println(Math.sqrt(sum));

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Constraint_test().test();
    }

}
