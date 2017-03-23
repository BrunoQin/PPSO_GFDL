package com.tongji.bruno.gfdl.algorithm.ppso;

import Jama.Matrix;
import com.tongji.bruno.gfdl.tool.FileHelper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 秦博 on 2017/3/22.
 */
public class PPSO {

    private static final int PCACOUNT = 80;
    private static final int XAXIS = 360;
    private static final int YAXIS = 200;
    private static final double BESTADAPT = 0.0;
    private static final int STEP = 10;

    private static final String CMD = "cmd /c \"D: && dir";

    private Matrix lambdaMatrix;

    private int swarmCount; //粒子数量
    private List<Matrix> swarmMatrices; //粒子群当前位置
    private List<Matrix> swarmPBest; //粒子个体极值位置
    private double[] swarmPBestValue; //粒子群个体极值
    private List<Matrix> swarmV; //粒子速度
    private Matrix swarmGBest; //粒子群体极值位置
    private double swarmGBestValue; //粒子群群体极值

    private double c1 = 0.8, c2 = 0.8;
    private double w = 2;


    public PPSO(int swarmCount){
        this.swarmCount = swarmCount;
        // todo
        this.lambdaMatrix = Matrix.random(XAXIS * YAXIS, PCACOUNT);
    }

    /**
     * 初始化生成一系列初始低维粒子矩阵
     * @return
     */
    public List<Matrix> init(){

        this.swarmMatrices = new ArrayList<Matrix>();
        for(int i = 0; i < this.swarmCount; i++){
            this.swarmMatrices.add(Matrix.random(PCACOUNT, 1));
        }

        return this.swarmMatrices;

    }

    public Matrix seek(){

        //初始化粒子个体极值
        this.swarmPBest = new ArrayList<Matrix>();
        this.swarmPBest = this.swarmMatrices;
        this.swarmPBestValue = new double[swarmCount];
        for(int i = 0; i < this.swarmMatrices.size(); i++){
            this.swarmPBestValue[i] = adaptValue(i, this.swarmMatrices.get(i));
        }

        //初始化群体极值
        int index = getMaxIndex(this.swarmPBestValue);
        this.swarmGBestValue = this.swarmPBestValue[index];
        this.swarmGBest = this.swarmPBest.get(index);

        //初始化粒子速度
        this.swarmV = new ArrayList<Matrix>();
        for(int i = 0; i < this.swarmCount; i++){
            this.swarmV.add(Matrix.random(PCACOUNT, 1));
        }

        for(int i = 0; i < STEP; i++){
            for(int j = 0; j < this.swarmCount; j++){
                advanceStep(j);
                double currentAdapt = adaptValue(j, this.swarmMatrices.get(j));
                //更新粒子个体最优矩阵和值
                // todo
                if(currentAdapt > this.swarmPBestValue[j]){
                    this.swarmPBestValue[j] = currentAdapt;
                    this.swarmPBest.set(j, this.swarmMatrices.get(j));
                }
            }
            index = getMaxIndex(this.swarmPBestValue);
            //更新粒子群体最优矩阵和值
            // todo
            if(this.swarmGBestValue > this.swarmPBestValue[index]){
                this.swarmGBestValue = this.swarmPBestValue[index];
                this.swarmGBest = this.swarmPBest.get(index);
            }
        }

        return null;
    }

    public double adaptValue(int order, Matrix swarm){
        //这个地方要联系矩阵修改文件，并且要用java调shell，很麻烦！
        //把粒子变成原空间
        Matrix sourceMatrix = this.lambdaMatrix.times(swarm);
        int row = sourceMatrix.getRowDimension();
        int col = sourceMatrix.getColumnDimension();
        double[][] sstArray = new double[XAXIS][YAXIS];
        for(int i = 0; i < row; i++){
            for(int j = 0; j < col; j++){
                sstArray[j / YAXIS][j % YAXIS] = sourceMatrix.get(i, j);
            }
        }
        Matrix sstMatrix = new Matrix(sstArray);

        String orderFileName = FileHelper.prepareFile(order, sstMatrix);
        FileHelper.copyFile(orderFileName, "path to real input file and rename", true);

        //调shell
        try{
            Process process = Runtime.getRuntime().exec(CMD);
            process.waitFor();
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "gbk"); //gbk：解决输出乱码
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null){
                System.out.println(line);
            }

            is.close();
            isr.close();
            br.close();
        } catch (Exception e){
            e.printStackTrace();
        }

        //处理restart文件 todo

        return 2.0;
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
