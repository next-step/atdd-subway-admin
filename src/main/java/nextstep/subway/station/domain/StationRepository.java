package nextstep.subway.station.domain;

import nextstep.subway.line.domain.Line;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StationRepository extends JpaRepository<Station, Long> {
    @Override
    List<Station> findAll();

    List<Station> findByLine(Line line);
}