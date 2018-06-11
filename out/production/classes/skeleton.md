# Identifying optimal precursor for two types of El Nino events based on CNOP with GFDL CM2p1 model 
*****
## 1. Introduction
```
1. 厄尔尼诺事件分为两类，两类厄尔尼诺事件影响不同，区分方式也不同；近年来CP事件越来越常见，找到两类厄尔尼诺事件的OPR非常重要，尤其是CP
2. CNOP是识别两类厄尔尼诺OPR的主要方式，Duan et al.（2014）与Xu Hui（2014）分别用不同的方法研究ZC模式的CP OPR，但是受限于模式
3. GFDL CM2p1模式是有能力模拟两类厄尔尼诺的大尺度海气耦合模式，求解OPR就是求解GFDL CM2p1模式的CNOP
4. 由于GFDL CM2p1模式没有相应的伴随模式，求解需要依靠智能算法，并且已经有成功案例：ZC、ROMS、MM5、WRF等。一般采取先降维后搜索的方式，降维生成的特征空间往往在计算出来后不会变化
5. GFDL CM2p1模式选取的计算纬度较高（50*200*360），并且进行一年的非线性积分需要四个小时，如果进行常规的搜索将会非常慢，想要加速智能算法的寻优收敛，引入了动态迭代的特征空间
6. 贡献：动态特征空间的PSO算法，GFDL CM2p1模式，两类厄尔尼诺
```
*****
## 2. CNOP and GFDL CM2p1 model
### 2.1 CNOP？？
```
CNOP-I
适应度值函数及解释
```
### 2.2 GFDL CM2p1 model solving CNOP？？
*****
## 3. Solving CNOP of GFDL CM2p1 model with Dynamic feature space particle swarm optimization(DFSPSO)
```
算法流程图：1）降维，2）还原，3）迭代（约束），4）动态特征空间迭代，1）还原。。。结果
在迭代初期使用大步长加大搜索空间，在迭代末期使用小步长，在最优值附近搜索，加速收敛
```
### 3.1 Feature extraction
```
特征值时空转换
```
### 3.2 Dynamic feature space
```
1. 由于GFDL CM模式维数过高（50*200*360），若想加速计算粒子群算法速度，则必须使用降维。
2. 通常的做法是使用预先处理好的样本在粒子群算法开始前提取其特征值，并得到其特征空间，利用该特征空间对粒子进行低维高维的映射。
3. 通常在算法进行的过程中不会对样本空间进行改变。但是这样在迭代末期不能很好的抓住最优值特征，
	即，一般意义的样本通常只是数量上的堆加，大部分样本都只具有一般特征而不具备我们想要寻找的特征，而通过这样的样本组成的特征空间在迭代末期不能抓住最优值的特征，因为在迭代末期大部分粒子都已进化成具备一定特殊特征的粒子，继续使用原样本特征空间显然是不适用的。
4. 在迭代的过程中使用动态的特征空间有利于抓住想要搜寻的特征，并且可以加速收敛，使粒子群提前达到最优值。
```

#### 3.2.1 Sample replacement
```
样本替换
```
#### 3.2.2 Matrix perturbation
```
矩阵摄扰
Rayleigh商与Hermite矩阵
特征空间还原与粒子约束
```
*****
## 4. Experiment results and analysis
### 4.1 Environment and parameter settings
```
计算资源条件
模式参数设置
```
### 4.2 Optimal precursor for EP-El Nino
```
粒子数与迭代步数
扰动添加范围
适应度值计算范围
实验结果并与模式中找到的对比（pattern nino3 nino4）
```
### 4.3 Optimal precursor for CP-El Nino
```
粒子数与迭代步数
扰动添加范围
适应度值计算范围
实验结果并与模式中找到的对比（pattern nino3 nino4）
```
*****
## 5. Conclusions and future works
*****
## References