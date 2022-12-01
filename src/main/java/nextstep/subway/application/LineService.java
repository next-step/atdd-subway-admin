package nextstep.subway.application;

import nextstep.subway.consts.ErrorMessage;
import nextstep.subway.domain.*;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Station upStation = findStationById(lineRequest.getUpStationId());
        Station downStation = findStationById(lineRequest.getDownStationId());
        /*Line persistLine = lineRepository.save(lineRequest.toLine(upStation, downStation));
        return LineResponse.of(persistLine);*/
        Line line = lineRequest.toLine();
        Sections sections = new Sections();
        sections.add(new Section(upStation, downStation, lineRequest.getDistance(), line));
        line.addSections(sections);
        return LineResponse.of(line);
    }

    private Station findStationById(Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new NoSuchElementException(ErrorMessage.ERROR_STATION_NOT_EXIST));
    }


    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(line -> LineResponse.of(line))
                .collect(Collectors.toList());
    }

    public LineResponse findLineByid(Long id) {
        return lineRepository.findById(id)
                .map(line -> LineResponse.of(line))
                .orElseThrow(() -> new NoSuchElementException(ErrorMessage.ERROR_LINE_NOT_EXIST));
    }

    @Transactional
    public void updateLine(Long id, LineRequest lineRequest) {
        Line originLine = lineRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(ErrorMessage.ERROR_LINE_NOT_EXIST));
        //originLine.update(lineRequest.toLine(originLine.getSections()));
        originLine.update(lineRequest.toLine());
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }
}
