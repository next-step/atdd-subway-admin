package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class LineTestFixture {
    public static ExtractableResponse<Response> requestCreateLine(String name, String color, Long upStationId, Long downStationId, int distance){
        LineRequest lineRequest = LineRequest.of(name, color, upStationId, downStationId, distance );
        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract();
    }

    public static ExtractableResponse<Response> requestGetAllLine(){
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    public static ExtractableResponse<Response> requestGetLine(Long id){
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines/"+ id)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    public static void requestUpdateLine(Long id,LineUpdateRequest params){
        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().put("/lines/"+ id)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    public static void requestDeleteLine(Long id){
        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/lines/"+ id)
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .extract();
    }


}
