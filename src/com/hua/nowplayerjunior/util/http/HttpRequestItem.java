package com.hua.nowplayerjunior.util.http;


import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class HttpRequestItem {
    private String url;
    private List<NameValuePair> contents = new ArrayList<NameValuePair>();
    private List<NameValuePair> httpHeaderList = new ArrayList<NameValuePair>();
    
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    
    public List<NameValuePair> getContents() {
        return contents;
    }
    public void setContents(List<NameValuePair> contents) {
        this.contents = contents;
    }
    public void addContent(final String name, final String value) {
        this.contents.add(new BasicNameValuePair(name, value));
    }
    
    public List<NameValuePair> getHttpHeaderList() {
        return httpHeaderList;
    }
    public void setHttpHeaderList(List<NameValuePair> httpHeaderList) {
        this.httpHeaderList = httpHeaderList;
    }
    public void addHttpHeader(final String name, final String value) {
        this.httpHeaderList.add(new BasicNameValuePair(name, value));
    }
    @Override
    public String toString() {
        return "HttpRequestItem [url=" + url + ", contents=" + contents
                + ", httpHeaderList=" + httpHeaderList + "]";
    }
    

    
}

