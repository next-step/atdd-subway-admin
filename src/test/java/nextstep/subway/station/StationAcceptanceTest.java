package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.utils.RestAssuredTemplate;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import static nextstep.subway.PageController.URIMapping.STATION;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    private static RestAssuredTemplate restAssuredTemplate = new RestAssuredTemplate(STATION);
    private static StationRequest param1;
    private static StationRequest param2;

    @BeforeEach
    public void register() {
        setTestParameter();
    }

    @AfterEach
    public void rollback() {
        setTestParameter();
    }

    private static void setTestParameter() {
        param1 = StationRequest.builder()
                .name("강남")
                .build();

        param2 = StationRequest.builder()
                .name("역삼역")
                .build();
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

    public static ExtractableResponse<Response> requestShowStations() {
        return restAssuredTemplate.get();
    }

    public static ExtractableResponse<Response> requestCreateStation(StationRequest param) {
        return restAssuredTemplate.post(param.toMap());
    }

    public static ExtractableResponse<Response> requestDeleteStation(String uri) {
        return restAssuredTemplate.delete(uri);
    }
}
