package nextstep.subway.section.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SectionRepository extends JpaRepository<Section, Long> {
    void deleteByLineId(long lineId);

    List<Section> findAllByLineId(Long lineId);
}
