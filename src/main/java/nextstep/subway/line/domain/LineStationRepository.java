package nextstep.subway.line.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LineStationRepository extends JpaRepository<LineStation, Long> {

    Optional<LineStation> findFirstByUpStationId(long id);
    Optional<LineStation> findFirstByStationId(long id);
}
