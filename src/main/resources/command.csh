#!/bin/csh

cd /HOME/iocas_mmu_2/BIGDATA/PPSO/$argv[1]/exp/
module load netcdf/4.3.2/01-CF-14
yhbatch -N 2 -n 45 -p bigdata fr21.csh
