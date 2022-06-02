package nextstep.subway.api;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.Map;

public class BaseApi {
    protected final String baseUrl;

    public BaseApi(String url) {
        this.baseUrl = url;
    }

    public String urlWithId(Long id) {
        return String.format(this.baseUrl + "/%d", id);
    }

    protected ExtractableResponse<Response> create(Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(this.baseUrl)
                .then().log().all().extract();
    }

    protected ExtractableResponse<Response> findById(Long id) {
        return RestAssured.given().log().all()
                .when().get(urlWithId(id))
                .then().log().all()
                .extract();
    }

    protected ExtractableResponse<Response> findAll() {
        return RestAssured.given().log().all()
                .when().get(this.baseUrl)
                .then().log().all()
                .extract();
    }

    protected ExtractableResponse<Response> update(Long id, Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(urlWithId(id))
                .then().log().all().extract();
    }

    protected ExtractableResponse<Response> delete(Long id) {
        return RestAssured.given().log().all()
                .when().delete(urlWithId(id))
                .then().log().all().extract();
    }
}
