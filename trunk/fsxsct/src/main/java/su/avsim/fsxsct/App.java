package su.avsim.fsxsct;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.*;
//import java.text.DecimalFormatSymbols;

//import java.util.Formatter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;


class GeoCords {
    double lat, lon;

    public GeoCords (double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

}
/**
 * ����� �������� ��������� ����� ��
 */
class TaxiwayPoint { // �����������

    int index;
    taxiwayPointType type;
    taxiwayPointOrientation ori;
    double lat, lon;

    public enum taxiwayPointType {

        NORMAL, HOLD_SHORT, ILS_HOLD_SHORT
    }

    public enum taxiwayPointOrientation {

        FORWARD, REVERSE
    }

    public TaxiwayPoint(int index,
            taxiwayPointType type,
            taxiwayPointOrientation ori,
            double lat,
            double lon) {
        this.index = index;
        this.type = type;
        this.ori = ori;
        this.lat = lat;
        this.lon = lon;
    }

    public static taxiwayPointType returnType(String s) {
        if (s.equalsIgnoreCase("NORMAL")) {
            return taxiwayPointType.NORMAL;
        }
        if (s.equalsIgnoreCase("HOLD_SHORT")) {
            return taxiwayPointType.HOLD_SHORT;
        }
        if (s.equalsIgnoreCase("ILS_HOLD_SHORT")) {
            return taxiwayPointType.ILS_HOLD_SHORT;
        }
        return taxiwayPointType.NORMAL;
    }

    public static taxiwayPointOrientation returnOrientation(String s) {
        if (s.equalsIgnoreCase("FORWARD")) {
            return taxiwayPointOrientation.FORWARD;
        }
        if (s.equalsIgnoreCase("REVERSE")) {
            return taxiwayPointOrientation.REVERSE;
        }
        return taxiwayPointOrientation.FORWARD;
    }
}

/**
 * ���� ����� � ��� �������� �� ������������� ������������������ �����
 * ���� ������ �����������
 * @author dagon
 */
class Trig {

    /**
     * ����� ��������� �������� ������ �� ����� 1 � ����� 2
     * ������������������ ��������� �������� �� ���������� ����� ���������� - ���� �� ����
     *
     * @param startLat
     * @param startLon
     * @param endLat
     * @param endLon
     * @return
     */
    public static float getBearing(double startLat, double startLon, double endLat, double endLon) {
        //double q;
        //double dLat = Math.toRadians(endLat - startLat);
        double dLon = Math.toRadians(endLon - startLon);
        startLat = Math.toRadians(startLat);
        endLat = Math.toRadians(endLat);
        double dPhi = Math.log(Math.tan(endLat / 2 + Math.PI / 4) / Math.tan(startLat / 2 + Math.PI / 4));

        /*if (dPhi != 0) {
        q = dLat / dPhi;
        } else {
        q = Math.cos(startLat);
        }
        if (Math.abs(dLon) > Math.PI) {
        dLon = dLon > 0 ? -(2 * Math.PI - dLon) : (2 * Math.PI + dLon);
        }
        //var d = Math.sqrt(dLat * dLat + q * q * dLon * dLon) * R;
         */
        return Double.valueOf(Math.atan2(dLon, dPhi)).floatValue();
    }

    public static GeoCords getPoint(double lat, double lon, float dist) {
        
        return new GeoCords (1d,1d);
    }
}

public class App
        extends DefaultHandler {

    List<TaxiwayPoint> TaxiwayPointsList = new ArrayList<TaxiwayPoint>(); // ������, ���������� ����� ��

    public void parseURI(String uri) {
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            sp.parse(uri, this);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    /** Start document. */
    public void startDocument() {
        //System.out.println("<?xml version=\"1.0\"?>");
    } // startDocument()

    protected int getAsInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            System.err.println("Error while converting string to int" + e);
            return 0;
        }


    }

    protected String splitDegrees(double coord) { // �������� ������� � �������� �� ��/���/c
        String coordString = new String();
        try {
            double degrees = Math.floor(coord);
            double tempMinutesLeft = (coord - degrees) * 60.0d;
            double minutes = Math.floor(tempMinutesLeft);
            double seconds = (tempMinutesLeft - minutes) * 60.0d; // � ������
            // ���������� ������������ ������, ����� ������� ���������� ������
            coordString = String.format(Locale.US, "%1$03.0f.%2$02.0f.%3$06.3f", degrees, minutes, seconds);
        } catch (Exception e) {
            System.err.println("������ ��� �������� ���������� " + coordString + " " + e);
        }

        return coordString;
    }

    protected String getLat(double lat) { // ��������� ������ � ������ sct
        String latString = new String();
        if (lat >= 0) {
            latString = "N";
        }
        if (lat <= 0) {
            latString = "S";
            lat = -lat;
        }
        latString += splitDegrees(lat);
        return latString;
    }

    protected String getLon(double lon) { // ��������� ������ � ������ sct
        String lonString = new String();
        if (lon >= 0) {
            lonString = "E";
        }
        if (lon <= 0) {
            lonString = "W";
            lon = -lon;
        }
        lonString += splitDegrees(lon);

        return lonString;
    }

    protected String drawTaxiwayBorders(double startLat, double startLon, double endLat,
            double endLon, float width) {
        System.out.println(Math.toDegrees(Trig.getBearing(startLat, startLon, endLat, endLon)) + "�");

        return "";
    }

