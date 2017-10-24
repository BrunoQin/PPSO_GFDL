package com.tongji.bruno.gfdl.test;

import Jama.Matrix;
import Jama.SingularValueDecomposition;
import com.tongji.bruno.gfdl.algorithm.pca.PCA;
import com.tongji.bruno.gfdl.ppso.tool.FileHelper;

/**
 * Created by 秦博 on 2017/10/24.
 */
public class PCATest {

    public static void main(String[] args) {

        PCA pca = new PCA();
        SingularValueDecomposition i = pca.getLeftU();
        Matrix u = i.getU();
        int row = u.getRowDimension();
        Matrix lambdaMatrix = new Matrix(row, 156);
        lambdaMatrix.setMatrix(0, row - 1, 0, 155, u.getMatrix(0, row - 1, 0, 155));
        for(int o = 0; o <lambdaMatrix.getColumnDimension(); o++){
            for(int p = 0; p < lambdaMatrix.getRowDimension(); p++){
                FileHelper.writeFile(Double.toString(lambdaMatrix.get(p, o)), "D:\\lambda_156.txt");
            }
            System.out.println(o);
        }
        lambdaMatrix = lambdaMatrix.inverse();
        for(int o = 0; o <lambdaMatrix.getColumnDimension(); o++){
            for(int p = 0; p < lambdaMatrix.getRowDimension(); p++){
                FileHelper.writeFile(Double.toString(lambdaMatrix.get(p, o)), "D:\\inverse_lambda_156.txt");
            }
            System.out.println(o);
        }
        u.print(3, 3);

    }

}
