package server;

import general.User;
import general.route.Coordinates;
import general.route.Route;
import general.route.location.first.Location;

import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DatabaseWorker {
    private static Connection connection;
    private static Statement statement;

    static {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("database.txt"));
            String url = bufferedReader.readLine();
            String user = bufferedReader.readLine();
            String password = bufferedReader.readLine();
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();
        } catch (IOException | SQLException exc) {
            GlobalLogger.logger.error("Невозможно подключиться к базе данных. Без нее серверу капут.");
            System.exit(-1);
        }
    }

    private DatabaseWorker() {}

    public static List<Route> getAllRoutes() throws SQLException {
        ResultSet resultSet = statement.executeQuery("SELECT * FROM postgres.public.routes");

        return getRoutes(resultSet);
    }

    private static List<Route> getRoutes(ResultSet resultSet) throws SQLException {
        List<Route> data = new ArrayList<>();
        while (resultSet.next()) {
            data.add(new Route(
                    resultSet.getString("name"), resultSet.getDate("creationDate"),
                    new Coordinates(resultSet.getDouble("c_x"), resultSet.getInt("c_y")),
                    new Location(resultSet.getDouble("from_x"),
                            resultSet.getLong("from_y"), resultSet.getLong("from_z"),
                            resultSet.getString("from_name")),
                    new general.route.location.second.Location(resultSet.getInt("to_x"),
                            resultSet.getInt("to_y"), resultSet.getLong("to_z")),
                    resultSet.getDouble("distance"), resultSet.getInt("id"),
                    resultSet.getString("owner")
            ));
        }
        return  data;
    }

    public static boolean addNewRoute(Route route, User user) {
        try {
            statement.executeUpdate("INSERT INTO postgres.public.routes values((select nextval('serial')), '" +
                    route.getName() + "', " + route.getCoordinates().getX() + ", " + route.getCoordinates().getY() + ", '" +
                    new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(route.getCreationDate()) + "', " +
                    route.getFrom().getX() + ", " + route.getFrom().getY() + ", " + route.getFrom().getZ() + ", '" +
                    route.getFrom().getName() + "', " + route.getTo().getX() + ", " + route.getTo().getY() + ", " +
                    route.getTo().getZ() + ", " + route.getDistance() + ", '" + user.getUser() + "');");
            return true;
        } catch (SQLException sqlException) {
            GlobalLogger.logger.warn(sqlException.getMessage());
            return false;
        }
    }

    public static boolean updateById(Route route, Integer id, User user) {
        try {
            statement.executeUpdate("UPDATE postgres.public.routes SET name='" + route.getName() +"', c_x=" +
                    route.getCoordinates().getX() + ", c_y=" + route.getCoordinates().getY() + ", creationdate='" +
                    new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(route.getCreationDate()) + "', from_x=" +
                    route.getFrom().getX() + ", from_y=" + route.getFrom().getY() + ", from_z=" + route.getFrom().getZ() +
                    ", from_name='" + route.getFrom().getName() +"', to_x=" + route.getTo().getX() + ", to_y=" +
                    route.getTo().getY() + ", to_z=" + route.getTo().getZ() + ", distance=" + route.getDistance() +
                    " WHERE '" + Account.hash256(user.getPassword()) + "' IN (SELECT postgres.public.userdata.password " +
                    "FROM postgres.public.userdata WHERE owner='" +
                    user.getUser() + "') AND id=" + id + " AND owner='" + user.getUser() +"';");
            if (statement.getUpdateCount() != 0)
                return true;
            else
                return false;
        } catch (SQLException sqlException) {
            GlobalLogger.logger.warn(sqlException.getMessage());
            return false;
        }
    }

    public static boolean removeById(Integer id, User user) {
        try {
            statement.executeUpdate("DELETE FROM postgres.public.routes WHERE id=" + id + " AND '" +
                    Account.hash256(user.getPassword()) + "' IN (SELECT postgres.public.userdata.password " +
                    "FROM postgres.public.userdata where owner='" + user.getUser() + "') AND owner='" + user.getUser() + "';");
            return true;
        } catch (SQLException sqlException) {
            GlobalLogger.logger.warn(sqlException.getMessage());
            return false;
        }
    }

    public static boolean clear(User user) {
        try {
            statement.executeUpdate("DELETE FROM postgres.public.routes WHERE '" +
                    Account.hash256(user.getPassword()) + "' IN (SELECT postgres.public.userdata.password " +
                    "FROM postgres.public.userdata where owner='" + user.getUser() + "') AND owner='" + user.getUser() + "';");
            return true;
        } catch (SQLException sqlException) {
            GlobalLogger.logger.warn(sqlException.getMessage());
            return false;
        }
    }
}
