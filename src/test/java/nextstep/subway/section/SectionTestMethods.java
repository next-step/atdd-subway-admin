package nextstep.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.SectionRequest;

import static nextstep.subway.testutils.RestAssuredMethods.post;

public class SectionTestMethods {
    public static final String URI_SECTIONS = "/lines/%s/sections";
    
    public static ExtractableResponse<Response> 구간_추가(Long id, SectionRequest sectionRequest) {
        return post(String.format(URI_SECTIONS, id), sectionRequest);
    }
}
