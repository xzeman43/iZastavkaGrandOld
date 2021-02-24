package cz.zemankrystof.izastavka.helpers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import androidx.work.Worker;
import androidx.work.WorkerParameters;
import cz.zemankrystof.izastavka.MainActivity;
import cz.zemankrystof.izastavka.data.model.LinkSchema.DayLines;
import cz.zemankrystof.izastavka.data.model.LinkSchema.LinkSchemas;
import cz.zemankrystof.izastavka.data.model.LinkSchema.NightLines;
import cz.zemankrystof.izastavka.data.model.StopTimetables.StopTimetables;
import cz.zemankrystof.izastavka.data.remote.ApiUtils;
import cz.zemankrystof.izastavka.data.remote.StopsService;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static cz.zemankrystof.izastavka.data.Constants.postId;
import static cz.zemankrystof.izastavka.data.Constants.stopId;

public class DayNightWorkHelper extends Worker {

    public DayNightWorkHelper(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d("DayNightWorker", "Doing my job!");
        downloadDayNightTimetables();
        return Result.success();
    }

    public void downloadDayNightTimetables(){
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
