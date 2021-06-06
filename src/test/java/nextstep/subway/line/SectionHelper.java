package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import nextstep.subway.line.dto.SectionRequest;
import org.springframework.http.MediaType;

public class SectionHelper {

    public static ExtractableResponse 섹션_추가(SectionRequest sectionRequest, Long lineId) {
        return RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/" + lineId + "/sections/")
                .then().log().all().extract();
    }
}
