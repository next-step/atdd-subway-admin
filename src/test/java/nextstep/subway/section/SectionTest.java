package nextstep.subway.section;

import org.junit.jupiter.api.BeforeEach;

class SectionTest {


    @BeforeEach
    void setUp() {

    }

    /*@DisplayName("구간 생성시 유효성 검사")
    @Test
    public void 구간생성시_예외발생확인() {
        //when
        //then
        assertThatThrownBy(() -> Section.create(null, upStation, downStation, 10))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> Section.create(line, null, downStation, 10))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> Section.create(line, upStation, null, 10))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> Section.create(line, upStation, downStation, 0))
                .isInstanceOf(ValueOutOfBoundsException.class);
    }*/


}