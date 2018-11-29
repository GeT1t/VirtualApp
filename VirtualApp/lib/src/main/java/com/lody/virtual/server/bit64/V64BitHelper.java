package com.lody.virtual.server.bit64;

import android.app.ActivityManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.MemoryFile;
import android.os.ParcelFileDescriptor;
import android.os.Process;

import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.env.VirtualRuntime;
import com.lody.virtual.client.ipc.ProviderCall;
import com.lody.virtual.helper.ArtDexOptimizer;
import com.lody.virtual.helper.compat.NativeLibraryHelperCompat;
import com.lody.virtual.helper.utils.FileUtils;
import com.lody.virtual.os.VEnvironment;
import com.lody.virtual.os.VUserInfo;
import com.lody.virtual.os.VUserManager;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dalvik.system.DexFile;

/**
 * @author Lody
 */
public class V64BitHelper extends ContentProvider {


    private static final String[] METHODS = {
            "getRunningAppProcess",
            "getRunningTasks",
            "getRecentTasks",
            "forceStop",
            "copyPackage",
            "cleanPackageData"

    };

    private static String getAuthority() {
        return VirtualCore.getConfig().get64bitHelperAuthority();
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }


    @Override
    public Bundle call(String method, String arg, Bundle extras) {
        if (METHODS[0].equals(method)) {
            return getRunningAppProcess64(extras);
        } else if (METHODS[1].equals(method)) {
            return getRunningTasks64(extras);
        } else if (METHODS[2].equals(method)) {
            return getRecentTasks64(extras);
        } else if (METHODS[3].equals(method)) {
            return forceStop64(extras);
        } else if (METHODS[4].equals(method)) {
            return copyPackage64(extras);
        } else if (METHODS[5].equals(method)) {
            return uninstallPackageData64(extras);
        }
        return null;
    }

    private Bundle uninstallPackageData64(Bundle extras) {
        int userId = extras.getInt("user_id", -1);
        String packageName = extras.getString("package_name");
        if (packageName == null) {
            return null;
        }
        if (userId == -1) {
            VEnvironment.getPackageResourcePath64(packageName).delete();
            FileUtils.deleteDir(VEnvironment.getDataAppPackageDirectory64(packageName));
            VEnvironment.getOdexFile64(packageName).delete();
            List<VUserInfo> userInfos = VUserManager.get().getUsers();
            if(userInfos != null) {
                for (VUserInfo info : userInfos) {
                    FileUtils.deleteDir(VEnvironment.getDataUserPackageDirectory64(info.id, packageName));
                }
            }
        } else {
            FileUtils.deleteDir(VEnvironment.getDataUserPackageDirectory64(userId, packageName));
        }
        return null;
    }

    private Bundle getRunningAppProcess64(Bundle extras) {
        ActivityManager am = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningAppProcessInfo> processes = new ArrayList<>(am.getRunningAppProcesses());
        Bundle res = new Bundle();
        res.putParcelableArrayList("running_processes", processes);
        return res;
    }

    private Bundle getRunningTasks64(Bundle extras) {
        ActivityManager am = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        int maxNum = extras.getInt("max_num", Integer.MAX_VALUE);
        ArrayList<ActivityManager.RunningTaskInfo> tasks = new ArrayList<>(am.getRunningTasks(maxNum));
        Bundle res = new Bundle();
        res.putParcelableArrayList("running_tasks", tasks);
        return res;
    }

    private Bundle getRecentTasks64(Bundle extras) {
        ActivityManager am = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        int maxNum = extras.getInt("max_num", Integer.MAX_VALUE);
        int flags = extras.getInt("flags", 0);
        ArrayList<ActivityManager.RecentTaskInfo> tasks = new ArrayList<>(am.getRecentTasks(maxNum, flags));
        Bundle res = new Bundle();
        res.putParcelableArrayList("recent_tasks", tasks);
        return res;
    }

    private Bundle forceStop64(Bundle extras) {
        Object pidOrPids = extras.get("target");
        if (pidOrPids instanceof Integer) {
            int pid = (int) pidOrPids;
            Process.killProcess(pid);
        } else if (pidOrPids instanceof int[]) {
            int[] pids = (int[]) pidOrPids;
            for (int pid : pids) {
                Process.killProcess(pid);
            }
        }
        return null;
    }

