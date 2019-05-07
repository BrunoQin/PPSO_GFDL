package com.tongji.bruno.gfdl;

import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

public class Process_main {

    private static String TARGET = "/BIGDATA1/iocas_mmu_2/PPSO_qin/record/12_16_ocean.nc";
    private static String STANDARD = "/BIGDATA1/iocas_mmu_2/GFDL-CM2p1/output/sst_ave400Y.nc";
    private static String[] PARAMETERS = {"sst_jan_ave", "sst_feb_ave", "sst_mar_ave", "sst_april_ave", "sst_may_ave", "sst_june_ave", "sst_july_ave", "sst_agu_ave", "sst_sep_ave", "sst_oct_ave", "sst_nov_ave", "sst_dec_ave"};

    public static void main(String[] args) {
        Process_main m = new Process_main();
        m.nino3_change(TARGET, STANDARD);
    }

    public void nino3_change(String target, String standard){
        try{
            NetcdfFile target_file = NetcdfFile.open(target);
            NetcdfFile standard_file = NetcdfFile.open(standard);

            Variable sst_target = target_file.findVariable("sst");

            double sum;

            for(int time = 0; time < 12; time++){
                sum = 0;
                for(int i = 79; i < 129; i++){
                    for(int j = 62; j < 129; j++){
                        Variable sst_standard = standard_file.findVariable(PARAMETERS[time]);
                        double sst_t = sst_target.read(time + ":" + time + ":1, " + i + ":" + i + ":1, " + j + ":" + j + ":1").getDouble(0);
                        double sst_s = sst_standard.read(i + ":" + i + ":1, " + j + ":" + j + ":1").getDouble(0);
                        sum += sst_t - sst_s;
                    }
                }
                System.out.println(time + ": " + sum);
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
