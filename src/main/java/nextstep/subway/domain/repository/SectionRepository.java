package nextstep.subway.domain.repository;

import java.util.List;
import java.util.Optional;
import nextstep.subway.domain.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SectionRepository extends JpaRepository<Section, Long> {

    @Query(value = "select s from Section s where s.line.id = :lineId")
    Optional<List<Section>> findByLineId(@Param("lineId") Long lineId);
}
