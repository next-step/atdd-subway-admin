package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.utils.RequestTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> response = createRequest(new StationRequest("강남역"));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        StationRequest stationRequest = new StationRequest("강남역");
        createRequest(stationRequest);

        // when
        ExtractableResponse<Response> response = createRequest(stationRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        StationResponse createResponse1 = createRequest(new StationRequest("강남역"))
                .as(StationResponse.class);
        StationResponse createResponse2 = createRequest(new StationRequest("역삼역"))
                .as(StationResponse.class);

        // when
        ExtractableResponse<Response> response = selectAllRequest();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<StationResponse> result = response.jsonPath().getList(".", StationResponse.class);
        assertThat(result).contains(createResponse1, createResponse2);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        StationResponse createdResponse = createRequest(new StationRequest("강남역"))
                .as(StationResponse.class);

        // when
        ExtractableResponse<Response> response = deleteRequest(createdResponse.getId());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> createRequest(StationRequest stationRequest) {
        final String url = "/stations";
        return RequestTest.doPost(url, stationRequest);
    }

    private ExtractableResponse<Response> selectAllRequest() {
        final String url ="/stations";
        return RequestTest.doGet(url);
    }

    private ExtractableResponse<Response> deleteRequest(Long stationId) {
        final String url = "/stations/" + stationId;
        return RequestTest.doDelete(url);
    }
}
