package nextstep.subway.station;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.NoSuchElementException;
import nextstep.subway.common.exception.ErrorEnum;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DisplayName("지하철역 테스트")
@DataJpaTest
public class StationRepositoryTest {

    final String 지하철역_이름 = "지하철역";

    @Autowired
    private StationRepository stationRepository;

    @BeforeEach
    public void setUp() {

    }

    @Test
    void 지하철역_등록() {
        // When
        Station 지하철역 = 지하철역_등록(지하철역_이름);

        // Then
        assertAll(
                () -> assertThat(지하철역.getId()).isNotNull(),
                () -> assertThat(지하철역.getName()).isEqualTo(지하철역_이름)
        );
    }

    @Test
    void 지하철역_조회() {
        // Given
        Station 지하철역 = 지하철역_등록(지하철역_이름);

        // When
        Station 조회한_지하철역 = stationRepository.findById(지하철역.getId())
                .orElseThrow(() -> new RuntimeException(ErrorEnum.NOT_EXISTS_LINE.message()));

        // Then
        assertThat(지하철역).isEqualTo(조회한_지하철역);
    }

    @Test
    void 지하철역_삭제() {
        // Given
        Station 지하철역 = 지하철역_등록(지하철역_이름);

        // When
        stationRepository.deleteById(지하철역.getId());

        // Then
        assertThatThrownBy(() -> stationRepository.findById(지하철역.getId()).get()).isInstanceOf(
                NoSuchElementException.class);
    }

    public Station 지하철역_등록(String stationName) {
        return stationRepository.save(new Station(stationName));
    }
}
