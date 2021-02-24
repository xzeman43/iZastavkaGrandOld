package cz.zemankrystof.izastavka.helpers;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import cz.zemankrystof.izastavka.R;

public class LineColorHelper {

    public int[] getRightColorForLine(Context context, String lineName) {
        int[] color = new int[2];
        color[1] = 0;
        if (lineName.contentEquals("001")) {
            color[0] =  context.getResources().getColor(R.color.lineOne);
        } else if (lineName.contentEquals("002")) {
            color[0] = context.getResources().getColor(R.color.lineTwo);
        } else if (lineName.contentEquals("003")) {
            color[0] = context.getResources().getColor(R.color.lineThree);
        } else if (lineName.contentEquals("004")) {
            color[0] = context.getResources().getColor(R.color.lineFour);
        } else if (lineName.contentEquals("005")) {
            color[0] = context.getResources().getColor(R.color.lineFive);
        } else if (lineName.contentEquals("006")) {
            color[0] = context.getResources().getColor(R.color.lineSix);
        } else if (lineName.contentEquals("007")) {
            color[0] = context.getResources().getColor(R.color.lineSeven);
        } else if (lineName.contentEquals("008")) {
            color[0] = context.getResources().getColor(R.color.lineEight);
        } else if (lineName.contentEquals("009")) {
            color[0] = context.getResources().getColor(R.color.lineNine);
        } else if (lineName.contentEquals("010")) {
            color[0] = context.getResources().getColor(R.color.lineTen);
        } else if (lineName.contentEquals("011")) {
            color[0] = context.getResources().getColor(R.color.lineEleven);
        } else if (lineName.contentEquals("012")) {
            color[0] = context.getResources().getColor(R.color.lineTwelve);
        } else if (lineName.contentEquals("013")) {
            color[0] = context.getResources().getColor(R.color.lineThirteen);
        } else if (lineName.contentEquals("014")) {
            color[0] = context.getResources().getColor(R.color.lineFourteen);
        } else if (lineName.contains("N")) {
            color[0] = context.getResources().getColor(R.color.lineNight);
            color[1] = context.getResources().getColor(R.color.white);
        } else if (isTroley(lineName)) {
            color[0] = context.getResources().getColor(R.color.lineTroley);
            color[1] = context.getResources().getColor(R.color.yellow);
        } else if (isMhd(lineName)) {
            color[0] = context.getResources().getColor(R.color.lineMhd);
        } else if (isNight(lineName)) {
            color[0] = context.getResources().getColor(R.color.lineNight);
            color[1] = context.getResources().getColor(R.color.yellow);
        } else {
            color[0] = context.getResources().getColor(R.color.DeepSkyBlue);
        }

        return color;
    }

    private boolean isTroley(String toCheck){
        for (int i = 24; i < 40; i++) {
            if (toCheck.contains("" + i)){
                return true;
            }
        }
        return false;
    }

    private boolean isMhd(String toCheck){
        for (int i = 40; i < 89; i++) {
            if (toCheck.contains("" + i) && !(toCheck.contains("E"))){
                return true;
            }
        }
        return false;
    }

    private boolean isNight(String toCheck){
        for (int i = 89; i < 100; i++) {
            if (toCheck.contains("" + i)){
                return true;
            }
        }
        return false;
    }

    private boolean isTram(String toCheck){
        String plainNumber = toCheck.replaceFirst("^0+(?!$)", "");
        for (int i = 0; i < 15; i++) {
            if (plainNumber.contentEquals("" + i) || plainNumber.contentEquals("0")){
                return true;
            }
        }
        return false;
    }

    public String getLineType(String lineNumber){
        if(isTram(lineNumber)){
            return "tram";
        }else if(isTroley(lineNumber)){
            return "troley";
        }else if(isMhd(lineNumber)){
            return "mhd";
        }else if(isNight(lineNumber)){
            return "nightmhd";
        }else{
            return "unknown";
        }
    }

    public Drawable getLineDrawable(Context context,String lineType){
        if(lineType.contentEquals("tram")){
            return context.getResources().getDrawable(R.drawable.tram_clear);
        }else if(lineType.contentEquals("troley")){
            return context.getResources().getDrawable(R.drawable.trolley_clear);
        }else if(lineType.contentEquals("mhd")){
            return context.getResources().getDrawable(R.drawable.bus_clear);
        }else if(lineType.contentEquals("nightmhd")){
            return context.getResources().getDrawable(R.drawable.bus_night_clear);
        }else{
            return null;
        }
    }
}
