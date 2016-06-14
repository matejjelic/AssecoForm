package com.asseco.assecoform.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.asseco.assecoform.R;
import com.asseco.assecoform.model.WebContentHash;
import com.asseco.assecoform.model.WebContentHashDataSource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by matej on 6/13/16.
 */
public class UrlUtils extends AsyncTask<Void, String, String> {

    private static final String LOG_TAG = "UrlUtils";
    private URL url;
    private Context context;
    private ProgressBar progressBar;
    private boolean urlAlreadyStored;

    public UrlUtils(URL url, Context context, ProgressBar progressBar) {
        this.url = url;
        this.context = context;
        this.progressBar = progressBar;
        this.urlAlreadyStored = false;
    }

    public String getHashedWebsiteContent() {
        StringBuffer result = null;
        String body = getWebsiteContent();

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(body.getBytes());

            byte byteData[] = md.digest();
            result = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                String hex = Integer.toHexString(0xff & byteData[i]);
                if (hex.length() == 1) result.append('0');
                result.append(hex);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return result.toString();
    }

    /**
     * Gets website content for a given URL.
     * Source http://stackoverflow.com/questions/5867975/reading-websites-contents-into-string
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
            } else {
                encounteredError = true;
            }
        } catch (IOException e) {
            encounteredError = true;
            Log.d(LOG_TAG, e.getMessage(), e);
        }

        if (encounteredError) {
            cancel(true);
            Toast.makeText(context, R.string.errorHashingUrl, Toast.LENGTH_SHORT).show();
        }

        return result;
    }

    private void storeHash(String hash) {
        String firstMd5ByteHex = hash.substring(0, 1);
        int firstMd5ByteInt = Integer.parseInt(firstMd5ByteHex, 16);

        // if the first byte is even - store to DB, if it's odd - store to SharedPrefs
        if (firstMd5ByteInt % 2 == 0) {
            storeToDatabase(hash);
        } else {
            Toast.makeText(context, "Hash " + hash + " for URL " + url.toString() + " is stored in SharedPreferences.", Toast.LENGTH_LONG).show();
//            storeToSharedPrefs();
        }
    }

    private void storeToDatabase(String md5) {
        WebContentHashDataSource ds = new WebContentHashDataSource(context);

        WebContentHash contentHash = new WebContentHash(url.toString(), md5);
        ds.insertWebContentHash(contentHash);
        System.out.println("***** Stored in DB: [" + url.toString() + ", " + md5 + "]");
        Toast.makeText(context, "Hash " + md5 + " for URL " + url.toString() + " is stored in Database.", Toast.LENGTH_LONG).show();
    }

    private boolean urlExistsInStorage() {
        boolean result;
        WebContentHashDataSource ds = new WebContentHashDataSource(context);
        String hash = ds.getHashForUrl(url.toString());

        if (hash != null && !hash.isEmpty()) {
            result = true;
            Toast.makeText(context, "Hash " + hash + " for URL " + url.toString() + " is already stored in Database.", Toast.LENGTH_LONG).show();
        } else {
            result = false;
        }

        return result;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        urlAlreadyStored = urlExistsInStorage();
        if (!urlAlreadyStored) {
            progressBar.setVisibility(ProgressBar.VISIBLE);
            progressBar.animate();
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        progressBar.setVisibility(ProgressBar.GONE);
        storeHash(s);
    }

    @Override
    protected String doInBackground(Void... params) {
        String result = "";
        if (urlAlreadyStored) {
            cancel(true);
        } else {
            publishProgress("Hashing website content...");
            result = getHashedWebsiteContent();
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

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public boolean isUrlAlreadyStored() {
        return urlAlreadyStored;
    }

    public void setUrlAlreadyStored(boolean urlAlreadyStored) {
        this.urlAlreadyStored = urlAlreadyStored;
    }
}
