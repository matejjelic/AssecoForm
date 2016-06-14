package com.asseco.assecoform.model;

/**
 * Created by mjelic on 14.06.16..
 */
public class WebContentHash {

    private String url;
    private String md5Hash;

    public WebContentHash() {

    }

    public WebContentHash(String url, String md5Hash) {
        this.url = url;
        this.md5Hash = md5Hash;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMd5Hash() {
        return md5Hash;
    }

    public void setMd5Hash(String md5Hash) {
        this.md5Hash = md5Hash;
    }
}
