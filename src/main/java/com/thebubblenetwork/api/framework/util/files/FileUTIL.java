package com.thebubblenetwork.api.framework.util.files;

/**
 * ---------------------------------------------------------------------------------------------------------------------------
 * PvPSG plugin property Jacob Evans and The PvP Network
 * Any unauthorized distribution, viewing or editing without permission from the below will result in legal action
 * against you
 * Jacob Evans alias JAC0B, Ryan alias WillzaTeam
 * ----------------------------------------------------------------------------------------------------------------------------
 */

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileUTIL {
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    public static void copy(File src, File dest) throws IOException {
        if (src.isDirectory()) {
            if (!dest.exists()) {
                dest.mkdir();
            }
            String[] files = src.list();
            for (String file : files) {
                File srcFile = new File(src, file);
                File destFile = new File(dest, file);

                copy(srcFile, destFile);
            }
        } else {
            InputStream in = new FileInputStream(src);
            Object out = new FileOutputStream(dest);

            byte[] buffer = new byte['?'];
            int length;
            while ((length = in.read(buffer)) > 0) {
                ((OutputStream) out).write(buffer, 0, length);
            }
            in.close();
            ((OutputStream) out).close();
        }
    }

    public static void setPermissions(File dir, boolean writable, boolean readable, boolean runnable) {
        dir.setWritable(writable);
        dir.setReadable(readable);
        dir.setExecutable(runnable);
        if (dir.isDirectory()) {
            for (File f : dir.listFiles()) {
                setPermissions(f, writable, readable, runnable);
            }
        }
    }

    public static void unZip(String zipFile, String extractFolder) throws IOException {
        int BUFFER = 2048;
        File file = new File(zipFile);

        ZipFile zip = new ZipFile(file);
        String newPath = extractFolder;

        new File(newPath).mkdir();
        Enumeration zipFileEntries = zip.entries();
        while (zipFileEntries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
            String currentEntry = entry.getName();

            File destFile = new File(newPath, currentEntry);

            File destinationParent = destFile.getParentFile();

            destinationParent.mkdirs();
            if (!entry.isDirectory()) {
                BufferedInputStream is = new BufferedInputStream(zip.getInputStream(entry));

                byte[] data = new byte[BUFFER];

                FileOutputStream fos = new FileOutputStream(destFile);
                BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);
                int currentByte;
                while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
                    dest.write(data, 0, currentByte);
                }
                dest.flush();
                dest.close();
                is.close();
            }
        }
    }
}

