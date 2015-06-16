package com.thepriest.andrea.zramtool;

import android.text.format.Time;

public class LogHelper {

    public enum LogColor {
        WHITE, RED, GREEN, BLUE, GRAY
    }

    private static final String TAG = LogHelper.class.getSimpleName();
    public static String sLogText = "";
    public static String sWhiteColor = "<br><font color=\"#FFFFFF\">"; //white
    public static String sGrayColor = "<br><font color=\"#666666\">"; //gray
    public static String sType1Color = "<br><font color=\"#FF0000\">"; //red
    public static String sType2Color = "<br><font color=\"#00FF00\">"; //green
    public static String sType3Color = "<br><font color=\"#0099cc\">"; //blue
    public static String sClose = "</font><font color=\"#FFEEEE\"></font>";

    private static String getCSSStyle() {
        return
                "<style type=\"text/css\">"
                        + "body {background-color: #000000; }"
                        + "h1 {color: #bebebe; }"
                        + "li {color: #bebebe; }"
                        + "ul {color: #bebebe; }"
                        + "h1 { margin-left: 0px; font-size: 16pt; }"
                        + "li { margin-left: 0px; font-size: 6pt; }"
                        + "ul { padding-left: 30px;}"
                        + "</style>";
    }

    public static void clearLog() {
        sLogText = getCSSStyle();
    }

    public static void appendLog(String sText) {
        if (sLogText.length() > (1048576)) clearLog(); // Size is 1 MB, 1024 KB?
        Time currentTime = new Time();
        currentTime.setToNow();
        sText = currentTime.format("%H:%M:%S") + " " + sText;
        sLogText = sWhiteColor + sText + "\n" + sLogText + sClose;
    }

    public static void appendLog(String sText, LogColor iType) {
        Time currentTime = new Time();
        currentTime.setToNow();
        sText = currentTime.format("%H:%M:%S") + " " + sText;
        //      sLogText = sText + "\n" + sLogText;
        switch (iType) {
            case RED:
                sLogText = sType1Color + sText + "\n" + sLogText + sClose;
                break;
            case GREEN:
                sLogText = sType2Color + sText + "\n" + sLogText + sClose;
                break;
            case BLUE:
                sLogText = sType3Color + sText + "\n" + sLogText + sClose;
                break;
            case GRAY:
                sLogText = sGrayColor + sText + "\n" + sLogText + sClose;
                break;
            default:
                break;
        }
    }


}