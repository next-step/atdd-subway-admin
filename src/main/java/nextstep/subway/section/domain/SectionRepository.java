package nextstep.subway.section.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface SectionRepository extends JpaRepository<Section, Long> {
    Optional<Section> findByUpStationId(Long id);

    Optional<Section> findByDownStationId(Long id);

    List<Section> findByLineId(Long id);

    @Transactional
    @Modifying
    @Query("delete from Section s where s.line.id = :line_id")
    void deleteAllByLineId(@Param("line_id") Long lineId);
}
