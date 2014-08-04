package com.example.zztest.downloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import android.util.Log;

/**
 * @author Chris_Cui
 * Class:  CacheToFile
 */

public class CacheToFile {

    private static final String TAG = "CacheToFile";

    public static boolean deleteFile (String filename) {
        File file = new File(filename);
        if (file.exists() && file.isFile()) {
            return file.delete();
        }
        return false;
    }

    public static String readFile(String filename) {

        File file = new File(filename);
        FileInputStream fis = null;

        String ret = null;
        if (file.exists()) {

            try {
                fis = new FileInputStream(file);

                int len = fis.available();

                byte[] buffer = new byte[len];
                fis.read(buffer);

                ret = new String (buffer);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (fis != null) {
            try {
                fis.close();
            } catch (IOException e) {

                e.printStackTrace();
            }
        }

        return ret;
    }

    public static void writeFile(String filename, byte[] data) {
        if (filename != null) {
            Log.d(TAG, "wirte to file, Filename = " + filename);
            File file = new File(filename);

            File parent = file.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }

            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            FileOutputStream fos = null;

            try {
                fos = new FileOutputStream(file);

                fos.write(data);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (fos !=null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void appendString(String filename, String content) {
        if (filename != null && !filename.isEmpty()) {
            Log.d(TAG, "appendString to file, Filename = " + filename);
            RandomAccessFile file;
            try {
                file = new RandomAccessFile(filename, "rw");
                long length = file.length();
                file.seek(length);
                file.writeBytes(content);

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }
}
