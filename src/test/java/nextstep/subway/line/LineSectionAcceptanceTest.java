package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceUtil;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 구간 조회")
public class LineSectionAcceptanceTest extends AcceptanceTest {

    private LineRequest params;

    @BeforeEach
    void init() {
        StationAcceptanceUtil.지하철_역_생성_요청(new StationRequest("강남역"));
        StationAcceptanceUtil.지하철_역_생성_요청(new StationRequest("역삼역"));
        params = LineRequest.builder()
                .name("2호선")
                .color("bg-red-600")
                .upStationId(1L)
                .downStationId(2L)
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

    @DisplayName("노선에 등록된 구간을 순서대로 정렬하여 상행 종점부터 하행 종점까지 목록을 응답하기")
    @Test
    void getLineSortById() {

        // given
        StationAcceptanceUtil.지하철_역_생성_요청(new StationRequest("선릉역"));
        StationAcceptanceUtil.지하철_역_생성_요청(new StationRequest("삼성역"));
        params = LineRequest.builder()
                .name("2호선")
                .color("bg-red-600")
                .upStationId(2L)
                .downStationId(3L)
                .distance(10)
                .build();
        ExtractableResponse<Response> createResponse = LineAcceptanceUtil.지하철_노선_생성_요청(params);
        params = LineRequest.builder()
                .name("2호선")
                .color("bg-red-600")
                .upStationId(4L)
                .downStationId(2L)
                .distance(10)
                .build();
        LineAcceptanceUtil.지하철_구간_등록_요청(params);

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = LineAcceptanceUtil.지하철_노선_조회_요청(uri);

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        // 상행역_하행역까지_목록_응답됨
        List<Long> resultStationIds = response.jsonPath()
                .getObject(".", LineResponse.class)
                .getStations()
                .stream()
                .map(Station::getId)
                .collect(Collectors.toList());
        assertThat(resultStationIds).containsExactly(4L, 2L, 3L);
    }

}
