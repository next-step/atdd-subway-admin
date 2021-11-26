package nextstep.subway.line;

import static nextstep.subway.fixtrue.TestFactory.*;
import static nextstep.subway.line.LineAcceptanceTest.*;
import static nextstep.subway.station.StationAcceptanceTest.*;
import static org.assertj.core.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.dto.StationResponse;

public class SectionAcceptanceTest {

    StationResponse stationGangnam;
    StationResponse stationSinchon;
    LineRequest lineRequest;

    @BeforeEach
    void setUp() {
        stationGangnam = 지하철역_등록되어_있음(강남역);
        stationSinchon = 지하철역_등록되어_있음(신촌역);
        lineRequest = new LineRequest(LINE_ONE, LINE_ONE_COLOR_RED, stationGangnam.getId(), stationSinchon.getId(), 10
        );
    }

    @Test
    void 노선에_새로운_구간을_등록한다() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_새로운_구간_등록_요청();

        // then 
        지하철_노선에_지하철역_등록됨(response);
    }

    @Test
    void 노선에_새로운_구간_등록시_상행역_하행역_둘중_하나라도_존재하지_않는경우_예외() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_새로운_구간_등록_요청();

        // then
        지하철_노선에_지하철역_실패(response);
    }

    @Test
    void 노선에_새로운_구간_등록시_기존_거리보다_큰_경우_예외() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_새로운_구간_등록_요청();

        // then
        지하철_노선에_지하철역_실패(response);
    }

    @Test
    void 노선에_새로운_구간_등록시_기존에_존재하는_구간이면_예외() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_새로운_구간_등록_요청();

        // then
        지하철_노선에_지하철역_실패(response);
    }



    public ExtractableResponse<Response> 지하철_노선에_새로운_구간_등록_요청() {
        return post("/{lineId}/sections", new SectionRequest(stationGangnam.getId(), stationSinchon.getId(), 5));
    }

    public void 지하철_노선에_지하철역_등록됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private void 지하철_노선에_지하철역_실패(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
