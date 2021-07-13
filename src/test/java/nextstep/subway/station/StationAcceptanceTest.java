package nextstep.subway.station;

import static nextstep.subway.AcceptanceApi.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    StationRequest 강남역;
    StationRequest 역삼역;

    @BeforeEach
    void setup() {
        강남역 = new StationRequest("강남역");
        역삼역 = new StationRequest("역삼역");
    }

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> response = 지하철_역_생성_요청(강남역);

        // then
        지하철역이_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        지하철_역_생성_요청(강남역);

        // when
        ExtractableResponse<Response> response = 지하철_역_생성_요청(강남역);

        // then
        지하철역이_생성이_실패함(response);
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        ExtractableResponse<Response> 강남역_생성_응답 = 지하철_역_생성_요청(강남역);
        ExtractableResponse<Response> 역삼역_생성_응답 = 지하철_역_생성_요청(역삼역);

        // when
        ExtractableResponse<Response> response = 지하철_역_조회_요청();

        // then
        List<Long> expectedLineIds = Arrays.asList(강남역_생성_응답, 역삼역_생성_응답).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        지하철역이_조회됨(response, expectedLineIds);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        StationRequest 강남역 = new StationRequest("강남역");
        ExtractableResponse<Response> 강남역_생성_응답 = 지하철_역_생성_요청(강남역);

        // when
        String uri = 강남역_생성_응답.header("Location");
        ExtractableResponse<Response> response = 지하철_역_제거_요청(uri);

        // then
        지하철역이_제거됨(response);
    }

    public static void 지하철역이_제거됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    public static void 지하철역이_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    public static void 지하철역이_생성이_실패함(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static void 지하철역이_조회됨(ExtractableResponse<Response> response, List<Long> expectedLineIds) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> resultLineIds = response.jsonPath().getList(".", StationResponse.class).stream()
            .map(it -> it.getId())
            .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }
}
