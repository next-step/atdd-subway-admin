package section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    private static StationResponse 서울역;
    private static StationResponse 대전역;
    private static LineResponse 경부고속선;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        서울역 = StationAcceptanceTest.지하철역_등록되어_있음("서울역");
        대전역 = StationAcceptanceTest.지하철역_등록되어_있음("대전역");

        경부고속선 = LineAcceptanceTest.지하철_노선_등록되어_있음("경부고속선","BLUE",서울역.getId(), 대전역.getId(), 8);
    }

    @DisplayName("구간을 추가한다.")
    @Test
    void addSection() {
        // given
        StationResponse 광명역 = StationAcceptanceTest.지하철역_등록되어_있음("광명역");

        Map<String, String> params = new HashMap<>();
        params.put("upStationId", 서울역.getId().toString());
        params.put("downStationId", 광명역.getId().toString());
        params.put("distance", Integer.toString(5));

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/{lineId}/sections", 경부고속선.getId())
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // 값 검증
        LineResponse lineResponse = response.jsonPath().getObject(".", LineResponse.class);
        assertThat(lineResponse.getStations()).hasSize(3);
    }
}
