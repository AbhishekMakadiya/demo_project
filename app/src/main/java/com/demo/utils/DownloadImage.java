package com.demo.utils;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class DownloadImage {

    private String destFilePath;// = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
    private String destFileName;
    private OnDownloadImageListener onDownloadImageListener;

    public DownloadImage(String srcFilePath, String destFilePath, String destFileName, OnDownloadImageListener onDownloadImageListener) {
        this.destFilePath = destFilePath;
        this.destFileName = destFileName;
        this.onDownloadImageListener = onDownloadImageListener;

        new DownloadFromUrl().execute(srcFilePath);
    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadFromUrl extends AsyncTask<String, Void, String> {
        private String TAG = "DownloadImage";

        private String downloadImageBitmap(String sUrl) {
            return downloadImagesToSdCard(sUrl, destFileName, destFilePath);
        }

        @Override
        protected String doInBackground(String... strings) {
            return downloadImageBitmap(strings[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            LogHelper.INSTANCE.e(TAG, "onPostExecute : " + s);
            if (onDownloadImageListener != null) {
                onDownloadImageListener.onDownloadSuccess(s);
            }
        }
    }

    private String downloadImagesToSdCard(String downloadUrl, String imageName, String filepath) {
        try {
            URL url = new URL(downloadUrl);
            /* making a directory in sdcard */

            File myDir = new File(filepath);

            /*  if specified not exist create new */
            if (!myDir.exists()) {
                myDir.mkdir();
                LogHelper.INSTANCE.v("", "inside mkdir");
            }

            /* checks the file and if it already exist delete */
            File file = new File(myDir, imageName);
            if (file.exists()) {
//                return file.getAbsolutePath();
                boolean delete = file.delete();
            }

            /* Open a connection */
            URLConnection ucon = url.openConnection();
            InputStream inputStream = null;
            HttpURLConnection httpConn = (HttpURLConnection) ucon;
            httpConn.setRequestMethod("GET");
            httpConn.connect();

            if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = httpConn.getInputStream();
            }

            FileOutputStream fos = new FileOutputStream(file);
            int totalSize = httpConn.getContentLength();
            int downloadedSize = 0;
            byte[] buffer = new byte[1024];
            int bufferLength;
            if (inputStream != null) {
                while ((bufferLength = inputStream.read(buffer)) > 0) {
                    fos.write(buffer, 0, bufferLength);
                    downloadedSize += bufferLength;
                    LogHelper.INSTANCE.i("Progress:", "downloadedSize:" + downloadedSize + "totalSize:" + totalSize);
                }
            }
            fos.close();
            LogHelper.INSTANCE.d("test", "Image Saved in sdcard..");
            return file.getAbsolutePath();
        } catch (Exception e) {
            LogHelper.INSTANCE.printStackTrace(e);
            return "";
        }
    }

    public interface OnDownloadImageListener {
        void onDownloadSuccess(String filePath);
//        void onDownloadFailed(String message);
    }
}
