package com.tongji.bruno.gfdl;

import com.tongji.bruno.gfdl.pca.tool.CalculateHelper;
import ucar.ma2.Array;
import ucar.ma2.ArrayDouble;
import ucar.ma2.Index;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFileWriter;
import ucar.nc2.Variable;
import ucar.nc2.dataset.NetcdfDataset;

import java.util.Random;

import static com.tongji.bruno.gfdl.ppso.tool.FileHelper.copyFile;


public class Confirmation_test {

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

    public double isLegal(ArrayDouble sstaArray) {
        try{
            String cnopFileName = Constants.CNOP_FILENAME;
            String stdFileName = Constants.STD_FILENAME;
            String latFileName = Constants.STANDARD_FILENAME;
            NetcdfFile cnopNcfile = NetcdfFile.open(cnopFileName);

            this.lat = getLat(latFileName);
            sigma = getSigma(stdFileName);

            Variable varBean_o = cnopNcfile.findVariable(Constants.PSO_PARAMETER);
            Index index = sstaArray.getIndex();

            double [][] p = new double[Constants.ROW][1];

            double sum = 0.0;
            for(int i = Constants.PER_MINLAT; i <= Constants.PER_MAXLAT; i++){
                for(int j = Constants.PER_MINLON; j <= Constants.PER_MAXLON; j++){
                    for(int k = 0; k <= Constants.PER_LEVEL; k++){
                        double island = varBean_o.read(Constants.START_MONTH + ":" + Constants.START_MONTH+ ":1, " + (Constants.PER_LEVEL + 1) + ":" + (Constants.PER_LEVEL + 1) + ":1, " + i + ":" + i + ":1, " + j + ":" + j + ":1").getDouble(0);
                        if(island < 9E36 && island > -1E20 && this.sigma[k][i-Constants.PER_MINLAT][j-Constants.PER_MINLON] != 0){
                            Array tem_o = varBean_o.read("0:0:1, " + k + ":" + k + ":1, " + i + ":" + i + ":1, " + j + ":" + j + ":1");
                            double tm =  tem_o.getDouble(0);
                            double tn =  sstaArray.get(index.set(0, k, i, j));
                            sum += Math.pow(Math.cos(this.lat[i]) * (tn - tm) / this.sigma[k][i-Constants.PER_MINLAT][j-Constants.PER_MINLON], 2);
                        }
                    }
                }
            }
            double cur = Math.sqrt(sum);
            System.out.println(cur);
            return cur;

        } catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }


    public void addRandomPerturbation(String fileName){
        try{
            copyFile(Constants.CNOP_FILENAME, fileName, true);
            NetcdfFile oldNcfile = NetcdfFile.open(Constants.CNOP_FILENAME);

            Dimension xaxis = oldNcfile.getDimensions().get(0);
            Dimension time = oldNcfile.getDimensions().get(1);
            Dimension zaxis = oldNcfile.getDimensions().get(2);
            Dimension yaxis = oldNcfile.getDimensions().get(3);
            ArrayDouble sstaArray = new ArrayDouble.D4(time.getLength(), zaxis.getLength(), yaxis.getLength(), xaxis.getLength());
            Index index = sstaArray.getIndex();
            Variable varBean_o = oldNcfile.findVariable(Constants.PSO_PARAMETER);

            int a = Constants.GAUSSIAN_MEAN;
            double b = Constants.GAUSSIAN_VAR;
            Random r = new Random();

            for(int k = 0; k < 50; k++){
                for(int i = 0; i < 200; i++){
                    for(int j = 0; j < 360; j++){
                        Array tem = varBean_o.read(Constants.START_MONTH + ":" + Constants.START_MONTH+ ":1, "+ k + ":" + k + ":1, " + i + ":" + i + ":1, " + j + ":" + j + ":1");
                        double island = varBean_o.read(Constants.START_MONTH + ":" + Constants.START_MONTH+ ":1, " + (Constants.PER_LEVEL + 1) + ":" + (Constants.PER_LEVEL + 1) + ":1, " + i + ":" + i + ":1, " + j + ":" + j + ":1").getDouble(0);
                        if(k <= Constants.PER_LEVEL &&
                                i >= Constants.PER_MINLAT && i <= Constants.PER_MAXLAT &&       // 包含两侧边界
                                j >= Constants.PER_MINLON && j <= Constants.PER_MAXLON &&       // 包含两侧边界
                                island < 9E36 &&
                                island > -1E20){
                            double per = Math.sqrt(b) * r.nextGaussian() + a;
                            sstaArray.set(index.set(0, k, i, j), tem.getDouble(0) + per);
                        } else {
                            sstaArray.set(index.set(0, k, i, j), tem.getDouble(0));
                        }
                    }
                }
            }

            double cur = isLegal(sstaArray);
            NetcdfFile baseFile = NetcdfFile.open(Constants.BASE_FILENAME);
            Variable varBean_b = baseFile.findVariable(Constants.PSO_PARAMETER);

            while (cur > Constants.CONSTRAINT){
                for(int k = 0; k < 50; k++){
                    for(int i = 0; i < 200; i++){
                        for(int j = 0; j < 360; j++){
                            Array tem = varBean_b.read(Constants.START_MONTH + ":" + Constants.START_MONTH+ ":1, "+ k + ":" + k + ":1, " + i + ":" + i + ":1, " + j + ":" + j + ":1");
                            double island = varBean_b.read(Constants.START_MONTH + ":" + Constants.START_MONTH+ ":1, " + (Constants.PER_LEVEL + 1) + ":" + (Constants.PER_LEVEL + 1) + ":1, " + i + ":" + i + ":1, " + j + ":" + j + ":1").getDouble(0);
                            if(k <= Constants.PER_LEVEL &&
                                    i >= Constants.PER_MINLAT && i <= Constants.PER_MAXLAT &&       // 包含两侧边界
                                    j >= Constants.PER_MINLON && j <= Constants.PER_MAXLON &&       // 包含两侧边界
                                    island < 9E36 &&
                                    island > -1E20){
                                double tem_b =  tem.getDouble(0);
                                sstaArray.set(index.set(0, k, i, j), tem_b + (sstaArray.get(index.set(0, k, i, j)) - tem_b) * (Constants.CONSTRAINT / cur));
                            } else {
                                sstaArray.set(index.set(0, k, i, j), sstaArray.get(index.set(0, k, i, j)));
                            }
                        }
                    }
                }
                cur = isLegal(sstaArray);
            }
            oldNcfile.close();
            baseFile.close();

            NetcdfFileWriter ncfile = NetcdfFileWriter.openExisting(fileName);
            Variable varBean = ncfile.findVariable(Constants.PSO_PARAMETER);

            System.out.println("start prepare " + fileName);
            ncfile.write(varBean, sstaArray);
            ncfile.close();
            System.out.println("finish prepare " + fileName);

        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {

        for(int i = 0; i < Constants.COUNT; i++){
            new Confirmation_test().addRandomPerturbation(Constants.DATA_PATH + "perturbation/ocean_temp_salt_" + i + ".nc");
        }

    }

}
