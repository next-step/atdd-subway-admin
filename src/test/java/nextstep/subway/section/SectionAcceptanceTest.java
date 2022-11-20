package nextstep.subway.section;
import static nextstep.subway.station.StationTestFixture.requestCreateStation;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.line.LineTestFixture;
import nextstep.subway.station.StationTestFixture;
import nextstep.subway.utils.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

@DisplayName("노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SectionAcceptanceTest {
    @LocalServerPort
    int port;

    @Autowired
    DatabaseCleanup databaseCleanup;

    StationResponse 강남역;
    StationResponse 광교역;
    LineResponse 신분당선;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        databaseCleanup.execute();

        // given
        강남역 = requestCreateStation("강남역").as(StationResponse.class);
        광교역 = StationTestFixture.requestCreateStation("광교역").as(StationResponse.class);
        신분당선 = LineTestFixture.requestCreateLine("신분당선", "bg-red-600", 강남역.getId(), 광교역.getId(), 10 ).as(LineResponse.class);
    }

    /**
     * When 지하철_노선에_지하철역_등록_요청
     * Then 지하철_노선에_지하철역_등록됨
     */
    @DisplayName("노선 구간을 등록한다.")
    @Test
    void addSection() {
        // when
        ExtractableResponse<Response> response = SectionTestFixture.requestAddSection(2L, 4L, 10 );
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

    }
}
