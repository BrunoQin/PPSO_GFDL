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

    public static SingularValueDecomposition compose(List<double[][][]> averageList, int year){
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

        return composedMatrix.svd();
    }

    public static double[][][] toNormalArray(Array data){

        double[][][] sstaMatrix;

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
                    }
                }
            }
        }
        return sstaMatrix;
    }

}
