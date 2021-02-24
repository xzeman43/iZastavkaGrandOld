package cz.zemankrystof.izastavka.helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import cz.zemankrystof.izastavka.data.model.LinkSchema.DayLines;
import cz.zemankrystof.izastavka.data.model.LinkSchema.LinkSchemas;
import cz.zemankrystof.izastavka.data.model.LinkSchema.NightLines;
import cz.zemankrystof.izastavka.data.remote.ApiUtils;
import cz.zemankrystof.izastavka.data.remote.StopsService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DayNightBR extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("DayNightBR", "Downloading dayNight timetables!");
        downloadDayNightTimetables();
    }

    private void downloadDayNightTimetables(){
        StopsService service = ApiUtils.getStopsService();
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
}
