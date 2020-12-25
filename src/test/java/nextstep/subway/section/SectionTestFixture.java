package nextstep.subway.section;

import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.LineTestFixture;
import nextstep.subway.section.dto.SectionRequest;

public class SectionTestFixture {

	public static final String SECTION_SUB_URL = "/sections";
	public static final String SECTION_CREATE_URL_FORMAT = "%s/%d%s";
	public static final String SECTION_DELETE_URL_FORMAT = SECTION_CREATE_URL_FORMAT + "/?stationId=%d";

	public static ExtractableResponse<Response> requestCreateSection(Long lineId, SectionRequest sectionRequest) {
		return RestAssured.given().log().all()
			.body(sectionRequest)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post(String.format(SECTION_CREATE_URL_FORMAT, LineTestFixture.LINE_URL_PREFIX, lineId, SECTION_SUB_URL))
			.then()
			.log()
			.all()
			.extract();
	}

	public static ExtractableResponse<Response> requestDeleteSection(Long lineId, Long stationId) {
		return RestAssured.given().log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.delete(String.format(
				SECTION_DELETE_URL_FORMAT,
				LineTestFixture.LINE_URL_PREFIX,
				lineId,
				SECTION_SUB_URL,
				stationId))
			.then()
			.log()
			.all()
			.extract();
	}
}
