package nextstep.subway.section;

import static nextstep.subway.factory.LineFactory.*;
import static nextstep.subway.factory.StationFactory.*;
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
import nextstep.subway.station.dto.StationResponse;

public class SectionAcceptanceTest extends AcceptanceTest {
	private StationResponse 강남역;
	private StationResponse 광교역;
	private LineResponse 신분당선;

	@BeforeEach
	public void setUp() {
		super.setUp();

		// given
		강남역 = 지하철역_생성("강남역").as(StationResponse.class);
		광교역 = 지하철역_생성("광교역").as(StationResponse.class);

		HashMap createParams = new HashMap<>();
		createParams.put("name", "신분당선");
		createParams.put("color", "bg-red-600");
		createParams.put("upStationId", 강남역.getId());
		createParams.put("downStationId", 광교역.getId());
		createParams.put("distance", 10);

		신분당선 = 라인_생성(createParams).as(LineResponse.class);
	}

	@DisplayName("노선에 구간을 등록한다.")
	@Test
	void 구간등록_성공() {
		// given
		final StationResponse 판교역 = 지하철역_생성("판교역").as(StationResponse.class);

		Map<String, Object> params = new HashMap<>();
		params.put("upStationId", 판교역.getId());
		params.put("downStationId", 광교역.getId());
		params.put("distance", 5);

		// when
		// 지하철_노선에_구간_등록_요청
		final ExtractableResponse<Response> response = 지하철_구간_등록_요청(params);

		// then
		// 지하철_노선에_구간_등록됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
	}

	private ExtractableResponse<Response> 지하철_구간_등록_요청(Map<String, Object> params) {
		return RestAssured.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines/{lineId}/sections", 신분당선.getId())
			.then().log().all()
			.extract();
	}

	@DisplayName("노선에 동일 구간을 등록한다.")
	@Test
	void 동일구간등록() {
		// given
		Map<String, Object> params = new HashMap<>();
		params.put("upStationId", 강남역.getId());
		params.put("downStationId", 광교역.getId());
		params.put("distance", 8);

		// when
		// 지하철_노선에_구간_등록_요청
		final ExtractableResponse<Response> response = 지하철_구간_등록_요청(params);

		// then
		// 지하철_노선에_구간등록_실패됨
		지하철_노선에_구간등록_실패됨(response);
	}

	@DisplayName("노선에 상행 또는 하행 역이 겹치는 구간에 더 긴 거리를 등록한다.")
	@Test
	void 먼거리등록() {
		// given
		final StationResponse 판교역 = 지하철역_생성("판교역").as(StationResponse.class);

		Map<String, Object> params = new HashMap<>();
		params.put("upStationId", 판교역.getId());
		params.put("downStationId", 광교역.getId());
		params.put("distance", 15);

		// when
		// 지하철_노선에_구간_등록_요청
		final ExtractableResponse<Response> response = 지하철_구간_등록_요청(params);

		// then
		// 지하철_노선에_구간등록_실패됨
		지하철_노선에_구간등록_실패됨(response);
	}

	@DisplayName("노선에 상행 하행 둘다 존재하지 않는 역에 대한 구간을 등록한다.")
	@Test
	void 없는역_구간_등록() {
		// given
		final StationResponse 판교역 = 지하철역_생성("판교역").as(StationResponse.class);
		final StationResponse 상현역 = 지하철역_생성("상현역").as(StationResponse.class);

		Map<String, Object> params = new HashMap<>();
		params.put("upStationId", 판교역.getId());
		params.put("downStationId", 상현역.getId());
		params.put("distance", 5);

		// when
		// 지하철_노선에_구간_등록_요청
		final ExtractableResponse<Response> response = 지하철_구간_등록_요청(params);

		// then
		// 지하철_노선에_구간등록_실패됨
		지하철_노선에_구간등록_실패됨(response);
	}

	private void 지하철_노선에_구간등록_실패됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
}
