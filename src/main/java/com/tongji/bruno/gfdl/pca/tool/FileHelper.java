package com.tongji.bruno.gfdl.pca.tool;

import com.tongji.bruno.gfdl.Constants;
import ucar.ma2.Array;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;
import ucar.nc2.dataset.NetcdfDataset;

import java.util.List;

/**
 * Created by 秦博 on 2017/3/21.
 */
public class FileHelper {

    private static final String fileName = Constants.PCA_PATH;
    private static final String parameter = Constants.PCA_PARAMETER;

    private int year;
    private int n;
    private List<Array> monthArrays;
    private Array singleMonthArray;

    public FileHelper(int n){
        this.n = n;
        try {
            NetcdfFile ncfile = NetcdfDataset.open(fileName);

            Variable sst = ncfile.findVariable(parameter);
            Array part = sst.read(n + ":" + n + ":1, 0:" + Constants.PCA_LEVEL + ":1, " + Constants.PCA_MINLAT + ":" + Constants.PCA_MAXLAT + ":1, " + Constants.PCA_MINLON + ":" + Constants.PCA_MAXLON + ":1");
            this.singleMonthArray = part;

        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public Array getSingleMonthArray() {
        return singleMonthArray;
    }
}
