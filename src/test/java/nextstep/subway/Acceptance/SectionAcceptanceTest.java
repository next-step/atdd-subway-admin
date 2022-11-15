package nextstep.subway.Acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철구간 관련 기능")
public class SectionAcceptanceTest extends AbstractAcceptanceTest {

    StationResponse 강남역;
    StationResponse 정자역;
    LineResponse 신분당선;


    /**
     * Given 역 두개를 등록한다.
     * Given 강남역을 상행, 광교역을 하행으로 하는 신분당선을 등록한다.
     */
    @BeforeEach
    public void setUpData() {
        강남역 = 지하철역_신규_생성_요청("강남역").as(StationResponse.class);
        정자역 = 지하철역_신규_생성_요청("광교역").as(StationResponse.class);
        신분당선 = 지하철_노선_신규_생성_요청("신분당선", "bg-red-600", 강남역.getId(), 정자역.getId()).as(LineResponse.class);
    }

    /**
     * Given 판교역을 새로 생성하고
     * When 강남역의 하행으로 판교역을 4거리로 추가하면,
     * Then 역 사이의 새로운 역이 등록된다.
     */
    @DisplayName("역 사이에 새로운 역을 등록할 수 있다.")
    @Test
    void section_add() {
        //given
        StationResponse 판교역 = 지하철역_신규_생성_요청("판교역").as(StationResponse.class);

        //when
        ExtractableResponse<Response> response = 지하철_구간_신규_등록_요청(신분당선.getId(), 강남역.getId(), 판교역.getId(), 4);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(지하철_노선_단일_조회_및_소속_역_아이디_조회(신분당선.getId())).contains((int)(long)판교역.getId());
    }

    /**
     * Given 판교역을 새로 생성하고
     * When 강남역의 하행으로 판교역을 10거리로 추가하면,
     * Then 길이 제한으로 등록할 수 없다.
     */
    @DisplayName("역 사이에 새로운 역을 등록할 때 기존 역 사이의 길이보다 크거나 같으면 등록할 수 없다.")
    @Test
    void section_distance() {
        //given
        StationResponse 판교역 = 지하철역_신규_생성_요청("판교역").as(StationResponse.class);

        //when
        ExtractableResponse<Response> response = 지하철_구간_신규_등록_요청(신분당선.getId(), 강남역.getId(), 판교역.getId(), 10);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 신논현역을 새로 생성하고
     * When 강남역의 상행역으로 신논현역을 1거리고 추가하면
     * Then 신분당선에 신논현역이 신규로 등록된다.
     */
    @DisplayName("새로운 역을 상행 종점으로 등록할 수 있다.")
    @Test
    void section_add_as_up_station() {
        //given
        StationResponse 신논현역 = 지하철역_신규_생성_요청("신논현역").as(StationResponse.class);

        //when
        ExtractableResponse<Response> response = 지하철_구간_신규_등록_요청(신분당선.getId(), 신논현역.getId(), 강남역.getId(), 1);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(지하철_노선_단일_조회_및_소속_역_아이디_조회(신분당선.getId())).contains((int)(long)신논현역.getId());
    }

    /**
     * Given 광교역을 새로 생성하고
     * When 정자역의 하행역으로 광교역을 5 거리로 추가하면
     * Then 신분당선에 관교역이 등록된다.
     */
    @DisplayName("새로운 역을 하행 종점으로 등록할 수 있다.")
    @Test
    void section_add_as_down_station() {
        //given
        StationResponse 광교중앙역 = 지하철역_신규_생성_요청("광교중앙역").as(StationResponse.class);

        //when
        ExtractableResponse<Response> response = 지하철_구간_신규_등록_요청(신분당선.getId(), 정자역.getId(), 광교중앙역.getId(), 5);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(지하철_노선_단일_조회_및_소속_역_아이디_조회(신분당선.getId())).contains((int)(long)광교중앙역.getId());
    }

    /**
     * When 강남역과 정자역 구간을 등록하면
     * Then 이미 모든 역이 등록되어 있어 에러가 발생한다.
     */
    @DisplayName("추가하는 역이 모두 구간에 포함되어 있으면 등록 할 수 없다.")
    @Test
    void section_add_but_already_registered() {
        //when
        ExtractableResponse<Response> response = 지하철_구간_신규_등록_요청(신분당선.getId(), 강남역.getId(), 정자역.getId(), 10);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 서초역과 교대역을 생성고
     * When 신분당선에 서초역과 교대역을 등록하면
     * Then 두 역이 신분당선에 포함되어 있지 않기 때문에 등록할 수 없다.
     */
    @DisplayName("추가하는 역이 모두 구간에 포함되어 있지 않으면 등록 할 수 없다.")
    @Test
    void section_add_but_none_intersection() {
        //given
        StationResponse 서초역 = 지하철역_신규_생성_요청("서초역").as(StationResponse.class);
        StationResponse 교대역 = 지하철역_신규_생성_요청("교대역").as(StationResponse.class);

        //when
        ExtractableResponse<Response> response = 지하철_구간_신규_등록_요청(신분당선.getId(), 서초역.getId(), 교대역.getId(), 10);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

    }

    private ExtractableResponse<Response> 지하철_구간_신규_등록_요청(Long lineId, Long upStationId, Long downStationId, int distance) {
        HashMap<String, Object> param = new HashMap<>();
        param.put("upStationId", upStationId + "");
        param.put("downStationId", downStationId + "");
        param.put("distance", distance);

        return RestAssured.given().log().all()
                .body(param)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();
    }

    private List<Integer> 지하철_노선_단일_조회_및_소속_역_아이디_조회(Long id) {
        return RestAssured.given().log().all()
                .when().get("/lines/" + id)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().jsonPath()
                .getList("stations.id");
    }
}
