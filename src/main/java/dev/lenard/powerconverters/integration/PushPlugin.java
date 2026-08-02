package dev.lenard.powerconverters.integration;

/**
 * A producer-side integration that has to actively push energy at its neighbours each tick,
 * rather than waiting to be pulled from.
 *
 * <p>Needed because BuildCraft machines never pull: engines push into them. A provider that
 * only answers pull requests is therefore invisible to a Quarry sitting right next to it, and
 * would require a wooden kinesis pipe as an adapter, which is not what players expect from
 * something standing in for an engine.
 */
public interface PushPlugin {

    void push();
}
