package nextstep.subway.section;

import static nextstep.subway.common.StationConstants.DMC;
import static nextstep.subway.common.StationConstants.GIMPO_AIRPORT;
import static nextstep.subway.common.StationConstants.GONGDEOK;
import static nextstep.subway.common.StationConstants.GYEYANG;
import static nextstep.subway.common.StationConstants.HONGIK_UNIV;
import static nextstep.subway.common.StationConstants.MAGONGNARU;
import static nextstep.subway.common.StationConstants.SEOUL;
import static nextstep.subway.common.StationConstants.createAllStations;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.common.LineTestData;
import nextstep.subway.common.StationConstants;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.section.dto.SectionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철 구간 관련 기능")
class SectionAcceptanceTest extends AcceptanceTest {

    private LineTestData AIRPORT_EXPRESS_GONGDEOK_TO_HONGIK;
    private LineTestData AIRPORT_EXPRESS_SEOUL_TO_HONGIK;

    @BeforeEach
    void setUpTestData() {
        AIRPORT_EXPRESS_GONGDEOK_TO_HONGIK = new LineTestData(
            "공항철도", "#0065B3", 100, GONGDEOK.toResponse(), HONGIK_UNIV.toResponse()
        );

        AIRPORT_EXPRESS_SEOUL_TO_HONGIK = new LineTestData(
            "공항철도", "#0065B3", 200, SEOUL.toResponse(), HONGIK_UNIV.toResponse());

        createAllStations();
    }

    @DisplayName("기존 노선의 하행 종점에 새 구간을 등록한다.")
    @TestFactory
    Stream<DynamicTest> addSectionTest01() {
        return Stream.of(
            dynamicTest("공항철도 노선 생성(공덕역-홍대입구역)", () -> createLineRequestSuccess(AIRPORT_EXPRESS_GONGDEOK_TO_HONGIK)),
            dynamicTest("홍대입구역-DMC역 구간 추가", () -> addSectionRequestSuccess(HONGIK_UNIV, DMC, 50))
        );
    }

    @DisplayName("기존 노선의 상행 종점에 새 구간을 등록한다.")
    @TestFactory
    Stream<DynamicTest> addSectionTest02() {
        return Stream.of(
            dynamicTest("공항철도 노선 생성(공덕역-홍대입구역)", () -> createLineRequestSuccess(AIRPORT_EXPRESS_GONGDEOK_TO_HONGIK)),
            dynamicTest("서울역-공덕역 구간 추가", () -> addSectionRequestSuccess(SEOUL, GONGDEOK, 50))
        );
    }

    @DisplayName("기존 노선 가운데에 새 구간을 등록한다.")
    @TestFactory
    Stream<DynamicTest> addSectionTest03() {
        return Stream.of(
            dynamicTest("공항철도 노선 생성(서울역-홍대입구역)", () -> createLineRequestSuccess(AIRPORT_EXPRESS_SEOUL_TO_HONGIK)),
            dynamicTest("서울역-공덕역 구간 추가", () -> addSectionRequestSuccess(SEOUL, GONGDEOK, 50))
        );
    }

    @DisplayName("기존 노선 가운데에 새 구간을 등록한다.")
    @TestFactory
    Stream<DynamicTest> addSectionTest04() {
        return Stream.of(
            dynamicTest("공항철도 노선 생성(서울역-홍대입구역)", () -> createLineRequestSuccess(AIRPORT_EXPRESS_SEOUL_TO_HONGIK)),
            dynamicTest("공덕역-홍대입구역 구간 추가", () -> addSectionRequestSuccess(GONGDEOK, HONGIK_UNIV, 50))
        );
    }

    @DisplayName("서울역 ~ 계양역 구간 완성")
    @TestFactory
    Stream<DynamicTest> addSectionTest05() {
        return Stream.of(
            dynamicTest("공항철도 노선 생성(공덕역-홍대입구역)", () -> createLineRequestSuccess(AIRPORT_EXPRESS_GONGDEOK_TO_HONGIK)),
            dynamicTest("서울역-공덕역 구간 추가", () -> addSectionRequestSuccess(SEOUL, GONGDEOK, 50)),
            dynamicTest("홍대입구역-DMC역 구간 추가", () -> addSectionRequestSuccess(HONGIK_UNIV, DMC, 100)),
            dynamicTest("DMC역-마곡나루 구간 추가", () -> addSectionRequestSuccess(DMC, MAGONGNARU, 150)),
            dynamicTest("마곡나루역-김포공항역 구간 추가", () -> addSectionRequestSuccess(MAGONGNARU, GIMPO_AIRPORT, 200)),
            dynamicTest("김포공항역-계양역 구간 추가", () -> addSectionRequestSuccess(GIMPO_AIRPORT, GYEYANG, 250))
        );
    }

