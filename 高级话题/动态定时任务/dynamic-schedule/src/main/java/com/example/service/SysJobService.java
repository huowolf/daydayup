package com.example.service;



import com.example.entity.SysJob;

import java.util.List;

public interface SysJobService {

    /**
     * 新增job
     * @param sysJob
     * @return
     */
    boolean addSysJob(SysJob sysJob);

    /**
     * 编辑job
     * @param sysJob
     * @return
     */
    boolean editSysJob(SysJob sysJob);

    /**
     * 删除job
     */
    boolean deleteSysJob(Integer jobId);

    /**
     * 列出所有的job
     * @return
     */
    List<SysJob> listSysJob();

    /**
     * 开启job
     * @param jobId
     */
    void startSysJob(Integer jobId);

    /**
     * 停止job
     * @param jobId
     */
    void stopSysJob(Integer jobId);
}
