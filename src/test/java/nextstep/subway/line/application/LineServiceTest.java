package nextstep.subway.line.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import nextstep.subway.common.domain.Name;
import nextstep.subway.common.exception.DuplicateDataException;
import nextstep.subway.common.exception.NotFoundException;
import nextstep.subway.line.domain.Color;
import nextstep.subway.line.domain.Distance;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.Sections;
import nextstep.subway.line.dto.LineCreateRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineUpdateRequest;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("노선 서비스")
@ExtendWith(MockitoExtension.class)
class LineServiceTest {

    @Mock
    private LineRepository repository;

    @Mock
    private StationService stationService;

    @InjectMocks
    private LineService service;

    @Test
    @DisplayName("노선 저장")
    void saveLine() {
        //given
        String expectedName = "name";
        String expectedColor = "color";

        Station gangnam = givenGangnamStations(1L);
        Station yeoksam = givenYeoksamStations(2L);
        notExistsDuplicationName(expectedName);
        returnSavedLine(gangnamYoeksamLine());

        //when
        save(lineCreateRequest(expectedName, expectedColor));

        //then
        lineSaved(expectedName, expectedColor, gangnam, yeoksam, savedLine());
    }

    @Test
    @DisplayName("이미 존재하는 이름으로 저장하면 DuplicateDataException")
    void saveLine_alreadyExistsName_thrownDuplicateDataException() {
        //given
        String requestName = "name";
        alreadyExistsDuplicationName(requestName);

        //when
        ThrowingCallable saveCall = () -> save(lineCreateRequest(requestName, "color"));

        //then
        assertThatExceptionOfType(DuplicateDataException.class)
            .isThrownBy(saveCall)
            .withMessageEndingWith("already exists");
    }

    @Test
    @DisplayName("존재하지 않는 지하쳘 역으로 저장하면 NotFoundException")
    void saveLine_notExistsStation_thrownNotFoundException() {
        //given
        notExistsStation(anyLong());

        //when
        ThrowingCallable saveCall = () -> save(lineCreateRequest("name", "color"));

        //then
        assertThatExceptionOfType(NotFoundException.class)
            .isThrownBy(saveCall);
    }

    @Test
    @DisplayName("존재하지 않는 아이디로 노선 찾으면 NotFoundException")
    void findOne_notExits_thrownNotFoundException() {
        //given
        notExistsLine();

        //when
        ThrowingCallable findOneCall = () -> service.findOne(Long.MAX_VALUE);

        //then
        assertThatExceptionOfType(NotFoundException.class)
            .isThrownBy(findOneCall)
            .withMessageMatching("line id\\(\\d+\\) does not exist");
    }

    @Test
    @DisplayName("노선 이름과 색상 수정")
    void update() {
        //given
        String expectedName = "name";
        String expectedColor = "color";
        notExistsDuplicationName(expectedName);
        Line line = gangnamYoeksamLine();
        givenLine(line);

        //when
        service.update(Long.MAX_VALUE, new LineUpdateRequest(expectedName, expectedColor));

        //then
        assertAll(
            () -> assertThat(line.name()).isEqualTo(Name.from(expectedName)),
            () -> assertThat(line.color()).isEqualTo(Color.from(expectedColor))
        );
    }

    @Test
    @DisplayName("중복되는 이름으로 노선을 수정하면 DuplicateDataException")
    void update_duplicationName_thrownDataIntegrityViolationException() {
        //given
        String updatedName = "name";
        alreadyExistsDuplicationName(updatedName);

        //when
        ThrowingCallable updateCall = () -> service
            .update(Long.MAX_VALUE, new LineUpdateRequest(updatedName, "any"));

        //then
        assertThatExceptionOfType(DuplicateDataException.class)
            .isThrownBy(updateCall)
            .withMessageEndingWith("already exists");
    }

    @Test
    @DisplayName("존재하지 않는 노선을 수정하면 NotFoundException")
    void update_notExits_thrownNotFoundException() {
        //given
        notExistsLine();

        //when
        ThrowingCallable updateCall = () -> service
            .update(Long.MAX_VALUE, new LineUpdateRequest("any", "any"));

        //then
        assertThatExceptionOfType(NotFoundException.class)
            .isThrownBy(updateCall)
            .withMessageMatching("line id\\(\\d+\\) does not exist");
    }

