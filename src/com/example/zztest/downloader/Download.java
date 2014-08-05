package com.example.zztest.downloader;

import java.io.*;
import java.net.*;
import java.util.*;

import android.os.Environment;
import android.util.Log;

// This class downloads a file from a URL.
public class Download extends Observable implements Runnable {

    public static String SDPATH = Environment.getExternalStorageDirectory().toString() + "/51voa/cache";

    private static String TAG = "Download";

    private static String cachefileInfo = SDPATH + "/info_";

    // Max size of download buffer.
    private static final int MAX_BUFFER_SIZE = 1024;

    // These are the status names.
    public static final String STATUSES[] = {"Idle", "Downloading", "Paused", "Complete", "Cancelled", "Error"};

    // These are the status codes.
    public static final int IDLE = 0;
    public static final int DOWNLOADING = 1;
    public static final int PAUSED = 2;
    public static final int COMPLETE = 3;
    public static final int CANCELLED = 4;
    public static final int ERROR = 5;

    private URL url; // download URL
    private int size; // size of download in bytes
    private int downloaded; // number of bytes downloaded
    private int status; // current status of download
    private String localFilename;
    private String key;

    // Constructor for Download.
    public Download(URL url, String key) {
        this.url = url;
        this.key = key;
        size = -1;
        downloaded = 0;
        status = IDLE;
        Download.createDownloadfold();
        localFilename = SDPATH + "/" + getFileName();
        getDownloadedprogress();
    }

    private synchronized void getDownloadedprogress() {
        String cachefilename = cachefileInfo + getFileName() + ".tmp";

        String lengthstr = CacheToFile.readFile(cachefilename);

        if (lengthstr != null && !lengthstr.isEmpty()) {
            try {
                downloaded = Integer.parseInt(lengthstr);
            } catch (NumberFormatException e) {
                downloaded = 0;
            }
        }
    }

    private synchronized void wirteDownloadedInfo(String length) {

        String cachefilename = cachefileInfo + getFileName() + ".tmp";

        Log.d(TAG, "wirte to file, length = " + length);
        CacheToFile.writeFile(cachefilename, length.getBytes());
    }

    private synchronized boolean deleteDownloadedInfo() {

        String cachefilename = cachefileInfo + getFileName() + ".tmp";
        File file = new File(cachefilename);
        if (file.exists() && file.isFile()) {
            return file.delete();
        }
        return false;
    }

    public static void createDownloadfold() {

        File file = new File(Environment.getExternalStorageDirectory().toString() + "/51voa/cache/");
        if (!file.exists()) {
            file.mkdirs();
        }

    }

    // Get this download's URL.
    public String getUrl() {
        return url.toString();
    }

    // Get this download's URL.
    public String getLocalFilename() {
        return localFilename;
    }

    // Get this download's size.
    public int getSize() {
        return size;
    }

    // Get this download's progress.
    public float getProgress() {
        return ((float) downloaded / size) * 100;
    }

    // Get this download's status.
    public int getStatus() {

        Log.d(TAG, "getStatus() ");
        return status;
    }

    // Pause this download.
    public void pause() {
        Log.d(TAG, "pause() ");
        status = PAUSED;
        stateChanged();
    }

    // Resume this download.
    public void resume() {
        Log.d(TAG, "resume() ");
        status = DOWNLOADING;
        stateChanged();
        download();
    }

    // Cancel this download.
    public void cancel() {
        Log.d(TAG, "cancel() ");
        status = CANCELLED;
        stateChanged();
    }

    // Mark this download as having an error.
    private void error() {
        Log.d(TAG, "error() ");
        status = ERROR;
        stateChanged();
    }

    // Start or resume downloading.
    public void download() {
        Log.d(TAG, "download() ");
        status = DOWNLOADING;
        Thread thread = new Thread(this);
        thread.start();
    }

    // Get file name portion of URL.
    private String getFileName() {
        String fileName = url.getFile();
        return fileName.substring(fileName.lastIndexOf('/') + 1);
    }

    // Download file.
    public void run() {

        RandomAccessFile file = null;

        InputStream stream = null;

        try {
            // Open connection to URL.
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Specify what portion of file to download.
            connection.setRequestProperty("Range", "bytes=" + downloaded + "-");

            // Connect to server.
            connection.connect();

            // Make sure response code is in the 200 range.
            if (connection.getResponseCode() / 100 != 2) {
                error();
            }
            // Check for valid content length.
            int contentLength = connection.getContentLength();
            if (contentLength < 1) {
                error();
            }
            // Set the size for this download if it hasn't been already set.
            if (size == -1) {
                size = contentLength;
                stateChanged();
            }
            if (downloaded > size) {
                downloaded = 0;
            }

            Log.d(TAG, "download() localFilename = " + localFilename + ", downloaded = " + downloaded + ", size = "
                    + size);
            // Open file and seek to the end of it.
            file = new RandomAccessFile(localFilename, "rw");
            file.seek(downloaded);

            stream = connection.getInputStream();

            byte buffer[];
            while (status == DOWNLOADING) {
                // Size buffer according to how much of the file is left to
                // download.
                if (size - downloaded > MAX_BUFFER_SIZE) {
                    buffer = new byte[MAX_BUFFER_SIZE];
                } else {
                    buffer = new byte[size - downloaded];
                }

                // Read from server into buffer.
                int read = stream.read(buffer);
                if (read == -1) {
                    Log.d(TAG, "read length is -1");
                    break;
                }

                // Write buffer to file.
                file.write(buffer, 0, read);
                downloaded += read;
                stateChanged();

            }

            // Change status to complete if this point was reached because
            // downloading has finished.
            if (status == DOWNLOADING) {
                status = COMPLETE;
                deleteDownloadedInfo();
                stateChanged();
            } else {
                wirteDownloadedInfo(downloaded + "");
            }

        } catch (Exception e) {
            wirteDownloadedInfo(downloaded + "");
            error();
        } finally {
            // Close file.
            if (file != null) {
                try {
                    file.close();
                } catch (Exception e) {
                }
            }

            // Close connection to server.
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e) {
                }
            }
        }
    }

    // Notify observers that this download's status has changed.
    private void stateChanged() {
        setChanged();
        notifyObservers();
    }

}
