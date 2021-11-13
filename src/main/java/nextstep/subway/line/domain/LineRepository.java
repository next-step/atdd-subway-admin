package nextstep.subway.line.domain;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LineRepository extends JpaRepository<Line, Long> {

    @Override
    @EntityGraph(attributePaths = {"sections.station"}, type = EntityGraph.EntityGraphType.LOAD)
    List<Line> findAll();

}
