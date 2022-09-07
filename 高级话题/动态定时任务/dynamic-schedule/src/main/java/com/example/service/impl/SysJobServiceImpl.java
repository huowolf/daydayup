package com.example.service.impl;


import com.example.core.CronTaskRegistrar;
import com.example.core.SchedulingRunnable;
import com.example.entity.SysJob;
import com.example.enums.SysJobStatus;
import com.example.mapper.SysJobMapper;
import com.example.service.SysJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class SysJobServiceImpl implements SysJobService {
    @Autowired
    CronTaskRegistrar cronTaskRegistrar;
    @Autowired
    SysJobMapper sysJobMapper;

    @Override
    public boolean addSysJob(SysJob sysJob) {
        int insert = sysJobMapper.insert(sysJob);
        if(insert <= 0){
            throw new RuntimeException("新增job失败");
        }

        //根据job状态删除启动job
        if(sysJob.getJobStatus().equals(SysJobStatus.RUNNING.ordinal())){
            SchedulingRunnable task = new SchedulingRunnable(sysJob.getBeanName(), sysJob.getMethodName(), sysJob.getMethodParams());
            cronTaskRegistrar.addCronTask(task, sysJob.getCron());
        }
        return true;
    }

    @Override
    public boolean editSysJob(SysJob sysJob) {
        SysJob existedSysJob = sysJobMapper.selectById(sysJob.getJobId());

        if(Objects.isNull(existedSysJob)){
            return false;
        }

        int i = sysJobMapper.updateById(sysJob);
        if(i <= 0){
            throw new RuntimeException("更新job失败");
        }

        //修改定时任务，先移除原来的任务，再添加新任务。
        if(existedSysJob.getJobStatus().equals(SysJobStatus.RUNNING.ordinal())){
            SchedulingRunnable task = new SchedulingRunnable(existedSysJob.getBeanName(), existedSysJob.getMethodName(), existedSysJob.getMethodParams());
            cronTaskRegistrar.removeCronTask(task);
        }

        if (sysJob.getJobStatus().equals(SysJobStatus.RUNNING.ordinal())) {
            SchedulingRunnable task = new SchedulingRunnable(sysJob.getBeanName(), sysJob.getMethodName(), sysJob.getMethodParams());
            cronTaskRegistrar.addCronTask(task, sysJob.getCron());
        }
        return true;
    }

    @Override
    public boolean deleteSysJob(Integer jobId) {
        SysJob existedSysJob = sysJobMapper.selectById(jobId);

        if(Objects.isNull(existedSysJob)){
            return false;
        }

        if(existedSysJob.getJobStatus().equals(SysJobStatus.RUNNING.ordinal())) {
            SchedulingRunnable task = new SchedulingRunnable(existedSysJob.getBeanName(), existedSysJob.getMethodName(), existedSysJob.getMethodParams());
            cronTaskRegistrar.removeCronTask(task);
        }

        int i = sysJobMapper.deleteById(jobId);
        if(i <= 0){
            throw new RuntimeException("删除job失败");
        }
        return true;
    }

    @Override
    public List<SysJob> listSysJob() {
        return sysJobMapper.selectList(null);
    }

    @Override
    public void startSysJob(Integer jobId) {
        //处于停止状态的job才可以开启
        SysJob existedSysJob = sysJobMapper.selectById(jobId);

        if(Objects.isNull(existedSysJob)){
            return;
        }

        if(existedSysJob.getJobStatus().equals(SysJobStatus.STOP.ordinal())){
            SchedulingRunnable task = new SchedulingRunnable(existedSysJob.getBeanName(), existedSysJob.getMethodName(), existedSysJob.getMethodParams());
            cronTaskRegistrar.addCronTask(task, existedSysJob.getCron());

            //改变状态为运行中
            existedSysJob.setJobStatus(SysJobStatus.RUNNING.ordinal());
            sysJobMapper.updateById(existedSysJob);
        }
    }

    @Override
    public void stopSysJob(Integer jobId) {
        //处于运行状态的job才可以关闭
        SysJob existedSysJob = sysJobMapper.selectById(jobId);

        if(Objects.isNull(existedSysJob)){
            return;
        }

        if(existedSysJob.getJobStatus().equals(SysJobStatus.RUNNING.ordinal())){
            SchedulingRunnable task = new SchedulingRunnable(existedSysJob.getBeanName(), existedSysJob.getMethodName(), existedSysJob.getMethodParams());
            cronTaskRegistrar.removeCronTask(task);

            //改变状态为停止
            existedSysJob.setJobStatus(SysJobStatus.STOP.ordinal());
            sysJobMapper.updateById(existedSysJob);
        }
    }
}
