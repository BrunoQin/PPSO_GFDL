package com.tongji.bruno.gfdl.pca.tool;

import Jama.Matrix;
import ucar.ma2.Array;
import ucar.ma2.Index;

import java.util.List;

/**
 * Created by 秦博 on 2017/3/21.
 */
public class CalculateHelper {

//    public static double[][] calAverage(List<Array> monthArrays){
//        int[] shape = monthArrays.get(0).reduce().getShape();
//        double[][] sum = new double[shape[0]][shape[1]];
//        Matrix sumMatrix = new Matrix(sum);
//        for(int i = 0; i < monthArrays.size(); i++){
//            Matrix subMatrix = new Matrix(toNormalArray(monthArrays.get(i)));
//            sumMatrix = sumMatrix.plus(subMatrix);
//        }
//        sumMatrix = sumMatrix.times((double)1/monthArrays.size());
//        sum = sumMatrix.getArray();
//        return sum;
//    }

    public static Matrix compose(List<double[][][]> averageList, int year){
        int depth = averageList.get(0).length;
        int row = averageList.get(0)[0].length;
        int col = averageList.get(0)[0][0].length;
        double[][] composedArray = new double[depth * row * col][year];
        Matrix composedMatrix = new Matrix(composedArray);
        for(int i = 0; i < averageList.size(); i++){
            for(int j = 0; j < depth; j++){
                for(int k = 0; k < row; k++){
                    for(int l = 0; l < col; l++){
                        composedMatrix.set(j * row * col + l * row + k, i, averageList.get(i)[j][k][l]);
                    }
                }
            }
        }

        return composedMatrix;
    }

    public static double[][][] toNormalArray(Array data){

        double[][][] sstaMatrix;
        double sum = 0.0;
        int count = 0;

        data = data.reduce();
        int[] shape = data.getShape();
        Index index = data.getIndex();
        sstaMatrix = new double[shape[0]][shape[1]][shape[2]];
        for (int i = 0; i < shape[0]; i++) {
            for (int j = 0; j < shape[1]; j++) {
                for (int k = 0; k < shape[2]; k++) {
                    if(data.getDouble(index.set(i, j, k)) >= 9E36){
                        sstaMatrix[i][j][k] = 0;
                    }else{
                        sstaMatrix[i][j][k] = data.getDouble(index.set(i, j, k));
                        sum += sstaMatrix[i][j][k];
                        count++;
                    }
                }
            }
        }

        double ave = sum / (double)count;
        for (int i = 0; i < shape[0]; i++) {
            for (int j = 0; j < shape[1]; j++) {
                for (int k = 0; k < shape[2]; k++) {
                    if(data.getDouble(index.set(i, j, k)) >= 9E36){
                        continue;
                    }else{
                        sstaMatrix[i][j][k] -= ave;
                    }
                }
            }
        }

        return sstaMatrix;
    }

}
