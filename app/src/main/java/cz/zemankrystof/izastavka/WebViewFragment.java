package cz.zemankrystof.izastavka;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;

import cz.zemankrystof.izastavka.data.Constants;

import static cz.zemankrystof.izastavka.data.Constants.item1URLcontent;
import static cz.zemankrystof.izastavka.data.Constants.item2URLcontent;
import static cz.zemankrystof.izastavka.data.Constants.item3URLcontent;
import static cz.zemankrystof.izastavka.data.Constants.item4URLcontent;

public class WebViewFragment extends Fragment {

    @Override
    public void onDestroy() {
        super.onDestroy();
        webView.stopLoading();
        webView.destroy();
        mListener.onBottomBarIconClick(1, false);
    }

    public static WebViewFragment newInstance() {
        WebViewFragment myFragment = new WebViewFragment();
        Bundle args = new Bundle();
        args.putString("defaultURL", Constants.item4URL);
        myFragment.setArguments(args);
        return myFragment;
    }

    public WebViewFragment() {
    }

    private OnBottomBarIconClick mListener;

    // Views needed for webView
    private WebView webView;
    private ProgressBar progressBar;
    private TextView progressTextView;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_webview,null);
        initializeWebView();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnBottomBarIconClick) {
            mListener = (OnBottomBarIconClick) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement OnHistoryDetailInfoSelected.");
        }
    }

    public void initializeWebView(){
        webView = (WebView)rootView.findViewById(R.id.webView);
        webView.setBackgroundColor(0x00000000);
        progressBar = (ProgressBar)rootView.findViewById(R.id.progressBar);
        progressTextView = (TextView)rootView.findViewById(R.id.progressTV);

        /*String userAgent = "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 Firefox/4.0";
        webView.getSettings().setUserAgentString(userAgent);*/

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        //webView.getSettings().setDomStorageEnabled(true);

        //webView.getSettings().setUseWideViewPort(true);
        //webView.getSettings().setLoadWithOverviewMode(true);

        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);

        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        //webView.getSettings().setSupportMultipleWindows(true);


        webView.setWebChromeClient(new WebChromeClient(){


            @Override
            public void onReceivedTitle(WebView view, String title) {
                if(getActivity() != null) {
                    if (getActivity().getWindow() != null) {
                        getActivity().getWindow().setTitle(title); //Set Activity tile to page title.
                    }
                }
            }

            @Override
            public void onProgressChanged(WebView view, int progress) {
                if(progress < 100 && progressBar.getVisibility() == ProgressBar.GONE){
                    progressBar.setVisibility(ProgressBar.VISIBLE);
                    progressTextView.setVisibility(View.VISIBLE);
                }

                progressBar.setProgress(progress);
                if(progress == 100) {
                    progressBar.setVisibility(ProgressBar.GONE);
                    progressTextView.setVisibility(View.GONE);
                }
            }
        });

        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //Toast.makeText(getContext(),"Zakázáno", Toast.LENGTH_SHORT);
                return true;
            }
        });

        webView.setLongClickable(false);

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.d("ERROR", "Received error!" + errorCode + description + failingUrl);
                //Toast.makeText(MainActivity.this, "Stránky s nevhodným obsahem blokovány!", Toast.LENGTH_SHORT).show();
                //view.goBack();
                //super.onReceivedError(view, errorCode, description, failingUrl);
                view.stopLoading();
                view.loadUrl(view.getOriginalUrl());
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                Log.d("ERROR", "onReceived error!" + error);
                //Toast.makeText(MainActivity.this, "Stránky s nevhodným obsahem blokovány!", Toast.LENGTH_SHORT).show();
                view.stopLoading();
                //view.loadUrl(view.getOriginalUrl());
                super.onReceivedError(view,request,error);
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                Log.d("ERROR", "onHttpReceived error!" + errorResponse.getReasonPhrase());
                //Toast.makeText(MainActivity.this, "Stránky s nevhodným obsahem blokovány!", Toast.LENGTH_SHORT).show();
                //view.stopLoading();
                //view.loadUrl(view.getOriginalUrl());
                super.onReceivedHttpError(view,request,errorResponse);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e("URL", "url: " + url);
                if(url.contains("blockedDomain") || (stringContainsItemFromList(url.toLowerCase()))) {
                    view.stopLoading();
                    Toast.makeText(getActivity(), "Stránky s nevhodným obsahem blokovány!", Toast.LENGTH_SHORT).show();
                    //view.goBack();
                    Log.d("URL",url);
                    return true;
                }else{
                    view.loadUrl(url);
                    Log.d("URL",url);
                }
                return false;
            }

