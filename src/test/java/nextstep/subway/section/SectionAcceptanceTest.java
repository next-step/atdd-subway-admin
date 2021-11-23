package nextstep.subway.section;

import static nextstep.subway.line.LineAcceptanceTest.지하철_노선_생성;
import static nextstep.subway.station.StationAcceptanceTest.지하철_역_생성;
import static nextstep.subway.utils.AcceptanceTestUtil.post;
import static org.assertj.core.api.Assertions.assertThat;

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

@DisplayName("구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    private StationResponse 강남역;
    private StationResponse 광교역;
    private LineResponse 신분당선;

    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철_역_생성(new StationRequest("강남역")).as(StationResponse.class);
        광교역 = 지하철_역_생성(new StationRequest("광교역")).as(StationResponse.class);

        신분당선 = 지하철_노선_생성(
            new LineRequest("신분당선", "bg-red-600", 강남역.getId(), 광교역.getId(), 10)).as(
            LineResponse.class);
    }

    @DisplayName("역 사이에 새 구간을 등록한다.")
    @Test
    void addSection() {
        // given
        StationResponse 청계산입구역 = 지하철_역_생성(new StationRequest("청계산입구역")).as(StationResponse.class);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_구간_추가(신분당선.getId(), 청계산입구역);

        // then
        구간_등록됨(response);
    }

    @DisplayName("상행 종점 구간으로 등록한다.")
    @Test
    void addSection_상행_종점_구간() {
        // given
        StationResponse 청계산입구역 = 지하철_역_생성(new StationRequest("청계산입구역")).as(StationResponse.class);

        // when
        ExtractableResponse<Response> response = 상행_종점에_구간_추가(신분당선.getId(), 청계산입구역);

        // then
        구간_등록됨(response);
    }

    @DisplayName("하행 종점 구간으로 등록한다.")
    @Test
    void addSection_하행_종점_구간() {
        // given
        StationResponse 청계산입구역 = 지하철_역_생성(new StationRequest("청계산입구역")).as(StationResponse.class);

        // when
        ExtractableResponse<Response> response = 하행_종점에_구간_추가(신분당선.getId(), 청계산입구역);

        // then
        구간_등록됨(response);
    }

    private ExtractableResponse<Response> 지하철_노선에_구간_추가(Long lineId, StationResponse 청계산입구역) {
        return post("/lines/" + lineId + "/sections",
            new SectionRequest(강남역.getId(), 청계산입구역.getId(), 8));
    }

    private ExtractableResponse<Response> 상행_종점에_구간_추가(Long lineId, StationResponse 청계산입구역) {
        return post("/lines/" + lineId + "/sections",
            new SectionRequest(청계산입구역.getId(), 강남역.getId(), 8));
    }

    private ExtractableResponse<Response> 하행_종점에_구간_추가(Long lineId, StationResponse 청계산입구역) {
        return post("/lines/" + lineId + "/sections",
            new SectionRequest(광교역.getId(), 청계산입구역.getId(), 8));
    }

    private void 구간_등록됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
