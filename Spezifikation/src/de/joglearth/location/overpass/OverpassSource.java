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
import de.joglearth.location.Location;
import de.joglearth.location.LocationType;
import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.util.HTTP;


/**
 * Provides responses from the OverpassAPI, for e.g. detailed information to a POI or a place.
 */
public class OverpassSource implements Source<OverpassQuery, Collection<Location>> {

    /* Location = POI */
    private Map<LocationType, String> locationRequest;
    private final ExecutorService executor;
    private final String url = "http://overpass-api.de/api/interpreter";
    
    
    public OverpassSource(){
        executor = Executors.newFixedThreadPool(2);
        
        locationRequest = new HashMap<LocationType, String>();
        locationRequest.put(LocationType.RESTAURANT, OverpassQueryGenerator.restaurant);
        locationRequest.put(LocationType.ACTIVITY, OverpassQueryGenerator.activity);
        locationRequest.put(LocationType.BANK, OverpassQueryGenerator.bank);
        locationRequest.put(LocationType.EDUCATION,OverpassQueryGenerator.education);
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
        
        executor.execute(new Runnable() {
            
            @Override
            public void run() {
                // TODO Auto-generated method stub
                
            }
        });
        
        
        return null;
    }
    
    private Collection<Location> getLocations(OverpassQuery request) {
        
        String query = locationRequest.get(request.type);
        
        double north = request.area.getLatitudeFrom();
        double south = request.area.getLatitudeTo();
        double east = request.area.getLongitudeTo();
        double west = request.area.getLongitudeFrom();
        
        query.replace("$north$", north+"");
        query.replace("$south$", south+"");
        query.replace("$east$", east+"");
        query.replace("$west$", west+"");
        
        ArrayList<String> getRequest = new ArrayList<String>();
        getRequest.add("data");
        getRequest.add(query);
        
        byte[] response = HTTP.get(url, getRequest); 
       
        return parseXml(new String(response), request.type);
        
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FactoryConfigurationError e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return location;
        
    }
    
    public boolean readNodes(XMLStreamReader xmlReader, ArrayList<Location> location, LocationType type) throws XMLStreamException {
        while (xmlReader.hasNext()) {
            int event = xmlReader.next();
            switch (event) {
                case END_DOCUMENT:
                    return false;
                case START_ELEMENT:
                    if (xmlReader.getLocalName().equals("node")) {
                        readEntryNode(xmlReader, location, type);
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

    private boolean readEntryNode(XMLStreamReader xmlReader, ArrayList<Location> location, LocationType type) {

        Double longitude = Double.valueOf(xmlReader.getAttributeValue(null, "lon"));
        Double latitude = Double.valueOf(xmlReader.getAttributeValue(null, "lat"));
        String details = xmlReader.getAttributeValue(null, "description");
        String name = xmlReader.getAttributeValue(null, "name");

        GeoCoordinates point = new GeoCoordinates(longitude, latitude);

        // TODO LocationType für Suchergebnisse?!
        Location current = new Location(point, type, details, name);

        location.add(current);

        return true;

    }
    
    public boolean readWays(XMLStreamReader xmlReader, ArrayList<Location> location, LocationType type) throws XMLStreamException {
        while (xmlReader.hasNext()) {
            int event = xmlReader.next();
            switch (event) {
                case END_DOCUMENT:
                    return false;
                case START_ELEMENT:
                    if (xmlReader.getLocalName().equals("way")) {
                        readEntryWays(xmlReader, location, type);
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

    private boolean readEntryWays(XMLStreamReader xmlReader, ArrayList<Location> location, LocationType type) {

        Double longitude = Double.valueOf(xmlReader.getAttributeValue(null, "lon"));
        Double latitude = Double.valueOf(xmlReader.getAttributeValue(null, "lat"));
        String details = xmlReader.getAttributeValue(null, "description");
        String name = xmlReader.getAttributeValue(null, "name");

        GeoCoordinates point = new GeoCoordinates(longitude, latitude);

        // TODO LocationType für Suchergebnisse?!
        Location current = new Location(point, type, details, name);

        location.add(current);

        return true;

    }
    
    
    public boolean readRelations(XMLStreamReader xmlReader, ArrayList<Location> location, LocationType type) throws XMLStreamException {
        while (xmlReader.hasNext()) {
            int event = xmlReader.next();
            switch (event) {
                case END_DOCUMENT:
                    return false;
                case START_ELEMENT:
                    if (xmlReader.getLocalName().equals("relation")) {
                        readEntryRelations(xmlReader, location, type);
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

    private boolean readEntryRelations(XMLStreamReader xmlReader, ArrayList<Location> location, LocationType type) {

        Double longitude = Double.valueOf(xmlReader.getAttributeValue(null, "lon"));
        Double latitude = Double.valueOf(xmlReader.getAttributeValue(null, "lat"));
        String details = xmlReader.getAttributeValue(null, "description");
        String name = xmlReader.getAttributeValue(null, "name");

        GeoCoordinates point = new GeoCoordinates(longitude, latitude);

        // TODO LocationType für Suchergebnisse?!
        Location current = new Location(point, type, details, name);

        location.add(current);

        return true;

    }


    @Override
    public void dispose() {
        // TODO Automatisch generierter Methodenstub
        
    }
    
    
}
