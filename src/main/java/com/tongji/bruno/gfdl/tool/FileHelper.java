package com.tongji.bruno.gfdl.tool;

import Jama.Matrix;
import ucar.ma2.ArrayDouble;
import ucar.ma2.Index;
import ucar.nc2.*;

import java.io.*;
import java.util.List;

/**
 * Created by 秦博 on 2017/3/22.
 */
public class FileHelper {

    private static final String fileName = "D:\\github\\PPSO_GFDL\\src\\main\\resources\\ocean_temp_salt.res.nc";
    private static final String newName = "D:\\github\\PPSO_GFDL\\src\\main\\resources\\ocean_temp_salt_";
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
            for(int i = 0; i < yaxis.getLength(); i++){
                for(int j = 0; j < xaxis.getLength(); j++){
                    double sst = sstaArray.get(index.set(0, 0, i, j));
                    double ssta = swarm.get(i, j);
                    sstaArray.set(index.set(0, 0, i, j), sst + ssta);
                }
            }

            ncfile.write(PARAMETER, sstaArray);

            return orderFileName;
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

}
