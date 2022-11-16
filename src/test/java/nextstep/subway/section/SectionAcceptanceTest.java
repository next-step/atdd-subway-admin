package nextstep.subway.section;

import static nextstep.subway.line.LineAcceptanceTestFixture.지하철_노선_생성;
import static nextstep.subway.line.LineAcceptanceTestFixture.지하철_노선_조회;
import static nextstep.subway.section.SectionAcceptanceTestFixture.지하철_노선_구간_삭제;
import static nextstep.subway.section.SectionAcceptanceTestFixture.지하철_노선_구간_추가;
import static nextstep.subway.station.StationAcceptanceTestFixture.지하철역_생성;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import nextstep.subway.AbstractAcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AbstractAcceptanceTest {
    
    private Long 강남역_ID;
    
    private Long 광교역_ID;
    
    private Long 양재역_ID;
    
    private Long 신사역_ID;
    
    @BeforeEach
    void addSection() {
        강남역_ID = 지하철역_생성("강남역").jsonPath().getLong("id");
        광교역_ID = 지하철역_생성("광교역").jsonPath().getLong("id");
        양재역_ID = 지하철역_생성("양재역").jsonPath().getLong("id");
        신사역_ID = 지하철역_생성("신사역").jsonPath().getLong("id");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 역 사이에 새로운 역을 등록하면
     * Then 새로운 구간이 추가된다.
     */
    @DisplayName("역 사이에 새로운 역을 등록한다.")
    @Test
    void addStationBetweenLine() {
        // given
        Long 신분당선_ID = 지하철_노선_생성("신분당선", "주황색", 강남역_ID, 광교역_ID, 12).jsonPath().getLong("id");

        // when
        지하철_노선_구간_추가(신분당선_ID, 강남역_ID, 양재역_ID, 5);

        // then
        List<String> result = 지하철_노선_조회(신분당선_ID).jsonPath().getList("stations.name");
        assertThat(result).hasSize(3)
            .contains("강남역", "광교역", "양재역");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 상행 종점에 지하철 구간을 등록하면
     * Then 노선에 새로운 지하철 역이 추가된다
     */
    @DisplayName("새로운 역을 상행 종점으로 등록한다.")
    @Test
    void addUpStationSection() {
        // given
        Long 신분당선_ID = 지하철_노선_생성("신분당선", "주황색", 강남역_ID, 광교역_ID, 12).jsonPath().getLong("id");

        // when
        지하철_노선_구간_추가(신분당선_ID, 양재역_ID, 강남역_ID, 3);

        // then
        List<String> result = 지하철_노선_조회(신분당선_ID).jsonPath().getList("stations.name");
        assertThat(result).hasSize(3)
            .contains("강남역", "광교역", "양재역");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 하행 종점에 지하철 구간을 등록하면
     * Then 노선에 새로운 지하철 역이 추가된다
     */
    @DisplayName("새로운 역을 하행 종점으로 등록한다.")
    @Test
    void addDownStationSection() {
        // given
        Long 신분당선_ID = 지하철_노선_생성("신분당선", "주황색", 강남역_ID, 광교역_ID, 12).jsonPath().getLong("id");

        // when
        지하철_노선_구간_추가(신분당선_ID, 광교역_ID, 양재역_ID, 3);

        // then
        List<String> result = 지하철_노선_조회(신분당선_ID).jsonPath().getList("stations.name");
        assertThat(result).hasSize(3)
            .contains("강남역", "광교역", "양재역");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 역 사이에 새로운 역을 등록 시 기존 역 사이 길이보다 크거나 같으면
     * Then 노선에 새로운 지하철 역을 등록할 수 없다
     */
    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록할 수 없다.")
    @Test
    void addSectionDistanceException() {
        // given
        Long 신분당선_ID = 지하철_노선_생성("신분당선", "주황색", 강남역_ID, 광교역_ID, 5).jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = 지하철_노선_구간_추가(신분당선_ID, 강남역_ID, 양재역_ID, 7);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 상행역과 하행역 이미 노선에 모두 등록되어 있다면
     * Then 노선에 새로운 지하철 역을 등록할 수 없다
     */
    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void addSectionDuplicateException() {
        // given
        Long 신분당선_ID = 지하철_노선_생성("신분당선", "주황색", 강남역_ID, 광교역_ID, 5).jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = 지하철_노선_구간_추가(신분당선_ID, 강남역_ID, 광교역_ID, 5);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 상행역과 하행역 둘 중 하나도 포함되어있지 않으면
     * Then 노선에 새로운 지하철 역을 등록할 수 없다
     */
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void addSectionNotExistException() {
        // given
        Long 신분당선_ID = 지하철_노선_생성("신분당선", "주황색", 강남역_ID, 광교역_ID, 5).jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = 지하철_노선_구간_추가(신분당선_ID, 양재역_ID, 신사역_ID, 5);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 종점을 제거하면
     * Then 다음으로 오던 역이 종점이 된다.
     */
    @DisplayName("")
    @Test
    void removeDownStationSection() {
        // given
        Long 신분당선_ID = 지하철_노선_생성("신분당선", "주황색", 강남역_ID, 광교역_ID, 10).jsonPath().getLong("id");
        지하철_노선_구간_추가(신분당선_ID, 강남역_ID, 양재역_ID, 5);

        // when
        지하철_노선_구간_삭제(신분당선_ID, 광교역_ID);

        // then
        List<String> result = 지하철_노선_조회(신분당선_ID).jsonPath().getList("stations.name");
        assertThat(result).hasSize(2)
            .contains("강남역", "양재역");
    }
}
