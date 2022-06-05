package nextstep.subway.acceptance.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.station.Station;
import nextstep.subway.dto.line.LineRequest;
import nextstep.subway.dto.line.PutLineRequest;
import org.springframework.http.MediaType;

public final class Line {
    private Line() {
    }

    static Long 지하철역_생성(String stationName) {
        ExtractableResponse<Response> response = Station.지하철역_추가(stationName);
        return response.jsonPath().getLong("id");
    }

    static ExtractableResponse<Response> 지하철노선_생성(LineRequest request) {
        return RestAssured.given().log().all().body(request).contentType(MediaType.APPLICATION_JSON_VALUE).when()
                .post("/lines").then().log().all().extract();
    }

    static ExtractableResponse<Response> 지하철노선_아이디로_조회(Long lineId) {
        return RestAssured.given().log().all().when().get("/lines/" + lineId).then().log().all().extract();
    }

    static ExtractableResponse<Response> 지하철노선_전체_목록_조회() {
        return RestAssured.given().log().all().when().get("/lines").then().log().all().extract();
    }

    static ExtractableResponse<Response> 지하철노선_정보_수정(Long lineId, PutLineRequest request) {
        return RestAssured.given().log().all().body(request).contentType(MediaType.APPLICATION_JSON_VALUE).when()
                .put("/lines/" + lineId).then().log().all().extract();
    }

    static ExtractableResponse<Response> 지하철노선_아이디로_삭제(Long lineId) {
        return RestAssured.given().log().all().when().delete("/lines/" + lineId).then().log().all().extract();
    }
}
