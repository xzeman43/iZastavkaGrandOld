package cz.zemankrystof.izastavka.helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import cz.zemankrystof.izastavka.data.model.StopTimetables.StopTimetables;
import cz.zemankrystof.izastavka.data.remote.ApiUtils;
import cz.zemankrystof.izastavka.data.remote.StopsService;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static cz.zemankrystof.izastavka.data.Constants.postId;
import static cz.zemankrystof.izastavka.data.Constants.stopId;

public class TimetablesBR extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("TimetableBR", "Downloading timetables!");
        downloadTimetables();
    }

    private void downloadTimetables(){
        StopsService service = ApiUtils.getStopsService();
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
                    realm.close();
                }
            }

            @Override
            public void onFailure(Call<StopTimetables> call, Throwable t) {
                Log.d("Timetables", "Failed" + t.getMessage());
            }
        });
    }
}
