package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.*;
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.utils.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 구간 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SectionAcceptanceTest {

    @LocalServerPort
    int port;

    @Autowired
    DatabaseCleanup databaseCleanup;

    private static final String BASE_URL = "/sections";
    private static final String PARAMETER = "?stationId=";

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        databaseCleanup.execute();
    }

    /**
     * When 지하철 노선에 신규로 구간을 등록하면
     * Then 등록한 구간이 조회된다.
     */
    @DisplayName("노선에 첫 신규 구간을 등록한다.")
    @Test
    void 지하철_노선_첫_구간_생성_테스트() {
        // when
        Station 당산역 = getSavedStation("당산역");
        Station 합정역 = getSavedStation("합정역");

        LineRequest lineRequest = new LineRequest("2호선", "bg-green-600", 당산역, 합정역, 10);
        ExtractableResponse<Response> response = LineAcceptanceTest.createLine(lineRequest);
        LineResponse 이호선 = response.as(LineResponse.class);

        // then
        ExtractableResponse<Response> findResponse = retrieveAllSectionByLine(response.header("Location"));
        List<SectionResponse> sections = getSectionResponses(findResponse);

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(findSpecificSection(sections, 당산역, 합정역)).isNotNull(),
                () -> assertThat(findSpecificSection(sections, 당산역, 합정역).getLineId()).isEqualTo(이호선.getId())
        );
    }

    /**
     * given 기존의 구간에
     * when 새로운 지하철 역을 추가하면
     * then 새로운 구간이 생성된다.
     */
    @DisplayName("구간에 새로운 역을 등록한다.")
    @Test
    void 지하철_구간에_새로운_역_생성_테스트() {
        // given
        Station 당산역 = getSavedStation("당산역");
        Station 홍대입구역 = getSavedStation("홍대입구역");

        LineRequest lineRequest1 = new LineRequest("2호선", "bg-green-600", 당산역, 홍대입구역, 10);
        ExtractableResponse<Response> response = LineAcceptanceTest.createLine(lineRequest1);
        LineResponse 이호선 = response.as(LineResponse.class);

        // when
        Station 합정역 = getSavedStation("합정역");
        SectionRequest lineRequest2 = new SectionRequest(당산역.getId(), 합정역.getId(), 4);
        ExtractableResponse<Response> sectionResponse2 = addSection(response.header("Location"), lineRequest2);

        // then
        ExtractableResponse<Response> findResponse = retrieveAllSectionByLine(response.header("Location"));
        List<SectionResponse> sections = getSectionResponses(findResponse);

        assertAll(
                () -> assertThat(sectionResponse2.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(sections).hasSize(2),
                () -> assertThat(findSpecificSection(sections, 당산역, 합정역)).isNotNull(),
                () -> assertThat(findSpecificSection(sections, 당산역, 합정역).getDistance()).isEqualTo(4),
                () -> assertThat(findSpecificSection(sections, 합정역, 홍대입구역).getDistance()).isEqualTo(6)
        );

    }

    /**
     * Given 지하철 구간을 2번 등록하고
     * When 지하철 노선 전체 구간을 조회하면
     * Then 등록한 구간들이 조회된다.
     */
    @DisplayName("노선의 전체 구간을 조회한다.")
    @Test
    void 지하철_노선_전체_구간_조회_테스트() {
        //given
        Station 당산역 = getSavedStation("당산역");
        Station 홍대입구역 = getSavedStation("홍대입구역");

        LineRequest lineRequest = new LineRequest("2호선", "bg-green-600", 당산역, 홍대입구역, 10);
        ExtractableResponse<Response> response = LineAcceptanceTest.createLine(lineRequest);
        LineResponse 이호선 = response.as(LineResponse.class);

        Station 합정역 = getSavedStation("합정역");
        SectionRequest sectionRequest2 = new SectionRequest(합정역.getId(), 홍대입구역.getId(), 6);
        ExtractableResponse<Response> addSectionResponse2 = addSection(response.header("Location"), sectionRequest2);

        // when
        ExtractableResponse<Response> responses = retrieveAllSectionByLine(response.header("Location"));

        // then
        assertAll(
                () -> assertThat(responses.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(getSectionResponses(responses)).hasSize(2)
        );
    }

    /**
     * Given 기존의 상행 종점역에
     * When 새로운 상행 종점역 생성 요청을 하면
     * Then 지하철 노선에 새로운 상행 종점역이 등록된다.
     */
    @DisplayName("새로운 상행 종점역을 생성한다.")
    @Test
    void 새로운_상행_종점역_생성_테스트() {
        // given
        Station 당산역 = getSavedStation("당산역");
        Station 합정역 = getSavedStation("합정역");

        LineRequest lineRequest = new LineRequest("2호선", "bg-green-600", 당산역, 합정역, 10);
        ExtractableResponse<Response> response = LineAcceptanceTest.createLine(lineRequest);
        LineResponse 이호선 = response.as(LineResponse.class);

        // when
        Station 영등포구청역 = getSavedStation("영등포구청역");

        SectionRequest firstSection = new SectionRequest(영등포구청역.getId(), 당산역.getId(), 17);
        ExtractableResponse<Response> addFirstSectionResponse = addSection(response.header("Location"), firstSection);

        // then
        assertAll(
                () -> assertThat(addFirstSectionResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(findSpecificSection(getSectionResponses(addFirstSectionResponse), 영등포구청역, 당산역)).isNotNull()
        );
    }

    /**
     * Given 기존의 하행 종점역에
     * When 새로운 하행 종점역 생성 요청을 하면
     * Then 지하철 노선에 새로운 하행 종점역이 등록된다.
     */
    @DisplayName("새로운 하행 종점역을 생성한다.")
    @Test
    void 새로운_하행_종점역_생성_테스트() {
        // given
        Station 당산역 = getSavedStation("당산역");
        Station 합정역 = getSavedStation("합정역");

        LineRequest lineRequest = new LineRequest("2호선", "bg-green-600", 당산역, 합정역, 10);
        ExtractableResponse<Response> response = LineAcceptanceTest.createLine(lineRequest);
        LineResponse 이호선 = response.as(LineResponse.class);

        // when
        Station 홍대입구역 = getSavedStation("홍대입구역");

        SectionRequest lastSection = new SectionRequest(합정역.getId(), 홍대입구역.getId(), 7);
        ExtractableResponse<Response> addLastSectionResponse = addSection(response.header("Location"), lastSection);

        ExtractableResponse<Response> findResponse = retrieveAllSectionByLine(response.header("Location"));
        List<SectionResponse> sections = getSectionResponses(findResponse);

        // then
        assertAll(
                () -> assertThat(addLastSectionResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(findSpecificSection(getSectionResponses(addLastSectionResponse), 합정역, 홍대입구역)).isNotNull()
        );
    }

    /**
     * Given 기존 구간에
     * When 기존 구간 거리 이상의 거리를 가진 새로운 역을 등록 하면
     * Then 등록이 거부된다.
     */
    @DisplayName("기존 구간 이상 거리의 새로운 역을 등록한다.")
    @Test
    void 기존_구간_거리_이상_거리의_역_추가_테스트() {
        // given
        Station 당산역 = getSavedStation("당산역");
        Station 홍대입구역 = getSavedStation("홍대입구역");

        LineRequest lineRequest1 = new LineRequest("2호선", "bg-green-600", 당산역, 홍대입구역, 10);
        ExtractableResponse<Response> response = LineAcceptanceTest.createLine(lineRequest1);
        LineResponse 이호선 = response.as(LineResponse.class);

        // when
        Station 합정역 = getSavedStation("합정역");

        SectionRequest lineRequest2 = new SectionRequest(당산역.getId(), 합정역.getId(), 15);
        ExtractableResponse<Response> sectionResponse2 = addSection(response.header("Location"), lineRequest2);

        // then
        assertThat(sectionResponse2.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 기존 구간에 등록된
     * When 상행역과 하행역을 가진 구간을 등록하면
     * Then 등록이 거부된다.
     */
    @DisplayName("기존에 등록된 상행역과 하행역의 구간을 등록한다.")
    @Test
    void 기존에_등록된_상하행역_등록_테스트() {
        // given
        Station 당산역 = getSavedStation("당산역");
        Station 합정역 = getSavedStation("합정역");

        LineRequest lineRequest = new LineRequest("2호선", "bg-green-600", 당산역, 합정역, 10);
        ExtractableResponse<Response> response = LineAcceptanceTest.createLine(lineRequest);
        LineResponse 이호선 = response.as(LineResponse.class);

        // when
        SectionRequest section2 = new SectionRequest(당산역.getId(), 합정역.getId(), 10);
        ExtractableResponse<Response> addSectionResponse2 = addSection(response.header("Location"), section2);

        // then
        assertThat(addSectionResponse2.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 기존 구간에 등록되지 않은
     * When 상행역과 하행역을 가진 구간을 등록하면
     * Then 등록이 거부된다.
     */
    @DisplayName("기존에 등록되지 않은 상행역과 하행역의 구간을 등록한다.")
    @Test
    void 기존에_등록되지_않은_상하행역_등록_테스트() {

        // given
        Station 당산역 = getSavedStation("당산역");
        Station 합정역 = getSavedStation("합정역");

        LineRequest lineRequest = new LineRequest("2호선", "bg-green-600", 당산역, 합정역, 10);
        ExtractableResponse<Response> response = LineAcceptanceTest.createLine(lineRequest);
        LineResponse 이호선 = response.as(LineResponse.class);

        // when
        Station 여의도역 = getSavedStation("여의도역");
        Station 여의나루역 = getSavedStation("여의나루역");
        SectionRequest section2 = new SectionRequest(여의도역.getId(), 여의나루역.getId(), 3);
        ExtractableResponse<Response> addSectionResponse2 = addSection(response.header("Location"), section2);

        // then
        assertThat(addSectionResponse2.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

    }

    /**
     * given 기존의 구간에
     * when 새로운 지하철 구간들을 추가하고 조회하면
     * then 정렬된 구간이 조회된다.
     */
    @DisplayName("지하철 구간 정렬 테스트")
    @Test
    void 지하철_전체_구간_정렬_조회_테스트() {
        // given
        Station 당산역 = getSavedStation("당산역");
        Station 신촌역 = getSavedStation("신촌역");

        LineRequest lineRequest1 = new LineRequest("2호선", "bg-green-600", 당산역, 신촌역, 10);
        ExtractableResponse<Response> response = LineAcceptanceTest.createLine(lineRequest1);
        LineResponse 이호선 = response.as(LineResponse.class);

        // when
        Station 이대역 = getSavedStation("이대역");
        SectionRequest lineRequest2 = new SectionRequest(신촌역.getId(), 이대역.getId(), 4);
        ExtractableResponse<Response> sectionResponse2 = addSection(response.header("Location"), lineRequest2);

        Station 합정역 = getSavedStation("합정역");
        SectionRequest lineRequest3 = new SectionRequest(당산역.getId(), 합정역.getId(), 4);
        ExtractableResponse<Response> sectionResponse3 = addSection(response.header("Location"), lineRequest3);

        Station 홍대입구역 = getSavedStation("홍대입구역");
        SectionRequest lineRequest4 = new SectionRequest(홍대입구역.getId(), 신촌역.getId(), 4);
        ExtractableResponse<Response> sectionResponse4 = addSection(response.header("Location"), lineRequest4);

        Station 영등포구청역 = getSavedStation("영등포구청역");
        SectionRequest lineRequest5 = new SectionRequest(영등포구청역.getId(), 당산역.getId(), 4);
        ExtractableResponse<Response> sectionResponse5 = addSection(response.header("Location"), lineRequest5);

        ExtractableResponse<Response> findResponse = retrieveAllSectionByLine(response.header("Location"));
        List<SectionResponse> sections = getSectionResponses(findResponse);

        // then
        List<String> answers = new ArrayList<>(Arrays.asList(new String[]{"영등포구청역", "당산역", "합정역", "홍대입구역", "신촌역"}));
        List<String> upStations = sections.stream()
                .map(sectionResponse -> sectionResponse.getUpStation().getName())
                .collect(Collectors.toList());

        assertThat(upStations).isEqualTo(answers);

    }

    /**
     * Given 노선에 등록된 구간 중
     * When 상행종점역을 삭제하면
     * Then 노선에서 상행종점역이 삭제된다.
     */
    @DisplayName("구간에서 상행종점역을 삭제한다.")
    @Test
    void 구간의_상행_종점역_삭제_테스트() {

        // given
        Station 영등포구청역 = getSavedStation("영등포구청역");
        Station 당산역 = getSavedStation("당산역");
        Station 합정역 = getSavedStation("합정역");
        Station 홍대입구역 = getSavedStation("홍대입구역");

        LineRequest lineRequest = new LineRequest("2호선", "bg-green-600", 영등포구청역, 당산역, 15);
        ExtractableResponse<Response> response = LineAcceptanceTest.createLine(lineRequest);
        LineResponse 이호선 = response.as(LineResponse.class);

        SectionRequest section1 = new SectionRequest(당산역.getId(), 합정역.getId(), 10);
        addSection(response.header("Location"), section1);
        SectionRequest section2 = new SectionRequest(합정역.getId(), 홍대입구역.getId(), 10);
        addSection(response.header("Location"), section2);

        // when
        ExtractableResponse<Response> deleteResponse = deleteStationById(response.header("Location"), getStationId(영등포구청역));
        ExtractableResponse<Response> responses = retrieveAllSectionByLine(response.header("Location"));
        List<SectionResponse> sections = getSectionResponses(responses);

        // then
         assertAll(
                 () -> assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.OK.value()),
                 () -> assertThat(findSpecificSection(sections, 영등포구청역, 당산역)).isNull()
        );
    }


    /**
     * Given 노선에 등록된 구간 중
     * When 하행종점역을 삭제하면
     * Then 노선에서 하행종점역이 삭제된다.
     */
    @DisplayName("구간에서 하행종점역을 삭제한다.")
    @Test
    void 구간의_하행_종점역_삭제_테스트() {

        // given
        Station 영등포구청역 = getSavedStation("영등포구청역");
        Station 당산역 = getSavedStation("당산역");
        Station 합정역 = getSavedStation("합정역");
        Station 홍대입구역 = getSavedStation("홍대입구역");

        LineRequest lineRequest = new LineRequest("2호선", "bg-green-600", 영등포구청역, 당산역, 15);
        ExtractableResponse<Response> response = LineAcceptanceTest.createLine(lineRequest);
        LineResponse 이호선 = response.as(LineResponse.class);

        SectionRequest section1 = new SectionRequest(당산역.getId(), 합정역.getId(), 10);
        addSection(response.header("Location"), section1);
        SectionRequest section2 = new SectionRequest(합정역.getId(), 홍대입구역.getId(), 10);
        addSection(response.header("Location"), section2);

        // when
        ExtractableResponse<Response> deleteResponse = deleteStationById(response.header("Location"), getStationId(홍대입구역));
        ExtractableResponse<Response> responses = retrieveAllSectionByLine(response.header("Location"));
        List<SectionResponse> sections = getSectionResponses(responses);

        // then
        assertAll(
                () -> assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(findSpecificSection(sections, 합정역, 홍대입구역)).isNull()
        );
    }

    /**
     * Given 노선에 등록된 구간 중
     * When 중간 구간의 특정 역을 삭제하면
     * Then 노선에서 해당 역이 삭제된다.
     */
    @DisplayName("중간 구간의 특정 역을 삭제한다.")
    @Test
    void 중간역_삭제_테스트() {

        // given
        Station 영등포구청역 = getSavedStation("영등포구청역");
        Station 당산역 = getSavedStation("당산역");
        Station 합정역 = getSavedStation("합정역");
        Station 홍대입구역 = getSavedStation("홍대입구역");

        LineRequest lineRequest = new LineRequest("2호선", "bg-green-600", 영등포구청역, 당산역, 15);
        ExtractableResponse<Response> response = LineAcceptanceTest.createLine(lineRequest);
        LineResponse 이호선 = response.as(LineResponse.class);

        SectionRequest section1 = new SectionRequest(당산역.getId(), 합정역.getId(), 10);
        addSection(response.header("Location"), section1);
        SectionRequest section2 = new SectionRequest(합정역.getId(), 홍대입구역.getId(), 10);
        addSection(response.header("Location"), section2);

        // when
        ExtractableResponse<Response> deleteResponse = deleteStationById(response.header("Location"), getStationId(합정역));
        ExtractableResponse<Response> responses = retrieveAllSectionByLine(response.header("Location"));
        List<SectionResponse> sections = getSectionResponses(responses);

        // then
        assertAll(
                () -> assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(findSpecificSection(sections, 합정역, 홍대입구역)).isNull(),
                () -> assertThat(findSpecificSection(sections, 당산역, 홍대입구역)).isNotNull(),
                () -> assertThat(findSpecificSection(sections, 당산역, 홍대입구역).getDistance()).isEqualTo(20)
        );
    }

    /**
     * Given 노선의 구간에
     * When 등록되지 않은 역을 삭제하는 경우
     * Then 역을 삭제하지 않는다
     */
    @DisplayName("등록되지 않은 역 삭제 시 예외처리 테스트")
    @Test
    void 등록되지_않은_역_삭제_테스트() {

        // given
        Station 영등포구청역 = getSavedStation("영등포구청역");
        Station 당산역 = getSavedStation("당산역");
        Station 합정역 = getSavedStation("합정역");

        LineRequest lineRequest = new LineRequest("2호선", "bg-green-600", 영등포구청역, 당산역, 20);
        ExtractableResponse<Response> response = LineAcceptanceTest.createLine(lineRequest);
        LineResponse 이호선 = response.as(LineResponse.class);

        SectionRequest section1 = new SectionRequest(당산역.getId(), 합정역.getId(), 10);
        addSection(response.header("Location"), section1);

        // when
        Station 서울역 = getSavedStation("서울역");
        ExtractableResponse<Response> deleteResponse = deleteStationById(response.header("Location"), getStationId(서울역));
        ExtractableResponse<Response> responses = retrieveAllSectionByLine(response.header("Location"));
        List<SectionResponse> sections = getSectionResponses(responses);

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 하나의 구간이 등록된 노선에서
     * When 역을 삭제하는 경우
     * Then 역을 삭제하지 않는다
     */
    @DisplayName("구간이 하나인 노선에서 역 삭제 시 예외처리 테스트")
    @Test
    void 구간이_하나인_노선의_역_삭제_예외처리_테스트() {

        // given
        Station 영등포구청역 = getSavedStation("영등포구청역");
        Station 당산역 = getSavedStation("당산역");

        LineRequest lineRequest = new LineRequest("2호선", "bg-green-600", 영등포구청역, 당산역, 20);
        ExtractableResponse<Response> response = LineAcceptanceTest.createLine(lineRequest);
        LineResponse 이호선 = response.as(LineResponse.class);

        // when
        ExtractableResponse<Response> deleteResponse = deleteStationById(response.header("Location"), getStationId(당산역));
        ExtractableResponse<Response> responses = retrieveAllSectionByLine(response.header("Location"));
        List<SectionResponse> sections = getSectionResponses(responses);

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> addSection(String location, SectionRequest sectionRequest) {

        ExtractableResponse<Response> response =
                RestAssured.given().log().all()
                        .body(sectionRequest)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().post(location + BASE_URL)
                        .then().log().all()
                        .extract();

        return response;
    }

    private ExtractableResponse<Response> retrieveAllSectionByLine(String location) {

        ExtractableResponse<Response> response =
                RestAssured.given().log().all()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().get(location + BASE_URL)
                        .then().log().all()
                        .extract();

        return response;
    }

    private ExtractableResponse<Response> deleteStationById(String location, String stationId) {

        ExtractableResponse<Response> response =
                RestAssured.given().log().all()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().delete(location + BASE_URL + PARAMETER + stationId)
                        .then().log().all()
                        .extract();

        return response;
    }

    private Station getSavedStation(String stationName) {
        return StationAcceptanceTest.createStation(stationName).as(Station.class);
    }

    private List<SectionResponse> getSectionResponses(ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", SectionResponse.class);
    }

    private SectionResponse findSpecificSection(List<SectionResponse> sections, Station upStation, Station downStation) {
        return sections.stream()
                .filter(res -> res.findSpecificSection(StationResponse.of(upStation), StationResponse.of(downStation)))
                .findAny()
                .orElse(null);
    }

    private String getStationId(Station station) {
        return String.valueOf(station.getId());
    }
}