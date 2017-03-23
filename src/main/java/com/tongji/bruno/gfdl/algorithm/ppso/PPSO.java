package com.tongji.bruno.gfdl.algorithm.ppso;

import Jama.Matrix;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 秦博 on 2017/3/22.
 */
public class PPSO {

    private static final int PCACOUNT = 80;
    private static final double BESTADAPT = 0.0;
    private static final int STEP = 10;

    private Matrix lambdaMatrix;

    private int swarmCount; //粒子数量
    private int dimension; //粒子矩阵行数
    private List<Matrix> swarmMatrices; //粒子群当前位置
    private List<Matrix> swarmPBest; //粒子个体极值位置
    private double[] swarmPBestValue; //粒子群个体极值
    private List<Matrix> swarmV; //粒子速度
    private Matrix swarmGBest; //粒子群体极值位置
    private double swarmGBestValue; //粒子群群体极值

    private double c1 = 0.8, c2 = 0.8;
    private double w = 2;


    public PPSO(int swarmCount){
        this.swarmCount = swarmCount;
        // todo
        this.lambdaMatrix = Matrix.random(dimension, PCACOUNT);
        //this.dimension = lambdaMatrix.getRowDimension();
    }

    /**
     * 初始化生成一系列初始低维粒子矩阵
     * @return
     */
    public List<Matrix> init(){

        this.swarmMatrices = new ArrayList<Matrix>();
        for(int i = 0; i < this.swarmCount; i++){
            this.swarmMatrices.add(Matrix.random(PCACOUNT, 1));
        }

        return this.swarmMatrices;

    }

    public Matrix seek(){

        //初始化粒子个体极值
        this.swarmPBest = new ArrayList<Matrix>();
        this.swarmPBest = this.swarmMatrices;
        this.swarmPBestValue = new double[swarmCount];
        for(int i = 0; i < this.swarmMatrices.size(); i++){
            this.swarmPBestValue[i] = adaptValue(this.swarmMatrices.get(i));
        }

        //初始化群体极值
        int index = getMaxIndex(this.swarmPBestValue);
        this.swarmGBestValue = this.swarmPBestValue[index];
        this.swarmGBest = this.swarmPBest.get(index);

        //初始化粒子速度
        this.swarmV = new ArrayList<Matrix>();
        for(int i = 0; i < this.swarmCount; i++){
            this.swarmV.add(Matrix.random(PCACOUNT, 1));
        }

        return null;
    }

    public double adaptValue(Matrix swarm){
        return 2.0;
    }

    public void advanceStep(int index){

        Matrix v = this.swarmV.get(index).times(w)
                .plus((this.swarmPBest.get(index).minus(this.swarmMatrices.get(index))).times((Math.random() * c1)))
                .plus((this.swarmGBest.minus(this.swarmMatrices.get(index))).times((Math.random() * c1)));
        this.swarmV.set(index, v);
        this.swarmMatrices.set(index, this.swarmMatrices.get(index).plus(v));

    }

    public int getMaxIndex(double[] arr){
        int maxIndex = 0;
        for(int i = 0; i < arr.length; i++){
            if(arr[maxIndex] < arr[i]){
                maxIndex = i;
            }
        }
        return maxIndex;
    }

}
