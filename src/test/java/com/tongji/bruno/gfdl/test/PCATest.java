package com.tongji.bruno.gfdl.test;

import Jama.Matrix;
import Jama.SingularValueDecomposition;
import com.tongji.bruno.gfdl.Constants;
import com.tongji.bruno.gfdl.algorithm.pca.PCA;
import com.tongji.bruno.gfdl.ppso.tool.FileHelper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by 秦博 on 2017/10/24.
 */
public class PCATest {

    public static void main(String[] args) {

//        PCA pca = new PCA();
//        SingularValueDecomposition i = pca.getLeftU();
//        Matrix u = i.getU();
//        Matrix s = i.getS();
//        int row_s = s.getRowDimension();
//        int col_s = s.getColumnDimension();
//        System.out.println(row_s == col_s);
//        double sum = 0;
//        for(int o = 0; o < row_s; o++){
//            double num = s.get(o, o);
//            FileHelper.writeFile(Double.toString(num), Constants.RESOURCE_PATH + "s.txt");
//            sum += num;
//            if(o % 100 == 0){
//                System.out.println(o);
//            }
//        }
//        System.out.println(sum);
//        double tem = 0;
//        int index = 0;
//        for(int o = 0; o < row_s; o++){
//            double num = s.get(o, o);
//            tem += num;
//            if(tem / sum >= 0.8){
//                System.out.println(o);
//                index = o;
//            }
//        }
//        int row_u = u.getRowDimension();
//        System.out.println(row_u);
//        Matrix lambdaMatrix = new Matrix(row_u, index);
//        lambdaMatrix.setMatrix(0, row_u - 1, 0, index - 1, u.getMatrix(0, row_u - 1, 0, index - 1));
//        File file = new File(Constants.RESOURCE_PATH + "u.txt");  //存放数组数据的文件
//        FileWriter out = null;  //文件写入流
//        try {
//            out = new FileWriter(file);
//            for(int o = 0; o <lambdaMatrix.getColumnDimension(); o++){
//                for(int p = 0; p < lambdaMatrix.getRowDimension(); p++){
//                    out.write(Double.toString(lambdaMatrix.get(p, o)) + "\t");
//                }
//                out.write("\r\n");
//            }
//            out.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

}
