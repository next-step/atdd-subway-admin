package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineTestFixture;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationTestFixture;
import nextstep.subway.station.dto.StationResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

@DisplayName("구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {
    private StationResponse stationUp;
    private StationResponse stationDown;
    private StationResponse stationAdd;
    private LineResponse line;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        stationUp = StationTestFixture.지하철역_등록("강남역").as(StationResponse.class);
        stationDown = StationTestFixture.지하철역_등록("광교역").as(StationResponse.class);
        stationAdd = StationTestFixture.지하철역_등록("교대역").as(StationResponse.class);
        line = LineTestFixture.지하철_노선_등록("1호선", "blue", stationUp.getId(), stationDown.getId(), 10).as(LineResponse.class);
    }

    @DisplayName("노선에 구간을 등록한다.")
    @Test
    void addSection() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", stationUp.getId().toString());
        params.put("downStationId", stationAdd.getId().toString());
        params.put("distance", "5");

        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("id", line.getId())
                .when().post("/lines/{id}/sections")
                .then().log().all()
                .extract();

        // then
        // 지하철_노선에_지하철역_등록됨
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
