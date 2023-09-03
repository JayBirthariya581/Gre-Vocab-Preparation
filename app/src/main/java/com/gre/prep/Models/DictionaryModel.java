package com.gre.prep.Models;

import java.io.Serializable;

public class DictionaryModel implements Serializable {
    String name,url;

    public DictionaryModel(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
