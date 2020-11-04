package com.kmschr.direram.aprs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PacketLog {

    private List<APRSPacket> packets;

    public PacketLog() {
        this.packets = new ArrayList<>();
    }

    public void addPacket(APRSPacket packet) {
        this.packets.add(packet);
    }

    public void writeKML(File kmlFile) {
        try {
            FileWriter fw = new FileWriter(kmlFile.getCanonicalPath());

            SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
            Date date = new Date(System.currentTimeMillis());

            fw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                     "<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n" +
                     "  <Document>\n" +
                     "    <name>Aries CoVId</name>\n" +
                     "    <description>" + formatter.format(date) + "</description>\n" +
                     "    <Style id=\"yellowLineGreenPoly\">\n" +
                     "      <LineStyle>\n" +
                     "        <color>7f00ffff</color>\n" +
                     "        <width>4</width>\n" +
                     "      </LineStyle>\n" +
                     "      <PolyStyle>\n" +
                     "        <color>7f00ff00</color>\n" +
                     "      </PolyStyle>\n" +
                     "    </Style>\n" +
                     "    <Placemark>\n" +
                     "      <name>Rocket Path</name>\n" +
                     "      <description>Rocket Path</description>\n" +
                     "      <styleURL>#yellowLineGreenPoly</styleURL>\n" +
                     "      <LineString>\n" +
                     "        <extrude>0</extrude>\n" +
                     "        <tessellate>1</tessellate>\n" +
                     "        <altitudeMode>relativeToGround</altitudeMode>\n" +
                     "        <coordinates>\n");

            for (APRSPacket packet : packets) {
                if (!packet.isAPRS() || packet.getPosition() == null || !packet.getCallSign().equals("K0BKK-9"))
                    continue;
                fw.write("          " + packet.getPosition().getLongitude() + "," + packet.getPosition().getLatitude() + "," + (int) (packet.getAltitude() / 0.3048) + "\n");
            }

            fw.write("        </coordinates>\n" +
                     "      </LineString>\n" +
                     "    </Placemark>\n" +
                     "  </Document>\n" +
                     "</kml>");
            fw.close();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    public void writeCSV(File csvFile) {
        try {
            FileWriter fw = new FileWriter(csvFile.getCanonicalPath());
            fw.write("RAW_PACKET,CALLSIGN,TIME,LONGITUDE,LATITUDE,ALTITUDE\n");

            for (APRSPacket packet : packets) {
                if (!packet.isAPRS() || packet.getPosition() == null)
                    continue;
                fw.write(packet.getRaw().replace(',', '.') + "," +
                        packet.getCallSign() + "," +
                        packet.getRecieveTime() + "," +
                        packet.getPosition().getLongitude() + "," +
                        packet.getPosition().getLatitude() + "," +
                        packet.getAltitude() + "\n");
            }

            fw.close();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

}
