package com.tongji.bruno.gfdl.algorithm.ppso;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import com.tongji.bruno.gfdl.Constants;
import com.tongji.bruno.gfdl.ppso.tool.FileHelper;
import com.tongji.bruno.gfdl.ppso.tool.ShellHelper;
import com.tongji.bruno.gfdl.ppso.tool.ShellThreadHelper;
import ucar.ma2.Array;
import ucar.ma2.Index;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 秦博 on 2017/3/22.
 */
public class PPSO {

    private static final int PCACOUNT = Constants.PCA_COUNT;
    private static final int STEP = Constants.STEP;

    private List<ShellThreadHelper> shellThreadHelpers;

    private Matrix samples; //样本
    private Matrix lambdaMatrix;
    private Matrix c;
    private Matrix d;
    private Matrix v;

    private Matrix outputMatrix; //平均态

    private int swarmCount; //粒子数量
    private int modelCount; //模式数量
    private List<Matrix> top5;
    private List<Matrix> swarmMatrices; //粒子群当前位置
    private List<Matrix> swarmPBest; //粒子个体极值位置
    private double[] swarmPBestValue; //粒子群个体极值
    private List<Matrix> swarmV; //粒子速度
    private Matrix swarmGBest; //粒子群体极值位置
    private double swarmGBestValue; //粒子群群体极值

    private double c1 = 0.8, c2 = 0.8;
    private double w = 2;

    double[] lat;
    double[][][] sigma;

    public PPSO(int swarmCount, int modelCount, Matrix samples){
        this.swarmCount = swarmCount;
        this.modelCount = modelCount;
        this.samples = samples;
        this.outputMatrix = FileHelper.readRestartFile();

        this.lat = FileHelper.getLat();
        this.sigma = FileHelper.getSigma();

        initLambda(this.samples);
    }

    public void initLambda(Matrix samples){
        Matrix _c = samples.transpose().times(samples);
        EigenvalueDecomposition _c_c = _c.eig();
        Matrix _c_v = _c_c.getV();
        Matrix _c_d = _c_c.getD();
        this.c = _c;
        this.d = _c_d;
        this.v = _c_v;
        this.lambdaMatrix = getLambdaMatrix(this.v);
    }

    public Matrix getLambdaMatrix(Matrix v){
        Matrix tv = samples.times(v);
        int m = tv.getRowDimension();
        int n = tv.getColumnDimension();
        for(int i = 0; i < n; i++){
            double s = Math.sqrt(v.get(i, i));
            for(int j = 0; j < m; j++){
                tv.set(j, i, tv.get(j, i) / s);
            }
        }
        return tv;
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
            Matrix temp = Matrix.random(PCACOUNT, 1).minus(mod).times(110);
            this.swarmMatrices.add(temp);
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
        for(int i = 0; i < 21; i++){
            for(int j = 0; j < 200; j++){
                for(int k = 0; k < 180; k++){
                    if(this.sigma[i][j][k] != 0){
                        sum += Math.pow(Math.cos(this.lat[j]) * p.get(i * 200 * 180 + k * 200 + j, 0) / this.sigma[i][j][k], 2);
                    }
                }
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
            Matrix temp = Matrix.random(PCACOUNT, 1).minus(mod).times(4.0);
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

                this.shellThreadHelpers = new ArrayList<ShellThreadHelper>();

                //准备文件
                for(int j = 0; j < this.modelCount; j++){
                    double sum = isLegal(id + j);
                    if(sum > Constants.CONSTRAINT){
                        this.swarmMatrices.set(id + j, this.swarmMatrices.get(id + j).times(Constants.CONSTRAINT / sum));
                    }
                    ShellThreadHelper shellThreadHelper = new ShellThreadHelper(j, this.lambdaMatrix.times(this.swarmMatrices.get(id + j)));
                    this.shellThreadHelpers.add(shellThreadHelper);
                }

                //并行运行
                for(int j = 0; j < this.modelCount; j++){
                    this.shellThreadHelpers.get(j).start();
                    System.out.println("step " + i + " swarm " + (id + j) + " is running! good luck!!!");
                }

                this.shellThreadHelpers.clear();

                //判断完成
                while(true) {
                    try {
                        Thread.sleep(1000 * 60 * 5);
                        Boolean tem_1 = ShellHelper.exec("/usr/bin/yhqueue");
                        Thread.sleep(1000 * 60 * 2);
                        Boolean tem_2 = ShellHelper.exec("/usr/bin/yhqueue");
                        Thread.sleep(1000 * 60 * 1);
                        Boolean tem_3 = ShellHelper.exec("/usr/bin/yhqueue");
                        if (tem_1 && tem_2 && tem_3) {
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
                    FileHelper.writeFile("step" + i + "swarm" + (id + j) + "---current adapt " + Double.toString(currentAdapt), Constants.RESOURCE_PATH  + "best.txt");
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
            if(this.swarmGBestValue < this.swarmPBestValue[index]){
                this.swarmGBestValue = this.swarmPBestValue[index];
                this.swarmGBest = this.swarmPBest.get(index);
            }

            //寻找替换目标
            this.top5 = new ArrayList<>();
            this.top5.add(this.swarmMatrices.get(index));
            double[] temPbest = this.swarmPBestValue;
            for(int m = 0; m < 4; m++){
                temPbest[index] = 0;
                index = getMaxIndex(temPbest);
                this.top5.add(this.swarmMatrices.get(index));
            }

            //替换
            for(int m = 0; m < 5; m++){
                Matrix s = this.lambdaMatrix.times(this.top5.get(m));
                double sum = 0.0;
                int count = 0;
                for(int n = 0; n < s.getRowDimension(); n++){
                    if(s.get(n, 0) != 0){
                        sum += s.get(n, 0);
                        count++;
                    }
                }
                double ave = sum / count;
                for(int n = 0; n < s.getRowDimension(); n++){
                    if(s.get(n, 0) != 0){
                        this.samples.set(n, i * 5 + m, s.get(n, 0) - ave);
                    }
                }
            }

            //计算特征空间，特征向量
            Matrix nc = this.samples.transpose().times(this.samples);
            Matrix dc = nc.minus(this.c);
            Matrix u = this.d.plus(this.v.transpose().times(dc).times(this.v));
            u = getDia(u);
            this.v = nc.minus(u).inverse().times(this.v);
            this.d = this.d.plus(this.v.transpose().times(dc).times(this.v));
            this.lambdaMatrix = getLambdaMatrix(this.v);
            this.c = nc;

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
                double adapt = 0;
                for(int j = 62; j < 130; j++){
                    for(int k = 40; k < 200; k++){
                        adapt += Math.pow(part.reduce().getDouble(index.set(j, k)) - outputMatrix.get(j, k), 2);
                }
                ncfile.close();
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

    public Matrix getDia(Matrix u){
        Matrix tem = new Matrix(300, 300);
        for(int i = 0; i < tem.getRowDimension(); i++){
            for(int j = 0; j < tem.getColumnDimension(); j++){
                tem.set(i, j, u.get(i, j));
            }
        }

        return tem;

    }

}