package nextstep.subway.line;

import static nextstep.subway.line.LineCRUD.지하철노선_생성;
import static nextstep.subway.line.LineCRUD.지하철노선_아이디로_조회;
import static nextstep.subway.station.StationCRUD.지하철역_생성;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequestDto;
import nextstep.subway.line.dto.SectionRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철 구간 관련 기능")
class SectionAcceptanceTest extends AcceptanceTest {
    private static final int 노선_거리 = 10;

    private Long 노선;
    private Long 강남역;
    private Long 선릉역;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철역_생성("강남역");
        선릉역 = 지하철역_생성("선릉역");
        LineRequestDto request = new LineRequestDto("2호선", "bg-green-600", 강남역, 선릉역, 노선_거리);

        // Given
        노선 = 지하철노선_생성(request).jsonPath().getLong("id");
    }

    /**
     * When 노선의 구간과 동일한 상행역 또는 동일한 하행역을 가지는 구간을 추가하면
     * Then 그 구간의 상행역과 하행역 사이에 새로운 역이 추가된다.
     */
    @Test
    @DisplayName("노선에 구간을 추가하면 조회 시 생성한 노선을 찾을 수 있다.")
    void 지하철구간_추가() {
        Long 역삼역 = 지하철역_생성("역삼역");

        SectionRequestDto sectionRequestDto = new SectionRequestDto(강남역, 역삼역, 4);

        // When
        ExtractableResponse<Response> response = 구간_추가(sectionRequestDto, 노선);

        응답코드_검증(response, HttpStatus.OK);

        // Then
        노선의_지하철역_검증("역삼역");
    }

    /**
     * When 새로운 역을 상행으로 상행역을 하행역인 구간을 추가하면
     * Then 노선은 새로운 역을 상행 종점으로 가진다.
     */
    @Test
    @DisplayName("노선에 새로운 역을 상행으로 기존 상행역을 하행역으로 가지는 구간을 추가하면 새로운 역이 상행 종점이 된다.")
    void 새로운_상행_종점역_추가() {
        Long 방배역 = 지하철역_생성("방배역");
        SectionRequestDto sectionRequestDto = new SectionRequestDto(방배역, 강남역, 4);

        // When
        ExtractableResponse<Response> response = 구간_추가(sectionRequestDto, 노선);

        응답코드_검증(response, HttpStatus.OK);

        // Then
        노선의_지하철역_검증("방배역");
    }

    /**
     * When 하행 종점역을 상행역으로 새로운 역을 하행역인 구간을 추가하면
     * Then 노선은 새로운 역을 하행 종점역으로 가진다.
     */
    @Test
    @DisplayName("노선에 기존 하행 종점역을 상행역으로 새로운 역을 하행행으로 가지는 구간을 추가하면 새로운 역이 하행 종점이 된다.")
    void 새로운_하행_종점역_추가() {
        Long 역삼역 = 지하철역_생성("역삼역");
        SectionRequestDto sectionRequestDto = new SectionRequestDto(강남역, 역삼역, 4);

        // When
        ExtractableResponse<Response> response = 구간_추가(sectionRequestDto, 노선);

        응답코드_검증(response, HttpStatus.OK);

        // Then
        노선의_지하철역_검증("역삼역");
    }

    /**
     * Scenario: 역 사이에 새로운 역을 등록한다.
     *   Given 주어진 노선에
     *   When 기존 역 사이 거리보다 크거나 같은 거리의 구간을 추가하면
     *   Then 400 Bad Request를 반환한다.
     */
    @Test
    @DisplayName("역 사이에 새로운 역을 추가할 때 기존 구간의 거리보다 크거나 같은 거리의 구간을 추가할 수 없다.")
    void 역_사이_새로운_역_추가시_거리_검증() {
        Long 역삼역 = 지하철역_생성("역삼역");
        SectionRequestDto sectionRequestDto = new SectionRequestDto(역삼역, 선릉역, 노선_거리);

        // When
        ExtractableResponse<Response> response = 구간_추가(sectionRequestDto, 노선);

        // Then
        응답코드_검증(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Scenario: 노선에 상행역과 하행역이 모두 등록된 경우
     *   Given 주어진 노선에
     *   When 등록된 역을 상행과 하행역으로 가지는 구간을 추가하면
     *   Then 400 Bad Request를 반환한다.
     */
    @Test
    @DisplayName("노선에 상하행역 모두 같은 구간이 존재하면 추가할 수 없다.")
    void 상행역_하행역_모두_같은_구간_추가() {
        SectionRequestDto sectionRequestDto = new SectionRequestDto(강남역, 선릉역, 5);

        // When
        ExtractableResponse<Response> response = 구간_추가(sectionRequestDto, 노선);

        // Then
        응답코드_검증(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Scenario: 새로운 구간에 노선의 상행역과 하행역 둘 중 하나도 포함되지 않은 경우
     *   Given 주어진 노선에
     *   When 등록되지 않은 역을 상행과 하행역으로 가지는 구간을 추가하면
     *   Then 400 Bad Request를 반환한다.
     */
    @Test
    @DisplayName("추가할 구간에 기존 상행역이나 하행역과 동일하지 않으면 추가할 수 없다.")
    void 상하행역_모두_기존_구간에_없는_구간_추가() {
        Long 잠실역 = 지하철역_생성("잠실역");
        Long 잠실나루역 = 지하철역_생성("잠실나루역");
        SectionRequestDto sectionRequestDto = new SectionRequestDto(잠실역, 잠실나루역, 5);

        // When
        ExtractableResponse<Response> response = 구간_추가(sectionRequestDto, 노선);

        // Then
        응답코드_검증(response, HttpStatus.BAD_REQUEST);
    }

    private ExtractableResponse<Response> 구간_추가(SectionRequestDto sectionRequestDto, Long lineId) {
        return RestAssured.given().log().all()
                .body(sectionRequestDto).contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(String.format("/lines/%d/sections", lineId))
                .then().log().all().extract();
    }

    private void 노선의_지하철역_검증(String 역이름) {
        List<String> stationNames = 지하철노선_아이디로_조회(노선).jsonPath().getList("stations.name", String.class);
        assertThat(stationNames).containsAnyOf(역이름);
    }
}
