package nextstep.subway.line.domain.Line;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LineRepository extends JpaRepository<Line, Long> {

    @Query("select l from Line l " +
            "left join fetch l.stations.lineStations ls " +
            "left join fetch ls.nextStation " +
            "left join fetch ls.preStation " +
            "where l.id = :id")
    Optional<Line> findOneWithStations(@Param("id") Long id);

    @Query("select l from Line l " +
            "left join fetch l.stations.lineStations ls " +
            "left join fetch ls.nextStation " +
            "left join fetch ls.preStation ")
    List<Line> findAllWithStations();
}
