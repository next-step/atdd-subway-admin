package nextstep.subway.line.application;

import javax.persistence.EntityNotFoundException;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

import nextstep.subway.line.domain.Section;
import nextstep.subway.station.application.StationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {

    private final LineRepository lineRepository;
    private final StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public LineResponse saveLine(LineRequest request) {
        Section section = getSectionFrom(request);
        Line line = getLineFrom(request, section);

        return LineResponse.of(lineRepository.save(line));
    }

    private Section getSectionFrom(LineRequest request) {
        return new Section(
            stationService.getStation(request.getUpStationId()),
            stationService.getStation(request.getDownStationId()),
            request.getDistance()
        );
    }

    private Line getLineFrom(LineRequest request, Section section) {
        Line line = request.toLine();
        line.add(section);

        return line;
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        return lineRepository.findAll()
            .stream()
            .map(LineResponse::of)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findLineBy(Long id) {
        return lineRepository
            .findById(id)
            .map(LineResponse::of)
            .orElseThrow(EntityNotFoundException::new);
    }

    public LineResponse updateLineBy(Long id, LineRequest lineRequest) {
        return lineRepository
            .findById(id)
            .map(it -> it.getUpdatedLineBy(lineRequest))
            .map(LineResponse::of)
            .orElseThrow(EntityNotFoundException::new);
    }

    public void deleteStationBy(Long id) {
        lineRepository.deleteById(id);
    }
}
