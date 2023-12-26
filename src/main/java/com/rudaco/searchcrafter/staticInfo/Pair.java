package com.rudaco.searchcrafter.staticInfo;

import java.util.Objects;

public class Pair<T, P> {
    public T first;
    public P second;
    public Pair(T first, P second){
        this.first = first;
        this.second = second;
    }

    @Override
    public int hashCode() {
        return Objects.hash(first,second);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) obj;
        return Objects.equals(first, pair.first) &&
                Objects.equals(second, pair.second);
    }
}
