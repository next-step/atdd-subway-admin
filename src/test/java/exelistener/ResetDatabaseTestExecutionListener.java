package exelistener;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

/**
 * 테스트에 사용될 H2 데이터베이스를 Reset하기 위한 TestExecutionListener
 * @see <pre>https://stackoverflow.com/questions/34617152/how-to-re-create-database-before-each-test-in-spring</pre>
 */
public class ResetDatabaseTestExecutionListener extends AbstractTestExecutionListener {

    @Autowired
    DataSource dataSource;

    /**
     * 테스트 시작전 dataSource를 주입한다.
     */
    @Override
    public void beforeTestClass(TestContext testContext) {
        testContext.getApplicationContext()
                .getAutowireCapableBeanFactory()
                .autowireBean(this);
    }

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        cleanupDatabase();
    }

    private void cleanupDatabase() throws SQLException {
        Connection c = dataSource.getConnection();
        Statement s = c.createStatement();

        // Disable FK
        s.execute("SET REFERENTIAL_INTEGRITY FALSE");

        // Find all tables and truncate them
        Set<String> tables = new HashSet<>();
        ResultSet rs = s.executeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES  where TABLE_SCHEMA='PUBLIC'");
        while (rs.next()) {
            tables.add(rs.getString(1));
        }
        rs.close();
        for (String table : tables) {
            s.executeUpdate("TRUNCATE TABLE " + table);
        }

        // Idem for sequences
        Set<String> sequences = new HashSet<>();
        rs = s.executeQuery("SELECT SEQUENCE_NAME FROM INFORMATION_SCHEMA.SEQUENCES WHERE SEQUENCE_SCHEMA='PUBLIC'");
        while (rs.next()) {
            sequences.add(rs.getString(1));
        }
        rs.close();
        for (String seq : sequences) {
            s.executeUpdate("ALTER SEQUENCE " + seq + " RESTART WITH 1");
        }

        // Enable FK
        s.execute("SET REFERENTIAL_INTEGRITY TRUE");
        s.close();
        c.close();
    }
}
