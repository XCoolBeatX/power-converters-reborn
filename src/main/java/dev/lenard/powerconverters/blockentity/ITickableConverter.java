package dev.lenard.powerconverters.blockentity;

/**
 * Implemented by block entities that need a server tick.
 *
 * <p>Only the push-style producers actually need this. The EU and MJ producers are pulled
 * from by their respective networks, and every consumer is pushed into, so they stay passive.
 */
public interface ITickableConverter {

    void serverTick();
}
