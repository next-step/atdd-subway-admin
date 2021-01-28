package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {
	private LineResponse 신분당선;
	private StationResponse 강남역;
	private StationResponse 양재역;
	private StationResponse 청계산입구역;
	private StationResponse 판교역;
	private StationResponse 광교역;

	@BeforeEach
	public void setUp() {
		super.setUp();
		강남역 = StationAcceptanceTest.지하철역_생성_요청("강남역").as(StationResponse.class);
		양재역 = StationAcceptanceTest.지하철역_생성_요청("양재역").as(StationResponse.class);
		청계산입구역 = StationAcceptanceTest.지하철역_생성_요청("청계산입구역").as(StationResponse.class);
		판교역 = StationAcceptanceTest.지하철역_생성_요청("판교역").as(StationResponse.class);
		광교역 = StationAcceptanceTest.지하철역_생성_요청("광교역").as(StationResponse.class);

		LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", 양재역.getId(), 판교역.getId(), 10);
		신분당선 = LineAcceptanceTest.노선_생성_요청(lineRequest).as(LineResponse.class);
	}

	@DisplayName("역 사이에 새로운 역을 등록 한다.")
	@Test
	void addSectionBetweenStations() {
		ExtractableResponse<Response> createResponse = 구간_등록_요청(신분당선, 양재역, 청계산입구역, 3);

		구간_등록_성공(createResponse);
		ExtractableResponse<Response> response = LineAcceptanceTest.노선_조회_요청(신분당선);
		LineAcceptanceTest.노선_조회_성공(response, Arrays.asList(양재역, 청계산입구역, 판교역));
	}

	@DisplayName("새로운 역을 상행 종점으로 등록 한다.")
	@Test
	void addSectionWithNewUpStation() {
		ExtractableResponse<Response> createResponse = 구간_등록_요청(신분당선, 강남역, 양재역, 3);

		구간_등록_성공(createResponse);
		ExtractableResponse<Response> response = LineAcceptanceTest.노선_조회_요청(신분당선);
		LineAcceptanceTest.노선_조회_성공(response, Arrays.asList(강남역, 양재역, 판교역));
	}

	@DisplayName("새로운 역을 하행 종점으로 등록 한다.")
	@Test
	void addSectionWithNewDownStation() {
		ExtractableResponse<Response> createResponse = 구간_등록_요청(신분당선, 판교역, 광교역, 3);

		구간_등록_성공(createResponse);
		ExtractableResponse<Response> response = LineAcceptanceTest.노선_조회_요청(신분당선);
		LineAcceptanceTest.노선_조회_성공(response, Arrays.asList(양재역, 판교역, 광교역));
	}

	@DisplayName("존재하지 않는 노선은 등록할 수 없다.")
	@Test
	void addSectionWithNoLine() {
		ExtractableResponse<Response> response = 구간_등록_요청(new LineResponse(10L, null, null, null, null, null), 판교역, 광교역,
			3);

		구간_등록_실패(response);
	}

	@DisplayName("존재하지 않는 역은 등록할 수 없다.")
	@Test
	void addSectionWithNoStation() {
		ExtractableResponse<Response> upStationResponse = 구간_등록_요청(신분당선, new StationResponse(20L, null, null, null),
			광교역, 3);

		구간_등록_실패(upStationResponse);

		ExtractableResponse<Response> downStationResponse = 구간_등록_요청(신분당선, 판교역,
			new StationResponse(21L, null, null, null), 3);

		구간_등록_실패(downStationResponse);
	}

	@DisplayName("기존 역 사이 길이보다 크거나 같으면 등록할 수 없다.")
	@Test
	void addSectionWhitLongDistance() {
		ExtractableResponse<Response> response = 구간_등록_요청(신분당선, 양재역, 청계산입구역, 15);

		구간_등록_실패(response);
	}

	@DisplayName("상행역, 하행역이 모두 등록되어 있으면 등록할 수 없다.")
	@Test
	void addSectionWhitContainsStations() {
		ExtractableResponse<Response> response = 구간_등록_요청(신분당선, 판교역, 양재역, 3);

		구간_등록_실패(response);
	}

	@DisplayName("상행역, 하행역이 모두 등록되어 있지 않으면 등록할 수 없다.")
	@Test
	void addSectionWhitNotContainsStations() {
		ExtractableResponse<Response> response = 구간_등록_요청(신분당선, 강남역, 광교역, 5);

		구간_등록_실패(response);
	}

	@DisplayName("노선의 구간을 제거한다.")
	@Test
	void deleteSection() {
		구간_등록_되어있음(신분당선, 강남역, 양재역, 5);
		ExtractableResponse<Response> deleteResponse = 구간_삭제_요청(신분당선, 강남역);

		구간_삭제_성공(deleteResponse);
		ExtractableResponse<Response> response = LineAcceptanceTest.노선_조회_요청(신분당선);
		LineAcceptanceTest.노선_조회_성공(response, Arrays.asList(양재역, 판교역));
	}

	@DisplayName("중간역이 제거될 경우 재배치 한다.")
	@Test
	void deleteSectionWhenMiddleStation() {
		구간_등록_되어있음(신분당선, 강남역, 양재역, 5);
		ExtractableResponse<Response> deleteResponse = 구간_삭제_요청(신분당선, 양재역);

		구간_삭제_성공(deleteResponse);
		ExtractableResponse<Response> response = LineAcceptanceTest.노선_조회_요청(신분당선);
		LineAcceptanceTest.노선_조회_성공(response, Arrays.asList(강남역, 판교역));
	}

	@DisplayName("구간이 하나인 노선은 삭제할 수 없다.")
	@Test
	void deleteSectionWhenOneSection() {
		ExtractableResponse<Response> response = 구간_삭제_요청(신분당선, 양재역);

		구간_삭제_실패(response);
	}

	public static ExtractableResponse<Response> 구간_등록_요청(LineResponse line, StationResponse upStation,
		StationResponse downStation, int distance) {
		SectionRequest sectionRequest = new SectionRequest(upStation.getId(), downStation.getId(),
			distance);

		return RestAssured.given().log().all()
			.body(sectionRequest)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().post("/lines/{lineId}/sections", line.getId())
			.then().log().all()
			.extract();
	}

	public static void 구간_등록_성공(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(response.header("Location")).isNotNull();
	}

	public static void 구간_등록_실패(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
	}

	public static void 구간_등록_되어있음(LineResponse line, StationResponse upStation, StationResponse downStation,
		int distance) {
		구간_등록_성공(구간_등록_요청(line, upStation, downStation, distance));
	}

	public static ExtractableResponse<Response> 구간_삭제_요청(LineResponse line, StationResponse station) {
		return RestAssured.given().log().all()
			.queryParam("stationId", station.getId())
			.when().delete("/lines/{lineId}/sections", line.getId())
			.then().log().all()
			.extract();
	}

	public static void 구간_삭제_성공(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}

	public static void 구간_삭제_실패(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
	}
}
