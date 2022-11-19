package nextstep.subway.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.SectionRequest;

@Service
@Transactional(readOnly = true)
public class SectionService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public SectionService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public void saveSection(Long id, SectionRequest sectionRequest) {
        Line line = lineRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지하철노선 ID 입니다."));
        Station upStation = stationRepository.findById(sectionRequest.getUpStationId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지하철 ID 입니다."));
        Station downStation = stationRepository.findById(sectionRequest.getDownStationId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지하철 ID 입니다."));

        line.saveSection(
            new Section(line, upStation, downStation, sectionRequest.getDistance())
        );
    }

    @Transactional
    public void removeSectionByStationId(Long id, Long stationId) {
        Line line = lineRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지하철노선 ID 입니다."));
        Station station = stationRepository.findById(stationId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지하철 ID 입니다."));

        line.removeSectionByStation(station);
    }
}
