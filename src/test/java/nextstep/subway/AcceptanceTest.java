package nextstep.subway;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import io.restassured.RestAssured;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AcceptanceTest {
	@LocalServerPort
	private int port;

	@Autowired
	private DataBaseCleanUp dataBaseCleanUp;

	@BeforeEach
	public void setUp() throws SQLException {
		if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
			RestAssured.port = port;
		}
		dataBaseCleanUp.cleanUp();
	}
}
