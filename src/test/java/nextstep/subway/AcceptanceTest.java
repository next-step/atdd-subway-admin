package nextstep.subway;

import io.restassured.RestAssured;
import io.restassured.internal.path.ObjectConverter;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.utils.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "test")
public class AcceptanceTest {

    @LocalServerPort
    int port;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
            databaseCleanup.afterPropertiesSet();
        }

        databaseCleanup.execute();
    }

    protected List<ExtractableResponse<Response>> 목록을_저장한다(Object[] bodies, String url) {
        List<ExtractableResponse<Response>> responseList = new ArrayList<>();
        for (Object body : bodies) {
            responseList.add(저장한다(body, url));
        }
        return responseList;
    }

    protected ExtractableResponse<Response> 저장한다(Object body, String url) {
        return given().body(body)
            .when()
            .post(url)
            .then().log().all()
            .extract();
    }

    protected ExtractableResponse<Response> 조회한다(String url) {
        return given()
            .when()
            .get(url)
            .then().log().all()
            .extract();
    }

    protected ExtractableResponse<Response> 수정한다(Object body, String url) {
        return given()
            .body(body)
            .when()
            .put(url)
            .then().log().all()
            .extract();
    }

    protected ExtractableResponse<Response> 삭제한다(String url) {
        return given()
            .when()
            .delete(url)
            .then().log().all()
            .extract();
    }

    protected <T> List<T> getIdsByResponse(List<ExtractableResponse<Response>> responses,
        Class<T> genericType) {
        if (genericType == null) {
            throw new IllegalArgumentException("타입지정은 필수입니다.");
        }
        return responses.stream()
            .map(it -> ObjectConverter
                .convertObjectTo(it.header("Location").split("/")[2], genericType))
            .collect(Collectors.toList());
    }

    protected Long getLongIdByResponse(ExtractableResponse<Response> response) {
        return response.body().jsonPath().getLong("id");
    }

    private RequestSpecification given() {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE);
    }
}
