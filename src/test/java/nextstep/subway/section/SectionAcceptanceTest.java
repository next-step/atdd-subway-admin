package nextstep.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineAcceptanceTestSupport;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.StationAcceptanceTestSupport;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    private static final String 강남역 = "강남역";
    private static final String 양재역 = "양재역";
    private static final String 판교역 = "판교역";
    private static final String 서울숲역 = "서울숲역";
    private LineResponse lineResponse;

    @BeforeEach
    void beforeEach() {
        ExtractableResponse<Response> response
                = LineAcceptanceTestSupport.지하철_노선_생성_요청("신분당선", "bg-red-600"
                , 강남역, 양재역, 10);
        this.lineResponse = response.as(LineResponse.class);
    }

    @Test
    @DisplayName("노선에 구간을 기존 구간 사이에 등록한다.")
    void addSectionPutInExistSection() {
        // given
        Long addStationId = StationAcceptanceTestSupport.지하철_역_생성_요청(new StationRequest(판교역))
                .as(StationResponse.class).getId();

        SectionRequest sectionRequest
                = new SectionRequest(LineAcceptanceTestSupport.getUpStationId(), addStationId, 5);

        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> sectionAddResponse
                = SectionAcceptanceTestSupport.지하철_노선에_지하철역_등록_요청(
                        this.lineResponse.getId(), sectionRequest);

        LineResponse findUpdateLineResponse
                = LineAcceptanceTestSupport.지하철_노선_조회_요청("/lines/" + this.lineResponse.getId())
                .as(LineResponse.class);

        // then
        // 지하철 노선이 추가 됨
        assertThat(sectionAddResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(findUpdateLineResponse.getStations().size()).isEqualTo(3);
        assertThat(findUpdateLineResponse.getStations().stream().map(StationResponse::getName))
                .contains(강남역, 양재역, 판교역);
    }

    @Test
    @DisplayName("노선에 구간을 맨앞에-상행종점- 등록한다.")
    void addSectionToFirst() {
        // given
        Long addStationId = StationAcceptanceTestSupport.지하철_역_생성_요청(new StationRequest(판교역))
                .as(StationResponse.class).getId();

        SectionRequest sectionRequest
                = new SectionRequest(addStationId, LineAcceptanceTestSupport.getUpStationId(), 5);

        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> sectionAddResponse
                = SectionAcceptanceTestSupport.지하철_노선에_지하철역_등록_요청(
                        this.lineResponse.getId(), sectionRequest);

        LineResponse findUpdateLineResponse
                = LineAcceptanceTestSupport.지하철_노선_조회_요청("/lines/" + this.lineResponse.getId())
                .as(LineResponse.class);

        // then
        // 지하철 노선이 추가 됨
        assertThat(sectionAddResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(findUpdateLineResponse.getStations().size()).isEqualTo(3);
        assertThat(findUpdateLineResponse.getStations().stream().map(StationResponse::getName))
                .contains(강남역, 양재역, 판교역);
    }

    @Test
    @DisplayName("노선에 구간을 맨뒤에-하행종점- 등록한다.")
    void addSectionToLast() {
        // given
        Long addStationId = StationAcceptanceTestSupport.지하철_역_생성_요청(new StationRequest(판교역))
                .as(StationResponse.class).getId();

        SectionRequest sectionRequest
                = new SectionRequest(LineAcceptanceTestSupport.getDownStationId(), addStationId, 5);

        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> sectionAddResponse
                = SectionAcceptanceTestSupport.지하철_노선에_지하철역_등록_요청(
                this.lineResponse.getId(), sectionRequest);

        LineResponse findUpdateLineResponse
                = LineAcceptanceTestSupport.지하철_노선_조회_요청("/lines/" + this.lineResponse.getId())
                .as(LineResponse.class);

        // then
        // 지하철 노선이 추가 됨
        assertThat(sectionAddResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(findUpdateLineResponse.getStations().size()).isEqualTo(3);
        assertThat(findUpdateLineResponse.getStations().stream().map(StationResponse::getName))
                .contains(강남역, 양재역, 판교역);
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 11})
    @DisplayName("노선에 구간을 등록 시 기존 구간의 길이보다 크거나 같으면 오류 발생.")
    void addSectionMoreThanDistanceOccurredException(int input) {
        // given
        Long addStationId = StationAcceptanceTestSupport.지하철_역_생성_요청(new StationRequest(판교역))
                .as(StationResponse.class).getId();

        SectionRequest sectionRequest
                = new SectionRequest(LineAcceptanceTestSupport.getUpStationId(), addStationId, input);

        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> sectionAddResponse
                = SectionAcceptanceTestSupport.지하철_노선에_지하철역_등록_요청(
                        this.lineResponse.getId(), sectionRequest);

        // then
        // 지하철 노선 추가 시 오류 발생
        assertThat(sectionAddResponse.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    @DisplayName("노선에 구간을 등록 시 기존 구간의 길이보다 크거나 같으면 오류 발생.")
    void addSectionAlreadyRegisteredStationOccurredException() {
        // given
        Long addStationId1 = StationAcceptanceTestSupport.지하철_역_생성_요청(new StationRequest(판교역))
                .as(StationResponse.class).getId();
        Long addStationId2 = StationAcceptanceTestSupport.지하철_역_생성_요청(new StationRequest(서울숲역))
                .as(StationResponse.class).getId();

        SectionRequest sectionRequest = new SectionRequest(addStationId1, addStationId2, 5);

        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> sectionAddResponse
                = SectionAcceptanceTestSupport.지하철_노선에_지하철역_등록_요청(
                        this.lineResponse.getId(), sectionRequest);

        // then
        // 지하철 노선 추가 시 오류 발생
        assertThat(sectionAddResponse.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    @DisplayName("노선에 등록 된 구간을 삭제")
    void deleteSection() {
        // given
        Long addStationId = StationAcceptanceTestSupport.지하철_역_생성_요청(new StationRequest(판교역))
                .as(StationResponse.class).getId();

        SectionRequest sectionRequest
                = new SectionRequest(LineAcceptanceTestSupport.getUpStationId(), addStationId, 5);

        ExtractableResponse<Response> sectionAddResponse
                = SectionAcceptanceTestSupport.지하철_노선에_지하철역_등록_요청(
                this.lineResponse.getId(), sectionRequest);

        // when
        // 지하철_노선에_지하철역_삭제_요청
        ExtractableResponse<Response> sectionRemoveResponse
                = SectionAcceptanceTestSupport.지하철_노선에_지하철역_삭제_요청(
                this.lineResponse.getId(), addStationId);

        LineResponse findUpdateLineResponse
                = LineAcceptanceTestSupport.지하철_노선_조회_요청("/lines/" + this.lineResponse.getId())
                .as(LineResponse.class);

        // then
        // 지하철 구간 삭제 됨
        assertThat(sectionRemoveResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(findUpdateLineResponse.getStations().size()).isEqualTo(2);
        assertThat(findUpdateLineResponse.getStations().stream().map(StationResponse::getName)).doesNotContain(판교역);
    }
}
