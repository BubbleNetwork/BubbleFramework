package com.thebubblenetwork.api.framework.util.http;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Created by Jacob on 13/12/2015.
 */
public class DownloadUtil {
    public static void download(File to, String address, CopyOption option) throws Exception {
        Files.copy(download(address), to.toPath(), option);
    }

    public static void download(File to, String address) throws Exception {
        download(to, address, StandardCopyOption.REPLACE_EXISTING);
    }

    public static InputStream download(String address) throws Exception {
        return new URL(address).openStream();
    }
}
