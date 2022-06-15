package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.DatabaseCleanup;
import nextstep.subway.dto.line.LineResponse;
import nextstep.subway.dto.line.SectionRequest;
import nextstep.subway.dto.station.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static nextstep.subway.SubwayApi.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선 관련 기능")
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {

    @LocalServerPort
    int port;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    private StationResponse 당고개역;
    private StationResponse 오이도역;
    private ExtractableResponse<Response> response;

    @BeforeEach
    void setUp() {
        테스트_격리();

        노선생성();
    }

    private void 테스트_격리() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
            databaseCleanup.afterPropertiesSet();
        }
        databaseCleanup.cleanUp();
    }

    private void 노선생성() {
        // 역 생성
        당고개역 = 지하철역_생성("당고개역").body().as(StationResponse.class);
        오이도역 = 지하철역_생성("오이도역").body().as(StationResponse.class);

        // 노선 생성
        response = 지하철_노선_생성("4호선", "하늘색", 20, 당고개역.getId(), 오이도역.getId());
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다.
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> lineNames = 지하철역_노선_이름_목록조회();
        assertThat(lineNames).containsAnyOf("4호선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성하면
     * Then 지하철 노선 생성이 안된다
     */
    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철역을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // when
        StationResponse 소요산역 = 지하철역_생성("소요산역").body().as(StationResponse.class);
        StationResponse 인천역 = 지하철역_생성("인천역").body().as(StationResponse.class);
        int statusCode = 지하철_노선_생성("4호선", "파란색", 20, 소요산역.getId(), 인천역.getId()).statusCode();

        // then
        assertThat(statusCode).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 기존에 존재하는 지하철 노선 색갈로 지하철 노선을 생성하면
     * Then 지하철 노선 생성이 안된다
     */
    @DisplayName("기존에 존재하는 지하철 노선 색갈로 지하철역을 생성한다.")
    @Test
    void createLineWithDuplicateColor() {
        // when
        StationResponse 소요산역 = 지하철역_생성("소요산역").body().as(StationResponse.class);
        StationResponse 인천역 = 지하철역_생성("인천역").body().as(StationResponse.class);
        int statusCode = 지하철_노선_생성("1호선", "하늘색", 20, 소요산역.getId(), 인천역.getId()).statusCode();

        // then
        assertThat(statusCode).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("생성한 모든 노선 목록을 조회한다.")
    @Test
    void findAllLines() {
        // given
        StationResponse 소요산역 = 지하철역_생성("소요산역").body().as(StationResponse.class);
        StationResponse 인천역 = 지하철역_생성("인천역").body().as(StationResponse.class);
        지하철_노선_생성("1호선", "파란색", 20, 소요산역.getId(), 인천역.getId()).statusCode();

        // when
        List<String> lineNames = 지하철역_노선_이름_목록조회();

        // then
        assertAll(
                () -> assertThat(lineNames).hasSize(2),
                () -> assertThat(lineNames).containsExactly("4호선", "1호선")
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("생성한 노선을 조회한다.")
    @Test
    void findLineById() {
        // given
        long lineId = response.jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = 지하철역_노선_정보_조회(lineId);
        LineResponse lineResponse = convertLineResponse(response);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(lineResponse.getName()).isEqualTo("4호선"),
                () -> assertThat(lineResponse.getColor()).isEqualTo("하늘색")
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다.
     */
    @DisplayName("지하철 노선 이름과 색갈을 수정한다.")
    @Test
    void modifyLineById() {
        // given
        long lineId = response.jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> lineModifyResponse = 지하철역_노선_수정(lineId, "1호선", "파란색");

        // then
        assertThat(lineModifyResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        // then
        ExtractableResponse<Response> response = 지하철역_노선_정보_조회(lineId);
        LineResponse lineResponse = convertLineResponse(response);

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(lineResponse.getName()).isEqualTo("1호선"),
                () -> assertThat(lineResponse.getColor()).isEqualTo("파란색")
        );
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 기존에 존재하는 노선이름으로 수정하면
     * Then 해당 지하철 노선 이름은 수정되지 않는다.
     */
    @DisplayName("기존에 존해하는 지하철 노선의 이름으로 수정한다.")
    @Test
    void modifyLineDuplicateName() {
        // given
        StationResponse 소요산역 = 지하철역_생성("소요산역").body().as(StationResponse.class);
        StationResponse 인천역 = 지하철역_생성("인천역").body().as(StationResponse.class);
        지하철_노선_생성("1호선", "파란색", 20, 소요산역.getId(), 인천역.getId());

        long lineId = response.jsonPath()
                .getLong("id");

        // when
        ExtractableResponse<Response> response = 지하철역_노선_수정(lineId, "1호선", "하늘색");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 기존에 존재하는 노선이름으로 수정하면
     * Then 해당 지하철 노선 색갈은 수정되지 않는다.
     */
    @DisplayName("기존에 존해하는 지하철 노선의 색갈로 수정한다.")
    @Test
    void modifyLineDuplicateColor() {
        // given
        StationResponse 소요산역 = 지하철역_생성("소요산역").body().as(StationResponse.class);
        StationResponse 인천역 = 지하철역_생성("인천역").body().as(StationResponse.class);
        지하철_노선_생성("1호선", "파란색", 20, 소요산역.getId(), 인천역.getId());

        long lineId = response.jsonPath()
                .getLong("id");

        // when
        ExtractableResponse<Response> response = 지하철역_노선_수정(lineId, "4호선", "파란색");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다.
     */
    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        String lineName = "4호선";
        long lineId = response.jsonPath()
                .getLong("id");

        // when
        ExtractableResponse<Response> response = 지하철_노선_삭제(lineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        // then
        List<String> lineNames = 지하철역_노선_이름_목록조회();
        assertThat(lineNames.contains(lineName)).isFalse();
    }

    /**
     * Given 지하철 노선을 생성하고
     * Given 추가할 역을 생성하고
     * When 역 사이에 새로운 역을 등록하면
     * Then 새로운 구간이 추가된다.
     */
    @DisplayName("역 사이에 새로운 역을 등록한다.(하행역 동일)")
    @Test
    void addSection_하행역_동일() {
        // given
        LineResponse 사호선 = response.body().as(LineResponse.class);
        StationResponse 상록수역 = 지하철역_생성("상록수역").body().as(StationResponse.class);

        // when
        SectionRequest sectionRequest = new SectionRequest(상록수역.getId(), 오이도역.getId(), 10);
        ExtractableResponse<Response> response = 지하철_노선_구간추가(사호선.getId(), sectionRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * Given 지하철 노선을 생성하고
     * Given 추가할 역을 생성하고
     * When 역 사이에 새로운 역을 등록하면
     * Then 새로운 구간이 추가된다.
     */
    @DisplayName("역 사이에 새로운 역을 등록한다.(상행역 동일)")
    @Test
    void addSection_상행역_동일() {
        // given
        LineResponse 사호선 = response.body().as(LineResponse.class);
        StationResponse 상록수역 = 지하철역_생성("상록수역").body().as(StationResponse.class);

        // when
        SectionRequest sectionRequest = new SectionRequest(당고개역.getId(), 상록수역.getId(), 10);
        ExtractableResponse<Response> response = 지하철_노선_구간추가(사호선.getId(), sectionRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * Given 지하철 노선을 생성하고
     * Given 추가할 역을 생성하고
     * When 새로운 역을 상행 종점으로 등록하면
     * Then 새로운 구간이 추가된다.
     * Then 상행역이 새로운 역으로 바뀐다
     */
    @DisplayName("새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void addSection_새로운_역을_상행_종점으로() {
        // given
        LineResponse 사호선 = response.body().as(LineResponse.class);
        StationResponse 상록수역 = 지하철역_생성("상록수역").body().as(StationResponse.class);

        // when
        SectionRequest sectionRequest = new SectionRequest(상록수역.getId(), 당고개역.getId(), 10);
        ExtractableResponse<Response> response = 지하철_노선_구간추가(사호선.getId(), sectionRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * Given 지하철 노선을 생성하고
     * Given 추가할 역을 생성하고
     * When 새로운 역을 하행 종점으로 등록하면
     * Then 새로운 구간이 추가된다.
     * Then 하행역이 새로운 역으로 바뀐다
     */
    @DisplayName("새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void addSection_새로운_역을_하행_종점으로() {
        // given
        LineResponse 사호선 = response.body().as(LineResponse.class);
        StationResponse 상록수역 = 지하철역_생성("상록수역").body().as(StationResponse.class);

        // when
        SectionRequest sectionRequest = new SectionRequest(오이도역.getId(), 상록수역.getId(), 10);
        ExtractableResponse<Response> response = 지하철_노선_구간추가(사호선.getId(), sectionRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * Given 지하철 노선을 생성하고
     * Given 추가할 역을 생성하고
     * When 구간 사이 길이보다 긴 구간을 추가할 경우
     * Then 구간이 추가되지 않는다.
     */
    @ParameterizedTest(name = "기존 역 사이 길이보다 크거나 같으면 추가할 수 없음")
    @ValueSource(ints = {20, 21})
    void addSection_구간길이가_크거나_같으면_추가할_수_없다(int distance) {
        // given
        LineResponse 사호선 = response.body().as(LineResponse.class);
        StationResponse 상록수역 = 지하철역_생성("상록수역").body().as(StationResponse.class);

        // when
        SectionRequest sectionRequest = new SectionRequest(상록수역.getId(), 오이도역.getId(), distance);
        ExtractableResponse<Response> response = 지하철_노선_구간추가(사호선.getId(), sectionRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선을 생성하고
     * Given 추가할 역을 생성하고
     * When 상행역과 하행역 둘다 노선에 포함되지 않는 구간을 추가할 때
     * Then 구간이 추가되지 않는다.
     */
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void addSection_상행역_하행역_둘다_포함_안된_경우_추가할_수_없다() {
        // given
        LineResponse 사호선 = response.body().as(LineResponse.class);
        StationResponse 소요산역 = 지하철역_생성("소요산역").body().as(StationResponse.class);
        StationResponse 인천역 = 지하철역_생성("인천역").body().as(StationResponse.class);

        // when
        SectionRequest sectionRequest = new SectionRequest(소요산역.getId(), 인천역.getId(), 10);
        ExtractableResponse<Response> response = 지하철_노선_구간추가(사호선.getId(), sectionRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선을 생성하고
     * Given 추가할 역을 생성하고
     * When 상행역과 하행역이 이미 노선에 등록되어 있는 구간을 등록할 때
     * Then 구간이 추가되지 않는다.
     */
    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있으면 추가할 수 없음")
    @Test
    void addSection_상행역_하행역_둘다_포함된_경우_추가할_수_없다() {
        // given
        LineResponse 사호선 = response.body().as(LineResponse.class);

        // when
        SectionRequest sectionRequest = new SectionRequest(오이도역.getId(), 당고개역.getId(), 10);
        ExtractableResponse<Response> response = 지하철_노선_구간추가(사호선.getId(), sectionRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
