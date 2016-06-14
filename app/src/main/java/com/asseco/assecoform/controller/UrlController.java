package com.asseco.assecoform.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.asseco.assecoform.R;
import com.asseco.assecoform.controller.timer.ButtonTimer;
import com.asseco.assecoform.model.WebContentHash;
import com.asseco.assecoform.model.WebContentHashDataSource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Timer;

/**
 * Created by matej on 6/13/16.
 */
public class UrlController extends AsyncTask<Void, String, String> {

    private static final String LOG_TAG = "UrlController";
    boolean encounteredError;
    private URL url;
    private Context context;
    private ProgressBar progressBar;
    private Button button;
    private boolean urlAlreadyStored;
    private SharedPreferences sharedPreferences;

    public UrlController(URL url, Context context, ProgressBar progressBar, Button button) {
        this.url = url;
        this.context = context;
        this.progressBar = progressBar;
        this.urlAlreadyStored = false;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.button = button;
        this.encounteredError = false;
    }

    public String getHashedWebsiteContent() {
        StringBuffer result = null;
        String body = getWebsiteContent();

        if (body != null) {
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
        }

        if (result != null) {
            return result.toString();
        } else {
            return null;
        }

    }

    /**
     * Gets website content for a given URL.
     * Source http://stackoverflow.com/questions/5867975/reading-websites-contents-into-string
     */
    private String getWebsiteContent() {
        String result = null;

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
//            Log.d(LOG_TAG, e.getMessage(), e);
        }

        if (encounteredError) {
            cancel(true);
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
            storeToSharedPreferences(hash);
        }
    }

    private void storeToDatabase(String md5) {
        WebContentHashDataSource ds = new WebContentHashDataSource(context);
        WebContentHash contentHash = new WebContentHash(url.toString(), md5);
        ds.insertWebContentHash(contentHash);

        Toast.makeText(context, "Hash " + md5 + " for URL " + url.toString() + " is stored in Database.", Toast.LENGTH_LONG).show();
    }

    private void storeToSharedPreferences(String md5) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(url.toString(), md5);
        editor.commit();

        Toast.makeText(context, "Hash " + md5 + " for URL " + url.toString() + " is stored in SharedPreferences.", Toast.LENGTH_LONG).show();
    }

    private boolean urlExistsInStorage() {
        boolean result;

        if (urlExistsInDatabase()) {
            result = true;
        } else result = urlExistsiInSharedPreferences();

        return result;
    }

    private boolean urlExistsInDatabase() {
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

    private boolean urlExistsiInSharedPreferences() {
        boolean result;
        if (sharedPreferences != null) {
            String hash = sharedPreferences.getString(url.toString(), "");
            if (!hash.isEmpty()) {
                result = true;
                Toast.makeText(context, "Hash " + hash + " for URL " + url.toString() + " is already stored in SharedPreferences.", Toast.LENGTH_LONG).show();
            } else {
                result = false;
            }
        } else {
            result = false;
        }
        return result;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        progressBar.setVisibility(ProgressBar.GONE);
        if (encounteredError) {
            Toast.makeText(context, R.string.errorHashingUrl, Toast.LENGTH_SHORT).show();
        }
        button.setClickable(true);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        urlAlreadyStored = urlExistsInStorage();
        if (!urlAlreadyStored) {
            progressBar.setVisibility(ProgressBar.VISIBLE);
            progressBar.animate();
            button.setClickable(false);
        } else {
            disableButtonForFiveSeconds();
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        progressBar.setVisibility(ProgressBar.GONE);
        button.setClickable(true);
        if (encounteredError) {
            progressBar.setVisibility(ProgressBar.GONE);
        }
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
            if (result == null || result.isEmpty()) {
                cancel(true);
            }
        }

        return result;
    }

    private void disableButtonForFiveSeconds() {
        Timer timer = new Timer();
        timer.schedule(new ButtonTimer(button), 0, 1000);
    }

}
