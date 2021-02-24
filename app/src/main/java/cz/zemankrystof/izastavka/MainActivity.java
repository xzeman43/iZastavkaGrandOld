package cz.zemankrystof.izastavka;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.lb.auto_fit_textview.AutoResizeTextView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import cz.zemankrystof.izastavka.data.model.Departure;
import cz.zemankrystof.izastavka.data.model.LinkSchema.DayLines;
import cz.zemankrystof.izastavka.data.model.LinkSchema.LinkSchemas;
import cz.zemankrystof.izastavka.data.model.LinkSchema.NightLines;
import cz.zemankrystof.izastavka.data.model.Stop;
import cz.zemankrystof.izastavka.data.model.StopTimetables.StopTimetables;
import cz.zemankrystof.izastavka.data.remote.ApiUtils;
import cz.zemankrystof.izastavka.data.remote.BannerService;
import cz.zemankrystof.izastavka.data.remote.FlixService;
import cz.zemankrystof.izastavka.data.remote.RJService;
import cz.zemankrystof.izastavka.data.remote.RetrofitClient;
import cz.zemankrystof.izastavka.data.remote.StopsService;
import cz.zemankrystof.izastavka.helpers.DayNightBR;
import cz.zemankrystof.izastavka.helpers.DownloadHelper;
import cz.zemankrystof.izastavka.helpers.MqttHelper;
import cz.zemankrystof.izastavka.helpers.DayNightWorkHelper;
import cz.zemankrystof.izastavka.helpers.TimetablesWorker;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static cz.zemankrystof.izastavka.data.Constants.item1IconActive;
import static cz.zemankrystof.izastavka.data.Constants.item1IconInactive;
import static cz.zemankrystof.izastavka.data.Constants.item2IconActive;
import static cz.zemankrystof.izastavka.data.Constants.item2IconInactive;
import static cz.zemankrystof.izastavka.data.Constants.item3IconActive;
import static cz.zemankrystof.izastavka.data.Constants.item3IconInactive;
import static cz.zemankrystof.izastavka.data.Constants.item4IconActive;
import static cz.zemankrystof.izastavka.data.Constants.item4IconInactive;
import static cz.zemankrystof.izastavka.data.Constants.postId;
import static cz.zemankrystof.izastavka.data.Constants.stopId;

public class MainActivity extends AppCompatActivity implements WebViewFragment.OnBottomBarIconClick{

    // Retrofit total recall
    private StopsService service;

    // Stops autorefresh handler
    Handler loadStopsHandler;
    Runnable loadStopsRunnable;

    //Device policies are essential :)
    private DevicePolicyManager mDpm;
    private ImageView bannerIV;

    private FlixService flixService;
    private RJService rjService;
    private BannerService bannerService;

    ConstraintLayout background_theme;

    //MQTT Rumble in the codejungle
    MqttHelper mqttHelper;

    private static List<Object> depList = new ArrayList<>();

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.e("CfgChanged", "RIGHT NOW");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e("onSaveInstanceS", "Called?");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e("OnCreate", "Activity Recreated!!");

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        initializeViews();

        if(savedInstanceState == null){
            Log.d("SavedInstance", "Null");

            //TODO uncomment
            ComponentName deviceAdmin = new ComponentName(this, AdminReceiver.class);
            mDpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            //mDpm.setKeyguardDisabled(deviceAdmin, true);
            if (!mDpm.isAdminActive(deviceAdmin)) {
                Toast.makeText(this, "No Admin rights!!!", Toast.LENGTH_SHORT).show();
            }

            if (mDpm.isDeviceOwnerApp(getPackageName())) {
                mDpm.setLockTaskPackages(deviceAdmin, new String[]{getPackageName()});
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mDpm.setKeyguardDisabled(deviceAdmin, true);
                }
            } else {
                Toast.makeText(this, "Admin privileges not received!!!", Toast.LENGTH_SHORT).show();
            }

            rjService = ApiUtils.getRJService();
            flixService = ApiUtils.getFlixService();
            bannerService = ApiUtils.getBannerService();
            startLockTask();

