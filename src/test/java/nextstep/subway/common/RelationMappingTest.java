package nextstep.subway.common;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.SectionRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
public class RelationMappingTest {
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Test
    @DisplayName("동일성 보장")
    void identity() {
        final Station station1 = stationRepository.save(new Station("양재시민의 숲"));
        final Station station2 = stationRepository.findById(station1.getId()).get();
        assertThat(station1 == station2).isTrue();

        final Station station3 = stationRepository.findByName("양재시민의 숲");
        assertThat(station1 == station3).isTrue();
    }

    @Test
    void update() {
        final Station station1 = stationRepository.save(new Station("양재시민의 숲"));
        station1.changeName("상현");
        final Station station2 = stationRepository.findByName("상현");
        assertThat(station2).isNotNull();
    }

    @Test
    void 노선에_구간_정보_저장() {
        final Line line1 = new Line("신분당선", "pink");
        final Line line2 = lineRepository.save(line1);
        final Station station1 = stationRepository.save(new Station("양재시민의 숲"));
        final Station station2 = stationRepository.save(new Station("상현"));

        Section section1 = new Section(station1, station2, 50);
        line2.addSection(section1);

        Line actual = lineRepository.findByName("신분당선");

        assertThat(actual.getSections()).hasSize(1);

        assertThat(actual.getSections().get(0).getUpStation()).isEqualTo(station1);
    }

    @Test
    void 노선에_구간_정보_지하철역_삭제() {
        final Line line1 = new Line("신분당선", "pink");
        final Line line2 = lineRepository.save(line1);
        final Station station1 = stationRepository.save(new Station("양재시민의 숲"));
        final Station station2 = stationRepository.save(new Station("상현"));

        Section section1 = new Section(station1, station2, 50);
        line2.addSection(section1);

        final Station station3 = stationRepository.save(new Station("판교"));
        Section section2 = new Section(station1, station3, 30);
        line2.addSection(section2);
        lineRepository.flush();

        Line actual = lineRepository.findByName("신분당선");


        assertThat(actual.getSections()).hasSize(2);
        assertThat(actual.getSections().get(0).getUpStation()).isEqualTo(station1);
        assertThat(actual.getSections().get(0).getDownStation()).isEqualTo(station3);
        assertThat(actual.getSections().get(1).getUpStation()).isEqualTo(station3);
        assertThat(actual.getSections().get(1).getDownStation()).isEqualTo(station2);

        actual.deleteSection(station3);
        assertThat(actual.getSections()).hasSize(1);
        assertThat(actual.getSections().get(0).getUpStation()).isEqualTo(station1);
        assertThat(actual.getSections().get(0).getDownStation()).isEqualTo(station2);
        assertThat(actual.getSections().get(0).getDistance()).isEqualTo(50);

    }

    @Test
    void 구간이_하나인_노선에서_마지막_구간을_제거할_수_없음() {
        final Line line1 = new Line("신분당선", "pink");
        final Line line2 = lineRepository.save(line1);
        final Station station1 = stationRepository.save(new Station("양재시민의 숲"));
        final Station station2 = stationRepository.save(new Station("상현"));

        Section section1 = new Section(station1, station2, 50);
        line2.addSection(section1);

        Line actual = lineRepository.findByName("신분당선");

        assertThatThrownBy(() -> {
            actual.deleteSection(station2);
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("구간이 하나인 지하철역은 삭제할 수 없습니다!");

        assertThatThrownBy(() -> {
            actual.deleteSection(station1);
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("구간이 하나인 지하철역은 삭제할 수 없습니다!");

    }
}
