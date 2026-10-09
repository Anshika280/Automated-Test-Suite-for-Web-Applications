package framework.db;

import framework.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

/**
 * Thread-safe Database Utility for executing DDL, DML, and SQL Queries
 * during automated database validation tests.
 */
public class DatabaseUtils {

    private static final Logger log = LoggerFactory.getLogger(DatabaseUtils.class);

    private DatabaseUtils() {
        // Private constructor for utility class
    }

    static {
        try {
            String driver = ConfigManager.get("db.driver", "org.h2.Driver");
            Class.forName(driver);
            log.info("Registered Database Driver: {}", driver);
        } catch (ClassNotFoundException e) {
            log.error("Failed to load database driver class", e);
            throw new RuntimeException("Database driver class not found", e);
        }
    }

    /**
     * Obtains a new JDBC Connection using configured database credentials.
     *
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        String url = ConfigManager.get("db.url", "jdbc:h2:mem:ats_db;DB_CLOSE_DELAY=-1");
        String username = ConfigManager.get("db.username", "sa");
        String password = ConfigManager.get("db.password", "");
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Executes an INSERT, UPDATE, or DELETE query with optional parameters.
     *
     * @param sql    SQL Query string
     * @param params Parameter arguments
     * @return Number of rows affected
     */
    public static int executeUpdate(String sql, Object... params) {
        log.debug("Executing DB Update: {}", sql);
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setParameters(pstmt, params);
            int rowsAffected = pstmt.executeUpdate();
            log.debug("DB Update completed. Rows affected: {}", rowsAffected);
            return rowsAffected;

        } catch (SQLException e) {
            log.error("Error executing DB Update: {}", sql, e);
            throw new RuntimeException("Database update execution failed", e);
        }
    }

    /**
     * Executes a SELECT query with optional parameters and returns results as a list of maps.
     *
     * @param sql    SQL Query string
     * @param params Parameter arguments
     * @return List of column-name to value maps representing each row
     */
    public static List<Map<String, Object>> executeQuery(String sql, Object... params) {
        log.debug("Executing DB Query: {}", sql);
        List<Map<String, Object>> results = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setParameters(pstmt, params);
            try (ResultSet rs = pstmt.executeQuery()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnLabel(i);
                        Object columnValue = rs.getObject(i);
                        row.put(columnName.toUpperCase(), columnValue);
                    }
                    results.add(row);
                }
            }
            log.debug("DB Query fetched {} row(s)", results.size());
            return results;

        } catch (SQLException e) {
            log.error("Error executing DB Query: {}", sql, e);
            throw new RuntimeException("Database query execution failed", e);
        }
    }

    /**
     * Executes a SELECT query expecting a single scalar result (e.g. COUNT(*)).
     *
     * @param sql    SQL Query string
     * @param params Parameter arguments
     * @return Single object result or null
     */
    public static Object executeScalar(String sql, Object... params) {
        List<Map<String, Object>> results = executeQuery(sql, params);
        if (!results.isEmpty()) {
            Map<String, Object> firstRow = results.get(0);
            if (!firstRow.isEmpty()) {
                return firstRow.values().iterator().next();
            }
        }
        return null;
    }

    /**
     * Executes DDL script (such as table creation, schema initialization).
     *
     * @param sql DDL statements separated by semicolons
     */
    public static void executeScript(String sql) {
        log.info("Executing DB Schema/DDL Script");
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            log.info("Successfully executed DB DDL Script");
        } catch (SQLException e) {
            log.error("Error executing DB script", e);
            throw new RuntimeException("Failed to execute database DDL script", e);
        }
    }

    /**
     * Binds parameters to a PreparedStatement safely.
     */
    private static void setParameters(PreparedStatement pstmt, Object... params) throws SQLException {
        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
        }
    }
}
