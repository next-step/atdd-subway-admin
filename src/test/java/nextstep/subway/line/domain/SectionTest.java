package nextstep.subway.line.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.station.domain.Station;

class SectionTest {
    @DisplayName("두 역중 하나의 역이라도 포함되고 같은 포지션에 존재하는지 확인한다.")
    @Test
    void containsAndSamePosition() {
        int distance = 10;
        Section section = new Section(new Station("잠실역"), new Station("강남역"), distance);

        // same position
        Section samePosition = new Section(new Station("잠실역"), new Station("삼성역"), distance);
        assertTrue(section.containsAndSamePosition(samePosition));

        // not same position
        Section notSamePosition = new Section(new Station("삼성역"), new Station("잠실역"), distance);
        assertFalse(section.containsAndSamePosition(notSamePosition));

        // not contains position
        Section notContainsStation = new Section(new Station("합정역"), new Station("신촌역"), distance);
        assertFalse(section.containsAndSamePosition(notContainsStation));
    }

    @DisplayName("두 역중 하나의 역이라도 포함되고 같은 포지션에 존재하는지 않는지 확인한다.")
    @Test
    void containsAndNotSamePosition() {
        int distance = 10;
        Section section = new Section(new Station("잠실역"), new Station("강남역"), distance);

        // same position
        Section samePosition = new Section(new Station("잠실역"), new Station("삼성역"), distance);
        assertFalse(section.containsAndNotSamePosition(samePosition));

        // not same position
        Section notSamePosition = new Section(new Station("삼성역"), new Station("잠실역"), distance);
        assertTrue(section.containsAndNotSamePosition(notSamePosition));

        // not contains position
        Section notContainsStation = new Section(new Station("합정역"), new Station("신촌역"), distance);
        assertFalse(section.containsAndNotSamePosition(notContainsStation));
    }
}
