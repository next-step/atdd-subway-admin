package nextstep.subway.section;

import static nextstep.subway.line.LineAcceptanceTestHelper.*;
import static nextstep.subway.section.SectionAcceptanceTestHelper.*;
import static nextstep.subway.station.StationAcceptanceTestHelper.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.domain.Station;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {
    private Station 강남역;
    private Station 양재역;
    private Station 판교역;
    private Station 잠실역;
    private Station 삼성역;

    private long 지하철_신분당선_id;
    private long 지하철_2호선_id;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철_역_등록되어_있음("강남역");
        양재역 = 지하철_역_등록되어_있음("양재역");
        판교역 = 지하철_역_등록되어_있음("판교역");
        잠실역 = 지하철_역_등록되어_있음("잠실역");
        삼성역 = 지하철_역_등록되어_있음("삼성역");

        지하철_신분당선_id = 지하철_노선_등록되어_있음("신분당선", "red", 강남역, 양재역, 10);
        지하철_2호선_id = 지하철_노선_등록되어_있음("2호선", "green", 잠실역, 강남역, 100);
    }

    @DisplayName("노선에 구간을 등록한다.")
    @Test
    void 노선에_구간을_등록한다() {
        //given
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", String.valueOf(양재역.getId()));
        params.put("downStationId", String.valueOf(판교역.getId()));
        params.put("distance", "10");

        // when
        ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(지하철_신분당선_id, params);

        // then
        지하철_노선에_구간_등록됨(response);
        지하철_노선에_지하철역_포함됨(지하철_신분당선_id, 판교역);
    }

    @DisplayName("노선 구간 사이에 새로운 역을 등록한다")
    @Test
    void 노선_구간_사이에_새로운_역을_등록한다() {
        //given
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", String.valueOf(삼성역.getId()));
        params.put("downStationId", String.valueOf(강남역.getId()));
        params.put("distance", "10");

        // when
        ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(지하철_2호선_id, params);

        // then
        지하철_노선에_구간_등록됨(response);
        지하철_노선에_포함된_역들이_일치함(지하철_2호선_id, Arrays.asList(잠실역, 삼성역, 강남역));
    }

    @DisplayName("기존 역 사이 길이보다 큰 노선을 추가하여 실패한다")
    @ParameterizedTest
    @ValueSource(strings = {"100", "101"})
    void 기존_역_사이_길이보다_크거나_같은_노선을_추가하여_실패한다(String distance) {
        //given
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", String.valueOf(삼성역.getId()));
        params.put("downStationId", String.valueOf(강남역.getId()));
        params.put("distance", distance);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(지하철_2호선_id, params);

        // then
        지하철_노선에_구간_추가_실패됨(response);
    }

    @DisplayName("새로운 역을 상행 종점으로 등록한다")
    @Test
    void 새로운_역을_상행_종점으로_등록한다() {
        //given
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", String.valueOf(삼성역.getId()));
        params.put("downStationId", String.valueOf(강남역.getId()));
        params.put("distance", "10");

        // when
        ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(지하철_신분당선_id, params);

        // then
        지하철_노선에_구간_등록됨(response);
        지하철_노선에_포함된_역들이_일치함(지하철_신분당선_id, Arrays.asList(삼성역, 강남역, 양재역));
    }

    @DisplayName("새로운 역을 하행 종점으로 등록한다")
    @Test
    void 새로운_역을_하행_종점으로_등록한다() {
        //given
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", String.valueOf(양재역.getId()));
        params.put("downStationId", String.valueOf(판교역.getId()));
        params.put("distance", "10");

        // when
        ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(지하철_신분당선_id, params);

        // then
        지하철_노선에_구간_등록됨(response);
        지하철_노선에_포함된_역들이_일치함(지하철_신분당선_id, Arrays.asList(강남역, 양재역, 판교역));
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void 상행역과_하행역이_이미_노선에_모두_등록되어_있다면_추가할_수_없음() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", String.valueOf(잠실역.getId()));
        params.put("downStationId", String.valueOf(강남역.getId()));
        params.put("distance", "10");

        // when
        ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(지하철_2호선_id, params);

        // then
        지하철_노선에_구간_추가_실패됨(response);
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void 상행역과_하행역_둘_중_하나도_포함되어있지_않으면_추가할_수_없음() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", String.valueOf(양재역.getId()));
        params.put("downStationId", String.valueOf(판교역.getId()));
        params.put("distance", "10");

        // when
        ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(지하철_2호선_id, params);

        // then
        지하철_노선에_구간_추가_실패됨(response);
    }

    @DisplayName("종점이 제거될 경우 다음으로 오던 역이 종점이 됨")
    @Test
    void 종점이_제거될_경우_다음으로_오던_역이_종점이_됨() {
        // given
        지하철_노선에_구간_등록되어_있음(지하철_2호선_id, 잠실역, 삼성역, "10");

        // when
        ExtractableResponse<Response> response = 지하철_노선예_구간_삭제_요청(지하철_2호선_id, 잠실역.getId());

        // then
        지하철_노선에_구간_삭제됨(response);
        지하철_노선에_포함된_역들이_일치함(지하철_2호선_id, Arrays.asList(삼성역, 강남역));
    }
}
