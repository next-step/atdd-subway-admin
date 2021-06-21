package nextstep.subway.line.domain;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LineRepository extends JpaRepository<Line, Long> {
	Optional<List<Line>> findByIdIn(Set<Long> lineIds);
}
