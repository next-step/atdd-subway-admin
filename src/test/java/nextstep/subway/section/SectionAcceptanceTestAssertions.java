package nextstep.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class SectionAcceptanceTestAssertions {

    static final String DISTANCE = "distance";
    static final String SECTIONS = "sections";

    static void 구간_등록됨(ExtractableResponse<Response> 노선_응답) {
        List<Object> 구간 = 노선_응답.body().jsonPath().getList(SECTIONS);
        assertThat(구간.isEmpty()).isFalse();
    }

    static void 구간_등록_실패함(ExtractableResponse<Response> 노선_응답) {
        assertThat(노선_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    static void 구간_거리_등록됨(ExtractableResponse<Response> 노선_응답, int 거리) {
        Map<String, String> 응답_본문 = 노선_응답.body().jsonPath().getMap(SECTIONS + "[0]", String.class, String.class);

        assertThat(응답_본문)
                .extracting(map -> map.get(DISTANCE)).isEqualTo(거리 + "");
    }

    static void 총_구간_거리_합이_같음(ExtractableResponse<Response> 노선_응답, int 기존_구간거리) {
        노선_응답.body().jsonPath().getMap(SECTIONS + "[0]", String.class, String.class);
    }
}
