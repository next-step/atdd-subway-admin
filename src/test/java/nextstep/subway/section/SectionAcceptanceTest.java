package nextstep.subway.section;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.CustomExtractableResponse;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.station.StationAcceptanceTest;

@DisplayName("구간 관련 기능")
public class SectionAcceptanceTest extends CustomExtractableResponse{
	private static final String BASIC_URL_SECTIONS = "/sections";

	public Map<String, Long> stations = new HashMap<>();
	public Map<String, Long> lines = new HashMap<>();
	
	
	@BeforeEach
	public void setUp() {
		super.setUp();
		지하철_생성_요청("지하철역");
	    지하철_생성_요청("새로운지하철역");
	    지하철_생성_요청("또다른지하철역");
	    
	    stations.clear();
	    for(StationResponse station: 지하철_조회_요청()) {
	    	stations.put(station.getName(), station.getId());
	    }
	    
	    노선_생성_요청("신분당선", "bg-red-600", stations.get("지하철역").toString(), stations.get("새로운지하철역").toString(), "10");
	    
	    lines.clear();
	    for(LineResponse line: 노선_조회_요청()) {
	    	lines.put(line.getName(), line.getId());
	    }
	}
	
	@DisplayName("역 사이에 새로운 역을 등록한다.")
	@Test
	void addSection() {
		구간_등록_요청(lines.get("신분당선"), stations.get("지하철역").toString(), stations.get("새로운지하철역").toString(), "4");
	}
	
	private ExtractableResponse<Response> 지하철_생성_요청(String name) {
		Map<String, String> params = new HashMap<>();
		params.put("name", name);
		return post(StationAcceptanceTest.BASIC_URL_STATIONS, params);
	}

	private List<StationResponse> 지하철_조회_요청() {
		return getList(get(StationAcceptanceTest.BASIC_URL_STATIONS), StationResponse.class);
	}

	private ExtractableResponse<Response> 노선_생성_요청(
			String name, 
			String color, 
			String upStationId, 
			String downStationId, 
			String distance) {
		Map<String, String> params = new HashMap<>();
		params.put("name", name);
		params.put("color", color);
		params.put("upStationId", upStationId);
		params.put("downStationId", downStationId);
		params.put("distance", distance);
		return post(LineAcceptanceTest.BASIC_URL_LINES, params);
	}

	private List<LineResponse> 노선_조회_요청() {
		return getList(get(LineAcceptanceTest.BASIC_URL_LINES), LineResponse.class);
	}
	
	private ExtractableResponse<Response> 구간_등록_요청(
			Long id, 
			String upStationId, 
			String downStationId, 
			String distance) {
		Map<String, String> params = new HashMap<>();
		params.put("upStationId", upStationId);
		params.put("downStationId", downStationId);
		params.put("distance", distance);
		return post(basicUrlSection(id), params);
	}

	private String basicUrlSection(Long id) {
		return joinUrl(
				joinUrl(LineAcceptanceTest.BASIC_URL_LINES, 
						id), 
				BASIC_URL_SECTIONS);
	}
}
