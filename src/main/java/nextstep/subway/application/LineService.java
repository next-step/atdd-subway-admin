package nextstep.subway.application;

import nextstep.subway.domain.*;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.exception.LineException;
import nextstep.subway.exception.StationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.exception.LineExceptionMessage.NONE_EXISTS_LINE;
import static nextstep.subway.exception.StationExceptionMessage.NONE_EXISTS_STATION;

@Service
@Transactional(readOnly = true)
public class LineService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Station upStation = findStation(lineRequest.getUpStationId());
        Station downStation = findStation(lineRequest.getDownStationId());
        Line persistStation = lineRepository.save(lineRequest.toLine(upStation, downStation));
        return LineResponse.from(persistStation);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream().map(LineResponse::from).collect(Collectors.toList());
    }

    public LineResponse findLine(Long id) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new LineException(NONE_EXISTS_LINE.getMessage()));
        return LineResponse.from(line);
    }

    @Transactional
    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public void modifyLine(Long id, LineRequest lineRequest) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new LineException(NONE_EXISTS_LINE.getMessage()));
        line.modifyLine(lineRequest.getName(), lineRequest.getColor());
    }

    private Station findStation(Long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new StationException(NONE_EXISTS_STATION.getMessage()));
    }

    @Transactional
    public void saveSection(Long id, SectionRequest sectionRequest) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new LineException(NONE_EXISTS_LINE.getMessage()));
        Station upStation = findStation(sectionRequest.getUpStationId());
        Station downStation = findStation(sectionRequest.getDownStationId());
        line.addSection(new Section(line, upStation, downStation, sectionRequest.getDistance()));
    }

    @Transactional
    public void deleteSection(Long lineId,Long stationId) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new LineException(NONE_EXISTS_LINE.getMessage()));
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new StationException(NONE_EXISTS_STATION.getMessage()));
        line.deleteSection(line,station);
    }
}
