package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.station.StationAcceptanceTest;

@DisplayName("노선 관련 기능")
public class LineAcceptanceTest extends CustomExtractableResponse{
	public static final String BASIC_URL_LINES = "/lines";
	
	public Map<String, Long> stations = new HashMap<>();
    
	@BeforeEach
	public void setUp() {
		super.setUp();
		지하철_생성_요청("지하철역");
	    지하철_생성_요청("새로운지하철역");
	    지하철_생성_요청("또다른지하철역");
	    
	    stations.clear();
	    for(StationResponse s: 지하철_조회_요청()) {
	    	stations.put(s.getName(), s.getId());
	    }
	}

	/**
     * When 노선을 생성하면
     * Then 노선이 생성된다
     * Then 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> CreateResponse = 
        		노선_생성_요청("신분당선", "bg-red-600", stations.get("지하철역").toString(), stations.get("새로운지하철역").toString(), "10");
        long id = getId(CreateResponse);
        List<String> lines = 노선_리스트_이름_조회(); 

		// then
		assertAll(() -> assertThat(CreateResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
				() -> assertNotNull(id),
				() -> assertThat(lines).containsAnyOf("신분당선"));
    }   
    
    /**
     * Given 2개의 노선을 생성하고
     * When 노선 목록을 조회하면
     * Then 2개의 노선을 응답 받는다
     */
    @DisplayName("노선을 조회한다.")
    @Test
    void getLines() {
        // given
    	노선_생성_요청("신분당선", "bg-red-600", stations.get("지하철역").toString(), stations.get("새로운지하철역").toString(), "10");
    	노선_생성_요청("분당선", "bg-green-600", stations.get("지하철역").toString(), stations.get("또다른지하철역").toString(), "10");	
		
		// when
        ExtractableResponse<Response> response = get(BASIC_URL_LINES);
        List<String> lines = 노선_리스트_이름_조회();
        
		// then
		assertAll(() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
				() -> assertThat(lines).contains("신분당선"),
				() -> assertThat(lines).contains("분당선"));
    }    
    
    /**
     * Given 2개의 노선을 생성하고
     * When id값으로 노선을 조회하면
     * Then 원하는 노선을 응답 받는다.
     */
    @DisplayName("노선id로 원하는 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        long id = getId(
        		노선_생성_요청("신분당선", "bg-red-600", stations.get("지하철역").toString(), stations.get("새로운지하철역").toString(), "10"));
    	노선_생성_요청("분당선", "bg-green-600", stations.get("지하철역").toString(), stations.get("또다른지하철역").toString(), "10");	
		
        // when
		long findId = 노선_ID_조회(id)
				.jsonPath()
				.getLong("id");

		// then
		assertEquals(id, findId);
    }    

    /**
     * Given 2개의 노선을 생성하고
     * When 생성한 지하철 노선을 수정
     * Then 해당 지하철 노선 정보는 수정 확인
     */
    @DisplayName("원하는 노선의 정보를 수정한다")
    @Test
    void updateLine() {
        // given
    	long id = getId(
    			노선_생성_요청("신분당선", "bg-red-600", stations.get("지하철역").toString(), stations.get("새로운지하철역").toString(), "10"));
    	노선_생성_요청("분당선", "bg-green-600", stations.get("지하철역").toString(), stations.get("또다른지하철역").toString(), "10");	
		
        // when
    	ExtractableResponse<Response> createdLine = 노선_ID_조회(id);
		ExtractableResponse<Response> response = 노선_수정_요청(id, "다른분당선", "bg-red-800");
		ExtractableResponse<Response> findLine = 노선_ID_조회(id);
		findLine.jsonPath().getString("name");
		// then
		assertAll(() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
				() -> assertEquals(findLine.jsonPath().getLong("id"), findLine.jsonPath().getLong("id")),
				() -> assertNotEquals(createdLine.jsonPath().getString("name"), findLine.jsonPath().getString("name")),
				() -> assertNotEquals(createdLine.jsonPath().getString("color"), findLine.jsonPath().getString("color")));
    }
    
    /**
     * Given 노선을 생성하고
     * When 생성한 지하철 노선을 삭제
     * Then 해당 지하철 노선 정보는 수정 확인
     */
    @DisplayName("원하는 노선의 정보를 삭제한다")
    @Test
    void deleteLine() {
        // given
    	long id = getId(
    			노선_생성_요청("신분당선", "bg-red-600", stations.get("지하철역").toString(), stations.get("새로운지하철역").toString(), "10"));
		
        // when
		ExtractableResponse<Response> response = 노선_삭제_요청(id);

		// then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
	
	private List<StationResponse> 지하철_조회_요청() {
		return getList(get(StationAcceptanceTest.BASIC_URL_STATIONS), StationResponse.class);
	}
    
	private ExtractableResponse<Response> 지하철_생성_요청(String name) {
		Map<String, String> params = new HashMap<>();
		params.put("name", name);
		return post(StationAcceptanceTest.BASIC_URL_STATIONS, params);
	}

	private List<String> 노선_리스트_이름_조회() {
		return get(BASIC_URL_LINES)
				.jsonPath()
					.getList("name", String.class);
	}

	private ExtractableResponse<Response> 노선_ID_조회(Long id) {
		String url = joinUrl(BASIC_URL_LINES, id);
		return get(url);
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
		return post(BASIC_URL_LINES, params);
	}

	private ExtractableResponse<Response> 노선_수정_요청(
			Long id, 
			String name, 
			String color) {
		Map<String, String> params = new HashMap<>();
		params.put("name", name);
		params.put("color", color);

		String url = joinUrl(BASIC_URL_LINES, id);
		return put(url, params);
	}

	private ExtractableResponse<Response> 노선_삭제_요청(Long id) {
		String url = joinUrl(BASIC_URL_LINES, id);
		return delete(url);
	}
}
