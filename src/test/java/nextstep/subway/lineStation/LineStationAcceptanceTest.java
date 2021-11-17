package nextstep.subway.lineStation;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineStationRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.line.LineAcceptanceTestMethod.*;
import static nextstep.subway.utils.Fixture.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
public class LineStationAcceptanceTest extends AcceptanceTest {

    private LineAndLineStation 신분당선_생성_강남_광교() {
        StationResponse 강남 = 역_생성(강남역);
        StationResponse 광교 = 역_생성(광교역);
        LineResponse response = 신규_지하철_노선_생성_요청("/lines", 신분당선, 14, 강남, 광교).as(LineResponse.class);

        return new LineAndLineStation(response, Arrays.asList(강남, 광교));
    }

    @DisplayName("[상행기준] 지하철 노선의 구간을 추가한다. (강남 -> 광교) => ([기준]강남 -> 홍대 -> 광교)")
    @Test
    void 상행을_기준으로_신규_노선_추가() {
        // given
        LineAndLineStation 신분당선_생성_강남_광교 = 신분당선_생성_강남_광교();
        Long 신분당선_아이디 = 신분당선_생성_강남_광교.getLineResponse().getId();
        StationResponse 추가역 = 역_생성(홍대역);
        StationResponse 강남역 = 신분당선_생성_강남_광교.getStationResponses()
                .stream()
                .filter(f -> f.getName().equals("강남역"))
                .findFirst().get();
        StationResponse 광교역 = 신분당선_생성_강남_광교.getStationResponses()
                .stream()
                .filter(f -> f.getName().equals("광교역"))
                .findFirst().get();

        // when
        ExtractableResponse<Response> actual
                = 지하철_노선_구간_추가("/lines/{id}/lineStations", 신분당선_아이디, 구간_추가(강남역, 추가역, 1));

        // then
        응답_확인_OK(actual);
        순서_검증(신분당선_아이디, 강남역, 추가역, 광교역);
    }

    @DisplayName("[상행기준] 지하철 노선의 구간을 추가한다. 길이초과 발생")
    @Test
    void 상행을_기준으로_신규_노선_추가_길이초과() {
        // given
        LineAndLineStation 신분당선_생성_강남_광교 = 신분당선_생성_강남_광교();
        Long 신분당선_아이디 = 신분당선_생성_강남_광교.getLineResponse().getId();
        StationResponse 추가역 = 역_생성(홍대역);
        StationResponse 강남역 = 신분당선_생성_강남_광교.getStationResponses()
                .stream()
                .filter(f -> f.getName().equals("강남역"))
                .findFirst().get();
        StationResponse 광교역 = 신분당선_생성_강남_광교.getStationResponses()
                .stream()
                .filter(f -> f.getName().equals("광교역"))
                .findFirst().get();

        // when
        ExtractableResponse<Response> actual
                = 지하철_노선_구간_추가("/lines/{id}/lineStations", 신분당선_아이디, 구간_추가(강남역, 추가역, 100));

        응답_확인_BAD_REQUEST(actual);
    }

    @DisplayName("[상행기준] 지하철 신규 상행 종점 추가 (강남 -> 광교) => (홍대 -> 강남 -> 광교)")
    @Test
    void 지하철역_상행_신규_종점_추가() {
        // given
        LineAndLineStation 신분당선_생성_강남_광교 = 신분당선_생성_강남_광교();
        Long 신분당선_아이디 = 신분당선_생성_강남_광교.getLineResponse().getId();
        StationResponse 추가역 = 역_생성(홍대역);
        StationResponse 강남역 = 신분당선_생성_강남_광교.getStationResponses()
                .stream()
                .filter(f -> f.getName().equals("강남역"))
                .findFirst().get();
        StationResponse 광교역 = 신분당선_생성_강남_광교.getStationResponses()
                .stream()
                .filter(f -> f.getName().equals("광교역"))
                .findFirst().get();

        // when
        ExtractableResponse<Response> actual
                = 지하철_노선_구간_추가("/lines/{id}/lineStations", 신분당선_아이디, 구간_추가(추가역, 강남역, 5));
        ExtractableResponse<Response> lines = 지하철_노선_목록_조회_요청("/lines");

        // then
        응답_확인_OK(actual);
        상행_혹은_하행_추가_검증(lines, 추가역);
        순서_검증(신분당선_아이디, 추가역, 강남역, 광교역);
    }

    @DisplayName("[하행기준] 지하철 노선의 구간을 추가한다. (강남 -> 광교) => (강남 -> 홍대 -> [기준]광교)")
    @Test
    void 하행을_기준으로_신규_노선_추가() {
        // given
        LineAndLineStation 신분당선_생성_강남_광교 = 신분당선_생성_강남_광교();
        StationResponse 추가역 = 역_생성(홍대역);
        Long 신분당선_아이디 = 신분당선_생성_강남_광교.getLineResponse().getId();
        StationResponse 광교역 = 신분당선_생성_강남_광교.getStationResponses()
                .stream()
                .filter(f -> f.getName().equals("광교역"))
                .findFirst().get();

        // when
        ExtractableResponse<Response> actual
                = 지하철_노선_구간_추가("/lines/{id}/lineStations", 신분당선_아이디, 구간_추가(추가역, 광교역, 1));

        // then
        응답_확인_OK(actual);
    }

