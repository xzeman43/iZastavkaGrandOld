package cz.zemankrystof.izastavka;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextClock;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import cz.zemankrystof.izastavka.data.model.StopList;

public class SearchLinkFragment extends Fragment implements View.OnClickListener{

    private AutoCompleteTextView from;
    private AutoCompleteTextView to;
    private Button searchButton;
    private Button departureButton;
    private Button arrivalButton;
    private TextView datePickerTv;
    private TextClock timePickerTv;
    private boolean arrivalSelected = false;

    public static SearchLinkFragment newInstance() {
        SearchLinkFragment myFragment = new SearchLinkFragment();
        /*Bundle args = new Bundle();
        args.putString("defaultURL", Constants.item1URL);
        myFragment.setArguments(args);*/
        return myFragment;
    }

    public SearchLinkFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_link, null);

        String[] autocompletes = getAutocompletes();
        ArrayAdapter<String> adapter= new ArrayAdapter<String>(getActivity(),android.R.layout.select_dialog_item, autocompletes);

        from = rootView.findViewById(R.id.fromActv);
        from.setThreshold(1);
        from.setAdapter(adapter);
        to = rootView.findViewById(R.id.toActv);
        to.setThreshold(1);
        to.setAdapter(adapter);

        departureButton = rootView.findViewById(R.id.departureChangingBtn);
        departureButton.setOnClickListener(this);
        arrivalButton = rootView.findViewById(R.id.arrivalChangingBtn);
        arrivalButton.setOnClickListener(this);
        datePickerTv = rootView.findViewById(R.id.datePickerTv);
        timePickerTv = rootView.findViewById(R.id.timePickerTv);







        searchButton = rootView.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(this);

        /*searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebViewFragment fragment = new WebViewFragment();
                ((MainActivity)getActivity()).setActiveWebViewF(fragment);
                Bundle args = new Bundle();
                args.putString("defaultURL", "https://t.jizdnirady.idnes.cz/idsjmk/spojeni/?f="+ from.getText() +"&t="+ to.getText() +"&cmdSearch=true");
                fragment.setArguments(args);
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameLayout,fragment, "searchResult")
                        .commit();
            }
        });*/
        return rootView;
    }

    public String[] getAutocompletes(){
        Gson gson = new Gson();

        try {
            StopList[] list = gson.fromJson(new BufferedReader(new InputStreamReader(getActivity().getAssets().open("stopsList.json"))), StopList[].class);
            String[] autocompleteNames = new String[list.length];
            for (int i = 0; i < list.length; i++) {
               autocompleteNames[i] = list[i].getName();
            }
            return autocompleteNames;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.searchButton:
                WebViewFragment fragment = new WebViewFragment();
                ((MainActivity)getActivity()).setActiveWebViewF(fragment);
                ((MainActivity)getActivity()).setCurrentFragment(fragment);
                Bundle args = new Bundle();
                args.putString("defaultURL", "https://t.jizdnirady.idnes.cz/idsjmk/spojeni/?f="+ from.getText() +"&t="+ to.getText() + "&date=" + datePickerTv.getText() + "&time=" + timePickerTv.getText() + "&byarr=" + arrivalSelected + "&cmdSearch=true");
                fragment.setArguments(args);
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameLayout,fragment, "searchResult")
                        .commit();
                break;

            case R.id.arrivalChangingBtn:
                departureButton.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.button_departure_inactive));
                arrivalButton.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.button_departure_active));
                arrivalSelected = true;
                break;

            case R.id.departureChangingBtn:
                arrivalButton.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.button_departure_inactive));
                departureButton.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.button_departure_active));
                arrivalSelected = false;
                break;
        }
    }
}
