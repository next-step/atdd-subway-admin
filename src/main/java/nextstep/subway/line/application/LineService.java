package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;
    private SectionRepository sectionRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository, SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Station upStation = stationRepository.findById(request.getUpStationId())
                .orElseThrow(NoSuchElementException::new);
        Station downStation = stationRepository.findById(request.getDownStationId())
                .orElseThrow(NoSuchElementException::new);
        Section section = sectionRepository.save(Section.of(upStation, downStation, request.getDistance()));

        Line persistLine = lineRepository.save(
                Line.of(request.getName(),
                        request.getColor(),
                        section));
        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return LineResponse.listOf(lines);
    }

    @Transactional(readOnly = true)
    public LineResponse findById(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(NoSuchElementException::new);
        return LineResponse.of(line);
    }

    public LineResponse update(Long id, LineRequest lineRequest) {
        Line persistLine = lineRepository.findById(id).orElseThrow(NoSuchElementException::new);
        Station upStation = stationRepository.findById(lineRequest.getUpStationId()).orElseThrow(NoSuchElementException::new);
        Station downStation = stationRepository.findById(lineRequest.getDownStationId()).orElseThrow(NoSuchElementException::new);
        Line updateLine = Line.of(lineRequest.getName()
                                , lineRequest.getColor()
                                , Section.of(upStation, downStation, lineRequest.getDistance()));

        persistLine.update(updateLine);
        return LineResponse.of(persistLine);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }
}
