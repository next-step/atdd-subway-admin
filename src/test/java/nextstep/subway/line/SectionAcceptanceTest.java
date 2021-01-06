package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;

/**
 * @author : byungkyu
 * @date : 2021/01/06
 * @description :
 **/

@DisplayName("지하철 구간 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SectionAcceptanceTest extends AcceptanceTest {

	StationResponse 강남역;
	StationResponse 광교역;
	LineResponse 신분당선;

	@BeforeEach
	public void setUp() {
		super.setUp();

		// given
		강남역 = StationAcceptanceTest.지하철역_생성("강남역").as(StationResponse.class);
		광교역 = StationAcceptanceTest.지하철역_생성("광교역").as(StationResponse.class);

		Map<String, String> createParams = new HashMap<>();
		createParams.put("name", "신분당선");
		createParams.put("color", "bg-red-600");
		createParams.put("upStation", 강남역.getId() + "");
		createParams.put("downStation", 광교역.getId() + "");
		createParams.put("distance", 10 + "");
		신분당선 = LineAcceptanceTest.지하철_노선_생성_요청(createParams).as(LineResponse.class);
	}

	@DisplayName("노선에 구간을 등록한다.(시작역 동일, 추가되는 구간의 종점은 기존 종점범위 내에 존재)")
	@Test
	void addSectionSameUpStation() {
		// when
		// 지하철_노선에_지하철역_등록_요청
		StationResponse 판교역 = StationAcceptanceTest.지하철역_생성("판교역").as(StationResponse.class);
		int distance = 4;
		지하철_노선에_지하철역_등록_요청(신분당선.getId(), 강남역.getId(), 판교역.getId(), distance);

		// then
		ExtractableResponse<Response> response = LineAcceptanceTest.지하철_노선_조회_요청(신분당선.getId());
		지하철_노선에_지하철역_등록됨(response, 강남역, 판교역, 광교역);
	}

	@DisplayName("노선에 구간을 등록한다.(새로운 역을 상행 종점으로 등록)")
	@Test
	void addSectionNewUpStation() {
		// when
		// 지하철_노선에_지하철역_등록_요청
		StationResponse 신논현역 = StationAcceptanceTest.지하철역_생성("신논현역").as(StationResponse.class);
		int distance = 4;
		지하철_노선에_지하철역_등록_요청(신분당선.getId(), 신논현역.getId(), 강남역.getId(), distance);

		// then
		ExtractableResponse<Response> response = LineAcceptanceTest.지하철_노선_조회_요청(신분당선.getId());
		지하철_노선에_지하철역_등록됨(response, 신논현역, 강남역, 광교역);
	}

	@DisplayName("노선에 구간을 등록한다.(새로운 역을 하행 종점으로 등록)")
	@Test
	void addSectionNewDownStation() {
		// when
		// 지하철_노선에_지하철역_등록_요청
		StationResponse 호매실역 = StationAcceptanceTest.지하철역_생성("호매실역").as(StationResponse.class);
		int distance = 4;
		지하철_노선에_지하철역_등록_요청(신분당선.getId(), 광교역.getId(), 호매실역.getId(), distance);

		// then
		ExtractableResponse<Response> response = LineAcceptanceTest.지하철_노선_조회_요청(신분당선.getId());
		지하철_노선에_지하철역_등록됨(response, 강남역, 광교역, 호매실역);
	}

	private void 지하철_노선에_지하철역_등록됨(ExtractableResponse<Response> response, StationResponse... stationResponses) {
		List<Long> stationIds = Arrays.stream(stationResponses)
			.map(stationResponse -> stationResponse.getId())
			.collect(Collectors.toList());

		List<Long> actualStationIds = response.jsonPath().getList("stations", StationResponse.class).stream()
			.map(StationResponse::getId)
			.collect(Collectors.toList());

		assertThat(actualStationIds).containsAll(stationIds);
	}

	private ExtractableResponse<Response> 지하철_노선에_지하철역_등록_요청(Long lineId, Long upStationId, Long downStationId,
		int distance) {
		// when
		ExtractableResponse<Response> response = RestAssured
			.given().log().all()
			.body(new SectionRequest(upStationId, downStationId, distance))
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().post("/lines/{id}/sections", lineId)
			.then().log().all().extract();

		// then
		Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

		return response;
	}

}
