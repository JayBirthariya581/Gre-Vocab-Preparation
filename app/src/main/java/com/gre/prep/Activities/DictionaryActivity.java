package com.gre.prep.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.gre.prep.Adapters.DictionaryAdapter;
import com.gre.prep.Helper.CustomProgressDialog;
import com.gre.prep.Models.DictionaryModel;
import com.gre.prep.databinding.ActivityDictionaryBinding;


import java.util.ArrayList;
import java.util.List;

public class DictionaryActivity extends AppCompatActivity {

    ActivityDictionaryBinding binding;
    List<DictionaryModel> dictionaries;
    DictionaryAdapter dictionaryAdapter;
    String wordToSearch;
    CustomProgressDialog progressDialog;
    int defaultDictionary;
    LinearLayoutManager layoutManager;
    SharedPreferences sharedPreferences;
    String[] AD_DOMAINS_TO_BLOCK = {
            "doubleclick.net",
            "googleadservices.com",
            "admob.com",
            "adservice.google.com",
            "pagead2.googlesyndication.com",
            "pubads.g.doubleclick.net",
            "ads.pubmatic.com",
            "securepubads.g.doubleclick.net",
            "adserver.adtechus.com",
            "ad.doubleclick.net",
            "ad.yieldmanager.com",
            "ib.adnxs.com",
            "ads.yahoo.com",
            "c.amazon-adsystem.com",
            "advertising.com",
            "go.affec.tv",
            "pixel.advertising.com",
            "ad.crwdcntrl.net",
            "contextual.media.net",
            "engine.4dsply.com",
            "cdn.krxd.net",
            "bcp.crwdcntrl.net",
            "adserver.kiosked.com",
            "pixel.mathtag.com",
            "mads.amazon-adsystem.com",
            // Add more ad domains as needed
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDictionaryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


        wordToSearch = getIntent().getStringExtra("word").trim().toLowerCase();



        progressDialog = new CustomProgressDialog(DictionaryActivity.this);

        dictionaries = new ArrayList<>();
        dictionaries.add(new DictionaryModel("Google","https://www.google.com/search?q=define:"));
        dictionaries.add(new DictionaryModel("Oxford","https://www.oxfordlearnersdictionaries.com/definition/english/"));
        dictionaries.add(new DictionaryModel("Cambridge","https://dictionary.cambridge.org/dictionary/english/"));
        dictionaries.add(new DictionaryModel("Merriam Webster","https://www.merriam-webster.com/dictionary/"));
        dictionaries.add(new DictionaryModel("Collins","https://www.collinsdictionary.com/us/dictionary/english/"));
        dictionaries.add(new DictionaryModel("Vocabulary","https://www.vocabulary.com/dictionary/"));
        dictionaries.add(new DictionaryModel("Dictionary.com","https://www.dictionary.com/browse/"));
        dictionaries.add(new DictionaryModel("Translate","https://translate.google.com/?sl=en&tl=hi&text="));



        dictionaryAdapter = new DictionaryAdapter(DictionaryActivity.this,dictionaries);

        layoutManager = new LinearLayoutManager(DictionaryActivity.this,LinearLayoutManager.HORIZONTAL,false);

        binding.rv.setLayoutManager(layoutManager);
        binding.rv.setHasFixedSize(true);
        binding.rv.setAdapter(dictionaryAdapter);


        dictionaryAdapter.setDictionarySelectedListener(new DictionaryAdapter.DictionarySelectedListener() {
            @Override
            public void onDictionarySelected(DictionaryModel dictionary,int position) {

                progressDialog.show();

                layoutManager.scrollToPositionWithOffset(position, 100);

                loadUrl(dictionary.getUrl()+wordToSearch);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("defaultDictionary", position);
                editor.apply();

            }
        });



        configureWebView();


        defaultDictionary = sharedPreferences.getInt("defaultDictionary",1);

        dictionaryAdapter.setSelectedPosition(defaultDictionary);
        dictionaryAdapter.getDictionarySelectedListener().onDictionarySelected(dictionaries.get(defaultDictionary),defaultDictionary);
        dictionaryAdapter.notifyDataSetChanged();

    }



    private void configureWebView() {
        WebSettings webSettings = binding.webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Enable pinch zoom
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);

        // Improve rendering performance
        binding.webView.setLayerType(WebView.LAYER_TYPE_HARDWARE, null);

        // Set up WebViewClient to handle redirects within the WebView
        binding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                // Show the progress dialog when the page starts loading

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Dismiss the progress dialog when the page finishes loading
                progressDialog.dismiss();
            }

//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                // Check if the URL contains any ad domain to block
//                for (String adDomain : AD_DOMAINS_TO_BLOCK) {
//                    if (url.contains(adDomain)) {
//                        // Block the request by returning true
//                        return true;
//                    }
//                }
//
//                // Allow the request to continue loading
//                return false;
//            }
        });

        // Enable JavaScript
        webSettings.setJavaScriptEnabled(true);

        // Enable caching
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT); // Use cached resources if available


        // Set up WebChromeClient to handle JavaScript dialogs, etc.
        binding.webView.setWebChromeClient(new WebChromeClient());
    }

    private void loadUrl(String url) {
        binding.webView.loadUrl(url);
    }
}