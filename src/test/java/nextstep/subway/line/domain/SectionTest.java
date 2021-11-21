package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

@DataJpaTest
public class SectionTest {
    @Autowired
    private StationRepository stationRepository;

    @AfterEach
    void tearDown() {
        stationRepository.deleteAll();
    }

    @Test
    void isNextSection() {
        // given
        final Station 송내역 = stationRepository.save(new Station("송내역"));
        final Station 부천역 = stationRepository.save(new Station("부천역"));
        final Station 신도림역 = stationRepository.save(new Station("신도림역"));
        final Section section1 = new Section(송내역, 부천역, 10);
        final Section section2 = new Section(부천역, 신도림역, 10);

        // when, then
        assertThat(section2.isNextSection(section1)).isTrue();
        assertThat(section1.isNextSection(section2)).isFalse();
    }

    @Test
    void isOverlapped() {
        // given
        final Station 인천역 = stationRepository.save(new Station("인천역"));
        final Station 송내역 = stationRepository.save(new Station("송내역"));
        final Station 부천역 = stationRepository.save(new Station("부천역"));
        final Station 신도림역 = stationRepository.save(new Station("신도림역"));
        final Section section1 = new Section(인천역, 송내역, 10);
        final Section section2 = new Section(송내역, 신도림역, 20);
        final Section section3 = new Section(부천역, 신도림역, 10);

        // when, then
        assertThat(section1.isOverlapped(section2)).isFalse();
        assertThat(section2.isOverlapped(section3)).isTrue();
    }

    @Test
    void divideBy() {
        // given
        final Station 송내역 = stationRepository.save(new Station("송내역"));
        final Station 부천역 = stationRepository.save(new Station("부천역"));
        final Station 신도림역 = stationRepository.save(new Station("신도림역"));
        final Section section1 = new Section(송내역, 신도림역, 11);
        final Section section2 = new Section(부천역, 신도림역, 10);

        // when
        section1.divideBy(section2);

        // then
        assertThat(section1.getDownStation()).isEqualTo(부천역);
        assertThat(section1.getDistance()).isEqualTo(1);
    }
}