    private Bundle copyPackage64(Bundle extras) {
        boolean success = false;
        ParcelFileDescriptor fd = extras.getParcelable("fd");
        String packageName = extras.getString("package_name");
        if (fd != null && packageName != null) {
            File targetPath = VEnvironment.getPackageResourcePath64(packageName);
            try {
                FileInputStream is = new FileInputStream(fd.getFileDescriptor());
                FileUtils.writeToFile(is, targetPath);
                FileUtils.closeQuietly(is);
                VEnvironment.chmodPackageDictionary(targetPath);
                File libDir = VEnvironment.getAppLibDirectory64(packageName);
                NativeLibraryHelperCompat.copyNativeBinaries(targetPath, libDir);
                VEnvironment.linkUserAppLib(0, packageName);
                if (VirtualRuntime.isArt()) {
                    try {
                        ArtDexOptimizer.interpretDex2Oat(targetPath.getPath(), VEnvironment.getOdexFile64(packageName).getPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        DexFile.loadDex(targetPath.getPath(), VEnvironment.getOdexFile64(packageName).getPath(), 0).close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                success = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Bundle res = new Bundle();
        res.putBoolean("res", success);
        return res;
    }

    public static boolean is64BitProcessStarted() {
        try {
            new ProviderCall.Builder(VirtualCore.get().getContext(), getAuthority()).methodName("@").call();
            return true;
        } catch (IllegalAccessException e) {
            // ignore
        }
        return false;
    }

    public static List<ActivityManager.RunningAppProcessInfo> getRunningAppProcess64() {
        if (VirtualCore.get().is64BitEngineInstalled()) {
            Bundle res = new ProviderCall.Builder(VirtualCore.get().getContext(), getAuthority()).methodName(METHODS[0]).callSafely();
            if (res != null) {
                return res.getParcelableArrayList("running_processes");
            }
        }
        return Collections.emptyList();
    }

    public static List<ActivityManager.RunningTaskInfo> getRunningTasks64(int maxNum) {
        if (VirtualCore.get().is64BitEngineInstalled()) {
            Bundle res = new ProviderCall.Builder(VirtualCore.get().getContext(), getAuthority()).methodName(METHODS[1]).addArg("max_num", maxNum).callSafely();
            if (res != null) {
                return res.getParcelableArrayList("running_tasks");
            }
        }
        return Collections.emptyList();
    }


    public static List<ActivityManager.RecentTaskInfo> getRecentTasks64(int maxNum, int flags) {
        if (VirtualCore.get().is64BitEngineInstalled()) {
            Bundle res = new ProviderCall.Builder(VirtualCore.get().getContext(), getAuthority())
                    .methodName(METHODS[2])
                    .addArg("max_num", maxNum)
                    .addArg("flags", flags)
                    .callSafely();
            if (res != null) {
                return res.getParcelableArrayList("recent_tasks");
            }
        }
        return Collections.emptyList();
    }

    public static void forceStop64(int pid) {
        if (VirtualCore.get().is64BitEngineInstalled()) {
            new ProviderCall.Builder(VirtualCore.get().getContext(), getAuthority()).methodName(METHODS[3]).addArg("target", pid).callSafely();
        }
    }

    public static void forceStop64(int[] pids) {
        if (VirtualCore.get().is64BitEngineInstalled()) {
            new ProviderCall.Builder(VirtualCore.get().getContext(), getAuthority()).methodName(METHODS[3]).addArg("target", pids).callSafely();
        }
    }


    public static void uninstallPackageData64(int userId, String packageName) {
        if (VirtualCore.get().is64BitEngineInstalled()) {
            new ProviderCall.Builder(VirtualCore.get().getContext(), getAuthority()).methodName(METHODS[5])
                    .addArg("user_id", userId)
                    .addArg("package_name", packageName)
                    .callSafely();
        }
    }

    public static boolean copyPackage64(String packagePath, String packageName) {
        if (VirtualCore.get().is64BitEngineInstalled()) {
            try {
                FileInputStream is = new FileInputStream(packagePath);
                byte[] content = FileUtils.toByteArray(is);
                FileUtils.closeQuietly(is);
                final MemoryFile memoryFile = new MemoryFile("file_" + packageName, content.length);
                memoryFile.allowPurging(false);
                memoryFile.getOutputStream().write(content);
                FileDescriptor fd = mirror.android.os.MemoryFile.getFileDescriptor.call(memoryFile);
                ParcelFileDescriptor pfd = ParcelFileDescriptor.dup(fd);
                Bundle res = new ProviderCall.Builder(VirtualCore.get().getContext(), getAuthority())
                        .methodName(METHODS[4])
                        .addArg("fd", pfd)
                        .addArg("package_name", packageName)
                        .callSafely();
                memoryFile.close();
                if (res != null) {
                    return res.getBoolean("res");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
