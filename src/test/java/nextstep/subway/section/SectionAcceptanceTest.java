package nextstep.subway.section;

import static nextstep.subway.line.LineAcceptance.toLine;
import static nextstep.subway.line.LineAcceptance.toLineId;
import static nextstep.subway.line.LineAcceptance.toLineStationNames;
import static nextstep.subway.line.LineAcceptance.지하철_노선_생성;
import static nextstep.subway.line.LineAcceptance.지하철_노선_조회;
import static nextstep.subway.section.SectionAcceptance.지하철_구간_등록;
import static nextstep.subway.station.StationAcceptance.toStationId;
import static nextstep.subway.station.StationAcceptance.지하철역_생성;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.stream.Stream;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.global.exception.ExceptionType;
import nextstep.subway.section.dto.SectionRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.http.HttpStatus;

@DisplayName("지하철 구간 인수 테스트")
class SectionAcceptanceTest extends AcceptanceTest {

    private Long 신도림역_id;
    private Long 대림역_id;
    private Long 신대방역_id;
    private Long 신림역_id;
    private Long 강남역_id;
    private Long 노선_id;

    @BeforeEach
    void sectionSetUp() {
        신도림역_id = toStationId(지하철역_생성("신도림역"));
        대림역_id = toStationId(지하철역_생성("대림역"));
        신대방역_id = toStationId(지하철역_생성("신대방역"));
        신림역_id = toStationId(지하철역_생성("신림역"));
        강남역_id = toStationId(지하철역_생성("강남역"));
        노선_id = toLineId(지하철_노선_생성("2호선", "green", 대림역_id, 강남역_id, 20L));
    }

    @AfterEach
    void cleanUp() {
        databaseClean("section", "line", "station");
    }

    @DisplayName("역과 역 사이에 새로운 구간을 등록한다.")
    @TestFactory
    Stream<DynamicTest> createSection() {
        return Stream.of(
            dynamicTest("역 사이에 새로운 역을 등록한다" , () -> {
                SectionRequest sectionRequest = new SectionRequest(대림역_id, 신대방역_id, 7L);
                ExtractableResponse<Response> saveResponse = 지하철_구간_등록(노선_id, sectionRequest);
                assertThat(saveResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
            }),
            dynamicTest("노선을 조회하면 생성된 구간이 같이 조회되어야 한다", () -> {
                ExtractableResponse<Response> response = 지하철_노선_조회(노선_id);
                LineResponse result = toLine(response);
                assertAll(
                    () -> assertThat(result.getStations()).hasSize(3),
                    () -> assertThat(toLineStationNames(result)).containsExactly("대림역", "신대방역", "강남역")
                );
            })
        );
    }

    @DisplayName("새로운 역을 상행 종점에 등록한다.")
    @TestFactory
    Stream<DynamicTest> createSection2() {
        return Stream.of(
            dynamicTest("새로운 역을 상행 종점으로 등록한다" , () -> {
                SectionRequest sectionRequest = new SectionRequest(신도림역_id, 대림역_id, 10L);
                ExtractableResponse<Response> saveResponse = 지하철_구간_등록(노선_id, sectionRequest);
                assertThat(saveResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
            }),
            dynamicTest("노선을 조회하면 상행 종점으로 생성된 구간이 조회되어야 한다", () -> {
                ExtractableResponse<Response> response = 지하철_노선_조회(노선_id);
                LineResponse result = toLine(response);
                assertAll(
                    () -> assertThat(result.getStations()).hasSize(3),
                    () -> assertThat(toLineStationNames(result)).containsExactly("신도림역", "대림역", "강남역")
                );
            })
        );
    }

    @DisplayName("새로운 역을 하행 종점에 등록한다.")
    @TestFactory
    Stream<DynamicTest> createSection3() {
        return Stream.of(
            dynamicTest("새로운 역을 하행 종점으로 등록한다" , () -> {
                SectionRequest sectionRequest = new SectionRequest(강남역_id, 신림역_id, 10L);
                ExtractableResponse<Response> saveResponse = 지하철_구간_등록(노선_id, sectionRequest);
                assertThat(saveResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
            }),
            dynamicTest("노선을 조회하면 하행 종점으로 생성된 구간이 조회되어야 한다", () -> {
                ExtractableResponse<Response> response = 지하철_노선_조회(노선_id);
                LineResponse result = toLine(response);
                assertAll(
                    () -> assertThat(result.getStations()).hasSize(3),
                    () -> assertThat(toLineStationNames(result)).containsExactly("대림역", "강남역", "신림역")
                );
            })
        );
    }

    @DisplayName("노선 등록시 거리가 크거나 같으면 예외가 발생한다 ")
    @TestFactory
    Stream<DynamicTest> createSection_exception() {
        return Stream.of(
            dynamicTest("기존 노선의 길이보다 거리가 긴 노선을 역 사이에 등록하면 "
                + "예외가 발생한다" , () -> {
                SectionRequest sectionRequest = new SectionRequest(대림역_id, 신대방역_id, 30L);
                ExtractableResponse<Response> response = 지하철_구간_등록(노선_id, sectionRequest);
                assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                assertThat(response.asString()).contains(ExceptionType.IS_NOT_OVER_ORIGIN_DISTANCE.getMessage());
            })
        );
    }

    @DisplayName("노선 등록시 두 역이 이미 모두 존재하면 예외가 발생한다")
    @TestFactory
    Stream<DynamicTest> createSection_exception2() {
        return Stream.of(
            dynamicTest("신규 노선 등록시 기존 노선에 모두 존재하는 역을 등록하면 "
                + "예외가 발생한다" , () -> {
                SectionRequest sectionRequest = new SectionRequest(대림역_id, 강남역_id, 5L);
                ExtractableResponse<Response> response = 지하철_구간_등록(노선_id, sectionRequest);
                assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                assertThat(response.asString()).contains(ExceptionType.IS_EXIST_BOTH_STATIONS.getMessage());
            })
        );
    }

    @DisplayName("노선 등록시 두 역이 모두 존재하지 않으면 예외가 발생")
    @TestFactory
    Stream<DynamicTest> createSection_exception3() {
        return Stream.of(
            dynamicTest("신규 노선 등록시 기존 노선에 모두 존재하지 않는 역을 등록하면 "
                + "예외가 발생한다" , () -> {
                SectionRequest sectionRequest = new SectionRequest(신림역_id, 신대방역_id, 5L);
                ExtractableResponse<Response> response = 지하철_구간_등록(노선_id, sectionRequest);
                assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                assertThat(response.asString()).contains(ExceptionType.IS_NOT_EXIST_BOTH_STATIONS.getMessage());
            })
        );
    }
}
