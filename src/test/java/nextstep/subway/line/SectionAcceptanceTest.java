package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

	private StationResponse 양재역;
	private StationResponse 정자역;
	private LineResponse 신분당선;

	@BeforeEach
	public void setUp() {
		super.setUp();

		// given
		양재역 = StationAcceptanceTest.지하철_생성_요청("양재역").as(StationResponse.class);
		정자역 = StationAcceptanceTest.지하철_생성_요청("정자역").as(StationResponse.class);

		HashMap<String, Object> createParams = new HashMap<>();
		createParams.put("name", "신분당선");
		createParams.put("color", "bg-red-600");
		createParams.put("upStation", 양재역.getId());
		createParams.put("downStation", 정자역.getId());
		createParams.put("distance", 10);
		신분당선 = LineAcceptanceTest.지하철노선_생성_요청(createParams).as(LineResponse.class);
	}

	@DisplayName("역 사이에 새로운 역을 등록한다.")
	@Test
	void addSection_happyPath() {
		// when : 지하철_구간_등록_요청
		StationResponse 판교역 = StationAcceptanceTest.지하철_생성_요청("판교역").as(StationResponse.class);
		ExtractableResponse response = 지하철_구간_등록_요청(신분당선.getId(), 양재역.getId(), 판교역.getId(), 5);

		// then : 지하철_구간_등록됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
	}

	@DisplayName("이미 등록된 구간을 등록한다.")
	@Test
	void addSection_exceptionCase1() {
		// when : 지하철_구간_등록_요청
		ExtractableResponse response = 지하철_구간_등록_요청(신분당선.getId(), 정자역.getId(), 양재역.getId(), 8);

		// then : 지하철_구간_등록_실패됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("최초 등록이 아니면서, 이미 등록된 역을 하나도 포함하지 않는 구간을 등록한다.")
	@Test
	void addSection_exceptionCase2() {
		// when : 지하철_구간_등록_요청
		StationResponse 강남역 = StationAcceptanceTest.지하철_생성_요청("강남역").as(StationResponse.class);
		StationResponse 광교역 = StationAcceptanceTest.지하철_생성_요청("광교역").as(StationResponse.class);
		ExtractableResponse response = 지하철_구간_등록_요청(신분당선.getId(), 강남역.getId(), 광교역.getId(), 20);

		// then : 지하철_구간_등록_실패됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("역 사이에 새로운 역을 등록할 때, 기존 역 사이 길이보다 크게 등록한다.")
	@Test
	void addSection_exceptionCase3() {
		// when : 지하철_구간_등록_요청
		StationResponse 판교역 = StationAcceptanceTest.지하철_생성_요청("판교역").as(StationResponse.class);
		ExtractableResponse response = 지하철_구간_등록_요청(신분당선.getId(), 양재역.getId(), 판교역.getId(), 15);

		// then : 지하철_구간_등록_실패됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("역 사이에 새로운 역을 등록할 때, 기존 역 사이 길이보다 같게 등록한다.")
	@Test
	void addSection_exceptionCase4() {
		// when : 지하철_구간_등록_요청
		StationResponse 판교역 = StationAcceptanceTest.지하철_생성_요청("판교역").as(StationResponse.class);
		ExtractableResponse response = 지하철_구간_등록_요청(신분당선.getId(), 양재역.getId(), 판교역.getId(), 10);

		// then : 지하철_구간_등록_실패됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("새로운 역을 상행 종점으로 등록한다.")
	@Test
	void addSection_newUpStation() {
		// when : 지하철_구간_등록_요청
		StationResponse 강남역 = StationAcceptanceTest.지하철_생성_요청("강남역").as(StationResponse.class);
		ExtractableResponse response = 지하철_구간_등록_요청(신분당선.getId(), 강남역.getId(), 양재역.getId(), 3);

		// then : 지하철_구간_등록됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
	}

	@DisplayName("새로운 역을 하행 종점으로 등록한다.")
	@Test
	void addSection_newDownStation() {
		// when : 지하철_구간_등록_요청
		StationResponse 광교역 = StationAcceptanceTest.지하철_생성_요청("광교역").as(StationResponse.class);
		ExtractableResponse response = 지하철_구간_등록_요청(신분당선.getId(), 정자역.getId(), 광교역.getId(), 4);

		// then : 지하철_구간_등록됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
	}

	public static ExtractableResponse<Response> 지하철_구간_등록_요청(
		long lineId, long upStationId, long downStationId, int distance) {
		Map<String, Object> params = new HashMap<>();
		params.put("downStationId", upStationId);
		params.put("upStationId", downStationId);
		params.put("distance", distance);

		return RestAssured
			.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().post("/lines/" + lineId + "/sections")
			.then().log().all().extract();
	}
}