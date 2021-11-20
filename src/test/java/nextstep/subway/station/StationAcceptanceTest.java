package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static nextstep.subway.utils.AcceptanceTestUtil.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "강남역");

        // when
        ExtractableResponse<Response> response = 지하철_역_생성(params);

        // then
        지하철_역_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "강남역");
        지하철_역_생성(params);

        // when
        ExtractableResponse<Response> response = 지하철_역_생성(params);

        // then
        지하철_역_생성_실패(response, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        Map<String, String> params1 = new HashMap<>();
        params1.put("name", "강남역");
        ExtractableResponse<Response> createResponse1 = 지하철_역_생성(params1);

        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "역삼역");
        ExtractableResponse<Response> createResponse2 = 지하철_역_생성(params2);

        // when
        ExtractableResponse<Response> response = 지하철_역_조회();

        // then
        지하철_역_두개가_조회됨(createResponse1, createResponse2, response);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "강남역");
        ExtractableResponse<Response> createResponse = 지하철_역_생성(params);

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = 지하철_역_삭제(uri);

        // then
        지하철_역_삭제됨(response);
    }

    private ExtractableResponse<Response> 지하철_역_생성(Map<String, String> body) {
        return post("/stations", body);
    }

    private ExtractableResponse<Response> 지하철_역_조회() {
        return get("/stations");
    }

    private ExtractableResponse<Response> 지하철_역_삭제(String uri) {
        return delete(uri);
    }

    private void 지하철_역_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    private void 지하철_역_생성_실패(ExtractableResponse<Response> response, HttpStatus badRequest) {
        assertThat(response.statusCode()).isEqualTo(badRequest.value());
    }

    private void 지하철_역_두개가_조회됨(ExtractableResponse<Response> createResponse1, ExtractableResponse<Response> createResponse2,
        ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
            .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
            .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", StationResponse.class).stream()
            .map(it -> it.getId())
            .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    private void 지하철_역_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
