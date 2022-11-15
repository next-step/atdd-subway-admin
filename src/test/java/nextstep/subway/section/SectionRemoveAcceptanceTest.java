package nextstep.subway.section;

import io.restassured.RestAssured;
import nextstep.subway.DatabaseInitializer;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineStationResponse;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.dto.StationResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import static nextstep.subway.line.LineAcceptanceMethods.지하철_노선_생성;
import static nextstep.subway.line.LineAcceptanceMethods.지하철_노선_조회;
import static nextstep.subway.section.SectionAcceptanceMethods.지하철_노선에_지하철역_등록;
import static nextstep.subway.section.SectionAcceptanceMethods.지하철_노선에서_지하철역_제거;
import static nextstep.subway.station.StationAcceptanceMethods.지하철역_생성;

@DisplayName("구간 제거 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SectionRemoveAcceptanceTest {
    @LocalServerPort
    private int port;

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
     * Given 주어진 지하철 노선에 지하철역(구간)을 추가한다.
     * When 지하철 노선에서 상행 종점역을 제거한다.
     * Then 제거된 상행종점역 다음 역이 새로운 상행종점역이 된다.
     */
    @DisplayName("상행종점역이 제거될 경우 다음으로 오던 역이 상행종점역이 된다.")
    @Test
    void deleteUpStation() {
        StationResponse 강남역 = 지하철역_생성("강남역").as(StationResponse.class);
        SectionRequest 신사역_강남역_구간 = SectionRequest.of(신사역.getId(), 강남역.getId(), 5);
        지하철_노선에_지하철역_등록(신분당선.getId(), 신사역_강남역_구간);

        지하철_노선에서_지하철역_제거(신분당선.getId(), 신사역.getId());

        LineResponse 변경된_신분당선 = 지하철_노선_조회(신분당선.getId()).as(LineResponse.class);

        Assertions.assertThat(변경된_신분당선.getStations())
                .containsExactly(
                        LineStationResponse.from(강남역),
                        LineStationResponse.from(광교역)
                );
    }

    /**
     * Given 주어진 지하철 노선에 지하철역(구간)을 추가한다.
     * When 지하철 노선에서 하행종점역을 제거한다.
     * Then 제거된 하행종점역 전의 역이 새로운 하행종점역이 된다.
     */
    @DisplayName("하행종점역이 제거될 경우 이전에 오던 역이 하행종점역이 된다.")
    @Test
    void deleteDownStation() {
        StationResponse 강남역 = 지하철역_생성("강남역").as(StationResponse.class);
        SectionRequest 신사역_강남역_구간 = SectionRequest.of(신사역.getId(), 강남역.getId(), 5);
        지하철_노선에_지하철역_등록(신분당선.getId(), 신사역_강남역_구간);

        지하철_노선에서_지하철역_제거(신분당선.getId(), 광교역.getId());

        LineResponse 변경된_신분당선 = 지하철_노선_조회(신분당선.getId()).as(LineResponse.class);

        Assertions.assertThat(변경된_신분당선.getStations())
                .containsExactly(
                        LineStationResponse.from(신사역),
                        LineStationResponse.from(강남역)
                );
    }

    /**
     * Given 지하철 노선(A-B-C)를 생성한다.
     * When 지하철 노선(A-B-C)의 중간역(B)을 제거한다.
     * Then 지하철 노선은 A-C로 재배치된다.
     */
    @DisplayName("지하철 노선의 중간역이 제거될 경우 재배치를 한다.")
    @Test
    void deleteStationBetweenUpStationAndDownStation() {
        StationResponse 강남역 = 지하철역_생성("강남역").as(StationResponse.class);
        SectionRequest 신사역_강남역_구간 = SectionRequest.of(신사역.getId(), 강남역.getId(), 5);
        지하철_노선에_지하철역_등록(신분당선.getId(), 신사역_강남역_구간);

        지하철_노선에서_지하철역_제거(신분당선.getId(), 강남역.getId());

        LineResponse 변경된_신분당선 = 지하철_노선_조회(신분당선.getId()).as(LineResponse.class);

        Assertions.assertThat(변경된_신분당선.getStations())
                .containsExactly(
                        LineStationResponse.from(신사역),
                        LineStationResponse.from(광교역)
                );
    }
}
