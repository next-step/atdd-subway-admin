package nextstep.subway.application;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.SectionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SectionService {
    private final LineRepository lineRepository;

    public SectionService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public SectionResponse addSection(Long lineId, SectionRequest sectionRequest) {
        Line line = lineRepository.getById(lineId);
        Section section = new Section(lineId, new Station(sectionRequest.getUpStationId()),
                new Station(sectionRequest.getDownStationId()), sectionRequest.getDistance());
        line.addSection(section);

        return SectionResponse.from(section);
    }

    @Transactional(readOnly = true)
    public List<SectionResponse> findLineSections(Long lineId) {
        Line line = lineRepository.getById(lineId);
        List<Section> sections = line.getSections().getSections();

        return sections.stream()
                .map(SectionResponse::from)
                .collect(Collectors.toList());
    }
}
