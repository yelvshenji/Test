package com.unionbigdata.managementplatform.crawlermodule.client.webserver.conf;

/**
 * Configuration attributes of the system.
 * These configurations are stored in an configuration file in format of xml.
 * If you want to change these configurations,edit the configuration file.
 * <p>More information,see {@link ConfManager}</p>
 * @author yy
 * @version 1.0
 */
public enum  ConfAttributes {

    DEFAULT_PATH("default_path","/home/lwj/");

//    DFS_SERVER_PORT("dfs_server_port","4096"),
//
//    MAX_PROCESS_NUM("max_process_num","5"),
//
//    MAX_CONNECT_RETRY_TIME("max_connect_retry_time","3");

    private String name;
    private String value;
    /**
     * Get the key name of this configuration attribute in the configuration file.
     * @return key name.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the value of this configuration attribute.
     * @return value
     */
    public String getValue() {
        return value;
    }
    ConfAttributes(String name, String value){
        this.name = name;
        this.value = value;
    }
}
