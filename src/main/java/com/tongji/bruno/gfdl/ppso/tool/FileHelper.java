package com.tongji.bruno.gfdl.ppso.tool;

import Jama.Matrix;
import com.tongji.bruno.gfdl.Constants;
import com.tongji.bruno.gfdl.pca.tool.CalculateHelper;
import ucar.ma2.Array;
import ucar.ma2.ArrayDouble;
import ucar.ma2.Index;
import ucar.nc2.*;
import ucar.nc2.dataset.NetcdfDataset;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 秦博 on 2017/3/22.
 */
public class FileHelper {

    private static final String fileName = Constants.DATA_PATH + "ocean_temp_salt.res.nc";
    private static final String RESTART_FILENAME = Constants.DATA_PATH + "63.nc";
    private static final String STD_FILENAME = Constants.DATA_PATH + "std.nc";
    private static final String PARAMETER = "temp";

    /**
     * 将原文件拷贝成新文件，并将粒子矩阵写回新文件
     * @param order
     * @param swarm
     * @return
     */
    public static String prepareFile(int order, Matrix swarm){
        try{

            String orderFileName = Constants.RESOURCE_PATH + order + "/ocean_temp_salt_" + order + ".nc";
            copyFile(fileName, orderFileName, true);
            NetcdfFile oldNcfile = NetcdfFile.open(fileName);

            Dimension xaxis = oldNcfile.getDimensions().get(0);
            Dimension time = oldNcfile.getDimensions().get(1);
            Dimension zaxis = oldNcfile.getDimensions().get(2);
            Dimension yaxis = oldNcfile.getDimensions().get(3);
            ArrayDouble sstaArray = new ArrayDouble.D4(time.getLength(), zaxis.getLength(), yaxis.getLength(), xaxis.getLength());
            Index index = sstaArray.getIndex();
            Variable varBean_o = oldNcfile.findVariable(PARAMETER);
            for(int k = 0; k < 50; k++){
                for(int i = 0; i < 200; i++){
                    for(int j = 0; j < 360; j++){
                        Array tem = varBean_o.read("0:0:1, "+ k + ":" + k + ":1, " + i + ":" + i + ":1, " + j + ":" + j + ":1");
                        if(k < 11 && i >= 20 && i < 170 && j >= 40 && j < 200 && varBean_o.read("0:0:1, " + "11:11:1, " + i + ":" + i + ":1, " + j + ":" + j + ":1").getDouble(0) <= 9E36 && varBean_o.read("0:0:1, " + "21:21:1, " + i + ":" + i + ":1, " + j + ":" + j + ":1").getDouble(0) > -1E20){
                            double ssta = swarm.get(k * 200 * 180 + (j - 40) * 200 + i, 0);
                            sstaArray.set(index.set(0, k, i, j), tem.getDouble(0) + ssta);
                        } else {
                            sstaArray.set(index.set(0, k, i, j), tem.getDouble(0));
                        }
                    }
                }
            }
            oldNcfile.close();

            NetcdfFileWriter ncfile = NetcdfFileWriter.openExisting(orderFileName);
            Variable varBean = ncfile.findVariable(PARAMETER);
//            NetcdfFileWriteable over = NetcdfFileWriteable.openExisting(orderFileName, true);
            System.out.println("start prepare " + orderFileName);
            ncfile.write(varBean, sstaArray);
            ncfile.close();
            System.out.println("finish prepare " + orderFileName);

            return orderFileName;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    public static double[][][] getSigma(){
        double[][][] sigma = new double[21][200][120];
        try {
            NetcdfFile ncfile = NetcdfDataset.open(STD_FILENAME);

            Variable sst = ncfile.findVariable("std");
            Array part = sst.read("0:10:1, 0:149:1, 0:159:1");
            sigma = CalculateHelper.toNormalArray(part);

        } catch (Exception e){
            e.printStackTrace();
        }


        return sigma;

    }

    public static double[] getLat(){
        try{
            NetcdfFile ncfile = null;
            ncfile = NetcdfFile.open(RESTART_FILENAME);
            Variable lat = ncfile.findVariable("yt_ocean");
            double[] tem = (double[]) lat.read().copyToNDJavaArray();
            return tem;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static List<Matrix> readRestartFile(){
        try{
            List<Matrix> sst_ave = new ArrayList<>();
            NetcdfFileWriteable ncfile = NetcdfFileWriteable.openExisting(RESTART_FILENAME);

            for(int p = 0; p < 12; p++){
                Variable sst = ncfile.findVariable("sst");
                Array part = sst.read(p + ":" + p + ":1, 0:199:1, 0:359:1");
                double[][] temp = new double[200][360];
                Index index = part.reduce().getIndex();
                for(int i = 0; i < 200; i++){
                    for(int j = 0; j < 360; j++){
                        //读取数据
                        if(part.reduce().getDouble(index.set(i, j)) >= 9E36 || part.reduce().getDouble(index.set(i, j)) <= -1E20){
                            temp[i][j] = 0;
                        } else {
                            temp[i][j] = part.reduce().getDouble(index.set(i, j));
                        }
                    }
                }
                sst_ave.add(new Matrix(temp));
            }
            ncfile.close();
            return sst_ave;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static boolean copyFile(String srcFileName, String destFileName, boolean overlay) {
        File srcFile = new File(srcFileName);

        if (!srcFile.exists()) {
            System.out.println(srcFileName + "not exist!");
            return false;
        } else if (!srcFile.isFile()) {
            System.out.println(srcFileName + "not a file!");
            return false;
        }

        File destFile = new File(destFileName);
        if (destFile.exists()) {
            if (overlay) {
                new File(destFileName).delete();
            }
        } else {
            if (!destFile.getParentFile().exists()) {
                if (!destFile.getParentFile().mkdirs()) {
                    return false;
                }
            }
        }

        int byteRead = 0;
        InputStream in = null;
        OutputStream out = null;

        try {
            in = new FileInputStream(srcFile);
            out = new FileOutputStream(destFile);
            byte[] buffer = new byte[1024];

            while ((byteRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, byteRead);
            }
            return true;
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        } finally {
            try {
                if (out != null)
                    out.close();
                if (in != null)
                    in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void writeFile(String str, String path)
    {
        try
        {
            File file = new File(path);
            if(!file.exists())
                file.createNewFile();
            FileOutputStream out = new FileOutputStream(file,true);
            StringBuffer sb = new StringBuffer();
            sb.append(str + "\r\n");
            out.write(sb.toString().getBytes("utf-8"));
            out.close();
        }
        catch(IOException ex)
        {
            System.out.println(ex.getStackTrace());
        }
    }

    public static double[] readFile(String path){
        List<String> list = new ArrayList<String>();
        try
        {
            File file = new File(path);
            if(!file.exists())
                file.createNewFile();
            InputStreamReader read = new InputStreamReader(
                    new FileInputStream(file), "utf-8");
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt = null;
            lineTxt = bufferedReader.readLine();
            while (lineTxt != null) {
                list.add(lineTxt);
                lineTxt = bufferedReader.readLine();
            }
            bufferedReader.close();
            read.close();
        }
        catch(IOException ex)
        {
            System.out.println(ex.getStackTrace());
        }

        double[] num = new double[Constants.PCA_COUNT];
        for(int i = 0; i < list.size(); i = i + 2){
            num[i / 2] = Double.parseDouble(list.get(i));
        }
        return num;

    }

    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);

        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                System.out.println("delete" + fileName + " success!");
                return true;
            } else {
                System.out.println("delete" + fileName + " fail!");
                return false;
            }
        } else {
            System.out.println("delete:" + fileName + " not exist!");
            return false;
        }
    }

    public static boolean deleteDirectory(String sPath) {

        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);

        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        boolean flag = true;

        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) break;
            }
            else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前目录
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean createDir(String destDirName) {
        File dir = new File(destDirName);
        if (dir.exists()) {
            System.out.println("exist!");
            return false;
        }
        if (!destDirName.endsWith(File.separator)) {
            destDirName = destDirName + File.separator;
        }
        if (dir.mkdirs()) {
            System.out.println("create success!" + destDirName);
            return true;
        } else {
            System.out.println("create fail!");
            return false;
        }
    }

    public static double getAverage(double[] array){
        int sum = 0;
        for(int i = 0; i < array.length; i++){
            sum += array[i];
        }
        return (double)(sum / array.length);
    }

    public static double getStandardDeviation(double[] array){
        double sum = 0.0;
        double average = getAverage(array);
        for(int i = 0;i < array.length;i++){
            sum += Math.pow((array[i] - average), 2);
        }
        return Math.sqrt(sum / array.length);
    }

    public static String getOceanOutputFileName(int order) {
        String path = Constants.ROOT_PATH + order + "/CM2.1p1/history/"; // 路径
        File f = new File(path);
        if (!f.exists()) {
            System.out.println(path + " not exists");
            return null;
        }

        File fa[] = f.listFiles();
        for (int i = 0; i < fa.length; i++) {
            File fs = fa[i];
            if(fs.getName().contains("ocean")){
                return fs.getName();
            }
        }
        return null;
    }

    public static String getAtmosOutputFileName(int order) {
        String path = Constants.ROOT_PATH + order + "/CM2.1p1/history/"; // 路径
        File f = new File(path);
        if (!f.exists()) {
            System.out.println(path + " not exists");
            return null;
        }

        File fa[] = f.listFiles();
        for (int i = 0; i < fa.length; i++) {
            File fs = fa[i];
            if(fs.getName().contains("atmos")){
                return fs.getName();
            }
        }
        return null;
    }

}
