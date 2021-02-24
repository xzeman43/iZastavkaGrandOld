package cz.zemankrystof.izastavka;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
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

import java.io.File;
import java.io.IOException;
import java.util.List;

import cz.zemankrystof.izastavka.data.Constants;
import cz.zemankrystof.izastavka.data.model.LinkSchema.DayLines;
import cz.zemankrystof.izastavka.data.model.LinkSchema.NightLines;
import cz.zemankrystof.izastavka.data.model.StopTimetables.StopTimetables;
import cz.zemankrystof.izastavka.data.model.StopTimetables.Timetable;
import cz.zemankrystof.izastavka.helpers.LineColorHelper;
import io.realm.Realm;

public class DisplayCityTransportFragmentOld extends Fragment implements View.OnClickListener {

    /**
     * Key string for saving the state of current page index.
     */
    private static final String STATE_CURRENT_PAGE_INDEX = "current_page_index";

    /**
     * The filename of the PDF.
     */
    //private static final String FILENAME = "sample.pdf";¨¨

    /**
     * File descriptor of the PDF.
     */
    private ParcelFileDescriptor mFileDescriptor;

    /**
     * {@link android.graphics.pdf.PdfRenderer} to render the PDF.
     */
    private PdfRenderer mPdfRenderer;

    /**
     * Page that is currently shown on the screen.
     */
    private PdfRenderer.Page mCurrentPage;

    /**
     * {@link android.widget.ImageView} that shows a PDF page as a {@link android.graphics.Bitmap}
     */
    private ImageView mImageView;

    /**
     * {@link android.widget.Button} to move to the previous page.
     */
    private Button dayButton;

    /**
     * {@link android.widget.Button} to move to the next page.
     */
    private Button nightButton;

    /**
     * PDF page index
     */
    private int mPageIndex;

    private LinearLayout linePicLL;
    private LinearLayout lineNumberLL;

    public DisplayCityTransportFragmentOld() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_city_transport, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Retain view references.
        //mImageView = (ImageView) view.findViewById(R.id.imageV);
        dayButton = (Button) view.findViewById(R.id.dayBtn);
        nightButton = (Button) view.findViewById(R.id.nightBtn);
        linePicLL = view.findViewById(R.id.linePicLL);
        lineNumberLL = view.findViewById(R.id.lineLL);


        // Bind events.
        dayButton.setOnClickListener(this);
        nightButton.setOnClickListener(this);

        mPageIndex = 0;
        // If there is a savedInstanceState (screen orientations, etc.), we restore the page index.
        if (null != savedInstanceState) {
            mPageIndex = savedInstanceState.getInt(STATE_CURRENT_PAGE_INDEX, 0);
        }

