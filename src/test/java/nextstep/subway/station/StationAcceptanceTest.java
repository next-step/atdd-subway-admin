package nextstep.subway.station;

import static nextstep.subway.AcceptanceApi.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given
        StationRequest 강남역 = new StationRequest("강남역");

        // when
        ExtractableResponse<Response> response = 지하철_역_생성_요청(강남역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        StationRequest 강남역 = new StationRequest("강남역");
        지하철_역_생성_요청(강남역);

        // when
        ExtractableResponse<Response> response = 지하철_역_생성_요청(강남역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        StationRequest 강남역 = new StationRequest("강남역");
        ExtractableResponse<Response> 강남역_생성_응답 = 지하철_역_생성_요청(강남역);

        StationRequest 역삼역 = new StationRequest("역삼역");
        ExtractableResponse<Response> 역삼역_생성_응답 = 지하철_역_생성_요청(역삼역);

        // when
        ExtractableResponse<Response> response = 지하철_역_조회_요청();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Arrays.asList(강남역_생성_응답, 역삼역_생성_응답).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", StationResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
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
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
