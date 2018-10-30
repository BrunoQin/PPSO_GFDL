package com.tongji.bruno.gfdl.test;

import com.tongji.bruno.gfdl.Constants;
import com.tongji.bruno.gfdl.ppso.tool.FileHelper;
import ucar.ma2.Array;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;
import ucar.nc2.dataset.NetcdfDataset;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 秦博 on 2017/12/4.
 */
public class STDTest {

    public static void main(String[] args) {

        String fileName = Constants.DATA_PATH + "ta300Y_Lev21.nc";
        String parameter = "ta";

        List<double[][][]> data = new ArrayList<double[][][]>();

        try {
            NetcdfFile ncfile = NetcdfDataset.open(fileName);

            Variable sst = ncfile.findVariable(parameter);
//            for(int i = 0; i < 3600; i++){
//                Array part = sst.read(i + ":" + i + ":1, 0:20:1, 0:199:1, 0:179:1");
//                data.add(CalculateHelper.toNormalArray(part.reduce()));
//            }
//            ncfile.close();

            double[][][] sigma = new double[21][200][180];

            for(int i = 0; i < 21; i++){
                for(int j = 0; j < 200; j++){
                    for(int k = 0; k < 180; k++){
                        double[] tem = new double[3600];
                        for(int l = 0; l < 3600; l++){
//                            tem[l] = data.get(l)[i][j][k];
                            Array part = sst.read(l + ":" + l + ":1," + i + ":" + i + ":1," + j + ":" + j + ":1," + k + ":" + k + ":1,");
                            if(part.getDouble(0) >= 9E36){
                                tem[l] = 0;
                            }else{
                                tem[l] = part.getDouble(0);
                            }
                        }
                        sigma[i][j][k] = FileHelper.getStandardDeviation(tem);
                        System.out.println("第" + i * 200 * 180 + k * 200 + j + "个" + sigma[i][j][k]);
                    }
                }
            }
            ncfile.close();

            File file = new File(Constants.RESOURCE_PATH + "sigma.txt");  //存放数组数据的文件
            FileWriter out = null;  //文件写入流
            try {
                out = new FileWriter(file);

                for(int i = 0; i < 21; i++){
                    for(int j = 0; j < 200; j++){
                        for(int k = 0; k < 180; k++){
                            out.write(Double.toString(sigma[i][j][k]) + "\r\n");
                        }
                    }
                }

                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


        } catch (Exception e){
            e.printStackTrace();
        }

    }

}
