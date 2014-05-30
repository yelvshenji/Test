package com.unionbigdata.managementplatform.crawlermodule.client.webserver.communicate;

/**
 * Created by Administrator on 14-3-16.
 */

import com.unionbigdata.managementplatform.crawlermodule.client.webserver.Other.FileAccess;
import com.unionbigdata.managementplatform.crawlermodule.client.webserver.bean.Result;
import com.unionbigdata.managementplatform.crawlermodule.client.webserver.conf.ConfAttributes;
import com.unionbigdata.managementplatform.crawlermodule.client.webserver.conf.ConfManager;
import com.unionbigdata.managementplatform.crawlermodule.client.webserver.databasemanage.DatabaseManager;
import com.unionbigdata.managementplatform.crawlermodule.client.webserver.managelocalapps.*;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Path("file")
public class UploadRestService {

    static Logger logger = LogManager.getLogger(UploadRestService.class.getName());
    /**
     * upload single file, if the file already exist, then rename the file append the version in the file system and database;
     * then save file
     * 如果下次重传文件时版本和旧版本相同，数据库会报错,但不影响程序运行
     *
     * @param is
     * @param fdcd
     * @param path
     * @param version
     * @return
     */
    @POST
    @Path("uploadsingle")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
//    @Produces({MediaType.TEXT_HTML,MediaType.ACCEPT_CHARSET="UTF-8"})
    public Result<String> upload(@FormDataParam("file") InputStream is,
                           @FormDataParam("file") FormDataContentDisposition fdcd,
                           @FormDataParam("path") String path, @FormDataParam("version") String version) {
        //must get all parameter
        Result<String> result = new Result();
        DatabaseManager dm = new DatabaseManager();
        AppManage appManage = AppManage.getInstance();
        if (is == null) {
            result.setError_msg("must provide file!");
            return result;
        }
        if(path == null){
            path = ConfManager.getAttribute(ConfAttributes.DEFAULT_PATH);
        }
        String[] name = divideUUID(fdcd.getFileName());
        createDirectory(path);
        if(version == null){
            AppInfo appInfo = dm.getAppInfo(appManage.getIp(),path,name[0]);
            if(appInfo == null){
                version = "0.1";
            }else{
                version = autoUpdateVersion(appInfo.getVersion());
            }
        }
        String location = path + name[0];
        //if the upload file is duplicate, handle the old version file
        if (dm.isDuplicate(appManage.getIp(),path, name[0])) {
            renameFile(path, name[0]);
            AppInfo appInfo = dm.getAppInfo(appManage.getIp(),path,name[0]);
            dm.removeAppInfo(appManage.getIp(), path, name[0]);
            appInfo.setName(addVersionInName(appInfo.getName(), appInfo.getVersion()));
            dm.addAppInfo(appInfo);
        }
        saveFile(is, location);
        AppInfo appInfo = new AppInfo(0,appManage.getIp(),path, name[0], version, getFileSize(location),
                getCurrentTime());
        dm.addAppInfo(appInfo);

        result.setData("file " + name[0] + " saved in: " + path);
        return result;
    }

