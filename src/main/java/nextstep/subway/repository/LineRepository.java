package nextstep.subway.repository;

import nextstep.subway.domain.line.Line;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LineRepository extends JpaRepository<Line, Long> {

    @Query("select l " +
            "from Line l " +
            "join fetch l.upStation " +
            "join fetch l.downStation ")
    List<Line> findAllWithStations();

    @Query("select l " +
            "from Line l " +
            "join fetch l.upStation " +
            "join fetch l.downStation " +
            "where l.id = :id")
    Optional<Line> findByIdWithStations(Long id);
}
