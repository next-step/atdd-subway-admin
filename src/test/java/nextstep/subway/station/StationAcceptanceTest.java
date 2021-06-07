package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.utils.RestAssuredTemplate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static nextstep.subway.PageController.URIMapping.STATION;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    private static RestAssuredTemplate restAssuredTemplate;
    private static Map<String, String> param1;
    private static Map<String, String> param2;

    @BeforeAll
    public static void setup() {
        restAssuredTemplate = new RestAssuredTemplate(STATION);
        param1 = new HashMap<String, String>(){
            {
                put("name", "강남역");
            }
        };
        param2 = new HashMap<String, String>(){
            {
                put("name", "역삼역");
            }
        };
    }

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> response = requestCreateStation(param1);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        requestCreateStation(param1);

        // when
        ExtractableResponse<Response> response = requestCreateStation(param1);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        ExtractableResponse<Response> createResponse1 = requestCreateStation(param1);
        ExtractableResponse<Response> createResponse2 = requestCreateStation(param2);

        // when
        ExtractableResponse<Response> response = requestShowStations();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
                .map(restAssuredTemplate::getLocationId)
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
        ExtractableResponse<Response> createResponse = requestCreateStation(param1);

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = requestDeleteStation(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> requestShowStations() {
        return restAssuredTemplate.get();
    }

    private ExtractableResponse<Response> requestCreateStation(Map<String, String> param) {
        return restAssuredTemplate.post(param);
    }

    private ExtractableResponse<Response> requestDeleteStation(String uri) {
        return restAssuredTemplate.delete(uri);
    }
}
