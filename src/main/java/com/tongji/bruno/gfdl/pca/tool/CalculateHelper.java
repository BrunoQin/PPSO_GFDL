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
            Matrix subMatrix = new Matrix(toNormalArray(monthArrays.get(i)));
            sumMatrix = sumMatrix.plus(subMatrix);
        }
        sumMatrix = sumMatrix.times((double)1/monthArrays.size());
        sum = sumMatrix.getArray();
        return sum;
    }

    public static SingularValueDecomposition compose(List<double[][]> averageList, int year){
        int row = averageList.get(0).length;
        int col = averageList.get(0)[0].length;
        double[][] composedArray = new double[row * col][year];
        Matrix composedMatrix = new Matrix(composedArray);
        for(int i = 0; i < averageList.size(); i++){
            for(int j = 0; j < row; j++){
                for(int k = 0; k < col; k++){
                    if(averageList.get(i)[j][k] >= 9E36){
                        averageList.get(i)[j][k] = 0;
                    }
                    composedMatrix.set(k * row + j, i, averageList.get(i)[j][k]);
                }
            }
        }

//        System.out.println(averageList.get(0)[0][0]);
//        System.out.println(composedMatrix.getRowDimension());
//        System.out.println(composedMatrix.getColumnDimension());

        return composedMatrix.svd();
    }

    public static double[][] toNormalArray(Array data){

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
