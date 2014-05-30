package com.unionbigdata.managementplatform.crawlermodule.client.webserver.databasemanage;

import com.unionbigdata.managementplatform.crawlermodule.client.webserver.managelocalapps.AppInfo;
import com.unionbigdata.managementplatform.crawlermodule.client.webserver.managelocalapps.Job;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by lwj on 14-3-24.
 */
public class DatabaseManager {
    static Logger logger = LogManager.getLogger(DatabaseManager.class.getName());

    /**
     * get file form service then log the massage in database.
     * type is | name  | path       | pid  | version | isRun | command | size | creationdate | permissions | cpu  | time | mem  |
     *
     * @param appInfo
     */
    public void addAppInfo(AppInfo appInfo) {
        Statement statement = null;
        ResultSet rs = null;

        DBConnectionManager dbConnectionManager = DBConnectionManager.getInstance();
        Connection conn = dbConnectionManager.getConnection("mypool");
        try {
            String insert = "insert into client_file_table values ("+appInfo.getId()+",'"+appInfo.getIp()
                    +"','"+appInfo.getPath()+"','"+appInfo.getName()+"','"+appInfo.getVersion()
                    +"',"+appInfo.getSize()+",'"+appInfo.getCreationDate()+"');";
            System.out.println(insert);
            statement = conn.createStatement();
            statement.executeUpdate(insert);
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("addAppInfo has a SQL error!");
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    logger.error("addAppInfo has a close stream at SQL error!");
                }
            }
            dbConnectionManager.freeConnection("mypool",conn);
        }
        System.out.println("add App successfully in database.");
    }

    public void updateAppInfo(AppInfo appInfo) {
        removeAppInfo(appInfo.getIp(), appInfo.getPath(), appInfo.getName());
        addAppInfo(appInfo);
        System.out.println("update App successfully in database.");
    }

    /**
     * get the file massage from the data base.getConnection need the name of the DBConnectionPool name.
     * then generate a AppInfo class from the result which query from the Database
     * return the AppInfo class
     */
    public AppInfo getAppInfo(String ip,String path, String name) {
        ResultSet rs = null;
        Statement statement = null;
        AppInfo ai = null;
        DBConnectionManager dbConnectionManager = DBConnectionManager.getInstance();
        Connection conn = dbConnectionManager.getConnection("mypool");
        try {
            statement = conn.createStatement();
            String sql = "select * from client_file_table where ip = '"+ip+"' and path = '" + path +"' and name = '"+name+ "';";
            System.out.println(sql);
            rs = statement.executeQuery(sql);
            rs.next();
            ai = new AppInfo(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5),
                    rs.getLong(6),rs.getString(7));
        } catch (SQLException e) {
            logger.error("there is no the AppInfo!");
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    logger.error("addAppInfo has a close stream SQL error!");
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    logger.error("addAppInfo has a close stream SQL error!");
                }
            }
            dbConnectionManager.freeConnection("mypool",conn);
        }
        return ai;
    }

    public ArrayList<AppInfo> getAllAppInfo(String ip){
        ResultSet rs = null;
        Statement statement = null;
        ArrayList<AppInfo> appList = new ArrayList();
        AppInfo ai = null;
        DBConnectionManager dbConnectionManager = DBConnectionManager.getInstance();
        Connection conn = dbConnectionManager.getConnection("mypool");
        try {
            statement = conn.createStatement();
            String sql = "select * from client_file_table where ip = '"+ip+"';";
            System.out.println(sql);
            rs = statement.executeQuery(sql);
            while (rs.next()){
                ai = new AppInfo(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5),
                        rs.getLong(6),rs.getString(7));
                appList.add(ai);
            }
        } catch (SQLException e) {
            logger.error("there is no the AppInfo!");
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    logger.error("addAppInfo has a close stream SQL error!");
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    logger.error("addAppInfo has a close stream SQL error!");
                }
            }
            dbConnectionManager.freeConnection("mypool",conn);
        }
        return appList;
    }

    public ArrayList<Job> getAllRunningJob(String ip){
        ArrayList<AppInfo> appInfos = getAllAppInfo(ip);
        ArrayList<Job> jobsList = new ArrayList();
        for(int i = 0;i<appInfos.size();i++){
            Job job = getJob(appInfos.get(i).getId());
            if(job != null){
                jobsList.add(job);
            }
        }
        return jobsList;
    }
