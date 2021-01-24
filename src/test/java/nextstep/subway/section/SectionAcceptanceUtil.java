package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.section.dto.SectionRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("노선 구간 관련 Util")
public class SectionAcceptanceUtil {

    public static ExtractableResponse<Response> 지하철_노선_구간_등록_요청(long lineId, SectionRequest sectionRequest) {
        return RestAssured
                .given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/"+lineId+"/sections")
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_삭제_요청(Long lineId, long stationId) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/lines/"+lineId+"/sections?stationId="+stationId)
                .then().log().all().extract();
    }
}
