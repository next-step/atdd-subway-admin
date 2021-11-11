package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

import nextstep.subway.common.domain.Name;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
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
@DisplayName("지하철 노선 저장소")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
class LineRepositoryTest {

    private Station yeoksam;
    private Station gangnam;

    @Autowired
    private LineRepository repository;

    @BeforeEach
    void setUp(@Autowired StationRepository repository) {
        gangnam = repository.save(Station.from(Name.from("강남")));
        yeoksam = repository.save(Station.from(Name.from("역삼")));
    }

    @Test
    @DisplayName("저장")
    void save() {
        //given
        Name first = Name.from("1호선");
        Color red = Color.from("red");

        //then
        Line savedLine = save(first, red);

        //then
        savedLine(savedLine, first, red);
    }

    @Test
    @DisplayName("중복된 이름 저장하면 DataIntegrityViolationException")
    void save_duplicationName_thrownDataIntegrityViolationException() {
        //given
        Name first = Name.from("1호선");
        givenLine(first, Color.from("red"));

        //when
        ThrowingCallable saveCall = () -> save(first, Color.from("red"));

        //then
        assertThatExceptionOfType(DataIntegrityViolationException.class)
            .isThrownBy(saveCall);
    }

    @ParameterizedTest(name = "[{index}] \"{0}\"은 이미 존재하는 이름이라는 사실이 {1}")
    @DisplayName("이미 존재하는 이름인지 확인")
    @CsvSource({"1호선,true", "2호선,false"})
    void existsByName(String name, boolean expected) {
        //given
        givenLine(Name.from("1호선"), Color.from("red"));

        //when
        boolean existsByName = repository.existsByName(Name.from(name));

        //then
        assertThat(existsByName)
            .isEqualTo(expected);
    }

    private void savedLine(Line actualLine, Name expectedName, Color expectedColor) {
        assertAll(
            () -> assertThat(actualLine.id()).isNotNull(),
            () -> assertThat(actualLine.name()).isEqualTo(expectedName),
            () -> assertThat(actualLine.color()).isEqualTo(expectedColor)
        );
    }

    private void givenLine(Name name, Color color) {
        save(name, color);
    }

    private Line save(Name name, Color color) {
        return repository.save(
            Line.of(name, color, Sections.from(gangnamYeoksamSection())));
    }

    private Section gangnamYeoksamSection() {
        return Section.of(gangnam, yeoksam, Distance.from(10));
    }
}
