package com.example.csemaster.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListUtils {
    public static boolean compareList(List<String> bass, List<String> target) {
        Set<String> set = new HashSet<>(bass);
        set.addAll(target);

        // 비교 대상과 합집합 후에도 bass 리스트와 사이즈가 동일 하다면 서로 같음
        return set.size() == bass.size();
    }

}
