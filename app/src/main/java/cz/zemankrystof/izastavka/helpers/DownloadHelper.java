package cz.zemankrystof.izastavka.helpers;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.apache.commons.io.FileUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import cz.zemankrystof.izastavka.data.Constants;
import cz.zemankrystof.izastavka.data.model.LinkSchema.DayLines;
import cz.zemankrystof.izastavka.data.model.LinkSchema.NightLines;
import cz.zemankrystof.izastavka.data.model.StopTimetables.StopTimetables;
import cz.zemankrystof.izastavka.data.model.StopTimetables.Timetable;
import io.realm.Realm;
import io.realm.RealmList;

public class DownloadHelper extends AsyncTask<Object, String, String> {

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(Object... f_url) {

            for (int i = 0; i < f_url.length; i++) {
                if (f_url[i].getClass().equals(NightLines.class)){
                    NightLines nightLines = (NightLines) f_url[i];
                    String urlString = nightLines.getImageURL();
                    String downloadDirectory = "dayNight";
                    nightLines.setImageLocation(getName(urlString));
                    if(downloadAndSaveFile(urlString, downloadDirectory)) {
                        addToRealmAsRightObject(f_url[i], NightLines.class);
                    }

                } else if(f_url[i].getClass().equals(DayLines.class)){
                    DayLines dayLines = (DayLines)f_url[i];
                    String urlString = dayLines.getImageURL();
                    String downloadDirectory = "dayNight";
                    dayLines.setImageLocation(getName(urlString));
                    if(downloadAndSaveFile(urlString, downloadDirectory)) {
                        addToRealmAsRightObject(f_url[i], DayLines.class);
                    }
                } else if (f_url[i].getClass().equals(StopTimetables.class)) {
                    List<Timetable> timetables = ((StopTimetables)f_url[i]).getTimetables();
                    for (int j = 0; j < timetables.size(); j++) {
                        String urlString = timetables.get(j).getTimetableURL();
                        String downloadDirectory = "timetables";
                        String name = getName(urlString);
                        Log.d("TimetableName", "name: " + name);
                        timetables.get(j).setTimetableImageLocation(name);
                        if(downloadAndSaveFile(urlString,downloadDirectory)){
                            addToRealmAsRightObject(timetables.get(j), Timetable.class);
                        }
                    }
                }
            }
            return null;
        }

    /**
     * Method that split the string via / and use the last concat to get the name
     * @param getNameFrom string to parse the name from
     * @return the pdf name in format: name.pdf
     */
    private String getName(String getNameFrom){
        if (getNameFrom != null) {
            String[] returned;
            returned = getNameFrom.split("/");
            Log.d("Downloaded", "Location name: " + returned[returned.length - 1]);
            return returned[returned.length - 1];
        }else {
            return null;
        }
    }


    private boolean downloadAndSaveFile (String urlString, String downloadDirectory){
        try {
            int count;
            URL url = new URL(urlString);
            URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
            Log.d("URLParts", "Protocol: " + url.getProtocol() + " UserInfo: " + url.getUserInfo() + " Host: " + url.getHost() + " Port: " + url.getPort() + " Path: " +url.getPath()+ " Query: " + url.getQuery() + " Ref: " + url.getRef());
            url = new URL(uri.toASCIIString().replaceAll("\\(", "%28").replaceAll("\\)", "%29"));
            Log.d("URL", url.toString());
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(10000);
            connection.connect();

            // download the file
            InputStream input = new BufferedInputStream(url.openStream(),
                    8192);


            File file = new File(Constants.fileLocation + downloadDirectory + "/" );
            boolean filesCreated = file.mkdirs();
            // Output stream

            String name = getName(urlString);
            Log.d("OutputName", "Currently: " + Constants.fileLocation + downloadDirectory + "/" + name);
            FileOutputStream output = new FileOutputStream(Constants.fileLocation + downloadDirectory + "/" + name);

            byte data[] = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;

                // writing data to file
                output.write(data, 0, count);
            }

            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();
        } catch (FileNotFoundException e) {
            Log.e("Error: ", "FileNotFound " + e.getMessage());
            return false;
        } catch (MalformedURLException e) {
            Log.e("Error: ", "MalformedURL " + e.getMessage());
            return false;
        } catch (IOException e) {
            Log.e("Error: ", "IOException " + e.getMessage());
            return false;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return true;
    }

    private void addToRealmAsRightObject(final Object obj, Class classType){

        //RealmController controller = RealmController.getInstance();
        Realm realm = Realm.getDefaultInstance();

        if (classType == DayLines.class){
            //controller.addDayLines((DayLines) obj);
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate((DayLines)obj);
                }
            });
        }else if(classType == NightLines.class){
            //controller.addNightLines((NightLines) obj);
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate((NightLines)obj);
                }
            });
        }else if(classType == StopTimetables.class){
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate((StopTimetables)obj);
                }
            });
        }else if(classType == Timetable.class){
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Log.d("Timetable", "Adding to realm with filename");
                    realm.copyToRealmOrUpdate((Timetable)obj);
                }
            });
        }
        realm.close();
    }
}
