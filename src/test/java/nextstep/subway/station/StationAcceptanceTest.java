package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.stream.Stream;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {


    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given, when
        ExtractableResponse<Response> response = StationRestHelper.지하철역_생성("강남역");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        String station = "강남역";
        StationRestHelper.지하철역_생성(station);

        // when
        ExtractableResponse<Response> response = StationRestHelper.지하철역_생성(station);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        ExtractableResponse<Response> 강남역 = StationRestHelper.지하철역_생성("강남역");
        ExtractableResponse<Response> 역삼역 = StationRestHelper.지하철역_생성("역삼역");

        // when
        ExtractableResponse<Response> response = StationRestHelper.지하철역_전체_조회();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Stream.of(강남역, 역삼역)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> 강남역 = StationRestHelper.지하철역_생성("강남역");

        // when
        String uri = 강남역.header("Location");
        ExtractableResponse<Response> response = StationRestHelper.지하철역_삭제(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
