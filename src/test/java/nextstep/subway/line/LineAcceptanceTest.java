package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.path.json.JsonPath;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.station.StationAcceptanceTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("지하철노선 관련 기능 인수 테스트")
public class LineAcceptanceTest extends AcceptanceTest {

    private StationResponse upStation;
    private StationResponse downStation;
    private LineRequestParamsBuilder noNameLine;

    @BeforeEach
    public void setUp() {
        super.setUp();

        upStation = StationAcceptanceTestHelper.createStation("강남역").as(StationResponse.class);
        downStation = StationAcceptanceTestHelper.createStation("분당역").as(StationResponse.class);

        noNameLine = new LineRequestParamsBuilder()
            .withColor("red")
            .withUpStation(upStation.getId())
            .withDownStation(downStation.getId())
            .withDistance(10);
    }

    @DisplayName("존재하지 않는 지하철 노선을 생성하면, 지하철 노선 목록 조회시 생성한 노선을 찾을 수 있다.")
    @Test
    void createLine() {
        assertThat(LineAcceptanceTestHelper.createLine(noNameLine.withName("신분당선").build())
            .statusCode())
            .isEqualTo(HttpStatus.CREATED.value());
        assertThat(LineAcceptanceTestHelper.getLines().getList("name", String.class))
            .containsExactly("신분당선");
    }

    @DisplayName("기존에 존재하는 지하철 노선 정보로 또다시 지하철 노선 생성은 불가능 하다.")
    @Test
    void createStationWithDuplicateRequest() {
        assertThat(LineAcceptanceTestHelper.createLine(noNameLine.withName("신분당선").build())
            .statusCode())
            .isEqualTo(HttpStatus.CREATED.value());
        assertThat(LineAcceptanceTestHelper.createLine(noNameLine.withName("신분당선").build())
            .statusCode())
            .isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 생성 후 목록을 조회하면, 생성한 지하철 노선을 응답 받을 수 있다.")
    @Test
    void getLines() {
        LineAcceptanceTestHelper.createLine(noNameLine.withName("신분당선").build());
        LineAcceptanceTestHelper.createLine(noNameLine.withName("2호선").build());
        assertThat(LineAcceptanceTestHelper.getLines().getList("name", String.class))
            .containsAnyOf("신분당선", "2호선");
    }

    @DisplayName("지하철 노선 생성 후 반환된 아이디를 이용해 지하철 노선을 조회하면, 지하철 노선을 응답 받을 수 있다.")
    @Test
    void getLineById() {
        final String id = LineAcceptanceTestHelper.createLine(noNameLine.withName("신분당선").build())
            .jsonPath().getString("id");
        assertThat(LineAcceptanceTestHelper.getLine(id).getString("name"))
            .isEqualTo("신분당선");
    }

    @DisplayName("지하철 노선 생성 후 해당 지하철 노선 정보를 갱신하면, 지하철 노선 목록 조회시 갱신한 정보를 조회 할 수 있어야 한다.")
    @Test
    void updateLine() {
        final String id = LineAcceptanceTestHelper.createLine(noNameLine.withName("신분당선").withColor("R").build())
            .jsonPath().getString("id");
        final JsonPath line = LineAcceptanceTestHelper.updateLine(id, noNameLine.withName("분당선").withColor("G").build());
        assertAll(
            () -> assertThat(line.getString("name")).isEqualTo("분당선"),
            () -> assertThat(line.getString("color")).isEqualTo("G")
        );
    }

    @DisplayName("지하철 노선 생성 후 해당 지하철 노선을 삭제하면, 지하철 노선 목록 조회 시 삭제한 노선은 조회 되지 않아야 한다.")
    @Test
    void deleteLine() {
        final String targetId = LineAcceptanceTestHelper.createLine(noNameLine.withName("신분당선").build())
            .jsonPath().getString("id");
        final String nonTargetId = LineAcceptanceTestHelper.createLine(noNameLine.withName("분당선").build())
            .jsonPath().getString("id");

        LineAcceptanceTestHelper.deleteLine(targetId);

        assertThat(LineAcceptanceTestHelper.getLines().getList("id", String.class))
            .containsExactly(nonTargetId);
    }

}
