package nextstep.subway.line;

import static io.restassured.RestAssured.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

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
	private StationResponse 서동탄역;
	private StationResponse 양재역;
	private StationResponse 양재시민의숲역;
	private StationResponse 용산역;
	private LineResponse 신분당선;

	@BeforeEach
	public void setUp() {
		super.setUp();

		// given
		강남역 = stationAcceptanceTest.지하철역_생성_되어있음("강남역");
		광교역 = stationAcceptanceTest.지하철역_생성_되어있음("광교역");
		서동탄역 = stationAcceptanceTest.지하철역_생성_되어있음("서동탄역");
		양재역 = stationAcceptanceTest.지하철역_생성_되어있음("양재역");
		용산역 = stationAcceptanceTest.지하철역_생성_되어있음("용산역");
		양재시민의숲역 = stationAcceptanceTest.지하철역_생성_되어있음("양재시민의숲역");
		LineRequest request = new LineRequest("신분당선", "bg-red-600",
			강남역.getId(),
			광교역.getId(),
			10);
		신분당선 = lineAcceptanceTest.지하철_노선_생성_되어있음(request);

	}

	@DisplayName("노선 구간 사이에 새로운 구간을 등록한다. (상행이 같은 경우)")
	@Test
	void addSectionSameUp() {
		final SectionRequest sectionRequest = new SectionRequest(강남역.getId(), 양재역.getId(), 4);
		final List<SectionResponse> previousSectionResponse = 지하철_노선_구간_목록_조회(신분당선.getId())
				.jsonPath().getList(".", SectionResponse.class);

		// when
		ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), sectionRequest);
		// then
		final LineResponse lineResponse = 지하철_노선의_조회_요청(신분당선.getId());
		지하철_노선에_지하철역_등록됨(response, lineResponse);
		지하철_노선에_지하철역_등록됨_상행동일(response, previousSectionResponse, lineResponse);
	}

	@DisplayName("노선 구간 사이에 새로운 구간을 등록한다. (하행이 같은 경우)")
	@Test
	void addSectionSameDown() {
		final SectionRequest sectionRequest = new SectionRequest(양재역.getId(), 광교역.getId(), 4);
		final List<SectionResponse> previousSectionResponse = 지하철_노선_구간_목록_조회(신분당선.getId())
			.jsonPath().getList(".", SectionResponse.class);

		// when
		ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), sectionRequest);

		// then
		final LineResponse lineResponse = 지하철_노선의_조회_요청(신분당선.getId());
		지하철_노선에_지하철역_등록됨(response, lineResponse);
		지하철_노선에_지하철역_등록됨_하행동일(response, previousSectionResponse, lineResponse);
	}

	@DisplayName("새로운 역을 상행 종점으로 등록한다.")
	@Test
	void addSectionToTopUp() {
		final SectionRequest sectionRequest = new SectionRequest(용산역.getId(), 강남역.getId(), 4);

		// when
		ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), sectionRequest);

		// then
		지하철_노선에_지하철역_등록됨(response, 지하철_노선의_조회_요청(신분당선.getId()));
	}

	@DisplayName("새로운 역을 하행 종점으로 등록한다.")
	@Test
	void addSectionToBottomDown() {
		final SectionRequest sectionRequest = new SectionRequest(광교역.getId(), 서동탄역.getId(), 4);

		// when
		ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), sectionRequest);

		// then
		지하철_노선에_지하철역_등록됨(response, 지하철_노선의_조회_요청(신분당선.getId()));
	}

	@DisplayName("새로운 역을 상행 종점으로 등록한다.")
	@Test
	void addSectionToTopUp() {
		final SectionRequest sectionRequest = new SectionRequest(용산역.getId(), 강남역.getId(), 4);

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

	@DisplayName("구간 등록 예외 - 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
	@Test
	void addSectionThrowUpAndDownAlreadyExists() {
		final SectionRequest sectionRequest = new SectionRequest(강남역.getId(), 광교역.getId(), 9);

		// when
		ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), sectionRequest);

		// then
		지하철_노선에_지하철역_등록_예외_발생(response);
	}

	@DisplayName("구간 등록 예외 - 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
	@Test
	void addSectionThrowUpOrDownNotExistsInLine() {
		final SectionRequest sectionRequest = new SectionRequest(양재역.getId(), 양재시민의숲역.getId(), 9);

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

		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
			() -> assertThat(sectionResponse.getId()).isNotNull(),
			() -> assertThat(lineResponse.getStations().stream()
				.map(StationResponse::getId)
				.anyMatch(stationId -> stationId.equals(sectionResponse.getUpStationId()))).isTrue(),
			() -> assertThat(lineResponse.getStations().stream()
				.map(StationResponse::getId)
				.anyMatch(stationId -> stationId.equals(sectionResponse.getDownStationId()))).isTrue()
		);
	}

	public void 지하철_노선에_지하철역_등록됨_상행동일(final ExtractableResponse<Response> response,
		final List<SectionResponse>  previousSectionResponse, final LineResponse lineResponse) {

		SectionResponse sectionResponse = response.as(SectionResponse.class);
		List<SectionResponse> sectionResponses = 지하철_노선_구간_목록_조회(lineResponse.getId())
			.jsonPath().getList(".", SectionResponse.class);

		SectionResponse previousSameUpSection = previousSectionResponse.stream()
			.filter(previousSection -> previousSection.getUpStationId().equals(sectionResponse.getUpStationId()))
			.findFirst()
			.orElseGet(null);

		SectionResponse modifiedSectionToDown = sectionResponses.stream()
			.filter(modifiedSection -> modifiedSection.getUpStationId().equals(sectionResponse.getDownStationId()))
			.findFirst()
			.orElseGet(null);

		assertAll(
			() -> assertThat(modifiedSectionToDown).isNotNull(),
			() -> assertThat(previousSameUpSection).isNotNull(),
			() -> assertThat(previousSameUpSection.getDistance()).isEqualTo(sectionResponse.getDistance() +
				modifiedSectionToDown.getDistance())
		);
	}

	public void 지하철_노선에_지하철역_등록됨_하행동일(final ExtractableResponse<Response> response,
		final List<SectionResponse> previousSectionResponse, final LineResponse lineResponse) {

		SectionResponse sectionResponse = response.as(SectionResponse.class);
		List<SectionResponse> sectionResponses = 지하철_노선_구간_목록_조회(lineResponse.getId())
			.jsonPath().getList(".", SectionResponse.class);

		SectionResponse previousSameDownSection = previousSectionResponse.stream()
			.filter(previousSection -> previousSection.getDownStationId().equals(sectionResponse.getDownStationId()))
			.findFirst()
			.orElseGet(null);

		SectionResponse modifiedSectionToUp = sectionResponses.stream()
			.filter(modifiedSection -> modifiedSection.getDownStationId().equals(sectionResponse.getUpStationId()))
			.findFirst()
			.orElseGet(null);

		assertAll(
			() -> assertThat(modifiedSectionToUp).isNotNull(),
			() -> assertThat(previousSameDownSection).isNotNull(),
			() -> assertThat(previousSameDownSection.getDistance()).isEqualTo(sectionResponse.getDistance() +
				modifiedSectionToUp.getDistance())
		);
	}

	private ExtractableResponse<Response> 지하철_노선_구간_목록_조회(final Long id) {
		return given().log().all()
			.accept(MediaType.ALL_VALUE)
			.when()
			.get(String.format("/lines/%d/sections", id))
			.then().log().all()
			.extract();
	}

	private void 지하철_노선에_지하철역_등록_예외_발생(final ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

}
