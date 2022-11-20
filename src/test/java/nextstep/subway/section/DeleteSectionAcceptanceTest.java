package nextstep.subway.section;

import static nextstep.subway.fixtures.StationTestFixture.setStations;
import static nextstep.subway.section.DeleteSectionTestFixtures.경기광주역_모란역_구간만_조회된다;
import static nextstep.subway.section.DeleteSectionTestFixtures.경기광주역_미금역_구간만_등록되어_있다;
import static nextstep.subway.section.DeleteSectionTestFixtures.경기광주역_중앙역_구간으로_합쳐지며_길이도_합쳐진다;
import static nextstep.subway.section.DeleteSectionTestFixtures.노선이_경기광주역_모란역_중앙역_순서로_등록되어_있다;
import static nextstep.subway.section.DeleteSectionTestFixtures.모란역을_제거한다;
import static nextstep.subway.section.DeleteSectionTestFixtures.미금역을_제거하려_하면;
import static nextstep.subway.section.DeleteSectionTestFixtures.제거할_수_없다;
import static nextstep.subway.section.DeleteSectionTestFixtures.중앙역을_제거한다;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.domain.repository.StationRepository;
import nextstep.subway.fixtures.TestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("구간 제거 관련 기능")
class DeleteSectionAcceptanceTest extends TestFixtures {

    @Autowired
    StationRepository stationRepository;

    @BeforeEach
    void beforeEach() {
        setStations(stationRepository);
    }

    /**
     * Given 한 노선에 두개의 구간이 등록되어 있다.
     * <p>
     * When 두 구간에서 가장 마지막 역을 제거하면
     * <p>
     * Then 한 구간 만 조회된다.
     */
    @DisplayName("한 노선에 두개의 구간이 등록 된 상태에서 가장 마지막역을 제거하는 경우")
    @Test
    void deleteLastSectionsAndDownStation() {
        //given
        String lineId = 노선이_경기광주역_모란역_중앙역_순서로_등록되어_있다();

        //when
        중앙역을_제거한다(lineId);

        //then
        경기광주역_모란역_구간만_조회된다(lineId);
    }

    /**
     * Given 한 노선에 두개의 구간이 등록되어 있다.
     * <p>
     * When 앞 구간의 하행역이며 뒷 구간의 상행역에 해당하는 가운데 역을 제거하면
     * <p>
     * Then 앞 구간의 상행역과 뒷 구간의 하행역이 한 구간으로 합쳐지며 구간의 길이도 합쳐진다.
     */
    @DisplayName("한 노선에 두개의 구간이 등록 된 상태에서 가운데 역을 제거하는 경우")
    @Test
    void deleteBetweenStationOfSections() {
        //given
        String lineId = 노선이_경기광주역_모란역_중앙역_순서로_등록되어_있다();

        //when
        모란역을_제거한다(lineId);

        //then
        경기광주역_중앙역_구간으로_합쳐지며_길이도_합쳐진다(lineId);
    }

    /**
     * Given 한 노선에 두개의 구간이 등록되어 있다.
     * <p>
     * When 어느 구간에도 속하지 않은 역을 제거하려고 시도하면
     * <p>
     * Then 제거할 수 없다.
     */
    @DisplayName("노선에 등록되어있지 않은 역을 제거하려 하는 경우 제거할_수_없다.")
    @Test
    void deleteNotExistsStationOfSections() {
        //given
        String lineId = 노선이_경기광주역_모란역_중앙역_순서로_등록되어_있다();

        //when
        ExtractableResponse<Response> response = 미금역을_제거하려_하면(lineId);

        //then
        제거할_수_없다(response);
    }

    /**
     * Given 구간이 하나인 노선이 등록되어 있다.
     * <p>
     * When 해당 노선의 상행 혹은 하행에 해당하는 역을 제거하려고 시도하면
     * <p>
     * Then 제거할 수 없다.
     */
    @DisplayName("구간이 하나인 노선의 상행 혹은 하행에 해당하는 역을 제거하려 하면 제거할 수 없다.")
    @Test
    void deleteStationOfOneSectionThenThrow() {
        //given
        String lineId = 경기광주역_미금역_구간만_등록되어_있다();

        //when
        ExtractableResponse<Response> response = 미금역을_제거하려_하면(lineId);

        //then
        제거할_수_없다(response);
    }
}
