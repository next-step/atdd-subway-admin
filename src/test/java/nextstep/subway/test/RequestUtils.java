package nextstep.subway.test;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;

public class RequestUtils {

    private static final String PATH_VARIABLE_ID = "/{id}";

    public static ExtractableResponse<Response> requestDeleteById(String path, long id) {
        return RestAssured.given().log().all()
                .when().delete(path + PATH_VARIABLE_ID, id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> requestDeleteById(String path,Map<String,Object> queryParams ,long id) {
        return RestAssured.given().log().all()
                .queryParams(queryParams)
                .when().delete(path, id)
                .then().log().all()
                .extract();
    }

    public static List<ExtractableResponse<Response>> requestCreateBundle(List<Map<String, Object>> paramsBundle,
                                                                          String path) {
        List<ExtractableResponse<Response>> responses = new ArrayList<>();
        for (Map<String, Object> params : paramsBundle) {
            responses.add(requestCreate(params, path));
        }
        return responses;
    }

    public static ExtractableResponse<Response> requestCreate(Map<String, Object> params, String path) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().post(path)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> requestCreate(long lineId, Map<String, Object> sectionParams,
                                                              String path) {
        return RestAssured.given().log().all()
                .body(sectionParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.ALL_VALUE)
                .when().post(path, lineId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> requestGetAll(String path) {
        return RestAssured.given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get(path)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> requestGetById(String path, long lineId) {
        return RestAssured.given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get(path + PATH_VARIABLE_ID, lineId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> requestUpdateById(String path, long id, Map<String, Object> params) {
        return RestAssured.given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().put(path + PATH_VARIABLE_ID, id)
                .then().log().all()
                .extract();
    }

}