            background_theme.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {

                    Rect r = new Rect();
                    background_theme.getWindowVisibleDisplayFrame(r);
                    int screenHeight = background_theme.getRootView().getHeight();

                    // r.bottom is the position above soft keypad or device button.
                    // if keypad is shown, the r.bottom is smaller than that before.
                    int keypadHeight = screenHeight - r.bottom;

                    //Log.d("Keyboard", "keypadHeight = " + keypadHeight);

                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    //TODO uncomment
                    window.setStatusBarColor(Color.TRANSPARENT);

                    if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                        // keyboard is opened
                        View decorView = getWindow().getDecorView();
                        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                    }
                    else {
                        // keyboard is closed
                        View decorView = getWindow().getDecorView();
                        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                    }
                }
            });
        }

    }

    public void initializeViews() {
        bannerIV = this.findViewById(R.id.bannerIV);
        initializeBottomBar();
        initializeTopBar();
        initializeFrameLayout();

        setCurrentBotBarItem(bottom_bar_first_item_iv, bottom_bar_first_item_tv,R.color.first_item_icons,
                R.color.first_item_background, R.color.first_item_active, getResources().getDrawable(item1IconActive));

    }

    public void initializeTopBar(){
        topBar = findViewById(R.id.topBarLine);
        /*for (int i = 0; i < 6; i++) {
            View line = LayoutInflater.from(this).inflate(R.layout.line, null);
            if (i % 2 == 0){
                line.setBackgroundColor(getResources().getColor(R.color.black25));
            }
            topBar.addView(line);
        }*/
    }

    public void initializeFrameLayout(){
        //frameLayout = findViewById(R.id.frameLayout);
        FragmentManager supportFragmentManager = getSupportFragmentManager();

        DisplayCityTransportFragment displayCityTransportFragment = new DisplayCityTransportFragment();
        currentFragment = displayCityTransportFragment;
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.frameLayout, displayCityTransportFragment, "displayCity")
                .commit();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(inactivityHandler != null && inactivityRunnable != null) {
            inactivityHandler.removeCallbacks(inactivityRunnable);
            inactivityRunnable = null;
        }
        if(loadStopsHandler != null && loadStopsRunnable != null) {
            loadStopsHandler.removeCallbacks(loadStopsRunnable);
            loadStopsRunnable = null;
        }
        mqttHelper.disconnectMqtt();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("MainOnResume", "Called");
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        //service = ApiUtils.getStopsService();
        service = RetrofitClient.getClient().create(StopsService.class);
        startInactivityHandlers();
        startLoadStopsRefreshHandler();
        startMqtt();

    }

    @Override
    public void onBottomBarIconClick(int position, boolean canGoBack){

        /*switch (position){
            case 1:
                setCurrentBotBarItem(bottom_bar_first_item_iv, bottom_bar_first_item_tv,R.color.first_item_icons,
                        R.color.first_item_background, R.color.first_item_active, getDrawable(item1IconActive));
                //setProgressColor()
                break;

            case 2:
                setCurrentBotBarItem(bottom_bar_second_item_iv,bottom_bar_second_item_tv,R.color.second_item_icons,
                        R.color.second_item_background,R.color.second_item_active, getDrawable(item2IconActive));
                break;

            case 3:
                setCurrentBotBarItem(bottom_bar_third_item_iv,bottom_bar_third_item_tv,R.color.third_item_icons,
                        R.color.third_item_background,R.color.third_item_active, getDrawable(item3IconActive));
                break;

            case 4:
                setCurrentBotBarItem(bottom_bar_fourth_item_iv,bottom_bar_fourth_item_tv,R.color.fourth_item_icons,
                        R.color.fourth_item_background,R.color.fourth_item_active, getDrawable(item4IconActive));
        }*/

        if (canGoBack){
            if(bottom_bar_back_ll != null) {
                bottom_bar_back_ll.setVisibility(View.VISIBLE);
            }
        }else{
            if(bottom_bar_back_ll != null) {
                bottom_bar_back_ll.setVisibility(View.GONE);
            }
        }
    }

    public void initializeBottomBar(){
        bottom_bar_first_item_ll = (LinearLayout)findViewById(R.id.bottom_bar_first_item_ll);
        bottom_bar_second_item_ll = (LinearLayout)findViewById(R.id.bottom_bar_second_item_ll);
        bottom_bar_third_item_ll = (LinearLayout)findViewById(R.id.bottom_bar_third_item_ll);
        bottom_bar_fourth_item_ll = (LinearLayout)findViewById(R.id.bottom_bar_fourth_item_ll);
        bottom_bar_back_ll = (LinearLayout)findViewById(R.id.bottom_bar_back_ll);

        bottom_bar_first_item_iv = (ImageView)findViewById(R.id.bottom_bar_first_item_iv);
        bottom_bar_second_item_iv = (ImageView)findViewById(R.id.bottom_bar_second_item_iv);
        bottom_bar_third_item_iv = (ImageView)findViewById(R.id.bottom_bar_third_item_iv);
        bottom_bar_fourth_item_iv = (ImageView)findViewById(R.id.bottom_bar_fourth_item_iv);
        bottom_bar_back_iv = (ImageView)findViewById(R.id.bottom_bar_back_iv);

        bottom_bar_first_item_tv = (TextView)findViewById(R.id.bottom_bar_first_item_tv);
        bottom_bar_second_item_tv = (TextView)findViewById(R.id.bottom_bar_second_item_tv);
        bottom_bar_third_item_tv = (TextView)findViewById(R.id.bottom_bar_third_item_tv);
        bottom_bar_fourth_item_tv = (TextView)findViewById(R.id.bottom_bar_fourth_item_tv);
        bottom_bar_back_tv = (TextView)findViewById(R.id.bottom_bar_back_tv);

        background_theme = (ConstraintLayout) findViewById(R.id.background_theme);

        currently_selected_iv = bottom_bar_first_item_iv;
        currently_selected_tv = bottom_bar_first_item_tv;

        bottom_bar_back_ll.setVisibility(View.GONE);

        setBotBarListener();
    }

    /**
     * Initializes the weather bar views inside the main activity
     */
    public void initializeWeatherBar(){
        //weatherFace = Typeface.createFromAsset(getResources().getAssets(), "Ubuntu-Regular.ttf");
        tempTv = (TextView)findViewById(R.id.tempTv);
        humidityTv = findViewById(R.id.humidityTv);
        //uvTv = findViewById(R.id.uvTv);
        co2Tv = findViewById(R.id.co2Tv);
    }

    public void setBotBarIconActive(Drawable drawable, int color){
        currently_selected_iv.setImageDrawable(drawable);
        currently_selected_tv.setTextColor(color);
    }

    public void setBotBarListener(){


        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager supportFragmentManager = getSupportFragmentManager();
                switch (v.getId()){
                    case R.id.bottom_bar_first_item_iv:
                    case R.id.bottom_bar_first_item_tv:
                        activeWebViewF = null;

                        /*ShellExecutor exe = new ShellExecutor();

                        String outp = exe.Executor("am kill cz.zemankrystof.izastavka");
                        Log.e("MRDAT", ""+ outp +" " + outp.length());*/

                        DisplayCityTransportFragment displayCityTransportFragment = new DisplayCityTransportFragment();
                        currentFragment = displayCityTransportFragment;
                        supportFragmentManager
                                .beginTransaction()
                                .replace(R.id.frameLayout, displayCityTransportFragment, "displayCity")
                                .commit();

                        /*activeWebViewF = new WebViewFragment();
                        Bundle args = new Bundle();
                        args.putString("defaultURL", Constants.item1URL);
                        activeWebViewF.setArguments(args);
                        supportFragmentManager
                                .beginTransaction()
                                .replace(R.id.frameLayout, activeWebViewF, "iris")
                                .commit();
                        */
                        /*MapFragment mapFragment = MapFragment.newInstance();
                            supportFragmentManager
                                    .beginTransaction()
                                    .replace(R.id.frameLayout, mapFragment, "map")
                                    .commit();*/
                        //webView.stopLoading();
                        //webView.loadUrl(item1URL);

                        //TODO getdrawable return
                        setCurrentBotBarItem(bottom_bar_first_item_iv, bottom_bar_first_item_tv,R.color.first_item_icons,
                                R.color.first_item_background, R.color.first_item_active, getResources().getDrawable(item1IconActive));
                        /*setTheme(getResources().getColor(R.color.imobiliar_icons), getResources().getColor(R.color.imobiliar_background));
                        currently_selected_iv = bottom_bar_first_item_iv;
                        currently_selected_tv = bottom_bar_first_item_tv;
                        setBotBarIconActive(getResources().getColor(R.color.imobiliar_active));*/
                        break;

                    case R.id.bottom_bar_second_item_iv:
                    case R.id.bottom_bar_second_item_tv:
                        activeWebViewF = null;
                        SearchLinkFragment searchLinkfragment = SearchLinkFragment.newInstance();
                        currentFragment = searchLinkfragment;
                        supportFragmentManager
                                .beginTransaction()
                                .replace(R.id.frameLayout,searchLinkfragment, "searchLink")
                                .commit();

                        setCurrentBotBarItem(bottom_bar_second_item_iv,bottom_bar_second_item_tv,R.color.second_item_icons,
                                R.color.second_item_background,R.color.second_item_active, getResources().getDrawable(item2IconActive));
                        //webView.stopLoading();
                        //webView.loadUrl(item2URL);
                        //setCurrentBotBarItem(bottom_bar_second_item_iv,bottom_bar_second_item_tv,R.color.dpmb_icons,R.color.dpmb_background,R.color.dpmb_active);
                        /*setTheme(getResources().getColor(R.color.dpmb_icons), getResources().getColor(R.color.dpmb_background));
                        currently_selected_iv = bottom_bar_second_item_iv;
                        currently_selected_tv = bottom_bar_second_item_tv;
                        setBotBarIconActive(getResources().getColor(R.color.dpmb_active));*/
                        break;

                    case R.id.bottom_bar_third_item_iv:
                    case R.id.bottom_bar_third_item_tv:

                        activeWebViewF = WebViewFragment.newInstance();
                        currentFragment = activeWebViewF;
                        supportFragmentManager
                                .beginTransaction()
                                .replace(R.id.frameLayout,activeWebViewF, "webBrno")
                                .commit();

                        setCurrentBotBarItem(bottom_bar_third_item_iv,bottom_bar_third_item_tv,R.color.third_item_icons,
                                R.color.third_item_background,R.color.third_item_active, getResources().getDrawable(item3IconActive));
                        //webView.stopLoading();
                        //webView.loadUrl(item3URL);
                        //setCurrentBotBarItem(bottom_bar_third_item_iv,bottom_bar_third_item_tv,R.color.iris_icons,R.color.iris_background,R.color.iris_active);
                        /*setTheme(getResources().getColor(R.color.iris_icons), getResources().getColor(R.color.iris_background));
                        currently_selected_iv = bottom_bar_third_item_iv;
                        currently_selected_tv = bottom_bar_third_item_tv;
                        setBotBarIconActive(getResources().getColor(R.color.iris_active));*/
                        break;

                    case R.id.bottom_bar_fourth_item_iv:
                    case R.id.bottom_bar_fourth_item_tv:
                        activeWebViewF = null;

                        supportFragmentManager = getSupportFragmentManager();
                        DisplayCityMapFragment fragment = new DisplayCityMapFragment();
                        currentFragment = fragment;
                        supportFragmentManager
                                .beginTransaction()
                                .replace(R.id.frameLayout,fragment,"cityMap")
                                .commit();

                        /*NativePDFRenderer displayCityMapFragment = new NativePDFRenderer();
                        supportFragmentManager
                                .beginTransaction()
                                .replace(R.id.frameLayout,displayCityMapFragment,"cityMap")
                                .commit();
*/
                        setCurrentBotBarItem(bottom_bar_fourth_item_iv,bottom_bar_fourth_item_tv,R.color.fourth_item_icons,
                                R.color.fourth_item_background,R.color.fourth_item_active, getResources().getDrawable(item4IconActive));
                        //webView.stopLoading();
                        //webView.loadUrl(item4URL);
                        //setCurrentBotBarItem(bottom_bar_third_item_iv,bottom_bar_third_item_tv,R.color.iris_icons,R.color.iris_background,R.color.iris_active);
                        /*setTheme(getResources().getColor(R.color.iris_icons), getResources().getColor(R.color.iris_background));
                        currently_selected_iv = bottom_bar_third_item_iv;
                        currently_selected_tv = bottom_bar_third_item_tv;
                        setBotBarIconActive(getResources().getColor(R.color.iris_active));*/
                        break;

                    case R.id.bottom_bar_back_iv:
                    case R.id.bottom_bar_back_tv:
                        if(activeWebViewF != null) {
                            activeWebViewF.goBack();
                            Log.d("Back", "Pressed");
                        }
                        break;
                }
            }
        };

        bottom_bar_first_item_iv.setOnClickListener(onClickListener);
        bottom_bar_first_item_tv.setOnClickListener(onClickListener);
        bottom_bar_second_item_iv.setOnClickListener(onClickListener);
        bottom_bar_second_item_tv.setOnClickListener(onClickListener);
        bottom_bar_third_item_iv.setOnClickListener(onClickListener);
        bottom_bar_third_item_tv.setOnClickListener(onClickListener);
        bottom_bar_fourth_item_iv.setOnClickListener(onClickListener);
        bottom_bar_fourth_item_tv.setOnClickListener(onClickListener);
        bottom_bar_back_iv.setOnClickListener(onClickListener);
        bottom_bar_back_tv.setOnClickListener(onClickListener);
    }

    /**
     * Method for setting current active item on the bottom bar. Also calls for theme change based on that item
     * @param iv ImageView to be set as currently active
     * @param tv TextView to be set as currently active
     * @param iconColor Color to be set to all inactive icons
     * @param background Background color to be set
     * @param activeIconColor Color to be set to active icon
     * @param activeDr  Drawable of the active icon
     */
    public void setCurrentBotBarItem(ImageView iv, TextView tv, int iconColor, int background, int activeIconColor, Drawable activeDr){
        setTheme(getResources().getColor(iconColor), getResources().getColor(background));
        currently_selected_iv = iv;
        currently_selected_tv = tv;
        setBotBarIconActive(activeDr, getResources().getColor(activeIconColor));
    }

    /*@Override
    public void serviceSuccess(Channel channel) {

        try {
            Item item = channel.getItem();
            //int resourceId = getResources().getIdentifier("drawable/icon_"+ item.getCondition().getCode(), null, getPackageName());

            Log.d("Acquired condition:", "" + item.getCondition().getCode());
            conditionIconTextView.setTypeface(weatherFace);
            conditionIconTextView.setText(getResources().getIdentifier("icon_" + item.getCondition().getCode(), "string", getPackageName()));

            temperatureTextView.setText(item.getCondition().getTemperature() + "\u00b0" + channel.getUnits().getTemperature().toUpperCase());

            //windTextView.setText(service.getLocation());

            Log.d("Acquired weather:", item.getCondition().getDescription());
            int conditionId = getResources().getIdentifier(fixReceivedWeatherString(item.getCondition().getDescription().toLowerCase()), "string", getPackageName());
            //conditionTextView.setTypeface(weatherFace);
            conditionTextView.setText(conditionId);
            //conditionTextView.setText(item.getCondition().getDescription());
            windTextView.setText(channel.getWind().getSpeed() + " " + channel.getUnits().getSpeed());
        }catch (Exception e){
            Log.d("Weather type:", e.getMessage());
        }
    }

    @Override
    public void serviceFailure(Exception exception) {
        Toast.makeText(this,exception.getMessage(), Toast.LENGTH_SHORT).show();
    }*/


    /**
     * Method to set theme color to the icons and background
     * @param colorIcon Color to be set to inactive icons
     * @param colorBackground Color to be set to the background
     */
    public void setTheme(int colorIcon, int colorBackground){
        bottom_bar_first_item_iv.setImageDrawable(getResources().getDrawable(item1IconInactive));
        bottom_bar_first_item_tv.setTextColor(colorIcon);
        bottom_bar_second_item_iv.setImageDrawable(getResources().getDrawable(item2IconInactive));
        bottom_bar_second_item_tv.setTextColor(colorIcon);
        bottom_bar_third_item_iv.setImageDrawable(getResources().getDrawable(item3IconInactive));
        bottom_bar_third_item_tv.setTextColor(colorIcon);
        bottom_bar_fourth_item_iv.setImageDrawable(getResources().getDrawable(item4IconInactive));
        bottom_bar_fourth_item_tv.setTextColor(colorIcon);
        bottom_bar_back_iv.setColorFilter(colorIcon);
        bottom_bar_back_tv.setTextColor(colorIcon);
        //background_theme.setBackgroundColor(colorBackground);

        //temperatureTextView.setTextColor(colorIcon);
        //temperatureIconTextView.setTextColor(colorIcon);
        //conditionTextView.setTextColor(colorIcon);
        //conditionIconTextView.setTextColor(colorIcon);
        //windTextView.setTextColor(colorIcon);
        //windIconTextView.setTextColor(colorIcon);
//        pollution.setTextColor(colorIcon);
//        time.setTextColor(colorIcon);
    }

    public void loadStops() {
        service.getStops(stopId,postId).enqueue(new Callback<Stop>() {
            @Override
            public void onResponse(Call<Stop> call, Response<Stop> response) {

                if(response.isSuccessful()) {
                    Log.d("StopId","" + response.body().getStopID());

                    if (response.body().getPostList().size() > 0) {
                        List<Departure> departureList = response.body().getPostList().get(0).getDepartures();

                        Log.d("MainActivity", "posts loaded from API");
                        int i = 0;
                        Log.d("DEPARTURESIZE", "" + departureList.size());
                        TableLayout msgLayout = findViewById(R.id.topBarLineMsg);
                        TextView msgView = findViewById(R.id.topBarLineMsgTv);
                        //VerticalMarqueeTextView msgView = findViewById(R.id.topBarLineMsgTv);
                        TableLayout layout = findViewById(R.id.topBarLine);

                        if(response.body().getMessage() == null) {
                            layout.setVisibility(View.VISIBLE);
                            msgLayout.setVisibility(View.GONE);
                            msgView.setVisibility(View.GONE);
                            while ((i < 7) && (i < departureList.size())) {
                                Departure it = departureList.get(i);
                                TableRow row = (TableRow) layout.getChildAt(i + 1);
                                ((TextView) row.getChildAt(0)).setText(it.getLine());
                                ((TextView) row.getChildAt(1)).setText(it.getFinalStop());
                                ((TextView) row.getChildAt(2)).setText(it.getIsLowFloor() ? "x" : "");
                                ((TextView) row.getChildAt(3)).setText(it.getTimeMark());
                                i++;
                            }
                        }else {
                            layout.setVisibility(View.GONE);
                            msgLayout.setVisibility(View.VISIBLE);
                            msgView.setVisibility(View.VISIBLE);
                            Log.e("MSG", response.body().getMessage());
                            msgView.setText(response.body().getMessage());
                            while ((i < 5) && (i < departureList.size())) {
                                Departure it = departureList.get(i);
                                TableRow row = (TableRow) msgLayout.getChildAt(i + 1);
                                ((TextView) row.getChildAt(0)).setText(it.getLine());
                                ((TextView) row.getChildAt(1)).setText(it.getFinalStop());
                                ((TextView) row.getChildAt(2)).setText(it.getIsLowFloor() ? "x" : "");
                                ((TextView) row.getChildAt(3)).setText(it.getTimeMark());
                                i++;
                            }
                        }

                        /*LinearLayout list = findViewById(R.id.topBarLine);
                        while ((i < 6) && (i < departureList.size())) {
                            Departure it = departureList.get(i);
                            LinearLayout temp = (LinearLayout) list.getChildAt(i);
                            ((TextView) temp.getChildAt(0)).setText(it.getLine());
                            ((TextView) temp.getChildAt(1)).setText(it.getFinalStop());
                            ((TextView) temp.getChildAt(2)).setText(it.getIsLowFloor() ? "x" : "");
                            ((TextView) temp.getChildAt(3)).setText(it.getTimeMark());
                            i++;
                        }*/

                    }
                    /*while (i < 6 && i < departureList.size()){
                    //for (int i = 0; (i < 6 || i < departureList.size()); i++) {
                        View line = LayoutInflater.from(MainActivity.this).inflate(R.layout.line, null);
                        Departure it = departureList.get(i);
                        TextView lineNumber = line.findViewById(R.id.lineNumberTv);
                        TextView finalStop = line.findViewById(R.id.finalStopTv);
                        TextView isLowFloor = line.findViewById(R.id.isLowFloorTv);
                        TextView departure = line.findViewById(R.id.departureTv);
                        if (i % 2 == 0) {
                            line.setBackgroundColor(getResources().getColor(R.color.black25));
                        }
                        lineNumber.setText(it.getLine());
                        finalStop.setText(it.getFinalStop());
                        isLowFloor.setText(it.getIsLowFloor() ? "x" : "");
                        departure.setText(it.getTimeMark());
                        topBar.addView(line);
                        i++;
                    }*/
                    try{
                        Log.e("StopBody", "Closing");
                        response.body().toString();
                    }catch (Exception e){
                        Log.e("Exception", e.getMessage());
                    }
                } else {
                    int statusCode = response.code();
                    Log.e("StopService", "" + response.errorBody());
                    try{
                        response.raw().body().close();
                        response.errorBody().close();
                    }catch (Exception e){
                        Log.e("Exception", e.getMessage());
                    }
                    // handle request errors depending on status code
                }

            }

            @Override
            public void onFailure(Call<Stop> call, Throwable t) {
                //showErrorMessage();
                Log.d("LoadStops", "Error: " + t.getMessage());

            }
        });
    }

    public String fixReceivedWeatherString (String weather){
        weather = weather.replaceAll("\\(|\\)","");
        weather = weather.replaceAll(" ", "_");
        Log.d("Weather", "Fixed to: " + weather);
        return weather;
    }

    public void startInactivityHandlers(){
        if(inactivityHandler == null || inactivityRunnable == null) {
            inactivityHandler = new Handler();
            inactivityRunnable = new Runnable() {
                @Override
                public void run() {
                    Log.d("RUNNABLE", "InactivityHandler");

                    //task to do if user is inactive
                    //Toast.makeText(MainActivity.this,"Prepni neco!",Toast.LENGTH_SHORT).show();
                    //setTheme(getResources().getColor(R.color.first_item_icons), getResources().getColor(R.color.first_item_background));
                    //webView.loadUrl(item1URL);
                    //webView.clearHistory();
                    //Log.e("FragTag", "Is now:" + currentFragment.getTag());
                    if(!currentFragment.getTag().equalsIgnoreCase("displayCity")) {
                        activeWebViewF = null;
                        FragmentManager supportFragmentManager = getSupportFragmentManager();
                        DisplayCityTransportFragment displayCityTransportFragment = new DisplayCityTransportFragment();
                        currentFragment = displayCityTransportFragment;
                        supportFragmentManager
                                .beginTransaction()
                                .replace(R.id.frameLayout, displayCityTransportFragment, "displayCity")
                                .commit();

                        setCurrentBotBarItem(bottom_bar_first_item_iv, bottom_bar_first_item_tv, R.color.first_item_icons,
                                R.color.first_item_background, R.color.first_item_active, getResources().getDrawable(item1IconActive));

                /*DisplayCityTransportFragment displayCityTransportFragment = new DisplayCityTransportFragment();
                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frameLayout, displayCityTransportFragment, "displayCity")
                        .commit();

                currently_selected_iv = bottom_bar_first_item_iv;
                currently_selected_tv = bottom_bar_first_item_tv;
                setBotBarIconActive(getDrawable(item1IconActive),getResources().getColor(R.color.first_item_active));*/

                    }else{
                        ((DisplayCityTransportFragment)currentFragment).loadCurrentMap();
                    }
                    inactivityHandler.postDelayed(inactivityRunnable, 300000);
                }
            };
        }
        inactivityHandler.postDelayed(inactivityRunnable, 5000);

    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        if(inactivityHandler !=null) {
            inactivityHandler.removeCallbacks(inactivityRunnable);
            inactivityHandler.postDelayed(inactivityRunnable, 300000);
        }
    }
