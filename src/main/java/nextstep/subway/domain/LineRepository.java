package nextstep.subway.domain;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LineRepository extends JpaRepository<Line, Long> {
    @Override
    @EntityGraph(attributePaths = { "lineStations" })
    Optional<Line> findById(Long id);

    @Override
    @EntityGraph(attributePaths = { "lineStations" })
    List<Line> findAll();
}
