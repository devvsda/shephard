package com.devsda.platform.shepherdclient.model;

import com.devsda.platform.shepherd.model.DataSourceDetails;
import com.devsda.platform.shepherd.model.ServerDetails;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class ShepherdServerConfiguration {

    @JsonProperty("server")
    private ServerDetails serverDetails;
    @JsonProperty("datasource")
    private DataSourceDetails dataSourceDetails;
    @JsonProperty("headers")
    private Map<String, String> headers;

    public DataSourceDetails getDataSourceDetails() {
        return dataSourceDetails;
    }

    public void setDataSourceDetails(DataSourceDetails dataSourceDetails) {
        this.dataSourceDetails = dataSourceDetails;
    }

    public ServerDetails getServerDetails() {
        return serverDetails;
    }

    public void setServerDetails(ServerDetails serverDetails) {
        this.serverDetails = serverDetails;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public String toString() {
        return "ShepherdServerConfiguration{" +
                "serverDetails=" + serverDetails +
                ", headers=" + headers +
                '}';
    }
}
