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
import java.util.HashMap;
import java.util.HashSet;

public class Beautifier {

    public static void main(String[] args) throws SQLException, IOException {
        String[] routes = new String[]{"31", "32", "16", "26", "28", "40"};
        String route_where = "";
        for(String rt : routes) {
            if(!route_where.equals("")) route_where+="\",\"";
            route_where+=rt;
        }
        route_where = "(\"" + route_where + "\")";
        
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/bus2?user=root&password=");
        Schedule.init(conn);
        
        PreparedStatement ps = conn.prepareStatement("SELECT route_short_name, trip_id, service, trips.gtfs_data FROM trips, route WHERE route.gtfs_data IN (\"2012_03_24\", \"2011_12\") AND trips.route_id=route.route_id AND trips.gtfs_data=route.gtfs_data AND route_short_name IN " + route_where);
        
        ResultSet rs = ps.executeQuery();
        
        HashMap<String, HashSet<Integer>> tripFilter = new HashMap<>();
        HashMap<Integer, TripInfo> tripData = new HashMap<>();
        
        if(rs.first()) {
            do {
                String route_short_name = rs.getString(1);
                int trip_id = rs.getInt(2);
                String service = rs.getString(3);
                String gtfs_data = rs.getString(4);
                HashSet<Integer> list = tripFilter.get(gtfs_data);
                if (list == null) {
                    list = new HashSet<>();
                    tripFilter.put(gtfs_data, list);
                }
                list.add(trip_id);
                tripData.put(trip_id, new TripInfo(route_short_name, service, trip_id, gtfs_data));
            } while(rs.next());
        }
        
        for (String gtfs_data : tripFilter.keySet()) {
            System.out.println (gtfs_data+": "+tripFilter.get(gtfs_data).size());
            Schedule.pushFromGtfsFile("../DATA/" +gtfs_data+" stop_times.txt", tripFilter.get(gtfs_data), tripData);            
        }
        
    }
    
}
class TripInfo {
    String short_name;
    String service;
    int trip_id;
    String gtfs_data;
    public TripInfo(String short_name, String service, int trip_id, String gtfs_data) {
        this.short_name = short_name;
        this.service = service;
        this.trip_id = trip_id;
        this.gtfs_data = gtfs_data;
    }
}

class Schedule {
    int arrival_hr;
    int arrival_min;
    int stop_id;
    TripInfo ri;
    
    static PreparedStatement write = null;

    public static void init(Connection conn) throws SQLException {
        write = conn.prepareStatement("insert into schedule set short_name=?, gtfs_data=?, arrival_hr=?, arrival_min=?, service=?, trip_id=?, stop_id=?");
    }

    public int write() throws SQLException {
        write.setString(1, ri.short_name);
        write.setString(2, ri.gtfs_data);
        write.setInt(3, arrival_hr);
        write.setInt(4, arrival_min);
        write.setString(5, ri.service);
        write.setInt(6, ri.trip_id);
        write.setInt(7, stop_id);
        try {
            return write.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static int pushFromGtfsFile(String path, HashSet<Integer> trip_ids, HashMap<Integer, TripInfo> tripInfo) throws FileNotFoundException, IOException, SQLException {
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
            int trip_id = Integer.parseInt(parts[0]);
            if (trip_ids.contains(trip_id)) {
                Schedule t = new Schedule();
                String[] time = parts[3].split(":");
                t.stop_id = Integer.parseInt(parts[2]);
                t.arrival_hr = Integer.parseInt(time[0]);
                t.arrival_min = Integer.parseInt(time[1]);
                t.ri = tripInfo.get(trip_id);
              result += t.write();
            }
           // t.stop_id = Integer.parseInt(parts[2]);

        }
        fr.close();
        return result;
    }

}
