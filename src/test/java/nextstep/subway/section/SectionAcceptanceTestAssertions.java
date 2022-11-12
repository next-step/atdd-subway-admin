package nextstep.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ResponseBodyExtractionOptions;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class SectionAcceptanceTestAssertions {

    static final String ID = "id";
    static final String DISTANCE = "distance";
    static final String SECTIONS = "sections";

    static void 구간_등록됨(ExtractableResponse<Response> 생성_응답) {
        assertThat(생성_응답)
                .extracting(ExtractableResponse::body)
                .extracting(ResponseBodyExtractionOptions::jsonPath)
                .extracting(jsonPath -> jsonPath.getLong(ID))
                .isNotNull();
    }

    static void 구간_거리_등록됨(ExtractableResponse<Response> 생성_응답, int 거리) {
        Map<String, String> 응답_본문 = 생성_응답.body().jsonPath().getMap(SECTIONS + "[0]", String.class, String.class);

        assertThat(응답_본문)
                .extracting(map -> map.get(DISTANCE)).isEqualTo(거리 + "");
    }
}
