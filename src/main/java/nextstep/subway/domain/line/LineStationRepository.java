package nextstep.subway.domain.line;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LineStationRepository extends JpaRepository<LineStation, Long> {
    public LineStation findByUpStationIdAndDownStationId(Long upStationId, Long downStationId);
}
