package nextstep.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.line.LineSteps.지하철_노선_등록되어_있음;
import static nextstep.subway.section.SectionSteps.지하철_노선에_지하철역_등록_요청;
import static nextstep.subway.station.StationSteps.지하철_역_등록되어_있음;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {


    private StationResponse 강남역;
    private StationResponse 광교역;
    private LineResponse 신분당선;

    @BeforeEach
    void setUpLine() {

        강남역 = 지하철_역_등록되어_있음(new StationRequest("강남역")).as(StationResponse.class);
        광교역 = 지하철_역_등록되어_있음(new StationRequest("광교역")).as(StationResponse.class);

        LineRequest params = new LineRequest("신분당선", "bg-red-660", 강남역.getId(), 광교역.getId(), 10);
        신분당선 = 지하철_노선_등록되어_있음(params).as(LineResponse.class);
    }

    @DisplayName("새로운 역을 하행 종점으로 등록한다")
    @Test
    void addSectionLast() {

        // given
        StationResponse 판교역 = 지하철_역_등록되어_있음(new StationRequest("판교역")).as(StationResponse.class);
        SectionRequest params = new SectionRequest(광교역.getId(), 판교역.getId(), 3);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(params, 신분당선.getId());

        // then
        지하철_노선에_구간_등록됨(response);
    }

    @DisplayName("새로운 역을 상행 종점으로 등록한다")
    @Test
    void addSectionFirst() {

        // given
        StationResponse 정자역 = 지하철_역_등록되어_있음(new StationRequest("정자역")).as(StationResponse.class);
        SectionRequest params = new SectionRequest(정자역.getId(), 강남역.getId(), 3);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(params, 신분당선.getId());

        // then
        지하철_노선에_구간_등록됨(response);
    }

    @DisplayName("역 사이에 기존 역 사이 길이보다 크거나 같은 새로운 역을 등록한다")
    @Test
    void addSectionException1() {

        // given
        StationResponse 정자역 = 지하철_역_등록되어_있음(new StationRequest("정자역")).as(StationResponse.class);
        SectionRequest params1 = new SectionRequest(강남역.getId(), 정자역.getId(), 10);
        SectionRequest params2 = new SectionRequest(정자역.getId(), 광교역.getId(),10);

        // when
        ExtractableResponse<Response> response1 = 지하철_노선에_지하철역_등록_요청(params1, 신분당선.getId());
        ExtractableResponse<Response> response2 = 지하철_노선에_지하철역_등록_요청(params2, 신분당선.getId());

        // then
        지하철_노선에_구간_등록_실패됨(response1);
        지하철_노선에_구간_등록_실패됨(response2);
    }

    @DisplayName("노선에 이미 등록된 상행역과 하행역을 포함하는 구간을 등록한다")
    @Test
    void addSectionException2() {
        // given
        SectionRequest params = new SectionRequest(강남역.getId(), 광교역.getId(), 9);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(params, 신분당선.getId());

        // then
        지하철_노선에_구간_등록_실패됨(response);
    }

    @DisplayName("노선에 상행역과 하행역 둘 중 하나라도 포함하지 않는 구간을 등록한다")
    @Test
    void addSectionException3() {
        // given
        StationResponse 판교역 = 지하철_역_등록되어_있음(new StationRequest("판교역")).as(StationResponse.class);
        StationResponse 정자역 = 지하철_역_등록되어_있음(new StationRequest("정자역")).as(StationResponse.class);
        SectionRequest params = new SectionRequest(판교역.getId(), 정자역.getId(), 5);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(params, 신분당선.getId());

        // then
        지하철_노선에_구간_등록_실패됨(response);
    }

    private void 지하철_노선에_구간_등록_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void 지하철_노선에_구간_등록됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }
}
