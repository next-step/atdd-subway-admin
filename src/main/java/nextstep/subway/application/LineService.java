package nextstep.subway.application;

import java.util.List;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequestDTO;
import nextstep.subway.dto.LineResponseDTO;
import nextstep.subway.dto.LineResponsesDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LineService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponseDTO saveLine(LineRequestDTO lineRequestDTO) {
        Line line = lineRequestDTO.toLine();
        linkStations(lineRequestDTO, line);
        Line savedLine = lineRepository.save(line);
        return LineResponseDTO.of(savedLine);
    }

    private void linkStations(LineRequestDTO lineRequestDTO, Line line) {
        if (lineRequestDTO.getUpStationId() != null) {
            Station upStation = stationRepository.findById(lineRequestDTO.getUpStationId())
                    .orElseThrow(() -> new IllegalArgumentException("[ERROR] 상단 ID가 해당하는 지하철역이 없습니다."));
            line.addStation(upStation);
        }
        if (lineRequestDTO.getDownStationId() != null) {
            Station downStation = stationRepository.findById(lineRequestDTO.getDownStationId())
                    .orElseThrow(() -> new IllegalArgumentException("[ERROR] 하단 ID에 해당하는 지하철역이 없습니다."));

            line.addStation(downStation);
        }
    }

    @Transactional(readOnly = true)
    public LineResponsesDTO findAll() {
        List<Line> lines = lineRepository.findAllByDeletedFalse();
        return LineResponsesDTO.of(lines);
    }

    @Transactional(readOnly = true)
    public LineResponseDTO findOne(Long lineId) {
        Line line = getLine(lineId);
        return LineResponseDTO.of(line);
    }

    @Transactional
    public void deleteLine(Long lineId) {
        Line line = getLine(lineId);
        line.delete();
    }

    @Transactional
    public void updateLineInfo(Long id, LineRequestDTO lineRequestDTO) {
        Line line = getLine(id);
        line.update(lineRequestDTO.getName(), lineRequestDTO.getColor());
    }

    private Line getLine(Long lineId) {
        return lineRepository.findByIdAndDeletedFalse(lineId)
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] ID에 해당하는 노선이 없습니다."));
    }
}
