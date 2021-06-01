package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.application.SectionCommandService;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.application.StationQueryService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineCommandService {

    private final LineQueryService lineQueryService;
    private final StationQueryService stationQueryService;
    private final SectionCommandService sectionCommandService;

    private final LineRepository lineRepository;

    public LineCommandService(LineQueryService lineQueryService,
                              StationQueryService stationQueryService,
                              SectionCommandService sectionCommandService,
                              LineRepository lineRepository) {
        this.lineQueryService = lineQueryService;
        this.stationQueryService = stationQueryService;
        this.sectionCommandService = sectionCommandService;
        this.lineRepository = lineRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineRepository.save(request.toLine());

        Station upStation = stationQueryService.findStationById(request.getUpStationId());
        Station downStation = stationQueryService.findStationById(request.getDownStationId());

        Section section = new Section(upStation, downStation, request.getDistance());
        sectionCommandService.saveSection(section.toLine(persistLine));

        return LineResponse.of(persistLine);
    }

    public void updateLine(Long lineId, LineRequest lineRequest) {
        Line line = lineQueryService.findLineById(lineId);
        line.update(new Line(lineRequest.getName(), lineRequest.getColor()));

        lineRepository.save(line);
    }

    public void deleteLine(Long lineId) {
        Line line = lineQueryService.findLineById(lineId);
        lineRepository.delete(line);
    }
}
