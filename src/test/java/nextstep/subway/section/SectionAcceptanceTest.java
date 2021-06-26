package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;


public class SectionAcceptanceTest extends AcceptanceTest {

    private static int LINE_DISTANCE = 10;
    private StationResponse 문래역;
    private StationResponse 잠실역;
    private StationResponse 사당역;
    private String lineURL;

    @BeforeEach
    public void setUp() {
        super.setUp();

        문래역 = StationAcceptanceTest.지하철역_생성_요청("문래역").as(StationResponse.class);
        잠실역 = StationAcceptanceTest.지하철역_생성_요청("잠실역").as(StationResponse.class);
        사당역 = StationAcceptanceTest.지하철역_생성_요청("사당역").as(StationResponse.class);
        lineURL = LineAcceptanceTest.지하철_노선_등록되어_있음("2호선", "green", 문래역.getId(), 잠실역.getId(), LINE_DISTANCE);

    }


    @DisplayName("역 사이에 새로운 역을 등록할 경우 상행역 기반 ")
    @Test
    void addSectionInMiddleBased() {

        int newSectionDistance = LINE_DISTANCE - 1;
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(lineURL, 문래역.getId(), 사당역.getId(), newSectionDistance);


        List<Long> expectedStations = Arrays.asList(문래역.getId(), 사당역.getId(), 잠실역.getId());
        List<Integer> expectedDistances = Arrays.asList(0, newSectionDistance, LINE_DISTANCE - newSectionDistance);

        지하철_노선에_지하철역_등록됨(response);
        지하철_노선에_등록한_구간_포함됨(response, expectedStations);
        지하철_노선_구간_거리_계산됨(response, expectedDistances);
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 하행역 기반 ")
    @Test
    void addSectionInMiddleBased2() {

        int newSectionDistance = LINE_DISTANCE - 1;
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(lineURL, 사당역.getId(), 잠실역.getId(), newSectionDistance);


        List<Long> expectedStations = Arrays.asList(문래역.getId(), 사당역.getId(), 잠실역.getId());
        List<Integer> expectedDistances = Arrays.asList(0, LINE_DISTANCE - newSectionDistance, newSectionDistance);
        지하철_노선에_지하철역_등록됨(response);
        지하철_노선에_등록한_구간_포함됨(response, expectedStations);
        지하철_노선_구간_거리_계산됨(response, expectedDistances);
    }

    @DisplayName("새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void addSectionAtFirstBased() {

        int newSectionDistance = LINE_DISTANCE - 1;
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(lineURL, 사당역.getId(), 문래역.getId(), newSectionDistance);

        List<Long> expectedStations = Arrays.asList(사당역.getId(), 문래역.getId(), 잠실역.getId());
        List<Integer> expectedDistances = Arrays.asList(0, newSectionDistance, LINE_DISTANCE);


        지하철_노선에_지하철역_등록됨(response);
        지하철_노선에_등록한_구간_포함됨(response, expectedStations);
        지하철_노선_구간_거리_계산됨(response, expectedDistances);
    }


    @DisplayName("새로운 역을 하행 종점으로 등록할 경우")
    @Test
    void addSectionAtLastBased() {
        int newSectionDistance = LINE_DISTANCE - 1;
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(lineURL, 잠실역.getId(), 사당역.getId(), newSectionDistance);

        List<Long> expectedStations = Arrays.asList(문래역.getId(), 잠실역.getId(), 사당역.getId());
        List<Integer> expectedDistances = Arrays.asList(0, LINE_DISTANCE, newSectionDistance);


        지하철_노선에_지하철역_등록됨(response);
        지하철_노선에_등록한_구간_포함됨(response, expectedStations);
        지하철_노선_구간_거리_계산됨(response, expectedDistances);

    }

    @DisplayName("노선에 구간의 길이보다 긴 거리로 등록하면 등록할 수 없는 테스트")
    @Test
    void addSectionGreaterDistance() {

        int newSectionDistance = LINE_DISTANCE + 1;
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(lineURL, 문래역.getId(), 사당역.getId(), newSectionDistance);

        지하철_노선에_유효하지않은_구간정보(response);
    }


    @DisplayName("노선에 이미 등록된 역들로 구간을 등록할 수 없는 테스트")
    @Test
    void addAlreadyExistStations() {

        int newSectionDistance = LINE_DISTANCE + 1;
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(lineURL, 문래역.getId(), 잠실역.getId(), newSectionDistance);

        지하철_노선에_유효하지않은_구간정보(response);
    }

    @DisplayName("노선에 등록된 역이 하나도 없는 구간은 등록할 수 없는 테스트")
    @Test
    void addNoExistStations() {
        int newSectionDistance = LINE_DISTANCE - 1;
        StationResponse 신사역 = StationAcceptanceTest.지하철역_생성_요청("신사역").as(StationResponse.class);
        StationResponse 용산역 = StationAcceptanceTest.지하철역_생성_요청("용산역").as(StationResponse.class);

        ExtractableResponse<Response> response =
                지하철_노선에_지하철역_등록_요청(lineURL, 신사역.getId(), 용산역.getId(), newSectionDistance);

        지하철_노선에_유효하지않은_구간정보(response);
    }


    @DisplayName("노선에 구간세 사이를 제거한다.")
    @Test
    void deleteMiddleSection() {
        // given
        지하철_노선에_지하철역_등록_요청(lineURL, 문래역.getId(), 사당역.getId(), 5);

        ExtractableResponse<Response> response = 지하철_노선에_지하철역_제거_요청(lineURL, 사당역.getId());

        // then
        List<Long> expectedStations = Arrays.asList(문래역.getId(), 잠실역.getId());
        List<Integer> expectedDistances = Arrays.asList(0, 10);
        지하철_노선에_지하철역_제거됨(response);
        지하철_노선에_등록한_구간_포함됨(response, expectedStations);
        지하철_노선_구간_거리_계산됨(response, expectedDistances);
    }


    @DisplayName("노선에 구간에 상향 쪽을 제거한다.")
    @Test
    void deleteStartSection() {
        // given
        지하철_노선에_지하철역_등록_요청(lineURL, 잠실역.getId(), 사당역.getId(),5);

        // when
        ExtractableResponse<Response> response2 = 지하철_노선에_지하철역_제거_요청(lineURL, 문래역.getId());

        // then
        List<Long> expectedStations2 = Arrays.asList(잠실역.getId(), 사당역.getId());
        List<Integer> expectedDistances2 = Arrays.asList(0, 5);
        지하철_노선에_지하철역_제거됨(response2);
        지하철_노선에_등록한_구간_포함됨(response2, expectedStations2);
        지하철_노선_구간_거리_계산됨(response2, expectedDistances2);
    }

    @DisplayName("노선에 구간에 하향 쪽을 제거한다.")
    @Test
    void deleteLastSection() {
        // given
        지하철_노선에_지하철역_등록_요청(lineURL, 잠실역.getId(), 사당역.getId(),5);

        // when
        ExtractableResponse<Response> response2 = 지하철_노선에_지하철역_제거_요청(lineURL, 사당역.getId());

        // then
        List<Long> expectedStations2 = Arrays.asList(문래역.getId(), 잠실역.getId());
        List<Integer> expectedDistances2 = Arrays.asList(0, 10);
        지하철_노선에_지하철역_제거됨(response2);
        지하철_노선에_등록한_구간_포함됨(response2, expectedStations2);
        지하철_노선_구간_거리_계산됨(response2, expectedDistances2);
    }



    @DisplayName("노선에 등록되지 않은 역은 제거할 수 없다")
    @Test
    void deleteNoExistStations() {

        StationResponse 신사역 = StationAcceptanceTest.지하철역_생성_요청("신사역").as(StationResponse.class);
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_제거_요청(lineURL, 신사역.getId());

        지하철_노선에_유효하지않은_구간정보는_제거되지않음(response);
    }

    @DisplayName("노선에 등록된 구간이 1개일 때, 상행역을 제거할 수 없다")
    @Test
    void deleteUpStationInOnlyOneSections() {
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_제거_요청(lineURL, 문래역.getId());

        지하철_노선에_유효하지않은_구간정보는_제거되지않음(response);
    }

    @DisplayName("노선에 등록된 구간이 1개일 때, 하행역을 제거할 수 없다")
    @Test
    void deleteDownStationInOnlyOneSections() {
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_제거_요청(lineURL, 잠실역.getId());

        지하철_노선에_유효하지않은_구간정보는_제거되지않음(response);
    }

    private void 지하철_노선에_유효하지않은_구간정보는_제거되지않음(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> 지하철_노선에_지하철역_제거_요청(String lineURL, Long stationId) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete(lineURL + "/sections?stationId=" + stationId)
                .then().log().all().extract();
    }


    private void 지하철_노선에_유효하지않은_구간정보(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void 지하철_노선_구간_거리_계산됨(ExtractableResponse<Response> response, List<Integer> expectedDistances) {
        List<Integer> resultStationIds = response.jsonPath().getList("stations", SectionResponse.class).stream()
                .map(SectionResponse::getDistance)
                .collect(Collectors.toList());
        assertThat(resultStationIds).isEqualTo(expectedDistances);
    }

    private void 지하철_노선에_등록한_구간_포함됨(ExtractableResponse<Response> response, List<Long> expectedStationIDds) {
        List<Long> resultStationIds = response.jsonPath().getList("stations", SectionResponse.class).stream()
                .map(SectionResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultStationIds).isEqualTo(expectedStationIDds);
    }

    private void 지하철_노선에_지하철역_제거됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선에_지하철역_등록됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static ExtractableResponse<Response> 지하철_노선에_지하철역_등록_요청(String lineUri, Long upStationId, Long downStationId, int distance) {
        SectionRequest request = createSectionRequest(upStationId, downStationId, distance);

        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(lineUri + "/sections")
                .then().log().all().extract();
    }

    private static SectionRequest createSectionRequest(Long upStationId, Long downStationId, int distance) {
        return new SectionRequest(upStationId, downStationId, distance);
    }

}
