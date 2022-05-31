package nextstep.subway.section;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.CustomExtractableResponse;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.station.StationAcceptanceTest;

@DisplayName("구간 관련 기능")
public class SectionAcceptanceTest extends CustomExtractableResponse{
	private static final String BASIC_URL_SECTIONS = "sections";

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
	    ExtractableResponse<Response> response = 노선_조회_요청();
	    List<Map<String, Object>> lines = response.jsonPath().getList("");
	    for(int i=0;i<lines.size();i++) {
	    	this.lines.put(lines.get(i).get("name").toString(), Long.parseLong(lines.get(i).get("id").toString()));
	    }
	}

	@DisplayName("역 사이에 새로운 구간을 등록한다.")
	@Test
	void addSection() {
		ExtractableResponse<Response> CreateResponse = 
				구간_등록_요청(lines.get("신분당선"), stations.get("지하철역").toString(), stations.get("또다른지하철역").toString(), "4");
		노선_조회_요청();
		assertThat(CreateResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	@DisplayName("출발구간으로 등록한다.")
	@Test
	void addFirstSection() {
		ExtractableResponse<Response> CreateResponse = 
				구간_등록_요청(lines.get("신분당선"), stations.get("또다른지하철역").toString(), stations.get("지하철역").toString(), "4");
		assertThat(CreateResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	@DisplayName("마지막구간으로 등록한다.")
	@Test
	void addLastSection() {
		ExtractableResponse<Response> CreateResponse = 
				구간_등록_요청(lines.get("신분당선"), stations.get("새로운지하철역").toString(), stations.get("또다른지하철역").toString(), "4");
		assertThat(CreateResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	@DisplayName("구간길이가 같거나 큰 구간을 등록할수없다.")
	@Test
	void addSectionSameOrBigDistance() {
		ExtractableResponse<Response> CreateResponse = 
				구간_등록_요청(lines.get("신분당선"), stations.get("지하철역").toString(), stations.get("또다른지하철역").toString(), "10");
		assertThat(CreateResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
	
	@DisplayName("상행역과 하행역이 모두 같으면 등록할수없다.")
	@Test
	void addSectionSameSection() {
		ExtractableResponse<Response> CreateResponse = 
				구간_등록_요청(lines.get("신분당선"), stations.get("지하철역").toString(), stations.get("새로운지하철역").toString(), "4");
		assertThat(CreateResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
	
	@DisplayName("정보를 알수없는 지하철역은 등록할수없다.")
	@Test
	void addSectionNotFoundStation() {
		ExtractableResponse<Response> CreateResponse = 
				구간_등록_요청(lines.get("신분당선"), stations.get("지하철역").toString(), "15", "4");
		assertThat(CreateResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
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

	private  ExtractableResponse<Response> 노선_조회_요청() {
		return get(LineAcceptanceTest.BASIC_URL_LINES);
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
		return post(requestUrl(id), params);
	}
	
	private String requestUrl(Long id) {
		return LineAcceptanceTest.BASIC_URL_LINES + "/" + id + "/" + BASIC_URL_SECTIONS;
	}
}
