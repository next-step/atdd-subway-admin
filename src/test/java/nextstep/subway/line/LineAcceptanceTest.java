package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static nextstep.subway.line.LineTestMethods.*;
import static nextstep.subway.testutils.FactoryMethods.createStationResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        StationResponse 상행역 = createStationResponse("상행역");
        StationResponse 하행역 = createStationResponse("하행역");
        ExtractableResponse<Response> response = 노선_생성(
                "1호선", "blue", 상행역.getId(), 하행역.getId(), 10
        );

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(lineNames(노선_전체_조회())).containsAnyOf("1호선")
        );
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        StationResponse 상행역 = createStationResponse("상행역");
        StationResponse 하행역 = createStationResponse("하행역");
        StationResponse 상행역2 = createStationResponse("상행역2");
        StationResponse 하행역2 = createStationResponse("하행역2");
        노선_생성("1호선", "blue", 상행역.getId(), 하행역.getId(), 10);
        노선_생성("2호선", "blue", 상행역2.getId(), 하행역2.getId(), 10);

        //when
        ExtractableResponse<Response> response = 노선_전체_조회();
        List<String> lineNames = lineNames(response);

        //then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(lineNames.size()).isEqualTo(2),
                () -> assertThat(lineNames).contains("1호선", "2호선")
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        //given
        StationResponse 상행역 = createStationResponse("상행역");
        StationResponse 하행역 = createStationResponse("하행역");
        ExtractableResponse<Response> createdResponse = 노선_생성(
                "1호선", "blue", 상행역.getId(), 하행역.getId(), 10
        );

        //when
        Long id_1호선 = createdResponse.as(LineResponse.class).getId();
        ExtractableResponse<Response> findResponse = 노선_단건_조회(id_1호선);

        //then
        assertAll(
                () -> assertThat(findResponse.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(lineName(findResponse)).isEqualTo("1호선"),
                () -> assertThat(stations(findResponse).size()).isEqualTo(2)
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        //given
        StationResponse 상행역 = createStationResponse("상행역");
        StationResponse 하행역 = createStationResponse("하행역");
        ExtractableResponse<Response> createdResponse = 노선_생성(
                "1호선", "blue", 상행역.getId(), 하행역.getId(), 10
        );

        //when
        Long id_1호선 = createdResponse.as(LineResponse.class).getId();
        노선_수정(id_1호선, "1호선_new", "skyblue");

        //then
        ExtractableResponse<Response> response = 노선_단건_조회(id_1호선);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(lineName(response)).isEqualTo("1호선_new"),
                () -> assertThat(color(response)).isEqualTo("skyblue")
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteLine() {
        //given
        StationResponse 상행역 = createStationResponse("상행역");
        StationResponse 하행역 = createStationResponse("하행역");
        ExtractableResponse<Response> createdResponse = 노선_생성(
                "1호선", "blue", 상행역.getId(), 하행역.getId(), 10
        );

        //when
        Long id_1호선 = createdResponse.as(LineResponse.class).getId();
        ExtractableResponse<Response> deletedResponse = 노선_삭제(id_1호선);

        //then
        assertAll(
                () -> assertThat(deletedResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
                () -> assertThat(lineNames(노선_전체_조회())).doesNotContain("1호선")
        );
    }

    private List<String> lineNames(ExtractableResponse<Response> response) {
        return response.jsonPath().getList("name", String.class);
    }

    private String lineName(ExtractableResponse<Response> response) {
        return response.jsonPath().getString("name");
    }

    private List<Station> stations(ExtractableResponse<Response> response) {
        return response.jsonPath().getList("stations");
    }

    private String color(ExtractableResponse<Response> response) {
        return response.jsonPath().getString("color");
    }
}
