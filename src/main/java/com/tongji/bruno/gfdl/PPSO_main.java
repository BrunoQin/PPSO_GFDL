package com.tongji.bruno.gfdl;

import Jama.Matrix;
import Jama.SingularValueDecomposition;
import com.tongji.bruno.gfdl.algorithm.pca.PCA;
import com.tongji.bruno.gfdl.algorithm.ppso.PPSO;
import com.tongji.bruno.gfdl.ppso.tool.FileHelper;
import com.tongji.bruno.gfdl.ppso.tool.ShellHelper;
import com.tongji.bruno.gfdl.ppso.tool.ThreadHelper;
import ucar.ma2.Array;
import ucar.ma2.ArrayDouble;
import ucar.ma2.Index;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFileWriteable;
import ucar.nc2.Variable;

import java.io.*;
import java.util.List;

/**
 * Created by 秦博 on 2017/3/22.
 */
public class PPSO_main {

    public static void main(String[] args){
        System.out.println("hello world!");

        //===================主体======================
//        PCA pca = new PCA();
//        SingularValueDecomposition i = pca.getLeftU();
//
//        Matrix u = i.getU();
//
//        int row = u.getRowDimension();
//        Matrix lambdaMatrix = new Matrix(row, 80);
//        for(int k = 0; k < 80; k++){
//            lambdaMatrix.setMatrix(0, row - 1, k, k, u.getMatrix(0, row - 1, k, k));
//        }
//
//        System.out.println("pca finish!");
//
//        PPSO ppso = new PPSO(54, 6, lambdaMatrix);
//        List<Matrix> swarmMatrices = ppso.initSwarm();
//        List<Matrix> swarmV = ppso.initV();
//        Matrix gbest = ppso.seek();
//
//        for(int k = 0; k < 80; k++){
//            FileHelper.writeFile(Double.toString(gbest.get(k, 0)), Constants.RESOURCE_PATH  + "best.txt");
//        }
        //===================主体======================

//        try{
//        String newFileName = "D:\\b.nc";
//        String oldFileName = "D:\\a.nc";
////        copyFile(fileName, orderFileName, true);
//        NetcdfFileWriteable oldNcfile = NetcdfFileWriteable.openExisting(oldFileName);
//        NetcdfFileWriteable newNcfile = NetcdfFileWriteable.openExisting(newFileName);
//
//        Dimension xaxis = oldNcfile.getDimensions().get(0);
//        Dimension yaxis = oldNcfile.getDimensions().get(1);
//        Dimension zaxis = oldNcfile.getDimensions().get(2);
//        Dimension time = oldNcfile.getDimensions().get(3);
//        ArrayDouble sstaArray = new ArrayDouble.D4(time.getLength(), zaxis.getLength(), yaxis.getLength(), xaxis.getLength());
//        Index index = sstaArray.getIndex();
//        Variable varBean_o = oldNcfile.findVariable("temp");
//        Variable varBean_n = newNcfile.findVariable("temp");
//
//        for(int i = 0; i < 200; i++){
//            for(int j = 0; j < 360; j++){
//                for(int k = 0; k < 50; k++){
////                    if(k == 0){
////                        Array tem = varBean_o.read("0:0:1, "+ k + ":" + k + ":1, " + i + ":" + i + ":1, " + j + ":" + j + ":1");
////                        double[] t =  (double[])tem.copyTo1DJavaArray();
////                        System.out.println(t[0]);
////                        if(j < 40 || j > 220){
////                            sstaArray.set(index.set(0, k, i, j), t[0]);
////                        } else {
////                            sstaArray.set(index.set(0, k, i, j), t[0] + 1);
////                        }
////                    } else {
//                        Array tem_o = varBean_o.read("0:0:1, " + k + ":" + k + ":1, " + i + ":" + i + ":1, " + j + ":" + j + ":1");
//                        Array tem_n = varBean_n.read("0:0:1, " + k + ":" + k + ":1, " + i + ":" + i + ":1, " + j + ":" + j + ":1");
//                        double[] t =  (double[])tem_o.copyTo1DJavaArray();
//                        double[] r =  (double[])tem_n.copyTo1DJavaArray();
//                        sstaArray.set(index.set(0, k, i, j), t[0] - r[0]);
////                    }
//                }
//            }
//        }
//
//            String overFileName = "D:\\c.nc";
//            NetcdfFileWriteable overNcfile = NetcdfFileWriteable.openExisting(overFileName);
//            overNcfile.write("temp", sstaArray);
//
//        } catch (Exception e){
//            e.printStackTrace();
//        }

        String tem = ShellHelper.exec("/usr/bin/yhqueue");
        System.out.println(tem);
        System.out.println(tem.contains("fr21.csh"));
        new PPSO_main().exec("yhq");
        ShellHelper.callScript("yhq", "", "/HOME/iocas_mmu_2/BIGDATA/90/exp");
        tem = ShellHelper.exec("ls");
        System.out.println(tem);
        System.out.println("...");

    }

    public String executeLinuxCmd(String cmd) {
        System.out.println("got cmd job : " + cmd);
        Runtime run = Runtime.getRuntime();
        try {
            Process process = run.exec(cmd);
            InputStream in = process.getInputStream();
            BufferedReader bs = new BufferedReader(new InputStreamReader(in));
            // System.out.println("[check] now size \n"+bs.readLine());
            String result = null;
            while (in.read() != -1) {
                result = bs.readLine();
                System.out.println("job result [" + result + "]");
            }
            in.close();
            // process.waitFor();
            process.destroy();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object exec(String cmd) {
        try {
            String[] cmdA = { "/bin/sh", "-c", cmd };
            Process process = Runtime.getRuntime().exec(cmdA);
            LineNumberReader br = new LineNumberReader(new InputStreamReader(
                    process.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
