package de.joglearth.location.nominatim;

import static javax.xml.stream.XMLStreamConstants.CHARACTERS;
import static javax.xml.stream.XMLStreamConstants.END_DOCUMENT;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.management.Query;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import de.joglearth.geometry.Camera;
import de.joglearth.geometry.CameraUtils;
import de.joglearth.geometry.GeoCoordinates;
import de.joglearth.geometry.ScreenCoordinates;
import de.joglearth.geometry.Tile;
import de.joglearth.location.Location;
import de.joglearth.location.LocationType;
import de.joglearth.map.osm.OSMMapConfiguration;
import de.joglearth.map.osm.OSMTile;
import de.joglearth.settings.Settings;
import de.joglearth.settings.SettingsContract;
import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.SourceResponseType;
import de.joglearth.util.HTTP;


/**
 * Provides responses of search requests, for e.g. search requests for places or detailed
 * information to a point. The response will be prepared for the
 * {@link de.joglearth.surface.LocationManager}; uses the {@link de.joglearth.util.HTTP} for the
 * search request.
 * 
 */
public class NominatimSource implements Source<NominatimQuery, Collection<Location>> {

    private static final String XML_ELEMENT_ENTRY = "place";
    private final ExecutorService executor;


    /**
     * Constructor. Initializes the {@link de.joglearth.location.nominatim.NominatimSource}.
     */
    public NominatimSource() {
        executor = Executors.newFixedThreadPool(2);
    }

    @Override
    public SourceResponse<Collection<Location>> requestObject(final NominatimQuery key,
            final SourceListener<NominatimQuery, Collection<Location>> sender) {
        if (!(key instanceof NominatimQuery)) {
            return new SourceResponse<Collection<Location>>(SourceResponseType.MISSING, null);
        }

        executor.execute(new Runnable() {

            @Override
            public void run() {
                Collection<Location> response;

                if (key.type == NominatimQuery.Type.POINT) {
                    response = getPoint(key);
                } else {
                    response = getLocations(key);
                }

                sender.requestCompleted(key, response);
            }
        });

        return new SourceResponse<Collection<Location>>(SourceResponseType.ASYNCHRONOUS, null);
    }

    private Collection<Location> getLocations(NominatimQuery query) {

        String url = "http://nominatim.openstreetmap.org/search";
        // Get-Request: "?accept-language=de&format=xml&q=Rom";

        ArrayList<String> getRequest = new ArrayList<String>();
        getRequest.add("accept-language");
        getRequest.add(Settings.getInstance().getString(SettingsContract.LANGUAGE).toLowerCase());
        // TODO zum Testen mit main() s.u.
        // getRequest.add("de");
        getRequest.add("format");
        getRequest.add("xml");

        if (query.type == NominatimQuery.Type.LOCAL) {
            getRequest.add("viewbox");
            double left = query.area.getLongitudeFrom();
            double right = query.area.getLongitudeTo();
            double top = query.area.getLatitudeFrom();
            double bottom = query.area.getLatitudeTo();
            getRequest.add(left + "," + top + "," + right + "," + bottom);
        }

        // TODO weiteres, wie bounding box
        getRequest.add("q");
        getRequest.add(query.query);

        byte[] httpRes = HTTP.get(url, getRequest);
        String xmlResponse = new String(httpRes);

        ArrayList<Location> response = parseXml(xmlResponse);

        return response;
    }

    private Collection<Location> getPoint(NominatimQuery query) {

        String url = "http://nominatim.openstreetmap.org/reverse";
        // Get-Request : "?accept-language=de&format=xml&q=Rom";

        ArrayList<String> getRequest = new ArrayList<String>();
        getRequest.add("accept-language");
        getRequest.add(Settings.getInstance().getString(SettingsContract.LANGUAGE).toLowerCase());
        getRequest.add("format");
        getRequest.add("xml");

        getRequest.add("lat");
        getRequest.add("" + query.point.getLatitude());
        getRequest.add("lon");
        getRequest.add("" + query.point.getLongitude());

        byte[] httpRes = HTTP.get(url, getRequest);

        if (httpRes == null) {
            return new ArrayList<Location>();
        }

        String xmlResponse = new String(httpRes);

        ArrayList<Location> response = parseXml(xmlResponse);

        return response;
    }

