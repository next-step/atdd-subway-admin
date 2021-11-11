package nextstep.subway.station.domain;

import java.util.List;
import nextstep.subway.common.domain.Name;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StationRepository extends JpaRepository<Station, Long> {

    @Override
    List<Station> findAll();

    boolean existsByName(Name name);
}
