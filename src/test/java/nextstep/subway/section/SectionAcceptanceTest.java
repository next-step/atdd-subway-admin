package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    private StationResponse 상행역;
    private StationResponse 강남역;
    private StationResponse 정자역;
    private StationResponse 미금역;
    private StationResponse 광교역;
    private StationResponse 하행역;
    private LineResponse 신분당선;

    @BeforeEach
    public void setUp() {

        super.setUp();

        상행역 = StationAcceptanceTest.지하철역_등록되어_있음("상행역").as(StationResponse.class);
        강남역 = StationAcceptanceTest.지하철역_등록되어_있음("강남역").as(StationResponse.class);
        정자역 = StationAcceptanceTest.지하철역_등록되어_있음("정자역").as(StationResponse.class);
        미금역 = StationAcceptanceTest.지하철역_등록되어_있음("미금역").as(StationResponse.class);
        광교역 = StationAcceptanceTest.지하철역_등록되어_있음("광교역").as(StationResponse.class);
        하행역 = StationAcceptanceTest.지하철역_등록되어_있음("하행역").as(StationResponse.class);

        Map<String, String> createParams = LineAcceptanceTest.지하철_노선_더미_데이터_신분상선(강남역.getId(), 광교역.getId(), 10);
        신분당선 = LineAcceptanceTest.지하철_노선_등록되어_있음(createParams).as(LineResponse.class);
    }

    @DisplayName("노선에 구간을 등록한다.")
    @Test
    void addSection() {

        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> registerSectionResponse = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), 상행역.getId(), 강남역.getId(), 10);
        ExtractableResponse<Response> registerSectionResponse = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), 강남역.getId(), 정자역.getId(), 9);

        // 지하철_노선에_조회
        ExtractableResponse<Response> lineResponse =  LineAcceptanceTest.지하철_노선_조회(registerSectionResponse.header("Location"));

        // then
        // 지하철_노선에_지하철역_등록됨
        assertThat(registerSectionResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(lineResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(lineResponse.jsonPath().getList("stations", StationResponse.class).get(0).getName()).isEqualTo(상행역.getName());
        assertThat(lineResponse.jsonPath().getList("stations", StationResponse.class).get(1).getName()).isEqualTo(강남역.getName());
        assertThat(lineResponse.jsonPath().getList("stations", StationResponse.class).get(2).getName()).isEqualTo(광교역.getName());

    }

    @DisplayName("노선에 구간을 등록한다. - 새로운 역을 상행 종점으로 등록")
    @Test
    void addSection_새로운_역을_상행_종점으로_등록() {

        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> registerSectionResponse = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), 상행역.getId(), 강남역.getId(), 10);

        // 지하철_노선에_조회
        ExtractableResponse<Response> lineResponse =  LineAcceptanceTest.지하철_노선_조회(registerSectionResponse.header("Location"));

        // then
        // 지하철_노선에_지하철역_등록됨
        assertThat(registerSectionResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(lineResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(lineResponse.jsonPath().getList("stations", StationResponse.class).get(0).getName()).isEqualTo(상행역.getName());
        assertThat(lineResponse.jsonPath().getList("stations", StationResponse.class).get(1).getName()).isEqualTo(강남역.getName());
        assertThat(lineResponse.jsonPath().getList("stations", StationResponse.class).get(2).getName()).isEqualTo(광교역.getName());
    }

    @DisplayName("노선에 구간을 등록한다. - 새로운 역을 하행 종점으로 등록할 경우")
    @Test
    void addSection_새로운_역을_하행_종점으로_등록() {

        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> registerSectionResponse = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), 광교역.getId(), 하행역.getId(), 10);

        // 지하철_노선에_조회
        ExtractableResponse<Response> lineResponse =  LineAcceptanceTest.지하철_노선_조회(registerSectionResponse.header("Location"));

        // then
        // 지하철_노선에_지하철역_등록됨
        assertThat(registerSectionResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(lineResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(lineResponse.jsonPath().getList("stations", StationResponse.class).get(0).getName()).isEqualTo(강남역.getName());
        assertThat(lineResponse.jsonPath().getList("stations", StationResponse.class).get(1).getName()).isEqualTo(광교역.getName());
        assertThat(lineResponse.jsonPath().getList("stations", StationResponse.class).get(2).getName()).isEqualTo(하행역.getName());
    }

    @DisplayName("노선에 구간을 등록한다. - 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void addSectionException_역_사이_길이보다_크거나_같은_경우() {

        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), 강남역.getId(), 정자역.getId(), 10);

        // then
        // 지하철_노선에_지하철역_등록됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선에 구간을 등록한다. - 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void addSectionException_이미_노선에_모두_등록되어_있는_경우() {

        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), 강남역.getId(), 광교역.getId(), 10);
        // then
        // 지하철_노선에_지하철역_등록됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선에 구간을 등록한다. - 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void addSectionException_상행역과_하행역_둘_중_하나도_포함되어있지_않은_경우() {

        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), 정자역.getId(), 미금역.getId(), 10);

        // then
        // 지하철_노선에_지하철역_등록됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static ExtractableResponse<Response> 지하철_노선에_지하철역_등록_요청(Long lineId, Long upStationId, Long downStationId, Integer distance) {

        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();
    }
}
