package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.dto.StationResponse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    public static final Map<String, String> 신도림역 = new HashMap<String, String>() {{
        put("name", "신도림역");
    }};

    public static final Map<String, String> 서울역 = new HashMap<String, String>() {{
        put("name", "서울역");
    }};

    public static final Map<String, String> 강남역 = new HashMap<String, String>() {{
        put("name", "강남역");
    }};

    public static final Map<String, String> 역삼역 = new HashMap<String, String>() {{
        put("name", "역삼역");
    }};

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> response = 지하철_역_생성_요청(강남역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStation2_WithDuplicateName_ExceptionThrown() {
        // given
        지하철_역_등록되어_있음(강남역);

        // when
        ExtractableResponse<Response> response = 지하철_역_생성_요청(강남역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("잘못된 요청은 지하철 역 생성을 실패한다.")
    @Test
    void creatStation3_EmptyString_ExceptionThrown() {
        // given
        Map<String, String> wrongParams = new HashMap<String, String>() {{
            put("", "");
        }};

        // when
        ExtractableResponse<Response> response = 지하철_역_생성_요청(wrongParams);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("빈 값이 있으면 지하철 노선 생성을 실패한다.")
    @Test
    void creatStation4_EmptyString_ExceptionThrown() {
        // given
        Map<String, String> wrongParams = new HashMap<String, String>() {{
            put("name", "");
        }};

        // when
        ExtractableResponse<Response> response = 지하철_역_생성_요청(wrongParams);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        ExtractableResponse<Response> createResponse1 = 지하철_역_등록되어_있음(강남역);
        ExtractableResponse<Response> createResponse2 = 지하철_역_등록되어_있음(역삼역);

        // when
        ExtractableResponse<Response> response = 지하철_역_목록_조회_요청();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
            .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
            .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", StationResponse.class).stream()
            .map(StationResponse::getId)
            .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> createResponse = 지하철_역_등록되어_있음(강남역);

        // when
        ExtractableResponse<Response> response = 지하철_역_제거_요청(createResponse.header("Location"));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("없는 지하철 역을 제거한다.")
    @Test
    void deleteStation_NotFound_ExceptionThrown() {
        // when
        ExtractableResponse<Response> response = 지하철_역_제거_요청("/stations/1");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private static ExtractableResponse<Response> 지하철_역_생성_요청(Map<String, String> station) {
        return RestAssured.given().log().all()
            .body(station)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_역_등록되어_있음(Map<String, String> station) {
        return 지하철_역_생성_요청(station);
    }

    private ExtractableResponse<Response> 지하철_역_목록_조회_요청() {
        return RestAssured
            .given().log().all()
            .when()
            .get("/stations")
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 지하철_역_제거_요청(String uri) {
        return RestAssured
            .given().log().all()
            .when()
            .delete(uri)
            .then().log().all()
            .extract();
    }

}
