package nextstep.subway.line;

import static nextstep.subway.station.StationAcceptanceFixture.지하철_생성_결과에서_지하철역_번호를_조회한다;
import static nextstep.subway.station.StationAcceptanceFixture.지하철_역을_생성한다;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineUpdateRequest;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.StationResponse;
import org.springframework.http.MediaType;

public class LineAcceptanceFixture {

    public static LineRequest 노선_요청(String 노선명, String 노션_색깔, String 상행_지하철역, String 하행_지하철역) {
        return 노선_요청(노선명, 노션_색깔, 상행_지하철역, 하행_지하철역, 100L);
    }

    public static LineRequest 노선_요청(String 노선명, String 노션_색깔, String 상행_지하철역, String 하행_지하철역, Long 거리) {
        ExtractableResponse<Response> 상행_지하철역_생성_결과 = 지하철_역을_생성한다(상행_지하철역);
        ExtractableResponse<Response> 하행_지하철역_생성_결과 = 지하철_역을_생성한다(하행_지하철역);
        Long 상행_지하철역_번호 = 지하철_생성_결과에서_지하철역_번호를_조회한다(상행_지하철역_생성_결과);
        Long 하행_지하철역_번호 = 지하철_생성_결과에서_지하철역_번호를_조회한다(하행_지하철역_생성_결과);
        return new LineRequest(노선명, 노션_색깔, 상행_지하철역_번호, 하행_지하철역_번호, 거리);
    }


    public static LineUpdateRequest 노선_수정_요청(String 노선명, String 노션_색깔) {
        return new LineUpdateRequest(노선명, 노션_색깔);
    }


    public static String 노선_결과에서_노선_이름을_조회한다(ExtractableResponse<Response> 노선_생성_결과) {
        return 노선_생성_결과.jsonPath()
                .getString("name");
    }

    public static Long 노선_결과에서_노선_아이디를_조회한다(ExtractableResponse<Response> 노선_생성_결과) {
        return 노선_생성_결과.jsonPath()
                .getLong("id");
    }

    public static String 노선_결과에서_노선_색깔을_조회한다(ExtractableResponse<Response> 노선_생성_결과) {
        return 노선_생성_결과.jsonPath()
                .getString("color");
    }

    public static List<String> 노선_목록을_조회한다() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract().jsonPath().getList("name", String.class);

    }

    public static ExtractableResponse<Response> 특정_노선을_조회한다(Long 노선_아이디) {
        return RestAssured.given().log().all()
                .pathParam("id", 노선_아이디)
                .when().get("/lines/{id}")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 노선을_생성한다(LineRequest 노선_요청_정보) {
        return RestAssured.given().log().all()
                .body(노선_요청_정보)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 노선을_수정한다(Long 노선_아이디, LineUpdateRequest 노선_수정_정보) {
        return RestAssured.given().log().all()
                .body(노선_수정_정보)
                .pathParam("id", 노선_아이디)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/{id}")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 노선을_삭제한다(Long 노선_아이디) {
        return RestAssured.given().log().all()
                .pathParam("id", 노선_아이디)
                .when().delete("/lines/{id}")
                .then().log().all()
                .extract();
    }

    public static SectionRequest 구간_요청_정보(Long 상행역_번호, Long 하행역_번호) {
        return new SectionRequest(상행역_번호, 하행역_번호, 1L);
    }

    public static SectionRequest 구간_요청_정보(Long 상행역_번호, Long 두정역_번호, Long 길이) {
        return new SectionRequest(상행역_번호, 두정역_번호, 길이);
    }

    public static List<Long> 구간_생성_결과에서_지하철역_번호들을_조회한다(ExtractableResponse<Response> 구간_생성_결과) {
        return 구간_생성_결과.jsonPath().getList("stations", StationResponse.class)
                .stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
    }


    public static ExtractableResponse<Response> 노선에_구간을_생성한다(Long 노선_아이디, SectionRequest 구간_요청_정보) {
        return RestAssured.given().log().all()
                .body(구간_요청_정보)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("id", 노선_아이디)
                .when().post("lines/{id}/sections")
                .then().log().all()
                .extract();
    }
}
