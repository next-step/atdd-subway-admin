package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.SectionRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineResponses;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;
    private SectionRepository sectionRepository;

    private StationService stationService;

    public LineService(LineRepository lineRepository, SectionRepository sectionRepository,
        StationService stationService) {
        this.lineRepository = lineRepository;
        this.sectionRepository = sectionRepository;
        this.stationService = stationService;
    }

    public LineResponse saveLine(final LineRequest request) {
        Line persistLine = lineRepository.save(request.toLine());

        final Station upStation = stationService.findStationById(request.getUpStationId());
        final Station downStation = stationService.findStationById(request.getDownStationId());
        final Section section =
            sectionRepository.save(new Section(upStation, downStation, request.toDistance())).withLine(persistLine);

        persistLine.withSection(section);

        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public LineResponses findAllLines() {
        return LineResponses.of(lineRepository.findAll());
    }

    @Transactional(readOnly = true)
    public LineResponse findLineById(final long id) {
        return LineResponse.of(getLine(id));
    }

    public LineResponse updateLine(final long id, final LineRequest request) {
        final Line line = getLine(id);

        line.update(request.toLine());

        return LineResponse.of(line);
    }

    private Line getLine(final long id) {
        return lineRepository.findById(id).orElseThrow(IllegalStateException::new);
    }

    public void deleteLineById(final long id) {
        lineRepository.deleteById(id);
    }
}
