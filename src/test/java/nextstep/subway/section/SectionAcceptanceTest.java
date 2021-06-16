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

    @DisplayName("상행역이 같은 구간 추가 시 구간길이가 같거나 크면 추가할 수 없다.")
    @TestFactory
    Stream<DynamicTest> cannot_add_sections_when_sectionLength_same_and_bigger() {
        return Stream.of(
                // given
                // 지하철 노선 등록되어 있음
                DynamicTest.dynamicTest("지하철 노선과 구간 등록", () -> {
                    //when
                    지하철역_여러_생성();
                    지하철_노선_등록(new LineRequest("1호선", "Purple", 1L, 2L, 10));
                    지하철_노선_추가_등록(new SectionRequest(2L, 3L, 10), 1L);
                    지하철_노선_추가_등록(new SectionRequest(3L, 4L, 10), 1L);
                }),

                // then
                // 추가 등록 오류
                DynamicTest.dynamicTest("추가 등록 오류 조건", () -> {
                    // when
                    // 상행역 또는 하행역 둘 중 하나라도 존재해야함
                    ExtractableResponse<Response> expected = 지하철_노선_추가_등록(new SectionRequest(6L, 5L, 11), 1L);
                    // then
                    assertThat(expected.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());

                    // when
                    // 기존 구간 보다 같거나 크면 안됨.
                    expected = 지하철_노선_추가_등록(new SectionRequest(2L, 5L, 11), 1L);
                    // then
                    assertThat(expected.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
                })
        );
    }

    @DisplayName("구간 중간 삽입 테스트")
    @TestFactory
    Stream<DynamicTest> insert_section() {
        return Stream.of(
                // given
                // 지하철 노선 등록되어 있음
                DynamicTest.dynamicTest("지하철 노선과 구간 등록", () -> {
                    //when
                    지하철역_여러_생성();
                    지하철_노선_등록(new LineRequest("1호선", "Purple", 1L, 2L, 10));
                    지하철_노선_추가_등록(new SectionRequest(2L, 3L, 10), 1L);
                    지하철_노선_추가_등록(new SectionRequest(3L, 4L, 10), 1L);

                    // when and
                    // 중간 삽입
                    ExtractableResponse<Response> expected = 지하철_노선_추가_등록(new SectionRequest(2L, 5L, 5), 1L);
                }),

                // then
                // 중간 등록됨
                DynamicTest.dynamicTest("구간 중간 등록 확인", () -> {
                    ExtractableResponse<Response> response = 노선_구간_목록_조회(1L);
                    List<SectionResponse> expected = response.jsonPath().getList(".", SectionResponse.class);
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

    ExtractableResponse<Response> 노선_구간_목록_조회(Long id) {
        return RestAssured.given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines/{id}/sections", id)
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
