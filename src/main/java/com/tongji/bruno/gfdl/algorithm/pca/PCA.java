package com.tongji.bruno.gfdl.algorithm.pca;

import Jama.Matrix;
import Jama.SingularValueDecomposition;
import com.tongji.bruno.gfdl.pca.tool.CalculateHelper;
import com.tongji.bruno.gfdl.pca.tool.FileHelper;
import ucar.ma2.Array;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 秦博 on 2017/4/1.
 */
public class PCA {

    private static final int YEAR = 300;

    private SingularValueDecomposition leftU;

    public PCA(){

        List<FileHelper> fileHelperList = new ArrayList<FileHelper>();
        List<double[][]> averageList = new ArrayList<double[][]>();

        for(int i = 0; i < YEAR; i++){
            System.out.print(i + " begin!");
            FileHelper fileHelper =  new FileHelper(i * 12);
            fileHelperList.add(fileHelper);
        }

        for(int i = 0; i < YEAR; i++){
            double[][] average;
//            average = CalculateHelper.calAverage(fileHelperList.get(i).getMonthArrays());
            average = CalculateHelper.toNormalArray(fileHelperList.get(i).getSingleMonthArray().reduce());

            averageList.add(average);
        }

        this.leftU = CalculateHelper.compose(averageList, YEAR);

    }

    public SingularValueDecomposition getLeftU() {
        return leftU;
    }

    public void setLeftU(SingularValueDecomposition leftU) {
        this.leftU = leftU;
    }
}
