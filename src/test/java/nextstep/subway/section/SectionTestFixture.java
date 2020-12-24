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
}
