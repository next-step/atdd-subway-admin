package nextstep.subway.repository;

import nextstep.subway.domain.Station;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StationRepository extends JpaRepository<Station, Long> {
    @Override
    List<Station> findAll();
}
