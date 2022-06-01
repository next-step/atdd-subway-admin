package nextstep.subway.application;

import nextstep.subway.consts.ErrorMessage;
import nextstep.subway.domain.*;
import nextstep.subway.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

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
        Line persistLine = lineRepository.save(lineRequest.toLine(upStation, downStation));
        return LineResponse.of(persistLine, generateStationResponses(persistLine));
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(line -> LineResponse.of(line, generateStationResponses(line)))
                .collect(Collectors.toList());
    }

    public LineResponse findLineById(Long id) {
        return lineRepository.findById(id)
                .map(line -> LineResponse.of(line, generateStationResponses(line)))
                .orElseThrow(() -> new NoSuchElementException(ErrorMessage.ERROR_LINE_NOT_EXIST));
    }

    @Transactional
    public void updateLine(Long id, LineUpdateRequest lineUpdateRequest) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(ErrorMessage.ERROR_LINE_NOT_EXIST));
        line.update(lineUpdateRequest.toLine(line.getSections()));
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    private List<StationResponse> generateStationResponses(Line line) {
        return StationResponse.from(line.getSections());
    }

    @Transactional
    public void addSection(Long id, SectionRequest sectionRequest) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(ErrorMessage.ERROR_LINE_NOT_EXIST));
        Station upStation = findStation(sectionRequest.getUpStationId());
        Station downStation = findStation(sectionRequest.getDownStationId());
        line.addSection(new Section(upStation, downStation, sectionRequest.getDistance()));
    }

    private Station findStation(Long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(ErrorMessage.ERROR_STATION_NOT_EXIST));
    }
}
