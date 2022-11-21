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

import java.util.List;

import static nextstep.subway.section.SectionAcceptanceTestUtil.createSection;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SectionAcceptanceTest {
    @LocalServerPort
    int port;
    @Autowired
    private DatabaseCleanup databaseCleanup;
    private String 판교역;
    private String 경기광주역;
    private String 이매역;
    private String 경강선;

    @BeforeEach
    void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        databaseCleanup.execute();
        판교역 = StationAcceptanceTestUtil.createStation("판교역")
                .jsonPath().getString("id");
        경기광주역 = StationAcceptanceTestUtil.createStation("경기광주역")
                .jsonPath().getString("id");
        이매역 = StationAcceptanceTestUtil.createStation("이매역")
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
        // given
        경강선 = LineAcceptanceTestUtil.createLine("경강선", "bg-blue-600", 판교역, 경기광주역, "10")
                .jsonPath().getString("id");

        // when
        ExtractableResponse<Response> response = createSection(판교역, 이매역, "4", 경강선);
        List<String> stations = LineAcceptanceTestUtil.getLines(1L).jsonPath().getList("stations.name", String.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(stations).hasSize(3);
    }

    /**
     * Given : 새로운 역을 상행 종점으로 하는 구간을 생성한다
     * When : 새로운 구간을 등록하면,
     * Then : 구간이 등록된다.
     */
    @DisplayName("새로운 역을 상행 종점으로 생성하고, 등록한다")
    @Test
    void 새로운_상행_등록_성공() {
        // given
        경강선 = LineAcceptanceTestUtil.createLine("경강선", "bg-blue-600", 이매역, 경기광주역, "10")
                .jsonPath().getString("id");

        // when
        ExtractableResponse<Response> response = createSection(판교역, 이매역, "4", 경강선);
        List<String> stations = LineAcceptanceTestUtil.getLines(1L).jsonPath().getList("stations.name", String.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(stations).hasSize(3);
    }

    /**
     * Given : 새로운 역을 하행 종점으로 하는 구간을 생성한다
     * When : 새로운 구간을 등록하면,
     * Then : 구간이 등록된다.
     */
    @DisplayName("새로운 역을 하행 종점으로 생성하고, 등록한다")
    @Test
    void 새로운_하행_등록_성공() {
        // given
        경강선 = LineAcceptanceTestUtil.createLine("경강선", "bg-blue-600", 판교역, 이매역, "10")
                .jsonPath().getString("id");

        // when
        ExtractableResponse<Response> response = createSection(이매역, 경기광주역, "4", 경강선);
        List<String> stations = LineAcceptanceTestUtil.getLines(1L).jsonPath().getList("stations.name", String.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(stations).hasSize(3);
    }

    /**
     * When : 새로운 역의 구간 길이가 기존 구간 길이보다 길면
     * Then : 구간 등록이 실패한다.
     */
    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록할 수 없음")
    @Test
    void 길이가_더_긴경우_예외() {
        // when
        경강선 = LineAcceptanceTestUtil.createLine("경강선", "bg-blue-600", 판교역, 경기광주역, "10")
                .jsonPath().getString("id");
        ExtractableResponse<Response> response = createSection(이매역, 경기광주역, "10", 경강선);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
