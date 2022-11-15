package nextstep.subway.domain;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LineRepository extends JpaRepository<Line, Long> {

    @Override
    @EntityGraph(attributePaths = "stations")
    List<Line> findAll();
}
