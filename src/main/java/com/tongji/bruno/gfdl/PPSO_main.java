package com.tongji.bruno.gfdl;

import Jama.Matrix;
import Jama.SingularValueDecomposition;
import com.tongji.bruno.gfdl.algorithm.pca.PCA;
import com.tongji.bruno.gfdl.algorithm.ppso.PPSO;
import com.tongji.bruno.gfdl.ppso.tool.FileHelper;
import ucar.ma2.Array;
import ucar.ma2.ArrayDouble;
import ucar.ma2.Index;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFileWriteable;
import ucar.nc2.Variable;

import java.util.List;

/**
 * Created by 秦博 on 2017/3/22.
 */
public class PPSO_main {

    public static void main(String[] args){
        System.out.println("hello world!");

        //===================主体======================
        PCA pca = new PCA();
        SingularValueDecomposition i = pca.getLeftU();

        Matrix u = i.getU();

        int row = u.getRowDimension();
        Matrix lambdaMatrix = new Matrix(row, Constants.PCA_COUNT);
        for(int k = 0; k < Constants.PCA_COUNT; k++){
            lambdaMatrix.setMatrix(0, row - 1, k, k, u.getMatrix(0, row - 1, k, k));
        }

        System.out.println("pca finish!");

        PPSO ppso = new PPSO(60, 10, lambdaMatrix);
        List<Matrix> swarmMatrices = ppso.initSwarm();
        List<Matrix> swarmV = ppso.initV();
        Matrix gbest = ppso.seek();

        for(int k = 0; k < Constants.PCA_COUNT; k++){
            FileHelper.writeFile(Double.toString(gbest.get(k, 0)), Constants.RESOURCE_PATH  + "best.txt");
        }
        //===================主体======================

//        PCA pca = new PCA();
//        SingularValueDecomposition i = pca.getLeftU();
//        Matrix u = i.getU();
//        int row = u.getRowDimension();
//        Matrix lambdaMatrix = new Matrix(row, 156);
//        lambdaMatrix.setMatrix(0, row - 1, 0, 155, u.getMatrix(0, row - 1, 0, 155));
//        for(int o = 0; o <lambdaMatrix.getColumnDimension(); o++){
//            for(int p = 0; p < lambdaMatrix.getRowDimension(); p++){
//                FileHelper.writeFile(Double.toString(lambdaMatrix.get(p, o)), "D:\\lambda_156.txt");
//            }
//            System.out.println(o);
//        }
//        lambdaMatrix = lambdaMatrix.inverse();
//        for(int o = 0; o <lambdaMatrix.getColumnDimension(); o++){
//            for(int p = 0; p < lambdaMatrix.getRowDimension(); p++){
//                FileHelper.writeFile(Double.toString(lambdaMatrix.get(p, o)), "D:\\inverse_lambda_156.txt");
//            }
//            System.out.println(o);
//        }
//        u.print(3, 3);

//        try{
//            String newFileName = "D:\\1_39_ocean.nc";
//            String oldFileName = "D:\\ocean_temp_salt.res.nc";
//            NetcdfFileWriteable oldNcfile = NetcdfFileWriteable.openExisting(oldFileName);
//            NetcdfFileWriteable newNcfile = NetcdfFileWriteable.openExisting(newFileName);
//
//            Dimension xaxis = oldNcfile.getDimensions().get(0);
//            Dimension yaxis = oldNcfile.getDimensions().get(1);
//            Dimension zaxis = oldNcfile.getDimensions().get(2);
//            Dimension time = oldNcfile.getDimensions().get(3);
//
//            ArrayDouble sstaArray = new ArrayDouble.D4(time.getLength(), zaxis.getLength(), yaxis.getLength(), xaxis.getLength());
//            Index index = sstaArray.getIndex();
//            Variable varBean_o = oldNcfile.findVariable("temp");
//            Variable varBean_n = newNcfile.findVariable("temp");
//            for(int i = 0; i < 200; i++){
//                for(int j = 0; j < 360; j++){
//                    for(int k = 0; k < 1; k++){
//                        Array tem_n = varBean_n.read("0:0:1, " + k + ":" + k + ":1, " + i + ":" + i + ":1, " + j + ":" + j + ":1");
//                        Array tem_o = varBean_o.read("0:0:1, " + k + ":" + k + ":1, " + i + ":" + i + ":1, " + j + ":" + j + ":1");
//                        double tn =  tem_n.getDouble(0);
//                        System.out.println(tn);
//                        double tm =  tem_o.getDouble(0);
//                        System.out.println(tm);
//                        if(tn >= 9E36){
//                            tn = 0;
//                        }
//                        if(tn <= -1.0000000200408773E20){
//                            tn = 0;
//                        }
//                        if(tm >= 9E36){
//                            tm = 0;
//                        }
//                        if(tm <= -1.0000000200408773E20)
//                            tm = 0;
////                        System.out.println(tn - tm);
//                        sstaArray.set(index.set(0, k, i, j), tn - tm);
//                    }
//                }
//            }
//
//
//
////            String overFileName = "D:\\a.nc";
//            NetcdfFileWriteable over = NetcdfFileWriteable.openExisting(oldFileName, true);
//            over.write("temp", sstaArray);
////            NetcdfDataset overNcfile = NetcdfDataset.openDataset("D:\\ocean_temp_salt.res.nc");
//
//        } catch (Exception e){
//            e.printStackTrace();
//        }

//        ShellHelper.callScript("command.csh", "2", Constants.RESOURCE_PATH);
//        String tem = ShellHelper.exec("/usr/bin/yhqueue");
//        System.out.println(tem.contains("fr21.csh"));

    }

}
