package nextstep.subway.line;

import static io.restassured.RestAssured.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.line.dto.SectionResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

	private final StationAcceptanceTest stationAcceptanceTest = new StationAcceptanceTest();
	private final LineAcceptanceTest lineAcceptanceTest = new LineAcceptanceTest();

	private StationResponse 강남역;
	private StationResponse 광교역;
	private LineResponse 신분당선;

	@BeforeEach
	public void setUp() {
		super.setUp();

		// given
		강남역 = stationAcceptanceTest.지하철역_생성_되어있음("강남역");
		광교역 = stationAcceptanceTest.지하철역_생성_되어있음("광교역");
		LineRequest request = new LineRequest("신분당선", "bg-red-600",
			강남역.getId(),
			광교역.getId(),
			10);
		신분당선 = lineAcceptanceTest.지하철_노선_생성_되어있음(request);

	}

	@DisplayName("노선에 구간을 등록한다.")
	@Test
	void addSection() {

		final StationResponse 양재역 = stationAcceptanceTest.지하철역_생성_되어있음("양재역");
		final SectionRequest sectionRequest = new SectionRequest(강남역.getId(), 양재역.getId(), 5);

		// when
		ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(sectionRequest);

		// then
		지하철_노선에_지하철역_등록됨(response, 양재역.getId());

	}

	public ExtractableResponse<Response> 지하철_노선에_지하철역_등록_요청(final SectionRequest sectionRequest) {
		return given().log().all()
			.body(sectionRequest)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines/1/sections")
			.then().log().all()
			.extract();
	}

	private void 지하철_노선에_지하철역_등록됨(final ExtractableResponse<Response> response, final Long stationId) {
		SectionResponse sectionResponse = response.as(SectionResponse.class);
		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
			() -> assertThat(sectionResponse.getId()).isNotNull()
		);
	}


}
