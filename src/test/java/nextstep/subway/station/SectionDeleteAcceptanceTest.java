package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.domain.Station;
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

import java.util.Arrays;
import java.util.stream.Stream;

import static nextstep.subway.station.LineAcceptanceTest.요청이_실패한다;
import static nextstep.subway.station.StationAcceptanceTest.지하철역을_생성한다;

@DisplayName("지하철 구간 삭제 기능 관련")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SectionDeleteAcceptanceTest {

    @LocalServerPort
    int port;

    @Autowired
    private DatabaseCleaner databaseCleaner;
    private static StationResponse 상행역 = StationResponse.of(1L, "상행역");
    private static StationResponse 하행역 = StationResponse.of(2L, "하행역");;
    private static LineResponse 기본노선정보 = LineResponse.of(1L,"노선", "색상", 10L, Arrays.asList(상행역, 하행역));

    private static final String deleteStationInSectionUrl = "/lines/%d/sections?stationId=%d";

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        databaseCleaner.clear();

        상행역 = 지하철역을_생성한다("상행역").as(StationResponse.class);
        하행역 = 지하철역을_생성한다("하행역").as(StationResponse.class);
        기본노선정보 = LineAcceptanceTest.지하철_노선을_생성한다("노선", "색상", 10L, Arrays.asList(상행역.getId(), 하행역.getId())).as(LineResponse.class);
    }

    /**
     * When 구간에 포함된 역을 삭제하려고 하면
     * Then 실패함.
     */
    @ParameterizedTest
    @MethodSource("provideStationsInLine")
    void 노선에_구간이_하나인_경우_삭제할_수_없음(StationResponse station) {

        ExtractableResponse<Response> response =  구간에_포함된_역을_삭제함(기본노선정보.getId(),station);

        요청이_실패한다(response);
    }

    /**
     * When 노선에 포함되지 않은 역을 삭제하려고 하면
     * Then 실패함.
     */
    @Test
    void 노선에_포함되지_않은_역은_삭제할_수_없음() {

        ExtractableResponse<Response> response =  노선에서_역을_삭제함(기본노선정보.getId(),StationResponse.of(100L,"없음"));

        요청이_실패한다(response);
    }

    private ExtractableResponse<Response> 노선에서_역을_삭제함(Long lineId, StationResponse station) {
        return RequestUtil.deleteRequest(String.format(deleteStationInSectionUrl, lineId, station.getId()));
    }

    private ExtractableResponse<Response> 구간에_포함된_역을_삭제함(Long lineId, StationResponse station) {
        return 노선에서_역을_삭제함(lineId,station);
    }

    private static Stream<Arguments> provideStationsInLine(){
        return Stream.of(
                Arguments.of(상행역),
                Arguments.of(하행역)
        );
    }
}
