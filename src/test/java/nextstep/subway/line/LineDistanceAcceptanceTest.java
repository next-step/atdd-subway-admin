package nextstep.subway.line;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 구간 조회 기능")
public class LineDistanceAcceptanceTest extends AcceptanceTest {

	@DisplayName("노선 생성 시 종점역(상행, 하행)을 함께 추가하기")
	@Test
	@Disabled
	void createLineWithTerminal() throws JsonProcessingException {
		String upStationName = "강변역";
		String downStationName = "건대입구역";
		Long upStationId = 역_생성_후_stationId응답(upStationName);
		Long downStationId = 역_생성_후_stationId응답(downStationName);

		LineRequest lineRequest = new LineRequest("2호선", "bg-green-600", upStationId, downStationId, 3);
		// when
		// 지하철_노선_생성_요청
		String lineName = "2호선";
		ExtractableResponse response = 노선_생성_함수(lineRequest);
		// then
		// 지하철_노선_생성됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

		String lineId = response.header("Location").split("/")[2];
		ExtractableResponse<Response> linesResponse = ID로_노선을_조회한다(Long.valueOf(lineId));
		assertThat(linesResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

		LineResponse lineResponse = linesResponse.jsonPath().getObject(".", LineResponse.class);
		assertThat(lineResponse.getName()).isEqualTo(lineName);

		assertThat(lineResponse.getStations().stream())
				.extracting(SectionResponse::getName)
				.containsExactly(upStationName, downStationName);
	}

	@DisplayName("노선 조회 응답 결과에 등록된 구간을 참고하여 역 목록 응답 추가하기")
	@Test
	@Disabled
	void listLineWithTerminal() throws JsonProcessingException {
		String upStationName = "강변역";
		String downStationName = "건대입구역";
		Long upStationId = 역_생성_후_stationId응답(upStationName);
		Long downStationId = 역_생성_후_stationId응답(downStationName);
		LineRequest lineRequest = new LineRequest("2호선", "bg-green-600", upStationId, downStationId, 3);
		// when
		// 지하철_노선_생성_요청
		ExtractableResponse response = 노선_생성_함수(lineRequest);
		// then
		// 지하철_노선_생성됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

		ExtractableResponse<Response> linesResponse = 전체_노선을_조회한다();

		assertThat(linesResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
		List<LineResponse> lineResponse = linesResponse.jsonPath().getList(".", LineResponse.class);
		assertThat(lineResponse.get(0).getStations().stream().map(SectionResponse::getName)).containsExactly(upStationName, downStationName);
	}

}
