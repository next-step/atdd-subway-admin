package nextstep.subway.line;

import static nextstep.subway.line.LineAcceptanceTestUtils.*;
import static nextstep.subway.station.StationAcceptanceTestUtils.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    private Station station1;
    private Station station2;
    private Station station3;
    private Station station4;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        station1 = 지하철_역_등록되어_있음("강남역");
        station2 = 지하철_역_등록되어_있음("양재역");
        station3 = 지하철_역_등록되어_있음("잠실역");
        station4 = 지하철_역_등록되어_있음("사당역");
    }

    @Test
    void 지하철_노선을_생성한다() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "red");
        params.put("upStationId", String.valueOf(station1.getId()));
        params.put("downStationId", String.valueOf(station2.getId()));
        params.put("distance", "10");

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(params);

        // then
        지하철_노선_생성됨(response);
    }

    @Test
    void 기존에_존재하는_지하철_노선_이름으로_지하철_노선을_생성한다() {
        // given
        지하철_노선_등록되어_있음("신분당선", "red", station1, station2, 10);

        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "red");
        params.put("upStationId", String.valueOf(station1.getId()));
        params.put("downStationId", String.valueOf(station2.getId()));
        params.put("distance", "10");

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(params);

        // then
        지하철_노선_생성_실패됨(response);
    }

    @Test
    void 지하철_노선_목록을_조회한다() {
        // given
        long id1 = 지하철_노선_등록되어_있음("신분당선", "red", station1, station2, 10);
        long id2 = 지하철_노선_등록되어_있음("2호선", "green", station3, station4, 10);
        LocalDateTime now = LocalDateTime.now();
        List<LineResponse> expected = Arrays.asList(
            new LineResponse(id1, "신분당선", "red", now, now, Arrays.asList(station1, station2)),
            new LineResponse(id2, "2호선", "green", now, now, Arrays.asList(station3, station4)));

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        지하철_노선_목록_응답됨(response);
        지하철_노선_목록_포함됨(response, expected);
    }

    @Test
    void 지하철_노선을_조회한다() {
        // given
        long id = 지하철_노선_등록되어_있음("신분당선", "red", station1, station2, 10);
        LocalDateTime now = LocalDateTime.now();
        LineResponse expected = new LineResponse(id, "신분당선", "red", now, now, Arrays.asList(station1, station2));

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(id);

        // then
        지하철_노선_응답됨(response, expected);
    }

    @Test
    void 존재하지_않는_지하철_노선을_조회하여_실패한다() {
        // given

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(1L);

        // then
        지하철_노선_미존재_응답됨(response);
    }

    @Test
    void 지하철_노선을_수정한다() {
        // given
        long id = 지하철_노선_등록되어_있음("신분당선", "red", station1, station2, 10);
        Map<String, String> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "green");

        LocalDateTime now = LocalDateTime.now();
        LineResponse expected = new LineResponse(id, "2호선", "green", now, now, Arrays.asList(station1, station2));

        // when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(id, params);

        // then
        지하철_노선_수정됨(response, expected);
    }

    @Test
    void 존재하지_않는_지하철_노선을_수정하여_실패한다() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "green");
        params.put("upStationId", String.valueOf(station3.getId()));
        params.put("downStationId", String.valueOf(station4.getId()));
        params.put("distance", "10");

        // when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(1L, params);

        // then
        지하철_노선_미존재_응답됨(response);
    }

    @Test
    void 지하철_노선을_제거한다() {
        // given
        long id = 지하철_노선_등록되어_있음("신분당선", "red", station1, station2, 10);

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(id);

        // then
        지하철_노선_삭제됨(response);
    }
}
