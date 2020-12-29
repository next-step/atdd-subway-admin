package nextstep.subway.section.application;

import nextstep.subway.common.exception.NotExistsLineIdException;
import nextstep.subway.common.exception.NotExistsStationIdException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SectionService {
    private static final Integer INIT_SECTION_NUMBER = 0;
    private static final Integer SECTION_NUMBER_OFFSET = 1;

    private LineRepository lineRepository;
    private SectionRepository sectionRepository;
    private StationRepository stationRepository;

    public SectionService(LineRepository lineRepository, SectionRepository sectionRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.sectionRepository = sectionRepository;
        this.stationRepository = stationRepository;
    }

    public void saveSection(SectionRequest request) {
        Line line = lineRepository.findById(request.getLineId())
                .orElseThrow(() -> new NotExistsLineIdException(request.getLineId()));
        Station upStation = stationRepository.findById(request.getUpStationId())
                .orElseThrow(() -> new NotExistsStationIdException(request.getUpStationId()));
        Station downStation = stationRepository.findById(request.getDownStationId())
                .orElseThrow(() -> new NotExistsStationIdException(request.getDownStationId()));

        Integer sectionNumber = getSectionNumber(line.getId());
        sectionRepository.save(new Section(line, upStation, downStation, request.getDistance(), sectionNumber));
    }

    public void deleteAllByLineId(Long lineId) {
        List<Section> sections = sectionRepository.findByLineId(lineId);
        sectionRepository.deleteAll(sections);
    }

    private Integer getSectionNumber(Long lineId) {
        Optional<Section> section = sectionRepository.findTop1ByLineIdOrderBySectionNumberDesc(lineId);

        return section.map(Section::getSectionNumber)
                .orElse(INIT_SECTION_NUMBER) + SECTION_NUMBER_OFFSET;
    }
}
