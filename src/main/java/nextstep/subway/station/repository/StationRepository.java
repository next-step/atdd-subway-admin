package nextstep.subway.station.repository;

import java.util.List;
import nextstep.subway.station.domain.Station;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StationRepository extends JpaRepository<Station, Long> {
    @Override
    List<Station> findAll();
}
