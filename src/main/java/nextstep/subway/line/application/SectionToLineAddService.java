package nextstep.subway.line.application;

import nextstep.subway.line.domain.*;
import nextstep.subway.line.dto.LineStationCreateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class SectionToLineAddService {

    public SectionToLineAddService(LineRepository lineRepository){
        this.lineRepository = lineRepository;
    }

    private final LineRepository lineRepository;

    public void addSectionToLine(long lineId, LineStationCreateRequest request){
        Line line = lineRepository.findById(lineId).orElseThrow(() -> new IllegalArgumentException("없는 노선입니다."));
        LineStation lineStation = LineStation.of(request.getUpStationId(), request.getDownStationId(), request.getDistance());
        line.addLineStation(lineStation);
    }

}
