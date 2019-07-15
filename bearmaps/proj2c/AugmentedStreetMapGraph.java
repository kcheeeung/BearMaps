package bearmaps.proj2c;

import bearmaps.hw4.streetmap.Node;
import bearmaps.hw4.streetmap.StreetMapGraph;
import bearmaps.proj2ab.KDTree;
import bearmaps.proj2ab.Point;

import java.util.*;

/**
 * An augmented graph that is more powerful that a standard StreetMapGraph.
 * Specifically, it supports the following additional operations:
 * @source priceton library
 * @author Alan Yao, Josh Hug, Kevin Cheung
 */
public class AugmentedStreetMapGraph extends StreetMapGraph {
    public static final int TOTAL_SIZE = 27500;
    public static final int HASH_SIZE = 37000;
    public static final int NAMES_SIZE = 2600;

    private List<Point> streetIntersection; // contains only nodes w intersections
    private HashMap<Point, Long> map; // hash Point to ID
    private HashMap<String, HashSet<String>> cleanToFull;  // hash cleaned name to hashset of full names
    private HashMap<String, HashSet<Node>> cleanLocations; // hash cleaned name to a list of nodes/locations
    private KDTree ktree; // kdtree of coords. closest point -> use hashmap to get ID
    private MyTrieSet trieSet; // trieset of all the locations in berkeley

    public AugmentedStreetMapGraph(String dbPath) {
        super(dbPath);
        List<Node> nodes = this.getNodes();
        streetIntersection = new ArrayList<>(TOTAL_SIZE);
        map = new HashMap<>(HASH_SIZE);
        cleanToFull = new HashMap<>(NAMES_SIZE);
        cleanLocations = new HashMap<>(NAMES_SIZE);
        trieSet = new MyTrieSet();

        for (Node n : nodes) {
            Long nodeid = n.id();
            Point current = new Point(n.lon(), n.lat());
            map.put(current, nodeid);
            if (neighbors(nodeid).size() > 0) {
                streetIntersection.add(current); // drivable locations only
            }

            String fullName = n.name();
            if (fullName != null) {
                String cleanedName = cleanString(fullName);

                trieSet.add(cleanedName);

                if (!cleanToFull.containsKey(cleanedName)) {
                    HashSet<String> full = new HashSet<>();
                    full.add(fullName);
                    cleanToFull.put(cleanedName, full);
                } else {
                    HashSet<String> full = cleanToFull.get(cleanedName);
                    full.add(fullName);
                }

                if (!cleanLocations.containsKey(cleanedName)) {
                    HashSet<Node> loc = new HashSet<>();
                    loc.add(n);
                    cleanLocations.put(cleanedName, loc);
                } else {
                    HashSet<Node> loc = cleanLocations.get(cleanedName);
                    loc.add(n);
                }
            }
        }
        ktree = new KDTree(streetIntersection); // kdtree points

        // System.out.println("Number of total nodes: " + nodes.size());
        // System.out.println("Number of intersections: " + streetIntersection.size());
        // System.out.println("idtoname size: " + idtoname.size());
        // System.out.println("map size " + map.size());
        // System.out.println("cleantofull size " + cleanToFull.size());
        // System.out.println("cleanloc size " + cleanLocations.size());
        // might add the "places"
    }

    /**
     * For Project Part II
     * Returns the vertex closest to the given longitude and latitude.
     * @param lon The target longitude.
     * @param lat The target latitude.
     * @return The id of the node in the graph closest to the target.
     */
    public long closest(double lon, double lat) {
        return map.get(ktree.nearest(lon, lat));
    }

    /**
     * For Project Part III (gold points)
     * In linear time, collect all the names of OSM locations that prefix-match the query string.
     * @param prefix Prefix string to be searched for. Could be any case, with our without
     *               punctuation.
     * @return A <code>List</code> of the full names of locations whose cleaned name matches the
     * cleaned <code>prefix</code>.
     */
    public List<String> getLocationsByPrefix(String prefix) {
        List<String> found = new LinkedList<>();        
        List<String> keys = trieSet.keysWithPrefix(cleanString(prefix));
        // List of all keys matching prefix
        for (String k : keys) {
            HashSet<String> full = cleanToFull.get(k);
            if (full != null) {
                for (String f : full) {
                    found.add(f);
                }
            }
        }
        return found;
    }

    /**
     * For Project Part III (gold points)
     * Collect all locations that match a cleaned <code>locationName</code>, and return
     * information about each node that matches.
     * @param locationName A full name of a location searched for.
     * @return A list of locations whose cleaned name matches the
     * cleaned <code>locationName</code>, and each location is a map of parameters for the Json
     * response as specified: <br>
     * "lat" -> Number, The latitude of the node. <br>
     * "lon" -> Number, The longitude of the node. <br>
     * "name" -> String, The actual name of the node. <br>
     * "id" -> Number, The id of the node. <br>
     */
    public List<Map<String, Object>> getLocations(String locationName) {
        HashSet<Node> nodes = cleanLocations.get(cleanString(locationName));
        ArrayList<Map<String, Object>> loc = new ArrayList<>(nodes.size());
        for (Node n : nodes) {
            Map<String, Object> l = new HashMap<>(6);
            l.put("lat", n.lat());
            l.put("lon", n.lon());
            l.put("name", n.name());
            l.put("id", n.id());
            loc.add(l);
        }
        return loc;
    }

    /**
     * Useful for Part III. Do not modify.
     * Helper to process strings into their "cleaned" form, ignoring punctuation and capitalization.
     * @param s Input string.
     * @return Cleaned string.
     */
    private static String cleanString(String s) {
        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }
}