/*
    public void startWeatherRefreshHandler(){
        weatherHandler = new Handler();
        weatherRunnable = new Runnable() {
            @Override
            public void run() {
                service.refreshWeather("brno");
                weatherHandler.postDelayed(weatherRunnable,600000);
                Log.d("Menim","pocasi");
            }
        };

        weatherHandler.post(weatherRunnable);
    }
*/

    private void startMqtt() {
        mqttHelper = new MqttHelper(getApplicationContext());
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {

            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.d("MRDKA", "Teplota: " + mqttMessage.toString() + " " + topic + " " + topic.toLowerCase().contains("teplota"));
                if(topic.toLowerCase().contains("teplota")){
                    Log.d("Tepl", "teplota: inside");
                    double teplota = Double.parseDouble(mqttMessage.toString());
                    Log.d("Tepl", "teplota: " + teplota);
                    if (teplota > -30 && teplota < 40) {
                        Log.d("TEPLOTA", "je tedka: " + teplota);
                        tempTv.setText(teplota + getString(R.string.degrees));
                    }
                }else if(topic.contains("tlak")) {

                }else if(topic.contains("vlhkost")) {
                    double vlhkost = Double.parseDouble(mqttMessage.toString());
                    if (vlhkost >= 0 && vlhkost < 80) {
                        humidityTv.setText(vlhkost + "%");
                    }
//                }else if(topic.contains("uv")) {
//                    uvTv.setText(mqttMessage.toString());
                }else if(topic.contains("smog")) {
                    co2Tv.setText(mqttMessage.toString() + " ppm");
                }

                Log.w("Debug", topic + "" + mqttMessage.toString());

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }

    public void startLoadStopsRefreshHandler(){
        if(loadStopsHandler == null || loadStopsRunnable == null) {
            loadStopsHandler = new Handler();
            loadStopsRunnable = new Runnable() {
                @Override
                public void run() {
                    Log.d("MainActivity", "Refreshing stop departures!");
                    loadStops();
                    loadStopsHandler.postDelayed(loadStopsRunnable, 30000);
                }
            };
        }
        loadStopsHandler.post(loadStopsRunnable);
    }

    public void startTimetableRefreshHandlers(){
        Log.d("TimetableRefresh","IsHere");

        /*Intent dayNightInt = new Intent(this, DayNightBR.class);
        PendingIntent dayNightPInt = PendingIntent.getBroadcast(this,12345678, dayNightInt, 0);
        AlarmManager dayNightAM = (AlarmManager) getSystemService(ALARM_SERVICE);
        dayNightAM.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 86400000,dayNightPInt);

        Intent timetablesInt = new Intent(this, TimetablesBR.class);
        PendingIntent timetablesPInt = PendingIntent.getBroadcast(this,12345678, timetablesInt, 0);
        AlarmManager timetablesAM = (AlarmManager) getSystemService(ALARM_SERVICE);
        timetablesAM.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 2592000000L,timetablesPInt);*/


        /*OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(TimetablesWorker.class).addTag("timetable").build();
        WorkManager.getInstance().enqueue(request);*/
        PeriodicWorkRequest dayNightPeriodic = new PeriodicWorkRequest.Builder(DayNightWorkHelper.class, 30, TimeUnit.DAYS)
                .setConstraints(Constraints.NONE).addTag("dayNight").build();
        WorkManager.getInstance().cancelAllWorkByTag("dayNight");
        WorkManager.getInstance().enqueue(dayNightPeriodic);

        PeriodicWorkRequest timetablePeriodic = new PeriodicWorkRequest.Builder(TimetablesWorker.class, 1, TimeUnit.DAYS)
                .setConstraints(Constraints.NONE).addTag("timetable").build();
        WorkManager.getInstance().cancelAllWorkByTag("timetable");
        WorkManager.getInstance().enqueue(timetablePeriodic);

    }


    /*public void downloadDayNightTimetables(){
        service.getSchemas().enqueue(new Callback<LinkSchemas>() {
            @Override
            public void onResponse(Call<LinkSchemas> call, Response<LinkSchemas> response) {
                if (response.isSuccessful()){
                    DayLines dayLine = response.body().getDayLines();
                    new DownloadHelper().execute(dayLine);
                    NightLines nightLine = response.body().getNightLines();
                    new DownloadHelper().execute(nightLine);
                }
            }

            @Override
            public void onFailure(Call<LinkSchemas> call, Throwable t) {

            }
        });
    }

    public void downloadTimetables(){

        service.getTimetables(stopId,postId).enqueue(new Callback<StopTimetables>() {
            @Override
            public void onResponse(Call<StopTimetables> call, Response<StopTimetables> response) {
                if (response.isSuccessful()) {
                    Log.d("Timetable", response.body().toString());
                    final StopTimetables timetables = response.body();
                    Realm realm = Realm.getDefaultInstance();
                    double lastModified = 0;
                    if(realm.where(StopTimetables.class).findFirst() != null) {
                        lastModified = realm.where(StopTimetables.class).findFirst().getLastModified();
                    }
                    Log.d("TimetablesLastModified" , ""+ response.body().getLastModified() + " mynum: " + lastModified);
                    if(response.body().getLastModified() != lastModified) {
                        Log.d("TimetablesRealm", "inputting data to realm!");
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                realm.copyToRealmOrUpdate(timetables);
                            }
                        });
                        new DownloadHelper().execute(timetables);
                    }
                }
            }

            @Override
            public void onFailure(Call<StopTimetables> call, Throwable t) {
                Log.d("Timetables", "Failed" + t.getMessage());
            }
        });
    }*/
}
