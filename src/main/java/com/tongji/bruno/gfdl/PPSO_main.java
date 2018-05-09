package com.tongji.bruno.gfdl;

import Jama.Matrix;
import com.tongji.bruno.gfdl.algorithm.pca.PCA;
import com.tongji.bruno.gfdl.algorithm.ppso.PPSO;
import com.tongji.bruno.gfdl.ppso.tool.FileHelper;

import java.util.List;

/**
 * Created by 秦博 on 2017/3/22.
 */
public class PPSO_main {

    public static void main(String[] args){
        System.out.println("hello world!");

        //===================主体======================
        PCA pca = new PCA();

        Matrix lambdaMatrix = new Matrix(pca.getPCA());
        lambdaMatrix = lambdaMatrix.transpose();
        System.out.println("pca finish!");
        PPSO ppso = new PPSO(30, 10, lambdaMatrix);
        List<Matrix> swarmMatrices = ppso.initSwarm();
        List<Matrix> swarmV = ppso.initV();
        Matrix gbest = ppso.seek();

        for(int k = 0; k < Constants.PCA_COUNT; k++){
            FileHelper.writeFile(Double.toString(gbest.get(k, 0)), Constants.RESOURCE_PATH  + "best.txt");
        }
        //===================主体======================

    }

}
