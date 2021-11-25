package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static nextstep.subway.station.TestStationAcceptanceFactory.지하철_역_ID_추출;
import static nextstep.subway.station.TestStationAcceptanceFactory.지하철_역_목록_ID_추출;
import static nextstep.subway.station.TestStationAcceptanceFactory.지하철_역_목록_조회_요청;
import static nextstep.subway.station.TestStationAcceptanceFactory.지하철_역_삭제_요청;
import static nextstep.subway.station.TestStationAcceptanceFactory.지하철_역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    @Test
    void 지하철_역을_생성한다() {
        // when
        ExtractableResponse<Response> response = 지하철_역_생성_요청("강남역");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    void 기존에_존재하는_지하철역_이름으로_지하철역을_생성한다() {
        // given
        지하철_역_생성_요청("강남역");

        // when
        ExtractableResponse<Response> response = 지하철_역_생성_요청("강남역");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void 지하철역_목록을_조회한다() {
        /// given
        ExtractableResponse<Response> createResponse1 = 지하철_역_생성_요청("강남역");
        ExtractableResponse<Response> createResponse2 = 지하철_역_생성_요청("역삼역");

        // when
        ExtractableResponse<Response> response = 지하철_역_목록_조회_요청();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = 지하철_역_ID_추출(createResponse1, createResponse2);
        List<Long> resultLineIds = 지하철_역_목록_ID_추출(response);
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @Test
    void 지하철역을_제거한다() {
        // given
        ExtractableResponse<Response> createResponse = 지하철_역_생성_요청("강남역");
        Long createStationId = createResponse.jsonPath().getObject(".", Station.class).getId();

        // when
        ExtractableResponse<Response> response = 지하철_역_삭제_요청(createStationId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
