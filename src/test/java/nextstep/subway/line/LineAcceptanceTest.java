package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.dto.StationRequest;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        지하철역_여러_생성();
        LineRequest lineRequest = new LineRequest("1호선", "blue", 1L, 2L, 10);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_등록(lineRequest);

        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.jsonPath().getList("stations").stream().collect(Collectors.toList()).size()).isEqualTo(2);

    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        지하철역_여러_생성();
        LineRequest lineRequest = new LineRequest("잠실역", "Green", 1L, 2L, 10);
        지하철_노선_등록(lineRequest);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_등록(lineRequest);

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @TestFactory
    Stream<DynamicTest> getLines() {
        return Stream.of(
                // given
                // 지하철_노선_등록되어_있음
                DynamicTest.dynamicTest("여러 노선을 생성한다.", () -> {
                    //when
                    지하철역_여러_생성();
                    ExtractableResponse<Response> createFirstLine = 지하철_노선_등록(new LineRequest("1호선", "blue", 1L, 2L, 10));
                    ExtractableResponse<Response> createSecondLine = 지하철_노선_등록(new LineRequest("2호선", "green", 3L, 4L, 10));
                    ExtractableResponse<Response> createThirdLine = 지하철_노선_등록(new LineRequest("3호선", "orange", 5L, 6L, 10));

                    //then
                    assertThat(createFirstLine.statusCode()).isEqualTo(HttpStatus.CREATED.value());
                    assertThat(createSecondLine.statusCode()).isEqualTo(HttpStatus.CREATED.value());
                    ExtractableResponse<Response> searchResponse = 지하철_목록_조회();
                    assertThat(searchResponse.jsonPath().getList("stations[0].name").contains("신림역")).isTrue();
                    assertThat(searchResponse.jsonPath().getList("stations[1].name").contains("화곡역")).isTrue();
                    assertThat(searchResponse.jsonPath().getList("stations[2].name").contains("강남역")).isTrue();
                }),

                // then
                // 지하철_노선_목록_응답됨
                // 지하철_노선_목록_포함됨
                DynamicTest.dynamicTest("지하철 노선 목록에 포함되어 있는지 확인한다.", () -> {
                    //given
                    ExtractableResponse<Response> searchResponse = 지하철_목록_조회();

                    //when
                    List<String> searchLineNames = searchResponse.jsonPath().getList("name");

                    //then
                    assertThat(searchResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
                    assertThat(searchLineNames.contains("1호선")).isTrue();
                    assertThat(searchLineNames.contains("2호선")).isTrue();
                })
        );
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        지하철역_여러_생성();
        지하철_노선_등록(new LineRequest("1호선", "Purple", 1L, 2L, 10));
        지하철_노선_추가_등록(new SectionRequest(2L, 3L, 10), 1L);
        지하철_노선_추가_등록(new SectionRequest(3L, 4L, 10), 1L);
        지하철_노선_추가_등록(new SectionRequest(2L, 5L, 5), 1L);
        
        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> searchLine = 지하철_노선_조회(1L);

        // then
        // 지하철_노선_응답됨
        assertThat(searchLine.jsonPath().getList("stations").size()).isEqualTo(5);
        assertThat(searchLine.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(searchLine.jsonPath().getString("stations[2].name")).isEqualTo("강남역");
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        지하철역_여러_생성();
        지하철_노선_등록(new LineRequest("5호선", "Purple", 1L, 2L, 10));
        // 지하철_노선_조회
        ExtractableResponse<Response> response = 지하철_노선_조회(1L);
        // 지하철_노선_응답됨
        Long lineId = response.jsonPath().getLong("id");
        String color = response.jsonPath().getString("color");
        assertThat(color).isEqualTo("Purple");

        // when
        // 지하철_노선_수정_요청
        ExtractableResponse<Response> updateResponse = 지하철_노선_수정(new LineRequest("5호선", "Green", 1L, 3L, 10), lineId);
        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        // then
        // 지하철_노선_수정됨
        ExtractableResponse<Response> searchUpdateLine = 지하철_노선_조회(1L);
        color = searchUpdateLine.jsonPath().getString("color");
        assertThat(color).isEqualTo("Green");
        assertThat(searchUpdateLine.jsonPath().getString("stations[1].name")).isEqualTo("화곡역");
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        지하철역_여러_생성();
        지하철_노선_등록(new LineRequest("화곡역", "Purple", 1L, 2L, 10));

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> deleteResponse = 지하철_노선_제거(1L);

        // then
        // 지하철_노선_삭제됨
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    void 지하철역_여러_생성() {
        지하철역_생성(new StationRequest("신도림역"));
        지하철역_생성(new StationRequest("신림역"));
        지하철역_생성(new StationRequest("화곡역"));
        지하철역_생성(new StationRequest("까치역"));
        지하철역_생성(new StationRequest("강남역"));
        지하철역_생성(new StationRequest("교대역"));
    }

    ExtractableResponse<Response> 지하철_노선_등록(LineRequest lineRequest) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(lineRequest)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    ExtractableResponse<Response> 지하철_목록_조회() {
        return RestAssured.given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
    }

    ExtractableResponse<Response> 지하철_노선_조회(Long id) {
        return RestAssured.given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines/{id}", id)
                .then().log().all()
                .extract();
    }

    ExtractableResponse<Response> 지하철_노선_수정(LineRequest lineRequest, Long id) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(lineRequest)
                .when()
                .put("/lines/{id}", id)
                .then().log().all()
                .extract();
    }

    ExtractableResponse<Response> 지하철_노선_제거(Long id) {
        return RestAssured.given().log().all()
                .when()
                .delete("/lines/{id}", id)
                .then().log().all()
                .extract();
    }

    ExtractableResponse<Response> 지하철역_생성(StationRequest stationRequest) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(stationRequest)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    ExtractableResponse<Response> 지하철_노선_추가_등록(SectionRequest sectionRequest, Long lineId) {
        return RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/{id}/sections", lineId)
                .then().log().all()
                .extract();
    }
}
