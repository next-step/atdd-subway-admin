package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.testFactory.AcceptanceTestFactory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    private static final String stationServicePath="/stations";

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given
        Map<String, String> params = AcceptanceTestFactory.getNameContent("강남역");

        // when
        ExtractableResponse<Response> response =  AcceptanceTestFactory.post(params,stationServicePath);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        Map<String, String> params = AcceptanceTestFactory.getNameContent("강남역");
        AcceptanceTestFactory.post(params,stationServicePath);

        // when
        ExtractableResponse<Response> response = AcceptanceTestFactory.post(params,stationServicePath);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        Map<String, String> params1 = AcceptanceTestFactory.getNameContent("강남역");
        ExtractableResponse<Response> createResponse1 = AcceptanceTestFactory.post(params1,stationServicePath);
        Map<String, String> params2 = AcceptanceTestFactory.getNameContent("역삼역");
        ExtractableResponse<Response> createResponse2 = AcceptanceTestFactory.post(params2,stationServicePath);

        // when
        ExtractableResponse<Response> response = AcceptanceTestFactory.get(stationServicePath);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
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
        Map<String, String> params = new HashMap<>();
        params.put("name", "강남역");
        ExtractableResponse<Response> createResponse = AcceptanceTestFactory.post(params,stationServicePath);

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response =  AcceptanceTestFactory.delete(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
