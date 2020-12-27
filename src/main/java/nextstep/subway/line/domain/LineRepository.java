package nextstep.subway.line.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LineRepository extends JpaRepository<Line, Long> {

    @Query("select l from Line l join fetch l.lineStations where l.id = :id")
    Optional<Line> findByIdWithLineStation(Long id);

    @Query("select l from Line l join fetch l.lineStations where l.name = :name")
    Optional<Line> findByNameWithLineStation(String name);
}
