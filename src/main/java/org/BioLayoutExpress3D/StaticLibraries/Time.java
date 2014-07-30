package org.BioLayoutExpress3D.StaticLibraries;

import static java.lang.Math.*;

/**
*
* Time is a final class containing only static methods for some simple time calculations. It also support 'negative' time (time below zero).
*
* @author Thanos Theo, Georgios Moralis, 2008-2009
* @version 3.0.0.0
*/

public final class Time
{

    /**
    *  Converts a time string of the format 00:00:00 to seconds.
    */
    public static int convertTimeStringInSeconds(String time)
    {
        int firstColon = time.indexOf(':');
        int secondColon = time.indexOf(':', firstColon + 1);

        int hour = 0;
        int minute = 0;
        int second = Integer.parseInt( time.substring(secondColon + 1) );

        if ( (firstColon > 0) & (secondColon > 0) & (secondColon < time.length() - 1) )
        {
            hour = Integer.parseInt( time.substring(0, firstColon) );
            minute = Integer.parseInt( time.substring(firstColon + 1, secondColon) );
        }

        return ( ( time.startsWith("-") ) ? -1 : 1 ) * ( (hour * 3600) + (minute * 60) + second );
    }

    /**
    *  Checks the msecs passed and returns a string informing about the time elapsed.
    *  Proper 00:00:00 00 format for graphical presentation.
    */
    public static String convertSecondsToTimeString(int timePassed)
    {
        String sign = (timePassed < 0) ? "-" : "";
        if (timePassed < 0) timePassed = abs(timePassed);

        String sHr = "00", sMin = "00", sSec = "00";
        int iHr = 0, iMin = 0, iSec = 0;
        int remainder = 0;

        iHr = timePassed / (60 * 60);
        // A quick way to have a proper output for the sHr string. ? = if, : = else.
        sHr = (iHr < 10) ? "0" + Integer.toString(iHr) : Integer.toString(iHr);

        remainder = timePassed % (60 * 60);

        iMin = remainder / 60;
        // A quick way to have a proper output for the sMin string. ? = if, : = else.
        sMin = (iMin < 10) ? "0" + Integer.toString(iMin) : Integer.toString(iMin);

        remainder = remainder % 60;

        iSec = remainder;
        // A quick way to have a proper output for the sSec string. ? = if, : = else.
        sSec = (iSec < 10) ? "0" + Integer.toString(iSec) : Integer.toString(iSec);

        return ( sign + (sHr + ":" + sMin + ":" + sSec) );
    }

    /**
    *  Checks the msecs passed and returns a string informing about the time elapsed.
    *  Proper 00:00:00 00 format for graphical presentation.
    */
    public static String convertMSecsToTimeString(long timePassed)
    {
        String sign = (timePassed < 0) ? "-" : "";
        if (timePassed < 0) timePassed = abs(timePassed);

        String sHr = "00", sMin = "00", sSec = "00", sMsec = "00";
        int iHr = 0, iMin = 0, iSec = 0, iMsec = 0;
        int remainder = 0;

        iHr = (int)( timePassed / (1000 * 60 * 60) );
        // A quick way to have a proper output for the sHr string. ? = if, : = else.
        sHr = (iHr < 10) ? "0" + Integer.toString(iHr) : Integer.toString(iHr);

        remainder = (int)( timePassed % (1000 * 60 * 60) );

        iMin = remainder / (1000 * 60);
        // A quick way to have a proper output for the sMin string. ? = if, : = else.
        sMin = (iMin < 10) ? "0" + Integer.toString(iMin) : Integer.toString(iMin);

        remainder = remainder % (1000 * 60);

        iSec = remainder / 1000;
        // A quick way to have a proper output for the sSec string. ? = if, : = else.
        sSec = (iSec < 10) ? "0" + Integer.toString(iSec) : Integer.toString(iSec);

        iMsec = remainder % 1000;
        // A quick way to have a proper output for the sMsec string. ? = if, : = else.
        sMsec = (iMsec < 10) ? "0" + Integer.toString(iMsec) : Integer.toString(iMsec);

        return  ( sign + (sHr + ":" + sMin + ":" + sSec + " " + sMsec) );
    }


}