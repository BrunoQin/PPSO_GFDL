package com.tongji.bruno.gfdl.test;

import Jama.Matrix;
import com.tongji.bruno.gfdl.ppso.tool.FileHelper;
import ucar.ma2.Array;
import ucar.ma2.Index;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

import java.io.IOException;
import java.util.List;

/**
 * Created by Bruno on 23/03/2018.
 */
public class SSIMTest {

    public static void main(String[] args) {

        SSIMTest ssimTest = new SSIMTest();
        NetcdfFile ncfile = null;
        List<Matrix> outputMatrix = FileHelper.readRestartFile();
        try {

            ncfile = NetcdfFile.open("/Users/macbookpro/Desktop/02810101.ocean_month.nc");
            //处理restart文件获得adaptValue
            //计算（sst-sst'）平方求和 该值即为适应度值
            try{
                for(int p = 0; p < 60; p++){
                    Variable sst = ncfile.findVariable("sst");
                    Array part = sst.read(p + ":" + p + ":1, 0:199:1, 0:359:1");
                    Index index = part.reduce().getIndex();

                    double adapt = 0;
                    double[][] ssta = new double[68][160];
                    for(int j = 62; j < 130; j++){
                        for(int k = 40; k < 200; k++){
                            if(part.getDouble(index.set(j, k)) >= 9E36 || part.getDouble(index.set(j, k)) <= -1E20){
                                adapt += Math.pow(0 - outputMatrix.get(p % 12).get(j, k), 2);
                                ssta[j - 62][k - 40] = 0;
                            } else {
                                adapt += Math.pow(part.getDouble(index.set(j, k)) - outputMatrix.get(p % 12).get(j, k), 2);
                                ssta[j - 62][k - 40] = part.getDouble(index.set(j, k));
                            }

                        }
                    }

                    double[][] sstaave = new double[68][160];
                    for(int j = 62; j < 130; j++){
                        for(int k = 40; k < 200; k++){
                            if(outputMatrix.get(p % 12).get(j, k) >= 9E36 || outputMatrix.get(p % 12).get(j, k) <= -1E20){
                                sstaave[j - 62][k - 40] = 0;
                            } else {
                                sstaave[j - 62][k - 40] = outputMatrix.get(p % 12).get(j, k);
                            }
                        }
                    }

                    double[][] de = new double[68][160];
                    for(int j = 0; j < 68; j++){
                        for(int k = 0; k < 160; k++){
                            de[j][k] = ssta[j][k] - sstaave[j][k];
                        }
                    }

                    Matrix sstaM = new Matrix(ssta);
                    Matrix sstaaveM = new Matrix(sstaave);
                    Matrix sstaDe = new Matrix(de);
                    double avea = ssimTest.getAve(sstaM);
                    double aveb = ssimTest.getAve(sstaaveM);
                    double sigmaa = ssimTest.getSigma(sstaM, avea);
                    double sigmab = ssimTest.getSigma(sstaaveM, aveb);
                    double cov = ssimTest.getCov(sstaM, sstaaveM, avea, aveb);
                    double cona = (ssimTest.getMax(sstaM) - ssimTest.getMin(sstaM)) / ssimTest.getMax(sstaM);
                    double conb = (ssimTest.getMax(sstaaveM) - ssimTest.getMin(sstaaveM)) / ssimTest.getMax(sstaaveM);
                    double conde = (ssimTest.getMax(sstaDe) - ssimTest.getMin(sstaDe)) / (ssimTest.getMax(sstaDe) + 5);

                    double u = 1;
                    double k1 = 0.01;
                    double k2 = 0.03;
                    double c1 = Math.pow(u * k1, 2);
                    double c2 = Math.pow(u * k2, 2);
                    double c3 = c2 / 2;

                    double light = (2 * avea * aveb + c1) / ((Math.pow(avea, 2) + Math.pow(aveb, 2) + c1));
                    double contrast = (2 * sigmaa * sigmab + c2) / ((Math.pow(sigmaa, 2) + Math.pow(sigmab, 2) + c2));
                    double structure = (cov + c3) / (sigmaa * sigmab + c3);


                    double ssim = ((2 * avea * aveb + c1) * (2 * cov + c2)) /
                            ((Math.pow(avea, 2) + Math.pow(aveb, 2) + c1) * (Math.pow(sigmaa, 2) + Math.pow(sigmab, 2) + c2));

//                    System.out.println(0 - Math.pow(light, 1) * Math.pow(contrast, 10) * Math.pow(structure, 20));
                    System.out.println(adapt);
//                    System.out.println(p + "适应度值为:" + adapt + ",结构差异为:" + ssim);
//                    System.out.println(p + "适应度值为:" + adapt + ",亮度为:" + light + ",对比度为:" + contrast + ",结构为:" + structure + ",结构差异为:" + ssim + ",乘积为:" + light * contrast * structure);
//                    System.out.println(p + "适应度值为:" + adapt + ",结构为:" + structure + ",结构差异为:" + ssim + ",乘积为:" + light * contrast * structure);
//                    System.out.println(avea);
//                    System.out.println(aveb);
//                    System.out.println(sigmaa);
//                    System.out.println(sigmab);
//                    System.out.println(cov);
                }

            } catch (Exception e){
                e.printStackTrace();
            }

        } catch (IOException ioe) {

        }

    }

    public double getAve(Matrix a){
        int row = a.getRowDimension();
        int col = a.getColumnDimension();
        double sum = 0.0;
        int count = 1;
        for(int i = 0; i < row; i++){
            for(int j = 0; j < col; j++){
                sum += a.get(i, j);
                count++;
            }
        }
        return sum / count;
    }

    public double getSigma(Matrix a, double ave){
        int row = a.getRowDimension();
        int col = a.getColumnDimension();
        double sum = 0.0;
        int count = 0;
        for(int i = 0; i < row; i++){
            for(int j = 0; j < col; j++){
                sum += Math.pow(a.get(i, j) - ave, 2);
                count++;
            }
        }
        return Math.sqrt(sum / count);
    }

    public double getCov(Matrix a, Matrix b, double avea, double aveb){
        int row = a.getRowDimension();
        int col = a.getColumnDimension();
        double sum = 0.0;
        int count = 0;
        for(int i = 0; i < row; i++){
            for(int j = 0; j < col; j++){
                sum += (a.get(i, j) - avea) * (b.get(i, j) - aveb);
                count++;
            }
        }
        return sum / count;
    }

    public double getMax(Matrix a){
        double max = 0;
        int row = a.getRowDimension();
        int col = a.getColumnDimension();
        for(int i = 0; i < row; i++){
            for(int j = 0; j < col; j++){
                if(a.get(i, j) > max){
                    max = a.get(i, j);
                }
            }
        }
        return max;
    }

    public double getMin(Matrix a){
        double min = 1000;
        int row = a.getRowDimension();
        int col = a.getColumnDimension();
        for(int i = 0; i < row; i++){
            for(int j = 0; j < col; j++){
                if(a.get(i, j) < min && a.get(i, j) != 0){
                    min = a.get(i, j);
                }
            }
        }
        return min;
    }

}
