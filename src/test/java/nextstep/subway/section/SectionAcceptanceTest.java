package nextstep.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineAcceptanceUtil;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.StationAcceptanceUtil;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("노선 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    private long 광교역_id;
    private long 강남역_id;
    private ExtractableResponse<Response> 신분당선;

    @BeforeEach
    public void setUp() {
        super.setUp();

        신분당선 = LineAcceptanceUtil.지하철_노선_구간_생성_요청("신분당선", "bg-red-600", "강남역", "광교역", 10);
        강남역_id = StationAcceptanceUtil.지하철_역_조회("강남역").jsonPath().getObject(".", StationResponse.class).getId();
        광교역_id = StationAcceptanceUtil.지하철_역_조회("광교역").jsonPath().getObject(".", StationResponse.class).getId();
    }

    @DisplayName("노선에 구간을 등록한다.")
    @Test
    void addSection() {
        // given
        long 잠실역_id = StationAcceptanceUtil.지하철_역_생성_요청("잠실역");

        // 노선 조회 후 구간 등록
        LineResponse 신분당선_조회 = 신분당선.body().as(LineResponse.class);
        SectionRequest sectionRequest = new SectionRequest(광교역_id, 잠실역_id, 5);

        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> 지하철_노선_구간_등록 = SectionAcceptanceUtil.지하철_노선_구간_등록_요청(신분당선_조회.getId(), sectionRequest);

        // then
        // 지하철_노선에_지하철역_등록됨
        assertAll(
                () -> 지하철_노선_응답_결과(지하철_노선_구간_등록, HttpStatus.OK),
                () -> assertThat(LineAcceptanceUtil.지하철_노선_조회(신분당선_조회.getId()).body().as(LineResponse.class).getStations())
                        .extracting(StationResponse::getName)
                        .containsExactly("강남역", "광교역", "잠실역")
        );
    }

    @DisplayName("구간 길이 예외 테스트")
    @Test
    void distance_test() {
        // given
        long 잠실역_id = StationAcceptanceUtil.지하철_역_생성_요청("잠실역");

        // 노선 조회 후 구간 등록
        LineResponse 신분당선_조회 = 신분당선.body().as(LineResponse.class);
        SectionRequest sectionRequest = new SectionRequest(잠실역_id, 광교역_id, 10);

        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> 지하철_노선_구간_등록 = SectionAcceptanceUtil.지하철_노선_구간_등록_요청(신분당선_조회.getId(), sectionRequest);

        // then
        // 지하철_노선에_지하철역_등록됨
        지하철_노선_응답_결과(지하철_노선_구간_등록, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void not_included_station() {
        // given
        long 잠실역_id = StationAcceptanceUtil.지하철_역_생성_요청("잠실역");
        long 당산역_id = StationAcceptanceUtil.지하철_역_생성_요청("당산역");

        // 노선 조회 후 구간 등록
        LineResponse 신분당선_조회 = 신분당선.body().as(LineResponse.class);
        SectionRequest sectionRequest = new SectionRequest(잠실역_id, 당산역_id, 10);

        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> 지하철_노선_구간_등록 = SectionAcceptanceUtil.지하철_노선_구간_등록_요청(신분당선_조회.getId(), sectionRequest);

        // then
        // 지하철_노선에_지하철역_등록됨
        지하철_노선_응답_결과(지하철_노선_구간_등록, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("요청된 상행역과 하행역이 이미 있으면 추가할 수 없음")
    @Test
    void already_section() {
        // given
        // 노선 조회 후 구간 등록
        LineResponse 신분당선_조회 = 신분당선.body().as(LineResponse.class);
        SectionRequest sectionRequest = new SectionRequest(강남역_id, 광교역_id, 10);

        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> 지하철_노선_구간_등록 = SectionAcceptanceUtil.지하철_노선_구간_등록_요청(신분당선_조회.getId(), sectionRequest);

        // then
        // 지하철_노선에_지하철역_등록됨
        지하철_노선_응답_결과(지하철_노선_구간_등록, HttpStatus.BAD_REQUEST);
    }

    private void 지하철_노선_응답_결과(ExtractableResponse<Response> response, HttpStatus status) {
        assertThat(response.statusCode()).isEqualTo(status.value());
    }
}