    @DisplayName("[하행기준] 지하철 노선의 구간을 추가한다. 길이초과 발생")
    @Test
    void 하행을_기준으로_신규_노선_추가_길이초과() {
        // given
        LineAndLineStation 신분당선_생성_강남_광교 = 신분당선_생성_강남_광교();
        StationResponse 추가역 = 역_생성(홍대역);
        Long 신분당선_아이디 = 신분당선_생성_강남_광교.getLineResponse().getId();
        StationResponse 광교역 = 신분당선_생성_강남_광교.getStationResponses()
                .stream()
                .filter(f -> f.getName().equals("광교역"))
                .findFirst().get();

        // when
        ExtractableResponse<Response> actual
                = 지하철_노선_구간_추가("/lines/{id}/lineStations", 신분당선_아이디, 구간_추가(추가역, 광교역, 100));

        응답_확인_BAD_REQUEST(actual);
    }

    @DisplayName("[하행기준] 지하철 하행 신규 종점 추가 (강남 -> 광교) => (강남 -> 광교 -> 홍대)")
    @Test
    void 지하철역_하행_신규_종점_추가() {
        // given
        LineAndLineStation 신분당선_생성_강남_광교 = 신분당선_생성_강남_광교();
        Long 신분당선_아이디 = 신분당선_생성_강남_광교.getLineResponse().getId();
        StationResponse 추가역 = 역_생성(홍대역);
        StationResponse 강남역 = 신분당선_생성_강남_광교.getStationResponses()
                .stream()
                .filter(f -> f.getName().equals("강남역"))
                .findFirst().get();
        StationResponse 광교역 = 신분당선_생성_강남_광교.getStationResponses()
                .stream()
                .filter(f -> f.getName().equals("광교역"))
                .findFirst().get();

        // when
        ExtractableResponse<Response> actual
                = 지하철_노선_구간_추가("/lines/{id}/lineStations", 신분당선_아이디, 구간_추가(광교역, 추가역, 5));
        ExtractableResponse<Response> lines = 지하철_노선_목록_조회_요청("/lines");

        // then
        응답_확인_OK(actual);
        상행_혹은_하행_추가_검증(lines, 추가역);
        순서_검증(신분당선_아이디, 강남역, 광교역, 추가역);
    }

    @DisplayName("지하철 노선의 구간을 추가한다.(동일한 건에 대한 오류 발생)")
    @Test
    void 노선_동일역_추가() {
        StationResponse 강남 = 역_생성(강남역);
        StationResponse 광교 = 역_생성(광교역);
        ExtractableResponse<Response> response = 신규_지하철_노선_생성_요청("/lines", 신분당선, 10, 강남, 광교);
        Long id = response.as(LineResponse.class).getId();

        ExtractableResponse<Response> actual
                = 지하철_노선_구간_추가("/lines/{id}/lineStations", id, 구간_추가(강남, 광교, 5));

        응답_확인_BAD_REQUEST(actual);
    }

    @DisplayName("지하철 노선의 구간을 추가한다.(연결 고리가 없어 연결을 못하는 오류 발생)")
    @Test
    void 노선_추가_연결_실패() {
        ExtractableResponse<Response> response = 신규_지하철_노선_생성_요청("/lines", 신분당선, 10, 역_생성(강남역), 역_생성(광교역));
        Long id = response.as(LineResponse.class).getId();

        ExtractableResponse<Response> actual
                = 지하철_노선_구간_추가("/lines/{id}/lineStations", id, 구간_추가(역_생성(홍대역), 역_생성(신촌역), 5));

        응답_확인_BAD_REQUEST(actual);
    }

    private ExtractableResponse<Response> 지하철_노선_구간_추가(String path, Long id, LineStationRequest request) {
        return post(path, request, id);
    }

    private void 상행_혹은_하행_추가_검증(ExtractableResponse<Response> response, StationResponse excepted) {
        List<LineResponse> responses = response.jsonPath().getList(".", LineResponse.class);

        List<StationResponse> actual = new ArrayList<>();
        responses.stream()
                .map(LineResponse::getStations)
                .collect(Collectors.toList())
                .forEach(f -> actual.addAll(f.stream().map(StationResponse::of).collect(Collectors.toList())));

        assertThat(actual).contains(excepted);
    }


    private void 순서_검증(Long lineId
            , StationResponse excepted1, StationResponse excepted2, StationResponse excepted3) {
        LineResponse response = 지하철_노선_단건_조회("/lines/{id}", lineId).as(LineResponse.class);

        List<StationResponse> actual = response.getStations()
                .stream().map(StationResponse::of)
                .collect(Collectors.toList());

        assertThat(actual).containsExactly(excepted1, excepted2, excepted3);
    }


}
