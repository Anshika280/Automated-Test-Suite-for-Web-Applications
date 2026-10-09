package tests.db;

import framework.db.DatabaseUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

/**
 * Database Validation Test Suite verifying DDL execution, DML updates,
 * data seeding, scalar queries, and record assertions.
 */
public class DatabaseValidationTest {

    @BeforeClass
    public void setupDatabaseSchema() {
        // Create test table
        String createTableSql = "CREATE TABLE test_users (" +
                "id INT PRIMARY KEY, " +
                "username VARCHAR(50) NOT NULL, " +
                "email VARCHAR(100) NOT NULL, " +
                "role VARCHAR(20) NOT NULL, " +
                "active BOOLEAN DEFAULT TRUE" +
                ");";
        DatabaseUtils.executeScript(createTableSql);

        // Seed initial data
        DatabaseUtils.executeUpdate(
                "INSERT INTO test_users (id, username, email, role, active) VALUES (?, ?, ?, ?, ?)",
                101, "standard_user", "user1@ats.com", "USER", true
        );
        DatabaseUtils.executeUpdate(
                "INSERT INTO test_users (id, username, email, role, active) VALUES (?, ?, ?, ?, ?)",
                102, "admin_user", "admin@ats.com", "ADMIN", true
        );
    }

    @Test(priority = 1)
    public void testDatabaseConnectionAndSchemaCreation() {
        Object count = DatabaseUtils.executeScalar("SELECT COUNT(*) FROM test_users");
        Assert.assertNotNull(count, "Count query should not return null");
        Assert.assertEquals(((Number) count).intValue(), 2, "Database should initially contain 2 seeded records");
    }

    @Test(priority = 2)
    public void testExecuteSelectQuery() {
        List<Map<String, Object>> results = DatabaseUtils.executeQuery(
                "SELECT * FROM test_users WHERE username = ?", "standard_user"
        );

        Assert.assertEquals(results.size(), 1, "Should return exactly one user record");
        Map<String, Object> user = results.get(0);
        Assert.assertEquals(user.get("USERNAME"), "standard_user");
        Assert.assertEquals(user.get("EMAIL"), "user1@ats.com");
        Assert.assertEquals(user.get("ROLE"), "USER");
        Assert.assertEquals(user.get("ACTIVE"), true);
    }

    @Test(priority = 3)
    public void testExecuteUpdateAndRecordModification() {
        // Insert new record
        int inserted = DatabaseUtils.executeUpdate(
                "INSERT INTO test_users (id, username, email, role, active) VALUES (?, ?, ?, ?, ?)",
                103, "qa_tester", "tester@ats.com", "TESTER", false
        );
        Assert.assertEquals(inserted, 1, "One row should be inserted");

        // Update active status
        int updated = DatabaseUtils.executeUpdate(
                "UPDATE test_users SET active = ? WHERE id = ?",
                true, 103
        );
        Assert.assertEquals(updated, 1, "One row should be updated");

        // Assert update using scalar
        Object activeStatus = DatabaseUtils.executeScalar(
                "SELECT active FROM test_users WHERE id = ?", 103
        );
        Assert.assertEquals(activeStatus, true, "User active status should be updated to true");
    }

    @Test(priority = 4)
    public void testDataIntegrityAndCleanup() {
        int deleted = DatabaseUtils.executeUpdate("DELETE FROM test_users WHERE id = ?", 103);
        Assert.assertEquals(deleted, 1, "One row should be deleted");

        Object count = DatabaseUtils.executeScalar("SELECT COUNT(*) FROM test_users");
        Assert.assertEquals(((Number) count).intValue(), 2, "Record count should be back to 2");
    }

    @AfterClass
    public void teardownDatabaseSchema() {
        DatabaseUtils.executeScript("DROP TABLE IF EXISTS test_users;");
    }
}
