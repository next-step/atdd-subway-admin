package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
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

public class SectionAcceptanceTest extends AcceptanceTest {

	private LineResponse 신분당선;
	private StationResponse 강남역;
	private StationResponse 광교역;
	private StationResponse 양재역;
	private Map<String, String> createParams;

	@BeforeEach
	public void setUp() {
		super.setUp();

		// given
		강남역 = StationAcceptanceTest.지하철역_등록되어_있음("강남역").as(StationResponse.class);
		광교역 = StationAcceptanceTest.지하철역_등록되어_있음("광교역").as(StationResponse.class);
		양재역 = StationAcceptanceTest.지하철역_등록되어_있음("양재역").as(StationResponse.class);

		createParams = new HashMap<>();
		createParams.put("name", "신분당선");
		createParams.put("color", "bg-red-600");
		createParams.put("upStationId", 강남역.getId() + "");
		createParams.put("downStationId", 광교역.getId() + "");
		createParams.put("distance", 10 + "");
		신분당선 = 지하철_노선_등록되어_있음(createParams).as(LineResponse.class);
	}

	private ExtractableResponse<Response> 지하철_노선_등록되어_있음(Map<String, String> params) {
		return TestFactory.create(LineController.BASE_URI, params);
	}

	@Test
	@DisplayName("노선에 구간을 등록한다.")
	void addSection() {
		// given
		Map<String, String> params = new HashMap<>();
		params.put("upStationId", 강남역.getId() + "");
		params.put("downStationId", 양재역.getId() + "");
		params.put("distance", 1 + "");

		// when
		ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선, params);

		// then
		지하철_노선에_지하철역_등록됨(response);
	}

	private void 지하철_노선에_지하철역_등록됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
	}

	private ExtractableResponse<Response> 지하철_노선에_지하철역_등록_요청(LineResponse line, Map<String, String> params) {
		String uri = LineController.BASE_URI + "/" + line.getId() + "/sections";
		return TestFactory.create(uri, params);
	}

}
