package com.tongji.bruno.gfdl.test;

import Jama.Matrix;

/**
 * Created by 秦博 on 2017/10/30.
 */
public class ConstraintTest {

    public static void main(String[] args) {
        double [][] array = {
                {-1,1,0},
                {-4,3,0},
                {1 ,0,2}};
        //定义一个矩阵
        Matrix A = new Matrix(array);

        //由特征值组成的对角矩阵
        A.eig().getD().print(4,2);
        //每一列对应的是一个特征向量
        A.eig().getV().print(4,2);
    }

}
