package io.github.javiewer.listener;

/**
 * Project: JAViewer
 */

public abstract class EndlessOnScrollListener<I> extends BasicOnScrollListener<I> {

    @Override
    public boolean isEnd() {
        return false;
    }
}
