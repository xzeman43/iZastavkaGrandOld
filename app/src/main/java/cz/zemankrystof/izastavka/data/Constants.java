package cz.zemankrystof.izastavka.data;

import android.os.Environment;

import java.util.ArrayList;

import cz.zemankrystof.izastavka.R;

public class Constants {

    public static int item1IconActive = R.drawable.ikona_3amapapolohaaktiv;
    public static int item1IconInactive = R.drawable.ikona_3amapapoloha;
    public static String item1URL = "https://iris.bmhd.cz/";
    public static String item1URLcontent = "Location Map";

    public static int item2IconActive = R.drawable.ikona_3bvyhledavaniidsaktiv;
    public static int item2IconInactive = R.drawable.ikona_3bvyhledavaniids;
    public static String item2URL = "http://www.idsjmk.cz";
    public static String item2URLcontent = "Search for a link";

    public static int item3IconActive = R.drawable.ikona_3cdpbmactive;
    public static int item3IconInactive = R.drawable.ikona_3cdpmb;
    public static String item3URL = "http://www.dpmb.cz";
    public static String item3URLcontent = "City Transport";

    public static int item4IconActive = R.drawable.ikona_3dbrnoactive;
    public static int item4IconInactive = R.drawable.ikona_3dbrno;
    public static String item4URL = "https://www.gotobrno.cz/";
    public static String item4URLcontent = "About Brno";

    public static String fileLocation = Environment
            .getExternalStorageDirectory().toString()
            + "/iZastavka/";

    public static ArrayList<String> bannerURLs = new ArrayList<>();
    public static int bannerNum = 0;

    public static int stopId = 1397;
    //public static int stopId = 1146;
    public static int postId = 3;
}
