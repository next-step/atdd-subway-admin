package nextstep.subway.line.repository;

import nextstep.subway.line.domain.Line;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LineRepository extends JpaRepository<Line, Long> {
    @Override
    List<Line> findAll();
}
