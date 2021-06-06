package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class SectionRepositoryTest {

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @DisplayName("구간 저장")
    @Test
    void save() {
        // given
        Station upStation = new Station("강남");
        Station persistUpStation = stationRepository.save(upStation);
        Station downStation = new Station("광교");
        Station persistDownStation = stationRepository.save(downStation);

        // when
        Section section = new Section(persistUpStation, persistDownStation, 45);
        Section persistSection = sectionRepository.save(section);
        Section actual = sectionRepository.findById(persistSection.getId()).orElseThrow(IllegalArgumentException::new);

        // then
        Assertions.assertThat(persistSection).isSameAs(actual);
    }
}