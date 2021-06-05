package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static nextstep.subway.line.LineAcceptanceTest.StationConstants.BAKCHON;
import static nextstep.subway.line.LineAcceptanceTest.StationConstants.DMC;
import static nextstep.subway.line.LineAcceptanceTest.StationConstants.GEOMDAN_ORYU;
import static nextstep.subway.line.LineAcceptanceTest.StationConstants.GIMPO_AIRPORT;
import static nextstep.subway.line.LineAcceptanceTest.StationConstants.GONGDEOK;
import static nextstep.subway.line.LineAcceptanceTest.StationConstants.GYEYANG;
import static nextstep.subway.line.LineAcceptanceTest.StationConstants.GYULHYEON;
import static nextstep.subway.line.LineAcceptanceTest.StationConstants.HONGIK_UNIV;
import static nextstep.subway.line.LineAcceptanceTest.StationConstants.SEOUL;
import static nextstep.subway.line.LineAcceptanceTest.StationConstants.WANGGIL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@DisplayName("지하철 노선 관련 기능")
class LineAcceptanceTest extends AcceptanceTest {

    private LineTestData INCHEON_SUBWAY_LINE_1;
    private LineTestData INCHEON_SUBWAY_LINE_2;
    private LineTestData AIRPORT_EXPRESS_DEFAULT;
    private LineTestData AIRPORT_EXPRESS_SKIP_GONGDEOK;

    @BeforeEach
    void setUpField() {

        INCHEON_SUBWAY_LINE_1 = new LineTestData(
            "인천 1호선", "#7CA8D5", GYULHYEON.toResponse(), BAKCHON.toResponse()
        );

        INCHEON_SUBWAY_LINE_2 = new LineTestData(
            "인천 2호선", "#ED8B00", GEOMDAN_ORYU.toResponse(), WANGGIL.toResponse()
        );

        AIRPORT_EXPRESS_DEFAULT = new LineTestData(
            "공항철도", "#0065B3", GONGDEOK.toResponse(), HONGIK_UNIV.toResponse()
        );

        AIRPORT_EXPRESS_SKIP_GONGDEOK = new LineTestData(
            "공항철도", "#0065B3", 200, SEOUL.toResponse(), HONGIK_UNIV.toResponse());
    }

