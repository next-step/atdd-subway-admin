package nextstep.subway.line;

import static nextstep.subway.line.LineAcceptanceTest.*;
import static nextstep.subway.station.StationAcceptanceTest.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

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
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("지하철 노선의 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

	private static final String LINE_PATH = "/lines";
	private static final String SECTION_PATH = "/sections";
	private static final String SLASH = "/";

	private StationResponse 강남역;
	private StationResponse 광교역;
	private StationResponse 양재역;
	private StationResponse 성수역;
	private LineResponse 신분당선;

	@BeforeEach
	public void setUp() {
		super.setUp();

		강남역 = 지하철역이_등록되어있음(강남역_생성_요청값());
		광교역 = 지하철역이_등록되어있음(광교역_생성_요청값());
		양재역 = 지하철역이_등록되어있음(양재역_생성_요청값());
		성수역 = 지하철역이_등록되어있음(성수역_생성_요청값());

		신분당선 = 지하철_노선_등록되어_있음(신분당선_생성_요청값(강남역, 광교역));
	}

	@DisplayName("섹션으로 정거장리스트를 조회한다.")
	@Test
	void findAllStations() {
		// when
		List<StationResponse> 정거장리스트 = 신분당선.getStations();

		// then
		정거장리스트가_조회됨(정거장리스트);
	}

	void 정거장리스트가_조회됨(List<StationResponse> stationResponseList) {
		assertThat(stationResponseList).hasSize(2);
	}

	@DisplayName("노선에 구간을 등록한다.")
	@Test
	void addSection() {
		// when
		ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), 구간_등록요청(강남역, 양재역, 2));

		// then
		지하철_노선에_지하철역_등록됨(response);
	}

	SectionRequest 구간_등록요청(StationResponse downStation, StationResponse upStation, int distance) {
		return new SectionRequest(downStation.getId(), upStation.getId(), distance);
	}

	ExtractableResponse<Response> 지하철_노선에_지하철역_등록_요청(Long id, SectionRequest params) {
		return RestAssured.given().log().all()
			.when().body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.post(LINE_PATH + SLASH + id + SECTION_PATH)
			.then().log().all().extract();
	}

	void 지하철_노선에_지하철역_등록됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	@DisplayName("기존 역 사이 길이보다 크거나 같은 길이의 구간을 추가한다.")
	@Test
	void addSectionOverDistance() {
		// when
		ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), 구간_등록요청(강남역, 양재역, 11));

		// then
		지하철_노선에_지하철역_등록_실패됨(response);
	}

	void 지하철_노선에_지하철역_등록_실패됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
	}

	@DisplayName("이미 등록 되어있는 구간을 추가한다.")
	@Test
	void addSectionDuplicate() {
		// given
		LineResponse 이미_등록된_구간 = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), 구간_등록요청(강남역, 양재역, 5)).as(LineResponse.class);

		// when
		ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(이미_등록된_구간.getId(), 구간_등록요청(강남역, 양재역, 3));

		// then
		지하철_노선에_지하철역_등록_실패됨(response);
	}

	@DisplayName("상, 하행역 둘 중 하나도 포함되지 않은 구간을 추가한다.")
	@Test
	void addSectionNotInStations() {
		// when
		ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), 구간_등록요청(성수역, 양재역, 3));

		// then
		지하철_노선에_지하철역_등록_실패됨(response);
	}
}
