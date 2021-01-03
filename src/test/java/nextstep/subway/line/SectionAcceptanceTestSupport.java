package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.AddSectionRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTestSupport;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionAcceptanceTestSupport {

	static long 지하철역_생성_요청(String name) {
		return StationAcceptanceTestSupport.지하철역_생성_요청(name).body().as(StationResponse.class).getId();
	}

	static long 지하철노선_생성_요청(String name, String color,
	                        Long firstStationId, Long secondStationId, int stationDistance) {
		return LineAcceptanceTestSupport.지하철노선_생성_요청(name, color, firstStationId, secondStationId, stationDistance)
				.body().as(LineResponse.class).getId();
	}

	static ExtractableResponse<Response> 지하철노선_구간_추가(long lineId, long upStationId, long downStationId, int distance) {
		AddSectionRequest addSectionRequest = new AddSectionRequest(upStationId, downStationId, distance);
		return RestAssured
				.given().log().all()
				.body(addSectionRequest)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.when().post(String.format("/lines/%d/sections", lineId))
				.then().log().all().extract();
	}

	static void 지하철노선_구간_검사(long lineId, Long... sequenceStationIds) {
		LineResponse lineResponse = RestAssured
				.given().log().all()
				.when().get(String.format("/lines/%d", lineId))
				.then().log().all().extract()
				.body().as(LineResponse.class);
		List<Long> actualStationIds = lineResponse.getStations().stream()
				.map(StationResponse::getId)
				.collect(Collectors.toList());
		assertThat(actualStationIds).containsExactly(sequenceStationIds);
	}

	static void assertStatusCode(ExtractableResponse<Response> response, HttpStatus httpStatus) {
		assertThat(response.statusCode()).isEqualTo(httpStatus.value());
	}

	static ExtractableResponse<Response> 지하철노선_구간_제거(long lineId, long stationId) {
		return RestAssured
				.given().log().all()
				.queryParam("stationId", stationId)
				.when().delete(String.format("/lines/%d/sections", lineId))
				.then().log().all().extract();
	}

	static void assertMessageContains(ExtractableResponse<Response> response, String message) {
		assertThat(response.jsonPath().getString("message")).contains(message);
	}
}
