package nextstep.subway.application;

import nextstep.subway.domain.*;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final SectionRepository sectionRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository, SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository;
    }

    @Transactional
    public LineResponse createLine(LineRequest request) {
        Line line = lineRepository.save(request.createLine());
        Station upStation = findStation(request.getUpStationId());
        Station downStation = findStation(request.getDownStationId());
        line.addStation(upStation);
        line.addStation(downStation);
        sectionRepository.save(new Section(line, upStation));
        sectionRepository.save(new Section(line, upStation, downStation, request.getDistance()));

        return LineResponse.of(line, Arrays.asList(upStation, downStation));
    }

    @Transactional
    public void deleteLine(Long id) {
        List<Station> stations = stationRepository.findAllByLineId(id);
        for (Station station : stations) {
            station.setLine(null);
        }
        List<Section> sections = sectionRepository.findAllByLineId(id);
        for (Section section : sections) {
            section.setLine(null);
        }
        lineRepository.deleteById(id);
    }

    @Transactional
    public void updateLine(Long id, LineRequest request) {
        Line line = findById(id);
        line.update(request);
    }

    public LineResponse toLineResponse(Long id) {
        Line line = findById(id);
        return LineResponse.of(line, line.getStations());
    }

    public List<LineResponse> toLineResponses() {
        List<LineResponse> responses = new LinkedList<>();
        List<Line> lines = lineRepository.findAll();
        for (Line line : lines) {
            responses.add(LineResponse.of(line, line.getStations()));
        }

        return responses;
    }

    public Line findById(Long id) {
        return lineRepository.findById(id)
                             .orElseThrow(() -> new NoSuchElementException("지하철 노선이 존재하지 않습니다."));
    }

    public Station findStation(Long stationId) {
        return stationRepository.findById(stationId)
                                .orElseThrow(() -> new NoSuchElementException("지하철 역이 존재하지 않습니다"));
    }

    @Transactional
    public void addSection(Long id, SectionRequest request) {
        Line line = findById(id);
        Station upStation = findStation(request.getUpStationId());
        Station downStation = findStation(request.getDownStationId());
        Section section = sectionRepository.save(new Section(line, upStation, downStation, request.getDistance()));
        line.addSection(section);
    }
}
