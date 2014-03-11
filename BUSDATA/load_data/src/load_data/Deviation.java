package load_data;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 *
 * @author kukse_000
 */
public class Deviation {

    public static void main(String[] args) throws SQLException, IOException {
        DeviationDataSources pre = new DeviationDataSources();
        pre.gtfs = "2011_12";
        pre.oba.add("2011-12-16");
     //   pre.oba.add("2011-12-17");
      //  pre.oba.add("2011-12-18");
     //   pre.oba.add("2011-12-21");
        DeviationDataSources post = new DeviationDataSources();
        post.gtfs = "2012_03_24";
        post.oba.add("2012-03-02");
        post.oba.add("2012-03-03");
        post.oba.add("2012-03-04");
       // post.oba.add("2012-03-07");
        pre.pushDeviations();
       //  post.pushDeviations();
    }
}

class DeviationDataSources {

    String gtfs;
    ArrayList<String> oba = new ArrayList<>();

    public void pushDeviations() throws SQLException, FileNotFoundException, IOException {
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/bus2?user=root&password=");
        PreparedStatement ps = conn.prepareStatement("SELECT trip_id, lat, lon FROM schedule, stops WHERE AND trips.route_id=route.route_id AND trips.gtfs_data=route.gtfs_data AND route.gtfs_data=?");
        ps.setString(1, gtfs);

        PreparedStatement writeObaTuple = conn.prepareCall("INSERT INTO deviation SET lat=?, lon=?, deviation=?, time=?, hh=?, mm=?, oba_day=?, trip_id=?, vehicle_id=?");
        for (String oba_file : oba) {
            FileReader fr = new FileReader("../DATA/" + oba_file + " oba.txt");
            BufferedReader tr = new BufferedReader(fr);
            String line = tr.readLine();
            /*
             Header row:
             locationLat	locationLon	scheduleDeviation	time	trip_id	vehicle_agencyId	vehicle_id
             */
            while ((line = tr.readLine()) != null) {
                String[] parts = line.split("\t");
                try {
                    double lat = Double.parseDouble(parts[0]);
                    double lon = Double.parseDouble(parts[1]);
                    int deviation = Integer.parseInt(parts[2]);
                    long time = Long.parseLong(parts[3]);
                    int trip_id = -1;
                    int vehicle_id = -1;
                    try {
                        trip_id = Integer.parseInt(parts[4]);
                        vehicle_id = Integer.parseInt(parts[6]);
                    } catch (Exception e) {
                    }
                    Date date = new Date(time);
                    Calendar calendar = GregorianCalendar.getInstance();
                    calendar.setTime(date);
                    writeObaTuple.setDouble(1, lat);
                    writeObaTuple.setDouble(2, lon);
                    writeObaTuple.setInt(3, deviation);
                    writeObaTuple.setTimestamp(4, new Timestamp(time));
                    writeObaTuple.setInt(5, calendar.get(Calendar.HOUR_OF_DAY));
                    writeObaTuple.setInt(6, calendar.get(Calendar.MINUTE));
                    writeObaTuple.setString(7, oba_file);
                    writeObaTuple.setInt(8, trip_id);
                    writeObaTuple.setInt(9, vehicle_id);
                    writeObaTuple.executeUpdate();
                } catch (Exception e) {
                    //e.printStackTrace();;
                }
            }
            fr.close();
        }
    }
}
