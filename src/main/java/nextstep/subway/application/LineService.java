package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.repository.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
public class LineService {

    private LineRepository lineRepository;

    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository,
                       StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Line line = lineRepository.save(lineRequest.toLine());
        addStation(line, lineRequest.getUpStationId());
        addStation(line, lineRequest.getDownStationId());
        return LineResponse.of(line);
    }

    @Transactional(readOnly = true)
    public LineResponse finddById(Long id) {
        return LineResponse.of(lineRepository.getById(id));
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<LineResponse> responses = new ArrayList<>();
        lineRepository.findAll().stream().forEach(
                line -> responses.add(LineResponse.of(line))
        );
        return responses;
    }

    @Transactional
    public LineResponse updateById(Long id, LineRequest lineRequest) {
        Line line = lineRepository.getById(id);
        return LineResponse.of(lineRepository.save(line.updateInfo(lineRequest)));
    }

    @Transactional
    public void deleteById(Long id) {
        stationRepository.deleteByLineId(id);
        lineRepository.deleteById(id);
    }

    private void addStation(Line line, Long id) {
        line.addStation(getStation(id));
    }

    private Station getStation(Long id) {
        return stationRepository.getById(id);
    }

}
