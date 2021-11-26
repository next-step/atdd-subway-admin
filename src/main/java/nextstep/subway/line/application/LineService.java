package nextstep.subway.line.application;

import nextstep.subway.exception.BadRequestException;
import nextstep.subway.line.domain.Line;
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

    public LineResponse saveLine(LineRequest request) {
        validateDuplicate(request);

        Line persistLine = lineRepository.save(request.toLine());

        if (request.hasStationInfo()) {
            persistLine.addSection(
                    Section.of(
                            findStationById(request.getUpStationId()),
                            findStationById(request.getDownStationId()),
                            request.getDistance())
            );
        }

        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findLine(Long id) {
        Line line = findLineById(id);
        return LineResponse.of(line);
    }

    public LineResponse updateLine(LineRequest lineRequest, Long id) {
        Line line = findLineById(id);
        line.update(lineRequest.toLine());
        return LineResponse.of(line);
    }

    public LineResponse addSection(Long id, SectionRequest sectionRequest) {
        Line line = findLineById(id);
        line.addSection(
                Section.of(
                        findStationById(sectionRequest.getUpStationId()),
                        findStationById(sectionRequest.getDownStationId()),
                        sectionRequest.getDistance())
        );
        return LineResponse.of(line);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    private Line findLineById(Long id) {
        return lineRepository.findById(id).orElseThrow(BadRequestException::new);
    }

    private Station findStationById(Long stationId) {
        return stationRepository.findById(stationId).orElseThrow(BadRequestException::new);
    }

    private void validateDuplicate(LineRequest request) {
        if (lineRepository.existsByName(request.getName())) {
            throw new BadRequestException();
        }
    }
}
