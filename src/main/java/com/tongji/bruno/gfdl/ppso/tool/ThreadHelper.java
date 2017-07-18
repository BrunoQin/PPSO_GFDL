package com.tongji.bruno.gfdl.ppso.tool;

import com.tongji.bruno.gfdl.Constants;

/**
 * Created by 秦博 on 2017/7/11.
 */
public class ThreadHelper extends Thread {

    private String shellPath;

    public ThreadHelper(String shellPath){
        this.shellPath = shellPath;
    }

    @Override
    public void run() {
        ShellHelper.callScript("command.csh", shellPath, Constants.RESOURCE_PATH);
        super.run();
    }
}
