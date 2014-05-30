package com.unionbigdata.managementplatform.crawlermodule.client.detect;

import com.unionbigdata.managementplatform.crawlermodule.client.detect.database.DatabaseManager;
import com.unionbigdata.managementplatform.crawlermodule.client.detect.managelocalapps.AppInfo;
import com.unionbigdata.managementplatform.crawlermodule.client.detect.managelocalapps.AppManage;
import com.unionbigdata.managementplatform.crawlermodule.client.detect.managelocalapps.Job;
import com.unionbigdata.managementplatform.crawlermodule.client.detect.managelocalapps.LinuxProcessInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;


/**
 * Created by lwj on 14-5-5.
 */
public class Detect {
    static Logger logger = LogManager.getLogger(Detect.class.getName());
    float totalMemory;

    public Detect() {
        Runtime   r   =   Runtime.getRuntime();
        totalMemory   =   (float)   r.totalMemory();
    }

    public void handle(){
        AppManage appManage = AppManage.getInstance();
        DatabaseManager dm = new DatabaseManager();
        ArrayList<AppInfo> appInfos = dm.getAllAppInfo(appManage.getIp());
        ArrayList<Job> jobs = dm.getAllRunningJob(appManage.getIp());
        for(int i = 0;i<jobs.size();i++){
            Job job = jobs.get(i);
            LinuxProcessInfo linuxProcessInfo = appManage.getAppState(job.getPid());
            if(linuxProcessInfo == null){
                job.setRun(false);
                dm.updateJob(job);
            }else {
                job.setCpu(linuxProcessInfo.getCpu_percent());
                job.setDuration(linuxProcessInfo.getTIME());
                job.setMem((long)linuxProcessInfo.getMem_percent()*appManage.getTotalMEMSize());
                dm.updateJob(job);
            }
        }
    }

    public static void main(String[] args) {
        Detect detect = new Detect();
        detect.handle();
    }


}
