package nextstep.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    final static private String uri = "/lines/%s/sections";

    private StationAcceptanceTest stationAcceptanceTest = new StationAcceptanceTest();
    private LineAcceptanceTest lineAcceptanceTest = new LineAcceptanceTest();

    private Long 강남역Id;
    private Long 역삼역Id;
    private Long 신분당선Id;

    @BeforeEach
    public void setUp() {
        super.setUp();

        // given
        StationResponse 강남역 = stationAcceptanceTest.지하철_역_등록되어_있음(new StationRequest("강남역"));
        StationResponse 역삼역 = stationAcceptanceTest.지하철_역_등록되어_있음(new StationRequest("역삼역"));
        강남역Id = 강남역.getId();
        역삼역Id = 역삼역.getId();
        LineRequest 신분당선_요청 = new LineRequest("신분당선", "bg-red-600", 강남역Id, 역삼역Id, 10);
        LineResponse 신분당선 = lineAcceptanceTest.지하철_노선_등록되어_있음(신분당선_요청);
        신분당선Id = 신분당선.getId();
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우")
    @Test
    void 역_사이에_새로운_역을_등록() {
        StationResponse 선릉역 = stationAcceptanceTest.지하철_역_등록되어_있음(new StationRequest("선릉역"));
        SectionRequest sectionRequest = new SectionRequest(강남역Id, 선릉역.getId(), 4);

        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = 데이터_생성_요청(sectionRequest, String.format(uri, 신분당선Id));
        SectionResponse sectionResponse = response.as(SectionResponse.class);

        // then
        // 지하철_노선에_지하철역_등록됨
        데이터_생성됨(response);
    }

    @DisplayName("새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void 새로운_역을_상행_종점으로_등록할_경우() {
        StationResponse 선릉역 = stationAcceptanceTest.지하철_역_등록되어_있음(new StationRequest("선릉역"));
        SectionRequest sectionRequest = new SectionRequest(선릉역.getId(), 강남역Id, 4);

        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = 데이터_생성_요청(sectionRequest, String.format(uri, 신분당선Id));
        SectionResponse sectionResponse = response.as(SectionResponse.class);

        // then
        // 지하철_노선에_지하철역_등록됨
        데이터_생성됨(response);
    }

    @DisplayName("새로운 역을 하행 종점으로 등록할 경우")
    @Test
    void 새로운_역을_하행_종점으로_등록할_경우() {
        StationResponse 선릉역 = stationAcceptanceTest.지하철_역_등록되어_있음(new StationRequest("선릉역"));
        SectionRequest sectionRequest = new SectionRequest(선릉역.getId(), 역삼역Id, 4);

        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = 데이터_생성_요청(sectionRequest, String.format(uri, 신분당선Id));
        SectionResponse sectionResponse = response.as(SectionResponse.class);

        // then
        // 지하철_노선에_지하철역_등록됨
        데이터_생성됨(response);
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음.")
    @Test
    void 역_사이에_새로운_역을_등록_실패() {
        StationResponse 선릉역 = stationAcceptanceTest.지하철_역_등록되어_있음(new StationRequest("선릉역"));
        SectionRequest sectionRequest = new SectionRequest(강남역Id, 선릉역.getId(), 10);

        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = 데이터_생성_요청(sectionRequest, String.format(uri, 신분당선Id));

        // then
        // 지하철_노선에_지하철역_등록_실패
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.asString()).contains("입력한 구간의 길이가 옳바르지 않습니다.")
        );
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음.")
    @Test
    void 역_사이에_동일_역을_등록_실패() {
        SectionRequest sectionRequest = new SectionRequest(강남역Id, 역삼역Id, 4);

        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = 데이터_생성_요청(sectionRequest, String.format(uri, 신분당선Id));

        // then
        // 지하철_노선에_지하철역_등록_실패
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.asString()).contains("연결할 수 없는 역이 포함되어 있습니다.")
        );
    }

    @DisplayName("노선의 구간을 제거하는 기능.")
    @Test
    void 노선의_구간을_제거_성공() {
        // given
        StationResponse 선릉역 = stationAcceptanceTest.지하철_역_등록되어_있음(new StationRequest("선릉역"));
        SectionRequest sectionRequest = new SectionRequest(강남역Id, 선릉역.getId(), 4);
        ExtractableResponse<Response> createResponse = 데이터_생성_요청(sectionRequest, String.format(uri, 신분당선Id));
        SectionResponse sectionResponse = createResponse.as(SectionResponse.class);

        // when
        String deleteUri = String.format("/lines/%s/sections?stationId=%s", 신분당선Id, 선릉역.getId());
        ExtractableResponse<Response> response = 데이터_제거_요청(deleteUri);

        // then
        데이터_삭제완료됨(response);
    }

    @DisplayName("노선의 마지막 구간을 제거 실패")
    @Test
    void 노선의_마지막_구간을_제거_실패() {
        // given
        StationResponse 선릉역 = stationAcceptanceTest.지하철_역_등록되어_있음(new StationRequest("선릉역"));
        SectionRequest sectionRequest = new SectionRequest(강남역Id, 선릉역.getId(), 4);
        ExtractableResponse<Response> createResponse = 데이터_생성_요청(sectionRequest, String.format(uri, 신분당선Id));
        SectionResponse sectionResponse = createResponse.as(SectionResponse.class);

        // when
        String deleteUri = String.format("/lines/%s/sections?stationId=%s", 신분당선Id, 강남역Id);
        ExtractableResponse<Response> response = 데이터_제거_요청(deleteUri);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.asString()).contains("삭제할 수 없는 역입니다.")
        );
    }

}
