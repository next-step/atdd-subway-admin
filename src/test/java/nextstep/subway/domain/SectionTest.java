package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import nextstep.subway.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionTest {
    Station upStation;
    Station downStation;
    Section section;

    @BeforeEach
    void setUp() {
        upStation = Station.builder("양재")
                .id(1L)
                .build();
        downStation = Station.builder("판교")
                .id(2L)
                .build();
        section = Section.builder(upStation, downStation, Distance.valueOf(10))
                .build();
    }

    @DisplayName("상행 지하철역이 Null 일 경우 예외 테스트")
    @Test
    void createLineByNullUpStation() {
        Station downStation = Station.builder("새로운지하철역")
                .id(1L)
                .build();
        assertThatThrownBy(() -> Section.builder(null, downStation, Distance.valueOf(10))
                .build())
                .isInstanceOf(NotFoundException.class)
                .hasMessage("상행역 정보가 없습니다.");
    }

    @DisplayName("하행 지하철역이 Null 일 경우 예외 테스트")
    @Test
    void createLineByNullDownStation() {
        Station upStation = Station.builder("지하철역")
                .id(1L)
                .build();
        assertThatThrownBy(() -> Section.builder(upStation, null, Distance.valueOf(10))
                .build())
                .isInstanceOf(NotFoundException.class)
                .hasMessage("하행역 정보가 없습니다.");
    }

    @DisplayName("상행역이 같은 지하철 신규 구간 등록시 기존 케이스 구간 상행역 변경")
    @Test
    void updateSectionEqualToUpStation() {
        Station upStation = Station.builder("양재")
                .id(1L)
                .build();
        Station downStation = Station.builder("청계산입구역")
                .id(3L)
                .build();
        Section newSection = Section.builder(upStation, downStation, Distance.valueOf(5))
                .build();
        section.update(newSection);
        assertAll(
                () -> assertThat(section.upStation()).isEqualTo(downStation),
                () -> assertThat(section.distance()).isEqualTo(Distance.valueOf(5))
        );
    }

    @DisplayName("하행역이 같은 지하철 신규 구간 등록시 기존 케이스 구간 하행역 변경")
    @Test
    void updateSectionEqualToDownStation() {
        Station upStation = Station.builder("청계산입구역")
                .id(3L)
                .build();
        Station downStation = Station.builder("판교")
                .id(2L)
                .build();
        Section newSection = Section.builder(upStation, downStation, Distance.valueOf(5))
                .build();
        section.update(newSection);
        assertAll(
                () -> assertThat(section.downStation()).isEqualTo(upStation),
                () -> assertThat(section.distance()).isEqualTo(Distance.valueOf(5))
        );
    }
}
