package com.unionbigdata.managementplatform.crawlermodule.client.detect.managelocalapps;

/**
 * Created by lwj on 14-5-12.
 */
public class Job {
    private int id;
    private int file_id;
    private int pid;
    private boolean isRun;
    private String command;
    private float cpu;
    private long mem;
    private String start_time;
    private String duration;

    public Job() {
    }

    public Job(int id, int file_id,int pid, boolean isRun, String command, float cpu, long mem,
               String start_time, String duration) {
        this.id = id;
        this.file_id = file_id;
        this.pid = pid;
        this.isRun = isRun;
        this.command = command;
        this.cpu = cpu;
        this.mem = mem;
        this.start_time = start_time;
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFile_id() {
        return file_id;
    }

    public void setFile_id(int file_id) {
        this.file_id = file_id;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public boolean isRun() {
        return isRun;
    }

    public void setRun(boolean isRun) {
        this.isRun = isRun;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public float getCpu() {
        return cpu;
    }

    public void setCpu(float cpu) {
        this.cpu = cpu;
    }

    public long getMem() {
        return mem;
    }

    public void setMem(long mem) {
        this.mem = mem;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
