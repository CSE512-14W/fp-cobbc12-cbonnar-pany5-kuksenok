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
        DeviationDataSources all = new DeviationDataSources();
        all.oba.add("2011-09-16");
        all.oba.add("2011-09-17");
        all.oba.add("2011-09-18");
        
        all.pushDeviations();
    }
}

class DeviationDataSources {

    ArrayList<String> oba = new ArrayList<>();

    public void pushDeviations() throws SQLException, FileNotFoundException, IOException {
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/bus2?user=root&password=");
        PreparedStatement writeObaTuple = conn.prepareCall("INSERT INTO deviation SET lat=?, lon=?, deviation=?, hh=?, mm=?, oba_day=?");
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
                    Date date = new Date(time);
                    Calendar calendar = GregorianCalendar.getInstance();
                    calendar.setTime(date);
                    writeObaTuple.setDouble(1, lat);
                    writeObaTuple.setDouble(2, lon);
                    writeObaTuple.setInt(3, deviation);
                    writeObaTuple.setInt(4, calendar.get(Calendar.HOUR_OF_DAY));
                    writeObaTuple.setInt(5, calendar.get(Calendar.MINUTE));
                    writeObaTuple.setString(6, oba_file);
                    writeObaTuple.executeUpdate();
                } catch (Exception e) {
                    //e.printStackTrace();;
                }
            }
            fr.close();
        }
    }
}
