package nextstep.subway.domain.repository;

import java.util.Collection;
import java.util.List;
import nextstep.subway.domain.Station;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StationRepository extends JpaRepository<Station, Long> {
    @Override
    List<Station> findAll();

    List<Station> findByIdIn(Collection<Long> id);
}
