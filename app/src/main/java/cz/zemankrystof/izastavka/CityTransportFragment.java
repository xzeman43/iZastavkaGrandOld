package cz.zemankrystof.izastavka;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cz.zemankrystof.izastavka.data.model.LinkSchema.LinkSchemas;
import cz.zemankrystof.izastavka.data.remote.ApiUtils;
import cz.zemankrystof.izastavka.data.remote.StopsService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CityTransportFragment extends Fragment{
    private StopsService service;

    public static CityTransportFragment newInstance() {
        CityTransportFragment myFragment = new CityTransportFragment();
        /*Bundle args = new Bundle();
        args.putString("defaultURL", Constants.item1URL);
        myFragment.setArguments(args);*/
        return myFragment;
    }

    public CityTransportFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_link, null);
        service = ApiUtils.getStopsService();


        return rootView;
    }


    public void getSchemas(){

    }

    public String[] getSchemaUrls(){
        service.getSchemas().enqueue(new Callback<LinkSchemas>() {
            @Override
            public void onResponse(Call<LinkSchemas> call, Response<LinkSchemas> response) {

            }

            @Override
            public void onFailure(Call<LinkSchemas> call, Throwable t) {

            }
        });

        return null;
    }
}
