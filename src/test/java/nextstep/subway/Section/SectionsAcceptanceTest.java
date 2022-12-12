package nextstep.subway.Section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.StationRequest;
import nextstep.subway.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@DisplayName("지하철 노선 관련 기능")
public class SectionsAcceptanceTest extends AcceptanceTest {
    private final long DEFAULT_DISTANCE = 10;
    private StationResponse 강남역;
    private StationResponse 광교역;
    private StationResponse 정자역;
    private StationResponse 광교중앙역;
    private LineResponse 신분당선;

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
        강남역 = registerStation("강남역").as(StationResponse.class);
        광교역 = registerStation("광교역").as(StationResponse.class);
        정자역 = registerStation("정자역").as(StationResponse.class);
        광교중앙역 = registerStation("광교중앙역").as(StationResponse.class);
        신분당선 = registerLine("신분당선", "red", 강남역.getId(), 광교역.getId(), DEFAULT_DISTANCE).as(LineResponse.class);
    }

    @DisplayName("구간(Section) 추가 테스트(상행과 일치)")
    @Test
    void addSectionWhenIsSameUpStation() {
        ExtractableResponse<Response> response =
                addSection(generateSectionRequest(강남역.getId(), 정자역.getId(), 4), 신분당선.getId());
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("구간(Section) 추가 테스트(하행과 일치)")
    @Test
    void addSectionWhenIsSameDownStation() {
        ExtractableResponse<Response> response =
                addSection(generateSectionRequest(정자역.getId(), 광교역.getId(), 4), 신분당선.getId());
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("구간(Section) 추가 시 구간 길이로 인한 예외 테스트(하행과 일치)")
    @Test
    void makeExceptionToAddSectionWhenIsSameDownStationDistanceException() {
        ExtractableResponse<Response> response = addSection(generateSectionRequest(정자역.getId(), 광교역.getId(), DEFAULT_DISTANCE), 신분당선.getId());
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("구간(Section) 추가 시 구간 길이로 인한 예외 테스트(상행과 일치)")
    @Test
    void makeExceptionToAddSectionWhenIsSameUpStationDistanceException() {
        ExtractableResponse<Response> response = addSection(generateSectionRequest(강남역.getId(), 광교역.getId(), DEFAULT_DISTANCE), 신분당선.getId());
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("구간(Section) 추가 시 일치하는 Station 없을 시 예외")
    @Test
    void makeExceptionWhenNoMatchBothStation() {
        ExtractableResponse<Response> response = addSection(generateSectionRequest(광교중앙역.getId(), 정자역.getId(), 5), 신분당선.getId());
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("구간(Section) 추가 시 이미 있는 Section인 경우 예외")
    @Test
    void makeExceptionWhenAddSameSection() {
        ExtractableResponse<Response> response = addSection(generateSectionRequest(강남역.getId(), 광교역.getId(), 5), 신분당선.getId());
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("상행 종점 추가 테스트 ")
    @Test
    void addSectionWhenIsSameUpEndStation() {
        ExtractableResponse<Response> response = addSection(generateSectionRequest(정자역.getId(), 강남역.getId(), 5), 신분당선.getId());
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("하행 종점 추가 테스트 ")
    @Test
    void addSectionWhenIsSameDownEndStation() {
        ExtractableResponse<Response> response = addSection(generateSectionRequest(광교역.getId(), 광교중앙역.getId(), 5), 신분당선.getId());
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("구간(section) 추가 시 station이 모두 존재하면 예외")
    @TestFactory
    Stream<DynamicTest> makeExceptionWhenMatchBothStation() {
        return Stream.of(
                dynamicTest("테스트를 위한 section 추가", () -> {
                    ExtractableResponse<Response> response = addSection(generateSectionRequest(강남역.getId(), 정자역.getId(), 5), 신분당선.getId());
                    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                }),
                dynamicTest("기존 존재하는 station으로 section 추가하여 예외 발생", () -> {
                    ExtractableResponse<Response> response = addSection(generateSectionRequest(강남역.getId(), 광교역.getId(), 5), 신분당선.getId());
                    assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
                })
        );
    }

    @DisplayName("구간삭제 요청 파라미터에 대한 예외 검증 - LINE이 존재하지 않은 경우")
    @Test
    void makeExceptionWhenNoSuchLine() {
        ExtractableResponse<Response> response = removeLineStation(2, 강남역.getId());
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("구간삭제 요청 파라미터에 대한 예외 검증 - LINE이 존재하지 않은 경우")
    @Test
    void makeExceptionWhenNoSuchStation() {
        ExtractableResponse<Response> response = removeLineStation(신분당선.getId(), 99);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("구간삭제 요청 파라미터에 대한 예외 검증 - Line의 Sections의 size가 2 이하인 경우")
    @Test
    void makeExceptionWhenHasNotEnoughSectionToRemove() {
        ExtractableResponse<Response> response = removeLineStation(신분당선.getId(), 강남역.getId());
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("구간삭제 요청 파라미터에 대한 예외 검증 - 삭제 요청 Station이 요청한 Line에 등록되지 않은 경우")
    @TestFactory
    Stream<DynamicTest> makeExceptionAboutRemoveSectionsRequestParameter() {
        return Stream.of(
                dynamicTest("LINE에 Section을 추가하여 삭제 가능 사이즈를 만들어줌", () -> {
                    ExtractableResponse<Response> response = addSection(generateSectionRequest(강남역.getId(), 정자역.getId(), 4), 신분당선.getId());
                    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                }),
                dynamicTest("강남-정자-광교 노선에서 광교중앙역 삭제 요청", () -> {
                    ExtractableResponse<Response> response = removeLineStation(신분당선.getId(), 광교중앙역.getId());
                    assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
                })
        );
    }

    @DisplayName("종점삭제 - 상행 종점 구간 제거 테스트")
    @TestFactory
    Stream<DynamicTest> removeEndUpPointSection() {
        return Stream.of(
                dynamicTest("LINE에 Section을 추가하여 삭제 가능 사이즈를 만들어줌 (강남,광교) ->  (강남,광교) + insert(광교,정자)", () -> {
                    ExtractableResponse<Response> response = addSection(generateSectionRequest(광교역.getId(), 정자역.getId(), 4), 신분당선.getId());
                    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                }),
                dynamicTest("강남-광교-정자 노선에서 강남역 삭제 요청 - section id: 1 삭제", () -> {
                    ExtractableResponse<Response> response = removeLineStation(신분당선.getId(), 강남역.getId());
                    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                }),
                dynamicTest("LINE 조회 요청, 삭제내역 확인", () -> {
                    ExtractableResponse<Response> response = retrieveLine(신분당선.getId());
                    checkTheLineContainStations(response, Arrays.asList(광교역, 정자역));
                })
        );
    }

    @DisplayName("종점삭제 - 하행 종점 구간 제거 테스트")
    @TestFactory
    Stream<DynamicTest> removeEndDownPointSection() {
        return Stream.of(
                dynamicTest("LINE에 Section을 추가하여 삭제 가능 사이즈를 만들어줌 (강남,광교) ->  insert(강남,정자) + (정자,광교)", () -> {
                    ExtractableResponse<Response> response = addSection(generateSectionRequest(강남역.getId(), 정자역.getId(), 4), 신분당선.getId());
                    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                }),
                dynamicTest("강남-정자-광교 노선에서 광교역 삭제 요청 - section id: 1 삭제", () -> {
                    ExtractableResponse<Response> response = removeLineStation(신분당선.getId(), 광교역.getId());
                    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                }),
                dynamicTest("LINE 조회 요청, 삭제내역 확인", () -> {
                    ExtractableResponse<Response> response = retrieveLine(신분당선.getId());
                    checkTheLineContainStations(response, Arrays.asList(강남역, 정자역));
                })
        );
    }

    @DisplayName("종점이 아닌 중간 구간 삭제 테스트")
    @TestFactory
    Stream<DynamicTest> removeIntermediateSection() {
        return Stream.of(
                dynamicTest("LINE에 Section을 추가하여 삭제 가능 사이즈를 만들어줌 (강남,광교) ->  insert(강남,정자) + (정자,광교)", () -> {
                    ExtractableResponse<Response> response = addSection(generateSectionRequest(강남역.getId(), 정자역.getId(), 4), 신분당선.getId());
                    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                }),
                dynamicTest("강남(1)-정자(3)-광교(2) 노선에서 정자역 삭제 요청 - section id: 1 삭제", () -> {
                    ExtractableResponse<Response> response = removeLineStation(신분당선.getId(), 정자역.getId());
                    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                }),
                dynamicTest("LINE 조회 요청, 삭제내역 확인", () -> {
                    ExtractableResponse<Response> response = retrieveLine(신분당선.getId());
                    checkTheLineContainStations(response, Arrays.asList(강남역, 광교역));
                })
        );
    }

    private ExtractableResponse<Response> addSection(SectionRequest sectionRequest, long id) {
        return RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines" + DELIMITER + id + "/sections")
                .then().log().all()
                .extract();
    }

    public ExtractableResponse<Response> registerStation(String name) {
        return RestAssured.given().log().all()
                .body(generateStationRequest(name))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    public ExtractableResponse<Response> registerLine(String name, String color, long upStationId, long downStationId, long distance) {
        return RestAssured.given().log().all()
                .body(generateLineRequest(name, color, upStationId, downStationId, distance))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> removeLineStation(long lineId, long stationId) {
        return RestAssured.given().param("stationId", stationId).log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/lines" + DELIMITER + lineId + DELIMITER + "sections")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> retrieveLine(long lineId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines" + DELIMITER + lineId)
                .then().log().all()
                .extract();
    }

    public static void checkTheLineContainStations(ExtractableResponse<Response> response, List<StationResponse> expectedStations) {
        LineResponse line = response.as(LineResponse.class);
        List<Long> stationIds = line.getStations().stream()
                .map(station -> station.getId())
                .collect(Collectors.toList());

        List<Long> expectedStationIds = expectedStations.stream()
                .map(station -> station.getId())
                .collect(Collectors.toList());

        assertThat(stationIds).containsExactlyElementsOf(expectedStationIds);
    }

    private SectionRequest generateSectionRequest(long upStationId, long downStationId, long distance) {
        return new SectionRequest(upStationId, downStationId, distance);
    }

    private LineRequest generateLineRequest(String name, String color, long upStationId, long downStationId, long distance) {
        return new LineRequest(name, color, upStationId, downStationId, distance);
    }

    private StationRequest generateStationRequest(String name) {
        return new StationRequest(name);
    }

}
