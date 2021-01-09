package nextstep.subway.station.domain;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StationRepository extends JpaRepository<Station, Long> {
    @Override
    List<Station> findAll();
    Optional<Station> findByName(String name);
    Stream<Station> findAllByIdIn(long... ids);
}