    @Test
    @DisplayName("삭제")
    void delete() {
        //given
        long deletedId = Long.MAX_VALUE;

        //when
        service.delete(deletedId);

        //then
        verify(repository, only())
            .deleteById(deletedId);
    }

    @Test
    @DisplayName("구간 추가")
    void addSection() {
        //given
        Line line = gangnamYoeksamLine();
        givenLine(line);
        givenStation(1L, Station.from(Name.from("교대")));
        givenStation(2L, Station.from(Name.from("강남")));

        //when
        LineResponse lineResponse = service.addSection(
            anyLong(), new SectionRequest(1L, 2L, 10));

        //then
        assertThat(lineResponse.getStations())
            .extracting(StationResponse::getName)
            .containsExactly("교대", "강남", "역삼");
    }

    @Test
    @DisplayName("역 삭제")
    void deleteStation() {
        //given
        Line gyodaeGangnamYoeksamLine = gangnamYoeksamLine();
        gyodaeGangnamYoeksamLine.addSection(sectionTenDistance("교대", "강남"));
        givenLine(gyodaeGangnamYoeksamLine);
        givenStation(anyLong(), Station.from(Name.from("강남")));

        //when
        service.deleteStation(anyLong(), anyLong());

        //then
        assertThat(gyodaeGangnamYoeksamLine.stations())
            .extracting(Station::name)
            .containsExactly(Name.from("교대"), Name.from("역삼"));
    }

    private void lineSaved(String expectedName, String expectedColor,
        Station firstExpectedStation, Station secondExpectedStation, Line savedLine) {
        assertAll(
            () -> assertThat(savedLine.name()).isEqualTo(Name.from(expectedName)),
            () -> assertThat(savedLine.color()).isEqualTo(Color.from(expectedColor)),
            () -> assertThat(savedLine.stations())
                .containsExactly(firstExpectedStation, secondExpectedStation)
        );
    }

    private void notExistsLine() {
        when(repository.findById(anyLong()))
            .thenReturn(Optional.empty());
    }

    private void givenLine(Line line) {
        when(repository.findById(anyLong()))
            .thenReturn(Optional.of(line));
    }

    private void notExistsStation(long id) {
        when(stationService.findStation(id))
            .thenThrow(new NotFoundException("message"));
    }

    private void alreadyExistsDuplicationName(String name) {
        when(repository.existsByName(Name.from(name)))
            .thenReturn(true);
    }

    private void notExistsDuplicationName(String name) {
        when(repository.existsByName(Name.from(name)))
            .thenReturn(false);
    }

    private LineCreateRequest lineCreateRequest(String name, String color) {
        return new LineCreateRequest(name, color,
            new SectionRequest(1L, 2L, 10));
    }

    private Station givenGangnamStations(Long id) {
        Station gangnam = Station.from(Name.from("강남"));
        givenStation(id, gangnam);
        return gangnam;
    }

    private Station givenYeoksamStations(Long id) {
        Station yeoksam = Station.from(Name.from("역삼"));
        givenStation(id, yeoksam);
        return yeoksam;
    }

    private void givenStation(Long id, Station station) {
        when(stationService.findStation(id))
            .thenReturn(station);
    }

    private void save(LineCreateRequest request) {
        service.saveLine(request);
    }

    private void returnSavedLine(Line line) {
        when(repository.save(any(Line.class)))
            .thenReturn(line);
    }

    private Line gangnamYoeksamLine() {
        return Line.of(Name.from("name"), Color.from("color"),
            Sections.from(sectionTenDistance("강남", "역삼"))
        );
    }

    private Section sectionTenDistance(String upStationName, String downStationName) {
        return Section.of(
            Station.from(Name.from(upStationName)),
            Station.from(Name.from(downStationName)),
            Distance.from(10)
        );
    }


    private Line savedLine() {
        ArgumentCaptor<Line> lineArgumentCaptor = ArgumentCaptor.forClass(Line.class);
        verify(repository, times(1)).save(lineArgumentCaptor.capture());
        return lineArgumentCaptor.getValue();
    }
}
