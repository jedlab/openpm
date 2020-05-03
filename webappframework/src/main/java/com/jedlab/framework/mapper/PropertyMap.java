package com.jedlab.framework.mapper;

public interface PropertyMap<S, D>
{

    public D map(S source);
    public D map(S source, D instance);

}
