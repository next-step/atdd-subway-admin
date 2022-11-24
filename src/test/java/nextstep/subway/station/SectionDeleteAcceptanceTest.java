package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.util.DatabaseCleaner;
import nextstep.subway.util.RequestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.stream.Stream;

import static nextstep.subway.station.LineAcceptanceTest.요청이_실패한다;
import static nextstep.subway.station.StationAcceptanceTest.지하철역을_생성한다;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 삭제 기능 관련")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SectionDeleteAcceptanceTest {

    @LocalServerPort
    int port;

    @Autowired
    private DatabaseCleaner databaseCleaner;
    private static StationResponse 상행역 = StationResponse.of(1L, "상행역");
    private static StationResponse 하행역 = StationResponse.of(2L, "하행역");
    private static StationResponse 중간역 = StationResponse.of(3L, "중간역");
    private static LineResponse 기본노선정보 = LineResponse.of(1L, "노선", "색상", 10L, Arrays.asList(상행역, 하행역));

    private static final String deleteStationInSectionUrl = "/lines/%d/sections?stationId=%d";

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        databaseCleaner.clear();

        상행역 = 지하철역을_생성한다("상행역").as(StationResponse.class);
        하행역 = 지하철역을_생성한다("하행역").as(StationResponse.class);
        중간역 = 지하철역을_생성한다("중간역").as(StationResponse.class);
        기본노선정보 = LineAcceptanceTest.지하철_노선을_생성한다("노선", "색상", 10L, Arrays.asList(상행역.getId(), 하행역.getId())).as(LineResponse.class);
    }

    /**
     * When 구간에 포함된 역을 삭제하려고 하면
     * Then 실패함.
     */
    @ParameterizedTest
    @MethodSource("provideStationsInLine")
    void 노선에_구간이_하나인_경우_삭제할_수_없음(StationResponse station) {

        ExtractableResponse<Response> response = 노선에서_역을_삭제한다(기본노선정보.getId(), station);

        요청이_실패한다(response);
    }

    /**
     * When 노선에 포함되지 않은 역을 삭제하려고 하면
     * Then 실패함.
     */
    @Test
    void 노선에_포함되지_않은_역은_삭제할_수_없음() {

        ExtractableResponse<Response> response = 노선에서_역을_삭제한다(기본노선정보.getId(), 중간역);

        요청이_실패한다(response);
    }

    /**
     * Given 노선에 두개의 구간이 존재할 때
     * When 상행선 종점을 삭제하면
     * Then 종점에 연결된 역이 새로운 종점이 된다.
     */
    @Test
    void 상행선_종점을_제거하는_경우() {
        노선에_두개의_구간이_존재한다();

        ExtractableResponse<Response> response = 노선에서_역을_삭제한다(기본노선정보.getId(), 상행역);

        중간역이_새로운_종점이_된다(response, 중간역, 하행역);
    }

    /**
     * Given 노선에 두개의 구간이 존재할 때
     * When 하행선 종점을 삭제하면
     * Then 종점에 연결된 역이 새로운 종점이 된다.
     */
    @Test
    void 하행선_종점을_제거하는_경우() {
        노선에_두개의_구간이_존재한다();

        ExtractableResponse<Response> response = 노선에서_역을_삭제한다(기본노선정보.getId(), 하행역);

        중간역이_새로운_종점이_된다(response, 상행역, 중간역);
    }

    private static void 노선에_두개의_구간이_존재한다() {
        SectionAcceptanceTest.구간생성을_요청한다(기본노선정보.getId(), 상행역.getId(), 중간역.getId(), 4);
    }

    private void 중간역이_새로운_종점이_된다(ExtractableResponse<Response> response, StationResponse 신규상행종점, StationResponse 신규하행종점s) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK);
        ExtractableResponse<Response> 노선 = LineAcceptanceTest.지하철_노선을_조회한다(기본노선정보.getId());
        assertThat(노선.jsonPath().getList("stations.id")).containsExactly(신규상행종점.getId(), 신규하행종점s.getId());
    }

    private ExtractableResponse<Response> 노선에서_역을_삭제한다(Long lineId, StationResponse station) {
        return RequestUtil.deleteRequest(String.format(deleteStationInSectionUrl, lineId, station.getId()));
    }

    private static Stream<Arguments> provideStationsInLine() {
        return Stream.of(
                Arguments.of(상행역),
                Arguments.of(하행역)
        );
    }
}
