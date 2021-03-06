package com.elderdrivers.riru.xposed.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import de.robv.android.xposed.SELinuxHelper;

import static com.elderdrivers.riru.xposed.config.InstallerChooser.INSTALLER_DATA_BASE_DIR;
import static com.elderdrivers.riru.xposed.config.InstallerChooser.INSTALLER_PACKAGE_NAME;

public class ConfigManager {

    private static final String BLACK_LIST_PATH = INSTALLER_DATA_BASE_DIR + "conf/blacklist/";
    private static final String WHITE_LIST_PATH = INSTALLER_DATA_BASE_DIR + "conf/whitelist/";
    private static final String COMPAT_LIST_PATH = INSTALLER_DATA_BASE_DIR + "conf/compatlist/";
    private static final String USE_WHITE_LIST = INSTALLER_DATA_BASE_DIR + "conf/usewhitelist";
    private static final String DYNAMIC_MODULES = INSTALLER_DATA_BASE_DIR + "conf/dynamicmodules";
    private static final Set<String> WHITE_LIST = Collections.singleton(INSTALLER_PACKAGE_NAME);
    private static final HashMap<String, Boolean> compatModeCache = new HashMap<>();
    private static volatile boolean IS_DYNAMIC_MODULES = false;

    public static boolean isDynamicModulesMode() {
        return IS_DYNAMIC_MODULES;
    }

    public static synchronized void setDynamicModulesMode(boolean isDynamicModulesMode) {
        if (isDynamicModulesMode != IS_DYNAMIC_MODULES) {
            IS_DYNAMIC_MODULES = isDynamicModulesMode;
        }
    }

    public static boolean shouldUseWhitelist() {
        return isFileExists(USE_WHITE_LIST);
    }

    public static boolean shouldUseCompatMode(String packageName) {
        Boolean result;
        if (compatModeCache.containsKey(packageName)
                && (result = compatModeCache.get(packageName)) != null) {
            return result;
        }
        result = isFileExists(COMPAT_LIST_PATH + packageName);
        compatModeCache.put(packageName, result);
        return result;
    }

    public static boolean shouldHook(String packageName) {
        if (WHITE_LIST.contains(packageName)) {
            return true;
        }
        if (shouldUseWhitelist()) {
            return isFileExists(WHITE_LIST_PATH + packageName);
        } else {
            return !isFileExists(BLACK_LIST_PATH + packageName);
        }
    }

    private static boolean isFileExists(String path) {
        return SELinuxHelper.getAppDataFileService().checkFileExists(path);
    }
}
