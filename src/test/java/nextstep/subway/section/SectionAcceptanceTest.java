package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static nextstep.subway.line.LineAcceptanceTest.노선을_생성한다;
import static nextstep.subway.line.LineAcceptanceTest.특정_노선목록을_조회한다;
import static nextstep.subway.station.StationAcceptanceTest.지하철역을_생성한다;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("노선-라인 관련 인수테스트")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SectionAcceptanceTest {
    @LocalServerPort
    int port;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private SectionRepository sectionRepository;

    private StationResponse 건대역;
    private StationResponse 뚝섬유원지역;
    private StationResponse 청담역;
    private StationResponse 강남구청역;
    private StationResponse 논현역;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }

        // given
        건대역 = 지하철역을_생성한다("건대역").as(StationResponse.class);
        뚝섬유원지역 = 지하철역을_생성한다("뚝섬유원지역").as(StationResponse.class);
        청담역 = 지하철역을_생성한다("청담역").as(StationResponse.class);
        강남구청역 = 지하철역을_생성한다("강남구청역").as(StationResponse.class);
        논현역 = 지하철역을_생성한다("논현역").as(StationResponse.class);
    }

    /**
     * When 노선에 상행과 하행을 등록하면
     * Then 노선에 역이 등록된다.
     */
    @Test
    public void 노선_7호선_상행_건대_하행_고속터미널_등록() {
        LineResponse 칠호선 = 노선을_생성한다("7호선", "#EEEEEE", 건대역.getId(), 뚝섬유원지역.getId(), 10).as(LineResponse.class);

        ExtractableResponse<Response> response = 구간을_생성한다(칠호선.getId(), 뚝섬유원지역.getId(), 청담역.getId(), 10);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(노선에_포함된_역_아이디(칠호선)).containsExactly(건대역.getId(), 뚝섬유원지역.getId(), 청담역.getId());
    }

    /**
     * When 상행, 하행이 등록된 노선 중간에 역을 등록하면
     * Then 노선 중간에 역이 등록된다. (Happy Path)
     */
    @Test
    public void 역사이에_새로운_구간_등록() {
        LineResponse 칠호선 = 노선을_생성한다("7호선", "#EEEEEE", 청담역.getId(), 건대역.getId(), 20).as(LineResponse.class);

        ExtractableResponse<Response> response = 구간을_생성한다(칠호선.getId(), 강남구청역.getId(), 청담역.getId(), 10);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(노선에_포함된_역_아이디(칠호선)).containsExactly(강남구청역.getId(), 청담역.getId(), 건대역.getId());
    }

    /**
     * When 상행, 하행이 등록된 노선의 상행 종점에 새로운 역을 등록하면
     * Then 새로운 역이 상행 종점으로 등록된다. (Happy Path)
     */
    @Test
    public void 새로운_역이_상행_종점으로_등록() {
        LineResponse 칠호선 = 노선을_생성한다("7호선", "#EEEEEE", 청담역.getId(), 건대역.getId(), 20).as(LineResponse.class);

        ExtractableResponse<Response> response = 구간을_생성한다(칠호선.getId(), 뚝섬유원지역.getId(), 건대역.getId(), 10);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(노선에_포함된_역_아이디(칠호선)).containsExactly(청담역.getId(), 뚝섬유원지역.getId(), 건대역.getId());
    }

    /**
     * When 상행, 하행이 등록된 노선의 하행 종점에 새로운 역을 등록하면
     * Then 새로운 역이 하행 종점으로 등록된다. (Happy Path)
     */
    @Test
    public void 새로운_역이_하행_종점으로_등록() {
        LineResponse 칠호선 = 노선을_생성한다("7호선", "#EEEEEE", 청담역.getId(), 뚝섬유원지역.getId(), 10).as(LineResponse.class);

        ExtractableResponse<Response> response = 구간을_생성한다(칠호선.getId(), 뚝섬유원지역.getId(), 건대역.getId(), 10);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(노선에_포함된_역_아이디(칠호선)).containsExactly(청담역.getId(), 뚝섬유원지역.getId(), 건대역.getId());
    }

    /**
     * When 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면
     * Then 구간이 등록되지 않는다.
     */
    @Test
    public void 기존_구간_사이에_길이가_더_긴_구간이_등록() {
        LineResponse 칠호선 = 노선을_생성한다("7호선", "#EEEEEE", 청담역.getId(), 건대역.getId(), 10).as(LineResponse.class);

        ExtractableResponse<Response> response = 구간을_생성한다(칠호선.getId(), 뚝섬유원지역.getId(), 건대역.getId(), 11);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.asString()).contains(Section.SECTION_DISTANCE_MINUS_ERROR_MSG);
    }

    /**
     * When 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음
     * Then 구간이 등록되지 않는다.
     */
    @Test
    public void 상행역과_하행역이_이미_등록되어있는_구간인_경우() {
        LineResponse 칠호선 = 노선을_생성한다("7호선", "#EEEEEE", 청담역.getId(), 건대역.getId(), 10).as(LineResponse.class);

        ExtractableResponse<Response> response = 구간을_생성한다(칠호선.getId(), 청담역.getId(), 건대역.getId(), 5);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.asString()).contains(Sections.HAS_UP_AND_DOWN_STATION_MSG);
    }

    /**
     * When 상행역과 하행역 둘 중 하나도 포함되어있지 않으면
     * Then 구간이 등록되지 않는다.
     */
    @Test
    public void 상행역과_하행역이_둘다_포함되어_있지_않는_경우() {
        LineResponse 칠호선 = 노선을_생성한다("7호선", "#EEEEEE", 청담역.getId(), 건대역.getId(), 10).as(LineResponse.class);

        ExtractableResponse<Response> response = 구간을_생성한다(칠호선.getId(), 뚝섬유원지역.getId(), 강남구청역.getId(), 5);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.asString()).contains(Sections.HAS_NOT_UP_AND_DOWN_STATION_MSG);
    }

    /**
     * When 노선에 포함된 특정 역을 제거하면 (해당역이 노선의 중간일 때)
     * Then 해당 역을 상행으로 포함한 구역이 제거되고 기존 구간이 변경된다. (HappyPath)
     */
    @Test
    public void 특정역이_포함된_구간을_제거한다() {
        //when
        LineResponse 칠호선 = 노선을_생성한다("7호선", "#EEEEEE", 청담역.getId(), 건대역.getId(), 10).as(LineResponse.class);
        구간을_생성한다(칠호선.getId(), 뚝섬유원지역.getId(), 건대역.getId(), 5);

        //given
        ExtractableResponse<Response> response = 구간을_제거한다(칠호선.getId(), 뚝섬유원지역.getId());

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        //when
        ExtractableResponse<Response> responseLine = 특정_노선목록을_조회한다(칠호선.getId());
        LineResponse lineResponse = responseLine.as(LineResponse.class);

        //then
        assertThat(lineResponse.getStations()).doesNotContain(뚝섬유원지역);
        assertThat(lineResponse.getStations()
                                .stream()
                                .map(StationResponse::getId)
                                .collect(Collectors.toList())).containsExactlyInAnyOrder(청담역.getId(), 건대역.getId());
    }

    /**
     * When 노선에 포함된 특정 역을 제거하면 (해당역이 노선의 가장 상행역일 때)
     * Then 해당 역을 상행으로 포함한 구역이 제거되고 기존 구간이 변경된다. (HappyPath)
     */
    @Test
    public void 해당역이_노선의_가장_상행일_때_구간제거() {
        //when
        LineResponse 칠호선 = 노선을_생성한다("7호선", "#EEEEEE", 청담역.getId(), 건대역.getId(), 10).as(LineResponse.class);
        구간을_생성한다(칠호선.getId(), 뚝섬유원지역.getId(), 건대역.getId(), 5);

        //given
        ExtractableResponse<Response> response = 구간을_제거한다(칠호선.getId(), 청담역.getId());

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        //when
        ExtractableResponse<Response> responseLine = 특정_노선목록을_조회한다(칠호선.getId());
        LineResponse lineResponse = responseLine.as(LineResponse.class);

        //then
        assertThat(lineResponse.getStations()).doesNotContain(뚝섬유원지역);
        assertThat(lineResponse.getStations()
                .stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList())).containsExactlyInAnyOrder(뚝섬유원지역.getId(), 건대역.getId());
    }

    /**
     * When 노선에 포함된 특정 역을 제거하면 (해당역이 노선의 가장 하행역일 때)
     * Then 해당 역을 상행으로 포함한 구역이 제거되고 기존 구간이 변경된다. (HappyPath)
     */
    @Test
    public void 해당역이_노선의_가장_하행일_때_구간제거() {
        //when
        LineResponse 칠호선 = 노선을_생성한다("7호선", "#EEEEEE", 청담역.getId(), 건대역.getId(), 10).as(LineResponse.class);
        구간을_생성한다(칠호선.getId(), 뚝섬유원지역.getId(), 건대역.getId(), 5);

        //given
        ExtractableResponse<Response> response = 구간을_제거한다(칠호선.getId(), 건대역.getId());

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        //when
        ExtractableResponse<Response> responseLine = 특정_노선목록을_조회한다(칠호선.getId());
        LineResponse lineResponse = responseLine.as(LineResponse.class);

        //then
        assertThat(lineResponse.getStations()).doesNotContain(건대역);
        assertThat(lineResponse.getStations()
                .stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList())).containsExactlyInAnyOrder(청담역.getId(), 뚝섬유원지역.getId());
    }

    /**
     * When 노선에 하나의 구간만 존재한다면
     * Then 해당 노선에서 역을 제거할 수 없다.
     */
    @Test
    public void 해당역이_노선에_구간이_하나_뿐일_때_구간제거_불가() {
        //when
        LineResponse 칠호선 = 노선을_생성한다("7호선", "#EEEEEE", 청담역.getId(), 건대역.getId(), 10).as(LineResponse.class);

        //given
        ExtractableResponse<Response> response = 구간을_제거한다(칠호선.getId(), 건대역.getId());

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.asString()).contains(Sections.SECTION_IN_LINE_MINIMUN_SIZE_MSG);
    }

    /**
     * When 노선에 제거하려는 대상의 역이 존재하지 않다면
     * Then 해당 노선에서 역을 제거할 수 없다.
     */
    @Test
    public void 해당역이_노선에_없을_때_구간제거() {
        //when
        LineResponse 칠호선 = 노선을_생성한다("7호선", "#EEEEEE", 청담역.getId(), 건대역.getId(), 10).as(LineResponse.class);

        //given
        ExtractableResponse<Response> response = 구간을_제거한다(칠호선.getId(), 뚝섬유원지역.getId());

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.asString()).contains(Sections.NO_SEARCH_STATION_IN_LINE_MSG);
    }

    public static ExtractableResponse<Response> 구간을_생성한다(Long id, Long upStationId, Long downStationId, Integer distance) {
        SectionRequest sectionRequest = new SectionRequest(upStationId, downStationId, distance);
        return RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/" + id + "/sections")
                .then().log().all()
                .extract();
    }
    
    private List<Long> 노선에_포함된_역_아이디 (LineResponse line) {
        ExtractableResponse<Response> responseLine = 특정_노선목록을_조회한다(line.getId());
        LineResponse lineResponse = responseLine.as(LineResponse.class);

        return lineResponse.getStations()
                                    .stream()
                                    .flatMap(station -> Stream.of(station.getId()))
                                    .collect(Collectors.toList());
    }

    public static ExtractableResponse<Response> 구간을_제거한다(Long lineId, Long stationId) {
        return RestAssured.given().log().all()
                .body(stationId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/lines/{lineId}/sections", lineId)
                .then().log().all()
                .extract();
    }
}
