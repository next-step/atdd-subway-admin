package nextstep.subway.section;

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
import org.junit.jupiter.api.function.Executable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static nextstep.subway.common.StationConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

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
            dynamicTest("공항철도 노선 생성(공덕역-홍대입구역)", createLineSuccess(AIRPORT_EXPRESS_GONGDEOK_TO_HONGIK)),
            dynamicTest("홍대입구역-DMC역 구간 추가 성공", addSectionSuccess(HONGIK_UNIV, DMC, 50))
        );
    }

    @DisplayName("기존 노선의 상행 종점에 새 구간을 등록한다.")
    @TestFactory
    Stream<DynamicTest> addSectionTest02() {
        return Stream.of(
            dynamicTest("공항철도 노선 생성(공덕역-홍대입구역)", createLineSuccess(AIRPORT_EXPRESS_GONGDEOK_TO_HONGIK)),
            dynamicTest("서울역-공덕역 구간 추가 성공", addSectionSuccess(SEOUL, GONGDEOK, 50))
        );
    }

    @DisplayName("기존 노선 가운데에 새 구간을 등록한다.")
    @TestFactory
    Stream<DynamicTest> addSectionTest03() {
        return Stream.of(
            dynamicTest("공항철도 노선 생성(서울역-홍대입구역)", createLineSuccess(AIRPORT_EXPRESS_SEOUL_TO_HONGIK)),
            dynamicTest("서울역-공덕역 구간 추가 성공", addSectionSuccess(SEOUL, GONGDEOK, 50))
        );
    }

    @DisplayName("기존 노선 가운데에 새 구간을 등록한다.")
    @TestFactory
    Stream<DynamicTest> addSectionTest04() {
        return Stream.of(
            dynamicTest("공항철도 노선 생성(서울역-홍대입구역)", createLineSuccess(AIRPORT_EXPRESS_SEOUL_TO_HONGIK)),
            dynamicTest("공덕역-홍대입구역 구간 추가 성공", addSectionSuccess(GONGDEOK, HONGIK_UNIV, 50))
        );
    }

    @DisplayName("서울역 ~ 계양역 구간 완성")
    @TestFactory
    Stream<DynamicTest> addSectionTest05() {
        return Stream.of(
            dynamicTest("공항철도 노선 생성(공덕역-홍대입구역)", createLineSuccess(AIRPORT_EXPRESS_GONGDEOK_TO_HONGIK)),
            dynamicTest("서울역-공덕역 구간 추가 성공", addSectionSuccess(SEOUL, GONGDEOK, 50)),
            dynamicTest("홍대입구역-DMC역 구간 추가 성공", addSectionSuccess(HONGIK_UNIV, DMC, 100)),
            dynamicTest("DMC역-마곡나루 구간 추가 성공", addSectionSuccess(DMC, MAGONGNARU, 150)),
            dynamicTest("마곡나루역-김포공항역 구간 추가 성공", addSectionSuccess(MAGONGNARU, GIMPO_AIRPORT, 200)),
            dynamicTest("김포공항역-계양역 구간 추가 성공", addSectionSuccess(GIMPO_AIRPORT, GYEYANG, 250))
        );
    }

    @DisplayName("기존 노선 가운데에 새 구간을 등록하는 경우 기존 역 사이 간격보다 크거나 같지 않아야 한다.")
    @TestFactory
    Stream<DynamicTest> addSectionFailBecauseOfDistance01() {
        return Stream.of(
            dynamicTest("공항철도 노선 생성(서울역-홍대입구역)", createLineSuccess(AIRPORT_EXPRESS_SEOUL_TO_HONGIK)),
            dynamicTest("서울역-공덕역 구간 추가 실패", addSectionFail(SEOUL, GONGDEOK, 200))
        );
    }

    @DisplayName("기존 노선 가운데에 새 구간을 등록하는 경우 기존 역 사이 간격보다 크거나 같지 않아야 한다.")
    @TestFactory
    Stream<DynamicTest> addSectionFailBecauseOfDistance02() {
        return Stream.of(
            dynamicTest("공항철도 노선 생성(서울역-홍대입구역)", createLineSuccess(AIRPORT_EXPRESS_SEOUL_TO_HONGIK)),
            dynamicTest("공덕역-홍대입구역 구간 추가 실패", addSectionFail(GONGDEOK, HONGIK_UNIV, 200))
        );
    }

    @DisplayName("추가하려는 상/하행역이 기존 노선에 이미 등록되어 있다면 추가할 수 없다.")
    @TestFactory
    Stream<DynamicTest> addSectionFailBecauseOfDuplicate01() {
        return Stream.of(
            dynamicTest("공항철도 노선 생성(공덕역-홍대입구역)", createLineSuccess(AIRPORT_EXPRESS_GONGDEOK_TO_HONGIK)),
            dynamicTest("서울역-공덕역 구간 추가 성공", addSectionSuccess(SEOUL, GONGDEOK, 150)),
            dynamicTest("홍대입구역-서울역 구간 추가 실패", addSectionFail(HONGIK_UNIV, SEOUL, 100))
        );
    }

    @DisplayName("추가하려는 상/하행역이 기존 노선에 이미 등록되어 있다면 추가할 수 없다.")
    @TestFactory
    Stream<DynamicTest> addSectionFailBecauseOfDuplicate02() {
        return Stream.of(
            dynamicTest("공항철도 노선 생성(공덕역-홍대입구역)", createLineSuccess(AIRPORT_EXPRESS_GONGDEOK_TO_HONGIK)),
            dynamicTest("홍대입구역-공덕역 구간 추가 실패", addSectionFail(HONGIK_UNIV, GONGDEOK, 100))
        );
    }

    @DisplayName("추가하려는 상/하행역 중 하나라도 기존 노선에 포함되어 있지 않다면 추가할 수 없다.")
    @TestFactory
    Stream<DynamicTest> addSectionFailTest01() {
        return Stream.of(
            dynamicTest("공항철도 노선 생성(공덕역-홍대입구역)", createLineSuccess(AIRPORT_EXPRESS_GONGDEOK_TO_HONGIK)),
            dynamicTest("김포공항-계양역 구간 추가 실패", addSectionFail(GIMPO_AIRPORT, GYEYANG, 100))
        );
    }

    @DisplayName("노선에 구간이 1개 남은 경우 삭제 불가능")
    @TestFactory
    Stream<DynamicTest> removeSectionFailTest01() {
        return Stream.of(
            dynamicTest("공항철도 노선 생성(공덕역-홍대입구역)", createLineSuccess(AIRPORT_EXPRESS_GONGDEOK_TO_HONGIK)),
            dynamicTest("공덕역 구간 삭제 요청 실패", removeSectionFail(GONGDEOK)),
            dynamicTest("홍대입구역 구간 삭제 요청 실패 ", removeSectionFail(HONGIK_UNIV))
        );
    }

    @DisplayName("상행 종점 구간 삭제")
    @TestFactory
    Stream<DynamicTest> removeSectionTest01() {
        return Stream.of(
            dynamicTest("공항철도 노선 생성(공덕역-홍대입구역)", createLineSuccess(AIRPORT_EXPRESS_GONGDEOK_TO_HONGIK)),
            dynamicTest("서울역-공덕역 구간 추가 성공", addSectionSuccess(SEOUL, GONGDEOK, 150)),
            dynamicTest("서울역 구간 삭제 성공", removeSectionSuccess(SEOUL)),
            dynamicTest("구간 개수 확인", findSectionAndSizeIs(1)),
            dynamicTest("공덕역-홍대입구역 구간 확인", findSectionAndCheck(GONGDEOK, HONGIK_UNIV, 100))
        );
    }

    @DisplayName("하행 종점 구간 삭제")
    @TestFactory
    Stream<DynamicTest> removeSectionTest02() {
        return Stream.of(
            dynamicTest("공항철도 노선 생성(공덕역-홍대입구역)", createLineSuccess(AIRPORT_EXPRESS_GONGDEOK_TO_HONGIK)),
            dynamicTest("서울역-공덕역 구간 추가 성공", addSectionSuccess(SEOUL, GONGDEOK, 150)),
            dynamicTest("홍대입구역 구간 삭제 성공", removeSectionSuccess(HONGIK_UNIV)),
            dynamicTest("구간 개수 확인", findSectionAndSizeIs(1)),
            dynamicTest("서울역-공덕역 구간 확인", findSectionAndCheck(SEOUL, GONGDEOK, 150))
        );
    }

    @DisplayName("종점이 아닌 구간 삭제 시 빈 구간이 연결되어야 한다.")
    @TestFactory
    Stream<DynamicTest> removeSectionTest03() {
        return Stream.of(
            dynamicTest("공항철도 노선 생성(공덕역-홍대입구역)", createLineSuccess(AIRPORT_EXPRESS_GONGDEOK_TO_HONGIK)),
            dynamicTest("서울역-공덕역 구간 추가 성공", addSectionSuccess(SEOUL, GONGDEOK, 150)),
            dynamicTest("공덕역 구간 삭제 성공", removeSectionSuccess(GONGDEOK)),
            dynamicTest("구간 개수 확인", findSectionAndSizeIs(1)),
            dynamicTest("서울역-홍대입구 구간 확인", findSectionAndCheck(SEOUL, HONGIK_UNIV, 250))
        );
    }

    @DisplayName("서울역 ~ 계양역 구간 생성 후 서울역, DMC역, 김포공항역 삭제")
    @TestFactory
    Stream<DynamicTest> removeSectionTest04() {
        return Stream.of(
            dynamicTest("공항철도 노선 생성(공덕역-홍대입구역, 길이 100)", createLineSuccess(AIRPORT_EXPRESS_GONGDEOK_TO_HONGIK)),
            dynamicTest("서울역-공덕역 구간 추가 성공", addSectionSuccess(SEOUL, GONGDEOK, 50)),
            dynamicTest("홍대입구역-DMC역 구간 추가 성공", addSectionSuccess(HONGIK_UNIV, DMC, 100)),
            dynamicTest("DMC역-마곡나루 구간 추가 성공", addSectionSuccess(DMC, MAGONGNARU, 150)),
            dynamicTest("마곡나루역-김포공항역 구간 추가 성공", addSectionSuccess(MAGONGNARU, GIMPO_AIRPORT, 200)),
            dynamicTest("김포공항역-계양역 구간 추가 성공", addSectionSuccess(GIMPO_AIRPORT, GYEYANG, 250)),
            dynamicTest("서울역 구간 삭제 요청 성공", removeSectionSuccess(SEOUL)),
            dynamicTest("구간 개수 확인", findSectionAndSizeIs(5)),
            dynamicTest("DMC역 구간 삭제 요청 성공", removeSectionSuccess(DMC)),
            dynamicTest("홍대입구역-마곡나루역 구간 확인", findSectionAndCheck(HONGIK_UNIV, MAGONGNARU, 250)),
            dynamicTest("김포공항역 구간 삭제 성공", removeSectionSuccess(GIMPO_AIRPORT)),
            dynamicTest("마곡나루역-계양역 구간 확인", findSectionAndCheck(MAGONGNARU, GYEYANG, 450))
        );
    }

    private Executable createLineSuccess(LineTestData data) {
        return () -> {
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
        };
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

    private Executable addSectionSuccess(StationConstants upStation, StationConstants downStation, int distance) {
        return () -> {
            ExtractableResponse<Response> response = addSectionRequest(upStation.getId(),
                downStation.getId(),
                distance);

            assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

            testSection(upStation, downStation, distance, response);
        };
    }

    private void testSection(StationConstants upStation, StationConstants downStation, int distance,
        ExtractableResponse<Response> response) {
        Map<String, Object> map = new HashMap<>();
        map.put("upStationName", upStation.getName());
        map.put("downStationName", downStation.getName());
        map.put("distance", distance);

        assertThat(response.body().jsonPath().getList("sections")).contains(map);
    }


    private Executable addSectionFail(StationConstants upStation, StationConstants downStation, int distance) {
        return () -> {
            ExtractableResponse<Response> response = addSectionRequest(upStation.getId(),
                downStation.getId(),
                distance);

            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        };
    }

    private ExtractableResponse<Response> removeSectionRequest(StationConstants station) {
        return RestAssured.given().log().all()
            .when().delete("/lines/1/sections?stationId=" + station.getId())
            .then().log().all()
            .extract();
    }

    private Executable removeSectionSuccess(StationConstants station) {
        return () -> {
            ExtractableResponse<Response> response = removeSectionRequest(station);
            assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        };
    }

    private Executable removeSectionFail(StationConstants station) {
        return () -> {
            ExtractableResponse<Response> response = removeSectionRequest(station);
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        };
    }

    private ExtractableResponse<Response> getSectionRequest() {
        return RestAssured.given().log().all()
            .when().get("/lines/1/sections")
            .then().log().all()
            .extract();
    }

    private Executable findSectionAndCheck(StationConstants upStation, StationConstants downStation, int distance) {
        return () -> {
            ExtractableResponse<Response> response = getSectionRequest();
            testSection(upStation, downStation, distance, response);
        };
    }

    private Executable findSectionAndSizeIs(int sectionSize) {
        return () -> {
            ExtractableResponse<Response> response = getSectionRequest();
            assertThat(response.body().jsonPath().getList("sections")).hasSize(sectionSize);
        };
    }
}
