package com.gre.prep.Helper;

public class PathBuilder {
    private StringBuilder path;
    private boolean first;

    public StringBuilder getPath() {
        return path;
    }

    public void setPath(StringBuilder path) {
        this.path = path;
    }

    public PathBuilder() {
        path = new StringBuilder();
        first = true;
    }

    public PathBuilder child(String child) {
        if (!first) {
            path.append("/");
        }
        path.append(child);
        first = false;
        return this;
    }

    public PathBuilder clear() {
        path = new StringBuilder();
        first = true;
        return this;
    }

    public String build() {
        return path.toString();
    }
}
