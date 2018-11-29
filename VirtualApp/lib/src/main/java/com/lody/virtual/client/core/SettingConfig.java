package com.lody.virtual.client.core;

/**
 * @author Lody
 */
public abstract class SettingConfig {

    public abstract String getHostPackageName();

    public abstract String get64bitEnginePackageName();

    public String getBinderProviderAuthority() {
        return getHostPackageName() + ".virtual.service.BinderProvider";
    }

    public String get64bitHelperAuthority() {
        return get64bitEnginePackageName() + ".virtual.service.64bit_helper";
    }

    public String getProxyFileContentProviderAuthority() {
        return getHostPackageName() + ".virtual.fileprovider";
    }

    public boolean isEnableIORedirect() {
        return true;
    }

    public boolean isAllowCreateShortcut() {
        return true;
    }

    public boolean isUseRealDataDir(String packageName) {
        return false;
    }


    public boolean isDisableNotCopyApk(String pacakgeName){
        return false;
    }

    public enum AppLibConfig {
        UseRealLib,
        UseOwnLib,
        UseFakePathLib
    }

    public AppLibConfig getAppLibConfig(String packageName) {
        return AppLibConfig.UseRealLib;
    }

    public boolean isKeepAdminDeviceInfo() {
        return false;
    }

    public boolean isAllowServiceStartForeground() {
        return false;
    }

    public boolean autoCheckUpdateForNotCopyApk() {
        return true;
    }

    public boolean isEnableAppFileSystemIsolation() {
        return false;
    }

    public boolean isHideForegroundNotification() {
        return false;
    }

    public FakeWifiStatus getFakeWifiStatus() {
        return null;
    }

    public static class FakeWifiStatus {

        public static String DEFAULT_BSSID = "66:55:44:33:22:11";
        public static String DEFAULT_MAC = "11:22:33:44:55:66";
        public static String DEFAULT_SSID = "VA_SSID";

        public String getSSID() {
            return DEFAULT_SSID;
        }

        public String getBSSID() {
            return DEFAULT_BSSID;
        }

        public String getMAC() {
            return DEFAULT_MAC;
        }

    }

}
