package load_data;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Load_data {

    public static void main(String[] args) throws SQLException, IOException {
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/bus2?user=root&password=");
        Stop.init(conn);
        Route.init(conn);
        Trip.init(conn);
        Schedule.init(conn);

        String[] gtfs_dates = new String[]{"2011_11", "2011_12", "2012_03_01", "2012_03_24", "2012_04_11"};
        for (String gtfs_date : gtfs_dates) {
            System.out.println("Added stops for " + gtfs_date + " : " + Stop.pushFromGtfsFile("../DATA/" + gtfs_date + " stops.txt", gtfs_date));
            System.out.println("Added routes for " + gtfs_date + " : " + Route.pushFromGtfsFile("../DATA/" + gtfs_date + " routes.txt", gtfs_date));
            System.out.println("Added trips for " + gtfs_date + " : " + Trip.pushFromGtfsFile("../DATA/" + gtfs_date + " trips.txt", gtfs_date));
            System.out.println("Added hopeful schedule for " + gtfs_date + " : " + Trip.pushFromGtfsFile("../DATA/" + gtfs_date + " stop-times.txt", gtfs_date));

        }

        String[] oba_dates = new String[]{"2011-12-16", "2011-12-17", "2011-12-18", "2011-12-21", "2012-03-02", "2012-03-03", "2012-03-04", "2012-03-07"};
        for (String oba_date : oba_dates) {
            // 
        }
    }

    /**
     * Count the amount of tuples in a file- ignoring header row!
     *
     * @param path
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static int count(String path) throws FileNotFoundException, IOException {
        FileReader fr = new FileReader(path);
        BufferedReader tr = new BufferedReader(fr);
        String line = tr.readLine();
        int result = 0;
        while ((line = tr.readLine()) != null) {
            result++;
        }
        fr.close();
        return result;
    }

}
class Schedule {

    int stop_id;
    int trip_id;
    int arrival_hr;
    int arrival_min;
    String gtfs_data;
    
    static PreparedStatement write = null;
    static PreparedStatement count = null;

    public static void init(Connection conn) throws SQLException {
        write = conn.prepareStatement("insert into schedule set trip_id=?, stop_id=?, arrival_hr=?, arrival_min=?, gtfs_data=?");
        count = conn.prepareStatement("select count(*) from schedule where gtfs_data=?");
    }

    public static int count(String gtfs_data) throws SQLException {
        count.setString(1, gtfs_data);
        ResultSet rs = count.executeQuery();
        if (rs.first()) {
            return rs.getInt(1);
        }
        return 0;
    }

    public int write() throws SQLException {
        write.setInt(1, trip_id);
        write.setInt(2, stop_id);
        write.setInt(3, arrival_hr);
        write.setInt(4, arrival_min);
        write.setString(5, gtfs_data);
        try {
            return write.executeUpdate();
        } catch (Exception e) {
            return 0;
        }
    }

    public static int pushFromGtfsFile(String path, String gtfs_data) throws FileNotFoundException, IOException, SQLException {
        if (Trip.count(gtfs_data) == Load_data.count(path)) {
            return 0;
        }
        FileReader fr = new FileReader(path);
        BufferedReader tr = new BufferedReader(fr);
        String line = tr.readLine();
        /*
         Header row:
         trip_id,stop_sequence,stop_id,arrival_time,departure_time,stop_headsign,route_short_name,pickup_type,drop_off_type,shape_dist_traveled
         */
        int result = 0;
        while ((line = tr.readLine()) != null) {
            String[] parts = line.split(",");
            Schedule t = new Schedule();
            t.trip_id = Integer.parseInt(parts[0]);
            t.stop_id = Integer.parseInt(parts[1]);
            String[] time = parts[3].split(":");
            t.arrival_hr = Integer.parseInt(time[0]);
            t.arrival_min = Integer.parseInt(time[1]);
            t.gtfs_data = gtfs_data;
            result += t.write();
        }
        fr.close();
        return result;
    }

}

class Trip {

    int trip_id;
    int route_id;
    String service;
    String gtfs_data;
    static PreparedStatement write = null;
    static PreparedStatement count = null;

    public static void init(Connection conn) throws SQLException {
        write = conn.prepareStatement("insert into trips set route_id=?, trip_id=?, gtfs_data=?, service=?");
        count = conn.prepareStatement("select count(*) from trips where gtfs_data=?");
    }

    public static int count(String gtfs_data) throws SQLException {
        count.setString(1, gtfs_data);
        ResultSet rs = count.executeQuery();
        if (rs.first()) {
            return rs.getInt(1);
        }
        return 0;
    }

    public int write() throws SQLException {
        write.setInt(1, route_id);
        write.setInt(2, trip_id);
        write.setString(3, gtfs_data);
        write.setString(4, service);
        try {
            return write.executeUpdate();
        } catch (Exception e) {
            return 0;
        }
    }

