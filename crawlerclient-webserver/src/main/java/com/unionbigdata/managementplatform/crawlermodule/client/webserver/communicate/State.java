package com.unionbigdata.managementplatform.crawlermodule.client.webserver.communicate;

import com.unionbigdata.managementplatform.crawlermodule.client.webserver.bean.Result;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

/**
 * Created by lwj on 14-5-19.
 */
public class State {
    @POST
    @Path("isok")
    @Produces(MediaType.APPLICATION_JSON)
    public Result<String> isOK() {
        Result<String> result = new Result<String>();
        result.setStatus("success");
        result.setError_code("00000");
        result.setError_msg("状态正常");
        result.setData("");
        return result;
    }
}
