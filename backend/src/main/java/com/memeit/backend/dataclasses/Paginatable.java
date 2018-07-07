package com.memeit.backend.dataclasses;

import java.util.List;

/**
 * Created by Jv on 7/5/2018.
 */

public class Paginatable<T> {
    public static final int STEP=10;
    long page;
    List<T> items;
    long total;


    public boolean hasMore(){
        return (page*STEP)<total;
    }
}
