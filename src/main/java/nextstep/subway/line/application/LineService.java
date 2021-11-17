package nextstep.subway.line.application;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.SectionRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineUpdateRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

@Service
@Transactional(readOnly = true)
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final SectionRepository sectionRepository;

    public LineService(
        LineRepository lineRepository,
        StationRepository stationRepository,
        SectionRepository sectionRepository
    ) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest request) {
        Line line = lineRepository.save(Line.of(request.getName(), request.getColor()));
        Station upStation = stationRepository.findById(request.getUpStationId()).orElseThrow(NoSuchElementException::new);
        Station downStation = stationRepository.findById(request.getDownStationId()).orElseThrow(NoSuchElementException::new);
        Section section = sectionRepository.save(Section.of(line, upStation, downStation, request.getDistance()));
        line.addSection(section);
        return LineResponse.of(line);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
            .map(LineResponse::of)
            .collect(Collectors.toList());
    }

    public LineResponse findLine(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(NoSuchElementException::new);
        return LineResponse.of(line);
    }

    @Transactional
    public void updateLine(Long id, LineUpdateRequest request) {
        Line line = lineRepository.findById(id).orElseThrow(NoSuchElementException::new);
        line.update(Line.of(request.getName(), request.getColor()));
    }

    @Transactional
    public void deleteLineById(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(NoSuchElementException::new);
        lineRepository.delete(line);
    }
}
