package nextstep.subway.station;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    //5호선라인 지하철역들
    public static final Station aeogaeStation = new Station("애오개역");
    public static final Station chungjeongnoStation = new Station("충정로역");
    public static final Station seodaemunStation = new Station("서대문역");
    public static final Station gwanghwamunStation = new Station("광화문역");
    public static final Station yeouidoStation = new Station("여의도역");
    public static final Station yeouinaruStation = new Station("여의나루역");

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> response = 지하철역_등록되어_있음(gwanghwamunStation);

        // then
        assertResponseCode(response, HttpStatus.CREATED);
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        지하철역_등록되어_있음(gwanghwamunStation);

        // when
        ExtractableResponse<Response> response = 지하철역_등록되어_있음(gwanghwamunStation);

        // then
        assertResponseCode(response, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        ExtractableResponse<Response> createResponse1 = 지하철역_등록되어_있음(gwanghwamunStation);
        ExtractableResponse<Response> createResponse2 = 지하철역_등록되어_있음(seodaemunStation);

        // when
        ExtractableResponse<Response> response = get("/stations");

        // then
        assertResponseCode(response, HttpStatus.OK);
        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
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
        ExtractableResponse<Response> createResponse = 지하철역_등록되어_있음(gwanghwamunStation);

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = delete(uri);

        // then
        assertResponseCode(response, HttpStatus.NO_CONTENT);
    }

    public static ExtractableResponse<Response> 지하철역_등록되어_있음(Station station) {
        Map<String, String> params = new HashMap<>();
        params.put("name", station.getName());

        return post(params, "/stations");
    }
}
