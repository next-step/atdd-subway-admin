package nextstep.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class SectionAcceptanceTestAssertions {

    private static String STATIONS = "stations";
    private static String NAME = "name";

    static void 구간_등록됨(ExtractableResponse<Response> 노선_응답, List<String> 지하철역_목록) {
        assertThat(노선_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(노선_응답.body().jsonPath().getList(STATIONS + "." + NAME, String.class))
                .isEqualTo(지하철역_목록);
    }

    static void 구간_등록_실패함(ExtractableResponse<Response> 노선_응답) {
        assertThat(노선_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
