package nextstep.subway.station.domain;

import nextstep.subway.exception.NotFoundStationException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StationRepository extends JpaRepository<Station, Long> {
    @Override
    List<Station> findAll();

    default Station findByIdElseThrow(Long id) {
        return this.findById(id).orElseThrow(NotFoundStationException::new);
    }
}