package com.spiderbiggen.randomchampionselector.ddragon.tasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.util.Log;

import com.spiderbiggen.randomchampionselector.util.async.Progress;
import com.spiderbiggen.randomchampionselector.util.internet.DownloadCallback;
import com.spiderbiggen.randomchampionselector.util.internet.DownloadTask;
import com.spiderbiggen.randomchampionselector.util.internet.HttpRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created on 27-2-2018.
 *
 * @author Stefan Breetveld
 */
public class DownloadImageTask extends DownloadTask<DownloadImageTask.Entry, DownloadImageTask.Entry[]> {

    private static final String TAG = DownloadChampionsTask.class.getSimpleName();
    private AtomicInteger count;
    private int total;

    private boolean internet = true;

    public DownloadImageTask(@NonNull NetworkInfo networkInfo) {
        this(networkInfo, null, new AtomicInteger(), 0);
    }

    public DownloadImageTask(@NonNull NetworkInfo activeNetworkInfo, DownloadCallback<Entry[]> callback, AtomicInteger count, int total) {
        super(activeNetworkInfo, callback);
        this.count = count;
        this.total = total;
    }

    @Override
    protected void onNoInternet() {
        internet = false;
    }

    @Override
    protected Entry[] doInBackground(Entry... params) {
        try {
            for (Entry entry : params) {
                if (isCancelled()) {
                    break;
                }
                String url = entry.getUrl();
                File file = entry.getFile();
                Bitmap bitmap = null;
                if (file.exists()) {
                    try (FileInputStream stream = new FileInputStream(file)) {
                        bitmap = BitmapFactory.decodeStream(stream);
                    } catch (IOException e) {
                        exception = e;
                    }
                }
                if (bitmap == null && internet) {
                    if (url == null) {
                        throw new IllegalArgumentException("Url was null");
                    }
                    bitmap = downloadImage(url);
                    if (bitmap != null && (file.exists() || file.createNewFile())) {
                        try (FileOutputStream outputStream = new FileOutputStream(file)) {
                            bitmap.compress(Bitmap.CompressFormat.WEBP, 85, outputStream);
                        } catch (IOException e) {
                            exception = e;
                        }
                    }
                }
                updateProgress(Progress.DOWNLOAD_SUCCESS, count.incrementAndGet(), total);
            }
        } catch (Exception e) {
            exception = e;
        }
        return params;
    }

    @Override
    protected void onPostExecute(Entry[] result) {
        super.onPostExecute(result);
    }

    /**
     * Given a URL, sets up a connection and gets the HTTP response body from the server.
     * If the network request is successful, it returns the response body. Otherwise,
     * it will throw an IOException.
     */
    private Bitmap downloadImage(String url) throws IOException {
        try (HttpRequest request = new HttpRequest(url).connect()) {
            updateProgress(Progress.CONNECT_SUCCESS, 0, 0);
            int responseCode = request.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }
            try (InputStream stream = request.getResponseStream()) {
                updateProgress(Progress.GET_INPUT_STREAM_SUCCESS, 0, 0);
                Bitmap bitmap = BitmapFactory.decodeStream(stream);
                updateProgress(Progress.PROCESS_INPUT_STREAM_SUCCESS, 0, 0);
                return bitmap;
            }
        }
    }

    public static class Entry {
        private final String url;
        private final File file;

        public Entry(String url, @NonNull File file) {
            this.url = url;
            this.file = file;
        }

        public String getUrl() {
            return url;
        }

        public File getFile() {
            return file;
        }

        @Override
        public String toString() {
            return "Entry{" +
                    "url='" + url + '\'' +
                    ", file=" + file +
                    '}';
        }
    }

}
