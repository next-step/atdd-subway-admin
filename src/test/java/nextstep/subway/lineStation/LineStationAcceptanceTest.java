package nextstep.subway.lineStation;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineStationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.line.LineAcceptanceTestMethod.*;
import static nextstep.subway.utils.Fixture.*;

@DisplayName("지하철 구간 관련 기능")
public class LineStationAcceptanceTest extends AcceptanceTest {


    @DisplayName("지하철 노선의 구간을 추가한다.")
    @Test
    void addSection() {
        StationResponse 기점역 = 역_생성(강남역);
        ExtractableResponse<Response> response = 신규_지하철_노선_생성_요청("/lines", 신분당선, 10, 기점역, 역_생성(광교역));
        Long id = response.as(LineResponse.class).getId();

        ExtractableResponse<Response> actual
                = 지하철_노선_구간_추가("/lines/{id}/lineStations", id, 구간_추가(기점역, 역_생성(판교역), 5));

        응답_확인_OK(actual);
    }

    @DisplayName("지하철 노선의 구간을 추가한다.(동일한 건에 대한 오류 발생)")
    @Test
    void addSection_동일역_추가() {
        StationResponse 강남 = 역_생성(강남역);
        StationResponse 광교 = 역_생성(광교역);
        ExtractableResponse<Response> response = 신규_지하철_노선_생성_요청("/lines", 신분당선, 10, 강남, 광교);
        Long id = response.as(LineResponse.class).getId();

        ExtractableResponse<Response> actual
                = 지하철_노선_구간_추가("/lines/{id}/lineStations", id, 구간_추가(강남, 광교, 5));

        응답_확인_BAD_REQUEST(actual);
    }

    @DisplayName("지하철 노선의 구간을 추가한다.(연결 못하는 오류 발생)")
    @Test
    void addSection_연결_실패() {
        ExtractableResponse<Response> response = 신규_지하철_노선_생성_요청("/lines", 신분당선, 10, 역_생성(강남역), 역_생성(광교역));
        Long id = response.as(LineResponse.class).getId();

        ExtractableResponse<Response> actual
                = 지하철_노선_구간_추가("/lines/{id}/lineStations", id, 구간_추가(역_생성(홍대역), 역_생성(신촌역), 5));

        응답_확인_BAD_REQUEST(actual);
    }

    private ExtractableResponse<Response> 지하철_노선_구간_추가(String path, Long id, LineStationRequest 판교역) {
        return post(path, 판교역, id);
    }

}
