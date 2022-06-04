package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.BasicAcceptance;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.line.LineAcceptanceTest.지하철_노선_생성;
import static nextstep.subway.line.LineAcceptanceTest.지하철_노선_생성됨;
import static nextstep.subway.station.StationAcceptanceTest.역_객체로_변환;
import static nextstep.subway.station.StationAcceptanceTest.지하철역_생성;

public class SectionAcceptanceTest extends BasicAcceptance {

    private Station 강남역;
    private Station 양재역;

    @Override
    protected void beforeEachInit() {
        강남역 = 역_객체로_변환(지하철역_생성("강남역"));
        양재역 = 역_객체로_변환(지하철역_생성(("양재역")));
    }

    @DisplayName("노선에 구간을 등록한다.")
    @Test
    void addSection() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        final LineRequest 신분당선 = new LineRequest("신분당선", "bg-red-600", 강남역.getId(), 양재역.getId(), 10L);
        ExtractableResponse<Response> 생성된_신분당선 = 지하철_노선_생성(신분당선);

        // then
        지하철_노선_생성됨(생성된_신분당선);
    }
}
