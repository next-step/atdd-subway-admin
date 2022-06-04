package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import nextstep.subway.section.repository.SectionRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.repository.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class SectionTest {

    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    StationRepository stationRepository;
    @Autowired
    SectionRepository sectionRepository;

    private static Station upStation = new Station("신림역");
    private static Station downStation = new Station("서울대입구역");
    private static Station newUpStation = new Station("봉천역");
    @BeforeEach
    public void setUp() {
        upStation = new Station("신림역");
        downStation = new Station("서울대입구역");
        newUpStation = new Station("봉천역");
        stationRepository.saveAll(Arrays.asList(upStation, downStation, newUpStation));
    }

    @ParameterizedTest(name = "{0}")
    @DisplayName("구간이 만들어질 때 예외처리를 검사한다.")
    @MethodSource("providerCreateSection_validateCase")
    void createSection_validate(String name, Station upStation, Station downStation) {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> new Section(upStation, downStation, new Distance(5L)));
    }

    @Test
    @DisplayName("구간이 정상적으로 생성된다.")
    void createSection() {
        Section section = new Section(upStation, downStation, new Distance(5L));
        sectionRepository.save(section);

        entityManager.clear();
        Optional<Section> optionalSection = sectionRepository.findById(section.getId());

        assertThat(optionalSection).isPresent();
    }

    @Test
    @DisplayName("새로운 구간 추가될 때, 추가되는 거리가 현재보다 크다면 예외처리된다.")
    void changeSection_fail() {
        Section section = new Section(upStation, downStation, new Distance(10L));

        assertThatIllegalArgumentException()
            .isThrownBy(() -> section.changeSection(new Section(upStation, newUpStation, new Distance(11L))));
    }

    @Test
    @DisplayName("추가되는 구간이 상행구간이라면, 현재 구간의 상행역을 추가되는 구간의 하행역으로 변경한다.")
    void changeSection_ByUpSection() {
        Section section = new Section(upStation, downStation, new Distance(10L));

        section.changeSection(new Section(upStation, newUpStation, new Distance(5L)));

        assertThat(section.getUpStation().isSame(newUpStation)).isTrue();
    }

    @Test
    @DisplayName("추가되는 구간이 하행구간이라면, 현재 구간의 하행역을 추가되는 구간의 상행역으로 변경한다.")
    void changeSection_ByDownSection() {
        Section section = new Section(upStation, downStation, new Distance(10L));

        section.changeSection(new Section(newUpStation, downStation, new Distance(5L)));

        assertThat(section.getDownStation().isSame(newUpStation)).isTrue();
    }

    static Stream<Arguments> providerCreateSection_validateCase() {
        return Stream.of(
            Arguments.of(
                "상행역이 존재하지 않을 경우",
                null, downStation
            ),
            Arguments.of(
                "하행역이 존재하지 않을 경우",
                upStation, null
            ),
            Arguments.of(
                "상행역과 하행역이 같을 경우",
                upStation, upStation
            )
        );
    }

}
