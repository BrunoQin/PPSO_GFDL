package com.tongji.bruno.gfdl.test;

import ucar.ma2.Array;
import ucar.ma2.ArrayDouble;
import ucar.ma2.Index;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFileWriteable;
import ucar.nc2.Variable;

/**
 * Created by 秦博 on 2017/10/24.
 */
public class NCFileTest {

    public static void main(String[] args) {

        try{
            String newFileName = "D:\\1_39_ocean.nc";
            String oldFileName = "D:\\ocean_temp_salt.res.nc";
            NetcdfFileWriteable oldNcfile = NetcdfFileWriteable.openExisting(oldFileName);
            NetcdfFileWriteable newNcfile = NetcdfFileWriteable.openExisting(newFileName);

            Dimension xaxis = oldNcfile.getDimensions().get(0);
            Dimension yaxis = oldNcfile.getDimensions().get(1);
            Dimension zaxis = oldNcfile.getDimensions().get(2);
            Dimension time = oldNcfile.getDimensions().get(3);

            ArrayDouble sstaArray = new ArrayDouble.D4(time.getLength(), zaxis.getLength(), yaxis.getLength(), xaxis.getLength());
            Index index = sstaArray.getIndex();
            Variable varBean_o = oldNcfile.findVariable("temp");
            Variable varBean_n = newNcfile.findVariable("temp");
            for(int i = 0; i < 200; i++){
                for(int j = 0; j < 360; j++){
                    for(int k = 0; k < 1; k++){
                        Array tem_n = varBean_n.read("0:0:1, " + k + ":" + k + ":1, " + i + ":" + i + ":1, " + j + ":" + j + ":1");
                        Array tem_o = varBean_o.read("0:0:1, " + k + ":" + k + ":1, " + i + ":" + i + ":1, " + j + ":" + j + ":1");
                        double tn =  tem_n.getDouble(0);
                        System.out.println(tn);
                        double tm =  tem_o.getDouble(0);
                        System.out.println(tm);
                        if(tn >= 9E36){
                            tn = 0;
                        }
                        if(tn <= -1.0000000200408773E20){
                            tn = 0;
                        }
                        if(tm >= 9E36){
                            tm = 0;
                        }
                        if(tm <= -1.0000000200408773E20)
                            tm = 0;
                        sstaArray.set(index.set(0, k, i, j), tn - tm);
                    }
                }
            }

            NetcdfFileWriteable over = NetcdfFileWriteable.openExisting(oldFileName, true);
            over.write("temp", sstaArray);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
