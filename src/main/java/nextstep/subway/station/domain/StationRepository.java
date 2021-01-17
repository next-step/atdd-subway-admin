package nextstep.subway.station.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.stream.Stream;

public interface StationRepository extends JpaRepository<Station, Long> {
    @Override
    List<Station> findAll();

    Stream<Station> findAllByIdIn(Long... ids);
}