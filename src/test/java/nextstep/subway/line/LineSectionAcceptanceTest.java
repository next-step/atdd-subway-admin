package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 구간 조회")
public class LineSectionAcceptanceTest extends AcceptanceTest {

    private LineRequest params;

    @BeforeEach
    void init() {
        params = LineRequest.builder()
                .name("2호선")
                .color("bg-red-600")
                .upStationId(1L)
                .downStationId(1L)
                .distance(10)
                .build();
    }

    @DisplayName("노선 생성 시 종점역(상행, 하행)을 함께 추가하기")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = LineAcceptanceUtil.지하철_노선_생성_요청(params);

        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();

        // 지하철_역_목록_포함됨
        List<String> expectedStationNames = Arrays.asList("강남역", "역삼역");
        List<String> resultStationNames = response.jsonPath()
                .getObject(".", LineResponse.class)
                .getStations()
                .stream()
                .map(Station::getName)
                .collect(Collectors.toList());
        assertThat(resultStationNames).containsAll(expectedStationNames);

    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse = LineAcceptanceUtil.지하철_노선_생성_요청(params);

        // when
        // 지하철_노선_조회_요청
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = LineAcceptanceUtil.지하철_노선_조회_요청(uri);

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        // 지하철_역_목록_포함됨
        List<String> expectedStationNames = Arrays.asList("강남역", "역삼역");
        List<String> resultStationNames = response.jsonPath()
                .getObject(".", LineResponse.class)
                .getStations()
                .stream()
                .map(Station::getName)
                .collect(Collectors.toList());
        assertThat(resultStationNames).containsAll(expectedStationNames);

    }

}
