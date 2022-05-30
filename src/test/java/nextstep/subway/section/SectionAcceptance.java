package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.Map;
import nextstep.subway.section.dto.SectionResponse;
import org.springframework.http.MediaType;

public class SectionAcceptance {

    public static ExtractableResponse<Response> 지하철_구간_등록(Long 노선_id, Map<String, Object> params) {
        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines/{lineId}/sections" , 노선_id)
            .then().log().all()
            .extract();
    }

    public static SectionResponse ID가_상행역인_노선(Long id, List<SectionResponse> sections) {
        return sections.stream()
            .filter( x -> x.getUpStationId() == id)
            .findAny()
            .orElseThrow(null);
    }
}
