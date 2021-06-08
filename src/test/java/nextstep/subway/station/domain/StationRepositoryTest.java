package nextstep.subway.station.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class StationRepositoryTest {

    @Autowired
    private StationRepository stationRepository;

    @DisplayName("등록된 Station ID 를 이용해서 in 조건으로 검색하면 동일한 Station 이 조회되는지 테스트")
    @Test
    void given_StationsPersisted_when_FindByIdInClause_then_ReturnStationsPersisted() {
        // given
        final Station 강남역 = stationRepository.save(new Station("강남역"));
        final Station 삼성역 = stationRepository.save(new Station("삼성역"));
        final List<Long> ids = Arrays.asList(강남역.getId(), 삼성역.getId());
        final List<Station> expected = Arrays.asList(강남역, 삼성역);

        // when
        final List<Station> actual = stationRepository.findByIdIn(ids);

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
