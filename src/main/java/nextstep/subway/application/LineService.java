package nextstep.subway.application;

import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.line.LineRepository;
import nextstep.subway.domain.line.LineStation;
import nextstep.subway.domain.line.LineStationRepository;
import nextstep.subway.domain.station.Station;
import nextstep.subway.domain.station.StationRepository;
import nextstep.subway.dto.request.LineRequest;
import nextstep.subway.dto.request.LineSectionRequest;
import nextstep.subway.dto.response.LineReponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;
    private LineStationRepository lineStationRepository;

    @Autowired
    public LineService(LineRepository lineRepository, StationRepository stationRepository, LineStationRepository lineStationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.lineStationRepository = lineStationRepository;
    }

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    @Transactional
    public LineReponse createLine(LineRequest lineRequest) {
        Line line = lineRepository.save(lineRequestToLine(lineRequest));
        return LineReponse.of(line);
    }

    public List<LineReponse> getLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(line -> LineReponse.of(line))
                .collect(Collectors.toList());
    }

    public LineReponse getLine(Long id) {
        Line line = lineRepository.getById(id);
        return LineReponse.of(line);
    }

    @Transactional
    public LineReponse updateLine(Long id, LineRequest lineRequest) {
        Line line = lineRepository.getById(id);
        line.updateLine(lineRequest);
        return LineReponse.of(lineRepository.save(line));
    }

    @Transactional
    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    private Line lineRequestToLine(LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        LineStation lineStation = new LineStation(lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance());
        line.addLineStation(lineStation);

        return line;
    }

    @Transactional
    public LineReponse addLineSections(Long id, LineSectionRequest lineSectionRequest) {
        checkValidationForAddLineStation(lineSectionRequest);

        Line line = lineRepository.getById(id);
        LineStation lineStation = lineSectionRequest.toLineStation(lineSectionRequest);
        line.addLineStation(lineStation);

        return LineReponse.of(lineRepository.save(line));
    }

    private void checkValidationForAddLineStation(LineSectionRequest lineSectionRequest) {
        Optional<Station> upStation = stationRepository.findById(lineSectionRequest.getUpStationId());
        Optional<Station> downStation = stationRepository.findById(lineSectionRequest.getDownStationId());

        if (!upStation.isPresent() || !downStation.isPresent()) {
            throw new RuntimeException();
        }
    }

    public void deleteLineSections(Long lineId, Long stationId) {
        // 종점을 삭제하는 경우
        // 라인 조회후 없으면 익셉션, 스테이션 조회 후 없으면 익셉션
        // 있으면 라인스테이션에서 찾아서 삭제 시킨다.
        Station stationToDelete = stationRepository.findById(stationId)
                .orElseThrow(RuntimeException::new);
        Line line = lineRepository.findById(lineId)
                .orElseThrow(RuntimeException::new);

        // 라인에서 해당 스테이션을 받는다.
        line.removeStation(stationToDelete);
        // 섹션쪽으로 넘기고 업스테이션, 다운스테이션 같은 것들을 삭제한다.






        // 중간역을 삭제하는 경우

        // 첫역을 삭제하는 경우
    }
}
