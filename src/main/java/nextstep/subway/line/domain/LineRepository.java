package nextstep.subway.line.domain;

import nextstep.subway.common.domain.Name;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LineRepository extends JpaRepository<Line, Long> {

    boolean existsByName(Name name);
}
