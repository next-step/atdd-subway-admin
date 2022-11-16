package nextstep.subway.utils;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class CommonTestFixture {
    public static final String STATION_BASE_PATH = "/stations";
    public static final String LINE_BASE_PATH = "/lines";
    public static final String PATH_VARIABLE_ID = "/{id}";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String COLOR = "color";

    public static ExtractableResponse<Response> 생성(String path, Map<String, Object> params) {
        return given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(path)
                .then().log().all()
                .extract();
    }

    public static List<String> 목록_조회(String path) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get(path)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().jsonPath().getList(NAME, String.class);
    }

    public static ExtractableResponse<Response> 상세_조회(String path, long getId) {
        return given().log().all()
                .pathParam(ID, getId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get(path + PATH_VARIABLE_ID)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    public static ExtractableResponse<Response> 수정(String path, Map<String, Object> params, long updateId) {
        return given().log().all()
                .pathParam(ID, updateId)
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(path + PATH_VARIABLE_ID)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    public static ExtractableResponse<Response> 삭제(String path, long deleteId) {
        return given().log().all()
                .pathParam(ID, deleteId)
                .when().delete(path + PATH_VARIABLE_ID)
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .extract();
    }

    public static long 응답_ID_추출(ExtractableResponse<Response> response) {
        return response.jsonPath().getLong(ID);
    }

    public static void HTTP_상태코드_검증(int statusCode, HttpStatus httpStatus) {
        assertThat(statusCode).isEqualTo(httpStatus.value());
    }

    public static void 목록_검증_존재함(List<String> actualNames, String[] expectNames) {
        assertThat(actualNames).contains(expectNames);
    }

    public static void 목록_검증_존재하지_않음(List<String> actualNames, String[] stationNames) {
        assertThat(actualNames).doesNotContain(stationNames);
    }
}
