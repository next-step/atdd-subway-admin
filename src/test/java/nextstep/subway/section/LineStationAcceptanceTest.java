package nextstep.subway.section;

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


    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void addSection() {
        StationResponse 기점역 = 역_생성(강남역);
        ExtractableResponse<Response> response = 신규_지하철_노선_생성_요청("/lines", 신분당선, 10, 기점역, 역_생성(광교역));
        Long id = response.as(LineResponse.class).getId();

        ExtractableResponse<Response> actual
                = 지하철_노선_구간_추가("/lines/{id}/lineStations", id, 구간_추가(기점역, 역_생성(판교역), 5));

        응답_확인_OK(actual);
    }

    private ExtractableResponse<Response> 지하철_노선_구간_추가(String path, Long id, LineStationRequest 판교역) {
        return post(path, 판교역, id);
    }

}
