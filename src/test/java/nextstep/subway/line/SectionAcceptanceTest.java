package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.ui.LineController;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.utils.TestFactory;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

	private LineResponse 신분당선;
	private StationResponse 강남역;
	private StationResponse 광교역;
	private StationResponse 양재역;
	private StationResponse 새상행역;
	private StationResponse 새하행역;
	private Map<String, String> createParams;

	@BeforeEach
	public void setUp() {
		super.setUp();

		// given
		강남역 = StationAcceptanceTest.지하철역_등록되어_있음("강남역").as(StationResponse.class);
		광교역 = StationAcceptanceTest.지하철역_등록되어_있음("광교역").as(StationResponse.class);
		양재역 = StationAcceptanceTest.지하철역_등록되어_있음("양재역").as(StationResponse.class);
		새상행역 = StationAcceptanceTest.지하철역_등록되어_있음("새상행역").as(StationResponse.class);
		새하행역 = StationAcceptanceTest.지하철역_등록되어_있음("새하행역").as(StationResponse.class);
		신분당선 = 지하철_노선_등록되어_있음("신분당선", 강남역, 광교역, 10);
	}

	@Test
	@DisplayName("구간 사이에 구간을 추가한다")
	void addSection() {
		// when
		ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선, 강남역, 양재역, 1);

		// then
		지하철_노선에_지하철역_등록_응답됨(response, Arrays.asList(강남역, 양재역, 광교역));
	}

	@Test
	@DisplayName("상행 종점에 새 구간을 추가한다")
	void addSection2() {
		// when
		ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선, 새상행역, 강남역, 1);

		// then
		지하철_노선에_지하철역_등록_응답됨(response, Arrays.asList(새상행역, 강남역, 광교역));
	}

	@Test
	@DisplayName("하행 종점에 새 구간을 추가한다")
	void addSection3() {
		// when
		ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선, 광교역, 새하행역, 1);

		// then
		지하철_노선에_지하철역_등록_응답됨(response, Arrays.asList(강남역, 광교역, 새하행역));
	}

	@Test
	@DisplayName("노선에서 상행종점을 삭제한다")
	void removeSection1() {
		// given
		지하철_노선에_지하철역_추가됨(신분당선, 광교역, 양재역, 1);

		// when
		ExtractableResponse<Response> response = 지하철_노선에_지하철역_삭제_요청(신분당선, 강남역);

		// then
		지하철_노선에_지하철역_삭제_응답됨(response);
	}

	@Test
	@DisplayName("노선에서 하행종점을 삭제한다")
	void removeSection2() {
		// given
		지하철_노선에_지하철역_추가됨(신분당선, 광교역, 양재역, 1);

		// when
		ExtractableResponse<Response> response = 지하철_노선에_지하철역_삭제_요청(신분당선, 양재역);

		// then
		지하철_노선에_지하철역_삭제_응답됨(response);
	}

	@Test
	@DisplayName("노선에서 중간역을 삭제한다")
	void removeSection3() {
		// given
		지하철_노선에_지하철역_추가됨(신분당선, 광교역, 양재역, 1);

		// when
		ExtractableResponse<Response> response = 지하철_노선에_지하철역_삭제_요청(신분당선, 광교역);

		// then
		지하철_노선에_지하철역_삭제_응답됨(response);
	}

	@Test
	@DisplayName("노선에 해당 역이 없을 경우, 삭제 안됨")
	void removeSection4() {
		// when
		ExtractableResponse<Response> response = 지하철_노선에_지하철역_삭제_요청(신분당선, 양재역);

		// then
		지하철_노선에_지하철역_삭제_안됨(response);
	}

	@Test
	@DisplayName("노선에 구간이 하나일 경우, 삭제 안됨")
	void removeSection5() {
		// when
		ExtractableResponse<Response> response = 지하철_노선에_지하철역_삭제_요청(신분당선, 강남역);

		// then
		지하철_노선에_지하철역_삭제_안됨(response);
	}

	private LineResponse 지하철_노선_등록되어_있음(String lineName, StationResponse upStation,
		StationResponse downStation, int distance) {
		Map<String, String> params = new HashMap<>();
		params.put("name", lineName);
		params.put("color", "bg-red-600");
		params.put("upStationId", upStation.getId() + "");
		params.put("downStationId", downStation.getId() + "");
		params.put("distance", distance + "");
		return TestFactory.create(LineController.BASE_URI, params).as(LineResponse.class);
	}

	private void 지하철_노선에_지하철역_등록_응답됨(ExtractableResponse<Response> response, List<StationResponse> expectedStations) {
		List<StationResponse> stations = response.as(LineResponse.class).getStations();
		assertAll(
			() -> assertThat(stations).containsAll(expectedStations),
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value())
		);
	}

	private ExtractableResponse<Response> 지하철_노선에_지하철역_삭제_요청(LineResponse line, StationResponse station) {
		String uri = LineController.BASE_URI + "/" + line.getId() + "/sections?stationId=" + station.getId();
		return TestFactory.delete(uri);
	}

	private void 지하철_노선에_지하철역_삭제_응답됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	private void 지하철_노선에_지하철역_삭제_안됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	private ExtractableResponse<Response> 지하철_노선에_지하철역_등록_요청(LineResponse line, StationResponse upStation,
		StationResponse downStation,
		int distance) {
		Map<String, String> params = new HashMap<>();
		params.put("upStationId", upStation.getId() + "");
		params.put("downStationId", downStation.getId() + "");
		params.put("distance", distance + "");

		String uri = LineController.BASE_URI + "/" + line.getId() + "/sections";
		return TestFactory.create(uri, params);
	}

	private LineResponse 지하철_노선에_지하철역_추가됨(LineResponse line, StationResponse upStation, StationResponse downStation,
		int distance) {
		Map<String, String> params = new HashMap<>();
		params.put("upStationId", upStation.getId() + "");
		params.put("downStationId", downStation.getId() + "");
		params.put("distance", distance + "");

		String uri = LineController.BASE_URI + "/" + line.getId() + "/sections";
		return TestFactory.create(uri, params).as(LineResponse.class);
	}

}