    public Location reverseSearch(String osmId, String osmType) {

        System.out.println();

        String url = "http://nominatim.openstreetmap.org/reverse";
        // Get-Request: "?accept-language=de&format=xml&osm_id=...&osm_type=[N|W|R]";

        ArrayList<String> getRequest = new ArrayList<String>();
        getRequest.add("accept-language");
        getRequest.add(Settings.getInstance().getString(SettingsContract.LANGUAGE).toLowerCase());
        getRequest.add("format");
        getRequest.add("xml");
        getRequest.add("osm_id");
        getRequest.add(osmId);
        getRequest.add("osm_type");
        getRequest.add(osmType);

        byte[] httpRes = HTTP.get(url, getRequest);
        if (httpRes == null) {
            return null;
        }

        String xmlResponse = new String(httpRes);

        System.out.println(xmlResponse);

        ArrayList<Location> response = parseXml(xmlResponse);

        return response.get(0);
    }

    private ArrayList<Location> parseXml(String xml) {
        ArrayList<Location> location = new ArrayList<Location>();

        // TODO Limit z.b. 10
        try {
            XMLStreamReader xmlReader = XMLInputFactory.newInstance().createXMLStreamReader(
                    new StringReader(xml));

            while (xmlReader.hasNext()) {
                int event = xmlReader.next();
                switch (event) {
                    case END_DOCUMENT:
                        xmlReader.close();
                        break;
                    case START_ELEMENT:
                        if (xmlReader.getLocalName().equals(
                                "searchresults")) {
                            readLocations(xmlReader, location);
                            xmlReader.require(END_ELEMENT, null,
                                    "searchresults");
                        } else if (xmlReader.getLocalName().equals(
                                "reversegeocode")) {
                            readLocations(xmlReader, location);
                            xmlReader.require(END_ELEMENT, null,
                                    "reversegeocode");
                        }
                    case END_ELEMENT:
                    case CHARACTERS:
                        break;

                    default:
                        break;
                }
            }
        } catch (XMLStreamException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FactoryConfigurationError e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return location;
    }

    private boolean readLocations(XMLStreamReader xmlReader, ArrayList<Location> location)
            throws XMLStreamException {
        while (xmlReader.hasNext()) {
            int event = xmlReader.next();
            switch (event) {
                case END_DOCUMENT:
                    return false;
                case START_ELEMENT:
                    if (xmlReader.getLocalName().equals(XML_ELEMENT_ENTRY)) {
                        readEntry(xmlReader, location);
                    } else if ( xmlReader.getLocalName().equals("result")) {
                        readEntryReverse(xmlReader, location);
                    }
                    break;
                case END_ELEMENT:
                    if (xmlReader.getLocalName().equals("searchresults")
                            || xmlReader.getLocalName().equals(
                                    "reversegeocode")) {
                        return true;
                    }
                default:
                    break;
            }
        }

        return true;
    }

    private boolean readEntry(XMLStreamReader xmlReader, ArrayList<Location> location) {

        Double longitude = Double.valueOf(xmlReader.getAttributeValue(null, "lon"));
        Double latitude = Double.valueOf(xmlReader.getAttributeValue(null, "lat"));
        String details = xmlReader.getAttributeValue(null, "display_name");
        String[] name = details.split(",");

        GeoCoordinates point = new GeoCoordinates(longitude, latitude);

        // TODO LocationType für Suchergebnisse?!
        Location current = new Location(point, LocationType.SEARCH, details, name[0]);

        location.add(current);

        return true;

    }
    
    private boolean readEntryReverse(XMLStreamReader xmlReader, ArrayList<Location> location) throws XMLStreamException {

        Double longitude = Double.valueOf(xmlReader.getAttributeValue(null, "lon"));
        Double latitude = Double.valueOf(xmlReader.getAttributeValue(null, "lat"));
        String details = xmlReader.getElementText();
        String[] name = details.split(",");

        GeoCoordinates point = new GeoCoordinates(longitude, latitude);

        // TODO LocationType für Suchergebnisse?!
        Location current = new Location(point, LocationType.SEARCH, details, name[0]);

        location.add(current);

        return true;

    }

    @Override
    public void dispose() {

    }

    public static void main(String[] args) {
        NominatimSource source = new NominatimSource();

        NominatimQuery query = new NominatimQuery(NominatimQuery.Type.LOCAL);
        query.area = new Tile() {

            @Override
            public boolean intersects(double lonFrom, double latFrom, double lonTo, double latTo) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public double getLongitudeTo() {
                return 14;
            }

            @Override
            public double getLongitudeFrom() {
                return 13;
            }

            @Override
            public double getLatitudeTo() {
                // TODO Auto-generated method stub
                return 49;
            }

            @Override
            public double getLatitudeFrom() {
                // TODO Auto-generated method stub
                return 48;
            }

            @Override
            public boolean contains(GeoCoordinates coords) {
                // TODO Auto-generated method stub
                return false;
            }
        };
        query.query = "Passau";

        for (Location i : source.getLocations(query)) {
            System.out.println(i.details);
        }
    }
}
