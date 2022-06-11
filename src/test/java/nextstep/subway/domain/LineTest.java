package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        assertThat(line.getLineStations().stations())
                .containsExactly(StationResponse.of(finalUpStation), StationResponse.of(finalDownStation));
        assertThat(line.getLineStations().getByStation(finalUpStation).get().getPrevious()).isNull();
        assertThat(line.getLineStations().getByStation(finalDownStation).get().getNext()).isNull();
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
    void 등록할_구간의_거리가_0보다_크지_않으면_IllegalStatementException이_발생해야_한다() {
        // given
        line.setFinalStations(finalUpStation, finalDownStation, lineDistance);

        // when and then
        assertThatThrownBy(() -> line.registerSection(finalDownStation, new Station("new"), 0L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 상행선_하행선이_이미_등록된_구간을_등록하면_IllegalStatementException이_발생해야_한다() {
        // given
        line.setFinalStations(finalUpStation, finalDownStation, lineDistance);

        // when and then
        assertThatThrownBy(() -> line.registerSection(finalDownStation, finalUpStation, lineDistance))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 상행선_하행선이_하나도_등록되지_않은_구간을_등록하면_IllegalStatementException이_발생해야_한다() {
        // given
        line.setFinalStations(finalDownStation, finalUpStation, lineDistance);

        // when and then
        assertThatThrownBy(() -> line.registerSection(new Station("new1"), new Station("new2"), lineDistance))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 하행종점역이_아닌_기존_역을_상행역으로_새로운_구간을_등록할_수_있어야_한다() {
        // given
        line.setFinalStations(finalUpStation, finalDownStation, lineDistance);
        final Station newStation = new Station("양재역");
        final Long distance = 10L;

        // when
        final SectionResponse sectionResponse = line.registerSection(finalUpStation, newStation, distance);

        // then
        assertRegisteredSection(sectionResponse, finalUpStation, newStation, distance);
        assertThat(line.getLineStations().hasRelationTo(newStation)).isTrue();
    }

    @Test
    void 상행종점역이_아닌_기존_역을_하행역으로_새로운_구간을_추가할_수_있어야_한다() {
        // given
        line.setFinalStations(finalUpStation, finalDownStation, lineDistance);
        final Station newStation = new Station("양재역");
        final Long distance = 10L;

        // when
        final SectionResponse sectionResponse = line.registerSection(newStation, finalDownStation, distance);

        // then
        assertRegisteredSection(sectionResponse, newStation, finalDownStation, distance);
        assertThat(line.getLineStations().hasRelationTo(newStation)).isTrue();
    }

    @Test
    void 상행종점역을_하행역으로_새로운_구간을_추가할_수_있어야_한다() {
        // given
        line.setFinalStations(finalUpStation, finalDownStation, lineDistance);
        final Station newStation = new Station("신논현역");
        final Long distance = 10L;

        // when
        final SectionResponse sectionResponse = line.registerSection(newStation, finalUpStation, distance);

        // then
        assertRegisteredSection(sectionResponse, newStation, finalUpStation, distance);
        assertThat(line.getLineStations().hasRelationTo(newStation)).isTrue();
    }

    @Test
    void 하행종점역을_상행역으로_새로운_구간을_추가할_수_있어야_한다() {
        // given
        line.setFinalStations(finalUpStation, finalDownStation, lineDistance);
        final Station newStation = new Station("미금역");
        final Long distance = 10L;

        // when
        final SectionResponse sectionResponse = line.registerSection(finalDownStation, newStation, distance);

        // then
        assertRegisteredSection(sectionResponse, finalDownStation, newStation, distance);
        assertThat(line.getLineStations().hasRelationTo(newStation)).isTrue();
    }

    private void assertRegisteredSection(final SectionResponse newSection,
                                         final Station upStation,
                                         final Station downStation,
                                         final Long distance) {
        assertThat(newSection).isNotNull();
        assertThat(newSection.getLineName()).isEqualTo(line.getName());
        assertThat(newSection.getUpStationName()).isEqualTo(upStation.getName());
        assertThat(newSection.getDownStationName()).isEqualTo(downStation.getName());
        assertThat(newSection.getDistance()).isEqualTo(distance);
    }
}
