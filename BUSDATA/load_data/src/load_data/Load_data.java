package load_data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Load_data {

    public static void main(String[] args) throws SQLException {
        String[] gtfs_dates = new String[]{"2011_11", "2011_12", "2012_03_01", "2012_03_24", "2012_04_11"};
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/bus?user=root&password=");
        Trip.init(conn);
        Stop.init(conn);
        for (String gtfs_date : gtfs_dates) {

        }
    }

}

class Stop {

    int stop_id;
    double lat;
    double lon;
    String gtfs_data;
}

class Schedule {

    int stop_id;
    double lat;
    double lon;
    String gtfs_data;
}

class Trip {

    int trip_id;
    int route_id;
}

class Route {

    int route_id;
    String route_short_name;
    String route_headsign;
    String gtfs_date;
}

class Deviation {
    int stop_id;
    int trip_id;
    int deviation;
    long time;
}
