package nextstep.subway.station.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static nextstep.subway.common.message.DomainExceptionMessage.NOT_FOUND_MESSAGE;

public interface StationRepository extends JpaRepository<Station, Long> {
    @Override
    List<Station> findAll();

    default Station getById(Long id) {
        Optional<Station> optionalStation = findById(id);
        return optionalStation.orElseThrow(()-> new EntityNotFoundException(NOT_FOUND_MESSAGE.apply(id, "역")));
    }
}