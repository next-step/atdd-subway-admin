package nextstep.subway.domain.line;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LineRepository extends JpaRepository<Line, Long> {

    Optional<Line> findByName(LineName name);

    Optional<Line> findByColor(LineColor color);
}
