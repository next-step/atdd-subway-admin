package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class LineApi {
    public static ExtractableResponse<Response> 지하철노선_생성(LineRequest lineRequest) {
        final ExtractableResponse<Response> 지하철노선_생성_응답 =
                RestAssured.given().log().all()
                        .body(lineRequest)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().post("/line")
                        .then().log().all()
                        .extract();

        지하철노선_생성_검증(지하철노선_생성_응답);

        return 지하철노선_생성_응답;
    }

    public static void 지하철노선_생성_검증(ExtractableResponse<Response> 지하철노선_생성_응답) {
        ApiAcceptance.API응답_검증(지하철노선_생성_응답.statusCode(), HttpStatus.CREATED.value());
    }

    public static ExtractableResponse<Response> 지하철노선_조회() {
        final ExtractableResponse<Response> 지하철노선_조회_응답 =
                RestAssured.given().log().all()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().get("/line")
                        .then().log().all()
                        .extract();

        지하철노선_조회_검증(지하철노선_조회_응답);

        return 지하철노선_조회_응답;
    }

    public static void 지하철노선_조회_검증(ExtractableResponse<Response> 지하철노선_조회_응답) {
        ApiAcceptance.API응답_검증(지하철노선_조회_응답.statusCode(), HttpStatus.OK.value());
    }

    public static ExtractableResponse<Response> 지하철노선_수정(Long lineId, LineRequest lineRequest) {
        final ExtractableResponse<Response> 지하철노선_수정_응답 =
                RestAssured.given().log().all()
                        .body(lineRequest)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().put("/line/" + lineId)
                        .then().log().all()
                        .extract();

        지하철노선_수정_검증(지하철노선_수정_응답);

        return 지하철노선_수정_응답;
    }

    public static void 지하철노선_수정_검증(ExtractableResponse<Response> 지하철노선_수정_응답) {
        ApiAcceptance.API응답_검증(지하철노선_수정_응답.statusCode(), HttpStatus.OK.value());
    }

    public static ExtractableResponse<Response> 지하철노선_상세_조회(Long lineId) {
        final ExtractableResponse<Response> 지하철노선_상세_응답 =
                RestAssured.given().log().all()
                        .when().get("/line/" + lineId)
                        .then().log().all()
                        .extract();

        지하철노선_상세_검증(지하철노선_상세_응답);
        return 지하철노선_상세_응답;
    }

    public static void 지하철노선_상세_검증(ExtractableResponse<Response> 지하철노선_상세_응답) {
        ApiAcceptance.API응답_검증(지하철노선_상세_응답.statusCode(), HttpStatus.OK.value());
    }

    public static void 지하철노선_삭제(Long lineId) {
        ExtractableResponse<Response> 지하철노선_삭제_응답 =
                RestAssured.given().log().all()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().delete("/line/" + lineId)
                        .then().log().all()
                        .extract();

        지하철노선_삭제_검증(지하철노선_삭제_응답);
    }

    public static void 지하철노선_삭제_검증(ExtractableResponse<Response> 지하철노선_삭제_응답) {
        ApiAcceptance.API응답_검증(지하철노선_삭제_응답.statusCode(), HttpStatus.NO_CONTENT.value());
    }
}
