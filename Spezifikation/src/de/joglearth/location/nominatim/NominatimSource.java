package de.joglearth.location.nominatim;

import static javax.xml.stream.XMLStreamConstants.CHARACTERS;
import static javax.xml.stream.XMLStreamConstants.END_DOCUMENT;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import de.joglearth.geometry.GeoCoordinates;
import de.joglearth.location.Location;
import de.joglearth.location.LocationType;
import de.joglearth.settings.Settings;
import de.joglearth.settings.SettingsContract;
import de.joglearth.source.ProgressManager;
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
        executor = Executors.newFixedThreadPool(1);
    }

    @Override
    public SourceResponse<Collection<Location>> requestObject(final NominatimQuery key,
            final SourceListener<NominatimQuery, Collection<Location>> sender) {
        if (!(key instanceof NominatimQuery)) {
            return new SourceResponse<Collection<Location>>(SourceResponseType.MISSING, null);
        }
        
        ProgressManager.getInstance().requestArrived();

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
                
                ProgressManager.getInstance().requestCompleted();
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
            double left = Math.toDegrees(query.area.getLongitudeFrom());
            double right = Math.toDegrees(query.area.getLongitudeTo());
            double top = Math.toDegrees(query.area.getLatitudeFrom());
            double bottom = Math.toDegrees(query.area.getLatitudeTo());
            getRequest.add(left + "," + top + "," + right + "," + bottom);
        }

        // TODO weiteres, wie bounding box
        getRequest.add("q");
        getRequest.add(query.query);

        byte[] httpRes = HTTP.get(url, getRequest);
        
        if(httpRes == null) {
            return new ArrayList<Location>();
        }
        
        String xmlResponse;
        try {
            xmlResponse = new String(httpRes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            xmlResponse = "";
            e.printStackTrace();
        }
        ArrayList<Location> response = parseXml(xmlResponse, LocationType.SEARCH, query);

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
        getRequest.add("" + Math.toDegrees(query.point.getLatitude()));
        getRequest.add("lon");
        getRequest.add("" + Math.toDegrees(query.point.getLongitude()));
        
        byte[] httpRes = HTTP.get(url, getRequest);

        if (httpRes == null) {
            return new ArrayList<Location>();
        }

        String xmlResponse;
        try {
            xmlResponse = new String(httpRes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            xmlResponse = "";
            e.printStackTrace();
        }

        ArrayList<Location> response = parseXml(xmlResponse, LocationType.SEARCH, query);

        return response;
    }

    /**
     * 
     * Searches for an adress and geo coordinates by a OSM reference. More Information about the
     * reference ID and the reference type you can find in the OSM-Wiki
     * http://wiki.openstreetmap.org/wiki/Nominatim.
     * 
     * @param osmId OSM reference ID of the element
     * @param osmType Specifies the kind of element.
     * @param type {@link LocationType} of the element
     * @return A {@link Location}
     */
    public Location reverseSearch(String osmId, String osmType, LocationType type) {


        String url = "http://nominatim.openstreetmap.org/reverse";
        // Get-Request: "?accept-language=de&format=xml&osm_id=...&osm_type=[N|W|R]";

        ArrayList<String> getRequest = new ArrayList<String>();
        getRequest.add("accept-language");
        getRequest.add(Settings.getInstance().getString(SettingsContract.LANGUAGE).toLowerCase());
//        getRequest.add("de");
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

        ArrayList<Location> response = parseXml(xmlResponse, type, null);
        
        if(response.isEmpty()) {
            return null;
        }
        
        return response.get(0);
    }

    private ArrayList<Location> parseXml(String xml, LocationType type, NominatimQuery query) {
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
                            readLocations(xmlReader, location, type, query);
                            xmlReader.require(END_ELEMENT, null,
                                    "searchresults");
                        } else if (xmlReader.getLocalName().equals(
                                "reversegeocode")) {
                            readLocations(xmlReader, location, type, query);
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
            return location;
        } catch (FactoryConfigurationError e) {
            return location;
        }

        return location;
    }

    private boolean readLocations(XMLStreamReader xmlReader, ArrayList<Location> location,
            LocationType type, NominatimQuery query)
            throws XMLStreamException {
        while (xmlReader.hasNext()) {
            int event = xmlReader.next();
            switch (event) {
                case END_DOCUMENT:
                    return false;
                case START_ELEMENT:
                    if (xmlReader.getLocalName().equals(XML_ELEMENT_ENTRY)) {
                        readEntry(xmlReader, location, type, query);
                    } else if (xmlReader.getLocalName().equals("result")) {
                        readEntryReverse(xmlReader, location, type);
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

    private boolean readEntry(XMLStreamReader xmlReader, ArrayList<Location> location,
            LocationType type, NominatimQuery query) {

        Double longitude = Double.valueOf(xmlReader.getAttributeValue(null, "lon"));
        Double latitude = Double.valueOf(xmlReader.getAttributeValue(null, "lat"));
        String details = xmlReader.getAttributeValue(null, "display_name");
        String[] name = details.split(",");

        GeoCoordinates point = new GeoCoordinates(Math.toRadians(longitude), Math.toRadians(latitude));
        
        if(query!=null && query.type == NominatimQuery.Type.LOCAL) {
            if(!(query.area.contains(point))) {
                return false;
            }
        }

        Location current = new Location(point, type, details, name[0]);

        location.add(current);

        return true;

    }

    private boolean readEntryReverse(XMLStreamReader xmlReader, ArrayList<Location> location,
            LocationType type) throws XMLStreamException {

        Double longitude = Double.valueOf(xmlReader.getAttributeValue(null, "lon"));
        Double latitude = Double.valueOf(xmlReader.getAttributeValue(null, "lat"));
        String details = xmlReader.getElementText();
        String[] name = details.split(",");
        String id = name[0];
        
        if(name[0].matches("[0-9]+[a-z]?") && name.length > 1) {
            id = name[1];
        }

        GeoCoordinates point = new GeoCoordinates(Math.toRadians(longitude), Math.toRadians(latitude));

        Location current = new Location(point, type, details, id);

        location.add(current);

        return true;

    }

    @Override
    public void dispose() {
        executor.shutdown();
    }
}
