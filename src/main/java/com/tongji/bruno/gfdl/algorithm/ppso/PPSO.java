package com.tongji.bruno.gfdl.algorithm.ppso;

import Jama.Matrix;
import com.tongji.bruno.gfdl.Constants;
import com.tongji.bruno.gfdl.ppso.tool.FileHelper;
import com.tongji.bruno.gfdl.ppso.tool.ShellHelper;
import com.tongji.bruno.gfdl.ppso.tool.ThreadHelper;
import ucar.ma2.Array;
import ucar.ma2.Index;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 秦博 on 2017/3/22.
 */
public class PPSO {

    private static final int PCACOUNT = Constants.PCA_COUNT;
    private static final int STEP = Constants.STEP;

    private List<ThreadHelper> threadHelpers;

    private Matrix lambdaMatrix; //主成分

    private Matrix outputMatrix; //平均态

    private int swarmCount; //粒子数量
    private int modelCount; //模式数量
    private List<Matrix> swarmMatrices; //粒子群当前位置
    private List<Matrix> swarmPBest; //粒子个体极值位置
    private double[] swarmPBestValue; //粒子群个体极值
    private List<Matrix> swarmV; //粒子速度
    private Matrix swarmGBest; //粒子群体极值位置
    private double swarmGBestValue; //粒子群群体极值

    private double c1 = 0.8, c2 = 0.8;
    private double w = 2;

    double[] lat;
    double[][] sigma;

    public PPSO(int swarmCount, int modelCount, Matrix lambdaMatrix){
        this.swarmCount = swarmCount;
        this.modelCount = modelCount;
        this.lambdaMatrix = lambdaMatrix;
        this.outputMatrix = FileHelper.readRestartFile();

        this.lat = FileHelper.getLat();
        this.sigma = FileHelper.getSigma();
    }

    /**
     * 初始化生成一系列初始低维粒子矩阵
     * @return
     */
    public List<Matrix> initSwarm(){

        this.swarmMatrices = new ArrayList<Matrix>(this.swarmCount);
        for(int i = 0; i < this.modelCount; i++){
            FileHelper.createDir(Constants.RESOURCE_PATH + i);
        }
        for(int i = 0; i < this.swarmCount; i++){
            Matrix mod = new Matrix(PCACOUNT, 1);
            for(int j = 0; j < PCACOUNT; j++){
                mod.set(j, 0, 0.5);
            }
            Matrix temp = Matrix.random(PCACOUNT, 1).minus(mod).times(70.0);
            this.swarmMatrices.add(temp);
            if(isLegal(i) > 330){
                this.swarmMatrices.remove(temp);
                i = i - 1;
                continue;
            }
        }

        return this.swarmMatrices;

    }

    public double isLegal(int num){
        double[][] tem = new double[PCACOUNT][1];
        for(int j = 0; j < PCACOUNT; j++){
            tem[j][0] = this.swarmMatrices.get(num).get(j, 0);
        }
        Matrix p = new Matrix(tem);
        p = lambdaMatrix.times(p);
        double sum = 0.0;
        for(int j = 0; j < 200; j++){
            for(int k = 40; k < 221; k++){
                sum += Math.pow(Math.cos(this.lat[j]) * p.get(k * 200 + j, 0) / this.sigma[j][k], 2);
            }
        }
        return Math.sqrt(sum);

    }

    public List<Matrix> initV(){

        this.swarmV = new ArrayList<Matrix>();
        for(int i = 0; i < this.swarmCount; i++){
            Matrix mod = new Matrix(PCACOUNT, 1);
            for(int j = 0; j < PCACOUNT; j++){
                mod.set(j, 0, 0.5);
            }
            Matrix temp = Matrix.random(PCACOUNT, 1).minus(mod).times(8.0);
            this.swarmV.add(temp);
        }

        return this.swarmV;

    }

