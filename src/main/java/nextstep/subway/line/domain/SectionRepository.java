package nextstep.subway.line.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SectionRepository extends JpaRepository<Section, Long> {

	public Optional<Section> findByLineId(Long lineId);
}
