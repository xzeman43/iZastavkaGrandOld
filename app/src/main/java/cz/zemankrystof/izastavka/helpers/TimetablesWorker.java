package cz.zemankrystof.izastavka.helpers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import androidx.work.Worker;
import androidx.work.WorkerParameters;
import cz.zemankrystof.izastavka.data.Constants;
import cz.zemankrystof.izastavka.data.model.LinkSchema.DayLines;
import cz.zemankrystof.izastavka.data.model.LinkSchema.LinkSchemas;
import cz.zemankrystof.izastavka.data.model.LinkSchema.NightLines;
import cz.zemankrystof.izastavka.data.model.StopTimetables.StopTimetables;
import cz.zemankrystof.izastavka.data.model.StopTimetables.Timetable;
import cz.zemankrystof.izastavka.data.remote.ApiUtils;
import cz.zemankrystof.izastavka.data.remote.StopsService;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static cz.zemankrystof.izastavka.data.Constants.postId;
import static cz.zemankrystof.izastavka.data.Constants.stopId;

public class TimetablesWorker extends Worker{
    public TimetablesWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Worker.Result doWork() {
        Log.d("TimetablesWorker", "Doing my job!");
        downloadTimetables();
        return Result.success();
    }

    public void downloadTimetables(){
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
                        //lastModified = realm.where(StopTimetables.class).findFirst().getLastModified();
                    }
                    Log.d("TimetablesLastModified" , ""+ response.body().getLastModified() + " mynum: " + lastModified);
                    if(response.body().getLastModified() != lastModified) {
                        Log.d("TimetablesRealm", "inputting data to realm!");
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                RealmResults resa = realm.where(StopTimetables.class).findAll();
                                resa.deleteAllFromRealm();
                                RealmResults res = realm.where(Timetable.class).findAll();
                                res.deleteAllFromRealm();
                                realm.copyToRealmOrUpdate(timetables);
                            }
                        });
                        File file = new File(Constants.fileLocation + "timetables");
                        try {
                            FileUtils.deleteDirectory(file);
                            Log.d("MRDKA", "DELETED");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        new DownloadHelper().execute(timetables);
                    }
                    realm.close();
                }
            }

            @Override
            public void onFailure(Call<StopTimetables> call, Throwable t) {
                Log.d("Timetables", "Failed " + t.getMessage());
            }
        });
    }

}
