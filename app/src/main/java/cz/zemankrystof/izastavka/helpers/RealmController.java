package cz.zemankrystof.izastavka.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import cz.zemankrystof.izastavka.data.model.LinkSchema.DayLines;
import cz.zemankrystof.izastavka.data.model.LinkSchema.NightLines;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;


/**
 * Created by Krystof Zeman on 06.8.2018.
 */

public class RealmController {

    private Realm realm;

    private static volatile RealmController sRealmController = new RealmController();

    private RealmController(){
        realm = Realm.getDefaultInstance();
    }

    public static RealmController getInstance(){
        return sRealmController;
    }

    public void addDayLines(final DayLines dayLines){

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(dayLines);
            }
        });
    }

    public void addNightLines(final NightLines nightLines){

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(nightLines);
            }
        });
    }

    //public void addDayNightLinesPaths(String)

    public String[] getDayNightSchemasURL(){
        String[] dayNightSchemas = new String[2];
        dayNightSchemas[0] = realm.copyFromRealm(realm.where(DayLines.class).findFirst()).getImageURL();
        dayNightSchemas[1] = realm.copyFromRealm(realm.where(NightLines.class).findFirst()).getImageURL();
        return dayNightSchemas;
    }


/*
    public void cascadeDocumentDelete(final int userId){


        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                try {
                    Document doc = realm.where(User.class).equalTo("id",userId).findFirst().getDocuments().last();
                    delete(doc);
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }

    public void cascadeDeleteUser(final int userId){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                User user = realm.where(User.class).equalTo("id",userId).findFirst();
                delete(user);
            }
        });
    }
*/
    public static void delete(RealmObject rootObject) {
        if (rootObject == null) {
            return;
        }

        for (Method method : rootObject.getClass().getSuperclass().getDeclaredMethods()) {
            try {
                // Ignore non-getter methods
                boolean noParams = method.getParameterTypes().length == 0;
                if (!(method.getName().startsWith("get")) || !noParams) {
                    continue;
                }

                // Ignore primitive members
                Class<?> resultType = method.getReturnType();
                if (resultType.isPrimitive()) {
                    continue;
                }

                /*// Ignore methods annotated with SkipDelete
                if (method.isAnnotationPresent(SkipDelete.class)) {
                    continue;
                }*/

                if (RealmObject.class.isAssignableFrom(resultType)) {
                    // getter method returns a RealmObject, delete it
                    try {
                        RealmObject childObject = (RealmObject) method.invoke(rootObject);
                        delete(childObject);
                    } catch (Exception ex) {
                        Log.e("delete: RealmObject " + resultType.getSimpleName(), ex.toString());
                    }

                } else if (RealmList.class.isAssignableFrom(resultType)) {
                    // getter method returns a RealmList, delete the objects in the list
                    try {
                        RealmList childList = (RealmList) method.invoke(rootObject);
                        while (childList.iterator().hasNext()) {
                            RealmObject listItem = (RealmObject)childList.iterator().next();
                            Log.d("Deleting", listItem.toString());
                            delete(listItem);
                        }
                    } catch (Exception ex) {
                        Log.e("delete: RealmList " + resultType.getSimpleName(), ex.toString());
                    }
                }
            }
            catch (Exception ex) {
                Log.e("delete: ", ex.toString());
            }
        }

        rootObject.deleteFromRealm();
    }

    /*public String getCurrentToken(String userName){
        Realm realm = Realm.getDefaultInstance();
        final User user = realm.where(User.class).equalTo("userName",userName).findFirst();
    }*/

    /* realm.executeTransaction(new Realm.Transaction() {
    @Override
    public void execute(Realm realm) {
        user.setCountry("Ceska Rep");
        user.setStreet("Domamysl 39");
        user.setPc_dc("391 55, Chýnov");
    }
    });*/



}
