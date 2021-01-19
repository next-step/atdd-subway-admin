package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineResponse;
import org.hamcrest.MatcherAssert;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.jupiter.api.Assertions.assertAll;

public class LineResponseTestModule {

    public static void 응답코드_확인(ExtractableResponse<Response> response, int statusCode) {
        assertThat(response.statusCode()).isEqualTo(statusCode);
    }

    public static void 컨텐츠유형_확인(ExtractableResponse<Response> response, String contentType) {
        assertThat(response.header("Content-Type")).isEqualTo(contentType);
    }

    public static void 지하철_노선_생성및조회_검증(ExtractableResponse<Response> response) {
        assertAll(
                () -> assertThat(response.jsonPath().getLong("id")).isEqualTo(Long.parseLong(response.jsonPath().getString("id"))),
                () -> assertThat(response.jsonPath().getString("name")).isEqualTo(response.jsonPath().getString("name")),
                () -> assertThat(response.jsonPath().getString("color")).isEqualTo(response.jsonPath().getString("color")),
                () -> assertThat(response.jsonPath().getString("createdDate")).isNotNull(),
                () -> assertThat(response.jsonPath().getString("modifiedDate")).isNotNull()
        );
    }

    public static void 지하철_노선_목록_포함_검증(ExtractableResponse<Response> response) {
        response.jsonPath().getList(".", LineResponse.class)
                .forEach(line -> {
                    MatcherAssert.assertThat(line.getId(),
                            anyOf(equalTo(1L), equalTo(2L)));
                    MatcherAssert.assertThat(line.getName(),
                            either(containsString("1호선")).or(containsString("2호선")));
                    MatcherAssert.assertThat(line.getColor(),
                            either(containsString("파란색")).or(containsString("초록색")));
                    assertThat(line.getCreatedDate()).isNotNull();
                    assertThat(line.getModifiedDate()).isNotNull();
                });
    }

    public static void 지하철_노선_수정_검증(ExtractableResponse<Response> response,ExtractableResponse<Response> createLineResponse) {
        assertAll(
                () -> assertThat(response.jsonPath().getLong("id")).isEqualTo(Long.parseLong(response.jsonPath().getString("id"))),
                () -> assertThat(response.jsonPath().getString("name")).isEqualTo(response.jsonPath().getString("name")),
                () -> assertThat(response.jsonPath().getString("color")).isEqualTo(response.jsonPath().getString("color")),
                () -> assertThat(response.jsonPath().getString("createdDate")).isNotNull(),
                () -> assertThat(response.jsonPath().getString("modifiedDate")).isNotEqualTo(createLineResponse.jsonPath().getString("modifiedDate"))
        );
    }
}