//    public AppInfo getAppInfo(String name) {
//        ResultSet rs = null;
//        Statement statement = null;
//        AppInfo ai = null;
//        DBConnectionManager dbConnectionManager = DBConnectionManager.getInstance();
//        Connection conn = dbConnectionManager.getConnection("mypool");
//        try {
//            statement = conn.createStatement();
//            String sql = "select * from file where name = '" + name + "';";
//            System.out.println(sql);
//            rs = statement.executeQuery(sql);
//            rs.next();
//            ai = new AppInfo(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getInt(5),
//                    rs.getString(6));
//        } catch (SQLException e) {
//            e.printStackTrace();
//            logger.error("addAppInfo has a SQL error!");
//        } finally {
//            if (rs != null) {
//                try {
//                    rs.close();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                    logger.error("addAppInfo has a close stream SQL error!");
//                }
//            }
//            if (statement != null) {
//                try {
//                    statement.close();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                    logger.error("addAppInfo has a close stream SQL error!");
//                }
//            }
//            dbConnectionManager.freeConnection("mypool",conn);
//        }
//        return ai;
//    }

    public boolean removeAppInfo(String ip, String path, String name) {
        Statement statement = null;
        boolean status = false;

        DBConnectionManager dbConnectionManager = DBConnectionManager.getInstance();
        Connection conn = dbConnectionManager.getConnection("mypool");
        try {
            statement = conn.createStatement();
            String sql = "delete from client_file_table where ip = '"+ip+"' and path = '" + path +"' and name = '"+name+ "';";
            System.out.println(sql);
            int count = statement.executeUpdate(sql);
            System.out.println(count + "file(s) be deleted. ");
            status = true;
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("addAppInfo has a SQL error!");
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    logger.error("addAppInfo has a close statement SQL error!");
                }
            }
            dbConnectionManager.freeConnection("mypool",conn);
        }
        return status;
    }

    /**
     * whether the file already exist
     *
     * @param path
     * @param name
     * @return
     */
    public boolean isDuplicate(String ip, String path, String name) {
        Statement statement = null;
        ResultSet rs = null;
        boolean status = false;

        DBConnectionManager dbConnectionManager = DBConnectionManager.getInstance();
        Connection conn = dbConnectionManager.getConnection("mypool");
        try {
            statement = conn.createStatement();
            String sql = "select * from client_file_table where ip = '"+ip+"' and path = '" + path +"' and name = '"+name+ "';";
            System.out.println(sql);
            rs = statement.executeQuery(sql);
            rs.next();
            if (rs.getString("path").compareTo(path) == 0 && rs.getString("name").compareTo(name) == 0) {
                status = true;
            }
        } catch (SQLException e) {
            logger.warn("isDuplicate error!");
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    logger.error("addAppInfo has a close statement SQL error!");
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    logger.error("addAppInfo has a close result set SQL error!");
                }
            }
            dbConnectionManager.freeConnection("mypool",conn);
        }
        return status;
    }

    public Job getJob(int file_id){
        ResultSet rs = null;
        Statement statement = null;
        Job job = null;
        DBConnectionManager dbConnectionManager = DBConnectionManager.getInstance();
        Connection conn = dbConnectionManager.getConnection("mypool");
        String sql = "select * from client_job_table where file_id = "+file_id+";";
        try {
            statement = conn.createStatement();
            System.out.println(sql);
            rs = statement.executeQuery(sql);
            while (rs.next()){
                job = new Job(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getBoolean(4), rs.getString(5),
                        rs.getFloat(6),rs.getInt(7), rs.getString(8), rs.getString(9));
                if(job.isRun()){
                    break;
                }
            }
            if(job != null){
                if(job.isRun() == false){
                    job = null;
                }
            }
        } catch (SQLException e) {
            logger.error("Cannot get this job!"+" sql:"+sql);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    logger.error("addAppInfo has a close stream SQL error!");
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    logger.error("addAppInfo has a close stream SQL error!");
                }
            }
            dbConnectionManager.freeConnection("mypool",conn);
        }
        return job;
    }

    public boolean addJob(Job job){
        Statement statement = null;
        ResultSet rs = null;
        boolean flag = true;
        DBConnectionManager dbConnectionManager = DBConnectionManager.getInstance();
        Connection conn = dbConnectionManager.getConnection("mypool");
        try {
            String insert = "insert into client_job_table values ("+job.getId()
                    +","+job.getFile_id()+","+job.getPid()
                    +","+job.isRun()+",'"+job.getCommand()+"',"+job.getCpu()+","
                    +job.getMem()+",'"+job.getStart_time()+"','"+job.getDuration()+"');";
            System.out.println(insert);
            statement = conn.createStatement();
            statement.executeUpdate(insert);
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("addAppInfo has a SQL error!");
            flag = false;
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    logger.error("addAppInfo has a close stream at SQL error!");
                }
            }
            dbConnectionManager.freeConnection("mypool",conn);
        }
        System.out.println("add App successfully in database.");
        return flag;
    }

    public boolean removeJob(Job job){
        Statement statement = null;
        boolean status = false;

        DBConnectionManager dbConnectionManager = DBConnectionManager.getInstance();
        Connection conn = dbConnectionManager.getConnection("mypool");
        try {
            statement = conn.createStatement();
            String sql = "delete from client_job_table where id = "+job.getId()+ ";";
            System.out.println(sql);
            int count = statement.executeUpdate(sql);
            System.out.println(count + "file(s) be deleted. ");
            status = true;
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("addAppInfo has a SQL error!");
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    logger.error("addAppInfo has a close statement SQL error!");
                }
            }
            dbConnectionManager.freeConnection("mypool",conn);
        }
        return status;
    }

    public boolean updateJob(Job job){
        boolean status = false;
        if(removeJob(job) && addJob(job)){
            status = true;
        }
        return status;
    }
}
