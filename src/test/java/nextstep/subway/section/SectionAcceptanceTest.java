package nextstep.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionAcceptanceTest extends AcceptanceTest {

    private final String BASE_REQUEST_URI = "/lines/";
    private final String SECTION_ADD_URI = "/sections";
    private final StationAcceptanceTest stationAcceptanceTest = new StationAcceptanceTest();
    private final LineAcceptanceTest lineAcceptanceTest = new LineAcceptanceTest();

    private LineResponse 사호선_생성_응답;
    private SectionRequest 서울_회현_요청;
    private SectionRequest 회현_명동_요청;
    private SectionRequest 명동_충무로_요청;
    private SectionRequest 충무로_동역문_요청;
    private SectionRequest 회현_충무로_요청;

    private Long 사호선_ID;
    private Station 서울역;
    private Station 회현역;
    private Station 명동역;
    private Station 충무로역;
    private Station 동역문역;

    @BeforeEach
    public void setUp() {
        super.setUp();

        서울역 = stationAcceptanceTest.지하철_역_등록되어_있음("서울역").as(Station.class);
        회현역 = stationAcceptanceTest.지하철_역_등록되어_있음("회현역").as(Station.class);
        명동역 = stationAcceptanceTest.지하철_역_등록되어_있음("명동역").as(Station.class);
        충무로역 = stationAcceptanceTest.지하철_역_등록되어_있음("충무로역").as(Station.class);
        동역문역 = stationAcceptanceTest.지하철_역_등록되어_있음("동역문역").as(Station.class);

        사호선_생성_응답 = lineAcceptanceTest.지하철_노선_등록되어_있음(new LineRequest("4호선", "bg-blue", 회현역.getId(), 충무로역.getId(), 20))
                .as(LineResponse.class);

        사호선_ID = 사호선_생성_응답.getId();
        서울_회현_요청 = new SectionRequest(서울역.getId(), 회현역.getId(), 10);
        회현_명동_요청 = new SectionRequest(회현역.getId(), 명동역.getId(), 10);
        명동_충무로_요청 = new SectionRequest(명동역.getId(), 충무로역.getId(), 10);
        충무로_동역문_요청 = new SectionRequest(충무로역.getId(), 동역문역.getId(), 10);
        회현_충무로_요청 = new SectionRequest(회현역.getId(), 충무로역.getId(), 20);
    }

    @DisplayName("구간 등록")
    @Test
    void 구간등록() {
        // when
        //구간을_노선에_등록_요청
        // [회현]==[충무로]에서 [서울역]==[회현]==[충무로]
        ExtractableResponse<Response> response = 구간을_노선에_등록_요청(사호선_ID, 서울_회현_요청);

        // then
        //구간_등록_완료
        구간_OK_응답(response);
        노선에_지하철이_포함되었는지_점검(response, 충무로역);
        노선에_지하철이_포함되었는지_점검(response, 회현역);
        노선에_지하철이_포함되었는지_점검(response, 서울역);
    }

    @DisplayName("구간 등록 : 구간의 상행역이 상행종점")
    @Test
    void 구간등록_when_상행역이_상행종점() {
        // when
        //구간을_노선에_등록_요청
        // [회현]==[충무로]에서 [서울역]==[회현]==[충무로]
        ExtractableResponse<Response> response = 구간을_노선에_등록_요청(사호선_ID, 서울_회현_요청);

        // then
        //구간_등록_완료
        구간_OK_응답(response);
        List<Long> expectedOrderId = Arrays.asList(서울역.getId(), 회현역.getId(), 충무로역.getId());
        노선에_지하철이_순서대로_등록되었는지_점검(response, expectedOrderId);

    }

    @DisplayName("구간 등록 : 구간의 하행역이 하행종점")
    @Test
    void 구간등록_when_하행역이_하행종점() {
        // when
        //구간을_노선에_등록_요청
        // [회현]==[충무로]에서 [회현]==[충무로]==[동역문]
        ExtractableResponse<Response> response = 구간을_노선에_등록_요청(사호선_ID, 충무로_동역문_요청);

        // then
        //구간_등록_완료
        구간_OK_응답(response);
        List<Long> expectedOrderId = Arrays.asList(회현역.getId(), 충무로역.getId(), 동역문역.getId());
        노선에_지하철이_순서대로_등록되었는지_점검(response, expectedOrderId);
    }

    @DisplayName("구간 등록 : 역 사이에 새로운 역 등록(기존 상행역에 연결)")
    @Test
    void 구간등록_when_상행역에_신규_역_연결() {
        // when
        //구간을_노선에_등록_요청
        // [회현]==[충무로]에서 ([회현]==[명동])==[충무로]
        ExtractableResponse<Response> response = 구간을_노선에_등록_요청(사호선_ID, 회현_명동_요청);

        // then
        //구간_등록_완료
        구간_OK_응답(response);
        List<Long> expectedOrderId = Arrays.asList(회현역.getId(), 명동역.getId(), 충무로역.getId());
        노선에_지하철이_순서대로_등록되었는지_점검(response, expectedOrderId);
    }

    @DisplayName("구간 등록 : 역 사이에 새로운 역 등록(기존 하행역에 연결)")
    @Test
    void 구간등록_when_하행역에_신규_역_연결() {
        // when
        //구간을_노선에_등록_요청
        // [회현]==[충무로]에서 [회현]==([명동]==[충무로])
        ExtractableResponse<Response> response = 구간을_노선에_등록_요청(사호선_ID, 명동_충무로_요청);

        // then
        //구간_등록_완료
        구간_OK_응답(response);
        List<Long> expectedOrderId = Arrays.asList(회현역.getId(), 명동역.getId(), 충무로역.getId());
        노선에_지하철이_순서대로_등록되었는지_점검(response, expectedOrderId);
    }

    @DisplayName("구간등록 예외 상황 : 상행/하행역 모두 이미 등록되어 있음")
    @Test
    void 구간등록예외상황_when_상행하행_모두_이미_등록되어있음() {
        // when
        //구간을_노선에_등록_요청
        ExtractableResponse<Response> response = 구간을_노선에_등록_요청(사호선_ID, 회현_충무로_요청);

        // then
        //구간_등록_BAD_REQUEST_응답
        구간_등록_BAD_REQUEST_응답(response);
    }

    @DisplayName("구간등록 예외 상황 : 상행/하행역 모두 등록되어 있지 않음")
    @Test
    void 구간등록예외상황_when_상행하행_모두_등록되어_있지_않음() {
        // when
        //구간을_노선에_등록_요청
        SectionRequest 서울_동역문_요청 = new SectionRequest(서울역.getId(), 동역문역.getId(), 40);
        ExtractableResponse<Response> response = 구간을_노선에_등록_요청(사호선_ID, 서울_동역문_요청);

        // then
        //구간_등록_BAD_REQUEST_응답
        구간_등록_BAD_REQUEST_응답(response);
    }

    @DisplayName("구간등록 예외 상황 : 역사이에 추가하는 구간의 길이가 기존 길이와 같거나 긺")
    @Test
    void 구간등록예외상황_when_역사이에_구간을_추가할때_구간길이가_기존_구간길이와_같거나_긴_경우() {
        // when
        //구간을_노선에_등록_요청
        // [회현]==[충무로]에서 ([회현]==[명동])==[충무로]
        // 이때 회현-충무로간 거리가 20인데 회현-명동의 거리도 30
        SectionRequest newRequest = new SectionRequest(회현역.getId(), 명동역.getId(), 30);
        ExtractableResponse<Response> response = 구간을_노선에_등록_요청(사호선_ID, newRequest);

        // then
        //구간_등록_BAD_REQUEST_응답
        구간_등록_BAD_REQUEST_응답(response);
    }

    @DisplayName("구간 제거 : 노선의 마지막 구간 제거")
    @Test
    void 구간제거_when_마지막_구간() {
        //given
        //구간을_노선에_등록_요청
        // [회현]==[충무로]==[동역문]
        구간을_노선에_등록_요청(사호선_ID, 충무로_동역문_요청);

        // when
        //구간을_노선에서_제거_요청
        ExtractableResponse<Response> response = 구간을_노선에서_제거_요청(사호선_ID, 동역문역.getId());

        // then
        //구간_제거_완료
        구간_OK_응답(response);
        List<Long> expectedOrderId = Arrays.asList(회현역.getId(), 충무로역.getId());
        노선에_지하철이_순서대로_등록되었는지_점검(response, expectedOrderId);
    }

    @DisplayName("구간 제거 : 노선의 첫번째 구간 제거")
    @Test
    void 구간제거_when_첫번째_구간() {
        //given
        //구간을_노선에_등록_요청

        // when
        //구간을_노선에_제거_요청

        // then
        //구간_제거_완료
    }

    @DisplayName("구간 제거 : 노선의 중간 구간 제거")
    @Test
    void 구간제거_when_중간_구간() {
        //given
        //구간을_노선에_등록_요청

        // when
        //구간을_노선에_제거_요청

        // then
        //구간_제거_완료
    }

    @DisplayName("구간제거 예외상황 : 노선에 구간이 단 한 개인 경우")
    @Test
    void 구간제거예외상황_when_구간이_단_한개() {
        //given
        //구간을_노선에_등록_요청

        // when
        //구간을_노선에_제거_요청

        // then
        //구간_제거_완료
    }

    @DisplayName("구간제거 예외상황 : 노선에 없는 구간인 경우")
    @Test
    void 구간제거예외상황_when_존재하지_않는_구간() {
        //given
        //구간을_노선에_등록_요청

        // when
        //구간을_노선에_제거_요청

        // then
        //구간_제거_완료
    }

    private void 구간_OK_응답(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 구간_등록_BAD_REQUEST_응답(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> 구간을_노선에_등록_요청(Long lineId, SectionRequest request) {
        return httpPost(BASE_REQUEST_URI + lineId + SECTION_ADD_URI, request);
    }

    private ExtractableResponse<Response> 구간을_노선에서_제거_요청(Long lineId, Long stationId) {
        return httpDelete(BASE_REQUEST_URI + lineId + SECTION_ADD_URI + "?stationId=" + stationId);
    }

    private void 노선에_지하철이_포함되었는지_점검(ExtractableResponse<Response> response, Station station) {
        List<Long> stationId = response.as(LineResponse.class).getStationResponses().stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        assertThat(stationId).contains(station.getId());
    }

    private void 노선에_지하철이_순서대로_등록되었는지_점검(ExtractableResponse<Response> response, List<Long> stationIds) {
        List<Long> actualIds = response.as(LineResponse.class).getStationResponses().stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        assertThat(actualIds).containsExactlyElementsOf(stationIds);
    }
}
