package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.StationTestFactory;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.testFactory.AcceptanceTestFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

	public static final String LINE_SERVICE_PATH = "/lines";

	private StationResponse 강남역;
	private StationResponse 역삼역;
	private Map<String, String> 신분당선_정보;
	private Map<String, String> 이호선_정보;

	@BeforeEach
	public void setUp() {
		super.setUp();
		//	given
		강남역 = StationTestFactory.지하철역_생성(StationAcceptanceTest.강남역_정보).as(StationResponse.class);
		역삼역 = StationTestFactory.지하철역_생성(StationAcceptanceTest.역삼역_정보).as(StationResponse.class);

		신분당선_정보 = LineTestFactory.지하철_노선_정보_정의("신분당선", "bg-red-600", 강남역.getId(), 역삼역.getId(), 10);
		이호선_정보 = LineTestFactory.지하철_노선_정보_정의("2호", "bg-green-600", 강남역.getId(), 역삼역.getId(), 10);
	}

	@DisplayName("지하철 노선을 생성한다.")
	@Test
	void createLine() {

		// when
		ExtractableResponse<Response> 신분당선_생성_결과 = LineTestFactory.지하철_노선_생성(신분당선_정보);

		// then
		AcceptanceTestFactory.정상_생성_확인(신분당선_생성_결과);

		String 신분당선_요청_경로 = 신분당선_생성_결과.header("Location");
		지하철_노선이_응답에_포함되어있는_확인(신분당선_생성_결과, 신분당선_정보, Arrays.asList(강남역, 역삼역), 신분당선_요청_경로);
	}

	@DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
	@Test
	void createLine2() {
		// given
		LineTestFactory.지하철_노선_생성(신분당선_정보);

		// when
		ExtractableResponse<Response> 신분당선_중복_생성_결과 = LineTestFactory.지하철_노선_생성(신분당선_정보);

		// then
		AcceptanceTestFactory.예외_발생_확인(신분당선_중복_생성_결과);
	}

	@DisplayName("지하철 노선 목록을 조회한다.")
	@Test
	void getLines() {
		// given
		ExtractableResponse<Response> 신분당선_생성_되어있음 = LineTestFactory.지하철_노선_생성(신분당선_정보);
		ExtractableResponse<Response> 이호선_생성_되어있음 = LineTestFactory.지하철_노선_생성(이호선_정보);

		// when
		ExtractableResponse<Response> 지하철_노선_목록_결과 = 지하철_노선_조회(LINE_SERVICE_PATH);

		// then
		AcceptanceTestFactory.정상_처리_확인(지하철_노선_목록_결과);
		생성된_지하철_노선이_응답_목록에_포함되어있는_확인(Arrays.asList(신분당선_생성_되어있음, 이호선_생성_되어있음), 지하철_노선_목록_결과);
	}

	@DisplayName("특정 지하철 노선을 조회한다.")
	@Test
	void getLine() {
		// given
		ExtractableResponse<Response> 신분당선_생성_되어있음 = LineTestFactory.지하철_노선_생성(신분당선_정보);
		String 신분당선_요청_경로 = 신분당선_생성_되어있음.header("Location");

		// when
		ExtractableResponse<Response> 신분당선_조회_결과 = AcceptanceTestFactory.get(신분당선_요청_경로);

		//then
		AcceptanceTestFactory.정상_처리_확인(신분당선_조회_결과);
		지하철_노선이_응답에_포함되어있는_확인(신분당선_조회_결과, 신분당선_정보, Arrays.asList(강남역, 역삼역), 신분당선_요청_경로);
	}

	@DisplayName("없는 지하철 노선을 조회한다.")
	@Test
	void getLine2() {
		// given
		String 미등록_노선_요청_경로 = LINE_SERVICE_PATH + "/1";

		// when
		ExtractableResponse<Response> 미등록_노선_조회_결과 = 지하철_노선_조회(미등록_노선_요청_경로);

		// then
		AcceptanceTestFactory.예외_발생_확인(미등록_노선_조회_결과);
	}

	@DisplayName("지하철 노선을 수정한다.")
	@Test
	void updateLine() {
		// given
		ExtractableResponse<Response> 신분당선_생성_되어있음 = LineTestFactory.지하철_노선_생성(신분당선_정보);
		String 신분당선_요청_경로 = 신분당선_생성_되어있음.header("Location");

		// when
		Map<String, String> 신분당선_수정정보 = LineTestFactory.지하철_노선_정보_정의("구분당", "bg-blue-600",
			1L, 2L, 10);
		ExtractableResponse<Response> 신분당선_수정_결과 = 지하철_노선_수정(신분당선_수정정보, 신분당선_요청_경로);

		// then
		AcceptanceTestFactory.정상_처리_확인(신분당선_수정_결과);
	}

	private ExtractableResponse<Response> 지하철_노선_수정(Map<String, String> params, String createdUri) {
		return AcceptanceTestFactory.put(params, createdUri);
	}

	@DisplayName("없는 지하철 노선을 수정한다.")
	@Test
	void updateLine2() {
		// given
		String 미등록_노선_요청_경로 = LINE_SERVICE_PATH + "/1";

		// when
		Map<String, String> 미등록_노선_수정정보 = LineTestFactory.지하철_노선_정보_정의("구분당", "bg-blue-600",
			1L, 2L, 10);
		ExtractableResponse<Response> 미등록_노선_수정_결과 = 지하철_노선_수정(미등록_노선_수정정보, 미등록_노선_요청_경로);

		// then
		AcceptanceTestFactory.예외_발생_확인(미등록_노선_수정_결과);
	}

	@DisplayName("지하철 노선을 제거한다.")
	@Test
	void deleteLine() {
		// given
		ExtractableResponse<Response> 신분당선_생성_되어있음 = LineTestFactory.지하철_노선_생성(신분당선_정보);
		String 신분당선_요청_경로 = 신분당선_생성_되어있음.header("Location");

		// when
		ExtractableResponse<Response> 신분당선_삭제_결과 = 지하철_노선_삭제(신분당선_요청_경로);

		// then
		AcceptanceTestFactory.삭제_완료_확인(신분당선_삭제_결과);
	}

	private ExtractableResponse<Response> 지하철_노선_조회(String path) {
		return AcceptanceTestFactory.get(path);
	}

	private void 생성된_지하철_노선이_응답_목록에_포함되어있는_확인(List<ExtractableResponse<Response>> registeredLineResponses,
		ExtractableResponse<Response> response) {
		List<Long> expectedLineIds = registeredLineResponses.stream()
			.map(it -> Long.parseLong(it.header("Location").split("/")[2]))
			.collect(Collectors.toList());
		List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
			.map(it -> it.getId())
			.collect(Collectors.toList());
		assertThat(resultLineIds).containsAll(expectedLineIds);
	}

	private void 지하철_노선이_응답에_포함되어있는_확인(ExtractableResponse<Response> response, Map<String, String> requestLine,
		List<StationResponse> requestStations, String resourceUrl) {
		String createdId = resourceUrl.split("/")[2];

		LineResponse lineResponse = response.as(LineResponse.class);
		assertLineBaseCheck(lineResponse, createdId, requestLine);

		List<Station> stations = lineResponse.getStations();
		assertStations(stations, requestStations);
	}

	private void assertLineBaseCheck(LineResponse lineResponse, String createdId, Map<String, String> requestLine) {
		assertThat(lineResponse.getId()).isEqualTo(Long.parseLong(createdId));
		assertThat(lineResponse.getColor()).isEqualTo(requestLine.get("color"));
		assertThat(lineResponse.getName()).isEqualTo(requestLine.get("name"));
	}

	private void assertStations(List<Station> stations, List<StationResponse> requestStations) {
		List<Long> stationIds = stations.stream()
			.map(it -> it.getId())
			.collect(Collectors.toList());

		assertThat(requestStations.stream()
			.map(StationResponse::getId)
			.filter(id -> stationIds.contains(id))
			.count())
			.isEqualTo(requestStations.size());
	}

	private ExtractableResponse<Response> 지하철_노선_삭제(String createdUri) {
		return AcceptanceTestFactory.delete(createdUri);
	}

}
