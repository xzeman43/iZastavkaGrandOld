package cz.zemankrystof.izastavka;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.util.FitPolicy;


public class DisplayCityMapFragment extends Fragment {

    private View rootView;
    PDFView view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_disp_city_map, null);
        view = rootView.findViewById(R.id.disp_city_map);
        view.fromAsset("mapa.pdf")
                .pageFitPolicy(FitPolicy.HEIGHT)
                .load();

        return rootView;
    }
}
