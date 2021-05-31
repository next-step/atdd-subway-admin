package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import nextstep.subway.line.dto.LineRequest;
import org.springframework.http.MediaType;

public class LineHelper {

    public static ExtractableResponse 노선_생성(LineRequest lineRequest) {

        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all().extract();
    }

    public static LineRequest 노선_요청_정보(String name, String color) {
        return new LineRequest(name, color);
    }
}
