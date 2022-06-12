package nextstep.subway.domain.line;

import nextstep.subway.domain.common.Distance;
import nextstep.subway.domain.section.Section;
import nextstep.subway.domain.station.Station;
import nextstep.subway.domain.station.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class LineTest {

    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private EntityManager em;

    private Station upStation;
    private Station downStation;
    private Line line;

    @BeforeEach
    void createLine() {
        upStation = stationRepository.save(Station.create("당고개역"));
        downStation = stationRepository.save(Station.create("오이도역"));

        em.flush();
        em.clear();

        line = Line.create(
                LineName.of("4호선"),
                LineColor.of("하늘색"),
                Distance.of(20),
                upStation,
                downStation
        );

        line = lineRepository.save(line);

        em.flush();
        em.clear();
    }

    @DisplayName("노선을 생성한다.")
    @Test
    void setTerminus() {
        // then
        assertThat(line.getUpStation()).isEqualTo(upStation);
        assertThat(line.getDownStation()).isEqualTo(downStation);
    }

    @DisplayName("동일 구간을 추가하면 에러가 발생한다.")
    @Test
    void addSection_동일구간_추가() {
        // given
        Section section = Section.create(upStation, downStation, Distance.of(20));

        // when, then
        assertThatThrownBy(() -> line.addSection(section))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("노선 생성 후 역 사이에 새로운 구간을 추가한다.")
    @Test
    void addSection_구간추가() {
        //given
        Station newStation = stationRepository.save(Station.create("상록수역"));

        em.flush();
        em.clear();

        // when
        Section newSection = Section.create(newStation, downStation, Distance.of(5));
        line.addSection(newSection);

        //then
        assertThat(line.getSections().getSections()).hasSize(2);
        assertThat(line.getDistance().getValue()).isEqualTo(20);
    }

    @ParameterizedTest(name = "기존 역 사이 길이보다 크거나 같으면 추가할 수 없음")
    @ValueSource(ints = {20, 21})
    void addSection_구간길이가_크거나_같으면_추가할_수_없다(int distance) {
        //given
        Station newStation = stationRepository.save(Station.create("상록수역"));

        em.flush();
        em.clear();

        // when
        Section newSection = Section.create(newStation, downStation, Distance.of(distance));

        //then
        assertThatThrownBy(() -> line.addSection(newSection))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void addSection_상행역_하행역_둘다_포함_안된_경우_추가할_수_없다() {
        //given
        Station newUpStation = stationRepository.save(Station.create("상록수역"));
        Station newDownStation = stationRepository.save(Station.create("한양대역"));

        em.flush();
        em.clear();

        // when
        Section newSection = Section.create(newUpStation, newDownStation, Distance.of(5));

        //then
        assertThatThrownBy(() -> line.addSection(newSection))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있으면 추가할 수 없음")
    @Test
    void addSection_상행역_하행역_둘다_포함된_경우_추가할_수_없다() {
        //given
        Station newStation = stationRepository.save(Station.create("상록수역"));
        Section newSection = Section.create(newStation, downStation, Distance.of(5));
        line.addSection(newSection);

        em.flush();
        em.clear();

        // when, then
        assertThatThrownBy(() -> line.addSection(Section.create(downStation, upStation, Distance.of(20))))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("노선 생성 후 새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void addSection_새로운_역을_상행_종점으로() {
        //given
        Station newUpStation = stationRepository.save(Station.create("상록수역"));

        em.flush();
        em.clear();

        // when
        Section newSection = Section.create(newUpStation, upStation, Distance.of(5));
        line.addSection(newSection);

        //then
        assertThat(line.getSections().getSections()).hasSize(2);
        assertThat(line.getUpStation()).isEqualTo(newUpStation);
        assertThat(line.getDistance().getValue()).isEqualTo(25);
    }

    @DisplayName("노선 생성 후 새로운 역을 하행 종점으로 등록할 경우")
    @Test
    void addSection_새로운_역을_하행_종점으로() {
        //given
        Station newDownStation = stationRepository.save(Station.create("상록수역"));

        em.flush();
        em.clear();

        // when
        Section newSection = Section.create(downStation, newDownStation, Distance.of(5));
        line.addSection(newSection);

        //then
        assertThat(line.getSections().getSections()).hasSize(2);
        assertThat(line.getDownStation()).isEqualTo(newDownStation);
        assertThat(line.getDistance().getValue()).isEqualTo(25);
    }
}