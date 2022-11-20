package nextstep.subway.line;


import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.DefaultAcceptanceTest;
import nextstep.subway.dto.LineCreateRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.StationRequest;
import nextstep.subway.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철 노선의 구간 관련 기능")
public class SectionAcceptanceTest extends DefaultAcceptanceTest {

    private final String LINE_URI = "/lines";
    private final String STATION_URI = "/stations";
    private final String SECTION_URI = "/sections";

    private StationResponse 강남역;
    private StationResponse 판교역;
    private StationResponse 선릉역;
    private StationResponse 청계산역;
    private LineResponse 신분당선;

    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철역_생성_요청(지하철역_생성_요청값("강남역"));
        판교역 = 지하철역_생성_요청(지하철역_생성_요청값("판교역"));
        선릉역 = 지하철역_생성_요청(지하철역_생성_요청값("선릉역"));
        청계산역 = 지하철역_생성_요청(지하철역_생성_요청값("청계산역"));

        신분당선 = 노선_생성_요청(노선_생성_요청_값("신분당선", "RED", 강남역.getId(), 판교역.getId(), 10));
    }

    public StationResponse 지하철역_생성_요청(StationRequest stationRequest) {
        return RestAssured.given().log().all()
            .body(stationRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post(STATION_URI)
            .then().log().all().extract().as(StationResponse.class);
    }

    StationRequest 지하철역_생성_요청값(String 지하철역_이름) {
        return new StationRequest(지하철역_이름);
    }

    public LineResponse 노선_생성_요청(LineCreateRequest lineCreateRequest) {
        return RestAssured.given().log().all()
            .body(lineCreateRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post(LINE_URI)
            .then().log().all()
            .extract().as(LineResponse.class);
    }

    LineCreateRequest 노선_생성_요청_값(String name, String color, Long upStationId, Long downStationId, int distance) {
        return new LineCreateRequest(name, color, upStationId, downStationId, distance);
    }

    ExtractableResponse<Response> 노선에_지하철역_등록_요청(Long id, SectionRequest sectionRequest) {
        return RestAssured.given().log().all()
            .when().body(sectionRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .post(LINE_URI + '/' + id + SECTION_URI)
            .then().log().all().extract();
    }

    SectionRequest 구간_등록_요청(StationResponse downStation, StationResponse upStation, int distance) {
        return new SectionRequest(downStation.getId(), upStation.getId(), distance);
    }

    void 노선에_지하철역_등록됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    void 노선에_지하철역_등록_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }


    @DisplayName("노선에 구간을 등록한다.")
    @Test
    void 구간_등록() {
        // when
        ExtractableResponse<Response> response = 노선에_지하철역_등록_요청(신분당선.getId(), 구간_등록_요청(강남역, 청계산역, 2));

        // then
        노선에_지하철역_등록됨(response);
    }

    @DisplayName("새로운 역을 상행 종점으로 등록한다")
    @Test
    void 구간_등록_새로운_상행_종점() {
        // when
        ExtractableResponse<Response> response = 노선에_지하철역_등록_요청(신분당선.getId(), 구간_등록_요청(청계산역, 강남역, 2));

        // then
        노선에_지하철역_등록됨(response);
    }

    @DisplayName("새로운 역을 하행 종점으로 등록한다")
    @Test
    void 구간_등록_새로운_하행_종점() {
        // when
        ExtractableResponse<Response> response = 노선에_지하철역_등록_요청(신분당선.getId(), 구간_등록_요청(판교역, 청계산역, 2));

        // then
        노선에_지하철역_등록됨(response);
    }

    @DisplayName("기존 역 사이 길이보다 크거나 같은 길이의 구간을 추가한다.")
    @Test
    void addSectionOverDistance() {
        // when
        ExtractableResponse<Response> response = 노선에_지하철역_등록_요청(신분당선.getId(), 구간_등록_요청(강남역, 청계산역, 15));

        // then
        노선에_지하철역_등록_실패됨(response);
    }

    @DisplayName("이미 등록 되어있는 구간을 추가한다.")
    @Test
    void addSectionDuplicate() {
        // given
        LineResponse 이미_등록된_구간 = 노선에_지하철역_등록_요청(신분당선.getId(), 구간_등록_요청(강남역, 청계산역, 5)).as(LineResponse.class);

        // when
        ExtractableResponse<Response> response = 노선에_지하철역_등록_요청(이미_등록된_구간.getId(), 구간_등록_요청(청계산역, 판교역, 3));

        // then
        노선에_지하철역_등록_실패됨(response);
    }

    @DisplayName("상, 하행역 둘 중 하나도 포함되지 않은 구간을 추가한다.")
    @Test
    void addSectionNotInStations() {
        // when
        ExtractableResponse<Response> response = 노선에_지하철역_등록_요청(신분당선.getId(), 구간_등록_요청(청계산역, 선릉역, 3));

        // then
        노선에_지하철역_등록_실패됨(response);
    }

}
