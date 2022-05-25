package nextstep.subway.station;

import static nextstep.subway.AcceptanceTest.delete;
import static nextstep.subway.AcceptanceTest.get;
import static nextstep.subway.AcceptanceTest.post;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.station.dto.StationRequest;
import org.springframework.http.HttpStatus;

public class StationAcceptanceTestMethods {

    private static final String STATION_URL_PATH = "/stations";
    private static final String LOCATION_HEADER_KEY = "Location";
    private static final String SLASH = "/";

    private StationAcceptanceTestMethods() {}

    public static ExtractableResponse<Response> 지하철역_생성(StationRequest stationRequest) {
        return post(STATION_URL_PATH, stationRequest);
    }

    public static ExtractableResponse<Response> 지하철역_목록_조회() {
        return get(STATION_URL_PATH);
    }

    public static ExtractableResponse<Response> 지하철역_제거(ExtractableResponse response) {
        String location = response.header(LOCATION_HEADER_KEY);
        return delete(location);
    }

    public static void 지하철역_생성됨(ExtractableResponse response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header(LOCATION_HEADER_KEY)).isNotBlank();
    }

    public static void 지하철역_생성_실패(ExtractableResponse response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static void 지하철역_목록_응답(ExtractableResponse response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철역_목록에_포함(ExtractableResponse response, List<ExtractableResponse> targets) {
        List<Long> targetIds = targets.stream()
            .map(target -> Long.parseLong(target.header(LOCATION_HEADER_KEY).split(SLASH)[2]))
            .collect(Collectors.toList());

        List<Long> resultIds = response.jsonPath().getList("id", Long.class);
        assertThat(resultIds).containsAll(targetIds);
    }

    public static void 지하철역_제거됨(ExtractableResponse response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
