package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

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
	void create1() {
		ExtractableResponse<Response> response = 구간_등록_요청(신분당선, 양재역, 청계산입구역, 3);

		구간_등록_성공(response);
	}

	@DisplayName("새로운 역을 상행 종점으로 등록 한다.")
	@Test
	void create2() {
		ExtractableResponse<Response> response = 구간_등록_요청(신분당선, 강남역, 양재역, 3);

		구간_등록_성공(response);
	}

	@DisplayName("새로운 역을 하행 종점으로 등록 한다.")
	@Test
	void create3() {
		ExtractableResponse<Response> response = 구간_등록_요청(신분당선, 판교역, 광교역, 3);

		구간_등록_성공(response);
	}

	public static ExtractableResponse<Response> 구간_등록_요청(LineResponse line, StationResponse upStation,
		StationResponse downStation, int distance) {
		SectionRequest sectionRequest = new SectionRequest(upStation.getId(), downStation.getId(), distance);

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
}
