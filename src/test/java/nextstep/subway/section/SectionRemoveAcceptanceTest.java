package nextstep.subway.section;

import static nextstep.subway.helper.JsonPathExtractor.getId;
import static nextstep.subway.helper.JsonPathExtractor.getStationNames;
import static nextstep.subway.helper.JsonPathExtractor.getStations;
import static nextstep.subway.line.LineAcceptanceTestFixture.createLine;
import static nextstep.subway.line.LineAcceptanceTestFixture.findLine;
import static nextstep.subway.section.SectionAcceptanceTestFixture.addSection;
import static nextstep.subway.section.SectionAcceptanceTestFixture.deleteSection;
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

@DisplayName("지하철 구간 제거 관련 기능")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SectionRemoveAcceptanceTest extends AcceptanceTest {
    private Long 서초역;
    private Long 교대역;
    private Long 강남역;
    private Long 이호선;

    @BeforeEach
    void stationSetUp(){
        서초역 = getId(createStation("서초역"));
        교대역 = getId(createStation("교대역"));
        강남역 = getId(createStation("강남역"));
        이호선 = getId(createLine("2호선", "bg-green-600", 20, 서초역, 강남역));
    }


    /**
     *  Given 지하철 노선(A-B-C)을 생성하고
     *  When 지하철 노선의 상행 종점(A)를 제거할 경우
     *  Then 지하철 노선의 상행 종점은 B가 된다.
     */
    @DisplayName("상행 종점을 제거한다.")
    @Test
    void removeSectionOfHeadLine() {
        // given
        addSection(이호선, 서초역, 교대역, 5);

        // when
        ExtractableResponse<Response> removeSectionResponse = deleteSection(이호선, 서초역);
        assertThat(removeSectionResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        // then
        ExtractableResponse<Response> findLineResponse = findLine(이호선);
        assertThat(getStations((findLineResponse))).hasSize(2);
        assertThat(getStationNames(findLineResponse)).containsExactly("교대역", "강남역");
    }

    /**
     *  Given 지하철 노선(A-B-C)을 생성하고
     *  When 지하철 노선의 하행 종점(C)를 제거할 경우
     *  Then 지하철 노선의 하행 종점은 B가 된다.
     */
    @DisplayName("하행 종점을 제거한다.")
    @Test
    void removeSectionEndOfLine() {

    }

    /**
     *  Given 지하철 노선(A-B-C)을 생성하고
     *  When 지하철 노선의 중간역(B)를 제거할 경우
     *  Then 지하철 노선이 (A-C)로 재조정된다.
     */
    @DisplayName("중간 역을 제거한다.")
    @Test
    void deleteMiddleStation() {
        // given
        addSection(이호선, 서초역, 교대역, 5);

        // when
        ExtractableResponse<Response> removeSectionResponse = deleteSection(이호선, 교대역);
        assertThat(removeSectionResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        // then
        ExtractableResponse<Response> findLineResponse = findLine(이호선);
        assertThat(getStations((findLineResponse))).hasSize(2);
        assertThat(getStationNames(findLineResponse)).containsExactly("서초역", "강남역");
    }

    /**
     *  When 구간이 하나인 지하철 노선을 제거 하려는 경우
     *  Then 제거할 수 없다.
     */
    @DisplayName("구간이 하나인 지하철 노선은 제거할 수 없다.")
    @Test
    void removeSectionOnlyOneExist() {

    }

    /**
     *  When 노선에 포함되지 않는 역을 제거하려는 경우
     *  Then 제거할 수 없다.
     */
    @DisplayName("노선에 포함되지 않는 역을 제거할 수 없다.")
    @Test
    void removeStationNotContainsLine() {

    }
}
