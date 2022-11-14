package nextstep.subway.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DatabaseCleanUpUtils implements InitializingBean {

	@Autowired
	DataSource dataSource;

	private final List<String> tableNames = new ArrayList<>();

	@Override
	public void afterPropertiesSet() {
		DatabaseMetaData metaData;
		try {
			metaData = dataSource.getConnection().getMetaData();
			String[] types = {"TABLE"};
			ResultSet resultSet = metaData.getTables(null, null, "%", types);
			while (resultSet.next()) {
				tableNames.add(resultSet.getString(3));
			}
		} catch (SQLException e) {
			throw new RuntimeException("SQL EXCEPTION while cleanup" + e);
		}
	}

	@Transactional
	public void cleanUp() {
		try {
			Connection connection = dataSource.getConnection();
			connection.createStatement().execute("SET REFERENTIAL_INTEGRITY FALSE");
			for (String tableName : tableNames) {
				connection.createStatement().executeUpdate("TRUNCATE TABLE " + tableName);
				connection.createStatement().execute("ALTER TABLE " + tableName + " ALTER COLUMN ID RESTART WITH 1");
			}
			connection.createStatement().execute("SET REFERENTIAL_INTEGRITY TRUE");
		} catch (SQLException e) {
			throw new RuntimeException("SQL EXCEPTION while cleanup" + e);
		}
	}
}
