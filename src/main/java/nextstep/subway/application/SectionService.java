package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.SectionRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SectionService {
    private final LineRepository lineRepository;

    public SectionService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Line line = lineRepository.getById(lineId);
        Section section = new Section(lineId, new Station(sectionRequest.getUpStationId()),
                new Station(sectionRequest.getDownStationId()), sectionRequest.getDistance());
        line.addSection(section);
    }
}
