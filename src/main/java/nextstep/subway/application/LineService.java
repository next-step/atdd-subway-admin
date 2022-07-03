package nextstep.subway.application;

import nextstep.subway.domain.*;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse createLine(LineRequest request) {
        Station upStation = findStationById(request.getUpStationId());
        Station downStation = findStationById(request.getDownStationId());
        Line line = lineRepository.save(new Line(request.getName(), request.getColor(), upStation, downStation, request.getDistance()));

        return LineResponse.from(line);
    }

    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    public void updateLine(Long id, LineRequest request) {
        Line line = findById(id);
        line.update(request);
    }

    public LineResponse toLineResponse(Long id) {
        Line line = findById(id);
        return LineResponse.from(line);
    }

    public List<LineResponse> toLineResponses() {
        List<LineResponse> responses = new LinkedList<>();
        List<Line> lines = lineRepository.findAll();
        for (Line line : lines) {
            responses.add(LineResponse.from(line));
        }

        return responses;
    }

    public Line findById(Long id) {
        return lineRepository.findById(id)
                             .orElseThrow(() -> new NoSuchElementException("지하철 노선이 존재하지 않습니다."));
    }

    public Station findStationById(Long stationId) {
        return stationRepository.findById(stationId)
                                .orElseThrow(() -> new NoSuchElementException("지하철 역이 존재하지 않습니다"));
    }

    public void addSection(Long id, SectionRequest request) {
        Line line = findById(id);
        Station upStation = findStationById(request.getUpStationId());
        Station downStation = findStationById(request.getDownStationId());
        line.addLineStation(new Section(line, upStation, downStation, request.getDistance()));
    }

    public void removeSectionByStationId(Long lineId, Long stationId) {
        Line line = findById(lineId);
        Station delStation = findStationById(stationId);
        line.removeLineStation(delStation);
    }
}
