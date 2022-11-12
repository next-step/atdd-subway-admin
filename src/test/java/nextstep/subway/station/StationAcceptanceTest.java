package nextstep.subway.station;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StationAcceptanceTest {
    private static final String 숫자_REGEX = "\\d+";
    @LocalServerPort
    int port;

    private static ExtractableResponse<Response> 지하철역_생성_요청(final String name) {
        Map<String, String> params1 = new HashMap<>();
        params1.put("name", name);

        return RestAssured.given().log().all()
                .body(params1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract();
    }

    private static List<String> 등록된_모든_지하철역_이름_조회() {
        return RestAssured.given().log().all()
                .when().get("/stations")
                .then().log().all()
                .extract().jsonPath().getList("name");
    }

    private static ExtractableResponse<Response> 지하철역_삭제_요청(String path) {
        return RestAssured.given().log().all()
                .when().delete(path)
                .then().log().all()
                .extract();
    }

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
    }

    /**
     * When 지하철역을 생성하면 Then 지하철역이 생성된다 Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> response = 지하철역_생성_요청("강남역");

        // then
        지하철역이_생성됨(response);

        // then
        List<String> 등록된_모든_지하철역_이름_목록 = 등록된_모든_지하철역_이름_조회();
        목록에_주어진_역이_포함됨(등록된_모든_지하철역_이름_목록, "강남역");
    }

    private void 지하철역이_생성됨(ExtractableResponse<Response> response) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.header("Location")).matches("/stations/" + 숫자_REGEX)
        );
    }

    private void 목록에_주어진_역이_포함됨(List<String> 등록된_모든_지하철역_이름_목록, String 지하철역_이름) {
        assertThat(등록된_모든_지하철역_이름_목록).containsAnyOf(지하철역_이름);
    }

    /**
     * Given 지하철역을 생성하고 When 기존에 존재하는 지하철역 이름으로 지하철역을 생성하면 Then 지하철역 생성이 안된다
     */
    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        지하철역_생성_요청("강남역");

        // when
        ExtractableResponse<Response> response = 지하철역_생성_요청("강남역");

        // then
        지하철역이_생성되지_않음(response);
    }

    private void 지하철역이_생성되지_않음(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 2개의 지하철역을 생성하고 When 지하철역 목록을 조회하면 Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        // given
        지하철역_생성_요청("방이역");
        지하철역_생성_요청("올림픽공원역");

        // when
        List<String> 등록된_모든_지하철역_이름_목록 = 등록된_모든_지하철역_이름_조회();

        // then
        지하철역이_목록에_있음(등록된_모든_지하철역_이름_목록, "방이역", "올림픽공원역");
    }

    private void 지하철역이_목록에_있음(List<String> 등록된_모든_지하철역_이름_목록, String... 주어진_이름_목록) {
        assertThat(등록된_모든_지하철역_이름_목록).containsExactlyInAnyOrderElementsOf(Arrays.asList(주어진_이름_목록));
    }

    /**
     * Given 지하철역을 생성하고 When 그 지하철역을 삭제하면 Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> createdResponse = 지하철역_생성_요청("철원역");
        String 철원역_경로 = createdResponse.header("Location");

        // when
        ExtractableResponse<Response> response = 지하철역_삭제_요청(철원역_경로);

        // then
        지하철역이_제거됨(response);
    }

    private void 지하철역이_제거됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
