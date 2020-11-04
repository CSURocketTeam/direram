import com.kmschr.direram.aprs.APRSPacket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestAPRSPacket {

    APRSPacket testPacket;

    @BeforeEach
    void beforeEach() {
        this.testPacket = new APRSPacket("N6GN-2>APRS,W0UPS-15,WIDE2*:/040449h4036.62N/10514.52W-023/012/A=007513  Vp= 0.0, Vb=12.15, Vl=12.24, Ti=71  F, Te=55  F");
    }

    @Test
    void testIsAPRS() {
        Assertions.assertTrue(testPacket.isAPRS());
    }

    @Test
    void testGetCallSign() {
        Assertions.assertEquals("N6GN-2", testPacket.getCallSign());
    }

    @Test
    void testGetTimestamp() {
        Assertions.assertEquals("040449h", testPacket.getTimestamp());
    }

    @Test
    void testGetPosition() {
        Assertions.assertNotEquals(null, testPacket.getPosition());
    }

    @Test
    void testGetAltitude() {
        Assertions.assertEquals(7513, testPacket.getAltitude());
    }

    @Test
    void testGetCourse() {
        Assertions.assertEquals(23, testPacket.getCourse());
    }

    @Test
    void testGetSpeed() {
        Assertions.assertEquals(12, testPacket.getSpeed());
    }

}
