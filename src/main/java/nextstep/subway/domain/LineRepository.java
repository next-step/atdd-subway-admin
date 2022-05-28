package nextstep.subway.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LineRepository extends JpaRepository<Line, Long> {
    @Query("select distinct l from Line l join fetch l.stations where l.id = :id")
    Optional<Line> findByIdWithStations(@Param("id") Long id);
}
