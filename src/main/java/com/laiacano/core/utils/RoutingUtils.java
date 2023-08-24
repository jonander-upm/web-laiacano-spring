package com.laiacano.core.utils;

import org.springframework.beans.factory.annotation.Value;

public class RoutingUtils {
    private static final String LOCAL_URL = "http://localhost:4200";
    private static final String DEV_URL = "https://indigo-skyline-386510.web.app/";
    private static final String PROD_URL = ""; // TODO: Update prod URL
    private final String activeProfile;
    
    public RoutingUtils(String activeProfile) {
        this.activeProfile = activeProfile;
    }
    public String getFrontUrl() {
        String url = "";
        if(this.activeProfile.equals("prod")) {
            url = PROD_URL;
        } else if(this.activeProfile.equals("dev")) {
            url = DEV_URL;
        } else {
            url = LOCAL_URL;
        }
        return url;
    }
}
