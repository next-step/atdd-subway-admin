package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private Map<String, String> 지하철_1호선;
    private Map<String, String> 지하철_2호선;

    @BeforeEach
    public void setUp() {
        super.setUp();

        지하철_1호선 = 지하철_노선_종점포함_정보("1호선", "남색", StationAcceptanceTest.신도림역, StationAcceptanceTest.서울역, 10);
        지하철_2호선 = 지하철_노선_종점포함_정보("2호선", "녹색", StationAcceptanceTest.강남역, StationAcceptanceTest.역삼역, 10);
    }

    @DisplayName("지하철 노선을 종점과 함께 생성한다.")
    @Test
    void createLine_withEndpoint() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_종점포함_생성_요청(지하철_1호선);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2_WithDuplicateName_ExceptionThrown() {
        // given
        지하철_노선_종점포함_생성_요청(지하철_1호선);

        // when
        ExtractableResponse<Response> response = 지하철_노선_종점포함_생성_요청(지하철_1호선);

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("잘못된 요청은 지하철 노선 생성을 실패한다.")
    @Test
    void createLine3_EmptyString_ExceptionThrown() {
        // given
        Map<String, String> wrongParams = new HashMap<String, String>() {{
            put("", "");
        }};

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(wrongParams);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("빈 값이 있으면 지하철 노선 생성을 실패한다.")
    @Test
    void createLine4_EmptyString_ExceptionThrown() {
        // given
        Map<String, String> wrongParams = new HashMap<String, String>() {{
            put("name", "1호선");
            put("color", "");
        }};

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(wrongParams);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        ExtractableResponse<Response> createResponse1 = 지하철_노선_종점포함_등록되어_있음(지하철_1호선);
        ExtractableResponse<Response> createResponse2 = 지하철_노선_종점포함_등록되어_있음(지하철_2호선);

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        // 지하철_노선_목록_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        // 지하철_노선_목록_포함됨
        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
            .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
            .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
            .map(LineResponse::getId)
            .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        ExtractableResponse<Response> createResponse = 지하철_노선_종점포함_등록되어_있음(지하철_1호선);
        int lineId = createResponse.jsonPath().getObject(".", LineResponse.class).getId().intValue();

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(lineId);

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getObject(".", LineResponse.class).getName()).isEqualTo("1호선");
        assertThat(response.jsonPath().getObject(".", LineResponse.class).getStations().size()).isEqualTo(2);
    }

    @DisplayName("없는 지하철 노선을 조회한다.")
    @Test
    void getLine_NotFound_ExceptionThrown() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(1);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> createResponse = 지하철_노선_종점포함_등록되어_있음(지하철_1호선);
        int lineId = createResponse.jsonPath().getObject(".", LineResponse.class).getId().intValue();

        // when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(lineId, 지하철_2호선);

        // then
        // 지하철_노선_수정됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getObject(".", LineResponse.class).getId()).isEqualTo(1);
        assertThat(response.jsonPath().getObject(".", LineResponse.class).getName()).isEqualTo("2호선");
        assertThat(response.jsonPath().getObject(".", LineResponse.class).getColor()).isEqualTo("녹색");
        assertThat(response.jsonPath().getObject(".", LineResponse.class).getCreatedDate()).isNotEqualTo(
            response.jsonPath().getObject(".", LineResponse.class).getModifiedDate());
    }

    @DisplayName("없는 지하철 노선을 수정한다.")
    @Test
    void updateLine_NotFound_ExceptionThrown() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(1, 지하철_2호선);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> createResponse = 지하철_노선_종점포함_등록되어_있음(지하철_1호선);

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(createResponse.header("Location"));

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("없는 지하철 노선을 제거한다.")
    @Test
    void deleteLine_NotFound_ExceptionThrown() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청("/lines/1");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static Map<String, String> 지하철_노선_종점포함_정보(
        String name, String color, Map<String, String> upStation, Map<String, String> downStation, int distance
    ) {
        ExtractableResponse<Response> upStationCreateResponse = StationAcceptanceTest.지하철_역_등록되어_있음(upStation);
        ExtractableResponse<Response> downStationCreateResponse = StationAcceptanceTest.지하철_역_등록되어_있음(downStation);

        Long upStationId = upStationCreateResponse.jsonPath().getObject(".", StationResponse.class).getId();
        Long downStationId = downStationCreateResponse.jsonPath().getObject(".", StationResponse.class).getId();

        return new HashMap<String, String>() {{
            put("name", name);
            put("color", color);
            put("upStationId", String.valueOf(upStationId));
            put("downStationId", String.valueOf(downStationId));
            put("distance", String.valueOf(distance));
        }};
    }

    private static ExtractableResponse<Response> 지하철_노선_생성_요청(Map<String, String> line) {
        return RestAssured
            .given().log().all()
            .body(line)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines")
            .then().log().all()
            .extract();
    }

    private static ExtractableResponse<Response> 지하철_노선_종점포함_생성_요청(Map<String, String> line) {
        return 지하철_노선_생성_요청(line);
    }

    public static ExtractableResponse<Response> 지하철_노선_종점포함_등록되어_있음(Map<String, String> line) {
        return 지하철_노선_종점포함_생성_요청(line);
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured
            .given().log().all()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/lines")
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_조회_요청(int id) {
        return RestAssured
            .given().log().all()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get(String.format("/lines/%d", id))
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(int id, Map<String, String> line) {
        return RestAssured
            .given().log().all()
            .body(line)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put(String.format("/lines/%d", id))
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_제거_요청(String uri) {
        return RestAssured
            .given().log().all()
            .when()
            .delete(uri)
            .then().log().all()
            .extract();
    }
}
