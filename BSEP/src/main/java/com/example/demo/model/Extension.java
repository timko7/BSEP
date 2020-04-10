package com.example.demo.model;

import java.io.Serializable;
import java.security.cert.X509Extension;
import java.util.Set;


public class Extension  implements X509Extension {

    private  int extnID;
    private boolean critical;
    private String  extnValue;

    public Extension(int extnID, boolean critical, String extnValue) {
        this.extnID = extnID;
        this.critical = critical;
        this.extnValue = extnValue;
    }

    @Override
    public boolean hasUnsupportedCriticalExtension() {
        return false;
    }

    @Override
    public Set<String> getCriticalExtensionOIDs() {
        return null;
    }

    @Override
    public Set<String> getNonCriticalExtensionOIDs() {
        return null;
    }

    @Override
    public byte[] getExtensionValue(String oid) {
        return new byte[0];
    }
}
