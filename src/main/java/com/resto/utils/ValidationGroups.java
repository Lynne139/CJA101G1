package com.resto.utils;

import jakarta.validation.GroupSequence;

public interface ValidationGroups {
    interface First {}
    interface Second {}

    @GroupSequence({First.class, Second.class})
    interface Ordered {} //自動先驗 First 再驗 Second 的順序群組
}
