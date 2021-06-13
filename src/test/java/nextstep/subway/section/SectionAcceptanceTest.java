package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.station.dto.StationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 관련 기능 테스트")
public class SectionAcceptanceTest extends AcceptanceTest {

    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @DisplayName("구간 등록 테스트")
    @TestFactory
    Stream<DynamicTest> createSections() {
        return Stream.of(
                // given
                // 지하철 노선 등록되어 있음
                DynamicTest.dynamicTest("하나의 노선에 추가 구간을 등록할 수 있다", () -> {
                    //when
                    지하철역_여러_생성();
                    지하철_노선_등록(new LineRequest("1호선", "Purple", 1L, 2L, 10));
                    ExtractableResponse<Response> response = 지하철_노선_추가_등록(new SectionRequest(2L, 3L, 10), 1L);

                    // then
                    // 지하철 구간 등록됨
                    assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
                })
        );
    }

    @DisplayName("구간 목록 조회")
    @TestFactory
    Stream<DynamicTest> getSections() {
        return Stream.of(
                // given
                // 지하철 노선 등록되어 있음
                DynamicTest.dynamicTest("하나의 노선에 추가 구간을 등록할 수 있다", () -> {
                    //when
                    지하철역_여러_생성();
                    지하철_노선_등록(new LineRequest("1호선", "Purple", 1L, 2L, 10));
                    지하철_노선_추가_등록(new SectionRequest(2L, 3L, 10), 1L);
                    지하철_노선_추가_등록(new SectionRequest(3L, 4L, 10), 1L);

                    // then
                    // 지하철 구간 등록됨
                    ExtractableResponse<Response> response = 구간_목록_조회();
                    List<SectionResponse> expected = response.jsonPath().getList(".", SectionResponse.class);
                    assertThat(expected.size()).isEqualTo(3);
                }),

                // then
                // 1호선으로 등록된 지하철 갯수
                DynamicTest.dynamicTest("지하철역이 등록된 노선조회", () -> {
                    ExtractableResponse<Response> response = 지하철역_노선_조회(1L);
                    List<SectionResponse> expected = response.jsonPath().getList(".");
                    assertThat(expected.size()).isEqualTo(4);
                })
        );
    }

    void 지하철역_여러_생성() {
        지하철역_생성(new StationRequest("구로디지털단지역"));
        지하철역_생성(new StationRequest("신대방역"));
        지하철역_생성(new StationRequest("신림역"));
        지하철역_생성(new StationRequest("대방역"));
        지하철역_생성(new StationRequest("서울대입구역"));
        지하철역_생성(new StationRequest("사당역"));
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

    ExtractableResponse<Response> 지하철_노선_등록(LineRequest lineRequest) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(lineRequest)
                .when()
                .post("/lines")
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

    ExtractableResponse<Response> 구간_목록_조회() {
        return RestAssured.given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/sections")
                .then().log().all()
                .extract();
    }

    ExtractableResponse<Response> 지하철역_노선_조회(Long id) {
        return RestAssured.given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines/{id}/lineStations", id)
                .then().log().all()
                .extract();
    }
}
