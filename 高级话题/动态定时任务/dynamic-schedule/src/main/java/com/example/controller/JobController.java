package com.example.controller;


import com.example.base.AjaxResult;
import com.example.entity.SysJob;
import com.example.service.SysJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JobController {

    @Autowired
    private SysJobService sysJobService;

    @GetMapping("/addSysJob")
    public AjaxResult addSysJob(SysJob sysJob){
        return AjaxResult.success(sysJobService.addSysJob(sysJob));
    }

    @GetMapping("/editSysJob")
    public AjaxResult editSysJob(SysJob sysJob){
        return AjaxResult.success(sysJobService.editSysJob(sysJob));
    }

    @GetMapping("/deleteSysJob")
    public AjaxResult deleteSysJob(Integer jobId){
        return AjaxResult.success(sysJobService.deleteSysJob(jobId));
    }

    @GetMapping("/listSysJob")
    public AjaxResult listSysJob(){
        return AjaxResult.success(sysJobService.listSysJob());
    }

    @GetMapping("/startSysJob")
    public AjaxResult startSysJob(Integer jobId){
        sysJobService.startSysJob(jobId);
        return AjaxResult.success();
    }

    @GetMapping("/stopSysJob")
    public AjaxResult stopSysJob(Integer jobId){
        sysJobService.stopSysJob(jobId);
        return AjaxResult.success();
    }
}
