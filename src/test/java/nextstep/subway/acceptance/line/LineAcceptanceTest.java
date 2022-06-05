package nextstep.subway.acceptance.line;

import static nextstep.subway.acceptance.line.Line.지하철노선_생성;
import static nextstep.subway.acceptance.line.Line.지하철노선_아이디로_삭제;
import static nextstep.subway.acceptance.line.Line.지하철노선_아이디로_조회;
import static nextstep.subway.acceptance.line.Line.지하철노선_전체_목록_조회;
import static nextstep.subway.acceptance.line.Line.지하철노선_정보_수정;
import static nextstep.subway.acceptance.line.Line.지하철역_생성;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import nextstep.subway.acceptance.AcceptanceTest;
import nextstep.subway.dto.line.LineRequest;
import nextstep.subway.dto.line.PutLineRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    private LineRequest 신분당선;
    private LineRequest 분당선;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        Long upStationId = 지하철역_생성("지하철역");
        Long downStationId = 지하철역_생성("새로운지하철역");
        신분당선 = new LineRequest("신분당선", "bg-red-600", upStationId, downStationId, 10);

        Long downStationId2 = 지하철역_생성("또다른지하철역");
        분당선 = new LineRequest("분당선", "bg-green-600", upStationId, downStationId2, 10);
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @Test
    @DisplayName("지하철노선을 생성한다.")
    void 지하철노선_등록() {
        // When
        ExtractableResponse<Response> response = 지하철노선_생성(신분당선);
        응답코드_검증(response, HttpStatus.CREATED);

        // Then
        List<String> lineNames = 지하철노선_전체_목록_조회().jsonPath().getList("name", String.class);
        assertThat(lineNames).containsAnyOf(신분당선.getName());
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @Test
    @DisplayName("모든 지하철 노선을 생성한다.")
    void 지하철노선_목록_조회() {
        // Given
        지하철노선_생성(신분당선);
        지하철노선_생성(분당선);

        // When
        List<Long> lineIds = 지하철노선_전체_목록_조회().jsonPath().getList("id", Long.class);

        // Then
        assertThat(lineIds).hasSize(2);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @Test
    @DisplayName("아이디로 지하철노선을 조회한다.")
    void 지하철노선_조회() {
        // Given
        Long lineId = 지하철노선_생성(신분당선).jsonPath().getLong("id");

        // When
        ExtractableResponse<Response> response = 지하철노선_아이디로_조회(lineId);

        // Then
        assertAll(
                () -> 응답코드_검증(response, HttpStatus.OK),
                () -> assertThat(response.jsonPath().getLong("id")).isEqualTo(lineId),
                () -> assertThat(response.jsonPath().getString("name")).isEqualTo(신분당선.getName()),
                () -> assertThat(response.jsonPath().getString("color")).isEqualTo(신분당선.getColor()));
    }


    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @Test
    @DisplayName("특정 지하철노선의 정보를 수정할 수 있다.")
    void 지하철노선_수정() {
        final String 다른분당선 = "다른분당선";

        // Given
        Long lineId = 지하철노선_생성(신분당선).jsonPath().getLong("id");

        // When
        PutLineRequest request = new PutLineRequest(다른분당선, "bg-red-600");
        ExtractableResponse<Response> response = 지하철노선_정보_수정(lineId, request);
        응답코드_검증(response, HttpStatus.OK);

        // Then
        String name = 지하철노선_아이디로_조회(lineId).jsonPath().getString("name");
        assertThat(name).isEqualTo(다른분당선);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @Test
    @DisplayName("특정 지하철노선을 삭제할 수 있다.")
    void 지하철노선_삭제() {
        // Given
        Long lineId = 지하철노선_생성(신분당선).jsonPath().getLong("id");

        // When
        ExtractableResponse<Response> response = 지하철노선_아이디로_삭제(lineId);
        응답코드_검증(response, HttpStatus.NO_CONTENT);

        // Then
        response = 지하철노선_아이디로_조회(lineId);
        응답코드_검증(response, HttpStatus.NOT_FOUND);
    }
}
