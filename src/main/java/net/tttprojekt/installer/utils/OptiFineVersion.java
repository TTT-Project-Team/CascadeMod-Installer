package net.tttprojekt.installer.utils;

import lombok.Getter;

public enum OptiFineVersion {

    V_1_12_2_HD_U_G5("OptiFine_1.12.2_HD_U_G5");


    private static final String OPTIFINE_URL = "http://optifined.net/adloadx.php?f=%s.jar";
    @Getter private final String version;

    OptiFineVersion(String version) {
        this.version = version;
    }

    public String getLink() {
        return String.format(OPTIFINE_URL, this.version);
    }

}
