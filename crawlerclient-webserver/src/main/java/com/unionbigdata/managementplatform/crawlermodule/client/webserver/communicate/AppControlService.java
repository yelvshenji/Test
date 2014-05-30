package com.unionbigdata.managementplatform.crawlermodule.client.webserver.communicate;

import com.unionbigdata.managementplatform.crawlermodule.client.webserver.bean.Result;
import com.unionbigdata.managementplatform.crawlermodule.client.webserver.databasemanage.DatabaseManager;
import com.unionbigdata.managementplatform.crawlermodule.client.webserver.managelocalapps.*;
import org.glassfish.jersey.media.multipart.FormDataParam;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Path("control")
/**
 * Created by lwj on 14-3-26.
 */
public class AppControlService {

    static Logger logger = LogManager.getLogger(AppControlService.class.getName());

    /**
     * 启动程序，所需参数路径名path,文件名name,运行参数parameter(可选)
     * 如果参数是管道之类都命令，会报错，暂时不提供此功能
     *
     */
    @POST
    @Path("start")
    @Consumes("application/x-www-form-urlencoded")
    public Result<String> runApp(@FormParam("path") String path, @FormParam("name") String name,
                                 @FormParam("parameter") String parameter) {
        Result<String> result = new Result();
        if (path == null || name == null) {
            result.setError_msg("You must give me the path and the name.");
            return result;
        }
        System.out.println("start app :" + path + name + "parameter is " + parameter);
        try {
            threadRunApp th1 = AppManage.runApp(path, name, parameter);
            AppManage am = AppManage.getInstance();
            am.getAppThread().add(th1);
        } catch (IOException e) {
            logger.error(e);
            logger.error("start app failed!");
            result.setError_msg("start app failed!");
            return result;
        }
        result.setData("run app ok");
        return result;
    }

    /**
     * 从数据库查询程序运行的消息，所需参数有路径名path,文件名name
     */
    @POST
    @Path("queryfile")
    @Produces(MediaType.APPLICATION_JSON)
    public AppInfo getAppInfo(@FormParam("path") String path, @FormParam("name") String name) {
        AppManage appManage = AppManage.getInstance();
        DatabaseManager dm = new DatabaseManager();
        AppInfo appInfo = dm.getAppInfo(appManage.getIp(),path,name);
        if (appInfo == null) {
            //          return "there is no this app!";
            appInfo = new AppInfo();
        }
        return appInfo;
    }

    /**
     * 从数据库查询程序运行的消息，所需参数有进程pid
     */
    @POST
    @Path("queryjob")
    @Produces(MediaType.APPLICATION_JSON)
    public Job getAppRunningInfo(@FormParam("path") String path, @FormParam("name") String name) {
        AppManage appManage = AppManage.getInstance();
        DatabaseManager dm = new DatabaseManager();
        AppInfo appInfo = dm.getAppInfo(appManage.getIp(),path,name);
        Job job = dm.getJob(appInfo.getId());
        if (job != null) {
            return job;
        } else {
            job = new Job();
            return job;
        }
    }

    /**
     * 杀死进程，所需参数有进程pid
     */
    @POST
    @Path("stop")
    @Consumes("application/x-www-form-urlencoded")
    public Result<String> stopApp(@FormParam("path") String path, @FormParam("name") String name) {
        Result<String> result = new Result();
        AppManage appManage = AppManage.getInstance();
        DatabaseManager dm = new DatabaseManager();
        AppInfo appInfo = dm.getAppInfo(appManage.getIp(),path,name);
        Job job = dm.getJob(appInfo.getId());
        appManage.stopJob(job.getFile_id());
        job.setRun(false);
        dm.updateJob(job);
        result.setData("stop app successfully!");
        return result;
    }
}
