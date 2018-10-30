package com.tongji.bruno.gfdl.ppso.tool;

import Jama.Matrix;
import com.tongji.bruno.gfdl.Constants;

/**
 * Created by 秦博 on 2017/7/11.
 */
public class ShellThreadHelper extends Thread {

    private int shellPath;
    private Matrix matrix;

    public ShellThreadHelper(int shellPath, Matrix matrix){
        this.shellPath = shellPath;
        this.matrix = matrix;
    }

    @Override
    public void run() {
        FileHelper.prepareFile(shellPath, matrix);
        FileHelper.copyFile(Constants.RESOURCE_PATH + shellPath + "/ocean_temp_salt_" + shellPath + ".nc", Constants.ROOT_PATH + shellPath + "/CM2.1p1/INPUT/ocean_temp_salt.res.nc", true);
        ShellHelper.callScript("command.csh", " " + shellPath + "", Constants.RESOURCE_PATH);
        System.out.println("is running! good luck!!!");
        super.run();
    }
}
