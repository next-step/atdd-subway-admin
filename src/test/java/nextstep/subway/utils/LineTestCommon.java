package nextstep.subway.utils;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineRequest;
import org.springframework.http.MediaType;

public class LineTestCommon {

    /**
     * Line 파라미터 생성
     * @param name
     * @param color
     * @return LineRequest params
     */
    public static LineRequest createLineParams(String name, String color, Long upStationId, Long downStationId, Long distance) {
        return new LineRequest(name, color, upStationId, downStationId, distance);
    }

    /**
     * 지하철 노선 생성 요청
     * @param params
     * @param path
     * @return ExtractableResponse<Response>
     */
    public static ExtractableResponse<Response> createResponse(LineRequest params, String path) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(path)
                .then().log().all()
                .extract();
    }

    /**
     * 전체 조회 요청
     * @param path
     * @return ExtractableResponse<Response>
     */
    public static ExtractableResponse<Response> findAllResponse(String path) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(path)
                .then().log().all()
                .extract();
    }

    /**
     * 지하철 노선 조회 요청
     * @param id
     * @return ExtractableResponse<Response>
     */
    public static ExtractableResponse<Response> findOneResponse(Long id) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines/" + id)
                .then()
                .log()
                .all()
                .extract();
    }

    /**
     * 지하철 노선 수정 요청
     * @param id
     * @param request
     * @return ExtractableResponse<Response>
     */
    public static ExtractableResponse<Response> updateResponse(LineRequest request, Long id) {
        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + id)
                .then().log().all()
                .extract();
    }

    /**
     * 지하철 노선 제거 요청
     * @param id
     * @return ExtractableResponse<Response>
     */
    public static ExtractableResponse<Response> deleteResponse(Long id) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/lines/" + id)
                .then().log().all()
                .extract();
    }

}
