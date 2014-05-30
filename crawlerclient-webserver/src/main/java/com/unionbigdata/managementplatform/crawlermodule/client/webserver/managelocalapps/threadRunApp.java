package com.unionbigdata.managementplatform.crawlermodule.client.webserver.managelocalapps;

import com.unionbigdata.managementplatform.crawlermodule.client.webserver.communicate.UploadRestService;
import com.unionbigdata.managementplatform.crawlermodule.client.webserver.databasemanage.DatabaseManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by lwj on 14-3-20.
 */
public class threadRunApp implements Runnable {
    static Logger logger = LogManager.getLogger(threadRunApp.class.getName());

    private String path;
    private String name;
    private String parameter;
    private int pid;
    private boolean isRun;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public int getPid() {
        return pid;
    }

    public boolean isRun() {
        return isRun;
    }

    public void run() {
        BufferedReader br = null;
        //get command to run the application;
        String command;
        if (parameter != null) {
            command = "java -jar " + path + name + " " + parameter;
        } else {
            command = "java -jar " + path + name;
        }
        Runtime rt = Runtime.getRuntime();
        Process p = null;
        try {
            //execute and get pid
            p = rt.exec(command);
            Class clazz = Class.forName("java.lang.UNIXProcess");
            Field pidField = clazz.getDeclaredField("pid");
            pidField.setAccessible(true);
            Object value = pidField.get(p);
            System.out.println(value);
            pid = Integer.parseInt(value.toString());
            isRun = true;
            //add job
            AppManage appManage =AppManage.getInstance();
            DatabaseManager databaseManager = new DatabaseManager();
            AppInfo appInfo = databaseManager.getAppInfo(appManage.getIp(),path,name);
            LinuxProcessInfo linuxProcessInfo = appManage.getAppState(pid);
            Job job = new Job(0,appInfo.getId(),pid,isRun,parameter,linuxProcessInfo.getCpu_percent(),
                    (long)linuxProcessInfo.getMem_percent()*appManage.getTotalMEMSize(),getCurrentTime(),
                    linuxProcessInfo.getTIME());
            databaseManager.addJob(job);
            //print the jar massage;
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String msg = null;
            while ((msg = br.readLine()) != null) {
                System.out.println(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getCurrentTime(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        return df.format(new Date());
    }
}
