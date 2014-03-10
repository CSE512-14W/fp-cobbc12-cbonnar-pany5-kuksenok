package dbproc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dbproc {

    public static void main(String[] args) throws IOException, SQLException {
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/bus?user=root&password=");
        Trip.init(conn);
        Stop.init(conn);

       String[] folders = new String[]{"../clean data/march 2012", "../clean data/december 2011"};
       // String[] folders = new String[]{"../clean data/june-2012"};
        for (String folder : folders) {
            Map<String, Trip> trips = getRouteInfo(folder);
            List<String> datafiles = getDataFiles(folder);
            System.out.println("Read " + trips.size() + " trips. Going to read " + datafiles.size() + " datafiles");
            for (String file : datafiles) {
                int[] reads = pushStops(folder + "/" + file, trips);
                System.out.println(reads[1] + " successes, " + reads[0] + " failures and " + reads[2] + " blank locations");
            }
        }
    }

    public static List<String> getDataFiles(String path) {
        ArrayList<String> list = new ArrayList<>();
        File folder = new File(path);
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory() || fileEntry.getName().equals("routes.txt") || fileEntry.getName().equals("trips.txt")) {
                continue;
            } else {
                list.add(fileEntry.getName());
            }
        }
        return list;
    }

    public static int[] pushStops(String path, Map<String, Trip> trips) throws FileNotFoundException, IOException {
        FileReader fr = new FileReader(path);
        BufferedReader tr = new BufferedReader(fr);
        int[] read = new int[3];
        String line = tr.readLine(); // header row: locationLat	locationLon	scheduleDeviation	time	trip_id	vehicle_agencyId	vehicle_id
        while ((line = tr.readLine()) != null) {
            String[] parts = line.split("\t");
           
            Stop s = new Stop();
            try {
                s.lat = (parts[0].equals("NULL")) ? 0 : Double.parseDouble(parts[0]);
                s.lon = (parts[1].equals("NULL")) ? 0 : Double.parseDouble(parts[1]);
                if (s.lat == 0 || s.lon == 0) {
                    read[2]++;
                }
                s.deviation = (parts[0].equals("NULL")) ? 0 : Integer.parseInt(parts[2]);
                s.time = Long.parseLong(parts[3]);
                Trip t = trips.get(parts[4]);
                if (t == null || t.fk_trip_id == -1) {
                    read[0]++;
                    continue;
                }
                s.fk_trip_id = t.fk_trip_id;
                s.vehicle_id = Integer.parseInt(parts[6]);
                try {
                    if (s.write() == 0) {
                        System.out.println("write fail : " + line);
                        read[0]++;
                    } else {
                        read[1]++;
                    }
                } catch (SQLException e) {
                    System.out.println("sql fail : " + line);
                    read[0]++;
                }
            } catch (NumberFormatException e) {
                read[0]++;
            }
        }
        fr.close();
        return read;
    }

    /**
     * @return gtfs_trip_id->Trip
     */
    public static HashMap<String, Trip> getRouteInfo(String folder) throws FileNotFoundException, IOException {
        HashMap<String, Trip> trips = new HashMap<>();
        FileReader fr = new FileReader(folder + "/routes.txt");
        BufferedReader tr = new BufferedReader(fr);
        String line = tr.readLine(); // header row: route_short_name	route_id
        while ((line = tr.readLine()) != null) {
            String[] parts = line.split("\t");
            Trip t = new Trip();
            t.short_name = parts[0];
            t.route_id = parts[1];
            trips.put(t.route_id, t);
        }
        fr.close();
        fr = new FileReader(folder + "/trips.txt");
        tr = new BufferedReader(fr);
        line = tr.readLine(); // route_id	trip_headsign	trip_id
        while ((line = tr.readLine()) != null) {
            String[] parts = line.split("\t");
            try{
                Trip t = trips.get(parts[0]);
                t.headsign = parts[1];
                t.gtfs_trip_id = parts[2];
            } catch(NullPointerException npe){
                System.out.println(line);
                npe.printStackTrace();
            }
        }
        tr.close();
        HashMap<String, Trip> result = new HashMap<>();
        for (String i : trips.keySet()) {
            Trip t = trips.get(i);
            try {
                t.fk_trip_id = t.fetchFkTripId();
            } catch (SQLException e) {
                System.out.println("Unable to resolve " + t.gtfs_trip_id + " // "+t);
                
                e.printStackTrace();
            }
            result.put(t.gtfs_trip_id, t);
        }
        return result;
    }

}

class Trip {

    static PreparedStatement writer = null;
    static PreparedStatement reader = null;

    public static void init(Connection conn) throws SQLException {
        writer = conn.prepareStatement("insert into trip values (default, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        reader = conn.prepareStatement("select id from trip where gtfs_trip_id=?");
    }
    String gtfs_trip_id;
    String route_id;
    String headsign;
    String short_name;
    int fk_trip_id = -1;

    public int write() throws SQLException {
        writer.setString(1, route_id);
        writer.setString(2, headsign);
        writer.setString(3, short_name);
        writer.setString(4, gtfs_trip_id);
        writer.executeUpdate();
        ResultSet res = writer.getGeneratedKeys();
        while (res.next()) {
            return res.getInt(1);
        }
        return -1;
    }

    public int fetchFkTripId() throws SQLException {
        reader.setString(1, gtfs_trip_id);
        ResultSet res = reader.executeQuery();
        while (res.next()) {
            return res.getInt(1);
        }
        return write();
    }

    @Override
    public String toString() {
        return gtfs_trip_id + ";" + route_id + ";" + headsign + ";" + short_name + ";" + fk_trip_id;

    }

}

class Stop {

    static PreparedStatement ps = null;

    public static void init(Connection conn) throws SQLException {
        ps = conn.prepareStatement("insert into stop values (default, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
    }
    int fk_trip_id;
    int trip_id;
    int vehicle_id;
    Double lat, lon;
    int deviation;
    long time;

    public int write() throws SQLException {
        ps.setDouble(1, lat);
        ps.setDouble(2, lon);
        ps.setInt(3, deviation);
        ps.setTimestamp(4, new Timestamp(time));
        ps.setInt(5, vehicle_id);

        Date date = new Date(time);
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        ps.setString(6, calendar.get(Calendar.MONTH) + " " + calendar.get(Calendar.DAY_OF_MONTH) + " " + calendar.get(Calendar.YEAR));
        ps.setInt(7, calendar.get(Calendar.HOUR_OF_DAY));
        ps.setInt(8, calendar.get(Calendar.MINUTE));
        ps.setInt(9, fk_trip_id);
        return ps.executeUpdate();
    }
}

// new java.sql.Date(2009, 12, 11)
