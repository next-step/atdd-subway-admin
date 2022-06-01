package nextstep.subway.domain.LineStation;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.List;

@Embeddable
public class LineStations {

    @OneToMany(mappedBy = "")
    List<LineStation> lineStations;
}
