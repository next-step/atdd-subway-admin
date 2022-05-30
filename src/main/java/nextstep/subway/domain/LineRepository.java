package nextstep.subway.domain;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LineRepository extends JpaRepository<Line, Long> {
    @Override
    @EntityGraph(attributePaths = { "upStation", "downStation" })
    public List<Line> findAll();

    @Override
    @Modifying
    @Query("delete from Line a where a.id = :id")
    void deleteById(@Param("id") Long id);
}
