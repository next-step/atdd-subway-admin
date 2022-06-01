package nextstep.subway.acceptance;

import static org.assertj.core.api.Assertions.*;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("지하철구간 관련 기능")
public class SectionAcceptanceTest extends BaseAcceptanceTest {
    public static final String path = "/sections";

    StationResponse A역;
    StationResponse B역;
    StationResponse C역;
    StationResponse D역;
    StationResponse E역;
    LineResponse 신분당선;

    @BeforeEach
    public void setUp() {
        super.setUp();

        A역 = StationAcceptanceTest.지하철역_생성("A역").as(StationResponse.class);
        B역 = StationAcceptanceTest.지하철역_생성("B역").as(StationResponse.class);
        C역 = StationAcceptanceTest.지하철역_생성("C역").as(StationResponse.class);
        D역 = StationAcceptanceTest.지하철역_생성("D역").as(StationResponse.class);
        E역 = StationAcceptanceTest.지하철역_생성("E역").as(StationResponse.class);

        신분당선 = LineAcceptanceTest.지하철노선_생성("신분당선", "bg-red-600", B역.getId(), D역.getId(), 7)
                .as(LineResponse.class);
    }

    /**
     * When 신분당선에 B역-C역 구간을 등록하면 (B-C-D)
     * Then 신분당선에 C역이 등록된다.
     */
    @DisplayName("중간역을 등록한다.")
    @Test
    void 중간역_등록() {
        //when
        ExtractableResponse<Response> response = 지하철노선_구간_추가(신분당선.getId(), B역.getId(), C역.getId(), 4);

        //then
        응답결과_확인(response, HttpStatus.OK);
        지하철노선_지하철역_포함됨(response, C역);
    }

    /**
     * When 신분당선에 A역-B역 구간을 등록하면 (A-B-D)
     * Then 신분당선에 A역이 등록된다.
     */
    @DisplayName("새로운 역을 상행 종점으로 등록한다.")
    @Test
    void 상행_종점_등록() {
        //when
        ExtractableResponse<Response> response = 지하철노선_구간_추가(신분당선.getId(), A역.getId(), B역.getId(), 4);

        //then
        응답결과_확인(response, HttpStatus.OK);
        지하철노선_지하철역_포함됨(response, A역);
    }

    /**
     * When 신분당선에 D역-E역 구간을 등록하면 (B-D-E)
     * Then 신분당선에 E역이 등록된다.
     */
    @DisplayName("새로운 역을 하행 종점으로 등록한다.")
    @Test
    void 하행_종점_등록() {
        //when
        ExtractableResponse<Response> response = 지하철노선_구간_추가(신분당선.getId(), B역.getId(), E역.getId(), 3);

        //then
        응답결과_확인(response, HttpStatus.OK);
        지하철노선_지하철역_포함됨(response, E역);
    }

    /**
     * When 신분당선에 B역-C역 구간을 길이 20으로 등록하면 (B-C-D)
     * Then Internal Server Error(500)가 발생한다.
     */
    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없다.")
    @Test
    void invalid_중간역_길이_등록불가() {
        //when
        ExtractableResponse<Response> response = 지하철노선_구간_추가(신분당선.getId(), B역.getId(), C역.getId(), 20);

        //then
        응답결과_확인(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * When 신분당선에 B역-D역 구간을 등록하면
     * Then Internal Server Error(500)가 발생한다.
     */
    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없다.")
    @Test
    void invalid_상하행_중복_등록불가() {
        //when
        ExtractableResponse<Response> response = 지하철노선_구간_추가(신분당선.getId(), B역.getId(), D역.getId(), 7);

        //then
        응답결과_확인(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * When 신분당선에 A역-E역 구간을 등록하면
     * Then Internal Server Error(500)가 발생한다.
     */
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없다.")
    @Test
    void invalid_상하행_미포함_등록불가() {
        //when
        ExtractableResponse<Response> response = 지하철노선_구간_추가(신분당선.getId(), A역.getId(), E역.getId(), 20);

        //then
        응답결과_확인(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static ExtractableResponse<Response> 지하철노선_구간_추가(Long id, Long upStationId, Long downStationId,
                                                            Integer distance) {
        return post("/lines/" + id + path, new SectionRequest(upStationId, downStationId, distance));
    }

    private void 지하철노선_지하철역_포함됨(ExtractableResponse<Response> response, StationResponse expected) {
        List<StationResponse> stations = response.jsonPath().getList("stations", Station.class)
                .stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
        assertThat(stations).contains(expected);
    }
}
