package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.station.dto.StationResponse;
import org.assertj.core.api.Assertions;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("NonAsciiCharacters")
public class StationAcceptanceTestSupport {
	public StationAcceptanceTestSupport() {
	}

	static void 지하철역_생성_성공(ExtractableResponse<Response> response) {
		Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		Assertions.assertThat(response.header("Location")).isNotBlank();
	}

	static void 지하철역_생성_실패됨(ExtractableResponse<Response> response) {
		Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	static ExtractableResponse<Response> 지하철역_얻기() {
		return RestAssured.given().log().all()
				.when()
				.get("/stations")
				.then().log().all()
				.extract();
	}

	static void 지하철역_조회_검사(ExtractableResponse<Response> createResponse1, ExtractableResponse<Response> createResponse2, ExtractableResponse<Response> response) {
		Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
				.map(it -> Long.parseLong(it.header("Location").split("/")[2]))
				.collect(Collectors.toList());
		List<Long> resultLineIds = response.jsonPath().getList(".", StationResponse.class).stream()
				.map(StationResponse::getId)
				.collect(Collectors.toList());
		Assertions.assertThat(resultLineIds).containsAll(expectedLineIds);
	}

	static void 지하철역_삭제_성공(ExtractableResponse<Response> response) {
		Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}

	static ExtractableResponse<Response> 지하철역_삭제_요청(ExtractableResponse<Response> createResponse) {
		String uri = createResponse.header("Location");
		return RestAssured.given().log().all()
				.when()
				.delete(uri)
				.then().log().all()
				.extract();
	}

	public static ExtractableResponse<Response> 지하철역_생성_요청(String station) {
		// given
		Map<String, String> params = new HashMap<String, String>();
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