    public static int pushFromGtfsFile(String path, String gtfs_data) throws FileNotFoundException, IOException, SQLException {
        if (Trip.count(gtfs_data) == Load_data.count(path)) {
            return 0;
        }
        FileReader fr = new FileReader(path);
        BufferedReader tr = new BufferedReader(fr);
        String line = tr.readLine();
        /*
         Header row:
         route_id,service_id,trip_short_name,trip_headsign,route_short_name,direction_id,block_id,shape_id,wheelchair_accessible,trip_bikes_allowed,trip_id
         */
        int result = 0;
        while ((line = tr.readLine()) != null) {
            String[] parts = line.split(",");
            Trip t = new Trip();
            t.route_id = Integer.parseInt(parts[0]);
            t.trip_id = Integer.parseInt(parts[parts.length - 1]);
            t.service = parts[1];
            t.gtfs_data = gtfs_data;
            result += t.write();
        }
        fr.close();
        return result;
    }
}

class Route {

    int route_id;
    String route_short_name;
    String gtfs_data;
    static PreparedStatement write = null;
    static PreparedStatement count = null;

    public static void init(Connection conn) throws SQLException {
        write = conn.prepareStatement("insert into route set route_id=?, route_short_name=?, gtfs_data=?");
        count = conn.prepareStatement("select count(*) from route where gtfs_data=?");
    }

    public static int count(String gtfs_data) throws SQLException {
        count.setString(1, gtfs_data);
        ResultSet rs = count.executeQuery();
        if (rs.first()) {
            return rs.getInt(1);
        }
        return 0;
    }

    public int write() throws SQLException {
        write.setInt(1, route_id);
        write.setString(2, route_short_name);
        write.setString(3, gtfs_data);
        try {
            return write.executeUpdate();
        } catch (Exception e) {
            return 0;
        }
    }

    public static int pushFromGtfsFile(String path, String gtfs_data) throws FileNotFoundException, IOException, SQLException {
        if (Route.count(gtfs_data) == Load_data.count(path)) {
            return 0;
        }
        FileReader fr = new FileReader(path);
        BufferedReader tr = new BufferedReader(fr);
        String line = tr.readLine();
        /*
         Header row:
         agency_id,route_short_name,route_long_name,route_desc,route_type,route_url,route_color,route_text_color,route_bikes_allowed,route_id
         */
        int result = 0;
        while ((line = tr.readLine()) != null) {
            String[] parts = line.split(",");
            Route r = new Route();
            r.route_id = Integer.parseInt(parts[parts.length - 1]);
            r.route_short_name = parts[1];
            r.gtfs_data = gtfs_data;
            result += r.write();
        }
        fr.close();
        return result;
    }
}

class Stop {

    int stop_id;
    double lat;
    double lon;
    String gtfs_data;
    static PreparedStatement write = null;
    static PreparedStatement count = null;

    public static void init(Connection conn) throws SQLException {
        write = conn.prepareStatement("insert into stops set stop_id=?, lat=?, lon=?, gtfs_data=?");
        count = conn.prepareStatement("select count(*) from stops where gtfs_data=?");
    }

    public static int count(String gtfs_data) throws SQLException {
        count.setString(1, gtfs_data);
        ResultSet rs = count.executeQuery();
        if (rs.first()) {
            return rs.getInt(1);
        }
        return 0;
    }

    public int write() throws SQLException {
        write.setInt(1, stop_id);
        write.setDouble(2, lat);
        write.setDouble(3, lon);
        write.setString(4, gtfs_data);
        try {
            return write.executeUpdate();
        } catch (Exception e) {
            return 0;
        }
    }

    public static int pushFromGtfsFile(String path, String gtfs_data) throws FileNotFoundException, IOException, SQLException {
        if (Stop.count(gtfs_data) == Load_data.count(path)) {
            return 0;
        }
        FileReader fr = new FileReader(path);
        BufferedReader tr = new BufferedReader(fr);
        String line = tr.readLine();
        /*
         Header row:
         stop_id,stop_code,stop_name,stop_desc,stop_lat,stop_lon,zone_id,stop_url,location_type,parent_station,wheelchair_boarding,stop_direction
         */
        int result = 0;
        while ((line = tr.readLine()) != null) {
            String[] parts = line.split(",");
            Stop s = new Stop();
            s.lat = Double.parseDouble(parts[parts.length - 7]);
            s.lon = Double.parseDouble(parts[parts.length - 6]);
            s.gtfs_data = gtfs_data;
            s.stop_id = Integer.parseInt(parts[0]);
            result += s.write();
        }
        fr.close();
        return result;
    }
}


class Deviation {

    int stop_id;
    int trip_id;
    int deviation;
    long time;
}
