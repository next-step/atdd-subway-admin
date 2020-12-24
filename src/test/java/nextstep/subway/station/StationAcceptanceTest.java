package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.utils.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StationAcceptanceTest extends AcceptanceTest {

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @LocalServerPort
    private int port;

    @Override
    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        databaseCleanup.execute();
    }

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
    	// when
        ExtractableResponse<Response> response = 지하철역_생성_요청("강남역");

        // then
	    지하철역_생성_성공(response);
    }

	private void 지하철역_생성_성공(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(response.header("Location")).isNotBlank();
	}

	@DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        지하철역_생성_요청("강남역");

        // when
        ExtractableResponse<Response> response = 지하철역_생성_요청("강남역");

        // then
	    지하철역_생성_실패됨(response);
    }

	private void 지하철역_생성_실패됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        ExtractableResponse<Response> createResponse1 = 지하철역_생성_요청("강남역");
        ExtractableResponse<Response> createResponse2 = 지하철역_생성_요청("역삼역");

        // when
        ExtractableResponse<Response> response = 지하철역_얻기();

        // then
		지하철역_조회_검사(createResponse1, createResponse2, response);
	}

	private ExtractableResponse<Response> 지하철역_얻기() {
		return RestAssured.given().log().all()
		        .when()
		        .get("/stations")
		        .then().log().all()
		        .extract();
	}

	private void 지하철역_조회_검사(ExtractableResponse<Response> createResponse1, ExtractableResponse<Response> createResponse2, ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
				.map(it -> Long.parseLong(it.header("Location").split("/")[2]))
				.collect(Collectors.toList());
		List<Long> resultLineIds = response.jsonPath().getList(".", StationResponse.class).stream()
				.map(StationResponse::getId)
				.collect(Collectors.toList());
		assertThat(resultLineIds).containsAll(expectedLineIds);
	}

	@DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> createResponse = 지하철역_생성_요청("강남역");

        // when
		ExtractableResponse<Response> response = 지하철역_삭제_요청(createResponse);

		// then
		지하철역_삭제_성공(response);
	}

	private void 지하철역_삭제_성공(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}

	private ExtractableResponse<Response> 지하철역_삭제_요청(ExtractableResponse<Response> createResponse) {
		String uri = createResponse.header("Location");
		return RestAssured.given().log().all()
		        .when()
		        .delete(uri)
		        .then().log().all()
		        .extract();
	}

	public ExtractableResponse<Response> 지하철역_생성_요청(String station) {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", station);

        // when
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }
}
