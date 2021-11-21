package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("지하철 노선 구간 관련 기능")
class SectionAcceptanceTest extends AcceptanceTest {
    private StationResponse 잠실역;
    private StationResponse 강남역;
    private LineResponse 이호선;

    @BeforeEach
    public void setUp() {
        super.setUp();

        //given
        잠실역 = StationAcceptanceTest.지하철_역_생성_요청(new StationRequest("잠실역"))
            .as(StationResponse.class);
        강남역 = StationAcceptanceTest.지하철_역_생성_요청(new StationRequest("강남역"))
            .as(StationResponse.class);

        이호선 = LineAcceptanceTest.지하철_노선_생성_요청(
                new LineRequest("2호선", "bg-green", 잠실역.getId(), 강남역.getId(), 10))
            .as(LineResponse.class);
    }

    @DisplayName("지하철 상행 종점 구간을 추가한다.")
    @Test
    void addUpSection() {
        // given
        StationResponse 신당역 = StationAcceptanceTest.지하철_역_생성_요청(new StationRequest("신당역"))
            .as(StationResponse.class);

        // when
        // 지하철 구간 추가 요청을 한다.
        SectionRequest 상행_구간 = new SectionRequest(신당역.getId(), 잠실역.getId(), 10);
        ExtractableResponse<Response> response = 지하철_구간_추가_요청(상행_구간, 이호선.getId());

        // then
        // 지하철 구간 추가 됨.
        지하철_구간_추가됨(response);
    }

    @DisplayName("지하철 하행 종점 구간을 추가한다.")
    @Test
    void addDownSection() {
        // given
        StationResponse 교대역 = StationAcceptanceTest.지하철_역_생성_요청(new StationRequest("교대역"))
            .as(StationResponse.class);

        // when
        // 지하철 구간 추가 요청을 한다.
        SectionRequest 하행_구간 = new SectionRequest(강남역.getId(), 교대역.getId(), 10);
        ExtractableResponse<Response> response = 지하철_구간_추가_요청(하행_구간, 이호선.getId());

        // then
        // 지하철 구간 추가 됨.
        지하철_구간_추가됨(response);
    }

    @DisplayName("지하철 구간 사이에 새로운 상행역을 등록한다.")
    @Test
    void addBetweenUpSection() {
        // given
        StationResponse 선릉역 = StationAcceptanceTest.지하철_역_생성_요청(new StationRequest("선릉역"))
            .as(StationResponse.class);

        // when
        // 지하철 구간 추가 요청을 한다.
        SectionRequest 중간_구간 = new SectionRequest(선릉역.getId(), 강남역.getId(), 5);
        ExtractableResponse<Response> response = 지하철_구간_추가_요청(중간_구간, 이호선.getId());

        // then
        // 지하철 구간 추가 됨.
        지하철_구간_추가됨(response);
    }

    @DisplayName("지하철 구간 사이에 새로운 하행역을 등록한다.")
    @Test
    void addBetweenDownSection() {
        // given
        StationResponse 삼성역 = StationAcceptanceTest.지하철_역_생성_요청(new StationRequest("삼성역"))
            .as(StationResponse.class);

        // when
        // 지하철 구간 추가 요청을 한다.
        SectionRequest 중간_구간 = new SectionRequest(잠실역.getId(), 삼성역.getId(), 5);
        ExtractableResponse<Response> response = 지하철_구간_추가_요청(중간_구간, 이호선.getId());

        // then
        // 지하철 구간 추가 됨.
        지하철_구간_추가됨(response);
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있으면 구간 추가에 실패한다.")
    @Test
    void addSectionExistAllStation() {
        // when
        // 지하철 구간 추가 요청을 한다.
        SectionRequest 중복_구간 = new SectionRequest(잠실역.getId(), 강남역.getId(), 10);
        ExtractableResponse<Response> response = 지하철_구간_추가_요청(중복_구간, 이호선.getId());

        // then
        지하철_구간_추가_실패함(response);
    }

    @DisplayName("상행역과 하행역 모두 노선에 포함되어있지 않으면 구간 추가에 실패한다.")
    @Test
    void addSectionNotFoundAllStation() {
        // given
        StationResponse 신촌역 = StationAcceptanceTest.지하철_역_생성_요청(new StationRequest("신촌역"))
            .as(StationResponse.class);
        StationResponse 신당역 = StationAcceptanceTest.지하철_역_생성_요청(new StationRequest("신당역"))
            .as(StationResponse.class);

        // when
        // 지하철 구간 추가 요청을 한다.
        SectionRequest 존재하지_않는_구간 = new SectionRequest(신촌역.getId(), 신당역.getId(), 10);
        ExtractableResponse<Response> response = 지하철_구간_추가_요청(존재하지_않는_구간, 이호선.getId());

        // then
        지하철_구간_추가_실패함(response);
    }

    @DisplayName("지하철 구간사이에 구간을 추가할 때 기존 구간의 거리보다 새로운 구간의 거리가 크거나 같으면 실패한다.")
    @ParameterizedTest
    @ValueSource(ints = {10, 12})
    void addSectionGreaterThanOrEqualDistance(int distance) {
        // given
        StationResponse 삼성역 = StationAcceptanceTest.지하철_역_생성_요청(new StationRequest("삼성역"))
            .as(StationResponse.class);

        // when
        // 지하철 구간 추가 요청을 한다.
        SectionRequest 중간_구간 = new SectionRequest(잠실역.getId(), 삼성역.getId(), distance);
        ExtractableResponse<Response> response = 지하철_구간_추가_요청(중간_구간, 이호선.getId());

        // then
        지하철_구간_추가_실패함(response);
    }

    private ExtractableResponse<Response> 지하철_구간_추가_요청(SectionRequest params, Long lineId) {
        return RestAssured
            .given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines/{lineId}/sections", lineId)
            .then().log().all().extract();
    }

    private void 지하철_구간_추가됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private void 지하철_구간_추가_실패함(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
