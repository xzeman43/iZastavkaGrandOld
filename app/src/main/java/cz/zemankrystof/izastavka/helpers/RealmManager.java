package cz.zemankrystof.izastavka.helpers;

import android.content.Context;

import cz.zemankrystof.izastavka.data.model.LinkSchema.DayLines;
import io.realm.Realm;
import io.realm.RealmConfiguration;


// CURRENTLY NOT IN USE
public class RealmManager {
    static Realm realm;

    static RealmConfiguration realmConfiguration;

    public static void initializeRealmConfig(Context appContext) {
        if(realmConfiguration == null) {
            setRealmConfiguration(new RealmConfiguration.Builder()
                    .deleteRealmIfMigrationNeeded()
                    .build());
        }
    }

    public static void setRealmConfiguration(RealmConfiguration realmConfiguration) {
        RealmManager.realmConfiguration = realmConfiguration;
        Realm.setDefaultConfiguration(realmConfiguration);
    }

    private static int activityCount = 0;

    public static Realm getRealm() {
        return realm;
    }

    public static void incrementCount() {
        if(activityCount == 0) {
            if(realm != null) {
                if(!realm.isClosed()) {
                    realm.close();
                    realm = null;
                }
            }
            realm = Realm.getDefaultInstance();
        }
        activityCount++;
    }

    public static void decrementCount() {
        activityCount--;
        if(activityCount <= 0) {
            activityCount = 0;
            realm.close();
            Realm.compactRealm(realmConfiguration);
            realm = null;
        }
    }

    public void addDayLines(final DayLines dayLines){

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(dayLines);
            }
        });
    }
}