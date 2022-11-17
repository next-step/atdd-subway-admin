package nextstep.subway.application;

import nextstep.subway.domain.*;
import nextstep.subway.dto.CreateLineDto;
import nextstep.subway.dto.DtoConverter;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.UpdateLineDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {

    private final LineRepository lineRepository;

    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse register(CreateLineDto dto) {
        Station upStation = findStationById(dto.getUpStationId());
        Station downStation = findStationById(dto.getDownStationId());
        Distance distance = new Distance(dto.getDistance());
        Section section = new Section(upStation, downStation, distance);
        Line line = DtoConverter.toLineEntity(dto);
        line.addSection(section);
        return LineResponse.of(lineRepository.save(line));
    }

    @Transactional
    public void update(long id, UpdateLineDto dto) {
        Line line = findLineById(id);
        line.update(DtoConverter.toLineEntity(dto));
    }

    @Transactional(readOnly = true)
    public List<LineResponse> fetchAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    private Station findStationById(long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 역은 존재하지 않습니다."));
    }

    @Transactional(readOnly = true)
    public LineResponse fetchLine(long id) {
        Line line = findLineById(id);
        return LineResponse.of(line);
    }

    private Line findLineById(long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 노선은 존재하지 않습니다."));
    }

    @Transactional
    public void delete(long id) {
        lineRepository.deleteById(id);
    }
}
