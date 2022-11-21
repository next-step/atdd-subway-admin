package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionTest {

    @Test
    @DisplayName("구간은 상행역과 하행역이 있어야 생성할 수 있다.")
    void 생성() {
        // given
        Station upStation = Station.from("잠실역");
        Station downStation = Station.from("장지역");
        Integer distance = 10;

        // when
        Section section = Section.of(upStation, downStation, distance);

        // then
        assertThat(section).isNotNull();
    }

    @Test
    @DisplayName("구간은 상행역이 없다면 생성할 수 없다.")
    void 생성_실패_상행역_없음() {
        // given
        Station downStation = Station.from("장지역");
        Integer distance = 10;

        // expect
        assertThatIllegalArgumentException().isThrownBy(() -> Section.of(null, downStation, distance));
    }


    @Test
    @DisplayName("구간길이가 1미만이면 생성할 수 없다")
    void 생성_실패_구간길이_1미만() {
        // given
        Station upStation = Station.from("잠실역");
        Station downStation = Station.from("장지역");
        Integer distance = 0;

        // expect
        assertThatIllegalArgumentException().isThrownBy(() -> Section.of(upStation, downStation, distance));
    }

    @Test
    @DisplayName("A역과 C역을 가진 길이 10짜리 Z 구간 중간에, A역과 B역을 가진 길이 3짜리 구간으로 업데이트 하면, Z 구간은 B역과 C역을 가진 길이 7짜리로 변한다")
    void update() {
        // given
        Station AStation = Station.of(1L, "A역");
        Station CStation = Station.of(2L, "C역");
        Integer distance = 10;
        Section z = Section.of(AStation, CStation, distance);

        // given
        Station BStation = Station.of(3L, "B역");
        Integer ABdistance = 3;
        Section y = Section.of(AStation, BStation, ABdistance);

        // when
        z.update(y);

        // then
        assertAll(
                () -> assertThat(z.getStations()).containsExactly(BStation, CStation),
                () -> assertThat(z.getDistance()).isEqualTo(7)
        );
    }

    @Test
    @DisplayName("A역과 B역을 가진 길이 10짜리 Z 구간 중간에, A역과 b역을 가진 길이 3짜리 구간으로 업데이트 할 수 없다")
    void update_실패_같은역() {
        // given
        Station AStation = Station.of(1L, "A역");
        Station BStation = Station.of(2L, "C역");
        Integer distance = 10;
        Section z = Section.of(AStation, BStation, distance);

        // given
        Integer ABdistance = 3;
        Section y = Section.of(AStation, BStation, ABdistance);

        // expect
        assertThatIllegalArgumentException().isThrownBy(() -> z.update(y));

    }
}
