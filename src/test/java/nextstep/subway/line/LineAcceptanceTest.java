package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.utils.LineAcceptanceTestUtil;
import nextstep.subway.utils.StationAcceptanceTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private Long stationId1;
    private Long stationId2;
    private Long stationId3;

    @BeforeEach
    public void setUp() {
        super.setUp();
        // given
        ExtractableResponse<Response> createdStationResponse1 = StationAcceptanceTestUtil.지하철됨_역_생성_됨(
            "잠실역");
        ExtractableResponse<Response> createdStationResponse2 = StationAcceptanceTestUtil.지하철됨_역_생성_됨(
            "몽촌토성역");
        ExtractableResponse<Response> createdStationResponse3 = StationAcceptanceTestUtil.지하철됨_역_생성_됨(
            "강동구청역");

        stationId1 = createdStationResponse1.as(StationResponse.class).getId();
        stationId2 = createdStationResponse2.as(StationResponse.class).getId();
        stationId3 = createdStationResponse3.as(StationResponse.class).getId();
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        // when
        ExtractableResponse<Response> createResponse = LineAcceptanceTestUtil.지하철_노선_등록되어_있음("2호선",
            "RED", stationId1, stationId2, 100);

        // then
        assertAll(
            () -> assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
            () -> assertThat(createResponse.header("Location")).isNotBlank()
        );
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        LineAcceptanceTestUtil.지하철_노선_등록되어_있음("2호선", "RED",
            stationId1, stationId2, 100);

        // when
        ExtractableResponse<Response> createResponse = LineAcceptanceTestUtil.지하철_노선_등록되어_있음("2호선",
            "RED", stationId1, stationId2, 100);

        // then
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }


    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        ExtractableResponse<Response> createResponse1 = LineAcceptanceTestUtil.지하철_노선_등록되어_있음("2호선",
            "RED", stationId1, stationId2, 100);
        ExtractableResponse<Response> createResponse2 = LineAcceptanceTestUtil.지하철_노선_등록되어_있음("3호선",
            "ORANGE", stationId1, stationId3, 100);

        // when
        ExtractableResponse<Response> response = LineAcceptanceTestUtil.지하철_노선_목록_조회_요청();

        // then
        List<Long> expectedLineIds = LineAcceptanceTestUtil.ids_추출_ByLocation(
            Arrays.asList(createResponse1,
                createResponse2));
        List<Long> resultLineIds = LineAcceptanceTestUtil.ids_추출_ByLineResponse(response);
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(resultLineIds).containsAll(expectedLineIds)
        );
    }


    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        ExtractableResponse<Response> createResponse = LineAcceptanceTestUtil.지하철_노선_등록되어_있음("2호선",
            "RED", stationId1, stationId2, 100);

        // when
        ExtractableResponse<Response> response = LineAcceptanceTestUtil.지하철_노선_조회_요청(
            createResponse);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> createResponse1 = LineAcceptanceTestUtil.지하철_노선_등록되어_있음("2호선",
            "RED", stationId1, stationId2, 100);
        Map<String, String> updateParams = LineAcceptanceTestUtil.지하철_노선_생성_파라미터_맵핑("3호선", "RED");

        // when
        ExtractableResponse<Response> response = LineAcceptanceTestUtil.지하철_노선_수정_요청(updateParams,
            createResponse1);

        // then
        String responseLineName = response.jsonPath().get("name");
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(responseLineName).isEqualTo(updateParams.get("name"))
        );
    }


    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> createResponse = LineAcceptanceTestUtil.지하철_노선_등록되어_있음("2호선",
            "RED", stationId1, stationId2, 100);

        // when
        ExtractableResponse<Response> response = LineAcceptanceTestUtil.지하철_노선_제거_요청(
            createResponse);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

}
