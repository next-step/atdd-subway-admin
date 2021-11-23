package nextstep.subway.line.application;

import nextstep.subway.common.exception.DuplicateEntityException;
import nextstep.subway.common.exception.NotFoundEntityException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LineService {
    public static final String NOT_FOUND_ENTITY_MESSAGE = "Entity를 찾을 수 없습니다.";
    public static final String DUPLICATE_ENTITY_MESSAGE = "이미 존재하는 Entity가 있습니다.";

    private final LineRepository lineRepository;
    private final StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public LineResponse saveLine(LineRequest request) {
        validateUniqueName(request);
        Line persistLine = lineRepository.save(request.toLine());
        changeSections(persistLine, request);
        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return LineResponse.listOf(lines);
    }

    @Transactional(readOnly = true)
    public LineResponse findLineById(Long id) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new NotFoundEntityException(NOT_FOUND_ENTITY_MESSAGE));

        return LineResponse.of(line);
    }

    @Transactional
    public LineResponse update(Long id, LineRequest lineRequest) {
        Line persistLine = lineRepository.findById(id)
                .orElseThrow(() -> new NotFoundEntityException(NOT_FOUND_ENTITY_MESSAGE));

        if (!lineRequest.isSameName(persistLine.getName())) {
            validateUniqueName(lineRequest);
        }

        updateSections(persistLine, lineRequest);
        persistLine.update(lineRequest.toLine());
        return LineResponse.of(persistLine);
    }

    private void changeSections(Line line, LineRequest request) {
        if (request.getUpStationId() != null) {
            Station upStation = stationService.findStationById(request.getUpStationId());
            Section.of(line, upStation, 0);
        }

        if (request.getDownStationId() != null) {
            Station downStation = stationService.findStationById(request.getDownStationId());
            Section.of(line, downStation, request.getDistance());
        }
    }

    private void updateSections(Line line, LineRequest request) {
        line.clearSections();
        changeSections(line, request);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    private void validateUniqueName(LineRequest request) {
        if (lineRepository.existsByName(request.getName())) {
            throw new DuplicateEntityException(DUPLICATE_ENTITY_MESSAGE);
        }
    }
}
