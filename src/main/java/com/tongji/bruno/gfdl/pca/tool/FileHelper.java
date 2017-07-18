package com.tongji.bruno.gfdl.pca.tool;

import com.tongji.bruno.gfdl.Constants;
import ucar.ma2.Array;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;
import ucar.nc2.dataset.NetcdfDataset;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 秦博 on 2017/3/21.
 */
public class FileHelper {

    private static final String fileName = Constants.DATA_PATH + "ssta_300Y.nc";
    private static final String parameter = "ssta";

    private int year;
    private int n;
    private List<Array> monthArrays;
    private Array singleMonthArray;

//    public FileHelper(int year){
//        this.year = year;
//        this.monthArrays = new ArrayList<Array>();
//        try {
//            NetcdfFile ncfile = NetcdfDataset.open(fileName);
//
//            Variable sst = ncfile.findVariable(parameter);
//            for(int i = 0; i < 12; i++){
//                Array part = sst.read(year * 12 + i + ":" + (int)(year * 12 + i) + ":1, 0:199:1, 0:359:1");
//                this.monthArrays.add(part);
//            }
//
//        } catch (Exception e){
//            e.printStackTrace();
//        }
    public FileHelper(int n){
        this.n = n;
        try {
            NetcdfFile ncfile = NetcdfDataset.open(fileName);

            Variable sst = ncfile.findVariable(parameter);
            Array part = sst.read(n + ":" + n + ":1, 0:199:1, 0:359:1");
            System.out.println(part.reduce().getDouble(part.reduce().getIndex().set(10, 10)));
            this.singleMonthArray = part;

        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public List<Array> getMonthArrays() {
        return monthArrays;
    }

    public Array getSingleMonthArray() {
        return singleMonthArray;
    }
}
