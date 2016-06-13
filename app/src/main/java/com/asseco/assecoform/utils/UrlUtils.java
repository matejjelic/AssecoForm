package com.asseco.assecoform.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.asseco.assecoform.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by matej on 6/13/16.
 */
public class UrlUtils {

    private static final String LOG_TAG = "UrlUtils";
    private URL url;
    private Context context;

    public UrlUtils(URL url, Context context) {
        this.url = url;
        this.context = context;
    }

    public String getHashedWebsiteContent() {
        String result = null;
        String body = getWebsiteContent();

        return result;
    }

    /**
     * Source http://stackoverflow.com/questions/5867975/reading-websites-contents-into-string
     * Errors:
     * - http://stackoverflow.com/questions/6343166/how-to-fix-android-os-networkonmainthreadexception
     * Resolutions:
     * - http://stackoverflow.com/questions/9671546/asynctask-android-example
     * - http://stackoverflow.com/questions/9671546/asynctask-android-example
     */
    private String getWebsiteContent() {
        String result = null;
        boolean encounteredError = false;

        try {
            if (url != null) {
                URLConnection con = url.openConnection();
                InputStream in = con.getInputStream();
                String encoding = con.getContentEncoding() != null ? con.getContentEncoding() : "UTF-8";
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                byte[] buf = new byte[8192];
                int len = 0;
                while ((len = in.read(buf)) != -1) {
                    baos.write(buf, 0, len);
                }

                result = new String(baos.toByteArray(), encoding);
                System.out.println("***** BODY: " + result);
            } else {
                encounteredError = true;
            }
        } catch (IOException e) {
            encounteredError = true;
            Log.d(LOG_TAG, e.getMessage(), e);
        }

        if (encounteredError) {
            Toast.makeText(context, R.string.errorHashingUrl, Toast.LENGTH_SHORT).show();
        }

        return result;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

}
