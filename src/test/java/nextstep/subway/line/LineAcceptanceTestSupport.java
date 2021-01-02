package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineUpdateRequest;
import nextstep.subway.line.dto.LineCreateRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
class LineAcceptanceTestSupport {
	LineAcceptanceTestSupport() {
	}

	static void 지하철노선_프로퍼티_검사(ExtractableResponse<Response> createResponse,
	                          String name, String color,
	                          List<Long> orderedIds) {
		final LineResponse createdObject = createResponse.body().as(LineResponse.class);
		assertThat(createdObject.getName()).isEqualTo(name);
		assertThat(createdObject.getColor()).isEqualTo(color);
		assertThat(createdObject.getStations()).hasSize(orderedIds.size());
		assertThat(createdObject.getStations())
				.extracting(StationResponse::getId)
				.containsExactlyElementsOf(orderedIds)
				.containsSequence(orderedIds);
	}

	static ExtractableResponse<Response> 지하철노선_얻기() {
		return RestAssured
				.given().log().all()
				.when().get("/lines")
				.then().log().all().extract();
	}

	static void 지하철노선목록_조회_검사(ExtractableResponse<Response> getResponse,
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

	static ExtractableResponse<Response> 지하철노선_조회(ExtractableResponse<Response> createResponse) {
		String uri = createResponse.header("Location");
		return RestAssured
				.given().log().all()
				.when().get(uri)
				.then().log().all().extract();
	}

	static void 지하철노선_조회_검사(ExtractableResponse<Response> createResponse, ExtractableResponse<Response> getResponse) {
		// given
		final LineResponse createdObject = createResponse.body().as(LineResponse.class);
		final LineResponse getObject = getResponse.body().as(LineResponse.class);

		// when & then
		assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
		assertThat(getObject.getId()).isEqualTo(createdObject.getId());
		final List<Long> actualStationIds = getObject.getStations().stream()
				.map(StationResponse::getId)
				.collect(Collectors.toList());
		final List<Long> expectedStationIds = createdObject.getStations().stream()
				.map(StationResponse::getId)
				.collect(Collectors.toList());
		assertThat(actualStationIds).containsAll(expectedStationIds)
				.containsSequence(expectedStationIds);
	}

	static ExtractableResponse<Response> 지하철노선_수정_요청(ExtractableResponse<Response> createResponse,
	                                                 String name, String color) {
		String uri = createResponse.header("Location");

		LineUpdateRequest lineUpdateRequest = new LineUpdateRequest(name, color);
		return RestAssured.given().log().all()
				.body(lineUpdateRequest)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.when().put(uri)
				.then().log().all().extract();
	}

	static ExtractableResponse<Response> 지하철노선_삭제_요청(ExtractableResponse<Response> createResponse) {
		String uri = createResponse.header("Location");
		return RestAssured.given().log().all()
				.when().delete(uri)
				.then().log().all().extract();
	}

	static ExtractableResponse<Response> 지하철노선_생성_요청(String name, String color,
	                                                 Long firstStationId, Long secondStationId, int stationDistance) {
		LineCreateRequest lineRequest = new LineCreateRequest(name, color, firstStationId, secondStationId, stationDistance);

		return RestAssured
				.given().log().all()
				.body(lineRequest)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.when().post("/lines")
				.then().log().all().extract();
	}

	static void assertStatusCode(ExtractableResponse<Response> response, HttpStatus httpStatus) {
		assertThat(response.statusCode()).isEqualTo(httpStatus.value());
	}
}
