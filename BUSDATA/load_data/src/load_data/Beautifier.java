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
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author kukse_000
 */
public class Beautifier {

    public static void main(String[] args) throws SQLException {
        String[] routes = new String[]{"8", "31", "32", "10", "40", "44", "16", "26", "28", "2", "43", "48"};
        String route_where = "";
        for(String rt : routes) {
            if(!route_where.equals("")) route_where+="\",\"";
            route_where+=rt;
        }
        route_where = "(\"" + route_where + "\")";
        
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/bus2?user=root&password=");
        PreparedStatement ps = conn.prepareStatement("SELECT route_short_name, trip_id, service, trips.gtfs_data FROM trips, route WHERE trips.route_id=route.route_id AND trips.gtfs_data=route.gtfs_data AND route_short_name IN " + route_where);
        
        ResultSet rs = ps.executeQuery();
        
        HashMap<String, ArrayList<Integer>> tripFilter = new HashMap<>();
        HashMap<Integer, String[]> tripData = new HashMap<>();
        
        if(rs.first()) {
            do {
                String route_short_name = rs.getString(1);
                int trip_id = rs.getInt(2);
                String service = rs.getString(3);
                String gtfs_data = rs.getString(4);
                ArrayList<Integer> list = tripFilter.get(gtfs_data);
                if (list == null) {
                    list = new ArrayList<>();
                    tripFilter.put(gtfs_data, list);
                }
                list.add(trip_id);
                tripData.put(trip_id, new String[]{route_short_name, service});
                System.out.println(route_short_name+","+trip_id+","+service+","+gtfs_data);
            } while(rs.next());
        }
        
        
        String[] oba_dates = new String[]{"2011-12-16", "2011-12-17", "2011-12-18", "2011-12-21", "2012-03-02", "2012-03-03", "2012-03-04", "2012-03-07"};
        for (String oba_date : oba_dates) {
            // 
        }
    }
    
}
class Schedule {

    int stop_id;
    int trip_id;
    int arrival_hr;
    int arrival_min;
    
    static PreparedStatement write = null;

    public static void init(Connection conn) throws SQLException {
        write = conn.prepareStatement("insert into schedule set trip_id=?, stop_id=?, arrival_hr=?, arrival_min=?");
    }

    public int write() throws SQLException {
        write.setInt(1, trip_id);
        write.setInt(2, stop_id);
        write.setInt(3, arrival_hr);
        write.setInt(4, arrival_min);
        try {
            return write.executeUpdate();
        } catch (Exception e) {
            return 0;
        }
    }

    public static int pushFromGtfsFile(String path, String gtfs_data) throws FileNotFoundException, IOException, SQLException {
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
            t.stop_id = Integer.parseInt(parts[2]);
            String[] time = parts[3].split(":");
            t.arrival_hr = Integer.parseInt(time[0]);
            t.arrival_min = Integer.parseInt(time[1]);
            result += t.write();
        }
        fr.close();
        return result;
    }

}
