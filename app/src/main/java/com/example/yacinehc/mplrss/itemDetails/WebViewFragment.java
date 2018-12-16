package com.example.yacinehc.mplrss.itemDetails;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.TextView;

import com.example.yacinehc.mplrss.R;

public class WebViewFragment extends Fragment {
    private static final String WEB_URL = "webUrl";

    private String webUrl;
    private WebView webView;


    public WebViewFragment() {

    }

    public static WebViewFragment newInstance(String webUrl) {
        WebViewFragment fragment = new WebViewFragment();
        Bundle args = new Bundle();
        args.putString(WEB_URL, webUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            webUrl = getArguments().getString(WEB_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        webView = new WebView(getContext());
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl(webUrl);
        return webView;
    }

}
