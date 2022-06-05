package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.subway.dto.SectionResponse;
import org.junit.jupiter.api.Test;

public class LineTest {
    @Test
    void 이름과_색상으로_노선이_생성되어야_한다() {
        // when
        final Line line = givenLine();

        // then
        assertThat(line).isNotNull();
        assertThat(line).isInstanceOf(Line.class);
    }

    @Test
    void 노선에_구간이_없을_때_구간을_추가할_수_있어야_한다() {
        // given
        final Line line = givenLine();
        final Station finalUpStation = new Station("강남역");
        final Station finalDownStation = new Station("정자역");
        final Long lineDistance = 30L;
        final Section section = new Section(line, finalUpStation, finalDownStation, lineDistance);

        // when
        line.addSection(section);

        // then
        assertThat(line.getSections().sections()).contains(SectionResponse.of(section));
    }

//    @Test
//    void 지하철역과의_연관관계를_추가할_수_있어야_한다() {
//        // given
//        final Station station1 = new Station("강남역");
//        final Station station2 = new Station("양재역");
//        final Line line = new Line("신분당선", "bg-red-600");
//
//        // when
//        line.relateToStation(new LineStation(line, station1));
//        line.relateToStation(new LineStation(line, station2));
//
//        // then
//        assertThat(line.getLineStations().getStations()).containsExactly(station1, station2);
//    }
//
//    @Test
//    void 다른_노선과_지하철역의_연관관계를_추가하면_IllegalArgumentException이_발생해야_한다() {
//        // given
//        final Line line = new Line("신분당선", "bg-red-600");
//        final LineStation lineStation = new LineStation(
//                new Line("2호천", "bg-green-600"),
//                new Station("강남역")
//        );
//
//        // when and then
//        assertThatThrownBy(() -> line.relateToStation(lineStation))
//                .isInstanceOf(IllegalArgumentException.class);
//    }

    @Test
    void 노선을_수정할_수_있어야_한다() {
        // given
        final Line line = givenLine();
        final String newName = "수정된이름";
        final String newColor = "bg-modified-600";

        // when
        line.update(newName, newColor);

        // then
        assertThat(line.getName()).isEqualTo(newName);
        assertThat(line.getColor()).isEqualTo(newColor);
    }

    private Line givenLine() {
        return new Line("신분당선", "bg-red-600");
    }
}
