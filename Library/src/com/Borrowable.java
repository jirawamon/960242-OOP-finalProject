package com;

public interface Borrowable {
    boolean borrowItem(Member member);
    void returnItem();
}