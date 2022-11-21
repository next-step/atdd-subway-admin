package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.LineAcceptanceTestUtil;
import nextstep.subway.station.StationAcceptanceTestUtil;
import nextstep.subway.utils.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import static nextstep.subway.section.SectionAcceptanceTestUtil.createSection;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SectionAcceptanceTest {
    @LocalServerPort
    int port;
    @Autowired
    private DatabaseCleanup databaseCleanup;
    private String upStationId;
    private String downStationId;
    private String newStationId;
    private String lineId;

    @BeforeEach
    void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        databaseCleanup.execute();
        upStationId = StationAcceptanceTestUtil.createStation("판교역")
                .jsonPath().getString("id");
        downStationId = StationAcceptanceTestUtil.createStation("경기광주역")
                .jsonPath().getString("id");
        newStationId = StationAcceptanceTestUtil.createStation("이매역")
                .jsonPath().getString("id");

        lineId = LineAcceptanceTestUtil.createLine("신분당선", "bg-blue-600", upStationId, downStationId, "10")
                .jsonPath().getString("id");
    }

    /**
     * Given : 새로운 구간을 생성하고
     * When : 새로운 구간의 길이가 기존 길이보다 작으면
     * Then : 구간이 등록된다.
     */
    @DisplayName("역 사이에 새로운 역을 등록할 경우, 새로운 길이를 뺀 나머지를 새롭게 추가된 역과의 길이로 설정")
    @Test
    void 역사이에_새로운역_등록_성공() {
        // given / when
        ExtractableResponse<Response> response = createSection(upStationId, newStationId, "4", lineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
