package nextstep.subway.line.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LineRepository extends JpaRepository<Line, Long> {

    @Query("select l, s from Line l join fetch Section s on s.line.id = l.id")
    List<Line> findLineFetchJoin();

    @Query("select l, s from Line l join fetch Section s on s.line.id = l.id where l.id = :id")
    Optional<Line> findLineFetchJoinById(Long id);
}
