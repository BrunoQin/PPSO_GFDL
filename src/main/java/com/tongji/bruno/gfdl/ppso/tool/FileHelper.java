package com.tongji.bruno.gfdl.ppso.tool;

import Jama.Matrix;
import ucar.ma2.Array;
import ucar.ma2.ArrayDouble;
import ucar.ma2.Index;
import ucar.nc2.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 秦博 on 2017/3/22.
 */
public class FileHelper {

    private static final String fileName = "D:\\github\\PPSO_GFDL\\src\\main\\resources\\ocean_temp_salt.res.nc";
    private static final String newName = "D:\\github\\PPSO_GFDL\\src\\main\\resources\\ocean_temp_salt_";
    private static final String RESTART_FILENAME = "D:\\github\\PPSO_GFDL\\src\\main\\resources\\ocean_temp_salt.res.nc";
    private static final String PARAMETER = "temp";

    /**
     * 将原文件拷贝成新文件，并将粒子矩阵写回新文件
     * @param order
     * @param swarm
     * @return
     */
    public static String prepareFile(int order, Matrix swarm){
        try{
            String orderFileName = newName + order + ".nc";
            copyFile(fileName, orderFileName, true);
            NetcdfFileWriteable ncfile = NetcdfFileWriteable.openExisting(orderFileName);

            Dimension xaxis = ncfile.getDimensions().get(0);
            Dimension yaxis = ncfile.getDimensions().get(1);
            Dimension zaxis = ncfile.getDimensions().get(2);
            Dimension time = ncfile.getDimensions().get(3);
            ArrayDouble sstaArray = new ArrayDouble.D4(time.getLength(), zaxis.getLength(), yaxis.getLength(), xaxis.getLength());
            Index index = sstaArray.getIndex();
            Variable varBean = ncfile.findVariable(PARAMETER);
            Array origin = varBean.read();
            for(int i = 0; i < yaxis.getLength(); i++){
                for(int j = 0; j < xaxis.getLength(); j++){
                    Array tem = varBean.read("0:0:1, 0:0:1, " + i + ":" + i + ":1, " + j + ":" + j + ":1");
                    double[] k =  (double[])tem.copyTo1DJavaArray();
//                    System.out.println(k[0]);
                    double ssta = swarm.get(j * 200 + i, 0);
                    sstaArray.set(index.set(0, 0, i, j), k[0] + ssta);
                }
            }

            ncfile.write(PARAMETER, sstaArray);

            return orderFileName;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    public static double[][] getSigma(){

        try{
            NetcdfFileWriteable ncfile = NetcdfFileWriteable.openExisting("D:\\github\\PPSO_GFDL\\src\\main\\resources\\ssta_100year(all).nc");
            Variable sst = ncfile.findVariable("ssta");
            double[][][] march = new double[100][200][360];

            for(int i = 0; i < 100; i++){
                Array part = sst.read(i * 12 + 2 + ":" + (int)(i * 12 + 2) + ":1, 0:199:1, 0:359:1");
                Index index = part.reduce().getIndex();
                double[][] tem = new double[200][360];
                for(int j = 0; j < 200; j++){
                    for(int k = 0; k < 360; k++){
                        tem[j][k] = part.reduce().getDouble(index.set(j, k));
                    }
                }
                march[i] = tem;
            }

            double[][] sigma = new double[200][360];
            for(int i = 0; i < 200; i++){
                for(int j = 0; j < 360; j++){
                    double[] tem = new double[100];
                    for(int k = 0; k < 100; k++){
                        tem[k] = march[k][i][j];
                    }
                    sigma[i][j] = getStandardDevition(tem);
                }
            }

            return sigma;

        } catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    public static double[] getLat(){
        try{
            NetcdfFile ncfile = null;
            ncfile = NetcdfFile.open("D:\\github\\PPSO_GFDL\\src\\main\\resources\\ssta_100year(all).nc");
            Variable lat = ncfile.findVariable("yt_ocean");
            double[] tem = (double[]) lat.read().copyToNDJavaArray();
            return tem;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static Matrix readRestartFile(){
        try{
            NetcdfFileWriteable ncfile = NetcdfFileWriteable.openExisting(RESTART_FILENAME);

            Dimension xaxis = ncfile.getDimensions().get(0);
            Dimension yaxis = ncfile.getDimensions().get(1);
            Dimension zaxis = ncfile.getDimensions().get(2);
            Dimension time = ncfile.getDimensions().get(3);
            ArrayDouble sstaArray = new ArrayDouble.D4(time.getLength(), zaxis.getLength(), yaxis.getLength(), xaxis.getLength());
            double[][]  temp = new double[yaxis.getLength()][xaxis.getLength()];
            Index index = sstaArray.getIndex();
            for(int i = 0; i < yaxis.getLength(); i++){
                for(int j = 0; j < xaxis.getLength(); j++){
                    //读取第九个月的数据 todo
                    temp[i][j] = sstaArray.get(index.set(8, 0, i, j));
                }
            }

            return new Matrix(temp);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static boolean copyFile(String srcFileName, String destFileName, boolean overlay) {
        File srcFile = new File(srcFileName);

        if (!srcFile.exists()) {
            System.out.println("源文件：" + srcFileName + "不存在！");
            return false;
        } else if (!srcFile.isFile()) {
            System.out.println("复制文件失败，源文件：" + srcFileName + "不是一个文件！");
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
            FileOutputStream out = new FileOutputStream(file,true); //如果追加方式用true
            StringBuffer sb = new StringBuffer();
            sb.append(str + "\r\n");
            out.write(sb.toString().getBytes("utf-8"));//注意需要转换对应的字符集
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
                    new FileInputStream(file), "utf-8");// 考虑到编码格式
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

        double[] num = new double[80];
        for(int i = 0; i < list.size(); i = i + 2){
            num[i / 2] = Double.parseDouble(list.get(i));
        }
        return num;

    }

    public static double getAverage(double[] array){
        int sum = 0;
        for(int i = 0; i < array.length; i++){
            sum += array[i];
        }
        return (double)(sum / array.length);
    }

    public static double getStandardDevition(double[] array){
        double sum = 0;
        double average = getAverage(array);
        for(int i = 0;i < array.length;i++){
            sum += Math.sqrt(((double)array[i] - average) * (array[i] - average));
        }
        return (sum / (array.length - 1));
    }
}
