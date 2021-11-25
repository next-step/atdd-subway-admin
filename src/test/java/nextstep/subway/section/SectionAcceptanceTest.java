package nextstep.subway.section;

import static nextstep.subway.line.LineAcceptanceTest.지하철_노선_목록_조회;
import static nextstep.subway.line.LineAcceptanceTest.지하철_노선_생성;
import static nextstep.subway.station.StationAcceptanceTest.지하철_역_생성;
import static nextstep.subway.utils.AcceptanceTestUtil.delete;
import static nextstep.subway.utils.AcceptanceTestUtil.post;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
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
        ExtractableResponse<Response> response = 지하철_노선에_구간_추가(청계산입구역);

        // then
        구간_등록됨(response);
        응답_결과에_노선과_역_정보가_포함됨(response, 청계산입구역);
    }

    @DisplayName("상행 종점 구간으로 등록한다.")
    @Test
    void addSection_상행_종점_구간() {
        // given
        StationResponse 청계산입구역 = 지하철_역_생성(new StationRequest("청계산입구역")).as(StationResponse.class);

        // when
        ExtractableResponse<Response> response = 상행_종점에_구간_추가(청계산입구역);

        // then
        구간_등록됨(response);
        응답_결과에_노선과_역_정보가_포함됨(response, 청계산입구역);
    }

    @DisplayName("하행 종점 구간으로 등록한다.")
    @Test
    void addSection_하행_종점_구간() {
        // given
        StationResponse 청계산입구역 = 지하철_역_생성(new StationRequest("청계산입구역")).as(StationResponse.class);

        // when
        ExtractableResponse<Response> response = 하행_종점에_구간_추가(청계산입구역);

        // then
        구간_등록됨(response);
        응답_결과에_노선과_역_정보가_포함됨(response, 청계산입구역);
    }

    @DisplayName("역 사이에 새로운 구간을 등록하는 경우, 기존 역 사이 길이보다 크거나 같으면 등록 실패")
    @Test
    void 역_사이에_등록시_거리가_잘못된_경우_등록_실패() {
        // given
        StationResponse 청계산입구역 = 지하철_역_생성(new StationRequest("청계산입구역")).as(StationResponse.class);

        // when
        ExtractableResponse<Response> response = 구간_사이에_기존_구간과_동일한_거리로_구간_추가(청계산입구역);

        // then
        구간_등록_실패(response);
    }

    @DisplayName("상행역, 하행역이 모두 노선에 등록되어 있으면 등록 실패")
    @Test
    void 상행역_하행역_이미_노선에_등록된_경우_등록_실패() {
        // when
        ExtractableResponse<Response> response = 이미_등록된_구간을_등록();

        // then
        구간_등록_실패(response);
    }

    @DisplayName("상행역 하행역 둘 중 하나도 노선에 포함 안된 경우 등록 실패")
    @Test
    void 상행역_하행역_둘_중_하나도_포함_안된_경우_등록_실패() {
        // given
        StationResponse 양재역 = 지하철_역_생성(new StationRequest("양재역")).as(StationResponse.class);
        StationResponse 양재시민의숲역 = 지하철_역_생성(new StationRequest("양재시민의숲역")).as(StationResponse.class);

        // when
        ExtractableResponse<Response> response = 상행역_하행역_둘_중_하나도_노선에_없는_역을_등록(양재역, 양재시민의숲역);

        // then
        구간_등록_실패(response);
    }

    @DisplayName("역을 삭제한 경우 노선 목록 조회시 삭제한 역은 조회되지 않는다.")
    @Test
    void 구간_삭제() {
        // given
        StationResponse 청계산입구역 = 지하철_역_생성(new StationRequest("청계산입구역")).as(StationResponse.class);
        지하철_노선에_구간_추가(청계산입구역);

        // when
        ExtractableResponse<Response> response = 청계산입구역_구간_삭제(청계산입구역);

        // then
        구간_삭제됨(response);

        // when
        List<LineResponse> lines = 지하철_노선_목록_조회()
            .jsonPath().getList(".", LineResponse.class);

        // then
        삭제후에는_노선_목록에서_조회되지_않음(lines.get(0), 청계산입구역);

    }

    private ExtractableResponse<Response> 지하철_노선에_구간_추가(StationResponse 청계산입구역) {
        return post("/lines/" + 신분당선.getId() + "/sections",
            new SectionRequest(강남역.getId(), 청계산입구역.getId(), 8));
    }

    private ExtractableResponse<Response> 상행_종점에_구간_추가(StationResponse 청계산입구역) {
        return post("/lines/" + 신분당선.getId() + "/sections",
            new SectionRequest(청계산입구역.getId(), 강남역.getId(), 8));
    }

    private ExtractableResponse<Response> 하행_종점에_구간_추가(StationResponse 청계산입구역) {
        return post("/lines/" + 신분당선.getId() + "/sections",
            new SectionRequest(광교역.getId(), 청계산입구역.getId(), 8));
    }

    private ExtractableResponse<Response> 구간_사이에_기존_구간과_동일한_거리로_구간_추가(StationResponse 청계산입구역) {
        return post("/lines/" + 신분당선.getId() + "/sections",
            new SectionRequest(강남역.getId(), 청계산입구역.getId(), 10));
    }

    private ExtractableResponse<Response> 이미_등록된_구간을_등록() {
        return post("/lines/" + 신분당선.getId() + "/sections",
            new SectionRequest(강남역.getId(), 광교역.getId(), 8));
    }

    private ExtractableResponse<Response> 상행역_하행역_둘_중_하나도_노선에_없는_역을_등록(StationResponse 양재역,
        StationResponse 양재시민의숲역) {
        return post("/lines/" + 신분당선.getId() + "/sections",
            new SectionRequest(양재역.getId(), 양재시민의숲역.getId(), 8));
    }

    private ExtractableResponse<Response> 청계산입구역_구간_삭제(StationResponse 청계산입구역) {
        return delete("/lines/" + 신분당선.getId() + "/sections?stationId=" + 청계산입구역.getId());
    }

    private void 구간_등록됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 구간_등록_실패(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void 응답_결과에_노선과_역_정보가_포함됨(ExtractableResponse<Response> response,
        StationResponse 청계산입구역) {
        LineResponse line = response.as(LineResponse.class);
        assertThat(line.getStations()).hasSize(3);
        assertThat(line.getStations()).extracting("id", "name")
            .contains(tuple(강남역.getId(), 강남역.getName()),
                tuple(광교역.getId(), 광교역.getName()),
                tuple(청계산입구역.getId(), 청계산입구역.getName()));
    }

    private void 구간_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 삭제후에는_노선_목록에서_조회되지_않음(LineResponse line, StationResponse 청계산입구역) {
        assertThat(line.getStations()).hasSize(2);
        assertThat(line.getStations())
            .extracting("id", "name")
            .containsOnly(tuple(강남역.getId(), 강남역.getName()),
                tuple(광교역.getId(), 광교역.getName()));
    }
}
