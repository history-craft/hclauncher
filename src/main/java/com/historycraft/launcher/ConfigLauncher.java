package com.historycraft.launcher;

import java.util.ArrayList;
import java.util.List;

public class ConfigLauncher {

    private String urlMinecraft;
    private List<String> ignoredFiles;

    public List<String> getIgnoredFiles() {
        return ignoredFiles;
    }

    public void setIgnoredFiles(List<String> ignoredFiles) {
        this.ignoredFiles = ignoredFiles;
    }

    public void setUrlMinecraft(String urlMinecraft) {
        this.urlMinecraft = urlMinecraft;
    }

    public String getUrlMinecraft() {
        return urlMinecraft;
    }

    public void addIgnoredFiles(String file) {
        if (ignoredFiles== null ) {
            ignoredFiles = new ArrayList<>();
        }
        ignoredFiles.add(file);
    }


}
