# Dynamic feature space and step particle swarm optimization to solve optimal precursor for two types of El Nino events
*****
## 1. Introduction
```
1. 厄尔尼诺事件分为两类
2. 两类厄尔尼诺事件影响不同，区分方式也不同
3. 近年来CP事件越来越常见，找到两类厄尔尼诺事件的OPR非常重要，尤其是CP
4. 一方面，Duan et al.（2014）与Xu Hui（2014）分别用不同的方法研究ZC模式的CP OPR，受限于模式
5. 另一方面，使用智能算法寻找OPR（Mu et al. 2015），基于ZC验证了算法可行性
6. 然而，通过经典方法找到的OPR为何总是EP？受制于算法还是数值模式？是否可以通过算法结合模式找到CP的OPR？
7. GFDL CM2p1模式是有能力模拟两类厄尔尼诺的大尺度海气耦合模式，拟采用动态特征空间动态步长的粒子群算法寻找该模式下两类事件OPR
```
*****
## 2. CNOP and GFDL CM2p1 model
### 2.1 CNOP**
```
CNOP-I
适应度值函数及解释
```
### 2.2 GFDL CM2p1 model
*****
## 3. Dynamic feature space and step particle swarm optimization
### 3.1 Dynamic step PSO algrithm
```
在迭代初期使用大步长加大搜索空间，在迭代末期使用小步长，在最优值附近搜索，加速收敛
```
### 3.2 Dynamic feature space
```
1. 由于GFDL CM模式维数过高（50*200*360），若想加速计算粒子群算法速度，则必须使用降维。
2. 通常的做法是使用预先处理好的样本在粒子群算法开始前提取其特征值，并得到其特征空间，利用该特征空间对粒子进行低维高维的映射。
3. 通常在算法进行的过程中不会对样本空间进行改变。但是这样在迭代末期不能很好的抓住最优值特征，
	即，一般意义的样本通常只是数量上的堆加，大部分样本都只具有一般特征而不具备我们想要寻找的特征，而通过这样的样本组成的特征空间在迭代末期不能抓住最优值的特征，因为在迭代末期大部分粒子都已进化成具备一定特殊特征的粒子，继续使用原样本特征空间显然是不适用的。
4. 在迭代的过程中使用动态的特征空间有利于抓住想要搜寻的特征，并且可以加速收敛，使粒子群提前达到最优值。
```
#### 3.2.1 Feature extraction
```
特征值时空转换
```
#### 3.2.2 Sample replacement
```
样本替换
```
#### 3.2.3 Matrix perturbation
```
矩阵摄扰
Rayleigh商与Hermite矩阵
特征空间还原与粒子约束
```
*****
## 4. Experiments
### 4.1 Computing resources and model parameter
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
## 5. Conclusion and future work
*****
## References