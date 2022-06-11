package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import nextstep.subway.dto.SectionResponse;
import nextstep.subway.dto.StationResponse;
import org.junit.jupiter.api.Test;

class LineStationsTest {
    private final Line line = new Line("신분당선", "bg-red-600");
    private final Station station1 = new Station("강남역");
    private final Station station2 = new Station("판교역");
    private final long from1To2 = 30L;

    @Test
    void 상행종점역과_하행종점역을_등록할_수_있어야_한다() {
        // given
        final LineStations lineStations = new LineStations();

        // when
        lineStations.addFinalStations(line, station1, station2, from1To2);

        // then
        assertThat(lineStations.stations()).containsExactly(StationResponse.of(station1), StationResponse.of(station2));
    }

    @Test
    void 상행역인_신규역_추가_시_기존_구간의_거리보다_신규_구간의_거리가_짧지_않으면_IllegalArgumentException이_발생해야_한다() {
        // given
        final LineStations lineStations = newGivenLineStations();
        final Station upStation = new Station("양재역");

        // when and then
        assertThatThrownBy(() -> lineStations.addUpStation(line, upStation, station2, from1To2))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 상행역인_신규역_추가_시_기존_구간이_존재하면_기존_구간이_수정되어야_한다() {
        // given
        final LineStations lineStations = newGivenLineStations();
        final Station upStation = new Station("양재역");
        final long distance = 10L;

        // when
        lineStations.addUpStation(line, upStation, station2, distance);

        // then
        assertThat(lineStations.sections())
                .containsOnly(
                        new SectionResponse(line.getName(), station1.getName(), upStation.getName(),
                                from1To2 - distance),
                        new SectionResponse(line.getName(), upStation.getName(), station2.getName(), distance));
    }

    @Test
    void 상행역인_신규역_추가_시_하행역이_기존_상행종점역이어도_등록이되어야_한다() {
        // given
        final LineStations lineStations = newGivenLineStations();
        final Station upStation = new Station("신논현역");
        final long distance = 50L;

        // when
        lineStations.addUpStation(line, upStation, station1, distance);

        // then
        assertThat(lineStations.sections())
                .containsOnly(
                        new SectionResponse(line.getName(), upStation.getName(), station1.getName(), distance),
                        new SectionResponse(line.getName(), station1.getName(), station2.getName(), from1To2));
    }

    @Test
    void 하행역인_신규역_추가_시_기존_구간의_거리보다_신규_구간의_거리가_짧지_않으면_IllegalArgumentException이_발생해야_한다() {
        // given
        final LineStations lineStations = newGivenLineStations();
        final Station downStation = new Station("양재역");

        // when and then
        assertThatThrownBy(() -> lineStations.addDownStation(line, station1, downStation, from1To2))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 하행역인_신규역_추가_시_기존_구간이_존재하면_기존_구간이_수정되어야_한다() {
        // given
        final LineStations lineStations = newGivenLineStations();
        final Station downStation = new Station("양재역");
        final long distance = 10L;

        // when
        lineStations.addDownStation(line, station1, downStation, distance);

        // then
        assertThat(lineStations.sections())
                .containsOnly(
                        new SectionResponse(line.getName(), station1.getName(), downStation.getName(), distance),
                        new SectionResponse(line.getName(), downStation.getName(), station2.getName(),
                                from1To2 - distance));
    }

    @Test
    void 하행역인_신규역_추가_시_상행역이_기존_하행종점역이어도_등록이되어야_한다() {
        // given
        final LineStations lineStations = newGivenLineStations();
        final Station downStation = new Station("정자역");
        final long distance = 50L;

        // when
        lineStations.addDownStation(line, station2, downStation, distance);

        // then
        assertThat(lineStations.sections())
                .containsOnly(
                        new SectionResponse(line.getName(), station1.getName(), station2.getName(), from1To2),
                        new SectionResponse(line.getName(), station2.getName(), downStation.getName(), distance));
    }

    @Test
    void 연관관계를_추가할_수_있어야_한다() {
        // given
        final LineStations lineStations = new LineStations();
        final LineStation lineStation = new LineStation(line, station1, station2, from1To2);

        // when
        lineStations.add(lineStation);

        // then
        assertThat(lineStations.stations().size()).isEqualTo(1);
    }

    @Test
    void 지하철역_목록을_조회할_수_있어야_한다() {
        // given
        final LineStations lineStations = givenLineStations();

        // when
        final List<StationResponse> stations = lineStations.stations();

        // then
        assertThat(stations).containsExactly(StationResponse.of(station1), StationResponse.of(station2));
    }

    @Test
    void 지하철역으로_LineStation_객체를_조회할_수_있어야_한다() {
        // given
        final LineStations lineStations = givenLineStations();

        // when
        final Optional<LineStation> lineStation = lineStations.getByStation(station1);

        // then
        assertThat(lineStation.isPresent()).isTrue();
        assertThat(lineStation.get().getStation()).isEqualTo(station1);
    }

    @Test
    void 구간_목록을_반환할_수_있어야_한다() {
        // given
        final LineStations lineStations = givenLineStations();

        // when
        final List<SectionResponse> sections = lineStations.sections();

        // then
        assertThat(sections).containsExactly(
                new SectionResponse(line.getName(), station1.getName(), station2.getName(), 30L));
    }

    @Test
    void 지하철역이_노선에_등록되어_있는지_확인할_수_있어야_한다() {
        // given
        final LineStations lineStations = newGivenLineStations();
        final Station newStation = new Station("양재역");

        // when and then
        assertThat(lineStations.hasRelationTo(station1)).isTrue();
        assertThat(lineStations.hasRelationTo(newStation)).isFalse();
    }


    private LineStations givenLineStations() {
        final List<LineStation> lineStationList = Arrays.asList(
                new LineStation(line, station1, null, 0L, station2, from1To2),
                new LineStation(line, station2, station1, from1To2, null, 0L));
        return new LineStations(lineStationList);
    }

    private LineStations newGivenLineStations() {
        final LineStations lineStations = new LineStations();
        lineStations.addFinalStations(line, station1, station2, from1To2);
        return lineStations;
    }
}
