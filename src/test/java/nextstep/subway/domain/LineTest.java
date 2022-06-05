package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.subway.dto.SectionResponse;
import nextstep.subway.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LineTest {
    private Line line;
    private Station finalUpStation;
    private Station finalDownStation;
    private Long lineDistance;

    @BeforeEach
    void setUp() {
        line = new Line("신분당선", "bg-red-600");
        finalUpStation = new Station("강남역");
        finalDownStation = new Station("정자역");
        lineDistance = 30L;
    }

    @Test
    void 이름과_색상으로_노선이_생성되어야_한다() {
        // when
        final Line line = new Line("이름", "색상");

        // then
        assertThat(line).isNotNull();
        assertThat(line).isInstanceOf(Line.class);
    }

    @Test
    void 상행종점과_하행종점을_설정할_수_있어야_한다() {
        // when
        line.setFinalStations(finalUpStation, finalDownStation, lineDistance);

        // then
        assertThat(line.getSections().sections())
                .containsExactly(SectionResponse.of(new Section(line, finalUpStation, finalDownStation, lineDistance)));
        assertThat(line.getLineStations().stations())
                .containsExactly(StationResponse.of(finalUpStation), StationResponse.of(finalDownStation));
    }

    @Test
    void 노선을_수정할_수_있어야_한다() {
        // given
        final String newName = "수정된이름";
        final String newColor = "수정된색상";

        // when
        line.update(newName, newColor);

        // then
        assertThat(line.getName()).isEqualTo(newName);
        assertThat(line.getColor()).isEqualTo(newColor);
    }

    @Test
    void 하행종점역이_아닌_기존_역을_상행역으로_새로운_구간을_추가할_수_있어야_한다() {
        // given
        line.setFinalStations(finalUpStation, finalDownStation, lineDistance);
        final Station newStation = new Station("양재역");
        final Long distance = 10L;

        // when
        line.relateToSection(finalUpStation, newStation, distance);

        // then
        final Section section = line.getSections().getByUpStation(finalUpStation);
        assertNewSection(section, line, finalUpStation, newStation, distance);
        assertThat(line.getLineStations().stations()).contains(StationResponse.of(newStation));
        assertThat(line.getSections().getByDownStation(finalDownStation).getDistance())
                .isEqualTo(lineDistance - distance);
    }

    @Test
    void 상행종점역이_아닌_기존_역을_하행역으로_새로운_구간을_추가할_수_있어야_한다() {
        // given
        line.setFinalStations(finalUpStation, finalDownStation, lineDistance);
        final Station newStation = new Station("양재역");
        final Long distance = 10L;

        // when
        line.relateToSection(newStation, finalDownStation, distance);

        // then
        final Section section = line.getSections().getByUpStation(newStation);
        assertNewSection(section, line, newStation, finalDownStation, distance);
        assertThat(line.getLineStations().stations()).contains(StationResponse.of(newStation));
        assertThat(line.getSections().getByUpStation(finalUpStation).getDistance())
                .isEqualTo(lineDistance - distance);
    }

    @Test
    void 상행종점역을_하행역으로_새로운_구간을_추가할_수_있어야_한다() {
        // given
        line.setFinalStations(finalUpStation, finalDownStation, lineDistance);
        final Station newStation = new Station("신논현역");
        final Long distance = 10L;

        // when
        line.relateToSection(newStation, finalUpStation, distance);

        // then
        final Section section = line.getSections().getByUpStation(newStation);
        assertNewSection(section, line, newStation, finalUpStation, distance);
        assertThat(line.getLineStations().stations()).contains(StationResponse.of(newStation));
    }

    @Test
    void 하행종점역을_상행역으로_새로운_구간을_추가할_수_있어야_한다() {
        // given
        line.setFinalStations(finalUpStation, finalDownStation, lineDistance);
        final Station newStation = new Station("미금역");
        final Long distance = 10L;

        // when
        line.relateToSection(finalDownStation, newStation, distance);

        // then
        final Section section = line.getSections().getByUpStation(finalDownStation);
        assertNewSection(section, line, finalDownStation, newStation, distance);
        assertThat(line.getLineStations().stations()).contains(StationResponse.of(newStation));
    }

    private void assertNewSection(final Section newSection,
                                  final Line line,
                                  final Station upStation,
                                  final Station downStation,
                                  final Long distance) {
        assertThat(newSection).isNotNull();
        assertThat(newSection.getLine()).isEqualTo(line);
        assertThat(newSection.getUpStation()).isEqualTo(upStation);
        assertThat(newSection.getDownStation()).isEqualTo(downStation);
        assertThat(newSection.getDistance()).isEqualTo(distance);
    }
}
