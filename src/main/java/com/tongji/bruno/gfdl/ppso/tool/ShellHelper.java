package com.tongji.bruno.gfdl.ppso.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

/**
 * Created by 秦博 on 2017/4/16.
 */
public class ShellHelper {

    public static String exec(String cmd){
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(cmd);
            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = "";
            String results = "";
            while ((line = input.readLine()) != null) {
                results += line;
            }
            input.close();
            return results;
        } catch (Exception e) {
            System.out.println("blank");
            e.printStackTrace();
            return "error";
        }

    }

    public static void callScript(String script, String args, String... workspace){
        Process process = null;
        try {
            String cmd = "sh " + script + " " + args;
            File dir = null;
            if(workspace[0] != null){
                dir = new File(workspace[0]);
                System.out.println(workspace[0]);
            }
//            String[] evnp = {};
//            process = Runtime.getRuntime().exec(cmd, evnp, dir);
            process = Runtime.getRuntime().exec(new String[]{"/bin/csh", "-c", cmd},null, dir);
            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = "";
            while ((line = input.readLine()) != null) {
                System.out.println(line);
            }
            input.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }


    }
}
