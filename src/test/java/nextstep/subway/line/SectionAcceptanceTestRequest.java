package nextstep.subway.line;

import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.SectionRequest;

public class SectionAcceptanceTestRequest {

	public static ExtractableResponse<Response> 지하철_노선에_지하철역_등록_요청(String lineUri, Long upStationId, Long downStationId, int distance) {
		SectionRequest request = createSectionRequest(upStationId, downStationId, distance);

		return RestAssured.given().log().all()
			.body(request)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().post(lineUri + "/sections")
			.then().log().all().extract();
	}

	private static SectionRequest createSectionRequest (Long upStationId, Long downStationId, int distance) {
		return new SectionRequest(upStationId, downStationId, distance);
	}
}
