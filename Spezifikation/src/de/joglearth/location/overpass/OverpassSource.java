package de.joglearth.location.overpass;

import static javax.xml.stream.XMLStreamConstants.CHARACTERS;
import static javax.xml.stream.XMLStreamConstants.END_DOCUMENT;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import de.joglearth.geometry.GeoCoordinates;
import de.joglearth.geometry.Tile;
import de.joglearth.location.Location;
import de.joglearth.location.LocationType;
import de.joglearth.location.nominatim.NominatimSource;
import de.joglearth.source.ProgressManager;
import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.SourceResponseType;
import de.joglearth.util.HTTP;


/**
 * Provides responses from the OverpassAPI, for e.g. detailed information to a POI or a place.
 */
public class OverpassSource implements Source<OverpassQuery, Collection<Location>> {

    private Map<LocationType, String> locationRequest;
    private final ExecutorService executor;
    private final String url = "http://overpass-api.de/api/interpreter";
    private NominatimSource info;


    /**
     * Constructor. Initializes the {@link OverpassSource}.
     */
    public OverpassSource() {
        executor = Executors.newFixedThreadPool(1);
        info = new NominatimSource();
        
        locationRequest = new HashMap<LocationType, String>();
        locationRequest.put(LocationType.RESTAURANT, OverpassQueryGenerator.restaurant);
        locationRequest.put(LocationType.ACTIVITY, OverpassQueryGenerator.activity);
        locationRequest.put(LocationType.BANK, OverpassQueryGenerator.bank);
        locationRequest.put(LocationType.EDUCATION, OverpassQueryGenerator.education);
        locationRequest.put(LocationType.GROCERY_SHOPS, OverpassQueryGenerator.grocery);
        locationRequest.put(LocationType.HEALTH, OverpassQueryGenerator.health);
        locationRequest.put(LocationType.HIKING_AND_CYCLING, OverpassQueryGenerator.outdoor);
        locationRequest.put(LocationType.HOTELS, OverpassQueryGenerator.hotels);
        locationRequest.put(LocationType.NIGHTLIFE, OverpassQueryGenerator.nightlife);
        locationRequest.put(LocationType.POST, OverpassQueryGenerator.post);
        locationRequest.put(LocationType.SHOPS, OverpassQueryGenerator.shops);
        locationRequest.put(LocationType.TOILETS, OverpassQueryGenerator.toilets);
    }

    @Override
    public SourceResponse<Collection<Location>> requestObject(final OverpassQuery key,
            final SourceListener<OverpassQuery, Collection<Location>> sender) {
        if (!(key instanceof OverpassQuery)) {
            return new SourceResponse<Collection<Location>>(SourceResponseType.MISSING, null);
        }

        if (!(locationRequest.containsKey(key.type)) || (key.area == null)) {
            return new SourceResponse<Collection<Location>>(SourceResponseType.MISSING, null);
        }

        ProgressManager.getInstance().requestArrived();

        executor.execute(new Runnable() {

            @Override
            public void run() {

                Collection<Location> response = getLocations(key);
                sender.requestCompleted(key, response);
                ProgressManager.getInstance().requestCompleted();
            }
        });

        return new SourceResponse<Collection<Location>>(SourceResponseType.ASYNCHRONOUS, null);
    }

    private Collection<Location> getLocations(OverpassQuery request) {

        String query = locationRequest.get(request.type);

        double north = Math.toDegrees(request.area.getLatitudeTo());
        double south = Math.toDegrees(request.area.getLatitudeFrom());
        double east = Math.toDegrees(request.area.getLongitudeTo());
        double west = Math.toDegrees(request.area.getLongitudeFrom());

        query = query.replace("$north$", north + "");
        query = query.replace("$south$", south + "");
        query = query.replace("$east$", east + "");
        query = query.replace("$west$", west + "");

        ArrayList<String> getRequest = new ArrayList<String>();
        getRequest.add("data");
        getRequest.add(query);

        System.out.println(query);

        byte[] response = HTTP.get(url, getRequest);
        if (response == null) {
            return new ArrayList<Location>();
        }
        String xml = new String(response);
        System.out.println(xml);

        return parseXml(xml, request.type);

    }

    private Collection<Location> parseXml(String xml, LocationType type) {
        ArrayList<Location> location = new ArrayList<Location>();

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
                                "osm")) {
                            readNodes(xmlReader, location, type);
                            xmlReader.require(END_ELEMENT, null,
                                    "osm");
                        }
                    case END_ELEMENT:
                    case CHARACTERS:
                        break;

                    default:
                        break;
                }
            }
        } catch (XMLStreamException e) {
            return new ArrayList<Location>();
        } catch (FactoryConfigurationError e) {
            return new ArrayList<Location>();
        }

        return location;

    }

    private boolean readNodes(XMLStreamReader xmlReader, ArrayList<Location> location,
            LocationType type) throws XMLStreamException {
        while (xmlReader.hasNext()) {
            int event = xmlReader.next();
            switch (event) {
                case END_DOCUMENT:
                    return false;
                case START_ELEMENT:
                    if (xmlReader.getLocalName().equals("node")) {
                        readEntry(xmlReader, location, type, "N");
                    } else if (xmlReader.getLocalName().equals("way")) {
                        readEntry(xmlReader, location, type, "W");
                    } else if (xmlReader.getLocalName().equals("relation")) {
                        readEntry(xmlReader, location, type, "R");
                    }
                    break;
                case END_ELEMENT:
                    if (xmlReader.getLocalName().equals("osm")) {
                        return true;
                    }
                default:
                    break;
            }
        }

        return true;
    }

    private boolean readEntry(XMLStreamReader xmlReader, ArrayList<Location> location,
            LocationType type, String osmType) {

        String osmId = xmlReader.getAttributeValue(null, "id");

        Location current = info.reverseSearch(osmId, osmType, type);

        if (current != null) {
            location.add(current);
        }

        return true;

    }

    @Override
    public void dispose() {
        info.dispose();
        executor.shutdown();

    }

    public static void main(String[] args) {
        OverpassSource source = new OverpassSource();
        source.getLocations(new OverpassQuery(LocationType.ACTIVITY, new Tile() {

            @Override
            public boolean intersects(double lonFrom, double latFrom, double lonTo, double latTo) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public double getLongitudeTo() {
                // TODO Auto-generated method stub
                return 14;
            }

            @Override
            public double getLongitudeFrom() {
                // TODO Auto-generated method stub
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
        }));

        source.dispose();

    }

}
