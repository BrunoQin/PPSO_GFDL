package com.tongji.bruno.gfdl.test;

import com.tongji.bruno.gfdl.Constants;
import com.tongji.bruno.gfdl.ppso.tool.ShellHelper;

/**
 * Created by 秦博 on 2017/10/24.
 */
public class ShellTest {

    public static void main(String[] args) {

        ShellHelper.callScript("command.csh", "2", Constants.RESOURCE_PATH);
        String tem = ShellHelper.exec("/usr/bin/yhqueue");
        System.out.println(tem.contains("fr21.csh"));

    }

}
