package com.tongji.bruno.gfdl.algorithm.ppso;

import Jama.Matrix;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 秦博 on 2017/3/22.
 */
public class PPSO {

    private Matrix lambdaMatrix;

    private int swarmCount;
    private int dimension;

    public PPSO(int swarmCount, int dimension){
        this.swarmCount = swarmCount;
        this.dimension = dimension;
        // todo
        this.lambdaMatrix = Matrix.random(dimension, 80);
    }

    /**
     * 初始化生成一系列初始低维粒子矩阵
     * @return
     */
    public List<Matrix> init(){

        List<Matrix> swarmMatrices = new ArrayList<Matrix>();
        for(int i = 0; i < swarmCount; i++){
            swarmMatrices.add(Matrix.random(dimension, 1));
        }

        return swarmMatrices;

    }



}
