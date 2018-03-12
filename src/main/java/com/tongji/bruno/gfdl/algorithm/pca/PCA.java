package com.tongji.bruno.gfdl.algorithm.pca;

import Jama.Matrix;
import com.tongji.bruno.gfdl.Constants;
import com.tongji.bruno.gfdl.pca.tool.CalculateHelper;
import com.tongji.bruno.gfdl.pca.tool.FileHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 秦博 on 2017/4/1.
 */
public class PCA {

    private static final int YEAR = Constants.YEAR;
//    private static final String filename = Constants.DATA_PATH + "u.txt";

    private Matrix samples;

//    double[][] pca = new double[Constants.PCA_COUNT][Constants.ROW];  //读取出的数组

    public PCA(){

        List<FileHelper> fileHelperList = new ArrayList<FileHelper>();
        List<double[][][]> averageList = new ArrayList<double[][][]>();

        for(int i = 0; i < YEAR; i++){
            System.out.print(i + " begin!");
            FileHelper fileHelper =  new FileHelper(i * 12);
            fileHelperList.add(fileHelper);
        }

        for(int i = 0; i < YEAR; i++){
            double[][][] average;
            average = CalculateHelper.toNormalArray(fileHelperList.get(i).getSingleMonthArray().reduce());

            averageList.add(average);
        }

        this.samples = CalculateHelper.compose(averageList, YEAR);

    }

    public Matrix getSamples() {
        return samples;
    }

//    public double[][] getPCA(){
//        File file = new File(filename);  //存放数组数据的文件
//        BufferedReader in = null;
//        try {
//            in = new BufferedReader(new FileReader(file));
//            String line;  //一行数据
//            //逐行读取，并将每个数组放入到数组中
//            for(int i = 0; i < Constants.PCA_COUNT; i++){
//                line = in.readLine();
//                String[] temp = line.split("\t");
//                for (int j = 0; j < temp.length; j++) {
//                    pca[i][j] = Double.parseDouble(temp[j]);
//                }
//            }
//            in.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return pca;
//
//    }

}
