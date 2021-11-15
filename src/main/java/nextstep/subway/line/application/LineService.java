package nextstep.subway.line.application;

import nextstep.subway.global.EntityNotFoundException;
import nextstep.subway.line.domain.Distance;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineNameDuplicatedException;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
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

    public LineResponse saveLine(LineRequest lineRequest) {
        validateLine(lineRequest);

        Line line = lineRequest.toLine();
        Section section = new Section(
                findStationById(lineRequest.getUpStationId()),
                findStationById(lineRequest.getDownStationId()),
                new Distance(lineRequest.getDistance())
        );
        line.addSection(section);

        Line persistLine = lineRepository.save(line);
        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> getLines() {
        return lineRepository.findAll()
                .stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse getLine(Long id) {
        Line line = findLineById(id);
        return LineResponse.of(line);
    }

    public void updateLine(Long id, LineRequest updateLineRequest) {
        Line line = findLineById(id);
        line.update(updateLineRequest.toLine());
    }

    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    public void addSection(Long id, SectionRequest sectionRequest) {
        Line line = findLineById(id);
        Section section = sectionRequest.toSection(
                findStationById(sectionRequest.getUpStationId()),
                findStationById(sectionRequest.getDownStationId())
        );
        line.addSection(section);
    }

    private void validateLine(LineRequest request) {
        if (isNameDuplicated(request)) {
            throw new LineNameDuplicatedException();
        }
    }

    private boolean isNameDuplicated(LineRequest request) {
        return lineRepository.existsByName(request.getName());
    }

    private Line findLineById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Line"));
    }

    private Station findStationById(Long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Station"));
    }
}
