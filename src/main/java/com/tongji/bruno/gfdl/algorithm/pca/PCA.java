package com.tongji.bruno.gfdl.algorithm.pca;

import com.tongji.bruno.gfdl.pca.tool.CalculateHelper;
import com.tongji.bruno.gfdl.pca.tool.FileHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 秦博 on 2017/4/1.
 */
public class PCA {

    private static final int YEAR = 100;

    private double[][] composedArray;

    public PCA(){

        List<FileHelper> fileHelperList = new ArrayList<FileHelper>();
        List<double[][]> averageList = new ArrayList<double[][]>();

        for(int i = 0; i < YEAR; i++){
            FileHelper fileHelper =  new FileHelper(i);
            fileHelperList.add(fileHelper);
        }

        for(int i = 0; i < YEAR; i++){
            double[][] average;
            average = CalculateHelper.calAverage(fileHelperList.get(i).getMonthArrays());
            averageList.add(average);
        }

        this.composedArray = CalculateHelper.compose(averageList, YEAR);

    }

    public double[][] getComposedArray() {
        return composedArray;
    }

    public void setComposedArray(double[][] composedArray) {
        this.composedArray = composedArray;
    }
}
