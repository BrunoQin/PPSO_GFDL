package com.tongji.bruno.gfdl;

import com.tongji.bruno.gfdl.algorithm.ppso.PPSO;
import com.tongji.bruno.gfdl.tool.FileHelper;

/**
 * Created by 秦博 on 2017/3/22.
 */
public class PPSO_main {

    public static void main(String[] args) {
        System.out.println("hello world!");
        PPSO ppso = new PPSO(1, 100);
        FileHelper.test();
//        ppso.init();
    }

}
