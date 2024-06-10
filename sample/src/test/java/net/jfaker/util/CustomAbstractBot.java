package net.jfaker.util;

import net.jfaker.bot.AbstractBot;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class CustomAbstractBot<B> extends AbstractBot<B> {

    public Set<B> buildSet(final long size){
        return Stream.generate(this::build)
                .limit(size)
                .collect(Collectors.toSet());
    }

}