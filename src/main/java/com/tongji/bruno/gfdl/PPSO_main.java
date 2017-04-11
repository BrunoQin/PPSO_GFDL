package com.tongji.bruno.gfdl;

import Jama.Matrix;
import Jama.SingularValueDecomposition;
import com.tongji.bruno.gfdl.algorithm.pca.PCA;
import com.tongji.bruno.gfdl.algorithm.ppso.PPSO;
import com.tongji.bruno.gfdl.ppso.tool.FileHelper;
import ucar.ma2.Array;
import ucar.ma2.Index;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFileWriteable;
import ucar.nc2.Variable;

import java.io.IOException;
import java.util.List;

/**
 * Created by 秦博 on 2017/3/22.
 */
public class PPSO_main {

    private static final String fileName = "D:\\github\\PPSO_GFDL\\src\\main\\resources\\ssta_100year(all).nc";

    public static void main(String[] args) {
        System.out.println("hello world!");

//        NetcdfFile ncfile = null;
//        try {
//            ncfile = NetcdfFile.open(fileName);
//
//            //read dimensions
//            List<Dimension> list =  ncfile.getDimensions();
//            for(Dimension d : list){
//                System.out.println("name="+d.getName()+" length="+d.getLength());
//            }
//            //read variables
//            List<Variable> variables = ncfile.getVariables();
//            System.out.println();
//            for(Variable v : variables){
//                System.out.println("name="+v.getName()+" NameAndDimension="+v.getNameAndDimensions()+" ElementSize="+v.getElementSize());
//            }
//
//        } catch (IOException ioe) {
//        } finally {
//            if (null != ncfile)
//                try {
//                    ncfile.close();
//                } catch (IOException ioe) {
//                }
//        }

        //===================主体======================
        PCA pca = new PCA();
        SingularValueDecomposition i = pca.getLeftU();

        Matrix u = i.getU();
        int row = u.getRowDimension();
        Matrix lambdaMatrix = new Matrix(row, 80);
        for(int k = 0; k < 80; k++){
            lambdaMatrix.setMatrix(0, row - 1, k, k, u.getMatrix(0, row - 1, k, k));
        }

        PPSO ppso = new PPSO(6, lambdaMatrix);
        List<Matrix> swarmMatrices = ppso.initSwarm();
        List<Matrix> swarmV = ppso.initV();
        for(int k = 0; k < 6; k++){
            FileHelper.prepareFile(k, swarmMatrices.get(k));
        }
        //===================主体======================

//        //===================判断约束的范围======================
//        try{
//            NetcdfFileWriteable ncfile = NetcdfFileWriteable.openExisting("D:\\github\\PPSO_GFDL\\src\\main\\resources\\ssta_100year(all).nc");
//            Variable sst = ncfile.findVariable("ssta");
//            double[][][] march = new double[100][200][360];
//
//            for(int i = 0; i < 100; i++){
//                Array part = sst.read(i * 12 + 2 + ":" + (int)(i * 12 + 2) + ":1, 0:199:1, 0:359:1");
//                Index index = part.reduce().getIndex();
//                double[][] tem = new double[200][360];
//                for(int j = 0; j < 200; j++){
//                    for(int k = 0; k < 360; k++){
//                        tem[j][k] = part.reduce().getDouble(index.set(j, k));
//                    }
//                }
//                march[i] = tem;
//            }
//
//            double[][] sigma = FileHelper.getSigma();
//            double[] lat = FileHelper.getLat();
//            double[] tem = new double[100];
//            double sum = 0;
//            for(int i = 0; i < 100; i++){
//                for(int j = 0; j < 200; j++){
//                    for(int k = 0; k < 100; k++){
//                        tem[i] += Math.pow(Math.cos(lat[j]) * march[i][j][k] / sigma[j][k], 2);
//                    }
//                }
//                sum += Math.sqrt(tem[i]);
//            }
//
//            double max = tem[0];
//            for(int i = 0; i < 100; i++){
//                if(tem[i] > max){
//                    max = tem[i];
//                }
//            }
//
//            for(int i = 0; i < 100; i++){
//                System.out.println(Math.sqrt(tem[i]));
//            }
//
//            System.out.println(sum / 100);
//            System.out.println(Math.sqrt(max));
//
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//        //===================判断约束的范围======================


//        ShellHelper shellHelper = new ShellHelper("112.74.50.130", 22, "root", "Qinbo960415!");
//        String[] com = {"bsub ./fr21.csh"};
//        shellHelper.executeCommands(com);
//        System.out.println(shellHelper.getResponse());
//        shellHelper.disconnect();

    }

}
