package com.arkflame.mineclans.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Paginator<T> {
    private final List<T> items;
    private final int itemsPerPage;

    public Paginator(Set<T> items, int itemsPerPage) {
        this.items = new ArrayList<>(items);
        this.itemsPerPage = itemsPerPage;
    }

    public int getTotalPages() {
        return (int) Math.ceil((double) items.size() / itemsPerPage);
    }

    public Set<T> getPage(int pageNumber) {
        int startIndex = (pageNumber - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, items.size());
        
        if (startIndex >= items.size() || pageNumber < 1) {
            return Collections.emptySet();
        }
        
        return new HashSet<>(items.subList(startIndex, endIndex));
    }
}