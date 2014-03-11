package load_data;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author kukse_000
 */
public class Deviation {

    public static void main(String[] args) throws SQLException, IOException {
        DeviationDataSources pre = new DeviationDataSources();
        pre.gtfs = "2011_12";
        pre.oba.add("2011-12-16");
        pre.oba.add("2011-12-17");
        pre.oba.add("2011-12-18");
        pre.oba.add("2011-12-21");
        DeviationDataSources post = new DeviationDataSources();
        post.gtfs = "2012_03_24";
        post.oba.add("2012-03-02");
        post.oba.add("2012-03-03");
        post.oba.add("2012-03-04");
        post.oba.add("2012-03-07");
        pre.pushDeviations();
        post.pushDeviations();
    }
}

class DeviationDataSources {

    String gtfs;
    ArrayList<String> oba = new ArrayList<>();

    public void pushDeviations() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/bus2?user=root&password=");
        PreparedStatement ps = conn.prepareStatement("SELECT trip_id, lat, lon FROM schedule, stops WHERE AND trips.route_id=route.route_id AND trips.gtfs_data=route.gtfs_data AND route.gtfs_data=?");
        ps.setString(1, gtfs);

    }
}
