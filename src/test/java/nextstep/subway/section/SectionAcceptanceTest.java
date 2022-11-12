package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.DatabaseInitializer;
import nextstep.subway.line.LineAcceptanceMethods;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineStationResponse;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.StationAcceptanceMethods;
import nextstep.subway.station.dto.StationResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import static nextstep.subway.line.LineAcceptanceMethods.지하철_노선_생성;
import static nextstep.subway.line.LineAcceptanceMethods.지하철_노선_조회;
import static nextstep.subway.station.StationAcceptanceMethods.지하철역_생성;

@DisplayName("구간 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SectionAcceptanceTest {
    @LocalServerPort
    int port;

    @Autowired
    private DatabaseInitializer databaseInitializer;

    private StationResponse 신사역;
    private StationResponse 광교역;
    private LineResponse 신분당선;

    @BeforeEach
    void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
            databaseInitializer.afterPropertiesSet();
        }

        databaseInitializer.initialize();

        신사역 = 지하철역_생성("신사역").as(StationResponse.class);
        광교역 = 지하철역_생성("광교역").as(StationResponse.class);
        신분당선 = 지하철_노선_생성("신분당선", "red", 신사역.getId(), 광교역.getId(), 10)
                .as(LineResponse.class);
    }

    /**
     * Given 새로운 역 A를 생성한다.
     * When 지하철 노선(B역 - C역) 중간에(역 사이에) 새로운 역 A를 등록한다.
     * Then 지하철 노선 조회 시 등록된 새로운 역 A를 찾을 수 있다.(B역 - A역 - C역)
     */
    @DisplayName("역 사이에 새로운 역을 등록한다.")
    @Test
    void createSection() {
        StationResponse 강남역 = 지하철역_생성("강남역").as(StationResponse.class);
        SectionRequest 신사역_강남역_구간 = SectionRequest.of(신사역.getId(), 강남역.getId(), 5);

        SectionAcceptanceMethods.지하철_노선에_지하철역_등록(this.신분당선.getId(), 신사역_강남역_구간);

        LineResponse 신분당선 = 지하철_노선_조회(this.신분당선.getId()).as(LineResponse.class);
        Assertions.assertThat(신분당선.getStations())
                .containsExactly(
                        LineStationResponse.from(신사역),
                        LineStationResponse.from(강남역),
                        LineStationResponse.from(광교역)
                );
    }

    /**
     * Given 새로운 역 A를 생성한다.
     * When 새로운 역 A를 상행 종점으로 지하철 노선(B역 - C역)에 등록한다.
     * Then 지하철 노선 조회 시 새로운 역 A가 상행 종점인 것을 확인할 수 있다. (A역 - B역 - C역)
     */
    @DisplayName("새로운 역을 상행 종점으로 등록한다.")
    @Test
    void createSectionOfUpStation() {
        StationResponse 압구정역 = 지하철역_생성("압구정역").as(StationResponse.class);
        SectionRequest 압구정역_신사역_구간 = SectionRequest.of(압구정역.getId(), 신사역.getId(), 5);

        SectionAcceptanceMethods.지하철_노선에_지하철역_등록(this.신분당선.getId(), 압구정역_신사역_구간);

        LineResponse 신분당선 = 지하철_노선_조회(this.신분당선.getId()).as(LineResponse.class);
        Assertions.assertThat(신분당선.getStations())
                .containsExactly(
                        LineStationResponse.from(압구정역),
                        LineStationResponse.from(신사역),
                        LineStationResponse.from(광교역)
                );
    }

    /**
     * Given 새로운 역 A를 생성한다.
     * When 새로운 역 A을 하행 종점으로 지하철 노선(B역 - C역)에 등록한다.
     * Then 지하철 노선 조회 시 새로운 역 A가 하행 종점인 것을 확인할 수 있다. (B역 - C역 - A역)
     */
    @DisplayName("새로운 역을 하행 종점을 등록한다.")
    @Test
    void createSectionOfDownStation() {
        StationResponse 호매실역 = 지하철역_생성("호매실역").as(StationResponse.class);
        SectionRequest 광교역_호매실역_구간 = SectionRequest.of(광교역.getId(), 호매실역.getId(), 5);

        SectionAcceptanceMethods.지하철_노선에_지하철역_등록(this.신분당선.getId(), 광교역_호매실역_구간);

        LineResponse 신분당선 = 지하철_노선_조회(this.신분당선.getId()).as(LineResponse.class);
        Assertions.assertThat(신분당선.getStations())
                .containsExactly(
                        LineStationResponse.from(신사역),
                        LineStationResponse.from(광교역),
                        LineStationResponse.from(호매실역)
                );
    }

    /**
     * Given 새로운 역 A를 생성한다.
     * When 지하철 노선(B역 - C역) 중간에(역 사이에) 기존 역 사이 길이보가 크거나 같도록 새로운 역 A를 등록한다.
     * Then 새로운 역 A 등록(구간 등록)에 실패한다.
     */
    @DisplayName("역 사이에 시로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록할 수 없다.")
    @ParameterizedTest(name = "{index} | {displayName} | {argumentsWithNames}")
    @ValueSource(ints = {10, 100})
    void sectionDistanceOver(int input) {
        StationResponse 강남역 = 지하철역_생성("강남역").as(StationResponse.class);
        SectionRequest 신사역_강남역_구간 = SectionRequest.of(신사역.getId(), 강남역.getId(), input);

        ExtractableResponse<Response> response = SectionAcceptanceMethods.지하철_노선에_지하철역_등록(this.신분당선.getId(), 신사역_강남역_구간);

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 지하철 노선(B역 - C역)에 지하철 노선(B역 - C역)을 등록한다.
     * Then 지하철 구간 등록에 실패한다.
     */
    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없다.")
    @Test
    void duplicatedUpStationAndDownStation() {
        SectionRequest 중복_구간 = SectionRequest.of(신사역.getId(), 광교역.getId(), 10);

        ExtractableResponse<Response> response = SectionAcceptanceMethods.지하철_노선에_지하철역_등록(this.신분당선.getId(), 중복_구간);

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
