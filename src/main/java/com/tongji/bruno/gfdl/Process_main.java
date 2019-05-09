package com.tongji.bruno.gfdl;

import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

public class Process_main {

    private static String TARGET_JULY = "/BIGDATA1/iocas_mmu_2/PPSO_qin/record/cp-JULY/3_23_ocean.nc";
    private static String TARGET_OCT = "/BIGDATA1/iocas_mmu_2/PPSO_qin/record/cp-OCT/1_4_ocean.nc";
    private static String TARGET_JAN = "/BIGDATA1/iocas_mmu_2/PPSO_qin/record/cp-JAN/12_16_ocean.nc";
    private static String TARGET_APR = "/BIGDATA1/iocas_mmu_2/PPSO_qin/record/cp-APR/4_28_ocean.nc";

    private static String ORIGIN_P = "/BIGDATA1/iocas_mmu_2/GFDL-CM2p1/output/04210101.ocean_month.nc";
    private static String ORIGIN_N = "/BIGDATA1/iocas_mmu_2/GFDL-CM2p1/output/04260101.ocean_month.nc";
    private static String STANDARD = "/BIGDATA1/iocas_mmu_2/GFDL-CM2p1/output/sst_ave400Y.nc";
    private static String[] PARAMETERS = {"sst_jan_ave", "sst_feb_ave", "sst_mar_ave", "sst_april_ave", "sst_may_ave", "sst_june_ave", "sst_july_ave", "sst_agu_ave", "sst_sep_ave", "sst_oct_ave", "sst_nov_ave", "sst_dec_ave"};

    public static void main(String[] args) {
        Process_main m = new Process_main();
        m.july_cnop_change(TARGET_JULY, ORIGIN_P, ORIGIN_N);
        m.oct_cnop_change(TARGET_OCT, ORIGIN_P, ORIGIN_N);
        m.jan_cnop_change(TARGET_JAN, ORIGIN_P);
        m.apr_cnop_change(TARGET_APR, ORIGIN_P);

        m.july_nino4_change(TARGET_JULY, STANDARD);
        m.oct_nino4_change(TARGET_OCT, STANDARD);
        m.jan_nino4_change(TARGET_JAN, STANDARD);
        m.apr_nino4_change(TARGET_APR, STANDARD);
    }

