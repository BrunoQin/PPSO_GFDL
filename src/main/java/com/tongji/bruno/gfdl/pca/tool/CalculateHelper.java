package com.tongji.bruno.gfdl.pca.tool;

import Jama.Matrix;
import Jama.SingularValueDecomposition;
import ucar.ma2.Array;
import ucar.ma2.Index;

import java.util.List;

/**
 * Created by 秦博 on 2017/3/21.
 */
public class CalculateHelper {

    public static double[][] calAverage(List<Array> monthArrays){
        int[] shape = monthArrays.get(0).reduce().getShape();
        double[][] sum = new double[shape[0]][shape[1]];
        Matrix sumMatrix = new Matrix(sum);
        for(int i = 0; i < monthArrays.size(); i++){
            Matrix subMatrix = new Matrix(cal(monthArrays.get(i)));
            sumMatrix = sumMatrix.plus(subMatrix);
        }
        sumMatrix = sumMatrix.times((double)1/monthArrays.size());
        sum = sumMatrix.getArray();
        return sum;
    }

    public static double[][] compose(List<double[][]> averageList, int year){
        int row = averageList.get(0).length;
        int col = averageList.get(0)[0].length;
        double[][] composedArray = new double[row * col][year];
        Matrix composedMatrix = new Matrix(composedArray);
        for(int i = 0; i < averageList.size(); i++){
            for(int j = 0; j < row; j++){
                for(int k = 0; k < col; k++){
                    composedMatrix.set(k * row + j, i, averageList.get(i)[j][k]);
                }
            }
        }
        composedArray = composedMatrix.getArray();
        SingularValueDecomposition i = composedMatrix.svd();
//        i.getS().print(4, 6);
//        i.getV().print(4, 6);
        i.getU().print(4, 6);
        return composedArray;
    }

    private static double[][] cal(Array data){

        double[][] sstaMatrix;

        data = data.reduce();
        int[] shape = data.getShape();
        Index index = data.getIndex();
        sstaMatrix = new double[shape[0]][shape[1]];
        for (int i=0; i < shape[0]; i++) {
            for (int j = 0; j < shape[1]; j++) {
                double ssta = data.getDouble(index.set(i, j));
                sstaMatrix[i][j] = ssta;
            }
        }
        return sstaMatrix;
    }

}
