package nextstep.subway.line.application;

import nextstep.subway.exception.BadRequestException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {

    private final LineRepository lineRepository;
    private final StationService stationService;

    public LineService(final LineRepository lineRepository, final StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public LineResponse saveLine(final LineRequest lineRequest) {
        validateDuplicate(lineRequest);
        final Line persistLine = lineRepository.save(lineRequest.toLine());
        addSectionByRequest(persistLine, lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance());

        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        final List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findLine(final Long id) {
        final Line line = findLineById(id);
        return LineResponse.of(line);
    }

    public LineResponse updateLine(final LineRequest lineRequest, final Long id) {
        final Line line = findLineById(id);
        line.update(lineRequest.toLine());
        return LineResponse.of(line);
    }

    public LineResponse addSection(final Long id, final SectionRequest sectionRequest) {
        final Line line = findLineById(id);
        addSectionByRequest(line, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
        return LineResponse.of(line);
    }

    public void deleteLineById(final Long id) {
        lineRepository.deleteById(id);
    }

    private Line findLineById(final Long id) {
        return lineRepository.findById(id).orElseThrow(BadRequestException::new);
    }

    private void validateDuplicate(final LineRequest lineRequest) {
        if (lineRepository.existsByName(lineRequest.getName())) {
            throw new BadRequestException();
        }
    }

    private void addSectionByRequest(final Line persistLine, final Long upStationId, final Long downStationId, final int distance) {
        if (hasSectionInfo(upStationId, downStationId, distance)) {
            final Station upStation = stationService.findStationById(upStationId);
            final Station downStation = stationService.findStationById(downStationId);
            persistLine.addSection(upStation, downStation, distance);
        }
    }

    private boolean hasSectionInfo(final Long upStationId, final Long downStationId, final int distance) {
        return upStationId != null || downStationId != null || distance > 0;
    }
}
