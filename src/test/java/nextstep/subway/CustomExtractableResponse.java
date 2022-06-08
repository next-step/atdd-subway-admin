package nextstep.subway;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomExtractableResponse {

	@LocalServerPort
	protected int port;
	
	@PersistenceContext
	private EntityManager entityManager;

    @Autowired
    private DatabaseCleanup databaseCleanup;
    
    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        databaseCleanup.execute();
    }

	public <T> long getId(ExtractableResponse<Response> response) {
		return response
				.jsonPath()
				.getLong("id");
	}
	
	public <T> T getObject(ExtractableResponse<Response> response, Class<T> type) {
		return response
				.jsonPath()
				.getObject(".", type);
	}
	
	public <T> List<T> getList(ExtractableResponse<Response> response, Class<T> type) {
		return response
				.jsonPath()
				.getList(".", type);
	}
	
	public ExtractableResponse<Response> get(String url) {
		return RestAssured
				.given()
					.port(port)
					.log().all()
				.when()
					.get(url)
				.then()
					.log().all()
				.extract();
	}
	
	public ExtractableResponse<Response> post(String url, Map<String, String> params) {
		return RestAssured
				.given()
					.port(port)
				    .body(params)
				    .contentType(MediaType.APPLICATION_JSON_VALUE)
				    .log().all()
				.when()
					.post(url)
				.then()
					.log().all()
				.extract();
	}
	
	public ExtractableResponse<Response> put(String url, Map<String, String> params) {
		return RestAssured
            	.given()
					.port(port)
	                .body(params)
	                .contentType(MediaType.APPLICATION_JSON_VALUE)
	        		.log().all()
	            .when()
	            	.put(url)
	            .then()
	            	.log()
	            	.all()
	            .extract();
	}
    
	public ExtractableResponse<Response> delete(String url) {
		return RestAssured
				.given()
					.port(port)
					.log().all()
				.when()
					.delete(url)
				.then()
					.log().all()
				.extract();
	}
	
	public <T> String joinUrl(String url, T value) {
		return url + "/" + value.toString();
	}
}