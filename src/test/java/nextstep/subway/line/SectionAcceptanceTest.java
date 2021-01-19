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
	private StationResponse 양재역;
	private StationResponse 양재시민의숲역;
	private LineResponse 신분당선;

	@BeforeEach
	public void setUp() {
		super.setUp();

		// given
		강남역 = stationAcceptanceTest.지하철역_생성_되어있음("강남역");
		광교역 = stationAcceptanceTest.지하철역_생성_되어있음("광교역");
		양재역 = stationAcceptanceTest.지하철역_생성_되어있음("양재역");
		양재시민의숲역 = stationAcceptanceTest.지하철역_생성_되어있음("양재시민의숲역");
		LineRequest request = new LineRequest("신분당선", "bg-red-600",
			강남역.getId(),
			광교역.getId(),
			10);
		신분당선 = lineAcceptanceTest.지하철_노선_생성_되어있음(request);

	}

	@DisplayName("노선에 구간을 등록한다.")
	@Test
	void addSection() {
		final SectionRequest sectionRequest = new SectionRequest(강남역.getId(), 양재역.getId(), 4);

		// when
		ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), sectionRequest);

		// then
		지하철_노선에_지하철역_등록됨(response, 지하철_노선의_조회_요청(신분당선.getId()));
	}

	@DisplayName("구간 등록 예외 - 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이와 같으면 등록을 할 수 없음")
	@Test
	void addSectionThrowDistanceEqual() {
		final SectionRequest sectionRequest = new SectionRequest(강남역.getId(), 양재역.getId(), 10);

		// when
		ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), sectionRequest);

		// then
		지하철_노선에_지하철역_등록_예외_발생(response);
	}

	@DisplayName("구간 등록 예외 - 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이 보다 크면 등록을 할 수 없음")
	@Test
	void addSectionThrowDistanceGreaterThan() {
		final SectionRequest sectionRequest = new SectionRequest(강남역.getId(), 양재역.getId(), 11);

		// when
		ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), sectionRequest);

		// then
		지하철_노선에_지하철역_등록_예외_발생(response);
	}

	public ExtractableResponse<Response> 지하철_노선에_지하철역_등록_요청(final Long lineId,
			final SectionRequest sectionRequest) {
		return given().log().all()
			.body(sectionRequest)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines/" + lineId + "/sections")
			.then().log().all()
			.extract()
			;
	}

	public LineResponse 지하철_노선의_조회_요청(final Long lineId) {
		return lineAcceptanceTest.지하철_노선_조회_요청(lineId).as(LineResponse.class);
	}

	private void 지하철_노선에_지하철역_등록됨(final ExtractableResponse<Response> response,
			final LineResponse lineResponse) {
		SectionResponse sectionResponse = response.as(SectionResponse.class);
		SectionResponse foundSection = lineResponse.getSections().stream()
			.filter(section -> section.getId().equals(sectionResponse.getId()))
			.findAny()
			.orElseGet(null);

		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
			() -> assertThat(sectionResponse.getId()).isNotNull(),
			() -> assertThat(foundSection).isNotNull(),
			() -> assertThat(sectionResponse.getUpStationId()).isEqualTo(foundSection.getUpStationId()),
			() -> assertThat(sectionResponse.getDownStationId()).isEqualTo(foundSection.getDownStationId())
		);
	}

	private void 지하철_노선에_지하철역_등록_예외_발생(final ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

}
