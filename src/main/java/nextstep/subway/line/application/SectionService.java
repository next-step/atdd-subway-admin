package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.LineCreateResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.line.dto.SectionResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SectionService {

    private final LineService lineService;
    private final StationService stationService;

    public SectionService(LineService lineService, StationService stationService) {
        this.lineService = lineService;
        this.stationService = stationService;
    }

    @Transactional
    public LineCreateResponse addSection(Long id, SectionRequest request) {
        Line line = lineService.findLine(id);
        Station upStation = stationService.findById(request.getUpStationId());
        Station downStation = stationService.findById(request.getDownStationId());

        line.addSection(request.toSection(upStation, downStation));
        return LineCreateResponse.of(line);
    }

    public List<SectionResponse> findSection(Long id) {
        Line line = lineService.findLine(id);
        List<Section> sections = line.getSectionsInOrder();
        return sections.stream()
                .map(section -> SectionResponse.of(section.getUpStation(), section.getDownStation(), section.getDistance()))
                .collect(Collectors.toList());
    }

    public void deleteStation(Long lineId, Long stationId) {
        Line line = lineService.findLine(lineId);
        Station station = stationService.findById(stationId);
        line.removeSection(station);
    }
}
