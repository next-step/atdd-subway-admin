package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static nextstep.subway.station.StationAcceptanceTestFactory.*;
import static nextstep.subway.utils.RestAssuredApiTest.post;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given
        Map<String, String> params = getStationCreateParam("강남역");
        // when
        ExtractableResponse<Response> response = post("/stations", params);
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }


    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        지하철_역_등록되어_있음("강남역");
        // when
        final ExtractableResponse<Response> response = 지하철_역_등록되어_있음("강남역");
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        final StationResponse 강남역 = 지하철_역_등록되어_있음("강남역").as(StationResponse.class);
        final StationResponse 역삼역 = 지하철_역_등록되어_있음("역삼역").as(StationResponse.class);
        // when
        final ExtractableResponse<Response> response = 지하철_역_목록_조회_요청();
        // then
        final List<StationResponse> stationResponses = response.jsonPath().getList(".", StationResponse.class);
        assertThat(stationResponses).containsAll(Arrays.asList(강남역, 역삼역));
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        StationResponse 강남역 = 지하철_역_등록되어_있음("강남역").as(StationResponse.class);
        // when
        ExtractableResponse<Response> response = 지하철_역_제거_요청(강남역.getId());
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
