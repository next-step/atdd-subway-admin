package nextstep.subway.lineStation;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.SectionRequest;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LineStationAcceptanceFixture {

    private static final String LINE_URL = "/lines/";

    private static final String SECTIONS_URL = "/sections";

    private static final String STATION_NAME = "stations.name";

    public static ExtractableResponse<Response> 지하철_구간을_추가한다(Long lineId, Long upStationId, Long downStationId, int distance) {
        return RestAssured.given().log().all()
                .body(new SectionRequest(upStationId, downStationId, distance))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(LINE_URL + lineId+ SECTIONS_URL)
                .then().log().all()
                .extract();
    }

    public static void 상태코드를_체크한다(int statusCode, int expect) {
        assertThat(statusCode).isEqualTo(expect);
    }

    public static void 지하철_구간이_추가되었는지_체크한다(ExtractableResponse<Response> response, String... newStationName) {
        List<String> name = response.jsonPath().getList(STATION_NAME, String.class);
        assertThat(name).containsAll(Arrays.asList(newStationName));
    }
}
