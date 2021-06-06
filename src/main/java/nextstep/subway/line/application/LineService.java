package nextstep.subway.line.application;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Line line = request.toLine();

        Section newSection = Section.of(
            findStation(request.getUpStationId()),
            findStation(request.getDownStationId()),
            request.getDistance());

        line.createSections(Arrays.asList(newSection));

        return LineResponse.of(lineRepository.save(line));
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream().map(LineResponse::of).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findLine(Long id) {
        return lineRepository.findById(id).map(LineResponse::of).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public Station findStation(Long id) {
        return stationRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public void updateLine(LineRequest lineRequest, Long id) {
        Line findedLine = lineRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        findedLine.update(lineRequest.toLine());
    }

    public void deleteLine(Long id) {
        Line findedLine = lineRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        lineRepository.delete(findedLine);
    }
}
