package nextstep.subway.domain;

import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface LineRepository extends JpaRepository<Line, Long> {
}
