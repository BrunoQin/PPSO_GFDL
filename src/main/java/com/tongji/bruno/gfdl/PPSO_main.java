package com.tongji.bruno.gfdl;

import Jama.Matrix;
import Jama.SingularValueDecomposition;
import com.tongji.bruno.gfdl.algorithm.pca.PCA;
import com.tongji.bruno.gfdl.algorithm.ppso.PPSO;
import com.tongji.bruno.gfdl.ppso.tool.FileHelper;

import java.util.List;

/**
 * Created by 秦博 on 2017/3/22.
 */
public class PPSO_main {

    private static final String fileName = "D:\\github\\PPSO_GFDL\\src\\main\\resources\\ocean_month.nc";

    public static void main(String[] args) {
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

        PPSO ppso = new PPSO(6, lambdaMatrix);
        List<Matrix> swarmMatrices = ppso.initSwarm();
        List<Matrix> swarmV = ppso.initV();
        Matrix gbest = ppso.seek();

        for(int k = 0; k < 80; k++){
            FileHelper.writeFile(Double.toString(gbest.get(k, 0)), Constants.RESOURCE_PATH  + "best.txt");
        }

        //===================主体======================

    }

}
