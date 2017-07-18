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

    private static final int PCACOUNT = 80;
    private static final int STEP = 2;

    private List<ThreadHelper> threadHelpers;

    private Matrix lambdaMatrix; //主成分

    private Matrix outputMatrix; //平均态

    private int swarmCount; //粒子数量
    private List<Matrix> swarmMatrices; //粒子群当前位置
    private List<Matrix> swarmPBest; //粒子个体极值位置
    private double[] swarmPBestValue; //粒子群个体极值
    private List<Matrix> swarmV; //粒子速度
    private Matrix swarmGBest; //粒子群体极值位置
    private double swarmGBestValue; //粒子群群体极值

    private double c1 = 0.8, c2 = 0.8;
    private double w = 2;


    public PPSO(int swarmCount, Matrix lambdaMatrix){
        this.swarmCount = swarmCount;
        this.lambdaMatrix = lambdaMatrix;
        this.outputMatrix = FileHelper.readRestartFile();
    }

    /**
     * 初始化生成一系列初始低维粒子矩阵
     * @return
     */
    public List<Matrix> initSwarm(){

        this.swarmMatrices = new ArrayList<Matrix>(this.swarmCount);
        for(int i = 0; i < this.swarmCount; i++){
            FileHelper.createDir(Constants.RESOURCE_PATH + i);
            Matrix mod = new Matrix(PCACOUNT, 1);
            for(int j = 0; j < PCACOUNT; j++){
                mod.set(j, 0, 0.5);
            }
            Matrix temp = Matrix.random(PCACOUNT, 1).minus(mod).times(35.0);
            this.swarmMatrices.add(temp);
            for(int j = 0; j < PCACOUNT; j++){
                FileHelper.writeFile(Double.toString(temp.get(j, 0)), Constants.RESOURCE_PATH + i + "/" + i + ".txt");
            }
            if(!isLegal(i)){
                this.swarmMatrices.remove(temp);
                FileHelper.deleteFile(Constants.RESOURCE_PATH + i + "/" + i + ".txt");
                i = i - 1;
                continue;
            }
        }

        return this.swarmMatrices;

    }

    public boolean isLegal(int num){
        double[][] tem = new double[80][1];
        for(int j = 0; j < 80; j++){
//            tem[j][0] = FileHelper.readFile(Constants.RESOURCE_PATH + num + "/" + num + ".txt")[j];
            tem[j][0] = this.swarmMatrices.get(num).get(j, 0);
        }

        Matrix p = new Matrix(tem);
        p = lambdaMatrix.times(p);
        double[] lat = FileHelper.getLat();
        double[][] sigma = FileHelper.getSigma();
        double sum = 0.0;
        for(int j = 0; j < 200; j++){
            for(int k = 0; k < 360; k++){
                sum += Math.pow(Math.cos(lat[j]) * p.get(k * 200 + j, 0) / sigma[j][k], 2);
            }
        }
        if (Math.sqrt(sum) > 150){
            return false;
        }
        return true;

    }

    public List<Matrix> initV(){

        this.swarmV = new ArrayList<Matrix>();
        for(int i = 0; i < this.swarmCount; i++){
            Matrix mod = new Matrix(PCACOUNT, 1);
            for(int j = 0; j < PCACOUNT; j++){
                mod.set(j, 0, 0.5);
            }
            Matrix temp = Matrix.random(PCACOUNT, 1).minus(mod).times(4.0);
            this.swarmV.add(temp);
            for(int j = 0; j < PCACOUNT; j++){
                FileHelper.writeFile(Double.toString(temp.get(j, 0)), Constants.RESOURCE_PATH + i + "/v" + i + ".txt");
            }
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

            this.threadHelpers = new ArrayList<ThreadHelper>();

            //准备文件
            for(int j = 0; j < this.swarmCount; j++){
                FileHelper.prepareFile(j, this.lambdaMatrix.times(this.swarmMatrices.get(j)));
                FileHelper.copyFile(Constants.RESOURCE_PATH + j + "/ocean_temp_salt_" + j + ".nc", Constants.ROOT_PATH + j + "/CM2.1p1/INPUT/ocean_temp_salt.res.nc", true);
                ThreadHelper threadHelper = new ThreadHelper(j + "");
                this.threadHelpers.add(threadHelper);
            }

            //并行运行
            for(int j = 0; j < this.swarmCount; j++){
                this.threadHelpers.get(j).start();
                System.out.println("step " + i + " swarm " + j + " is running! good luck!!!");
            }

            this.threadHelpers.clear();

            //判断完成
            while(true) {
                try {
                    Thread.sleep(1000 * 60 * 6);
                    String tem = ShellHelper.exec("bjobs");
                    if (tem.equals("")) {
                        System.out.println("step " + i + " finish! ");
                        break;
                    } else {
                        System.out.println("Not Yet!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //计算适应度值
            for(int j = 0; j < this.swarmCount; j++) {
                double currentAdapt = adaptValue(j);
                //更新粒子个体最优矩阵和值
                if(currentAdapt > this.swarmPBestValue[j]){
                    this.swarmPBestValue[j] = currentAdapt;
                    this.swarmPBest.set(j, this.swarmMatrices.get(j));
                }
                FileHelper.writeFile("step" + i + "swarm" + j + "---" + Double.toString(this.swarmPBestValue[j]), Constants.RESOURCE_PATH  + "best.txt");
                System.out.println("step " + i + " swarm " + j + " is cleaning! ");

                FileHelper.copyFile(Constants.ROOT_PATH + j + "/CM2.1p1/history/01310101.ocean_month.nc", Constants.RESOURCE_PATH + i + "_" + j + "_ocean.nc", true);
                FileHelper.copyFile(Constants.ROOT_PATH + j + "/CM2.1p1/history/01310101.atmosphere_month.nc", Constants.RESOURCE_PATH + i + "_" + j + "_atmosphere.nc", true);

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
            ncfile = NetcdfFile.open(Constants.ROOT_PATH + order + "/CM2.1p1/history/01310101.ocean_month.nc");

            //处理restart文件获得adaptValue
            //计算（sst-sst'）平方求和 该值即为适应度值
            try{
                Variable sst = ncfile.findVariable("sst");
                Array part = sst.read("8:8:1, 0:199:1, 0:359:1");
                Index index = part.reduce().getIndex();
                double[][] tem = new double[200][360];
                double adapt = 0;
                for(int j = 0; j < 200; j++){
                    for(int k = 0; k < 360; k++){
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
                .plus((this.swarmPBest.get(index).minus(this.swarmMatrices.get(index))).times((Math.random() * c1)))
                .plus((this.swarmGBest.minus(this.swarmMatrices.get(index))).times((Math.random() * c2)));
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