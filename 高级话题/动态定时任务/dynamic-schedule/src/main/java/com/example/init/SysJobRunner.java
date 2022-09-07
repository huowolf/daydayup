package com.example.init;

import cn.hutool.core.collection.CollectionUtil;
import com.example.core.CronTaskRegistrar;
import com.example.core.SchedulingRunnable;
import com.example.entity.SysJob;
import com.example.enums.SysJobStatus;
import com.example.service.SysJobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class SysJobRunner implements CommandLineRunner {
    @Autowired
    SysJobService sysJobService;

    @Autowired
    CronTaskRegistrar cronTaskRegistrar;

    @Override
    public void run(String... args) {
        List<SysJob> sysJobList = sysJobService.listSysJob();
        if(CollectionUtil.isNotEmpty(sysJobList)){
            for (SysJob sysJob : sysJobList) {
                //根据job状态恢复job
                if(sysJob.getJobStatus().equals(SysJobStatus.RUNNING.ordinal())){
                    SchedulingRunnable task = new SchedulingRunnable(sysJob.getBeanName(), sysJob.getMethodName(), sysJob.getMethodParams());
                    cronTaskRegistrar.addCronTask(task, sysJob.getCron());
                }
            }
        }
    }
}
