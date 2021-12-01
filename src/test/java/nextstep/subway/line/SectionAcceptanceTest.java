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
import nextstep.subway.station.dto.StationResponse;

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

	@DisplayName("지하철 노선 시작점에 신규구간 추가")
	@Test
	void addSectionStartLocationSuccess() {
		// given (신규 구간 생성)
		SectionRequest param = new SectionRequest(1L, 2L, 15);

		// when (신규 구간 노선에 추가)
		ExtractableResponse<Response> response = post("/lines/1/sections", param);

		// then (신규 구간 추가 성공)
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
	}

	@DisplayName("지하철 노선 종점에 신규구간 추가")
	@Test
	void addSectionEndLocationSuccess() {
		// given (신규 구간 생성)
		SectionRequest param = new SectionRequest(4L, 5L, 15);

		// when (신규 구간 노선에 추가)
		ExtractableResponse<Response> response = post("/lines/1/sections", param);

		// then (신규 구간 추가 성공)
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
	}

	@DisplayName("지하철 노선 중간에 신규구간 추가 (시작점 일치)")
	@Test
	void addSectionMiddleStartLocationSuccess() {
		// given (신규 구간 생성)
		SectionRequest param = new SectionRequest(2L, 3L, 15);

		// when (신규 구간 노선에 추가)
		ExtractableResponse<Response> response = post("/lines/1/sections", param);

		// then (신규 구간 추가 성공)
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
	}

	@DisplayName("지하철 노선 중간에 신규구간 추가 (종점 일치)")
	@Test
	void addSectionMiddleEndLocationSuccess() {
		// given (신규 구간 생성)
		SectionRequest param = new SectionRequest(3L, 4L, 15);

		// when (신규 구간 노선에 추가)
		ExtractableResponse<Response> response = post("/lines/1/sections", param);

		// then (신규 구간 추가 성공)
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
	}

	@DisplayName("지하철 노선 중간에 신규구간 추가 길이가 길어서 실패")
	@Test
	void addSectionMiddleLocationFail() {
		// given (신규 구간 생성)
		SectionRequest param = new SectionRequest(3L, 4L, 30);

		// when (신규 구간 노선에 추가)
		ExtractableResponse<Response> response = post("/lines/1/sections", param);

		// then (신규구간 길이 제약으로 실패)
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("지하철 노선에 없는 역들로 구성된 신규구간 추가 실패")
	@Test
	void addSectionNullStationFail() {
		// given (신규 구간 생성)
		SectionRequest param = new SectionRequest(6L, 7L, 13);

		// when (신규 구간 노선에 추가)
		ExtractableResponse<Response> response = post("/lines/1/sections", param);

		// then (노선에 역이 존재하지 않아서 실패)
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("지하철 노선에 전부 존재하는 역들로 구성된 신규구간 추가 실패")
	@Test
	void addSectionAllContainStationFail() {
		// given (신규 구간 생성)
		SectionRequest param = new SectionRequest(2L, 4L, 13);

		// when (신규 구간 노선에 추가)
		ExtractableResponse<Response> response = post("/lines/1/sections", param);

		// then (노선에 전부 있는 역들이라서 실패)
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@Test
	@DisplayName("지하철 노선 내 구간에서 중간 역 삭제 요청 성공")
	public void deleteMiddleStationInSectionSuccess() {
		//given
		SectionRequest param = new SectionRequest(3L, 4L, 17);
		post("/lines/1/sections", param);
		//when
		ExtractableResponse<Response> deleteStationResponse = delete("/lines/1/sections?stationId=3");
		//then
		assertThat(deleteStationResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
		//when
		ExtractableResponse<Response> findLineResponse = get("/lines/1");
		//then
		List<Long> stationIds = getStationIds(findLineResponse);
		assertThat(stationIds).containsExactly(2L, 4L);
	}

	@Test
	@DisplayName("지하철 노선 내 구간에서 시작역 삭제 요청 성공")
	public void deleteStartStationInSectionSuccess() {
		//given
		SectionRequest param = new SectionRequest(3L, 4L, 17);
		post("/lines/1/sections", param);
		//when
		ExtractableResponse<Response> deleteStationResponse = delete("/lines/1/sections?stationId=2");
		//then
		assertThat(deleteStationResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
		//when
		ExtractableResponse<Response> findLineResponse = get("/lines/1");
		//then
		List<Long> stationIds = getStationIds(findLineResponse);
		assertThat(stationIds).containsExactly(3L, 4L);
	}

	@Test
	@DisplayName("지하철 노선 내 구간에서 종점역 삭제 요청 성공")
	public void deleteEndStationInSectionSuccess() {
		//given
		SectionRequest param = new SectionRequest(3L, 4L, 17);
		post("/lines/1/sections", param);
		//when
		ExtractableResponse<Response> deleteStationResponse = delete("/lines/1/sections?stationId=4");
		//then
		assertThat(deleteStationResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
		//when
		ExtractableResponse<Response> findLineResponse = get("/lines/1");
		//then
		List<Long> stationIds = getStationIds(findLineResponse);
		assertThat(stationIds).containsExactly(2L, 3L);
	}

	@Test
	@DisplayName("지하철 노선 내 1개 구간만 존재 시 역 삭제 요청 실패")
	public void deleteStationInSectionsSizeOneFail() {
		//given
		//when
		ExtractableResponse<Response> deleteStationResponse = delete("/lines/1/sections?stationId=2");
		//then
		assertThat(deleteStationResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@Test
	@DisplayName("지하철 노선 내 구간에 미 존재 역 삭제 요청 실패")
	public void deleteNotContainStationInSectionsFail() {
		//given
		SectionRequest param = new SectionRequest(3L, 4L, 17);
		post("/lines/1/sections", param);
		//when
		ExtractableResponse<Response> deleteStationResponse = delete("/lines/1/sections?stationId=1");
		//then
		assertThat(deleteStationResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	private List<Long> getStationIds(ExtractableResponse<Response> findLineResponse) {
		return findLineResponse.body()
			.jsonPath()
			.getObject(".", LineResponse.class)
			.getStations()
			.stream()
			.map(StationResponse::getId)
			.collect(Collectors.toList());
	}

}
