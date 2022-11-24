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
import static nextstep.subway.section.SectionAcceptanceTestUtil.removeSection;
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
    private String 부발역;
    private String 여주역;
    private String 경강선;

    @BeforeEach
    void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        databaseCleanup.execute();
        판교역 = StationAcceptanceTestUtil.createStation("판교역")
                .jsonPath().getString("id");
        이매역 = StationAcceptanceTestUtil.createStation("이매역")
                .jsonPath().getString("id");
        경기광주역 = StationAcceptanceTestUtil.createStation("경기광주역")
                .jsonPath().getString("id");
        부발역 = StationAcceptanceTestUtil.createStation("부발역")
                .jsonPath().getString("id");
        여주역 = StationAcceptanceTestUtil.createStation("여주역")
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

    /**
     * When : 새로운 구간의 상하행이 이미 등록되어 있는 구간이라면
     * Then : 구간 등록이 실패한다.
     */
    @DisplayName("새로운 구간의 상하행이 이미등록된 경우 예외")
    @Test
    void 상하행_기등록_예외() {
        // when
        경강선 = LineAcceptanceTestUtil.createLine("경강선", "bg-blue-600", 판교역, 경기광주역, "10")
                .jsonPath().getString("id");
        ExtractableResponse<Response> response = createSection(판교역, 경기광주역, "5", 경강선);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given : 구간에 3개의 역이 등록되어 있다.
     * When : 새로운 구간의 상하행 중 하나라도 기존 구간과 일치하지 않으면
     * Then : 구간 등록이 실패한다.
     */
    @DisplayName("새로운 구간의 상하행중 하나라도 기존 구간에 없으면 예외")
    @Test
    void 상하행_미등록_예외() {
        // given
        경강선 = LineAcceptanceTestUtil.createLine("경강선", "bg-blue-600", 판교역, 경기광주역, "10")
                .jsonPath().getString("id");
        createSection(경기광주역, 여주역, "50", 경강선);

        // when
        ExtractableResponse<Response> response = createSection(이매역, 부발역, "20", 경강선);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given : 구간에 3개의 역이 등록되어 있고
     * When : 마지막 역을 제거하면
     * Then : 구간에 2개의 역이 남고, 종전 역이 종점역으로 바뀐다.
     */
    @DisplayName("종점 제거 시 종전역이 종점으로 대체되는 지 확인")
    @Test
    void 종점제거_성공() {
        // given
        경강선 = LineAcceptanceTestUtil.createLine("경강선", "bg-blue-600", 판교역, 이매역, "10")
                .jsonPath().getString("id");
        createSection(이매역, 경기광주역, "50", 경강선);

        // when
        removeSection(경강선, 경기광주역, HttpStatus.OK);

        // then
        List<String> stationNames = LineAcceptanceTestUtil.getLines(Long.valueOf(경강선))
                .jsonPath().getList("stations.name", String.class);
        assertThat(stationNames).containsExactly("판교역", "이매역");
    }

    /**
     * Given : 구간에 3개의 역이 등록되어 있고
     * When : 중간 역을 제거하면
     * Then : 구간에 2개의 역이 남고, 처음역과 마지막 역이 연결된다.
     */
    @DisplayName("중간역을 제거 하는 경우 확인")
    @Test
    void 중간제거_성공() {
        // given
        경강선 = LineAcceptanceTestUtil.createLine("경강선", "bg-blue-600", 판교역, 이매역, "10")
                .jsonPath().getString("id");
        createSection(이매역, 경기광주역, "50", 경강선);

        // when
        removeSection(경강선, 이매역, HttpStatus.OK);

        // then
        List<String> stationNames = LineAcceptanceTestUtil.getLines(Long.valueOf(경강선))
                .jsonPath().getList("stations.name", String.class);
        assertThat(stationNames).containsExactly("판교역", "경기광주역");
    }

    /**
     * Given : 구간에 3개의 역이 등록되어 있고
     * When : 상행 종점 역을 제거하면
     * Then : 구간에 2개의 역이 남고, 중간역이 처음역이 되며, 중간과 마지막 역이 남는다.
     */
    @DisplayName("상행 종점을 제거하는 경우, 제거 성공하는 지 확인")
    @Test
    void 상행종점제거_성공() {
        // given
        경강선 = LineAcceptanceTestUtil.createLine("경강선", "bg-blue-600", 판교역, 이매역, "10")
                .jsonPath().getString("id");
        createSection(이매역, 경기광주역, "50", 경강선);

        // when
        removeSection(경강선, 판교역, HttpStatus.OK);

        // then
        List<String> stationNames = LineAcceptanceTestUtil.getLines(Long.valueOf(경강선))
                .jsonPath().getList("stations.name", String.class);
        assertThat(stationNames).containsExactly("이매역", "경기광주역");
    }

    /**
     * Given : 노선에 한 구간만 존재할 때
     * When : 역을 제거하면,
     * Then : 예외가 발생한다
     */
    @DisplayName("구간이 하나인 노선에서 역을 제거하려고 하는 경우 예외 처리")
    @Test
    void 최소구간제거_실패() {
        // given
        경강선 = LineAcceptanceTestUtil.createLine("경강선", "bg-blue-600", 판교역, 이매역, "10")
                .jsonPath().getString("id");

        // when / then
        removeSection(경강선, 판교역, HttpStatus.BAD_REQUEST);
    }

    /**
     * When : 노선에 없는 역을 제거하려고 할 때,
     * Then : 예외가 발생한다
     */
    @DisplayName("노선에 존재하지 않는 역을 제거하려고 했을 경우 에러")
    @Test
    void 노선에_없는_역_제거_실패() {
        // given
        경강선 = LineAcceptanceTestUtil.createLine("경강선", "bg-blue-600", 판교역, 이매역, "10")
                .jsonPath().getString("id");

        // when / then
        removeSection(경강선, 경기광주역, HttpStatus.BAD_REQUEST);
    }
}
