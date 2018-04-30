package com.tongji.bruno.gfdl.test;

import ucar.ma2.Array;
import ucar.ma2.ArrayDouble;
import ucar.ma2.Index;
import ucar.nc2.*;
import ucar.nc2.dataset.NetcdfDataset;

/**
 * Created by 秦博 on 2017/10/24.
 */
public class NCFileTest {

    public static void main(String[] args) {

        try{
            String newFileName = "/Users/Bruno/Desktop/ocean_temp_salt_0.nc";
            String oldFileName = "/Users/Bruno/Desktop/ocean_temp_salt.res.nc";
            NetcdfFile oldNcfile = NetcdfFile.open(oldFileName);
            NetcdfFile newNcfile = NetcdfFile.open(newFileName);

            Dimension xaxis = oldNcfile.getDimensions().get(0);
            Dimension time = oldNcfile.getDimensions().get(1);
            Dimension zaxis = oldNcfile.getDimensions().get(2);
            Dimension yaxis = oldNcfile.getDimensions().get(3);

            System.out.println(xaxis);
            System.out.println(time);
            System.out.println(zaxis);
            System.out.println(yaxis);

            ArrayDouble sstaArray = new ArrayDouble.D4(time.getLength(), zaxis.getLength(), yaxis.getLength(), xaxis.getLength());
            Index index = sstaArray.getIndex();
            Variable varBean_o = oldNcfile.findVariable("temp");
            Variable varBean_n = newNcfile.findVariable("temp");
            for(int i = 0; i < 200; i++){
                for(int j = 0; j < 360; j++){
                    for(int k = 0; k < 50; k++){
                        Array tem_n = varBean_n.read("0:0:1, " + k + ":" + k + ":1, " + i + ":" + i + ":1, " + j + ":" + j + ":1");
                        Array tem_o = varBean_o.read("0:0:1, " + k + ":" + k + ":1, " + i + ":" + i + ":1, " + j + ":" + j + ":1");
                        double tn =  tem_n.getDouble(0);
                        double tm =  tem_o.getDouble(0);
                        sstaArray.set(index.set(0, k, i, j), tn - tm);
                        if(tn - tm != 0){
                            System.out.println(tn - tm);
                        }
                    }
                }
            }

            NetcdfFileWriteable over = NetcdfFileWriteable.openExisting(newFileName, true);
            over.write("temp", sstaArray);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
