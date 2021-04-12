package com.rainchat.rainlib.placeholder;

public interface PlaceholderSupply<T> {
    Class<T> forClass();

    String getReplacement(String forKey);
}