/*
            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                if(stringContainsUrlFromList(request.getUrl().toString())) {
                    Log.e("Passing", "url: " + request.getUrl());
                    return null;
                }else{
                    Log.e("Blocking", "url: " + request.getUrl());
                    return new WebResourceResponse("text/plain", "utf-8", new ByteArrayInputStream("".getBytes()));
                }
            }
*/
            @Override
            public void onLoadResource(WebView view, String url) {
                Log.e("resource", "url: " + url);
                    super.onLoadResource(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                webView.setBackgroundColor(Color.WHITE);
                Log.d("URLS", "First " + url + " Second " + webView.getUrl());
                int itemClicked = 4;
                boolean canGoBack = false;
                /*if(url.contains(item1URLcontent)){
                   itemClicked = 1;
                } else if (url.contains(item2URLcontent)){
                    itemClicked = 2;
                } else if (url.contains(item3URLcontent)){
                    itemClicked = 3;
                } else if (url.contains(item4URLcontent)){
                    itemClicked = 4;
                }*/
                if (webView.canGoBack()){
                    canGoBack = true;
                }else{
                    canGoBack = false;
                }
                mListener.onBottomBarIconClick(itemClicked,canGoBack);
            }
        });


        Bundle args = getArguments();
        webView.loadUrl(args.getString("defaultURL"));

    }

    /**
     * Soo sorry to put such a thing into code, but at this time there is no better solution
     * @param inputStr String to be checked for rude words
     * @return  true if word is offensive, false otherwise
     */
    public static boolean stringContainsItemFromList(String inputStr) {
        String[] items = {
                "prcani", "prcat", "sukat", "suka", "mrdat", "mrd", "jeb", "kunda", "kurva", "pica", "prdel", "porno", "sex", "fuck", "fisting",
                "anal", "porking", "hovno", "soulozit", "whore", "slut", "milf", "defky", "devky", "slapka", "behna", "soustat", "curak", "cervix",
                "souloz"
        };
        for(int i =0; i < items.length; i++)
        {
            if(inputStr.contains(items[i]))
            {
                return true;
            }
        }
        return false;
    }


    public static boolean stringContainsUrlFromList(String inputStr) {
        String[] items = {
                "jizdnirady", "idnes", "ajax", "idos", "dpmb", "gotobrno", "ticbrno"
        };
        for(int i =0; i < items.length; i++)
        {
            if(inputStr.toLowerCase().contains(items[i]))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Interface used for propagating the changes to the main activity
     */
    public interface OnBottomBarIconClick{

        /**
         * Providing information about which position to recolor
         * @param position position of the bottom bar item
         * @param canGoBack information if the view can go back or not
         * @return
         */
        void onBottomBarIconClick(int position, boolean canGoBack);
    }

    /**
     * Method for setting the progress bar colors (text and color filter)
     * @param colorIcon The color to be set for progress bar
     */
    public void setProgressColor(int colorIcon){
        progressBar.getProgressDrawable().setColorFilter(colorIcon, PorterDuff.Mode.SRC_IN);
        progressTextView.setTextColor(colorIcon);
    }

    /**
     * Go back in webView
     */
    public void goBack(){
        webView.goBack();
    }

}
