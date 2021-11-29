package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
	private static final String LINE_ROOT_PATH = "/lines";
	private static final String STATION_ROOT_PATH = "/stations";
	private static LineRequest params = new LineRequest("신분당선", "pink", 1L, 2L, 10);
	private static LineRequest otherParams = new LineRequest("1호선", "blue", 3L, 4L, 8);

	@BeforeEach
	public void createStation() {
		post(STATION_ROOT_PATH, new StationRequest("양재역"));
		post(STATION_ROOT_PATH, new StationRequest("판교역"));
		post(STATION_ROOT_PATH, new StationRequest("두정역"));
		post(STATION_ROOT_PATH, new StationRequest("천안역"));
	}

	@DisplayName("지하철 노선을 생성한다.")
	@Test
	void createLineSuccess() {
		// given
		// when
		// 지하철_노선_생성_요청
		ExtractableResponse<Response> response = post(LINE_ROOT_PATH, params);
		// Response response = requestCreateLines(params);

		// then
		// 지하철_노선_생성됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(response.header("Location")).isNotBlank();
	}

	@DisplayName("중복된 노선 이름으로 생성 요청시 실패 테스트")
	@Test
	void createDuplicateLineFail() {
		// given
		// 지하철_노선_등록되어_있음
		post(LINE_ROOT_PATH, params);

		// when
		// 지하철_노선_중복생성_요청
		ExtractableResponse<Response> response = post(LINE_ROOT_PATH, params);

		// then
		// 지하철_노선_생성_실패됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("존재하지 않는 역으로 구간정보 전달시 노선 생성 요청시 실패 테스트")
	@Test
	void createLineNonStationFail() {
		// given
		// 존재하지 않는 역 id값 가지는 파라미터 생성
		LineRequest params = new LineRequest("신분당선", "pink", 3L, 5L, 24);

		// when
		// 노선 생성 요청
		ExtractableResponse<Response> response = post(LINE_ROOT_PATH, params);

		// then
		// 노선 생성 실패
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("지하철 노선 목록을 전체 조회.")
	@Test
	void getLines() {
		// given
		// 지하철_노선_등록되어_있음
		// 지하철_노선_등록되어_있음
		ExtractableResponse<Response> createResponse = post(LINE_ROOT_PATH, params);
		ExtractableResponse<Response> otherCreateResponse = post(LINE_ROOT_PATH, otherParams);

		// when
		// 지하철_노선_목록_조회_요청
		ExtractableResponse<Response> findResponse = get(LINE_ROOT_PATH);

		// then
		// 지하철_노선_목록_응답됨
		// 지하철_노선_목록_포함됨
		List<Long> findIds = findResponse.jsonPath().getList(".", LineResponse.class)
			.stream()
			.map(lineResponse -> lineResponse.getId())
			.collect(Collectors.toList());

		List<Long> createIds = Arrays.asList(createResponse, otherCreateResponse).stream()
			.map(res -> Long.parseLong(res.header("Location").split("/")[2]))
			.collect(Collectors.toList());

		assertThat(findIds).containsAll(createIds);
	}

	@DisplayName("지하철 노선 조회 성공")
	@Test
	void getLineSuccess() {
		// given
		// 지하철_노선_등록되어_있음
		ExtractableResponse<Response> createResponse = post(LINE_ROOT_PATH, params);
		String url = extractUrlByResponse(createResponse);

		// when
		// 지하철_노선_조회_요청
		ExtractableResponse<Response> findResponse = get(url);

		// then
		// 지하철_노선_응답됨
		assertThat(extractLineResponse(findResponse).getId()).isEqualTo(extractIdByURL(url));
	}

	@DisplayName("지하철 노선 내 구간 조회 성공")
	@Test
	void getLineAndSectionSuccess() {
		// given
		// 지하철_노선_등록
		ExtractableResponse<Response> createResponse = post(LINE_ROOT_PATH, params);
		String url = extractUrlByResponse(createResponse);

		// when
		// 지하철_노선_조회_요청
		ExtractableResponse<Response> findResponse = get(url);

		// then
		// 노선 내 지하철 역 확인
		LineResponse findLineResponse = extractLineResponse(findResponse);
		assertThat(findLineResponse.getStations().get(0).getName()).isEqualTo("양재역");
	}

	@DisplayName("지하철 노선 조회 실패")
	@Test
	void getLineFail() {
		// given
		// 지하철_노선_등록되어_있음
		post(LINE_ROOT_PATH, params);

		// when
		// 지하철_노선_조회_요청
		ExtractableResponse<Response> findResponse = get("/lines/3");

		// then
		// 지하철_노선_응답됨
		assertThat(findResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("지하철 노선 수정 성공")
	@Test
	void updateLineSuccess() {
		// given
		// 지하철_노선_등록되어_있음
		ExtractableResponse<Response> createResponse = post(LINE_ROOT_PATH, params);
		String url = extractUrlByResponse(createResponse);

		// when
		// 지하철_노선_수정_요청
		ExtractableResponse updateResponse = patch(url, otherParams);

		// then
		// 지하철_노선_수정됨
		LineResponse lineResponse = extractLineResponse(updateResponse);
		assertThat(lineResponse.getName()).isEqualTo("1호선");
		assertThat(lineResponse.getId()).isEqualTo(extractIdByURL(url));
	}

	@DisplayName("지하철 노선 수정 실패")
	@Test
	void updateLineFail() {
		// given
		// 지하철_노선_등록되어_있음
		ExtractableResponse<Response> createResponse = post(LINE_ROOT_PATH, params);

		// when
		// 지하철_노선_수정_요청
		ExtractableResponse updateResponse = patch("lines/3", otherParams);

		// then
		// 지하철_노선_수정됨
		assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("지하철 노선을 제거 성공")
	@Test
	void deleteLineSuccess() {
		// given
		// 지하철_노선_등록되어_있음
		ExtractableResponse<Response> createResponse = post(LINE_ROOT_PATH, params);
		post(LINE_ROOT_PATH, otherParams);
		String url = extractUrlByResponse(createResponse);

		// when
		// 지하철_노선_제거_요청
		delete(url);
		List<LineResponse> findAllResponse = get(LINE_ROOT_PATH).jsonPath().getList(".", LineResponse.class);

		// then
		// 지하철_노선_삭제됨
		assertThat(findAllResponse).hasSize(1);
	}

	@DisplayName("지하철 노선을 제거 실패")
	@Test
	void deleteLineFail() {
		// given
		// 지하철_노선_등록되어_있음
		ExtractableResponse<Response> createResponse = post(LINE_ROOT_PATH, params);

		// when
		// 지하철_노선_제거_요청
		ExtractableResponse deleteResponse = delete("/lines/3");

		// then
		// 지하철 노선 삭제 실패
		assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	private long extractIdByURL(String url) {
		return Long.parseLong(url.split("/")[2]);
	}

	private LineResponse extractLineResponse(ExtractableResponse<Response> response) {
		return response.jsonPath().getObject(".", LineResponse.class);
	}

	private String extractUrlByResponse(ExtractableResponse<Response> response) {
		return response.header("Location");
	}
}
