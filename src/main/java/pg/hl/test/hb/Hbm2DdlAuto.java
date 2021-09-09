package pg.hl.test.hb;

import lombok.Getter;

@Getter
public enum Hbm2DdlAuto {
    CreateDrop("create-drop"),
    Validate("validate");

    private final String value;

    Hbm2DdlAuto(String value) {
        this.value = value;
    }
}
