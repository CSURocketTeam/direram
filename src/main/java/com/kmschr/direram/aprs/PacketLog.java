package com.kmschr.direram.aprs;

import com.sothawo.mapjfx.Coordinate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
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

            var formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss");
            String date = formatter.format(LocalDateTime.now());

            fw.write("""
                    <?xml version="1.0" encoding="UTF-8"?>
                    <kml xmlns="http://www.opengis.net/kml/2.2">
                      <Document>
                        <name>Aries CoVId</name>
                        <description>%s</description>
                        <Style id="yellowLineGreenPoly">
                          <LineStyle>
                            <color>7f00ffff</color>
                            <width>4</width>
                          </LineStyle>
                          <PolyStyle>
                            <color>7f00ff00</color>
                          </PolyStyle>
                        </Style>
                        <Placemark>
                          <name>Rocket Path</name>
                          <description>Rocket Path</description>
                          <styleURL>#yellowLineGreenPoly</styleURL>
                          <LineString>
                            <extrude>0</extrude>
                            <tessellate>1</tessellate>
                            <altitudeMode>relativeToGround</altitudeMode>
                            <coordinates>""".formatted(date));
            for (APRSPacket packet : packets) {
                Coordinate pos = packet.getPosition();
                if (!packet.isAPRS() || pos == null)
                    continue;
                fw.write("          %s, %s, %d\n".formatted(pos.getLongitude(), pos.getLatitude(), (int) (packet.getAltitude() / 0.3048)));
            }
            fw.write("""
                                </coordinates>
                          </LineString>
                        </Placemark>
                      </Document>
                    </kml>
                    """);

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
