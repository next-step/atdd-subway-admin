package nextstep.subway.section.application;

import lombok.RequiredArgsConstructor;
import nextstep.subway.line.application.NoLineException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.section.domain.NoStationIdException;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SectionService {
    final private LineRepository lineRepository;
    final private StationRepository stationRepository;

    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> throwNoLineException(lineId));
        line.addSection(sectionRequest.toSection());
    }

    private NoLineException throwNoLineException(Long id) {
        return new NoLineException(id);
    }

    public void removeSectionByStationId(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> throwNoLineException(lineId));
        stationRepository.findById(stationId)
                .orElseThrow(() -> throwNoStationException(stationId));
        line.removeSectionByStationId(stationId);
    }

    private NoStationIdException throwNoStationException(Long id) {
        return new NoStationIdException("해당역이 존재하지 않습니다 stationId = " + id);
    }
}
