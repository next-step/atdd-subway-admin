package nextstep.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.BaseAcceptanceTest;
import nextstep.subway.common.RestAssuredTemplate;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static nextstep.subway.line.LineAcceptanceTest.지하철_노선_생성됨;
import static nextstep.subway.line.LineAcceptanceTest.지하철_노선_역_이름_리스트_조회;
import static nextstep.subway.station.StationAcceptanceTest.지하철역_생성됨;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends BaseAcceptanceTest {

    private Long 강남역_ID;
    private Long 광교역_ID;
    private Long 양재역_ID;
    private Long 판교역_ID;
    private Long 정자역_ID;
    private LineResponse 신분당선;

    @BeforeEach
    public void setUp() {
        super.setUp();
        강남역_ID = 지하철역_생성됨("강남역").body().jsonPath().getLong("id");
        광교역_ID = 지하철역_생성됨("광교역").body().jsonPath().getLong("id");
        양재역_ID = 지하철역_생성됨("양재역").body().jsonPath().getLong("id");
        판교역_ID = 지하철역_생성됨("판교역").body().jsonPath().getLong("id");
        정자역_ID = 지하철역_생성됨("정자역").body().jsonPath().getLong("id");

        신분당선 = 지하철_노선_생성됨("신분당선", "bg-red-600", 10, 강남역_ID, 광교역_ID).as(LineResponse.class);
    }

    /**
     * When 지하철역 사이에 새로운 역을 등록할 경우
     * Then 등록 성공 응답을 반환한다
     * Then 정상적으로 추가된 지하철역 리스트를 확인한다
     */
    @Test
    void 역_사이에_새로운_역을_등록() {
        // when
        ExtractableResponse<Response> response = 지하철_구간_생성됨(신분당선.getId(), 강남역_ID, 양재역_ID, 4);
        List<String> stationNames = 지하철_노선_역_이름_리스트_조회(신분당선.getId());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(stationNames).containsExactly("강남역", "양재역", "광교역");
    }

    @Test
    void 새로운_역을_상행_종점으로_등록() {

    }

    @Test
    void 새로운_역을_하행_종점으로_등록() {

    }

    /**
     * When 지하철역 사이에 기존 역 사이 길이보다 크거나 같은 구간을 등록할 경우
     * Then 등록 실패 응답을 반환한다
     */
    @Test
    void 역_사이에_새로운_역을_등록할_경우_기존_역_사이_길이보다_크거나_같으면_등록을_할_수_없음() {
        // when
        ExtractableResponse<Response> response = 지하철_구간_생성됨(신분당선.getId(), 강남역_ID, 양재역_ID, 10);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void 상행역과_하행역이_이미_노선에_모두_등록되어_있다면_추가할_수_없음() {
    }

    @Test
    void 상행역과_하행역_둘_중_하나도_포함되어있지_않으면_추가할_수_없음() {

    }

    public static ExtractableResponse<Response> 지하철_구간_생성됨(Long lineId, Long upStationId, Long downStationId, Integer distance) {
        return RestAssuredTemplate.post("/lines/" + lineId + "/sections", new SectionRequest(upStationId, downStationId, distance));
    }

}