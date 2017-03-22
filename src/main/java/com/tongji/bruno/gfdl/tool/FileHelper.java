package com.tongji.bruno.gfdl.tool;

import ucar.ma2.Array;
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
    private static final String newName = "D:\\github\\PPSO_GFDL\\src\\main\\resources\\1.nc";

    public static String test(){
        try{
            copyFile(fileName, newName, true);
            NetcdfFileWriteable ncfile = NetcdfFileWriteable.openExisting(newName);
            List<Dimension> list =  ncfile.getDimensions();
            for(Dimension d : list){
                System.out.println("name="+d.getName()+" length="+d.getLength());
            }
            //read variables
            List<Variable> variables = ncfile.getVariables();
            System.out.println();
            for(Variable v : variables){
                System.out.println("name="+v.getName()+" NameAndDimension="+v.getNameAndDimensions()+" ElementSize="+v.getElementSize());
            }

            String variable = "temp";
            Variable varbean = ncfile.findVariable(variable);
            Array part = varbean.read("0:0:1, 0:0:1, 0:199:1, 0:359:1");
            System.out.println("x轴从0到2 跨度为2 y轴从0到2 跨度为2：\n" + NCdumpW.printArray(part, variable, null));

            Dimension xaxis = ncfile.getDimensions().get(0);
            Dimension yaxis = ncfile.getDimensions().get(1);
            Dimension zaxis = ncfile.getDimensions().get(2);
            Dimension time = ncfile.getDimensions().get(3);
            ArrayDouble sstaArray = new ArrayDouble.D4(time.getLength(), zaxis.getLength(), yaxis.getLength(), xaxis.getLength());
            Index index = sstaArray.getIndex();
            for(int i = 0; i < yaxis.getLength(); i++){
                for(int j = 0; j < xaxis.getLength(); j++){
                    sstaArray.set(index.set(0, 0, i, j), (double) (3));
                }
            }

            ncfile.write("temp", sstaArray);

            return null;
        } catch (Exception e){
            e.printStackTrace();
        }

        return null;

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
