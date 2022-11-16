package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("지하철 구간 연결 종류를 결정하는 클래스 테스트")
class ConnectionTypeTest {

    @Test
    void 현재_구간의_상행역과_요청_구간의_하행역이_같으면_상행_종점역() {
        Station upStation = new Station("강남역");
        Station downStation = new Station("역삼역");
        Section current = new Section(upStation, downStation, 10);
        Section request = new Section(new Station("교대역"), upStation, 10);

        assertEquals(ConnectionType.FIRST, ConnectionType.match(current, request));
    }

    @Test
    void 현재_구간의_상행역과_요청_구간의_상행역이_같으면_역_사이() {
        Station upStation = new Station("강남역");
        Station downStation = new Station("선릉역");
        Section current = new Section(upStation, downStation, 10);
        Section request = new Section(upStation, new Station("역삼역"), 10);

        assertEquals(ConnectionType.MIDDLE, ConnectionType.match(current, request));
    }

    @Test
    void 현재_구간의_하행역과_요청_구간의_하행역이_같으면_역_사이() {
        Station upStation = new Station("강남역");
        Station downStation = new Station("선릉역");
        Section current = new Section(upStation, downStation, 10);
        Section request = new Section(new Station("역삼역"), downStation, 10);

        assertEquals(ConnectionType.MIDDLE, ConnectionType.match(current, request));
    }

    @Test
    void 현재_구간의_하행역과_요청_구간의_상행역이_같으면_하행_종점역() {
        Station upStation = new Station("강남역");
        Station downStation = new Station("역삼역");
        Section current = new Section(upStation, downStation, 10);
        Section request = new Section(downStation, new Station("선릉역"), 10);

        assertEquals(ConnectionType.LAST, ConnectionType.match(current, request));
    }
}
