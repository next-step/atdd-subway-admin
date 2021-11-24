package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.fixtrue.TestFactory;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.primitives.Longs.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    @Test
    void 지하철역을_생성한다() {
        // given
        StationRequest stationRequest = new StationRequest("강남역");

        // when
        ExtractableResponse<Response> response = 지하철역_생성_요청(stationRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @Test
    void 기존에_존재하는_지하철역_이름으로_지하철역을_생성한다() {
        // given
        StationRequest stationRequest = new StationRequest("강남역");

        지하철역_등록되어_있음(stationRequest);

        // when
        ExtractableResponse<Response> response = 지하철역_생성_요청(stationRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void 지하철역을_조회한다() {
        /// given
        StationRequest stationRequest1 = new StationRequest("강남역");
        StationResponse createResponse1 = 지하철역_등록되어_있음(stationRequest1);

        StationRequest stationRequest2 = new StationRequest("역삼역");
        StationResponse createResponse2 = 지하철역_등록되어_있음(stationRequest2);

        // when
        ExtractableResponse<Response> response = 지하철역_조회_요청();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<Long> resultLineIds = response.jsonPath().getList(".", StationResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(asList(createResponse1.getId(),createResponse2.getId()));
    }

    @Test
    void 지하철역을_제거한다() {
        // given
        StationRequest stationRequest = new StationRequest("강남역");
        StationResponse createResponse = 지하철역_등록되어_있음(stationRequest);

        // when
        ExtractableResponse<Response> response = 지하철역_삭제_요청(createResponse.getId());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }



    public static ExtractableResponse<Response> 지하철역_생성_요청(StationRequest params) {
        return TestFactory.post("/stations", params);
    }

    public static  StationResponse 지하철역_등록되어_있음(StationRequest params) {
        ExtractableResponse<Response> response = TestFactory.post("/stations", params);
        return TestFactory.toResponseData(response, StationResponse.class);
    }

    public static ExtractableResponse<Response> 지하철역_조회_요청() {
        return TestFactory.get("/stations");
    }

    public static ExtractableResponse<Response> 지하철역_삭제_요청(Long id) {
        return TestFactory.delete("/stations/{id}", id);
    }
}