        loadCurrentLines();
        loadCurrentLinesPic();
    }

    @Override
    public void onStart() {
        super.onStart();
        /*try {
            openRenderer(getActivity(), "day");
            showPage(mPageIndex);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }*/
    }

    @Override
    public void onStop() {
        try {
            closeRenderer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (null != mCurrentPage) {
            outState.putInt(STATE_CURRENT_PAGE_INDEX, mCurrentPage.getIndex());
        }
    }

    /**
     * Sets up a {@link android.graphics.pdf.PdfRenderer} and related resources.
     */
    private void openRenderer(Context context, String fileInfo) throws IOException {
        // In this sample, we read a PDF from the assets directory.
        //File file = new File(context.getCacheDir(), FILENAME);
        File file = new File(Constants.fileLocation + fileInfo);
        mFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
        Log.d("FILE PATH", "" + file.getAbsolutePath());
        /*if (!file.exists()) {
            // Since PdfRenderer cannot handle the compressed asset file directly, we copy it into
            // the cache directory.
            //InputStream asset = context.getAssets().open(FILENAME);
            InputStream asset = context.getAssets().open(Environment.getExternalStorageDirectory().toString() + "/test/test.pdf");
            FileOutputStream output = new FileOutputStream(file);
            final byte[] buffer = new byte[1024];
            int size;
            while ((size = asset.read(buffer)) != -1) {
                output.write(buffer, 0, size);
            }
            asset.close();
            output.close();
        }*/
        // This is the PdfRenderer we use to render the PDF.
        if (mFileDescriptor != null) {
            mPdfRenderer = new PdfRenderer(mFileDescriptor);
        }
    }

    /**
     * Closes the {@link android.graphics.pdf.PdfRenderer} and related resources.
     *
     * @throws java.io.IOException When the PDF file cannot be closed.
     */
    private void closeRenderer() throws IOException {
        if (null != mCurrentPage) {
            mCurrentPage.close();
        }
        if(mPdfRenderer != null) {
            mPdfRenderer.close();
        }
        if(mFileDescriptor != null) {
            mFileDescriptor.close();
        }
    }

    /**
     * Shows the specified page of PDF to the screen.
     *
     * @param index The page index.
     */
    private void showPage(int index) {
        if (mPdfRenderer.getPageCount() <= index) {
            return;
        }
        // Make sure to close the current page before opening another one.
        if (null != mCurrentPage) {
            mCurrentPage.close();
        }
        // Use `openPage` to open a specific page in PDF.
        mCurrentPage = mPdfRenderer.openPage(index);
        // Important: the destination bitmap must be ARGB (not RGB).
        /*Bitmap bitmap = Bitmap.createBitmap(mCurrentPage.getWidth(), mCurrentPage.getHeight(),
                Bitmap.Config.ARGB_8888);*/
        Bitmap bitmap = Bitmap.createBitmap(1920, 1440,
                Bitmap.Config.ARGB_8888);
        // Here, we render the page onto the Bitmap.
        // To render a portion of the page, use the second and third parameter. Pass nulls to get
        // the default result.
        // Pass either RENDER_MODE_FOR_DISPLAY or RENDER_MODE_FOR_PRINT for the last parameter.
        mCurrentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        // We are ready to show the Bitmap to user.
        mImageView.setImageBitmap(bitmap);
        //updateUi();
    }

    /**
     * Updates the state of 2 control buttons in response to the current page index.
     */
    private void updateUi() {
        int index = mCurrentPage.getIndex();
        int pageCount = mPdfRenderer.getPageCount();
        dayButton.setEnabled(0 != index);
        nightButton.setEnabled(index + 1 < pageCount);
        //getActivity().setTitle(getString(R.string.app_name_with_index, index + 1, pageCount));
    }

    /**
     * Gets the number of pages in the PDF. This method is marked as public for testing.
     *
     * @return The number of pages.
     */
    public int getPageCount() {
        return mPdfRenderer.getPageCount();
    }

    @Override
    public void onClick(View view) {
        Realm realm = Realm.getDefaultInstance();
        switch (view.getId()) {
            case R.id.dayBtn: {
                // Move to the previous page
                //showPage(mCurrentPage.getIndex() - 1);
                setDayNightSwitchColors(0);
                mImageView.setBackgroundColor(0x00000000);
                String path = "";
                if(realm.where(DayLines.class).findFirst() != null) {
                    path = realm.copyFromRealm(realm.where(DayLines.class).findFirst()).getImageLocation();
                    realm.close();
                }else {
                    break;
                }


                try {
                    openRenderer(getActivity(), "dayNight/" + path);
                    showPage(mPageIndex);
                }catch (IOException e){
                    Toast.makeText(getActivity(), "Error" + e, Toast.LENGTH_SHORT).show();
                }
                view.setPressed(true);
                break;
            }
            case R.id.nightBtn: {
                // Move to the next page
                //showPage(mCurrentPage.getIndex() + 1);
                setDayNightSwitchColors(1);
                mImageView.setBackgroundColor(0x00000000);
                String path = "";
                if(realm.where(NightLines.class).findFirst() != null) {
                    path = realm.copyFromRealm(realm.where(NightLines.class).findFirst()).getImageLocation();
                    realm.close();
                }else{
                    break;
                }
                try {
                    //closeRenderer();
                    openRenderer(getActivity(), "dayNight/" + path);
                    showPage(mPageIndex);
                }catch (IOException e){
                    Toast.makeText(getActivity(), "Error" + e, Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    /**
     * Handles the color switch logic for day/night buttons
     * @param button Identifies the pressed button; 0 - day, 1 - night
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
                            //closeRenderer();
                            openRenderer(getActivity(), "timetables/" + path);
                            showPage(mPageIndex);
                            mImageView.setBackgroundColor(getResources().getColor(R.color.white));
                            setDayNightSwitchColors(2);
                        } catch (IOException e) {
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

            for (int i = 0; i < timetables.size(); i++) {
                Timetable it = timetables.get(i);
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
        extractedNumber = toExtractFrom.split("_")[0];
        Log.d("EXTRACTING", "Num " + extractedNumber);
        return extractedNumber;

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

    public void addLineIcon(ImageView view, String lineType, String lineNumber, LineColorHelper helper){
        view.setImageDrawable(helper.getLineDrawable(getActivity(),lineType));
        int[] colors = helper.getRightColorForLine(getActivity(),lineNumber);
        view.setBackgroundColor(colors[0]);
        int size = convertFromDptoPx(59);
        int margin = convertFromDptoPx(5);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size,size );
        params.setMargins(0, margin, margin, margin);
        view.setLayoutParams(params);
        linePicLL.addView(view);
    }

}
