package com.tongji.bruno.gfdl;

import Jama.Matrix;
import Jama.SingularValueDecomposition;
import com.tongji.bruno.gfdl.algorithm.pca.PCA;
import com.tongji.bruno.gfdl.algorithm.ppso.PPSO;
import com.tongji.bruno.gfdl.ppso.tool.FileHelper;
import com.tongji.bruno.gfdl.ppso.tool.ShellHelper;
import ucar.ma2.Array;
import ucar.ma2.ArrayDouble;
import ucar.ma2.Index;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFileWriteable;
import ucar.nc2.Variable;
import ucar.nc2.dataset.NetcdfDataset;

import java.io.File;
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
        Matrix lambdaMatrix = new Matrix(row, 80);
        for(int k = 0; k < 80; k++){
            lambdaMatrix.setMatrix(0, row - 1, k, k, u.getMatrix(0, row - 1, k, k));
        }

        System.out.println("pca finish!");

        PPSO ppso = new PPSO(60, 10, lambdaMatrix);
        List<Matrix> swarmMatrices = ppso.initSwarm();
        List<Matrix> swarmV = ppso.initV();
        Matrix gbest = ppso.seek();

        for(int k = 0; k < 80; k++){
            FileHelper.writeFile(Double.toString(gbest.get(k, 0)), Constants.RESOURCE_PATH  + "best.txt");
        }
        //===================主体======================

//        try{
////            String newFileName = Constants.RESOURCE_PATH + 1 + "/ocean_temp_salt_" + 1 + ".nc";
//            String oldFileName = "D:\\ocean_temp_salt.res.nc";
////        copyFile(fileName, orderFileName, true);
////            NetcdfFileWriteable oldNcfile = NetcdfFileWriteable.openExisting(oldFileName);
//            NetcdfFile oldNcfile = NetcdfDataset.open(oldFileName);
////            NetcdfFileWriteable newNcfile = NetcdfFileWriteable.openExisting(newFileName);
//
//            Dimension xaxis = oldNcfile.getDimensions().get(0);
//            Dimension yaxis = oldNcfile.getDimensions().get(1);
//            Dimension zaxis = oldNcfile.getDimensions().get(2);
//            Dimension time = oldNcfile.getDimensions().get(3);
//            ArrayDouble sstaArray = new ArrayDouble.D4(time.getLength(), zaxis.getLength(), yaxis.getLength(), xaxis.getLength());
//            Index index = sstaArray.getIndex();
//            Variable varBean = oldNcfile.findVariable("temp");
//            Array origin = varBean.read();
//            for(int i = 0; i < 200; i++){
//                for(int j = 0; j < 360; j++){
//                    for(int k = 0; k < 1; k++){
////                        if(k == 0){
////                            Array tem = varBean.read("0:0:1, "+ k + ":" + k + ":1, " + i + ":" + i + ":1, " + j + ":" + j + ":1");
////                            double[] t =  (double[])tem.copyTo1DJavaArray();
////                            double ssta = swarm.get(j * 200 + i, 0);
////                            if(j < 40 || j > 220){
////                                sstaArray.set(index.set(0, k, i, j), t[0]);
////                            } else {
////                                sstaArray.set(index.set(0, k, i, j), t[0] + ssta);
////                            }
////                        } else {
//                            Array tem = varBean.read("0:0:1, " + k + ":" + k + ":1, " + i + ":" + i + ":1, " + j + ":" + j + ":1");
//                            double[] t =  (double[])tem.copyTo1DJavaArray();
//                            sstaArray.set(index.set(0, k, i, j), 0);
////                        }
//                    }
//                }
//            }
//
//
//
//            String overFileName = "D:\\a.nc";
//            NetcdfFileWriteable over = NetcdfFileWriteable.openExisting(overFileName, true);
//            over.write("temp", sstaArray);
//            NetcdfDataset overNcfile = NetcdfDataset.openDataset("D:\\ocean_temp_salt.res.nc");
//
//        } catch (Exception e){
//            e.printStackTrace();
//        }

//        ShellHelper.callScript("command.csh", "2", Constants.RESOURCE_PATH);
//        String tem = ShellHelper.exec("/usr/bin/yhqueue");
//        System.out.println(tem.contains("fr21.csh"));

    }

}
