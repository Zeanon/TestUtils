package de.zeanon.testutils.plugin.utils.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;


@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum CaseSensitive {

    CONFIRM(true),
    DENY(false);

    @Getter
    @Accessors(fluent = true)
    private final boolean confirm;
}