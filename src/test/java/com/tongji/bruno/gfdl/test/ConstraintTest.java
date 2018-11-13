package com.tongji.bruno.gfdl.test;

import Jama.Matrix;
import com.tongji.bruno.gfdl.Constants;
import com.tongji.bruno.gfdl.pca.tool.CalculateHelper;
import ucar.ma2.Array;
import ucar.ma2.ArrayDouble;
import ucar.ma2.Index;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFileWriter;
import ucar.nc2.Variable;
import ucar.nc2.dataset.NetcdfDataset;

/**
 * Created by 秦博 on 2017/10/30.
 */
public class ConstraintTest {

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

    public double isLegal(Matrix p){
        double sum = 0.0;
        for(int i = 0; i < Constants.PER_HEIGHT; i++){
            for(int j = 0; j < Constants.PER_ROW; j++){
                for(int k = 0; k < Constants.PER_COL; k++){
                    if(this.sigma[i][j][k] != 0){
                        sum += Math.pow(Math.cos(this.lat[j + Constants.PER_MINLAT]) * p.get(i * Constants.PER_ROW * Constants.PER_COL + k * Constants.PER_ROW + j, 0) / this.sigma[i][j][k], 2);
                    }
                }
            }
        }
        return Math.sqrt(sum);
    }

    public void test() {
        try{
            String newFileName = "/Users/macbookpro/Desktop/13_3_origin.nc";
            String oldFileName = "/Users/macbookpro/Desktop/ocean_temp_salt.63.nc";
            String stdFileName = "/Users/macbookpro/Desktop/std.ep.nc";
            String latFileName = "/Users/macbookpro/Desktop/167.nc";
            NetcdfFile oldNcfile = NetcdfFile.open(oldFileName);
            NetcdfFileWriter newNcfile = NetcdfFileWriter.openExisting(newFileName);

            this.lat = getLat(latFileName);
            sigma = getSigma(stdFileName);

            Dimension xaxis = newNcfile.getNetcdfFile().getDimensions().get(0);
            Dimension yaxis = newNcfile.getNetcdfFile().getDimensions().get(1);
            Dimension zaxis = newNcfile.getNetcdfFile().getDimensions().get(2);
            Dimension time = newNcfile.getNetcdfFile().getDimensions().get(3);

            System.out.println(xaxis);
            System.out.println(time);
            System.out.println(zaxis);
            System.out.println(yaxis);

            ArrayDouble sstaArray = new ArrayDouble.D4(time.getLength(), zaxis.getLength(), yaxis.getLength(), xaxis.getLength());

            Index index = sstaArray.getIndex();
            Variable varBean_o = oldNcfile.findVariable("temp");
            Variable varBean_n = newNcfile.findVariable("temp");

            double [][] p = new double[Constants.ROW][1];

            for(int i = Constants.PER_MINLAT; i < Constants.ADA_MAXLAT; i++){
                for(int j = Constants.PER_MINLON; j < Constants.PER_MAXLON; j++){
                    for(int k = 0; k < Constants.PER_LEVEL; k++){
                        Array tem_n = varBean_n.read("0:0:1, " + k + ":" + k + ":1, " + i + ":" + i + ":1, " + j + ":" + j + ":1");
                        Array tem_o = varBean_o.read("0:0:1, " + k + ":" + k + ":1, " + i + ":" + i + ":1, " + j + ":" + j + ":1");
                        double tn =  tem_n.getDouble(0);
                        double tm =  tem_o.getDouble(0);
                        p[k * Constants.PER_ROW * Constants.PER_COL + j * Constants.PER_ROW + i][0] = tn - tm;
                    }
                }
            }

            double islegal = isLegal(new Matrix(p));

            System.out.println(islegal);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ConstraintTest().test();
    }

}
