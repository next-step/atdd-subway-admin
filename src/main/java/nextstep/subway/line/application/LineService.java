package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.application.SectionService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private SectionService sectionService;
    private LineRepository lineRepository;
    private StationRepository stationRepository;


    public LineService(LineRepository lineRepository, StationRepository stationRepository, SectionService sectionService) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.sectionService = sectionService;
    }

    private Station findStationById(Long id) {
       return stationRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    public LineResponse saveLine(LineRequest request) {
        Station upStation = findStationById(request.getUpStationId());
        Station downStation = findStationById(request.getDownStationId());
        Line persistLine = lineRepository.save(request.toLine());
        sectionService.saveSection(request.getDistance(), null, upStation, persistLine);
        sectionService.saveSection(request.getDistance(), upStation, downStation, persistLine);
        List<Station> stations = sectionService.getStations(persistLine);
        return LineResponse.of(persistLine, stations);
    }

    public List<LineResponse> findByAll() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(line -> LineResponse
                        .of(line, sectionService.getStations(line)))
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(NoSuchElementException::new);
        return LineResponse.of(line, sectionService.getStations(line));
    }

    public LineResponse updateLine(LineRequest lineRequest, Long id) {
        Line line = lineRepository.findById(id).orElseThrow(NoSuchElementException::new);
        line.update(lineRequest.toLine());
        return LineResponse.of(line, sectionService.getStations(line));
    }

    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }
}
