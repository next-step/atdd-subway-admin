package nextstep.subway.section;

import static nextstep.subway.SubwayAppBehaviors.지하철구간을_생성한다;
import static nextstep.subway.SubwayAppBehaviors.지하철노선에_속한_지하철역_이름목록을_반환한다;
import static nextstep.subway.SubwayAppBehaviors.지하철노선을_생성하고_ID를_반환한다;
import static nextstep.subway.SubwayAppBehaviors.지하철역을_생성하고_생성된_ID를_반환한다;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import nextstep.subway.annotation.SubwayAcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

@DisplayName("지하철구간 관련 기능")
@SubwayAcceptanceTest
public class SectionAcceptanceTest {

    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    /**
     * Given 노선을 생성하고, 노선에 포함되지않은 새로운 역을 하나 생성한다.
     * When 새로운 역을 포함한 구간을 등록한다. 이때 새 구간은 기존 구간의 시작역을 포함한다.
     * Then 지하철 노선 조회 시 생성된 구간을 찾을 수 있다
     */
    @DisplayName("기존 구간의 시작역을 기준으로 지하철구간을 등록한다.")
    @Test
    void createSectionAtUpStation() {
        // given
        Long 강남역_ID = 지하철역을_생성하고_생성된_ID를_반환한다("강남역");
        Long 충정로역_ID = 지하철역을_생성하고_생성된_ID를_반환한다("충정로역");
        Long lineId = 지하철노선을_생성하고_ID를_반환한다(
                "2호선", "초록색", 강남역_ID, 충정로역_ID, 100L
        );
        Long 신촌역_ID = 지하철역을_생성하고_생성된_ID를_반환한다("신촌역");

        // when
        ExtractableResponse<Response> response = 지하철구간을_생성한다(lineId, 강남역_ID, 신촌역_ID, 50L);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        List<String> stationNames = 지하철노선에_속한_지하철역_이름목록을_반환한다(lineId);
        assertThat(stationNames).containsExactly("강남역", "신촌역", "충정로역");
    }

    /**
     * Given 노선을 생성하고, 노선에 포함되지않은 새로운 역을 하나 생성한다.
     * When 새로운 역을 포함한 구간을 등록한다. 이때 새 구간은 기존 구간의 종착역을 포함한다.
     * Then 지하철 노선 조회 시 생성된 구간을 찾을 수 있다
     */
    @DisplayName("기존 구간의 종착역을 기준으로 지하철구간을 등록한다.")
    @Test
    void createSectionAtDownStation() {
        // given
        Long 강남역_ID = 지하철역을_생성하고_생성된_ID를_반환한다("강남역");
        Long 충정로역_ID = 지하철역을_생성하고_생성된_ID를_반환한다("충정로역");
        Long lineId = 지하철노선을_생성하고_ID를_반환한다(
                "2호선", "초록색", 강남역_ID, 충정로역_ID, 100L
        );
        Long 신촌역_ID = 지하철역을_생성하고_생성된_ID를_반환한다("신촌역");

        // when
        ExtractableResponse<Response> response = 지하철구간을_생성한다(lineId, 신촌역_ID, 충정로역_ID, 50L);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        List<String> stationNames = 지하철노선에_속한_지하철역_이름목록을_반환한다(lineId);
        assertThat(stationNames).containsExactly("강남역", "신촌역", "충정로역");
    }

    /**
     * Given 노선을 생성하고, 노선에 포함되지않은 새로운 역을 하나 생성한다.
     * When 노선의 첫 구간을 등록한다.
     * Then 지하철 노선 조회 시 새로운 역이 가장 처음에 위치해 있다.
     */
    @DisplayName("노선의 첫 구간을 등록한다.")
    @Test
    void createSectionAtLineStart() {
        // given
        Long 강남역_ID = 지하철역을_생성하고_생성된_ID를_반환한다("강남역");
        Long 충정로역_ID = 지하철역을_생성하고_생성된_ID를_반환한다("충정로역");
        Long lineId = 지하철노선을_생성하고_ID를_반환한다(
                "2호선", "초록색", 강남역_ID, 충정로역_ID, 100L
        );
        Long 신촌역_ID = 지하철역을_생성하고_생성된_ID를_반환한다("신촌역");

        // when
        ExtractableResponse<Response> response = 지하철구간을_생성한다(lineId, 신촌역_ID, 강남역_ID,150L);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        List<String> stationNames = 지하철노선에_속한_지하철역_이름목록을_반환한다(lineId);
        assertThat(stationNames).containsExactly("신촌역", "강남역", "충정로역");
    }

    /**
     * Given 노선을 생성하고, 노선에 포함되지않은 새로운 역을 하나 생성한다.
     * When 노선의 마지막 구간을 등록한다.
     * Then 지하철 노선 조회 시 새로운 역이 가장 마지막에 위치해 있다.
     */
    @DisplayName("노선의 마지막 구간을 등록한다.")
    @Test
    void createSectionAtLineEnd() {
        // given
        Long 강남역_ID = 지하철역을_생성하고_생성된_ID를_반환한다("강남역");
        Long 충정로역_ID = 지하철역을_생성하고_생성된_ID를_반환한다("충정로역");
        Long lineId = 지하철노선을_생성하고_ID를_반환한다(
                "2호선", "초록색", 강남역_ID, 충정로역_ID, 100L
        );
        Long 신촌역_ID = 지하철역을_생성하고_생성된_ID를_반환한다("신촌역");

        // when
        ExtractableResponse<Response> response = 지하철구간을_생성한다(lineId, 충정로역_ID, 신촌역_ID, 150L);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        List<String> stationNames = 지하철노선에_속한_지하철역_이름목록을_반환한다(lineId);
        assertThat(stationNames).containsExactly("강남역", "충정로역", "신촌역");
    }

