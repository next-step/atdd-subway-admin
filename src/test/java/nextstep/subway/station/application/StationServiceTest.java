package nextstep.subway.station.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import nextstep.subway.common.domain.Name;
import nextstep.subway.common.exception.DuplicateDataException;
import nextstep.subway.common.exception.NotFoundException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationRequest;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("지하철 역 서비스")
@ExtendWith(MockitoExtension.class)
class StationServiceTest {

    @Mock
    private StationRepository repository;

    @InjectMocks
    private StationService service;

    @Test
    @DisplayName("저장")
    void saveStation() {
        //given
        String expectedName = "강남";
        notExistsDuplicationName(expectedName);
        returnSavedStation(mockStation());

        //when
        save(new StationRequest(expectedName));

        //then
        assertThat(savedStation().name())
            .isEqualTo(Name.from(expectedName));
    }

    @Test
    @DisplayName("이미 존재하는 이름으로 저장하면 DuplicateDataException")
    void saveStation_alreadyExistsName_thrownDataIntegrityViolationException() {
        //given
        String requestName = "name";
        alreadyExistsDuplicationName(requestName);

        //when
        ThrowingCallable saveCall = () -> save(new StationRequest(requestName));

        //then
        assertThatExceptionOfType(DuplicateDataException.class)
            .isThrownBy(saveCall)
            .withMessageEndingWith("already exists");
    }

    @Test
    @DisplayName("존재하지 않는 아이디로 역을 찾으면 NotFoundException")
    void findStation() {
        //given
        notExistsStation();

        //when
        ThrowingCallable findOneCall = () -> service.findStation(Long.MAX_VALUE);

        //then
        assertThatExceptionOfType(NotFoundException.class)
            .isThrownBy(findOneCall)
            .withMessageEndingWith("does not exist");
    }

    private void returnSavedStation(Station station) {
        when(repository.save(any(Station.class)))
            .thenReturn(station);
    }

    private Station mockStation() {
        return Station.from(Name.from("강남"));
    }

    private void notExistsStation() {
        when(repository.findById(anyLong()))
            .thenReturn(Optional.empty());
    }

    private void alreadyExistsDuplicationName(String name) {
        when(repository.existsByName(Name.from(name)))
            .thenReturn(true);
    }

    private Station savedStation() {
        ArgumentCaptor<Station> captor = ArgumentCaptor.forClass(Station.class);
        verify(repository, times(1)).save(captor.capture());
        return captor.getValue();
    }

    private void notExistsDuplicationName(String name) {
        when(repository.existsByName(Name.from(name)))
            .thenReturn(false);
    }

    private void save(StationRequest request) {
        service.saveStation(request);
    }
}
