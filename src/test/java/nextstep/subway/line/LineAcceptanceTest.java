package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.domain.Line;

@DisplayName("노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {

	@LocalServerPort
	int port;

	@BeforeEach
	public void setUp() {
		if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
			RestAssured.port = port;
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
        Map<String, String> params = new HashMap<>();
        params.put("name", "1호선");
        params.put("color", "파랑색");

        ExtractableResponse<Response> response =
                RestAssured
                	.given()
                		.log()
                		.all()
                        .body(params)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    	.post("/lines")
                    .then()
                    	.log()
                    	.all()
					.extract();

		// then
		assertAll(() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
				() -> assertNotNull(response.jsonPath().getObject(".", Line.class).getId()),
				() -> assertEquals(response.jsonPath().getObject(".", Line.class).getName(), "1호선"),
				() -> assertEquals(response.jsonPath().getObject(".", Line.class).getColor(), "파랑색"));
    }   
    
    /**
     * Given 2개의 노선을 생성하고
     * When 노선 목록을 조회하면
     * Then 2개의 노선을 응답 받는다
     */
    @DisplayName("노선을 조회한다.")
    @Test
    void getLines() {
        // when
        Map<String, String> params = new HashMap<>();
        params.put("name", "1호선");
        params.put("color", "파랑색");

		RestAssured
			.given()
				.body(params)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
				.post("/lines")
			.then();
		
		params.clear();
        params.put("name", "2호선");
        params.put("color", "초록색");
        
        RestAssured
			.given()
				.body(params)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
				.post("/lines")
			.then();
		
		// when
        ExtractableResponse<Response> response =
                RestAssured
                	.given()
                		.log()
                		.all()
                    .when()
                    	.get("/lines")
                    .then()
                    	.log()
                    	.all()
                    .extract();


		// then
		assertAll(() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
				() -> assertThat(response.jsonPath().getList(".", Line.class)).hasSize(2),
				() -> assertEquals(response.jsonPath().getList(".", Line.class).get(0).getName(), "1호선"),
				() -> assertEquals(response.jsonPath().getList(".", Line.class).get(0).getColor(), "파랑색"),
				() -> assertEquals(response.jsonPath().getList(".", Line.class).get(1).getName(), "2호선"),
				() -> assertEquals(response.jsonPath().getList(".", Line.class).get(1).getColor(), "초록색"));
    }    
    
    /**
     * Given 2개의 노선을 생성하고
     * When id값으로 노선을 조회하면
     * Then 원하는 노선을 응답 받는다.
     */
    @DisplayName("노선id로 원하는 노선을 조회한다.")
    @Test
    void getLine() {
        // when
        Map<String, String> params = new HashMap<>();
        params.put("name", "1호선");
        params.put("color", "파랑색");

		RestAssured
			.given()
				.body(params)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
				.post("/lines")
			.then();
		
		params.clear();
        params.put("name", "2호선");
        params.put("color", "초록색");
        
		Long id = RestAssured
				.given()
					.body(params)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
				.when()
					.post("/lines")
				.then()
				.extract()
					.jsonPath()
						.getObject(".", Line.class)
							.getId();
		
        // when
		String url = "/lines" + "/" + id;
        ExtractableResponse<Response> response =
                RestAssured
                	.given()
                		.log()
                		.all()
                    .when()
                    	.get(url)
                    .then()
                    	.log()
                    	.all()
                    .extract();

		// then
		assertAll(() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
				() -> assertEquals(response.jsonPath().getObject(".", Line.class).getId(), id),
				() -> assertEquals(response.jsonPath().getObject(".", Line.class).getName(), "2호선"),
				() -> assertEquals(response.jsonPath().getObject(".", Line.class).getColor(), "초록색"));
    }    

    /**
     * Given 2개의 노선을 생성하고
     * When 생성한 지하철 노선을 수정
     * Then 해당 지하철 노선 정보는 수정 확인
     */
    @DisplayName("원하는 노선의 정보를 수정한다")
    @Test
    void putLine() {
        // when
        Map<String, String> params = new HashMap<>();
        params.put("name", "1호선");
        params.put("color", "파랑색");

		RestAssured
			.given()
				.body(params)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
				.post("/lines")
			.then();
		
		params.clear();
        params.put("name", "2호선");
        params.put("color", "초록색");
        
		Long id = RestAssured
				.given()
					.body(params)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
				.when()
					.post("/lines")
				.then()
				.extract()
					.jsonPath()
						.getObject(".", Line.class)
							.getId();
		
        // when
		params.clear();
        params.put("name", "3호선");
        params.put("color", "주황색");
        
		String url = "/lines" + "/" + id;
        ExtractableResponse<Response> response =
                RestAssured
                	.given()
                		.log()
                		.all()
                        .body(params)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    	.put(url)
                    .then()
                    	.log()
                    	.all()
                    .extract();

		// then
		assertAll(() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
				() -> assertEquals(response.jsonPath().getObject(".", Line.class).getId(), id),
				() -> assertEquals(response.jsonPath().getObject(".", Line.class).getName(), "3호선"),
				() -> assertEquals(response.jsonPath().getObject(".", Line.class).getColor(), "주황색"));
    }  
    
    /**
     * Given 노선을 생성하고
     * When 생성한 지하철 노선을 삭제
     * Then 해당 지하철 노선 정보는 수정 확인
     */
    @DisplayName("원하는 노선의 정보를 삭제한다")
    @Test
    void deleteLine() {
        // when
        Map<String, String> params = new HashMap<>();
        params.put("name", "1호선");
        params.put("color", "파랑색");
        
		Long id = RestAssured
				.given()
					.body(params)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
				.when()
					.post("/lines")
				.then()
				.extract()
					.jsonPath()
						.getObject(".", Line.class)
							.getId();
		
        // when
		String url = "/lines" + "/" + id;
        ExtractableResponse<Response> response =
                RestAssured
                	.given()
                		.log()
                		.all()
                        .body(params)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    	.delete(url)
                    .then()
                    	.log()
                    	.all()
                    .extract();

		// then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }  
}
