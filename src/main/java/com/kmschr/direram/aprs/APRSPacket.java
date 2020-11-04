package com.kmschr.direram.aprs;

import com.sothawo.mapjfx.Coordinate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class APRSPacket {

    private String rawPacket;
    private Date recieveTime;

    public APRSPacket(String packet) {
        this.rawPacket = packet;
        this.recieveTime = new Date(System.currentTimeMillis());
    }

    public String getRaw() {
        return rawPacket;
    }

    public String getCallSign() {
        int callEnd = rawPacket.indexOf('>');
        return rawPacket.substring(0, callEnd);
    }

    private String matchToRegex(String regex) {
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(this.rawPacket);
        if (matcher.find())
            return matcher.group(0);
        return "";
    }

    public boolean isAPRS() {
        String destination = matchToRegex(">A[^,]+,");

        if (destination.equals(""))
            return false;

        destination = destination.substring(1, destination.length()-1);
        return destination.startsWith("AP");
    }

    public String getTimestamp() {
        return matchToRegex("[0-9]{6}[z|/|h]");
    }

    public String getRecieveTime() {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(recieveTime);
    }

    private String getLat() {
        return matchToRegex("[0-9]{4}\\.[0-9]{2}[N|S]");
    }

    private String getLng() {
        return matchToRegex("[0-9]{5}\\.[0-9]{2}[W|E]");
    }

    public String getPositionString() {
        String latitude = getLat();
        String longitude = getLng();

        if (latitude.equals("") || longitude.equals(""))
            return null;

        int degLat = Integer.parseInt(latitude.substring(0, 2));
        double minLat = Double.parseDouble(latitude.substring(2,7));
        int degLng = Integer.parseInt(longitude.substring(0, 3));
        double minLng = Double.parseDouble(longitude.substring(3,8));

        return degLat + "\u00B0" + minLat + "'" + latitude.charAt(7) + " " + degLng + "\u00B0" + minLng + "'" + longitude.charAt(8);
    }

    public Coordinate getPosition() {
        String latitude = getLat();
        String longitude = getLng();

        if (latitude.equals("") || longitude.equals(""))
            return null;

        int degLat = Integer.parseInt(latitude.substring(0, 2));
        double minLat = Double.parseDouble(latitude.substring(2,7));
        int degLng = Integer.parseInt(longitude.substring(0, 3));
        double minLng = Double.parseDouble(longitude.substring(3,8));
        double lat = degLat + (minLat / 60.0);
        double lng = degLng + (minLng / 60.0);
        lat = latitude.charAt(7) == 'N' ? lat : -lat;
        lng = longitude.charAt(8) == 'E' ? lng : -lng;

        return new Coordinate(lat, lng);
    }

    // altitude in feet MSL
    public int getAltitude() {
        String alt = matchToRegex("/A=[0-9]{6}");
        if (alt.equals(""))
            return -1;
        return Integer.parseInt(alt.substring(3));
    }

    // degrees (001-360), clockwise from due north
    public int getCourse() {
        String csespd = matchToRegex("[0-9]{3}/[0-9]{3}");
        if (csespd.equals(""))
            return -1;
        return Integer.parseInt(csespd.substring(0,3));
    }

    // speed in knots
    public int getSpeed() {
        String csespd = matchToRegex("[0-9]{3}/[0-9]{3}");
        if (csespd.equals(""))
            return -1;
        return Integer.parseInt(csespd.substring(4));
    }

}