    @DisplayName("기존 노선 가운데에 새 구간을 등록하는 경우 기존 역 사이 간격보다 크거나 같지 않아야 한다.")
    @TestFactory
    Stream<DynamicTest> addSectionFailBecauseOfDistance01() {
        return Stream.of(
            dynamicTest("공항철도 노선 생성(서울역-홍대입구역)", () -> createLineRequestSuccess(AIRPORT_EXPRESS_SEOUL_TO_HONGIK)),
            dynamicTest("서울역-공덕역 구간 추가", () -> addSectionRequestFail(SEOUL, GONGDEOK, 200))
        );
    }

    @DisplayName("기존 노선 가운데에 새 구간을 등록하는 경우 기존 역 사이 간격보다 크거나 같지 않아야 한다.")
    @TestFactory
    Stream<DynamicTest> addSectionFailBecauseOfDistance02() {
        return Stream.of(
            dynamicTest("공항철도 노선 생성(서울역-홍대입구역)", () -> createLineRequestSuccess(AIRPORT_EXPRESS_SEOUL_TO_HONGIK)),
            dynamicTest("공덕역-홍대입구역 구간 추가", () -> addSectionRequestFail(GONGDEOK, HONGIK_UNIV, 200))
        );
    }

    @DisplayName("추가하려는 상/하행역이 기존 노선에 이미 등록되어 있다면 추가할 수 없다.")
    @TestFactory
    Stream<DynamicTest> addSectionFailBecauseOfDuplicate01() {
        return Stream.of(
            dynamicTest("공항철도 노선 생성(공덕역-홍대입구역)", () -> createLineRequestSuccess(AIRPORT_EXPRESS_GONGDEOK_TO_HONGIK)),
            dynamicTest("서울역-공덕역 구간 추가", () -> addSectionRequestSuccess(SEOUL, GONGDEOK, 150)),
            dynamicTest("홍대입구역-서울역 구간 추가", () -> addSectionRequestFail(HONGIK_UNIV, SEOUL, 100))
        );
    }

    @DisplayName("추가하려는 상/하행역이 기존 노선에 이미 등록되어 있다면 추가할 수 없다.")
    @TestFactory
    Stream<DynamicTest> addSectionFailBecauseOfDuplicate02() {
        return Stream.of(
            dynamicTest("공항철도 노선 생성(공덕역-홍대입구역)", () -> createLineRequestSuccess(AIRPORT_EXPRESS_GONGDEOK_TO_HONGIK)),
            dynamicTest("홍대입구역-공덕역 구간 추가", () -> addSectionRequestFail(HONGIK_UNIV, GONGDEOK, 100))
        );
    }

    @DisplayName("추가하려는 상/하행역 중 하나라도 기존 노선에 포함되어 있지 않다면 추가할 수 없다.")
    @TestFactory
    Stream<DynamicTest> addSectionFailTest01() {
        return Stream.of(
            dynamicTest("공항철도 노선 생성(공덕역-홍대입구역)", () -> createLineRequestSuccess(AIRPORT_EXPRESS_GONGDEOK_TO_HONGIK)),
            dynamicTest("김포공항-계양역 구간 추가", () -> addSectionRequestFail(GIMPO_AIRPORT, GYEYANG, 100))
        );
    }

    private void createLineRequestSuccess(LineTestData data) {

        LineRequest lineRequest = data.getLine();
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                                                            .body(data.getLine())
                                                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                            .when().post("/lines")
                                                            .then().log().all()
                                                            .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).startsWith("/lines");

        assertThat(response.body().jsonPath().getString("name"))
            .isEqualTo(lineRequest.getName());

        assertThat(response.body().jsonPath().getString("color"))
            .isEqualTo(lineRequest.getColor());
    }

    private ExtractableResponse<Response> addSectionRequest(Long upStationId, Long downStationId, int distance) {

        SectionRequest sectionRequest = new SectionRequest(upStationId, downStationId, distance);

        return RestAssured.given().log().all()
                          .body(sectionRequest)
                          .contentType(MediaType.APPLICATION_JSON_VALUE)
                          .when().post("/lines/1/sections")
                          .then().log().all()
                          .extract();
    }

    private void addSectionRequestSuccess(StationConstants upStation, StationConstants downStation, int distance) {
        ExtractableResponse<Response> response = addSectionRequest(upStation.getId(),
                                                                   downStation.getId(),
                                                                   distance);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        Map<String, Object> map = new HashMap<>();
        map.put("upStationName", upStation.getName());
        map.put("downStationName", downStation.getName());
        map.put("distance", distance);

        assertThat(response.body().jsonPath().getList("sections")).contains(map);
    }

    private void addSectionRequestFail(StationConstants upStation, StationConstants downStation, int distance) {
        ExtractableResponse<Response> response = addSectionRequest(upStation.getId(),
                                                                   downStation.getId(),
                                                                   distance);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
