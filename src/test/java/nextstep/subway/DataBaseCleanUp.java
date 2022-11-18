package nextstep.subway;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataBaseCleanUp {

    @Autowired
    private DataSource dataSource;

    public void cleanUp() throws SQLException {
        Connection connection = dataSource.getConnection();

        connection.createStatement().execute("SET FOREIGN_KEY_CHECKS = 0");

        ResultSet tables = connection.getMetaData()
            .getTables(connection.getCatalog(), null, null, new String[]{"TABLE"});
        while (tables.next()) {
            connection.createStatement()
                .execute("TRUNCATE TABLE " + tables.getString("TABLE_NAME"));
        }

        connection.createStatement().execute("SET FOREIGN_KEY_CHECKS = 1");
        connection.close();
    }
}