    @DisplayName("지하철 노선 생성")
    @TestFactory
    Stream<DynamicTest> createLineRequestTest() {
        return Stream.of(
            dynamicTest("모든 지하철 역 생성", this::createAllStations),
            dynamicTest("인천 1호선 노선 생성", () -> createLineRequestSuccess(INCHEON_SUBWAY_LINE_1))
        );
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @TestFactory
    Stream<DynamicTest> createLineRequestTest02() {
        return Stream.of(
            dynamicTest("모든 지하철 역 생성", this::createAllStations),
            dynamicTest("인천 1호선 노선 생성", () -> createLineRequestSuccess(INCHEON_SUBWAY_LINE_1)),
            dynamicTest("인천 1호선 노선 다시 생성 시 실패", () -> {
                ExtractableResponse<Response> response = createLineRequest(INCHEON_SUBWAY_LINE_1);

                // then
                // 지하철_노선_생성_실패됨
                assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                assertThat(response.header("Location")).isBlank();
            })
        );
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @TestFactory
    Stream<DynamicTest> findLinesTest() {
        return Stream.of(
            dynamicTest("모든 지하철 역 생성", this::createAllStations),
            dynamicTest("인천 1호선 노선 생성", () -> createLineRequestSuccess(INCHEON_SUBWAY_LINE_1)),
            dynamicTest("인천 2호선 노선 생성", () -> createLineRequestSuccess(INCHEON_SUBWAY_LINE_2)),
            dynamicTest("지하철 노선 목록 조회 및 검증", () ->
                fineLinesSuccess(INCHEON_SUBWAY_LINE_1, INCHEON_SUBWAY_LINE_2))
        );
    }

    @DisplayName("존재하지 않는 노선 번호로 노선을 조회한다.")
    @TestFactory
    Stream<DynamicTest> getLineFailTest() {
        return Stream.of(
            dynamicTest("모든 지하철 역 생성", this::createAllStations),
            dynamicTest("인천 1호선 노선 생성", () -> createLineRequestSuccess(INCHEON_SUBWAY_LINE_1)),
            dynamicTest("지하철 노선 조회 요청", () -> {
                ExtractableResponse<Response> response = findLine(100L);
                assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
            })
        );
    }

    @DisplayName("지하철 노선을 수정한다.")
    @TestFactory
    Stream<DynamicTest> updateLineTest() {
        return Stream.of(
            dynamicTest("모든 지하철 역 생성", this::createAllStations),
            dynamicTest("인천 1호선 노선 생성", () -> createLineRequestSuccess(INCHEON_SUBWAY_LINE_1)),
            dynamicTest("인천 1호선 노선을 인천 2호선 노선으로 수정 및 검증", () -> updateLineTo(INCHEON_SUBWAY_LINE_2))
        );
    }

    @DisplayName("지하철 노선을 삭제한다.")
    @TestFactory
    Stream<DynamicTest> deleteLineTest() {
        return Stream.of(
            dynamicTest("모든 지하철 역 생성", this::createAllStations),
            dynamicTest("인천 1호선 노선 생성", () -> createLineRequestSuccess(INCHEON_SUBWAY_LINE_1)),
            dynamicTest("생성된 노선 삭제 및 검증", () -> {
                ExtractableResponse<Response> response = deleteLineRequest();
                assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
            })
        );
    }
    
    @DisplayName("기존 노선의 하행 종점에 새 구간을 등록한다.")
    @TestFactory
    Stream<DynamicTest> addSectionTest01() {
        return Stream.of(
            dynamicTest("모든 지하철 역 생성", this::createAllStations),
            dynamicTest("공항철도 기본 노선 생성", () -> createLineRequestSuccess(AIRPORT_EXPRESS_DEFAULT)),
            dynamicTest("홍대입구역-DMC역 구간 추가", () -> addSectionRequestSuccess(HONGIK_UNIV.getId(), DMC.getId()))
        );
    }
    
    @DisplayName("기존 노선의 상행 종점에 새 구간을 등록한다.")
    @TestFactory
    Stream<DynamicTest> addSectionTest02() {
        return Stream.of(
            dynamicTest("모든 지하철 역 생성", this::createAllStations),
            dynamicTest("공항철도 기본 노선 생성", () -> createLineRequestSuccess(AIRPORT_EXPRESS_DEFAULT)),
            dynamicTest("서울역-공덕역 구간 추가", () -> addSectionRequestSuccess(SEOUL.getId(), GONGDEOK.getId()))
        );
    }
    
    @DisplayName("기존 노선 가운데에 새 구간을 등록한다.")
    @TestFactory
    Stream<DynamicTest> addSectionTest03() {
        return Stream.of(
            dynamicTest("모든 지하철 역 생성", this::createAllStations),
            dynamicTest("공항철도 노선 생성(서울-홍대입구역)", () -> createLineRequestSuccess(AIRPORT_EXPRESS_SKIP_GONGDEOK)),
            dynamicTest("서울역-공덕역 구간 추가", () -> addSectionRequestSuccess(SEOUL.getId(), GONGDEOK.getId()))
        );
    }
    
    @DisplayName("기존 노선 가운데에 새 구간을 등록하는 경우 기존 역 사이 간격보다 크거나 같지 않아야 한다.")
    @TestFactory
    Stream<DynamicTest> addSectionFailTest01() {
        return Stream.of(
            dynamicTest("모든 지하철 역 생성", this::createAllStations),
            dynamicTest("공항철도 노선 생성(서울-홍대입구역)", () -> createLineRequestSuccess(AIRPORT_EXPRESS_SKIP_GONGDEOK)),
            dynamicTest("서울역-공덕역 구간 추가", () -> addSectionRequestFail(SEOUL.getId(), GONGDEOK.getId(), 200))
        );
    }
    
    @DisplayName("추가하려는 상/하행역이 기존 노선에 이미 등록되어 있다면 추가할 수 없다.")
    @TestFactory
    Stream<DynamicTest> addSectionFailTest02() {
        return Stream.of(
            dynamicTest("모든 지하철 역 생성", this::createAllStations),
            dynamicTest("공항철도 노선 생성", () -> createLineRequestSuccess(AIRPORT_EXPRESS_DEFAULT)),
            dynamicTest("홍대입구역-공덕역 구간 추가", () -> addSectionRequestFail(HONGIK_UNIV.getId(), GONGDEOK.getId(), 100))
        );
    }
    
    @DisplayName("추가하려는 상/하행역 중 하나라도 기존 노선에 포함되어 있지 않다면 추가할 수 없다.")
    @TestFactory
    Stream<DynamicTest> addSectionFailTest03() {
        return Stream.of(
            dynamicTest("모든 지하철 역 생성", this::createAllStations),
            dynamicTest("공항철도 노선 생성", () -> createLineRequestSuccess(AIRPORT_EXPRESS_DEFAULT)),
            dynamicTest("김포공항-계양역 구간 추가", () -> addSectionRequestFail(GIMPO_AIRPORT.getId(), GYEYANG.getId(), 100))
        );
    }

    private ExtractableResponse<Response> findSavedLine() {
        return RestAssured.given().log().all()
                          .when().get("/lines/1")
                          .then().log().all()
                          .extract();
    }

    private ExtractableResponse<Response> updateLineRequest(LineTestData data) {
        return RestAssured.given().log().all()
                          .body(data.getLine())
                          .contentType(MediaType.APPLICATION_JSON_VALUE)
                          .when().put("/lines/1")
                          .then().log().all()
                          .extract();
    }

    private ExtractableResponse<Response> deleteLineRequest() {
        return RestAssured.given().log().all()
                          .when().delete("/lines/1")
                          .then().log().all()
                          .extract();
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

    private void addSectionRequestSuccess(Long upStationId, Long downStationId) {
        ExtractableResponse<Response> response = addSectionRequest(upStationId,
                                                                   downStationId,
                                                                   100);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private void addSectionRequestFail(Long upStationId, Long downStationId, int distance) {
        ExtractableResponse<Response> response = addSectionRequest(upStationId,
                                                                   downStationId,
                                                                   distance);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void createAllStations() {
        StationConstants.getAllStations().forEach(this::createStationSuccess);
    }

    private void createStationSuccess(StationResponse stationResponse) {
        ExtractableResponse<Response> response =
            RestAssured.given().log().all()
                       .body(new StationRequest(stationResponse.getName()))
                       .contentType(MediaType.APPLICATION_JSON_VALUE)
                       .when().post("/stations")
                       .then().log().all()
                       .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private ExtractableResponse<Response> createLineRequest(LineTestData data) {
        return RestAssured.given().log().all()
                          .body(data.getLine())
                          .contentType(MediaType.APPLICATION_JSON_VALUE)
                          .when().post("/lines")
                          .then().log().all()
                          .extract();
    }

    private void createLineRequestSuccess(LineTestData data) {

        LineRequest lineRequest = data.getLine();
        ExtractableResponse<Response> response = createLineRequest(data);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).startsWith("/lines");

        assertThat(response.body().jsonPath().getString("name"))
            .isEqualTo(lineRequest.getName());

        assertThat(response.body().jsonPath().getString("color"))
            .isEqualTo(lineRequest.getColor());
    }

    private void fineLinesSuccess(LineTestData data1, LineTestData data2) {
        // when
        // 지하철_노선_목록_조회_요청
        // when
        ExtractableResponse<Response> response = findLines();

        LineRequest line1 = data1.getLine();
        LineRequest line2 = data2.getLine();

        // then
        // 지하철_노선_목록_응답됨
        // 지하철_노선_목록_포함됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<LineResponse> lines = response.body().jsonPath().getList("$", LineResponse.class);
        assertThat(lines.size()).isEqualTo(2);
        assertThat(lines).extracting(LineResponse::getName)
                         .contains(line1.getName(), line2.getName());
        assertThat(lines).extracting(LineResponse::getColor)
                         .contains(line1.getColor(), line2.getColor());

        assertThat(lines.get(0).getStations()).hasSameElementsAs(data1.getStations());
        assertThat(lines.get(1).getStations()).hasSameElementsAs(data2.getStations());
    }

    private ExtractableResponse<Response> findLines() {
        return findLine(null);
    }

    private ExtractableResponse<Response> findLine(Long lineId) {

        String additionalPath = "";
        if (lineId != null) {
            additionalPath += lineId;
        }

        return RestAssured.given().log().all()
                          .when().get("/lines/" + additionalPath)
                          .then().log().all()
                          .extract();
    }

    private void updateLineTo(LineTestData data) {
        ExtractableResponse<Response> response = updateLineRequest(data);
        ExtractableResponse<Response> actual = findSavedLine();

        // then
        // 지하철_노선_수정됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        LineRequest line = data.getLine();

        assertThat(actual.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actual.body().jsonPath().getString("name"))
            .isEqualTo(line.getName());
        assertThat(actual.body().jsonPath().getString("color"))
            .isEqualTo(line.getColor());
    }

    private static class LineTestData {

        private final LineRequest line;
        private final List<StationResponse> stations;

        public LineTestData(String name, String color,
                            StationResponse upStation,
                            StationResponse downStation) {
            this(name, color, 100, upStation, downStation);
        }

        public LineTestData(String name, String color, int distance,
                            StationResponse upStation,
                            StationResponse downStation) {
            this.line = new LineRequest(name, color, upStation.getId(), downStation.getId(), distance);
            this.stations = Arrays.asList(upStation, downStation);
        }

        public LineRequest getLine() {
            return line;
        }

        public List<StationResponse> getStations() {
            return stations;
        }
    }

    enum StationConstants {
        GYULHYEON(1L, "귤현역"),
        BAKCHON(2L, "박촌역"),
        GEOMDAN_ORYU(3L, "검단오류역"),
        WANGGIL(4L, "왕길역"),
        SEOUL(5L, "서울역"),
        GONGDEOK(6L, "공덕역"),
        HONGIK_UNIV(7L, "홍대입구역"),
        DMC(8L, "디지털미디어시티역"),
        MAGONGNARU(9L, "마곡나루역"),
        GIMPO_AIRPORT(10L, "김포공항역"),
        GYEYANG(11L, "계양역");

        private final Long id;
        private final String name;

        private static final Map<StationConstants, StationResponse> CACHE =
            Arrays.stream(values())
                  .collect(collectingAndThen(
                      toMap(Function.identity(),
                            c -> new StationResponse(c.getId(), c.getName(), null, null)),
                      Collections::unmodifiableMap));

        private static final List<StationResponse> ALL_STATIONS =
            Arrays.stream(values())
                  .map(CACHE::get)
                  .collect(collectingAndThen(toList(), Collections::unmodifiableList));

        StationConstants(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public static List<StationResponse> getAllStations() {
            return ALL_STATIONS;
        }

        public StationResponse toResponse() {
            return CACHE.get(this);
        }
    }
}
