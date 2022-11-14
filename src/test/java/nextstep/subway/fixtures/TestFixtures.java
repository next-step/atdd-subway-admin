package nextstep.subway.fixtures;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.Map;
import nextstep.subway.DatabaseCleanup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class TestFixtures {

    @LocalServerPort
    int port;

    @Autowired
    DatabaseCleanup databaseCleanup;

    protected void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        databaseCleanup.execute();
    }

    protected ExtractableResponse<Response> 생성(Map<String, String> paramMap, String path) {
        return RestAssured.given().log().all()
                .body(paramMap)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(path)
                .then().log().all()
                .extract();
    }

    protected String 생성_값_리턴(Map<String, String> paramMap, String path, String value) {
        return 생성(paramMap, path).jsonPath().getString(value);
    }

    protected List<String> 목록조회(String information, String path) {
        return RestAssured.given().log().all()
                .when().get(path)
                .then().log().all()
                .extract().jsonPath().getList(information, String.class);
    }

    protected String 조회(String path, String pathVariable, String information) {
        return RestAssured.given().log().all()
                .when().get(path, pathVariable)
                .then().log().all()
                .extract().jsonPath().getString(information);
    }

    protected ExtractableResponse<Response> 수정(Map<String, String> paramMap, String path, String pathVariable) {
        return RestAssured.given().log().all()
                .body(paramMap)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(path, pathVariable)
                .then().log().all()
                .extract();
    }

    protected ExtractableResponse<Response> 삭제(String path, String pathVariable) {
        return RestAssured.given().log().all()
                .when().delete(path, pathVariable)
                .then().log().all()
                .extract();
    }
}