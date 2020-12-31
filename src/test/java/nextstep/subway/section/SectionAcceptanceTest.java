


package nextstep.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;


@DisplayName("지하철 구간 관련 기능 테스트")
public class SectionAcceptanceTest extends AcceptanceTest {
    StationResponse station1;
    StationResponse station2;
    StationResponse station3;
    LineResponse line;

    @BeforeEach
    public void setUp() {
        super.setUp();
        // given
         station1 = StationAcceptanceTest.createStation("강남역").as(StationResponse.class);
         station2 = StationAcceptanceTest.createStation("광교역").as(StationResponse.class);
         station3 = StationAcceptanceTest.createStation("판교역").as(StationResponse.class);
         line = LineAcceptanceTest
                .createSubwayLine("신분당선", "bg-red-600",
                        station1.getId(), station2.getId(),
                        10).as(LineResponse.class);
    }

    @DisplayName("노선에 구간을 등록한다. case1 : 새로운 역을 하행 종점으로 등록할 경우")
    @Test
    void addSection1() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        LineAcceptanceTest.addSubwayStation(line.getId(), station2.getId(), station3.getId(), 6);

        // then
        // 지하철_노선에_지하철역_등록됨
        ExtractableResponse<Response> response = LineAcceptanceTest.searchSubwayLineOne(line.getId());
        LineResponse lineResponse = response.response().getBody().as(LineResponse.class);
        Assertions.assertThat(lineResponse.getName()).isEqualTo(line.getName());
        Assertions.assertThat(lineResponse.getStations())
                .extracting(StationResponse::getName)
                .containsExactly(station1.getName(), station2.getName(), station3.getName());
    }

    @DisplayName("노선에 구간을 등록한다. case2 : 새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void addSection2() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        LineAcceptanceTest.addSubwayStation(line.getId(), station3.getId(), station1.getId(), 6);

        // then
        // 지하철_노선에_지하철역_등록됨
        ExtractableResponse<Response> response = LineAcceptanceTest.searchSubwayLineOne(line.getId());
        LineResponse lineResponse = response.response().getBody().as(LineResponse.class);
        Assertions.assertThat(lineResponse.getName()).isEqualTo(line.getName());
        Assertions.assertThat(lineResponse.getStations())
                .extracting(StationResponse::getName)
                .containsExactly(station3.getName(), station1.getName(), station2.getName());
    }

    @DisplayName("노선에 구간을 등록한다. case3 : 역 사이에 새로운 역을 등록할 경우(기존상행-새로운하행 관계)")
    @Test
    void addSection3() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        LineAcceptanceTest.addSubwayStation(line.getId(), station1.getId(), station3.getId(), 6);

        // then
        // 지하철_노선에_지하철역_등록됨
        ExtractableResponse<Response> response = LineAcceptanceTest.searchSubwayLineOne(line.getId());
        LineResponse lineResponse = response.response().getBody().as(LineResponse.class);
        Assertions.assertThat(lineResponse.getName()).isEqualTo(line.getName());
        Assertions.assertThat(lineResponse.getStations())
                .extracting(StationResponse::getName)
                .containsExactly(station1.getName(), station3.getName(), station2.getName());
    }

    @DisplayName("노선에 구간을 등록한다. case4 : 역 사이에 새로운 역을 등록할 경우(새로상행-기존하행 관계)")
    @Test
    void addSection4() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        LineAcceptanceTest.addSubwayStation(line.getId(), station3.getId(), station2.getId(), 6);

        // then
        // 지하철_노선에_지하철역_등록됨
        ExtractableResponse<Response> response = LineAcceptanceTest.searchSubwayLineOne(line.getId());
        LineResponse lineResponse = response.response().getBody().as(LineResponse.class);
        Assertions.assertThat(lineResponse.getName()).isEqualTo(line.getName());
        Assertions.assertThat(lineResponse.getStations())
                .extracting(StationResponse::getName)
                .containsExactly(station1.getName(), station3.getName(), station2.getName());
    }

    @DisplayName("노선에 구간등록 실패한다. case1 : 구간길이가 크거나 같을 경우")
    @ParameterizedTest
    @CsvSource({"1, 3, 12", "1, 3, 10"})
    void failAddSection1(Long station1Id, Long station3Id, int distance) {
        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = LineAcceptanceTest.addSubwayStation(line.getId(), station1Id, station3Id, distance);

        // then
        // 지하철_노선에_지하철역_등록 실패됨
         Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("노선에 구간등록 실패한다. case2 : 이미 등록되어 있는 구간인 경우")
    @Test
    void failAddSection3() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = LineAcceptanceTest.addSubwayStation(line.getId(), station1.getId(), station2.getId(), 6);

        // then
        // 지하철_노선에_지하철역_등록 실패됨
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
    @DisplayName("노선에 구간등록 실패한다. case3 : 상행 하행 둘다 기존 구간에 등록되어 있지 않은 경우")
    @Test
    void failAddSection4() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        StationResponse station4 = StationAcceptanceTest.createStation("정자역").as(StationResponse.class);
        ExtractableResponse<Response> response = LineAcceptanceTest.addSubwayStation(line.getId(), station3.getId(), station4.getId(), 6);

        // then
        // 지하철_노선에_지하철역_등록 실패됨
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}

