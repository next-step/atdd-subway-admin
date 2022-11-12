package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.SectionRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.SectionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class SectionService {

    private final SectionRepository sectionRepository;
    private final StationService stationService;
    private final LineService lineService;

    public SectionService(SectionRepository sectionRepository, StationService stationService, LineService lineService) {
        this.sectionRepository = sectionRepository;
        this.stationService = stationService;
        this.lineService = lineService;
    }

    @Transactional
    public SectionResponse saveSection(final Long lineId, final SectionRequest sectionRequest) {
        Station upStation = stationService.findById(sectionRequest.getUpStationId());
        Station downStation = stationService.findById(sectionRequest.getDownStationId());
        Section section = new Section(upStation, downStation, sectionRequest.getDistance());

        Line line = lineService.findById(lineId);
        line.addSection(section);
        lineService.flush();
        return SectionResponse.of(section);
    }

    public List<SectionResponse> findAllSections(final Long lineId) {
        return sectionRepository.findByLineId(lineId)
                .stream()
                .map(SectionResponse::of)
                .collect(Collectors.toList());
    }
}
