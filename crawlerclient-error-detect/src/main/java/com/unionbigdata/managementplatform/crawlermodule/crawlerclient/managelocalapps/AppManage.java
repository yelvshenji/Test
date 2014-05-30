package com.unionbigdata.managementplatform.crawlermodule.crawlerclient.managelocalapps;

import com.unionbigdata.managementplatform.crawlermodule.crawlerclient.databasemanage.DatabaseManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

/**
 * 管理程序的运行，获取程序状态，杀死进程
 * 为单例类
 * 私有成员变量为list集合，里面存放运行单个程序的线程，线程中可以管理程序的流
 * Created by lwj on 14-3-20.
 */
public class AppManage {

    private List<threadRunApp> appThread;
    private String ip;
    private long totalMEMSize;

    public static class NestedAppMange {
        public static final AppManage INSTANCE = new AppManage();
    }

    public static AppManage getInstance() {
        return NestedAppMange.INSTANCE;
    }

    private AppManage() {
        appThread = new LinkedList<threadRunApp>();
        ip = getIP().getHostAddress();
        Runtime runtime = Runtime.getRuntime();
        totalMEMSize = runtime.totalMemory();
    }

    public List<threadRunApp> getAppThread() {
        return appThread;
    }

    /**
     * static function. use the path and name to run the app in the client.
     * call the threadRunApp class to achieve the multi-threading.
     * return the threadRunApp class generated in the function
     *
     */
    public static threadRunApp runApp(String path, String name, String parameter) throws IOException {
        threadRunApp td = new threadRunApp();
        td.setPath(path);
        td.setName(name);
        td.setParameter(parameter);
        Thread tdr = new Thread(td);
        tdr.start();
        return td;
    }

    public void killApp(int appPID, int signal) {
        String command = "kill -" + signal + " " + appPID;
        Runtime rt = Runtime.getRuntime();
        Process p = null;
        try {
            p = rt.exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * stop app and update the information in database
     */
    public boolean stopJob(int file_id) {
        boolean status = false;
        DatabaseManager dm = new DatabaseManager();
        Job job = dm.getJob(file_id);
        if(job.isRun()){
            String command = "kill -15 " + job.getPid();
            Runtime rt = Runtime.getRuntime();
            Process p = null;
            try {
                p = rt.exec(command);
                System.out.println("stop process " + job.getFile_id()+" pid:"+job.getPid() + " succeed.");
                job.setRun(false);
                removeElem(job.getPid());
                status = true;
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("stop process " + job.getFile_id()+" pid:"+job.getPid() + " failed.");
            }
        }else{
            System.out.println("this job is not running!");
            status = true;
        }
        return status;
    }

    public LinuxProcessInfo getAppState(int pid) {
        String[] command = new String[]{"/bin/sh", "-c", "ps aux|grep " + pid};
        Runtime rt = Runtime.getRuntime();
        Process p = null;
        BufferedReader br;
        LinuxProcessInfo linuxProcessInfo = null;
        try {
            p = rt.exec(command);
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            String[] st;
            while ((line = br.readLine()) != null) {
                st = line.split(" +");
                String cmd = "";
                for (int i = 10; i < st.length; i++) {
                    cmd += st[i];
                }
                if (Integer.parseInt(st[1]) == pid) {
                    linuxProcessInfo = new LinuxProcessInfo(st[0], Integer.parseInt(st[1]), Float.parseFloat(st[2]), Float.parseFloat(st[3]), st[4], st[5], st[6], st[7], st[8], st[9], cmd);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("stop process " + pid + " failed.");
        }
        return linuxProcessInfo;
    }

    private void removeElem(int pid) {
        for (int i = 0; i < appThread.size(); i++) {
            if (appThread.get(i).getPid() == pid) {
                appThread.remove(i);
            }
        }
    }

    private InetAddress getIP(){
        Enumeration allNetInterfaces = null;
        try {
            allNetInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        InetAddress ip = null;
        while (allNetInterfaces.hasMoreElements())
        {
            NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
            if(netInterface.getName().compareTo("lo") == 0){
                continue;
            }
            Enumeration addresses = netInterface.getInetAddresses();
            while (addresses.hasMoreElements())
            {
                ip = (InetAddress) addresses.nextElement();
                if (ip != null && ip instanceof Inet4Address)
                {
                    break;
                }
            }
        }
        return ip;
    }

    public String getIp(){
        return ip;
    }

    public long getTotalMEMSize() {
        return totalMEMSize;
    }

}
