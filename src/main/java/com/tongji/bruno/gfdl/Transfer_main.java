package com.tongji.bruno.gfdl;

import ucar.ma2.ArrayDouble;
import ucar.ma2.Index;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFileWriter;
import ucar.nc2.Variable;

public class Transfer_main {

    private static final String PER_FILE = Constants.RESOURCE_PATH + "exp/12_16_origin.nc";
    private static final String OLD_FILE = Constants.RESOURCE_PATH + "exp/ocean_temp_salt.jan.nc";
    private static final String NEW_FILE = Constants.RESOURCE_PATH + "exp/ocean_temp_salt.oct.nc";

    public static void main(String[] args) {

        try{

            NetcdfFile perFile = NetcdfFile.open(PER_FILE);
            NetcdfFile oldFile = NetcdfFile.open(OLD_FILE);
            NetcdfFile newFile = NetcdfFile.open(NEW_FILE);

            Dimension xaxis = perFile.getDimensions().get(0);
            Dimension time = perFile.getDimensions().get(1);
            Dimension zaxis = perFile.getDimensions().get(2);
            Dimension yaxis = perFile.getDimensions().get(3);

            ArrayDouble sstaArray = new ArrayDouble.D4(time.getLength(), zaxis.getLength(), yaxis.getLength(), xaxis.getLength());
            Index index = sstaArray.getIndex();
            Variable varBean_per = perFile.findVariable(Constants.PSO_PARAMETER);
            Variable varBean_old = oldFile.findVariable(Constants.PSO_PARAMETER);
            Variable varBean_new = oldFile.findVariable(Constants.PSO_PARAMETER);

            for(int k = 0; k < 50; k++){
                for(int i = 0; i < 200; i++){
                    for(int j = 0; j < 360; j++){
                        double island = varBean_per.read(Constants.START_MONTH + ":" + Constants.START_MONTH+ ":1, " + (Constants.PER_LEVEL + 1) + ":" + (Constants.PER_LEVEL + 1) + ":1, " + i + ":" + i + ":1, " + j + ":" + j + ":1").getDouble(0);
                        if(k <= Constants.PER_LEVEL &&
                                i >= Constants.PER_MINLAT && i <= Constants.PER_MAXLAT &&       // 包含两侧边界
                                j >= Constants.PER_MINLON && j <= Constants.PER_MAXLON &&       // 包含两侧边界
                                island < 9E36 &&
                                island > -1E20){
                            double ssta = varBean_per.read(Constants.START_MONTH + ":" + Constants.START_MONTH+ ":1, " + (Constants.PER_LEVEL + 1) + ":" + (Constants.PER_LEVEL + 1) + ":1, " + i + ":" + i + ":1, " + j + ":" + j + ":1").getDouble(0)
                                    - varBean_old.read(Constants.START_MONTH + ":" + Constants.START_MONTH+ ":1, " + (Constants.PER_LEVEL + 1) + ":" + (Constants.PER_LEVEL + 1) + ":1, " + i + ":" + i + ":1, " + j + ":" + j + ":1").getDouble(0)
                                    + varBean_new.read(Constants.START_MONTH + ":" + Constants.START_MONTH+ ":1, " + (Constants.PER_LEVEL + 1) + ":" + (Constants.PER_LEVEL + 1) + ":1, " + i + ":" + i + ":1, " + j + ":" + j + ":1").getDouble(0);

                            sstaArray.set(index.set(0, k, i, j), ssta);
                        } else {
                            sstaArray.set(index.set(0, k, i, j), varBean_new.read(Constants.START_MONTH + ":" + Constants.START_MONTH+ ":1, " + (Constants.PER_LEVEL + 1) + ":" + (Constants.PER_LEVEL + 1) + ":1, " + i + ":" + i + ":1, " + j + ":" + j + ":1").getDouble(0));
                        }
                    }
                }
            }
            perFile.close();
            oldFile.close();
            newFile.close();

            NetcdfFileWriter wncfile = NetcdfFileWriter.openExisting(NEW_FILE);
            Variable varBean = wncfile.findVariable(Constants.PSO_PARAMETER);

            System.out.println("start prepare ");
            wncfile.write(varBean, sstaArray);
            wncfile.close();
            System.out.println("finish prepare ");

        } catch (Exception e){
            e.printStackTrace();
        }

    }

}
