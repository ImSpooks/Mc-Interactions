package me.imspooks.mcinteractions;

import java.util.concurrent.CompletableFuture;

public class Interaction<T> extends CompletableFuture<T> {

    @SuppressWarnings("unchecked")
    void completeUnsafe(Object object) {
        this.complete((T) object);
    }
}
