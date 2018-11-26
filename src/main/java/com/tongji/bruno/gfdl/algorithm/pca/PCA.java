package com.tongji.bruno.gfdl.algorithm.pca;

import Jama.Matrix;
import Jama.SingularValueDecomposition;
import com.tongji.bruno.gfdl.Constants;
import com.tongji.bruno.gfdl.pca.tool.CalculateHelper;
import com.tongji.bruno.gfdl.pca.tool.FileHelper;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 秦博 on 2017/4/1.
 */
public class PCA {

    private static final int YEAR = Constants.YEAR;
    private static final String filename = Constants.DATA_PATH + Constants.PCA_U;
    private double[][] pca = new double[Constants.PCA_COUNT][Constants.ROW];  //读取出的数组

    public SingularValueDecomposition processPCA(){

        List<FileHelper> fileHelperList = new ArrayList<FileHelper>();
        List<double[][][]> averageList = new ArrayList<double[][][]>();

        for(int i = 0; i < YEAR; i++){
            System.out.print(i + " begin!");
            FileHelper fileHelper =  new FileHelper(i);
            fileHelperList.add(fileHelper);
        }

        for(int i = 0; i < YEAR; i++){
            double[][][] average;
            average = CalculateHelper.toNormalArray(fileHelperList.get(i).getSingleMonthArray().reduce());

            averageList.add(average);
        }

        return CalculateHelper.compose(averageList, YEAR);
    }

    public SingularValueDecomposition _processPCA(){
        try {
            NetcdfFile file_no_nan = NetcdfFile.open(Constants.PCA_NO_NAN_PATH);
            Variable ta = file_no_nan.findVariable(Constants.PCA_NO_NAN_PARAMETER);
            float[][] taFloatArray = (float[][]) ta.read().copyToNDJavaArray();
            double[][] taDoubleArray = new double[Constants.YEAR][Constants.PCA_EP_COL];
            for(int i = 0; i < taFloatArray.length; i++){
                for(int j = 0; j < taFloatArray[0].length; j++){
                    double tem = taFloatArray[i][j];
                    taDoubleArray[i][j] = tem;
                }
            }
            Matrix ssta = new Matrix(taDoubleArray);
            System.out.println("row is " + ssta.getRowDimension());
            System.out.println("col is " + ssta.getColumnDimension());
            return ssta.transpose().svd();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public double[][] getPCA(){
        File file = new File(filename);  //存放数组数据的文件
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));
            String line;  //一行数据
            //逐行读取，并将每个数组放入到数组中
            for(int i = 0; i < Constants.PCA_COUNT; i++){
                line = in.readLine();
                String[] temp = line.split("\t");
                for (int j = 0; j < temp.length; j++) {
                    pca[i][j] = Double.parseDouble(temp[j]);
                }
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pca;

    }

}
