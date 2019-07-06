package com.historycraft.launcher;

public class LocalConfig {

    private boolean useTlauncher;
    
    private String customLauncher;


    public String getCustomLauncher() {
        return customLauncher;
    }

    public boolean isUseTlauncher() {
        return useTlauncher;
    }

    public void setCustomLauncher(String customLauncher) {
        this.customLauncher = customLauncher;
    }

    public void setUseTlauncher(boolean useTlauncher) {
        this.useTlauncher = useTlauncher;
    }
}
