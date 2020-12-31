package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private static final int INITIAL_DISTANCE = 0;
    private LineRepository lineRepository;
    private StationRepository stationRepository;


    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    private Station findStationById(Long id) {
       return stationRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    public LineResponse saveLine(LineRequest request) {
        Station upStation = findStationById(request.getUpStationId());
        Station downStation = findStationById(request.getDownStationId());
        Line persistLine = lineRepository.save(request.toLine());
        persistLine.addSection(new Section(null, upStation, INITIAL_DISTANCE));
        persistLine.addSection(new Section(upStation, downStation, request.getDistance()));
        return LineResponse.of(persistLine, persistLine.getStations());
    }

    public List<LineResponse> findByAll() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(line -> LineResponse
                        .of(line, line.getStations()))
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(NoSuchElementException::new);
        return LineResponse.of(line, line.getStations());
    }

    public LineResponse updateLine(LineRequest lineRequest, Long id) {
        Line line = lineRepository.findById(id).orElseThrow(NoSuchElementException::new);
        line.update(lineRequest.toLine());
        return LineResponse.of(line, line.getStations());
    }

    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    public LineResponse addStation(Long id, SectionRequest sectionRequest) {
        Section newSection = new Section(findStationById(sectionRequest.getUpStationId()),
                                    findStationById(sectionRequest.getDownStationId()),
                                    sectionRequest.getDistance());

        Line line = lineRepository.findById(id).orElseThrow(NoSuchElementException::new);

        Sections sections = line.getSections();
        sections.updateSection(line, newSection);
        return LineResponse.of(line, line.getStations());
    }
}
