package nextstep.subway.line;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
    void addBetweenUpStation() {
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
    void addBetweenDownStation() {
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
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }
}
