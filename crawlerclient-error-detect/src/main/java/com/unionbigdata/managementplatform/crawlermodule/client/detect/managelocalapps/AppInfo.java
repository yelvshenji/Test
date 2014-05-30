package com.unionbigdata.managementplatform.crawlermodule.client.detect.managelocalapps;

/**
 * Created by lwj on 14-3-20.
 */
public class AppInfo {
    private int id;
    private String ip;
    private String path;
    private String name;
    private String version;
    private long size;
    private String creationDate;

    public AppInfo() {
    }

    public AppInfo(int id, String ip, String path, String name, String version, long size, String creationDate) {
        this.id = id;
        this.ip = ip;
        this.path = path;
        this.name = name;
        this.version = version;
        this.size = size;
        this.creationDate = creationDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }
}
