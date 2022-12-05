package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.utils.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SectionAcceptanceTest {
    public static final String LINE_URL = "/lines";
    public static final String STATION_URL = "/stations";
    public static final String STATION_KEY_NAME = "name";

    private StationResponse 상행종점역, 하행종점역;
    private LineResponse 신분당선;
    @LocalServerPort
    int port;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        databaseCleanup.execute();

        // create Station
        상행종점역 = createStationRest("상행종점역").as(StationResponse.class);
        하행종점역 = createStationRest("하행종점역").as(StationResponse.class);


        //create Line
        신분당선 = createLineRest(LineRequest.of("신분당선","bg-red-600", 상행종점역.getId(), 하행종점역.getId(),10)).as(LineResponse.class);

    }

    /**
     * Given 상행종점역, 하행종점역을 가진 노선을 생성하고 상행종점역, 새로운역 구간을 추가하고
     * When 노선을 조회하면
     * Then 상행종점역, 새로운역, 하행종점역이 조회된다.
     */
    @DisplayName("역 사이에 새로운 역을 등록한다.")
    @Test
    void addSectionMiddleStations() {
        // given
        StationResponse 새로운역 = createStationRest("새로운역").as(StationResponse.class);
        SectionRequest newSection = new SectionRequest(상행종점역.getId(),새로운역.getId(),5);
        addSectionRest(신분당선.getId(), newSection);

        // when
        LineResponse findLine = lineGet(신분당선.getId()).as(LineResponse.class);

        // then
        노선_역_순서확인(findLine, "상행종점역","새로운역","하행종점역");

    }



    /**
     * Given 상행종점역, 하행종점역을 가진 노선을 생성하고 새로운역, 상행종점역 구간을 추가하고
     * When 노선을 조회하면
     * Then 새로운역, 상행종점역, 하행종점역이 조회된다.
     */
    @DisplayName("새로운 역을 상행 종점으로 등록한다.")
    @Test
    void addSectionNewUpStation() {
        // given
        StationResponse 새로운역 = createStationRest("새로운역").as(StationResponse.class);
        SectionRequest newSection = new SectionRequest(새로운역.getId(),상행종점역.getId(),5);
        addSectionRest(신분당선.getId(), newSection);

        // when
        LineResponse findLine = lineGet(신분당선.getId()).as(LineResponse.class);

        // then
        노선_역_순서확인(findLine, "새로운역","상행종점역","하행종점역");
    }

    /**
     * Given 상행종점역, 하행종점역을 가진 노선을 생성하고 하행종점역, 새로운역 구간을 추가하고
     * When 노선을 조회하면
     * Then 상행종점역, 하행종점역, 새로운역이 조회된다.
     */
    @DisplayName("새로운 역을 하행 종점으로 등록한다.")
    @Test
    void addSectionNewDownStation() {
        // given
        StationResponse 새로운역 = createStationRest("새로운역").as(StationResponse.class);
        SectionRequest newSection = new SectionRequest(하행종점역.getId(),새로운역.getId(),5);
        addSectionRest(신분당선.getId(), newSection);

        // when
        LineResponse findLine = lineGet(신분당선.getId()).as(LineResponse.class);

        // then
        노선_역_순서확인(findLine, "상행종점역","하행종점역","새로운역");
    }

    // Happy-case

    /**
     * Given 상행종점역, 하행종점역을 가진 노선을 생성하고
     * When 새로운역, 하행종점역을 기존 역 사이보다 크거나 같게 등록하면
     * Then 예외가 발생한다.
     */
    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록 할 수 없다.")
    @Test
    void error_addSectionLongThanOriginLine() {
        // given
        StationResponse 새로운역 = createStationRest("새로운역").as(StationResponse.class);
        SectionRequest newSection = new SectionRequest(새로운역.getId(),하행종점역.getId(),10);

        // when
        ExtractableResponse<Response> response = addSectionRest(신분당선.getId(), newSection);

        // then
        노선등록_에러(response);
    }

    /**
     * Given 상행종점역, 하행종점역을 가진 노선을 생성하고
     * When 상행종점역, 하행종점역을 가진 구간을 등록하면
     * Then 예외가 발생한다.
     */
    @DisplayName("상행역과 하행역이 이미 노선에 등록되어 있으면 추가할 수 없다.")
    @Test
    void error_addSectionSameStationsLine() {
        // given
        SectionRequest newSection = new SectionRequest(상행종점역.getId(),하행종점역.getId(),5);

        // when
        ExtractableResponse<Response> response = addSectionRest(신분당선.getId(), newSection);

        // then
        노선등록_에러(response);
    }

    /**
     * Given 상행종점역, 하행종점역을 가진 노선을 생성하고
     * When 새상행종점역, 새하행종점역을 가진 구간을 등록하면
     * Then 예외가 발생한다.
     */
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어 있지 않다면 추가할 수 없다.")
    @Test
    void error_addSectionAnotherStationsLine() {
        // given
        StationResponse 새상행종점역 = createStationRest("새상행종점역").as(StationResponse.class);
        StationResponse 새하행종점역 = createStationRest("새하행종점역").as(StationResponse.class);
        SectionRequest newSection = new SectionRequest(새상행종점역.getId(),새하행종점역.getId(),5);

        // when
        ExtractableResponse<Response> response = addSectionRest(신분당선.getId(), newSection);

        // then
        노선등록_에러(response);
    }

    private ExtractableResponse<Response> createLineRest(LineRequest lineRequest) {
        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(LINE_URL)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> addSectionRest(Long lineId, SectionRequest sectionRequest) {
        return RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(LINE_URL+"/{lineId}/sections",lineId)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> lineGet(Long id) {
        return RestAssured.given().log().all()
                .when().get(LINE_URL+"/{id}",id)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> createStationRest(String stationName) {
        Map<String, String> params = new HashMap<>();
        params.put(STATION_KEY_NAME, stationName);
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(STATION_URL)
                .then().log().all()
                .extract();
    }

    private void 노선_역_순서확인(LineResponse lineResponse, String 역1, String 역2, String 역3) {
        assertThat(lineResponse.getStations().stream().map(StationResponse::getName).collect(Collectors.toList())).containsExactly(역1, 역2, 역3);
    }

    private void 노선등록_에러(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
