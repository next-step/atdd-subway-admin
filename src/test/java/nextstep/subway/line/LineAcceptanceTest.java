package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.MethodMode;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.comm.CustomExtractableResponse;
import nextstep.subway.dto.LineResponse;

@DisplayName("노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(methodMode = MethodMode.BEFORE_METHOD)
public class LineAcceptanceTest {
	private static final String BASIC_URL_LINES = "/lines";

    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;
    
	@LocalServerPort
	int port;

//    @Sql({"/sql/station/insert.sql"})
	@BeforeEach
	private void setUp() {
		if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
			RestAssured.port = port;
		}
	    EntityManager em = entityManagerFactory.createEntityManager();
	    em.getTransaction().begin();
	    em.createNativeQuery("delete from Station where id in (1, 2, 3)").executeUpdate();
	    em.createNativeQuery("insert into Station(id, name) values (1, '지하철역'), (2, '새로운지하철역'), (3, '또다른지하철역')").executeUpdate();
	    em.getTransaction().commit();
	}
	
	@AfterEach
	private void cleanup() {
	    EntityManager em = entityManagerFactory.createEntityManager();
	    em.getTransaction().begin();
	    em.createNativeQuery("truncate table Line").executeUpdate();
	    em.createNativeQuery("ALTER TABLE Line ALTER COLUMN id RESTART WITH 1").executeUpdate();
	    em.getTransaction().commit();
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
        ExtractableResponse<Response> CreateResponse = 노선_생성_요청("신분당선", "bg-red-600", "1", "2", "10");
        LineResponse createdLine =  CustomExtractableResponse.getObject(CreateResponse, LineResponse.class);
        List<LineResponse> lines = 노선_리스트_조회(); 

		// then
		assertAll(() -> assertThat(CreateResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
				() -> assertNotNull(createdLine.getId()),
				() -> assertEquals(createdLine.getId(), lines.get(0).getId()));
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
    	노선_생성_요청("신분당선", "bg-red-600", "1", "2", "10");
    	노선_생성_요청("분당선", "bg-green-600", "1", "3", "10");	
		
		// when
        ExtractableResponse<Response> response = CustomExtractableResponse.get(BASIC_URL_LINES);
        List<LineResponse> lines = 노선_리스트_조회();

		// then
		assertAll(() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
				() -> assertThat(lines).hasSize(2));
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
		LineResponse createdLine = CustomExtractableResponse
				.getObject(노선_생성_요청("신분당선", "bg-red-600", "1", "2", "10"), LineResponse.class);
		노선_생성_요청("분당선", "bg-green-600", "1", "3", "10");
		
        // when
		LineResponse line = 노선_ID_조회(createdLine.getId());

		// then
		assertEquals(createdLine.getId(), line.getId());
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
    	LineResponse createdLine = CustomExtractableResponse.getObject(노선_생성_요청("신분당선", "bg-red-600", "1", "2", "10"), LineResponse.class);
    	노선_생성_요청("분당선", "bg-green-600", "1", "3", "10");
		
        // when
		ExtractableResponse<Response> response = 노선_수정_요청(createdLine, "다른분당선", "bg-red-800");
		LineResponse line = 노선_ID_조회(createdLine.getId());
	    
		// then
		assertAll(() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
				() -> assertEquals(createdLine.getId(), line.getId()),
				() -> assertNotEquals(createdLine.getName(), line.getName()),
				() -> assertNotEquals(createdLine.getColor(), line.getColor()));
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
    	LineResponse createdLine = CustomExtractableResponse.getObject(노선_생성_요청("신분당선", "bg-red-600", "1", "2", "10"), LineResponse.class);
		
        // when
		ExtractableResponse<Response> response = 노선_삭제_요청(createdLine);

		// then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

	private List<LineResponse> 노선_리스트_조회() {
		return CustomExtractableResponse.getList(CustomExtractableResponse.get(BASIC_URL_LINES), LineResponse.class);
	}

	private LineResponse 노선_ID_조회(Long id) {
		String url = CustomExtractableResponse.joinUrl(BASIC_URL_LINES, id);
		return CustomExtractableResponse.getObject(CustomExtractableResponse.get(url), LineResponse.class);
	}

	private ExtractableResponse<Response> 노선_생성_요청(String name, String color, String upStationId, String downStationId, String distance) {
		Map<String, String> params = new HashMap<>();
		params.put("name", name);
		params.put("color", color);
		params.put("upStationId", upStationId);
		params.put("downStationId", downStationId);
		params.put("distance", distance);
		return CustomExtractableResponse.post(BASIC_URL_LINES, params);
	}

	private ExtractableResponse<Response> 노선_수정_요청(LineResponse createdLine, String name, String color) {
		Map<String, String> params = new HashMap<>();
		params.put("name", name);
		params.put("color", color);

		String url = CustomExtractableResponse.joinUrl(BASIC_URL_LINES, createdLine.getId());
		return CustomExtractableResponse.put(url, params);
	}

	private ExtractableResponse<Response> 노선_삭제_요청(LineResponse createdLine) {
		String url = CustomExtractableResponse.joinUrl(BASIC_URL_LINES, createdLine.getId());
		return CustomExtractableResponse.delete(url);
	}
}