    public Matrix seek(){

        //初始化粒子个体极值
        this.swarmPBest = new ArrayList<Matrix>();
        this.swarmPBest = this.swarmMatrices;
        this.swarmPBestValue = new double[swarmCount];
        for(int i = 0; i < this.swarmMatrices.size(); i++){
            this.swarmPBestValue[i] = 0;
        }

        //初始化群体极值
        int index = getMaxIndex(this.swarmPBestValue);
        this.swarmGBestValue = this.swarmPBestValue[index];
        this.swarmGBest = this.swarmPBest.get(index);

        for(int i = 0; i < STEP; i++){

            for(int p = 0; p < this.swarmCount / this.modelCount; p++){

                int id = p * this.modelCount;

                this.threadHelpers = new ArrayList<ThreadHelper>();

                //准备文件
                for(int j = 0; j < this.modelCount; j++){
                    double sum = isLegal(id + j);
                    if(sum > 330){
                        this.swarmMatrices.set(id + j, this.swarmMatrices.get(id + j).times(330 / sum));
                    }
                    FileHelper.prepareFile(j, this.lambdaMatrix.times(this.swarmMatrices.get(id + j)));
                    FileHelper.copyFile(Constants.RESOURCE_PATH + j + "/ocean_temp_salt_" + j + ".nc", Constants.ROOT_PATH + j + "/CM2.1p1/INPUT/ocean_temp_salt.res.nc", true);
                    ThreadHelper threadHelper = new ThreadHelper(j + "");
                    this.threadHelpers.add(threadHelper);
                }

                //并行运行
                for(int j = 0; j < this.modelCount; j++){
                    this.threadHelpers.get(j).start();
                    System.out.println("step " + i + " swarm " + (id + j) + " is running! good luck!!!");
                }

                this.threadHelpers.clear();

                //判断完成
                while(true) {
                    try {
                        Thread.sleep(1000 * 60 * 5);
                        String tem_1 = ShellHelper.exec("/usr/bin/yhqueue");
                        Thread.sleep(1000 * 60 * 2);
                        String tem_2 = ShellHelper.exec("/usr/bin/yhqueue");
                        Thread.sleep(1000 * 60 * 1);
                        String tem_3 = ShellHelper.exec("/usr/bin/yhqueue");
                        if (!tem_1.contains("fr21.csh") && !tem_2.contains("fr21.csh") && !tem_3.contains("fr21.csh")) {
                            System.out.println("step " + i + "group" + p + " finish! ");
                            break;
                        } else {
                            System.out.println("Not Yet!");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                //计算适应度值
                for(int j = 0; j < this.modelCount; j++) {
                    double currentAdapt = adaptValue(j);
                    //更新粒子个体最优矩阵和值
                    FileHelper.writeFile("step" + i + "swarm" + (id + j) + "--- current adapt " + Double.toString(currentAdapt), Constants.RESOURCE_PATH  + "best.txt");
                    if(currentAdapt > this.swarmPBestValue[id + j]){
                        this.swarmPBestValue[id + j] = currentAdapt;
                        this.swarmPBest.set(id + j, this.swarmMatrices.get(id + j));
                    }
                    FileHelper.writeFile("step" + i + "swarm" + (id + j) + "---" + Double.toString(this.swarmPBestValue[id + j]), Constants.RESOURCE_PATH  + "best.txt");
                    System.out.println("step " + i + " swarm " + (id + j) + " is cleaning! ");

                    FileHelper.copyFile(Constants.ROOT_PATH + j + "/CM2.1p1/INPUT/ocean_temp_salt.res.nc", Constants.RESOURCE_PATH + i + "_" + (id + j) + "_origin.nc", true);
                    FileHelper.copyFile(Constants.ROOT_PATH + j + "/CM2.1p1/history/" + FileHelper.getOceanOutputFileName(j), Constants.RESOURCE_PATH + i + "_" + (id + j) + "_ocean.nc", true);
                    FileHelper.copyFile(Constants.ROOT_PATH + j + "/CM2.1p1/history/" + FileHelper.getAtmosOutputFileName(j), Constants.RESOURCE_PATH + i + "_" + (id + j) + "_atmosphere.nc", true);

                    //善后工作，初始化运行条件以便后面工作
                    FileHelper.deleteDirectory(Constants.ROOT_PATH + j + "/CM2.1p1/ascii");
                    FileHelper.deleteDirectory(Constants.ROOT_PATH + j + "/CM2.1p1/history");
                    FileHelper.deleteDirectory(Constants.ROOT_PATH + j + "/CM2.1p1/RESTART");
                    FileHelper.deleteFile(Constants.ROOT_PATH + j + "/CM2.1p1/data_table");
                    FileHelper.deleteFile(Constants.ROOT_PATH + j + "/CM2.1p1/diag_table");
                    FileHelper.deleteFile(Constants.ROOT_PATH + j + "/CM2.1p1/field_table");
                    FileHelper.deleteFile(Constants.ROOT_PATH + j + "/CM2.1p1/input.nml");

                    FileHelper.deleteFile(Constants.ROOT_PATH + j + "/exp/CM2.1p1.output.tar.gz");
                    FileHelper.deleteFile(Constants.ROOT_PATH + j + "/exp/fms.out");

                }
            }

            index = getMaxIndex(this.swarmPBestValue);
            //更新粒子群体最优矩阵和值
            if(this.swarmGBestValue > this.swarmPBestValue[index]){
                this.swarmGBestValue = this.swarmPBestValue[index];
                this.swarmGBest = this.swarmPBest.get(index);
            }

            //寻步
            for(int j = 0; j < this.swarmCount; j++){
                advanceStep(j);
            }
        }

        return null;
    }

    public double adaptValue(int order){

        NetcdfFile ncfile = null;
        try {
            ncfile = NetcdfFile.open(Constants.ROOT_PATH + order + "/CM2.1p1/history/" + FileHelper.getOceanOutputFileName(order));

            //处理restart文件获得adaptValue
            //计算（sst-sst'）平方求和 该值即为适应度值
            try{
                Variable sst = ncfile.findVariable("sst");
                Array part = sst.read("11:11:1, 0:199:1, 0:359:1");
                Index index = part.reduce().getIndex();
                double[][] tem = new double[200][360];
                double adapt = 0;
                for(int j = 0; j < 200; j++){
                    for(int k = 40; k < 221; k++){
                        tem[j][k] = part.reduce().getDouble(index.set(j, k)) - outputMatrix.get(j, k);
                        adapt += Math.pow(tem[j][k], 2);
                }
            }

                return adapt;

            } catch (Exception e){
                e.printStackTrace();
            }

        } catch (IOException ioe) {
            return -1;
        }

        return -1;

    }

    public void advanceStep(int index){

        Matrix v = this.swarmV.get(index).times(w)
                .plus((this.swarmPBest.get(index).minus(this.swarmMatrices.get(index))).times(Math.random() * c1))
                .plus((this.swarmGBest.minus(this.swarmMatrices.get(index))).times(Math.random() * c2));
        this.swarmV.set(index, v);
        this.swarmMatrices.set(index, this.swarmMatrices.get(index).plus(v));

    }

    public int getMaxIndex(double[] arr){
        int maxIndex = 0;
        for(int i = 0; i < arr.length; i++){
            if(arr[maxIndex] < arr[i]){
                maxIndex = i;
            }
        }
        return maxIndex;
    }

}