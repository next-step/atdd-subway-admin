package nextstep.subway.unit.lineStation;

import nextstep.subway.application.exception.exception.NotFoundDataException;
import nextstep.subway.application.exception.exception.NotValidDataException;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineStation;
import nextstep.subway.domain.LineStations;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static nextstep.subway.application.exception.type.AlreadyExceptionType.ALREADY_LINE_STATION;
import static nextstep.subway.application.exception.type.ValidExceptionType.MIN_LINE_STATIONS_SIZE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class LineStationsUnitTest {

    @Test
    @DisplayName("이미 존재하는 역이라면 예외를 발생시킨다")
    void alreadyExistStationException() {
        Station upStation = new Station("선릉역");
        Station downStation = new Station("역삼역");
        Line line = Line.of("1호산", "red", upStation, downStation, 5);
        LineStation lineStation = LineStation.of(upStation, downStation, 10);

        assertThatThrownBy(() -> {
            line.addLineStation(lineStation);
        }).isInstanceOf(NotFoundDataException.class)
                .hasMessageContaining(ALREADY_LINE_STATION.getMessage());

    }

    @Test
    @DisplayName("lineStation이 비어있다면 station을 추가한다")
    void lineStationIsEmptyAddStation() {
        String upStationName = "선릉역";
        String downStationName = "역삼역";
        Station upStation = new Station("선릉역");
        Station downStation = new Station("역삼역");

        Line line = Line.of("1호산", "red", upStation, downStation, 5);

        List<Station> lineStations = line.getLineStations();

        assertThat(lineStations.get(0).getName()).isEqualTo(upStationName);
        assertThat(lineStations.get(1).getName()).isEqualTo(downStationName);
    }

    @Test
    @DisplayName("lineStation의 size가 1이하이면 오류를 리턴한디")
    void lineStationIfCheckMinStationSize() {
        Station upStation = new Station("선릉역");
        Station downStation = new Station("역삼역");
        Station deleteStation = new Station("사당역");

        Line line = Line.of("1호산", "red", upStation, downStation, 5);

        assertThatThrownBy(() -> {
            line.delete(deleteStation);
        }).isInstanceOf(NotValidDataException.class)
                .hasMessageContaining(MIN_LINE_STATIONS_SIZE.getMessage());
    }

    @Test
    @DisplayName("성공적으로 상행선을 제거 한다")
    void deleteUpStation() {
        Station upStation = new Station("선릉역");
        Station downStation = new Station("역삼역");
        Station deleteStation = new Station("사당역");

        LineStations lineStations = new LineStations(new ArrayList<>());

        LineStation lineStation = LineStation.of(upStation, downStation, 10);
        lineStations.addLineStation(lineStation);
        LineStation newStation = LineStation.of(downStation, deleteStation, 7);
        lineStations.addLineStation(newStation);

        lineStations.delete(upStation);

        assertThat(lineStations.getStations()).containsOnly(deleteStation, downStation);
    }

    @Test
    @DisplayName("성공적으로 하행선을 제거 한다")
    void deleteDownStation() {
        Station upStation = new Station("선릉역");
        Station downStation = new Station("역삼역");
        Station deleteStation = new Station("사당역");

        LineStations lineStations = new LineStations(new ArrayList<>());

        LineStation lineStation = LineStation.of(upStation, downStation, 10);
        lineStations.addLineStation(lineStation);
        LineStation newStation = LineStation.of(downStation, deleteStation, 7);
        lineStations.addLineStation(newStation);

        lineStations.delete(upStation);

        assertThat(lineStations.getStations()).containsOnly(deleteStation, downStation);
    }


    @Test
    @DisplayName("마지막 구간 삭제시 예외가 발생한다")
    void deleteEndStationException() {
        Station upStation = new Station("선릉역");
        Station downStation = new Station("역삼역");
        LineStations lineStations = new LineStations(new ArrayList<>());
        LineStation lineStation = LineStation.of(upStation, downStation, 10);
        lineStations.addLineStation(lineStation);

        assertThatThrownBy(() -> {
            lineStations.delete(downStation);
        }).isInstanceOf(NotValidDataException.class)
                .hasMessageContaining(MIN_LINE_STATIONS_SIZE.getMessage());
    }
}