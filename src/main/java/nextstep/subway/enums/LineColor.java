package nextstep.subway.enums;

import java.util.Arrays;
import nextstep.subway.exception.LineNotFoundException;

public enum LineColor {
    RED("bg-red-600"),
    GREEN("bg-green-600");

    private String colorName;

    LineColor(String colorName) {
        this.colorName = colorName;
    }

    public static LineColor getLineColorByName(String colorName) {
        return Arrays.stream(LineColor.values())
            .filter(lineColor -> lineColor.getColorName().equals(colorName))
            .findFirst()
            .orElseThrow(LineNotFoundException::new);
    }

    public String getColorName() {
        return colorName;
    }


}
