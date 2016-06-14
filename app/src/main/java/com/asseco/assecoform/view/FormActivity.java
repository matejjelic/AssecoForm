package com.asseco.assecoform.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.asseco.assecoform.R;
import com.asseco.assecoform.controller.UrlController;

import org.apache.commons.validator.routines.UrlValidator;

import java.net.MalformedURLException;
import java.net.URL;

public class FormActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String LOG_TAG = "FormActivity";
    private EditText url;
    private Button calculateHash;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        initialize();
    }

    private void initialize() {
        url = (EditText) findViewById(R.id.etUrl);
        calculateHash = (Button) findViewById(R.id.bttnCalculateHash);
        calculateHash.setOnClickListener(this);
        progressBar = (ProgressBar) findViewById(R.id.pbLoading);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bttnCalculateHash:
                fetchAndStoreHashedContent();
                break;

            default:
                break;
        }
    }

    private URL getUrlFromString(String urlString) {
        URL result = null;

        try {
            result = new URL(urlString);
        } catch (MalformedURLException e) {
            Log.d(LOG_TAG, e.getMessage(), e);
        }

        return result;
    }

    private void fetchAndStoreHashedContent() {
        Editable urlText = url.getText();
        if (urlText != null && !url.getText().toString().isEmpty()) {
            String urlString = url.getText().toString();
            urlString = correctUrl(urlString);
            boolean isUrlValid = validateURL(urlString);
            if (isUrlValid) {
                URL u = getUrlFromString(urlString);
                UrlController controller = new UrlController(u, this, progressBar, calculateHash);
                controller.execute();
            } else {
                Toast.makeText(this, R.string.invalidUrl, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, R.string.urlIsEmpty, Toast.LENGTH_SHORT).show();
        }
    }

    private String correctUrl(String url) {
        String result = url;
        if (!url.contains("http://") && !url.contains("https://")) {
            result = "http://" + url;
        }

        return result;
    }

    private boolean validateURL(String url) {
        UrlValidator urlValidator = new UrlValidator();
        return urlValidator.isValid(url);
    }

}
