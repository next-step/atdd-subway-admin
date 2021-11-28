package nextstep.subway.line;

import static nextstep.subway.fixtrue.Param.*;
import static nextstep.subway.fixtrue.TestFactory.*;
import static nextstep.subway.line.LineAcceptanceTest.*;
import static nextstep.subway.station.StationAcceptanceTest.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.fixtrue.Param;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;

public class SectionAcceptanceTest extends AcceptanceTest {

    @Test
    void 노선에_새로운_상행_구간을_등록한다() {
        // given
        StationResponse stationGangnam = 지하철역_등록되어_있음(강남역);
        StationResponse stationSinchon = 지하철역_등록되어_있음(신촌역);
        LineResponse lineResponse = 지하철_노선_등록되어_있음(
            new LineRequest(LINE_ONE, LINE_ONE_COLOR_RED, stationGangnam.getId(), stationSinchon.getId(), 10));

        StationResponse stationYoungSan = 지하철역_등록되어_있음(용산역);
        StationResponse stationSeoul = 지하철역_등록되어_있음(서울역);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_새로운_구간_등록_요청(lineResponse, stationYoungSan, stationGangnam,5);
        지하철_노선에_새로운_구간_등록_요청(lineResponse, stationSeoul, stationSinchon,5);

        ExtractableResponse<Response> 지하철_노선_조회_요청 = 지하철_노선_조회_요청(lineResponse.getId());

        // then 
        지하철_노선에_지하철역_등록됨(response);
    }

    @Test
    void 노선에_새로운_하행_구간을_등록한다() {
        // given
        StationResponse stationGangnam = 지하철역_등록되어_있음(강남역);
        StationResponse stationSinchon = 지하철역_등록되어_있음(신촌역);
        LineResponse lineResponse = 지하철_노선_등록되어_있음(
            new LineRequest(LINE_ONE, LINE_ONE_COLOR_RED, stationGangnam.getId(), stationSinchon.getId(), 10));

        StationResponse stationYounSan = 지하철역_등록되어_있음(용산역);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_새로운_구간_등록_요청(lineResponse, stationSinchon, stationYounSan,5);

        ExtractableResponse<Response> 지하철_노선_조회_요청 = 지하철_노선_조회_요청(lineResponse.getId());

        // then 
        지하철_노선에_지하철역_등록됨(response);
    }

    @Test
    void 노선에_새로운_구간_등록시_상행역_하행역_둘중_하나라도_존재하지_않는경우_예외() {
        StationResponse stationGangnam = 지하철역_등록되어_있음(강남역);
        StationResponse stationSinchon = 지하철역_등록되어_있음(신촌역);
        LineResponse lineResponse = 지하철_노선_등록되어_있음(
            new LineRequest(LINE_ONE, LINE_ONE_COLOR_RED, stationGangnam.getId(), stationSinchon.getId(), 10));

        StationResponse stationSeoul = 지하철역_등록되어_있음(서울역);
        StationResponse stationYounSan = 지하철역_등록되어_있음(용산역);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_새로운_구간_등록_요청(lineResponse, stationSeoul, stationYounSan,5);

        // then
        지하철_노선에_지하철역_실패(response);
    }

    @Test
    void 노선에_새로운_구간_등록시_기존_거리보다_큰_경우_예외() {
        StationResponse stationGangnam = 지하철역_등록되어_있음(강남역);
        StationResponse stationSinchon = 지하철역_등록되어_있음(신촌역);
        LineResponse lineResponse = 지하철_노선_등록되어_있음(
            new LineRequest(LINE_ONE, LINE_ONE_COLOR_RED, stationGangnam.getId(), stationSinchon.getId(), 10));

        StationResponse stationSeoul = 지하철역_등록되어_있음(강남역);
        StationResponse stationYounSan = 지하철역_등록되어_있음(용산역);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_새로운_구간_등록_요청(lineResponse, stationSeoul, stationYounSan,11);

        // then
        지하철_노선에_지하철역_실패(response);
    }

    @Test
    void 노선에_새로운_구간_등록시_기존에_존재하는_구간이면_예외() {
        StationResponse stationGangnam = 지하철역_등록되어_있음(강남역);
        StationResponse stationSinchon = 지하철역_등록되어_있음(신촌역);
        LineResponse lineResponse = 지하철_노선_등록되어_있음(
            new LineRequest(LINE_ONE, LINE_ONE_COLOR_RED, stationGangnam.getId(), stationSinchon.getId(), 10));
        // when
        ExtractableResponse<Response> response = 지하철_노선에_새로운_구간_등록_요청(lineResponse, stationGangnam, stationSinchon,5);

        // then
        지하철_노선에_지하철역_실패(response);
    }

    public ExtractableResponse<Response> 지하철_노선에_새로운_구간_등록_요청(
        LineResponse lineResponse, StationResponse stationGangnam,
        StationResponse stationSinchon, int distance) {
        Param param = createParam()
            .addParam("lineId", lineResponse.getId());
        return post("/{lineId}/sections", param, new SectionRequest(stationGangnam.getId(), stationSinchon.getId(), distance));
    }

    public void 지하철_노선에_지하철역_등록됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private void 지하철_노선에_지하철역_실패(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
