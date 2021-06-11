package nextstep.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.section.dto.SectionResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.CREATED;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {
    public static final String BASE_PATH = "/sections";

    @DisplayName("구간생성")
    @Test
    public void 구간생성시_구간생성확인() throws Exception {
        //given
        Map<String, String> params = 구간생성파라미터(1000);

        //when
        ExtractableResponse<Response> extractableResponse = 구간생성요청(params);

        //then
        구간생성됨(extractableResponse);
    }

    public static SectionResponse 구간_생성(int distance) {
        Map<String, String> params = 구간생성파라미터(distance);
        ExtractableResponse<Response> response = 구간생성요청(params);
        구간생성됨(response);

        return response.jsonPath().getObject(".", SectionResponse.class);
    }

    private static Map<String, String> 구간생성파라미터(int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("distance", String.valueOf(distance));
        return params;
    }

    private static void 구간생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(CREATED.value());
    }

    private static ExtractableResponse<Response> 구간생성요청(Map<String, String> params) {
        ExtractableResponse<Response> response =
                given()
                        .log().all()
                        .body(params)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                        .post(BASE_PATH)
                .then()
                        .log().all()
                        .extract();
        return response;
    }
}
