package nextstep.subway.line.domain;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class DistanceToLongConverter implements AttributeConverter<Distance, Long> {

    @Override
    public Long convertToDatabaseColumn(Distance distance) {
        return distance.get();
    }

    @Override
    public Distance convertToEntityAttribute(Long distance) {
        return new Distance(distance);
    }
}
