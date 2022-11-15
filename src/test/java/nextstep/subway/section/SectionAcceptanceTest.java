package nextstep.subway.section;

import static nextstep.subway.helper.JsonPathExtractor.getId;
import static nextstep.subway.helper.JsonPathExtractor.getStationNames;
import static nextstep.subway.helper.JsonPathExtractor.getStations;
import static nextstep.subway.line.LineAcceptanceTestFixture.createLine;
import static nextstep.subway.line.LineAcceptanceTestFixture.findLine;
import static nextstep.subway.section.SectionAcceptanceTestFixture.addSection;
import static nextstep.subway.station.StationAcceptanceTestFixture.createStation;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.base.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;

@DisplayName("지하철 구간 관련 기능")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SectionAcceptanceTest extends AcceptanceTest {
    private Long 서초역;
    private Long 강남역;

    @BeforeEach
    void stationSetUp(){
        서초역 = getId(createStation("서초역"));
        강남역 = getId(createStation("강남역"));
    }

    /**
     *  Given 지하철 노선을 생성하고
     *  When 지하철 구간을 추가하면
     *  Then 노선에 새로운 역이 추가된다.
     */
    @DisplayName("역 사이에 새로운 역을 등록한다.")
    @Test
    void addSectionBetweenLine() {
        // given
        Long lineId = getId(createLine("2호선", "bg-green-600", 20, 서초역, 강남역));

        // when
        Long 교대역 = getId(createStation("교대역"));
        ExtractableResponse<Response> createSectionResponse = addSection(lineId, 서초역, 교대역, 10);
        assertThat(createSectionResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        ExtractableResponse<Response> findLineResponse = findLine(lineId);
        assertThat(getStations((findLineResponse))).hasSize(3);
        assertThat(getStationNames(findLineResponse)).contains("서초역", "교대역", "강남역");
    }

    /**
     *  Given 지하철 노선을 생성하고
     *  When 새로운 역을 상행 종점으로 등록할 경우
     *  Then 노선의 상행 종점이 변경된다.
     */
    @DisplayName("새로운 역을 상행 종점으로 등록한다.")
    @Test
    void addSectionHeadOfLine() {
        // given
        Long lineId = getId(createLine("2호선", "bg-green-600", 20, 서초역, 강남역));

        // when
        Long 방배역 = getId(createStation("방배역"));
        ExtractableResponse<Response> createSectionResponse = addSection(lineId, 방배역, 서초역,10);
        assertThat(createSectionResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        ExtractableResponse<Response> findLineResponse = findLine(lineId);
        assertThat(getStations((findLineResponse))).hasSize(3);
        assertThat(getStationNames(findLineResponse)).contains("방배역", "서초역", "강남역");
    }

    /**
     *  Given 지하철 노선을 생성하고
     *  When 새로운 역을 하행 종점으로 등록할 경우
     *  Then 노선의 하행 종점이 변경된다.
     */
    @DisplayName("새로운 역을 하행 종점으로 등록한다.")
    @Test
    void addSectionEndOfLine() {
        // given
        Long lineId = getId(createLine("2호선", "bg-green-600", 20, 서초역, 강남역));

        // when
        Long 역삼역 = getId(createStation("역삼역"));
        ExtractableResponse<Response> createSectionResponse = addSection(lineId, 강남역, 역삼역, 10);
        assertThat(createSectionResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        ExtractableResponse<Response> findLineResponse = findLine(lineId);
        assertThat(getStations(findLineResponse)).hasSize(3);
        assertThat(getStationNames(findLineResponse)).contains("서초역", "강남역", "역삼역");
    }

    /**
     *  Given 지하철 노선을 생성하고
     *  When 역 사이의 거리가 기존 역 사이의 거리보다 크거나 같은 경우
     *  Then 등록할 수 없다
     */
    @DisplayName("추가하려는 역 사이의 거리가 기존 역 사이의 거리보다 크거나 같다면 등록할 수 없다")
    @Test
    void addSectionGreaterThanExistDistance() {
        // given
        Long lineId = getId(createLine("2호선", "bg-green-600", 20, 서초역, 강남역));

        // when
        Long 교대역 = getId(createStation("교대역"));
        ExtractableResponse<Response> createSectionResponse = addSection(lineId, 서초역, 교대역, 20);

        // then
        assertThat(createSectionResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     *  Given 지하철 노선을 생성하고
     *  When 이미 노선에 등록된 구간(상행, 하행)을 등록할 경우
     *  Then 등록할 수 없다
     */
    @DisplayName("이미 노선에 존재하는 구간을 등록 할 수 없다")
    @Test
    void addSectionDuplicateInLine() {
        // given
        Long lineId = getId(createLine("2호선", "bg-green-600", 20, 서초역, 강남역));

        // when
        ExtractableResponse<Response> createSectionResponse = addSection(lineId, 서초역, 강남역, 15);

        // then
        assertThat(createSectionResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     *  Given 지하철 노선을 생성하고
     *  When 노선에 포함되지 않는 역이 있는 구간을 등록할 경우
     *  Then 등록할 수 없다
     */
    @DisplayName("노선에 존재하지 않는 역을 포함한 구간은 등록할 수 없다")
    void addSectionNotExistStationInLine() {

    }
}
