package nextstep.subway;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.springframework.http.HttpStatus;

public class TestHelper {

    public static void 생성됨_확인(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    public static void 삭제됨_확인(ExtractableResponse<Response> 삭제_응답) {
        assertEquals(HttpStatus.NO_CONTENT.value(), 삭제_응답.statusCode());
    }

    public static void 이름_불포함_확인(ExtractableResponse<Response> response, String... names) {
        List<String> responseNames = response.body().jsonPath().getList("name", String.class);
        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.statusCode()),
            () -> assertThat(responseNames).doesNotContainSequence(names)
        );
    }

    public static long getId(ExtractableResponse<Response> 노선_생성_응답) {
        return 노선_생성_응답.body().jsonPath().getLong("id");
    }

}