    public void oct_cnop_change(String target, String origin_p, String origin_n){
        System.out.println("OCT");
        try{
            NetcdfFile target_file = NetcdfFile.open(target);
            NetcdfFile origin_file_p = NetcdfFile.open(origin_p);
            NetcdfFile origin_file_n = NetcdfFile.open(origin_n);

            Variable sst_target = target_file.findVariable("sst");
            Variable sst_origin_p = origin_file_p.findVariable("sst");
            Variable sst_origin_n = origin_file_n.findVariable("sst");

            double sum;

            for(int time = 0; time < 3; time++){
                sum = 0;
                for(int i = 79; i < 129; i++){
                    for(int j = 62; j < 129; j++){
                        double sst_t = sst_target.read(time + ":" + time + ":1, " + i + ":" + i + ":1, " + j + ":" + j + ":1").getDouble(0);
                        double sst_p = sst_origin_p.read((57 + time) + ":" + (57 + time) + ":1, " + i + ":" + i + ":1, " + j + ":" + j + ":1").getDouble(0);
                        sum += Math.pow((sst_t - sst_p), 2);
                    }
                }
                System.out.println(time + ": " + Math.sqrt(sum));
            }

            for(int time = 0; time < 9; time++){
                sum = 0;
                for(int i = 79; i < 129; i++){
                    for(int j = 62; j < 129; j++){
                        double sst_t = sst_target.read((3 + time) + ":" + (3 + time) + ":1, " + i + ":" + i + ":1, " + j + ":" + j + ":1").getDouble(0);
                        double sst_n = sst_origin_n.read(time + ":" + time + ":1, " + i + ":" + i + ":1, " + j + ":" + j + ":1").getDouble(0);
                        sum += Math.pow((sst_t - sst_n), 2);
                    }
                }
                System.out.println((time + 3) + ": " + Math.sqrt(sum));
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void july_cnop_change(String target, String origin_p, String origin_n){
        System.out.println("JULY");
        try{
            NetcdfFile target_file = NetcdfFile.open(target);
            NetcdfFile origin_file_p = NetcdfFile.open(origin_p);
            NetcdfFile origin_file_n = NetcdfFile.open(origin_n);

            Variable sst_target = target_file.findVariable("sst");
            Variable sst_origin_p = origin_file_p.findVariable("sst");
            Variable sst_origin_n = origin_file_n.findVariable("sst");

            double sum;

            for(int time = 0; time < 6; time++){
                sum = 0;
                for(int i = 79; i < 129; i++){
                    for(int j = 62; j < 129; j++){
                        double sst_t = sst_target.read(time + ":" + time + ":1, " + i + ":" + i + ":1, " + j + ":" + j + ":1").getDouble(0);
                        double sst_p = sst_origin_p.read((54 + time) + ":" + (54 + time) + ":1, " + i + ":" + i + ":1, " + j + ":" + j + ":1").getDouble(0);
                        sum += Math.pow((sst_t - sst_p), 2);
                    }
                }
                System.out.println(time + ": " + Math.sqrt(sum));
            }

            for(int time = 0; time < 6; time++){
                sum = 0;
                for(int i = 79; i < 129; i++){
                    for(int j = 62; j < 129; j++){
                        double sst_t = sst_target.read((6 + time) + ":" + (6 + time) + ":1, " + i + ":" + i + ":1, " + j + ":" + j + ":1").getDouble(0);
                        double sst_n = sst_origin_n.read(time + ":" + time + ":1, " + i + ":" + i + ":1, " + j + ":" + j + ":1").getDouble(0);
                        sum += Math.pow((sst_t - sst_n), 2);
                    }
                }
                System.out.println((time + 6) + ": " + Math.sqrt(sum));
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void jan_cnop_change(String target, String origin){
        System.out.println("JAN");
        try{
            NetcdfFile target_file = NetcdfFile.open(target);
            NetcdfFile origin_file = NetcdfFile.open(origin);

            Variable sst_target = target_file.findVariable("sst");
            Variable sst_origin = origin_file.findVariable("sst");

            double sum;

            for(int time = 0; time < 12; time++){
                sum = 0;
                for(int i = 79; i < 129; i++){
                    for(int j = 62; j < 129; j++){
                        double sst_t = sst_target.read(time + ":" + time + ":1, " + i + ":" + i + ":1, " + j + ":" + j + ":1").getDouble(0);
                        double sst_o = sst_origin.read(time + ":" + time + ":1, " + i + ":" + i + ":1, " + j + ":" + j + ":1").getDouble(0);
                        sum += Math.pow((sst_t - sst_o), 2);
                    }
                }
                System.out.println(time + ": " + Math.sqrt(sum));
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void apr_cnop_change(String target, String origin){
        System.out.println("APR");
        try{
            NetcdfFile target_file = NetcdfFile.open(target);
            NetcdfFile origin_file = NetcdfFile.open(origin);

            Variable sst_target = target_file.findVariable("sst");
            Variable sst_origin = origin_file.findVariable("sst");

            double sum;

            for(int time = 0; time < 12; time++){
                sum = 0;
                for(int i = 79; i < 129; i++){
                    for(int j = 62; j < 129; j++){
                        double sst_t = sst_target.read(time + ":" + time + ":1, " + i + ":" + i + ":1, " + j + ":" + j + ":1").getDouble(0);
                        double sst_o = sst_origin.read((3 + time) + ":" + (3 + time) + ":1, " + i + ":" + i + ":1, " + j + ":" + j + ":1").getDouble(0);
                        sum += Math.pow((sst_t - sst_o), 2);
                    }
                }
                System.out.println(time + ": " + Math.sqrt(sum));
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void july_nino4_change(String target, String standard){
        try{
            NetcdfFile target_file = NetcdfFile.open(target);
            NetcdfFile standard_file = NetcdfFile.open(standard);

            Variable sst_target = target_file.findVariable("sst");

            double sum;
            int count = (129 - 79) * (129 - 62);

            for(int time = 0; time < 12; time++){
                sum = 0;
                for(int i = 79; i < 129; i++){
                    for(int j = 62; j < 129; j++){
                        String p = "";
                        if((time + 6) >= 12){
                            p = PARAMETERS[time - 6];
                        } else {
                            p = PARAMETERS[time + 6];
                        }
                        Variable sst_standard = standard_file.findVariable(p);
                        double sst_t = sst_target.read(time + ":" + time + ":1, " + i + ":" + i + ":1, " + j + ":" + j + ":1").getDouble(0);
                        double sst_s = sst_standard.read(i + ":" + i + ":1, " + j + ":" + j + ":1").getDouble(0);
                        sum += sst_t - sst_s;
                    }
                }
                System.out.println(time + ": " + sum / count);
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void oct_nino4_change(String target, String standard){
        try{
            NetcdfFile target_file = NetcdfFile.open(target);
            NetcdfFile standard_file = NetcdfFile.open(standard);

            Variable sst_target = target_file.findVariable("sst");

            double sum;
            int count = (129 - 79) * (129 - 62);

            for(int time = 0; time < 12; time++){
                sum = 0;
                for(int i = 79; i < 129; i++){
                    for(int j = 62; j < 129; j++){
                        String p = "";
                        if((time + 9) >= 12){
                            p = PARAMETERS[time - 3];
                        } else {
                            p = PARAMETERS[time + 9];
                        }
                        Variable sst_standard = standard_file.findVariable(p);
                        double sst_t = sst_target.read(time + ":" + time + ":1, " + i + ":" + i + ":1, " + j + ":" + j + ":1").getDouble(0);
                        double sst_s = sst_standard.read(i + ":" + i + ":1, " + j + ":" + j + ":1").getDouble(0);
                        sum += sst_t - sst_s;
                    }
                }
                System.out.println(time + ": " + sum / count);
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void jan_nino4_change(String target, String standard){
        try{
            NetcdfFile target_file = NetcdfFile.open(target);
            NetcdfFile standard_file = NetcdfFile.open(standard);

            Variable sst_target = target_file.findVariable("sst");

            double sum;
            int count = (129 - 79) * (129 - 62);

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
                System.out.println(time + ": " + sum / count);
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void apr_nino4_change(String target, String standard){
        try{
            NetcdfFile target_file = NetcdfFile.open(target);
            NetcdfFile standard_file = NetcdfFile.open(standard);

            Variable sst_target = target_file.findVariable("sst");

            double sum;
            int count = (129 - 79) * (129 - 62);

            for(int time = 0; time < 12; time++){
                sum = 0;
                for(int i = 79; i < 129; i++){
                    for(int j = 62; j < 129; j++){
                        String p = "";
                        if((time + 3) >= 12){
                            p = PARAMETERS[time - 9];
                        } else {
                            p = PARAMETERS[time + 3];
                        }
                        Variable sst_standard = standard_file.findVariable(p);
                        double sst_t = sst_target.read(time + ":" + time + ":1, " + i + ":" + i + ":1, " + j + ":" + j + ":1").getDouble(0);
                        double sst_s = sst_standard.read(i + ":" + i + ":1, " + j + ":" + j + ":1").getDouble(0);
                        sum += sst_t - sst_s;
                    }
                }
                System.out.println(time + ": " + sum / count);
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
