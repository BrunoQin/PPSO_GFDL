package com.tongji.bruno.gfdl.pca.tool;

import ucar.ma2.Array;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;
import ucar.nc2.dataset.NetcdfDataset;

import java.util.List;

/**
 * Created by 秦博 on 2017/3/21.
 */
public class FileHelper {

//    private static final String fileName = Constants.DATA_PATH + "ta300Y_Lev21.nc";
    private static final String fileName = "/Users/macbookpro/Documents/github/PPSO_GFDL/data/ta300Y_Lev21.nc";
    private static final String parameter = "ta";

    private int year;
    private int n;
    private List<Array> monthArrays;
    private Array singleMonthArray;

    public FileHelper(int n){
        this.n = n;
        try {
            NetcdfFile ncfile = NetcdfDataset.open(fileName);

            Variable sst = ncfile.findVariable(parameter);
            Array part = sst.read(n + ":" + n + ":1, 0:20:1, 0:199:1, 0:179:1");
            this.singleMonthArray = part;

        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public Array getSingleMonthArray() {
        return singleMonthArray;
    }
}
