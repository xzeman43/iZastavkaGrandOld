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
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class NativePDFRenderer extends Fragment {

    /**
     * Key string for saving the state of current page index.
     */
    private static final String STATE_CURRENT_PAGE_INDEX = "current_page_index";

    /**
     * The filename of the PDF.
     */
    private static final String FILENAME = "mapa_old.pdf";

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
    private TouchImageView mImageView;

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


    public NativePDFRenderer() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_disp_city_map, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Retain view references.
        mImageView = (TouchImageView) view.findViewById(R.id.disp_city_map);

        mPageIndex = 0;
        // If there is a savedInstanceState (screen orientations, etc.), we restore the page index.
        if (null != savedInstanceState) {
            mPageIndex = savedInstanceState.getInt(STATE_CURRENT_PAGE_INDEX, 0);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            openRenderer(getActivity());
            showPage(mPageIndex);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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
    private void openRenderer(Context context) throws IOException {
        // In this sample, we read a PDF from the assets directory.
        File file = new File(context.getCacheDir(), FILENAME);
        Log.d("FILE PATH", "" + file.getAbsolutePath());
        if (!file.exists()) {
            // Since PdfRenderer cannot handle the compressed asset file directly, we copy it into
            // the cache directory.
            InputStream asset = context.getAssets().open(FILENAME);
            //InputStream asset = context.getAssets().open(Environment.getExternalStorageDirectory().toString() + "/test/test.pdf");
            FileOutputStream output = new FileOutputStream(file);
            final byte[] buffer = new byte[1024];
            int size;
            while ((size = asset.read(buffer)) != -1) {
                output.write(buffer, 0, size);
            }
            asset.close();
            output.close();
        }
        mFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
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
        Bitmap bitmap = Bitmap.createBitmap(1248, 1920,
                Bitmap.Config.ARGB_8888);
        // Here, we render the page onto the Bitmap.
        // To render a portion of the page, use the second and third parameter. Pass nulls to get
        // the default result.
        // Pass either RENDER_MODE_FOR_DISPLAY or RENDER_MODE_FOR_PRINT for the last parameter.
        mCurrentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        // We are ready to show the Bitmap to user.
        //mImageView.setBackgroundColor(getResources().getColor(R.color.white));
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
     * Converts dp into the pixels
     * @param dp display points to be converted
     * @return pixels converted from dp
     */
    private int convertFromDptoPx(int dp){
        final float scale = getContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (dp * scale + 0.5f);
        return  pixels;
    }


}