    /** Start element. */
    public void startElement(String namespaceURI, String localName,
            String rawName, Attributes attrs) {
        if (rawName.equals("TaxiwayPoint")) { // ��������� �����, �������� ��

            /*
            // ����� �� ������� ��� �������
            try {
            System.out.print(Integer.parseInt(attrs.getValue("index")) + " " + attrs.getValue("type"));
            System.out.println(" " + attrs.getValue("orientation") +
            " lat " + attrs.getValue("lat") + " lon '" + attrs.getValue("lon") + "'");
            //System.out.println ("test" + "'" + Float.valueOf(attrs.getValue("lat")).floatValue() + "'");
            System.out.println("converted geo: " + Double.valueOf(attrs.getValue("lat")).doubleValue() +
            " " + Double.valueOf(attrs.getValue("lon")).doubleValue());
            System.out.println("enums: " + TaxiwayPoint.returnType(attrs.getValue("type")) +
            TaxiwayPoint.returnOrientation(attrs.getValue("orientation")));
            } catch (Exception e) {
            System.err.println(e);
            }
             */

            try {
                TaxiwayPointsList.add(Integer.parseInt(attrs.getValue("index")),
                        new TaxiwayPoint(Integer.parseInt(attrs.getValue("index")),
                        TaxiwayPoint.returnType(attrs.getValue("type")),
                        TaxiwayPoint.returnOrientation(attrs.getValue("orientation")),
                        Double.valueOf(attrs.getValue("lat")).doubleValue(),
                        Double.valueOf(attrs.getValue("lon")).doubleValue()));

            } catch (Exception e) {
                System.err.println(e);
            }

        }

        if (rawName.equals("TaxiwayPath") && // ���������� ���� ��� �������
                (attrs.getValue("type").equalsIgnoreCase("TAXI"))) {
            // || (attrs.getValue("type").equalsIgnoreCase("PATH")))) // ��������� �����, �������� ��

            System.out.println("UUDD " + getLat(TaxiwayPointsList.get(getAsInt(attrs.getValue("start"))).lat) + " " +
                    getLon(TaxiwayPointsList.get(getAsInt(attrs.getValue("start"))).lon) + " " +
                    getLat(TaxiwayPointsList.get(getAsInt(attrs.getValue("end"))).lat) + " " +
                    getLon(TaxiwayPointsList.get(getAsInt(attrs.getValue("end"))).lon));
        }

        if (rawName.equals("TaxiwayPath") && // �������� �������
                (attrs.getValue("type").equalsIgnoreCase("TAXI"))) {
            System.out.println(drawTaxiwayBorders(
                    TaxiwayPointsList.get(getAsInt(attrs.getValue("start"))).lat,
                    TaxiwayPointsList.get(getAsInt(attrs.getValue("start"))).lon,
                    TaxiwayPointsList.get(getAsInt(attrs.getValue("end"))).lat,
                    TaxiwayPointsList.get(getAsInt(attrs.getValue("end"))).lon,
                    Float.valueOf(attrs.getValue("width")).floatValue()));
        }
    }

    /** Characters. */
    public void characters(char ch[], int start, int length) {
        //System.out.print(new String(ch, start, length));
    } // characters(char[],int,int);

    /** Ignorable whitespace. */
    public void ignorableWhitespace(char ch[], int start, int length) {
        //characters(ch, start, length);
    } // ignorableWhitespace(char[],int,int);

    /** End element. */
    public void endElement(String namespaceURI, String localName,
            String rawName) {
        //System.out.print("</");
        //System.out.print(rawName);
        //System.out.print(">");
    } // endElement(String)

    /** End document. */
    public void endDocument() {
        // No need to do anything.
    } // endDocument()

    /** Processing instruction. */
    public void processingInstruction(String target, String data) {
        System.out.print("<?");
        System.out.print(target);
        if (data != null && data.length() > 0) {
            System.out.print(' ');
            System.out.print(data);
        }
        System.out.print("?>");

    } // processingInstruction(String,String)

    //
    // ErrorHandler methods
    //
    /** Warning. */
    public void warning(SAXParseException ex) {
        System.err.println("[Warning] " +
                getLocationString(ex) + ": " +
                ex.getMessage());
    }

    /** Error. */
    public void error(SAXParseException ex) {
        System.err.println("[Error] " +
                getLocationString(ex) + ": " +
                ex.getMessage());
    }

    /** Fatal error. */
    public void fatalError(SAXParseException ex)
            throws SAXException {
        System.err.println("[Fatal Error] " +
                getLocationString(ex) + ": " +
                ex.getMessage());
        throw ex;
    }

    /** Returns a string of the location. */
    private String getLocationString(SAXParseException ex) {
        StringBuffer str = new StringBuffer();

        String systemId = ex.getSystemId();
        if (systemId != null) {
            int index = systemId.lastIndexOf('/');
            if (index != -1) {
                systemId = systemId.substring(index + 1);
            }
            str.append(systemId);
        }
        str.append(':');
        str.append(ex.getLineNumber());
        str.append(':');
        str.append(ex.getColumnNumber());

        return str.toString();
    } // getLocationString(SAXParseException):String

    /** Main program entry point. */
    public static void main(String argv[]) {
        System.out.println(argv[0]);
        if (argv.length == 0 ||
                (argv.length == 1 && argv[0].equals("-help"))) {
            System.out.println("\nUsage:  java App uri");
            System.out.println("   where uri is the URI of your XML document.");
            System.out.println("   Sample:  java App sonnet.xml");
            System.out.println("\nEchoes SAX events back to the console.");
            System.exit(1);
        }
        //java.text.DecimalFormatSymbols.setDecimalSeparator (".");
        App s1 = new App();
        s1.parseURI(argv[0]);
    } // main(String[])
}
