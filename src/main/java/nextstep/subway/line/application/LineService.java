package nextstep.subway.line.application;

import nextstep.subway.exception.NotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LinesResponse;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineRepository.save(request.toLine());
        persistLine.addSection(request.toSectionRequest().toSection());
        return getLine(persistLine.getId());
    }

    public LinesResponse getLines() {
        List<Line> lines = lineRepository.findAll();
        return new LinesResponse(lines);
    }

    public LineResponse getLine(Long id) throws NotFoundException {
        Line line = lineRepository.findById(id)
                .orElseThrow(NotFoundException::new);

        List<StationResponse> stationResponses = stationRepository.findAllById(line.getStations())
                .stream().map(station -> StationResponse.of(station))
                .collect(Collectors.toList());

        return LineResponse.of(line, stationResponses);
    }

    public LineResponse updateLine(Long id, LineRequest request) throws NotFoundException {
        return lineRepository.findById(id)
                .map(line -> line.update(request.toLine()))
                .map(LineResponse::of)
                .orElseThrow(NotFoundException::new);
    }

    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    public LineResponse addSection(Long id, SectionRequest sectionRequest) {
        Line line = lineRepository.findById(id).orElseThrow(NotFoundException::new);
        line.addSection(sectionRequest.toSection());
        return getLine(id);
    }
}