    @POST
    @Path("uploadautorun")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
//    @Produces({MediaType.TEXT_HTML,MediaType.ACCEPT_CHARSET="UTF-8"})
    public Result<String> uploadRun(@FormDataParam("file") InputStream is,
                              @FormDataParam("file") FormDataContentDisposition fdcd,
                              @FormDataParam("path") String path, @FormDataParam("version") String version, @FormDataParam("parameter") String parameter) {
        Result<String> result = upload(is, fdcd, path, version);
        AppControlService appControlService = new AppControlService();
        appControlService.runApp(path, fdcd.getFileName(), parameter);
        return result;
    }

//    已过时
//    /**
//     * the client send file to server. then save the file at the path. the path is the type of UNIX/LINUX and end of "/"
//     * the client must notify the server about the version of the file.
//     *
//     * @param form
//     * @param path
//     * @param version
//     * @return
//     */
//    @POST
//    @Path("upload")
//    @Consumes(MediaType.MULTIPART_FORM_DATA)
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response upload(@RequestParam("file") FormDataMultiPart form,
//                           @FormDataParam("path") String path, @FormDataParam("version") String version) {
//        if (form == null || path == null || version == null) {
//            return Response.status(403).encoding("UTF-8").type(MediaType.TEXT_HTML_TYPE).entity("must provide all parameter!").build();
//        }
//        AppManage appManage = AppManage.getInstance();
//        String location;
//        createDirectory(path);
//        List<FormDataBodyPart> l = form.getFields("file");
//        DatabaseManager dm = new DatabaseManager();
//        for (FormDataBodyPart p : l) {
//            InputStream ist = p.getValueAs(InputStream.class);
//            FormDataContentDisposition detail = p.getFormDataContentDisposition();
//            String strDecode = StringEscapeUtils.unescapeHtml(detail.getFileName());
//            location = path + strDecode;
//            saveFile(ist, location);
//            AppInfo appInfo = new AppInfo(appManage.getIP().getHostAddress(),path, detail.getFileName(), version,
//                    getFileSize(location), getCurrentTime());
//            dm.addAppInfo(appInfo);
//        }
//        return Response.status(200).encoding("UTF-8").type(MediaType.TEXT_HTML_TYPE).entity("file saved in: " + path).build();
//    }

    /**
     * private function. the upload function will call it to implement the way to save files in local system.
     *
     * @param is
     * @param dir
     * @return
     */
    private boolean saveFile(InputStream is, String dir) {
        boolean status = false;
        File file = new File(dir);
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            byte[] b = new byte[1024];
            int readLen = -1;
            while ((readLen = is.read(b)) != -1) {
                os.write(b, 0, readLen);
            }
            os.flush();
            status = true;
        } catch (IOException e) {
            logger.error(e);
            logger.error("saveFile has a IO error!");
            status = false;
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    logger.error(e);
                    logger.error("saveFile has a close stream error!");
                }
            }
            try {
                is.close();
            } catch (IOException e) {
                logger.error(e);
                logger.error("saveFile has a close stream error!");
            }

        }
        return status;
    }

    /**
     * To test is the directory exist or not.If do not exist return false.then create the directory.
     *
     * @param dir
     * @return
     */
    private boolean createDirectory(String dir) {
        File f = new File(dir);
        return f.mkdirs();
    }

    private long getFileSize(String location) {
        File file = new File(location);
        long size = 0;
        try {
            InputStream is = new FileInputStream(file);
            size = is.available();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            logger.error("get file size has a error! FileNotFoundException.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * stop app, copy file, delete file,save new version file
     *
     * @param path
     * @param name
     * @return
     */
    private boolean renameFile(String path, String name) {
        boolean status = false;
        DatabaseManager dm = new DatabaseManager();
        AppManage appManage = AppManage.getInstance();
        AppInfo appInfo = dm.getAppInfo(appManage.getIp(),path,name);
        Job job = dm.getJob(appInfo.getId());
        if(job != null){
            if (job.isRun()) {
                appManage.stopJob(appInfo.getId());
            }
        }
        String nameWithVersion = addVersionInName(name, appInfo.getVersion());
        try {
            FileAccess.Copy(path + name, path + nameWithVersion);
            status = FileAccess.delFile(path + name);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("rename file has a error!");
        }
        return status;
    }

    private String addVersionInName(String name, String version) {
        String[] st = name.split("\\.");
        String s1 = "";
        if(st.length == 1){
            s1 = name+"-"+version;
        }else{
            for (int i = 0; i < st.length - 2; i++) {
                s1 = s1 + st[i] + ".";
            }
            s1 = st[st.length - 2] + "-" + version + "." + st[st.length - 1];
        }
        return s1;
    }

    private String autoUpdateVersion(String version) {
        String[] st = version.split("\\.");
        st[st.length-1] = (Integer.parseInt(st[st.length-1])+1)+"";
        String t = "";
        for(String s:st){
            t+=s+".";
        }
        return t.substring(0,t.length()-1);
    }

    private String[] divideUUID(String name){
        String[] st = name.split("\\.");
        String[] s1 = new String[2];
        //s1[0] name s1[1] UUID
        try{
            if(st.length == 1){
                s1[0] = name.substring(0,name.length()-33);
                s1[1] = name.substring(name.length()-32,name.length());
            }else{
                for (int i = 0; i < st.length - 2; i++) {
                    s1[0] = s1[0] + st[i] + ".";
                }
                s1[0] = st[st.length - 2].substring(0,st[st.length - 2].length()-33) + "." + st[st.length - 1];
                s1[1] = st[st.length - 2].substring(st[st.length - 2].length()-32,st[st.length - 2].length());
            }
        }catch (StringIndexOutOfBoundsException e){
            s1[0] = name;
            s1[1] = "";
        }
        return s1;
    }

    public String getCurrentTime(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        return df.format(new Date());
    }
}