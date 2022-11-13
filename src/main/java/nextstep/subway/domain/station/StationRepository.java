package nextstep.subway.domain.station;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StationRepository extends JpaRepository<Station, Long> {
    @Override
    List<Station> findAll();
}