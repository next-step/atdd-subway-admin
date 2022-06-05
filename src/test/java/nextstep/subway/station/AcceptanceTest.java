package nextstep.subway.station;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@DisplayName("ATDD 테스트 공통")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AcceptanceTest {
	@LocalServerPort
	int port;

	@BeforeEach
	public void setUp() {
		if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
			RestAssured.port = port;
		}
	}

	protected void 요청_응답_확인(ExtractableResponse<Response> response, HttpStatus status) {
		assertThat(response.statusCode()).isEqualTo(status.value());
	}
}
