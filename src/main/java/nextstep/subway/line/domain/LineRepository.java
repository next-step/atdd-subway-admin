package nextstep.subway.line.domain;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LineRepository extends JpaRepository<Line, Long> {

    @Override
    @EntityGraph(attributePaths = {"sections.upStation","sections.downStation"}, type = EntityGraph.EntityGraphType.LOAD)
    Optional<Line> findById(Long id);

    @Override
    @EntityGraph(attributePaths = {"sections.upStation","sections.downStation"}, type = EntityGraph.EntityGraphType.LOAD)
    List<Line> findAll();

}
