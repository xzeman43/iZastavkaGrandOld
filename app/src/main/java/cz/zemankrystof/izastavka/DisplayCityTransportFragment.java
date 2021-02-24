package cz.zemankrystof.izastavka;

import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.zemankrystof.izastavka.data.Constants;
import cz.zemankrystof.izastavka.data.model.LinkSchema.DayLines;
import cz.zemankrystof.izastavka.data.model.LinkSchema.NightLines;
import cz.zemankrystof.izastavka.data.model.StopTimetables.StopTimetables;
import cz.zemankrystof.izastavka.data.model.StopTimetables.Timetable;
import cz.zemankrystof.izastavka.helpers.LineColorHelper;
import io.realm.Realm;

public class DisplayCityTransportFragment extends Fragment implements View.OnClickListener {
    private View rootView;
    private LinearLayout linePicLL;
    private LinearLayout lineNumberLL;
    private Button dayButton;
    private Button nightButton;
    private PDFView pdfView;

    // 0 = dayMap; 1 = nightMap; 2 = other
    private int picShown = 2;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_city_transport, null);
        pdfView = rootView.findViewById(R.id.pdfView);
        dayButton = (Button) rootView.findViewById(R.id.dayBtn);
        dayButton.setOnClickListener(this);
        nightButton = (Button) rootView.findViewById(R.id.nightBtn);
        nightButton.setOnClickListener(this);
        linePicLL = rootView.findViewById(R.id.linePicLL);
        lineNumberLL = rootView.findViewById(R.id.lineLL);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadCurrentLines();
        loadCurrentLinesPic();
        loadCurrentMap();

    }

    @Override
    public void onClick(View view) {
        Realm realm = Realm.getDefaultInstance();
        switch (view.getId()) {
            case R.id.dayBtn: {
                picShown = 0;
                setDayNightSwitchColors(0);
                String path = "";
                if(realm.where(DayLines.class).findFirst() != null) {
                    path = realm.copyFromRealm(realm.where(DayLines.class).findFirst()).getImageLocation();
                    realm.close();
                }else {
                    break;
                }
                try {
                    File file = new File(Constants.fileLocation + "dayNight/" + path);
                    pdfView.fromFile(file).onLoad(new OnLoadCompleteListener() {
                        @Override
                        public void loadComplete(int nbPages) {
                            pdfView.zoomWithAnimation(550,600,3);
//                            PointF pointF = new PointF(1,1000);
//                            pdfView.zoomCenteredTo( 3, pointF);
                        }
                    }).load();
                    pdfView.setMinZoom(1);

                }catch (Exception e){
                    Log.d("DayButton", e.getMessage());
                    Toast.makeText(getActivity(), "Error" + e, Toast.LENGTH_SHORT).show();
                }
                view.setPressed(true);
                break;
            }
            case R.id.nightBtn: {
                picShown = 1;
                setDayNightSwitchColors(1);
                String path = "";
                if(realm.where(NightLines.class).findFirst() != null) {
                    path = realm.copyFromRealm(realm.where(NightLines.class).findFirst()).getImageLocation();
                    realm.close();
                }else{
                    break;
                }
                try {
                    File file = new File(Constants.fileLocation + "dayNight/" + path);
                    pdfView.fromFile(file).onLoad(new OnLoadCompleteListener() {
                        @Override
                        public void loadComplete(int nbPages) {
                            pdfView.zoomWithAnimation(550,550,3);
                        }
                    }).load();
                }catch (Exception e){
                    Toast.makeText(getActivity(), "Error" + e, Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }


    /**
     * Handles the color switch logic for day/night buttons
     * @param button Identifies the pressed button; 0 - day, 1 - night, 2 - other
     */
    private void setDayNightSwitchColors(int button){
        if (button == 0) {
            dayButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_day_pressed));
            Drawable[] drawables = dayButton.getCompoundDrawables();
            drawables[2].setTint(getResources().getColor(R.color.white));
            nightButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_night_enabled));
            drawables = nightButton.getCompoundDrawables();
            drawables[2].setTint(getResources().getColor(R.color.mapNight));
        } else if (button == 1){
            dayButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_day_enabled));
            Drawable[] drawables = dayButton.getCompoundDrawables();
            drawables[2].setTint(getResources().getColor(R.color.mapDay));
            nightButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_night_pressed));
            drawables = nightButton.getCompoundDrawables();
            drawables[2].setTint(getResources().getColor(R.color.white));
        } else {
            dayButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_day_enabled));
            Drawable[] drawables = dayButton.getCompoundDrawables();
            drawables[2].setTint(getResources().getColor(R.color.mapDay));
            nightButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_night_enabled));
            drawables = nightButton.getCompoundDrawables();
            drawables[2].setTint(getResources().getColor(R.color.mapNight));
        }
    }

    /**
     * Method for loading the line icons into the headline bar
     */
    private void loadCurrentLines(){
        Log.d("CurrentLines", "Adding");
        Realm realm = Realm.getDefaultInstance();
        if(realm.where(StopTimetables.class).findFirst() != null)
        {
            List<Timetable> timetables = realm.copyFromRealm(realm.where(StopTimetables.class).findFirst().getTimetables());
            LineColorHelper helper = new LineColorHelper();
            realm.close();
            for (int i = 0; i < timetables.size(); i++) {
                Timetable it = timetables.get(i);
                final String path = it.getTimetableImageLocation();
                if(path == null){
                    continue;
                }
                Button button = new Button(getActivity());
                String lineNumber = extractLineNumber(it.getTimetableFileName());
                button.setText(lineNumber);
                int[] colors = helper.getRightColorForLine(getActivity(), lineNumber);
                if (colors[1] == 0) {
                    button.setBackgroundColor(colors[0]);
                } else {
                    button.setBackgroundColor(colors[0]);
                    button.setTextColor(colors[1]);
                }
                int size = convertFromDptoPx(59);
                int margin = convertFromDptoPx(5);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
                params.setMargins(0, margin, margin, margin);
                button.setLayoutParams(params);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            File file = new File(Constants.fileLocation + "timetables/" + path);
                            pdfView.fromFile(file).load();
                            setDayNightSwitchColors(2);
                            picShown = 2;
                        } catch (Exception e) {
                            Toast.makeText(getActivity(), "Error" + e, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                lineNumberLL.addView(button);

            }
        }
    }

    public void loadCurrentLinesPic(){
        Realm realm = Realm.getDefaultInstance();
        if (realm.where(StopTimetables.class).findFirst() != null) {
            List<Timetable> timetables = realm.copyFromRealm(realm.where(StopTimetables.class).findFirst().getTimetables());
            realm.close();
            LineColorHelper helper = new LineColorHelper();
            boolean tramPresent = false;
            boolean troleyPresent = false;
            boolean mhdPresent = false;
            boolean nightMhdPresent = false;
            boolean trainPresent = false;
            boolean boatPresent = false;
            Log.d("LinesPic", "Size: " + timetables.size());
            for (int i = 0; i < timetables.size(); i++) {
                Timetable it = timetables.get(i);
                final String path = it.getTimetableImageLocation();
                if(path == null){
                    continue;
                }
                ImageView view = new ImageView(getActivity());
                String lineNumber = extractLineNumber(it.getTimetableFileName());
                String lineType = helper.getLineType(lineNumber);
                Log.d("TIMETABLES", "Line type " + lineType);
                if (lineType.contentEquals("tram")) {
                    if (tramPresent) {
                        continue;
                    }
                    tramPresent = true;
                } else if (lineType.contentEquals("troley")) {
                    if (troleyPresent) {
                        continue;
                    }
                    troleyPresent = true;
                } else if (lineType.contentEquals("nightmhd")) {
                    if (nightMhdPresent) {
                        continue;
                    }
                    nightMhdPresent = true;
                } else if (lineType.contentEquals("mhd")) {
                    if (mhdPresent) {
                        continue;
                    }
                    mhdPresent = true;
                }
                view.setImageDrawable(helper.getLineDrawable(getActivity(), lineType));
                int[] colors = helper.getRightColorForLine(getActivity(), lineNumber);
                view.setBackgroundColor(colors[0]);
                int size = convertFromDptoPx(59);
                int margin = convertFromDptoPx(5);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
                params.setMargins(0, margin, margin, margin);
                view.setLayoutParams(params);
                linePicLL.addView(view);

            }
        }
    }

    /**
     * Method for extracting the line number from the given string
     * @param toExtractFrom
     * @return
     */
    private String extractLineNumber(String toExtractFrom){
        Log.d("EXTRACTING", "From " + toExtractFrom);
        String extractedNumber;
        /*extractedNumber = toExtractFrom.split("_")[0];
        Log.d("EXTRACTING", "Num " + extractedNumber);*/
        Pattern p = Pattern.compile("^[^\\d]*(\\d+)");
        Matcher m = p.matcher(toExtractFrom);
        if (m.find()) {
            return m.group(1);
        }
        return "prc";

    }

    /**
     * Converts dp into the pixels
     * @param dp display points to be converted
     * @return pixels converted from dp
     */
    private int convertFromDptoPx(int dp){
        final float scale = getContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (dp * scale + 0.5f);
        return  pixels;
    }

    public void loadCurrentMap(){
        int time = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        Realm realm = Realm.getDefaultInstance();
        String path = "";
        if(time > 5 && time < 22){
            if(picShown != 0) {
                picShown = 0;
                setDayNightSwitchColors(0);
                if (realm.where(DayLines.class).findFirst() != null) {
                    path = realm.copyFromRealm(realm.where(DayLines.class).findFirst()).getImageLocation();
                    realm.close();
                } else {
                    return;
                }
                try {
                    File file = new File(Constants.fileLocation + "dayNight/" + path);
                    pdfView.fromFile(file).onLoad(new OnLoadCompleteListener() {
                        @Override
                        public void loadComplete(int nbPages) {
                            //pdfView.setMidZoom(3);
                            pdfView.zoomWithAnimation(550, 600, 3);
//                            PointF pointF = new PointF(1,1000);
//                            pdfView.zoomCenteredTo( 3, pointF);
                        }
                    }).load();
                    pdfView.setMinZoom(1);

                } catch (Exception e) {
                    Log.d("DayButton", e.getMessage());
                    Toast.makeText(getActivity(), "Error" + e, Toast.LENGTH_SHORT).show();
                }
            }
        }else {
            if (picShown != 1) {
                picShown = 1;
                setDayNightSwitchColors(1);
                if (realm.where(NightLines.class).findFirst() != null) {
                    path = realm.copyFromRealm(realm.where(NightLines.class).findFirst()).getImageLocation();
                    realm.close();
                } else {
                    return;
                }
                try {
                    File file = new File(Constants.fileLocation + "dayNight/" + path);
                    pdfView.fromFile(file).onLoad(new OnLoadCompleteListener() {
                        @Override
                        public void loadComplete(int nbPages) {
                            pdfView.zoomWithAnimation(550, 550, 3);
                        }
                    }).load();
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Error" + e, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
