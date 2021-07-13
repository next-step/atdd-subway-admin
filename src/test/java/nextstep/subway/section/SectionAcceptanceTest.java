package nextstep.subway.section;

import static nextstep.subway.AcceptanceApi.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    private StationResponse 광교역;
    private StationResponse 강남역;
    private LineResponse 신분당선;

    @BeforeEach
    public void setUp() {
        super.setUp();

        // given
        강남역 = 지하철_역_생성_요청(new StationRequest("강남역")).as(StationResponse.class);
        광교역 = 지하철_역_생성_요청(new StationRequest("광교역")).as(StationResponse.class);

        신분당선 = 지하철_노선_생성_요청(
            new LineRequest("bg-red-600", "신분당선", 강남역.getId(), 광교역.getId(), 10))
            .as(LineResponse.class);
    }

    @DisplayName("노선에 역 사이에 새로운 역을 등록할 경우 구간을 등록한다.")
    @Test
    void 역_사이에_새로운_역을_등록할_경우() {
        //given
        StationResponse 양재역 = 지하철_역_생성_요청(new StationRequest("양재역")).as(StationResponse.class);
        SectionRequest sectionRequest = new SectionRequest(강남역.getId(), 양재역.getId(), 3);

        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = 지하철_구간_생성_요청(신분당선.getId(), sectionRequest);

        // then
        // 지하철_노선에_지하철역_등록됨
        지하철_노선에_지하철역_등록됨(response);
    }

    @DisplayName("노선에 새로운_역을_상행_종점으로_등록할_경우 구간을 등록한다.")
    @Test
    void 새로운_역을_상행_종점으로_등록할_경우() {
        //given
        StationResponse 교대역 = 지하철_역_생성_요청(new StationRequest("교대역")).as(StationResponse.class);
        SectionRequest sectionRequest = new SectionRequest(교대역.getId(), 강남역.getId(), 3);

        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = 지하철_구간_생성_요청(신분당선.getId(), sectionRequest);

        // then
        // 지하철_노선에_지하철역_등록됨
        지하철_노선에_지하철역_등록됨(response);
    }

    @DisplayName("노선에 새로운_역을_하행_종점으로_등록할_경우 구간을 등록한다.")
    @Test
    void 새로운_역을_하행_종점으로_등록할_경우() {
        //given
        StationResponse 기흥역 = 지하철_역_생성_요청(new StationRequest("기흥역")).as(StationResponse.class);
        SectionRequest sectionRequest = new SectionRequest(광교역.getId(), 기흥역.getId(), 3);

        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = 지하철_구간_생성_요청(신분당선.getId(), sectionRequest);

        // then
        // 지하철_노선에_지하철역_등록됨
        지하철_노선에_지하철역_등록됨(response);
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없다")
    @Test
    void 역_사이에_새로운_역을_등록할_경우_실패() {
        //given
        StationResponse 양재역 = 지하철_역_생성_요청(new StationRequest("양재역")).as(StationResponse.class);
        SectionRequest sectionRequest = new SectionRequest(강남역.getId(), 양재역.getId(), 15);

        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = 지하철_구간_생성_요청(신분당선.getId(), sectionRequest);
        // then
        지하철_노선에_지하철역_실패함(response);
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없다")
    @Test
    void 상행역과_하행역이_이미_노선에_모두_등록_실패() {
        //given
        SectionRequest sectionRequest = new SectionRequest(강남역.getId(), 광교역.getId(), 3);
        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = 지하철_구간_생성_요청(신분당선.getId(), sectionRequest);

        // then
        지하철_노선에_지하철역_실패함(response);
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없다")
    @Test
    void 상행역과_하행역_둘_중_하나도_포함되어있지_않음_실패() {
        //given
        StationResponse 양재역 = 지하철_역_생성_요청(new StationRequest("양재역")).as(StationResponse.class);
        StationResponse 판교역 = 지하철_역_생성_요청(new StationRequest("판교역")).as(StationResponse.class);
        SectionRequest sectionRequest = new SectionRequest(양재역.getId(), 판교역.getId(), 15);

        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = 지하철_구간_생성_요청(신분당선.getId(), sectionRequest);

        // then
        지하철_노선에_지하철역_실패함(response);
    }

    public static void 지하철_노선에_지하철역_실패함(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static void 지하철_노선에_지하철역_등록됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
