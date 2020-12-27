package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.utils.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest extends AcceptanceTest {

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

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = 지하철노선_생성_요청("2호선", "green");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine_duplicated() {
        // given
        지하철노선_생성_요청("2호선","green");

        // when
        ExtractableResponse<Response> response = 지하철노선_생성_요청("2호선", "green");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
	    ExtractableResponse<Response> createResponse1 = 지하철노선_생성_요청("2호선", "green");
	    ExtractableResponse<Response> createResponse2 = 지하철노선_생성_요청("3호선", "orange");

        // when
        ExtractableResponse<Response> response = 지하철노선_얻기();

        // then
	    지하철노선목록_조회_검사(response, createResponse1, createResponse2);
    }

	private ExtractableResponse<Response> 지하철노선_얻기() {
		return RestAssured
				.given().log().all()
				.when().get("/lines")
				.then().log().all().extract();
	}

	private void 지하철노선목록_조회_검사(ExtractableResponse<Response> getResponse,
	                           ExtractableResponse<Response>... createResponse) {
		assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
		List<Long> expectedStationIds = Arrays.stream(createResponse)
				.map(response -> response.header("Location").split("/")[2])
				.map(Long::parseLong)
				.collect(Collectors.toList());
		List<Long> actualStationIds = getResponse.jsonPath().getList(".", LineResponse.class).stream()
				.map(LineResponse::getId)
				.collect(Collectors.toList());
		assertThat(expectedStationIds).containsAll(actualStationIds);
	}

	@DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
	    ExtractableResponse<Response> createResponse = 지하철노선_생성_요청("2호선", "green");

        // when
        ExtractableResponse<Response> response = 지하철노선_조회(createResponse);

        // then
        지하철노선_조회_검사(createResponse, response);
    }

	private ExtractableResponse<Response> 지하철노선_조회(ExtractableResponse<Response> createResponse) {
    	String uri = createResponse.header("Location");
		return RestAssured
				.given().log().all()
				.when().get(uri)
				.then().log().all().extract();
	}

	private void 지하철노선_조회_검사(ExtractableResponse<Response> createResponse, ExtractableResponse<Response> getResponse) {
		assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
		Long expectedId = createResponse.body().as(LineResponse.class).getId();
		Long actualId = getResponse.body().as(LineResponse.class).getId();
		assertThat(actualId).isEqualTo(expectedId);
	}

	@DisplayName("존재하지 않는 지하철 노선을 조회한다.")
	@Test
	void getLine_notFound() {
    	// when
		ExtractableResponse<Response> response = 지하철노선_조회("lines/1");

		// then
		지하철노선_조회_없음(response);
	}

	private ExtractableResponse<Response> 지하철노선_조회(String uri) {
		return RestAssured
				.given().log().all()
				.when().get(uri)
				.then().log().all().extract();
	}

	private void 지하철노선_조회_없음(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
		ExtractableResponse<Response> createResponse = 지하철노선_생성_요청("2호선", "green");

        // when
        ExtractableResponse<Response> updateResponse = 지하철노선_수정_요청(createResponse, "3호선", "orange");

        // then
	    지하철노선_수정_검사(updateResponse);
    }

	private ExtractableResponse<Response> 지하철노선_수정_요청(ExtractableResponse<Response> createResponse,
	                                                  String name, String color) {
		String uri = createResponse.header("Location");

		Map<String, String> params = new HashMap<>();
		params.put("name", name);
		params.put("color", color);
		return RestAssured.given().log().all()
				.body(params)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.when().put(uri)
				.then().log().all().extract();
	}

	private void 지하철노선_수정_검사(ExtractableResponse<Response> updateResponse) {
		assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	@DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
	    ExtractableResponse<Response> createResponse = 지하철노선_생성_요청("2호선", "green");

        // when
	    ExtractableResponse<Response> deleteResponse = 지하철노선_삭제_요청(createResponse);

        // then
        지하철노선_삭제_검사(deleteResponse);
    }

	private ExtractableResponse<Response> 지하철노선_삭제_요청(ExtractableResponse<Response> createResponse) {
		String uri = createResponse.header("Location");
		return RestAssured.given().log().all()
				.when().delete(uri)
				.then().log().all().extract();
	}

	private void 지하철노선_삭제_검사(ExtractableResponse<Response> deleteResponse) {
		assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}

	private ExtractableResponse<Response> 지하철노선_생성_요청(String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        return RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all().extract();
    }
}
