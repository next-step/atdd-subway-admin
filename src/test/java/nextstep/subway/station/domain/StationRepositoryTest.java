package nextstep.subway.station.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

import nextstep.subway.common.domain.Name;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@DataJpaTest
@DisplayName("지하철 역 저장소")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
class StationRepositoryTest {

    @Autowired
    private StationRepository repository;

    @Test
    @DisplayName("저장")
    void save() {
        //given
        Name gangnam = Name.from("강남");

        //when
        Station savedStation = save(gangnam);

        //then
        savedStation(savedStation, gangnam);
    }

    @Test
    @DisplayName("중복된 이름 저장하면 DataIntegrityViolationException")
    void save_duplicationName_thrownDataIntegrityViolationException() {
        //given
        Name gangnam = Name.from("강남");
        givenStation(gangnam);

        //when
        ThrowingCallable saveCall = () -> save(gangnam);

        //then
        assertThatExceptionOfType(DataIntegrityViolationException.class)
            .isThrownBy(saveCall);
    }

    @ParameterizedTest(name = "[{index}] \"{0}\"은 이미 존재하는 이름이라는 사실이 {1}")
    @DisplayName("이미 존재하는 이름인지 확인")
    @CsvSource({"강남,true", "역삼,false"})
    void existsByName(String name, boolean expected) {
        //given
        givenStation(Name.from("강남"));

        //when
        boolean existsByName = repository.existsByName(Name.from(name));

        //then
        assertThat(existsByName)
            .isEqualTo(expected);
    }

    private Station save(Name name) {
        return repository.save(Station.from(name));
    }

    private void givenStation(Name name) {
        save(name);
    }

    private void savedStation(Station station, Name expectedName) {
        assertAll(
            () -> assertThat(station.getId()).isNotNull(),
            () -> assertThat(station.getName()).isEqualTo(expectedName),
            () -> assertThat(station.getCreatedDate()).isNotNull(),
            () -> assertThat(station.getModifiedDate()).isNotNull()
        );
    }
}
