package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import nextstep.subway.common.dto.ErrorResponse;
import nextstep.subway.common.exception.DuplicateAllStationException;
import nextstep.subway.common.exception.IllegalDistanceException;
import nextstep.subway.common.exception.NotExistAllStationException;
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

	@DisplayName("노선에 구간등록시 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
	@Test
	void addSectionSameUpStationIllegalDistanceException() {
		// when
		StationResponse 판교역 = StationAcceptanceTest.지하철역_생성("판교역").as(StationResponse.class);
		int distance = 10;
		ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), 강남역.getId(), 판교역.getId(),
			distance);

		// then
		지하철_노선_구간_등록_길이_오류(response);
	}

	@DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
	@Test
	void duplicateAllStationException() {
		// when
		int distance = 5;
		ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), 강남역.getId(), 광교역.getId(),
			distance);

		// then
		지하철_노선_구간_상행선_하행선_중복_오류(response);
	}

	@DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
	@Test
	void notExistAllStationException() {
		// when
		StationResponse 서울역 = StationAcceptanceTest.지하철역_생성("서울역").as(StationResponse.class);
		StationResponse 호매실역 = StationAcceptanceTest.지하철역_생성("호매실역").as(StationResponse.class);
		int distance = 5;
		ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), 서울역.getId(), 호매실역.getId(),
			distance);

		// then
		지하철_노선_구간_상행선_하행선_없음_오류(response);
	}

	@DisplayName("노선의 구간 제거(종점 제거시 다음으로 오던 역이 종점이 됨)")
	@Test
	void removeDownStationOnSection(){
		// given
		// 지하철_노선에_지하철역_등록_요청
		StationResponse 판교역 = StationAcceptanceTest.지하철역_생성("판교역").as(StationResponse.class);
		int distance = 4;
		지하철_노선에_지하철역_등록_요청(신분당선.getId(), 강남역.getId(), 판교역.getId(), distance);

		//when
		지하철_노선의_지하철역_삭제_요청(신분당선.getId(), 광교역.getId());

		//then
		ExtractableResponse<Response> response = LineAcceptanceTest.지하철_노선_조회_요청(신분당선.getId());
		지하철_노선의_상행선_하행선_조회(response, 강남역.getId(), 판교역.getId());
	}

	private void 지하철_노선의_상행선_하행선_조회(ExtractableResponse<Response> response, Long... stationIds) {
		List<Long> actualStationIds = response.jsonPath().getList("stations", StationResponse.class).stream()
			.map(StationResponse::getId)
			.collect(Collectors.toList());

		// then
		assertThat(actualStationIds).containsAll(Arrays.asList(stationIds));
	}

	private void 지하철_노선의_지하철역_삭제_요청(Long lineId, Long stationId) {
		// when
		ExtractableResponse<Response> response = RestAssured
		        .given().log().all()
		        .contentType(MediaType.APPLICATION_JSON_VALUE)
		        .when().delete("/lines/" + lineId + "/sections?stationId=" + stationId)
		        .then().log().all().extract();
		
		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		
	}

	private void 지하철_노선_구간_상행선_하행선_없음_오류(ExtractableResponse<Response> response) {
		String errorCode = response.jsonPath().getObject(".", ErrorResponse.class).getErrorCode();
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		assertThat(errorCode).isEqualTo(NotExistAllStationException.ERROR_CODE);
	}

	private void 지하철_노선_구간_상행선_하행선_중복_오류(ExtractableResponse<Response> response) {
		String errorCode = response.jsonPath().getObject(".", ErrorResponse.class).getErrorCode();
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		assertThat(errorCode).isEqualTo(DuplicateAllStationException.ERROR_CODE);
	}

	private void 지하철_노선_구간_등록_길이_오류(ExtractableResponse<Response> response) {
		String errorCode = response.jsonPath().getObject(".", ErrorResponse.class).getErrorCode();
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		assertThat(errorCode).isEqualTo(IllegalDistanceException.ERROR_CODE);
	}

	private void 지하철_노선에_지하철역_등록됨(ExtractableResponse<Response> response, StationResponse... stationResponses) {
		List<Long> stationIds = Arrays.stream(stationResponses)
			.map(stationResponse -> stationResponse.getId())
			.collect(Collectors.toList());

		List<Long> actualStationIds = response.jsonPath().getList("stations", StationResponse.class).stream()
			.map(StationResponse::getId)
			.collect(Collectors.toList());

		// then
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

		return response;
	}

}
