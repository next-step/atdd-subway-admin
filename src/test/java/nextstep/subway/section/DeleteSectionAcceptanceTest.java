package nextstep.subway.section;

import static nextstep.subway.fixtures.StationTestFixture.setStations;
import static nextstep.subway.section.DeleteSectionTestFixtures.경기광주역_모란역_구간만_조회된다;
import static nextstep.subway.section.DeleteSectionTestFixtures.노선이_경기광주역_모란역_중앙역_순서로_등록되어_있다;
import static nextstep.subway.section.DeleteSectionTestFixtures.중앙역을_제거한다;

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
    void deleteLastDownStation() {
        //given
        String lineId = 노선이_경기광주역_모란역_중앙역_순서로_등록되어_있다();

        //when
        중앙역을_제거한다(lineId);

        //then
        경기광주역_모란역_구간만_조회된다(lineId);
    }


}
