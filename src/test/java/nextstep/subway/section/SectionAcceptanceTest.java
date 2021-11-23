package nextstep.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.utils.ApiUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.line.LineAcceptanceTest.지하철_노선_등록;
import static nextstep.subway.station.StationAcceptanceTest.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    @DisplayName("구간 추가하기")
    @Test
    void addSectionTest() {

        ExtractableResponse<Response> lineResponse = 지하철_노선_등록("신분당선", "bg-red-600", 10);
        long lineId = lineResponse.body().jsonPath().getLong("id");

        Map<String, String> params = new HashMap<>();
        params.put("downStationId", "4");
        params.put("upStationId", "2");
        params.put("distance", "10");

        ExtractableResponse<Response> response = ApiUtils.post(String.format("/lines/%s/sections", lineId), params);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }
}
