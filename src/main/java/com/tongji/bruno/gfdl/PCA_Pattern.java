package com.tongji.bruno.gfdl;

import Jama.Matrix;
import com.tongji.bruno.gfdl.algorithm.pca.PCA;
import ucar.ma2.Array;
import ucar.ma2.ArrayDouble;
import ucar.ma2.Index;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFileWriter;
import ucar.nc2.Variable;

public class PCA_Pattern {

  public static void main(String[] args) {
    PCA pca = new PCA();

    System.out.println("start");
    Matrix lambdaMatrix = new Matrix(pca.getPCA());
    System.out.println("go on");
    lambdaMatrix = lambdaMatrix.transpose();
    System.out.println("pca finish!");

    Matrix pattern = new Matrix(lambdaMatrix.getRowDimension(), 1);

    int count = 20;

    for(int i = 0; i < lambdaMatrix.getRowDimension(); i++){
      double sum = 0.0;
      for(int j = 0; j < count; j++){
        sum += lambdaMatrix.get(i, j);
      }
      pattern.set(i, 0, sum/count);
    }

    try{

      String filename = "/Users/macbookpro/Desktop/ocean_temp_salt.res.nc";

      NetcdfFile ncfile = NetcdfFile.open(filename);

      Dimension xaxis = ncfile.getDimensions().get(0);
      Dimension time = ncfile.getDimensions().get(1);
      Dimension zaxis = ncfile.getDimensions().get(2);
      Dimension yaxis = ncfile.getDimensions().get(3);

      ArrayDouble sstaArray = new ArrayDouble.D4(time.getLength(), zaxis.getLength(), yaxis.getLength(), xaxis.getLength());
      Index index = sstaArray.getIndex();
      Variable varBean_o = ncfile.findVariable(Constants.PSO_PARAMETER);

      for(int k = 0; k < 50; k++){
        for(int i = 0; i < 200; i++){
          for(int j = 0; j < 360; j++){
            Array tem = varBean_o.read(Constants.START_MONTH + ":" + Constants.START_MONTH+ ":1, "+ k + ":" + k + ":1, " + i + ":" + i + ":1, " + j + ":" + j + ":1");
            double island = varBean_o.read(Constants.START_MONTH + ":" + Constants.START_MONTH+ ":1, " + (Constants.PER_LEVEL + 1) + ":" + (Constants.PER_LEVEL + 1) + ":1, " + i + ":" + i + ":1, " + j + ":" + j + ":1").getDouble(0);
            if(k <= Constants.PER_LEVEL &&
              i >= Constants.PER_MINLAT && i <= Constants.PER_MAXLAT &&       // 包含两侧边界
              j >= Constants.PER_MINLON && j <= Constants.PER_MAXLON &&       // 包含两侧边界
              island < 9E36 &&
              island > -1E20){
              double ssta = pattern.get(k * Constants.PER_ROW * Constants.PER_COL + (j - Constants.PER_MINLON) * Constants.PER_ROW + (i - Constants.PER_MINLAT), 0);
              sstaArray.set(index.set(0, k, i, j), ssta);
            } else {
              sstaArray.set(index.set(0, k, i, j), 0);
            }
          }
        }
      }
      ncfile.close();

      NetcdfFileWriter wncfile = NetcdfFileWriter.openExisting(filename);
      Variable varBean = wncfile.findVariable(Constants.PSO_PARAMETER);

      System.out.println("start prepare ");
      wncfile.write(varBean, sstaArray);
      ncfile.close();
      System.out.println("finish prepare ");

    } catch (Exception e){
      e.printStackTrace();
    }

  }

}
