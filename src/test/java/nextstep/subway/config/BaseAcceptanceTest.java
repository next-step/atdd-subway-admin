package nextstep.subway.config;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;

import io.restassured.RestAssured;
import nextstep.subway.util.DatabaseCleanUpUtils;

@AcceptanceTest
public class BaseAcceptanceTest {

	@LocalServerPort
	int port;

	@Autowired
	protected DatabaseCleanUpUtils cleanUpUtils;

	@BeforeEach
	protected void setUp() {
		if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
			RestAssured.port = port;
		}
		cleanUpUtils.cleanUp();
	}

}
