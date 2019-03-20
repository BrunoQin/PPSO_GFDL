package com.tongji.bruno.gfdl;

import Jama.Matrix;
import Jama.SingularValueDecomposition;
import com.tongji.bruno.gfdl.algorithm.pca.PCA;
import com.tongji.bruno.gfdl.ppso.tool.FileHelper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Bruno on 2018/10/27.
 */
public class PCA_main {

    public static void main(String[] args){
        System.out.println("hello world!");

        //===================主体======================
        PCA pca = new PCA();
        SingularValueDecomposition i = pca.processPCA();
//        SingularValueDecomposition i = pca._processPCA();
        Matrix u = i.getU();
        Matrix s = i.getS();
        int row_s = s.getRowDimension();
        int col_s = s.getColumnDimension();
        System.out.println(row_s == col_s);
        double sum = 0;
        for(int o = 0; o < row_s; o++){
            double num = s.get(o, o);
            FileHelper.writeFile(Double.toString(num), Constants.DATA_PATH + Constants.PCA_S);
            sum += num;
        }
        System.out.println(sum);
        int row_u = u.getRowDimension();
        int col_u = u.getColumnDimension();
        System.out.println(row_u);
        System.out.println(col_u);
        Matrix lambdaMatrix = new Matrix(row_u, col_u);
        lambdaMatrix.setMatrix(0, row_u - 1, 0, col_u - 1, u.getMatrix(0, row_u - 1, 0, col_u - 1));
        File file = new File(Constants.DATA_PATH + Constants.PCA_U);  //存放数组数据的文件
        FileWriter out = null;  //文件写入流
        try {
            out = new FileWriter(file);
            for(int o = 0; o <lambdaMatrix.getColumnDimension(); o++){
                for(int p = 0; p < lambdaMatrix.getRowDimension(); p++){
                    out.write(Double.toString(lambdaMatrix.get(p, o)) + "\t");
                }
                out.write("\r\n");
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //===================主体======================

    }

}
