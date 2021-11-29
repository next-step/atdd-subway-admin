package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.dto.StationRequest;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {
	private static final String LINE_ROOT_PATH = "/lines";
	private static final String STATION_ROOT_PATH = "/stations";

	@BeforeEach
	public void before() {
		post(STATION_ROOT_PATH, new StationRequest("두정역"));
		post(STATION_ROOT_PATH, new StationRequest("천안역"));
		post(STATION_ROOT_PATH, new StationRequest("봉명역"));
		post(STATION_ROOT_PATH, new StationRequest("쌍용역"));
		post(STATION_ROOT_PATH, new StationRequest("천안아산역"));

		post(STATION_ROOT_PATH, new StationRequest("양재역"));
		post(STATION_ROOT_PATH, new StationRequest("판교역"));

		post(LINE_ROOT_PATH, new LineRequest("1호선", "blue", 2L, 4L, 30));
	}

	@DisplayName("지하철 노선 시작점에 신규 구간 추가")
	@Test
	void addSectionStartLocationSuccess() {
		// given (신규 구간 생성)
		SectionRequest param = new SectionRequest(1L, 2L, 15);

		// when (신규 구간 노선에 추가)
		ExtractableResponse<Response> response = post("/lines/1/sections", param);

		// then (신규 구간 추가 성공)
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
	}

	@DisplayName("지하철 노선 종점에 신규 구간 추가")
	@Test
	void addSectionEndLocationSuccess() {
		// given (신규 구간 생성)
		SectionRequest param = new SectionRequest(4L, 5L, 15);

		// when (신규 구간 노선에 추가)
		ExtractableResponse<Response> response = post("/lines/1/sections", param);

		// then (신규 구간 추가 성공)
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
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
