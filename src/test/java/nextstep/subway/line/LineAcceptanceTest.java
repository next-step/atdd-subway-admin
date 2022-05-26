package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.domain.Line;

@DisplayName("노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {

	@LocalServerPort
	int port;

	@BeforeEach
	public void setUp() {
		if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
			RestAssured.port = port;
		}
	}

	/**
     * When 노선을 생성하면
     * Then 노선이 생성된다
     * Then 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        // when
        Map<String, String> params = new HashMap<>();
        params.put("name", "1호선");
        params.put("color", "파랑색");

        ExtractableResponse<Response> createResponse =
                RestAssured
                	.given()
                		.log()
                		.all()
                        .body(params)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    	.post("/lines")
                    .then()
                    	.log()
                    	.all()
					.extract();
        
		// then
		assertAll(() -> assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
				() -> assertNotNull(createResponse.jsonPath().getObject(".", Line.class).getId()),
				() -> assertEquals(createResponse.jsonPath().getObject(".", Line.class).getName(), "1호선"),
				() -> assertEquals(createResponse.jsonPath().getObject(".", Line.class).getColor(), "파랑색"));
    }   
}