    /**
     * Given 노선을 생성하고, 노선에 포함되지않은 새로운 역을 하나 생성한다.
     * When 새로운 역을 포함한 구간을 등록한다.
     *      이때 새로운 구간의 거리는 기존 역사이의 거리보다 길다.
     * Then 지하철 구간 등록에 실패한다.
     */
    @DisplayName("새로운 구간의 길이가 기존 역사이의 거리보다 길때 지하철구간 등록에 실패한다.")
    @Test
    void tooLongDistance() {
        // given
        Long 강남역_ID = 지하철역을_생성하고_생성된_ID를_반환한다("강남역");
        Long 충정로역_ID = 지하철역을_생성하고_생성된_ID를_반환한다("충정로역");
        Long lineId = 지하철노선을_생성하고_ID를_반환한다(
                "2호선", "초록색", 강남역_ID, 충정로역_ID, 100L
        );
        Long 신촌역_ID = 지하철역을_생성하고_생성된_ID를_반환한다("신촌역");

        // when
        ExtractableResponse<Response> response = 지하철구간을_생성한다(lineId, 강남역_ID, 신촌역_ID, 150L);

        // then
        assertThat(response.statusCode() == HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /**
     * Given 노선을 생성하고, 노선에 포함되지않은 새로운 역을 하나 생성한다.
     * When 새로운 역을 포함한 구간을 등록한다.
     *      이때 새로운 구간의 시작역과 종착역이 같다.
     * Then 지하철 구간 등록에 실패한다.
     */
    @DisplayName("새로운 구간의 시작역과 종착역이 동일한 경우 지하철구간 등록에 실패한다.")
    @Test
    void sameStartEnd() {
        // given
        Long 강남역_ID = 지하철역을_생성하고_생성된_ID를_반환한다("강남역");
        Long 충정로역_ID = 지하철역을_생성하고_생성된_ID를_반환한다("충정로역");
        Long lineId = 지하철노선을_생성하고_ID를_반환한다(
                "2호선", "초록색", 강남역_ID, 충정로역_ID, 100L
        );
        Long 신촌역_ID = 지하철역을_생성하고_생성된_ID를_반환한다("신촌역");

        // when
        ExtractableResponse<Response> response = 지하철구간을_생성한다(lineId, 신촌역_ID, 신촌역_ID, 50L);

        // then
        assertThat(response.statusCode()==HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /**
     * Given 노선을 생성한다.
     * When 구간 등록을 시도한다.
     *      이때 새로운 구간의 시작역과 종착역은 모두 기존 노선에 포함되어 있다.
     * Then 지하철 구간 등록에 실패한다.
     */
    @DisplayName("새로운 구간의 시작역과 종착역이 모두 기존 노선에 포함된경우 지하철구간 등록에 실패한다.")
    @Test
    void alreadyExist() {
        // given
        Long 강남역_ID = 지하철역을_생성하고_생성된_ID를_반환한다("강남역");
        Long 충정로역_ID = 지하철역을_생성하고_생성된_ID를_반환한다("충정로역");
        Long lineId = 지하철노선을_생성하고_ID를_반환한다(
                "2호선", "초록색", 강남역_ID, 충정로역_ID, 100L
        );
        // when
        ExtractableResponse<Response> response = 지하철구간을_생성한다(lineId, 강남역_ID, 충정로역_ID, 50L);

        // then
        assertThat(response.statusCode()==HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /**
     * Given 노선을 생성하고, 노선에 포함되지 않은 새로운 역을 두개 생성한다.
     * When 새로운 역을 포함한 구간을 등록한다.
     *      이때 새로운 구간의 시작역과 종착역은 모두 기존 노선에 없는 새로운 역이다.
     * Then 지하철 구간 등록에 실패한다.
     */
    @DisplayName("새로운 구간의 시작역과 종착역은 모두 기존 노선에 없는 새로운 역인 경우 지하철구간 등록에 실패한다.")
    @Test
    void completlyNew() {
        // given
        Long 강남역_ID = 지하철역을_생성하고_생성된_ID를_반환한다("강남역");
        Long 충정로역_ID = 지하철역을_생성하고_생성된_ID를_반환한다("충정로역");
        Long lineId = 지하철노선을_생성하고_ID를_반환한다(
                "2호선", "초록색", 강남역_ID, 충정로역_ID, 100L
        );
        Long 신촌역_ID = 지하철역을_생성하고_생성된_ID를_반환한다("신촌역");
        Long 잠실역_ID = 지하철역을_생성하고_생성된_ID를_반환한다("잠실역");

        // when
        ExtractableResponse<Response> response = 지하철구간을_생성한다(lineId, 신촌역_ID, 잠실역_ID, 50L);

        // then
        assertThat(response.statusCode()==HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
