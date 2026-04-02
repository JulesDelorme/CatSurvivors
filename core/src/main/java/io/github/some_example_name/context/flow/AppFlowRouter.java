package io.github.some_example_name.context.flow;

/**
 * Adapte le flux applicatif vers la couche d'affichage réelle.
 */
public interface AppFlowRouter {
    /**
     * Affiche l'écran correspondant à l'état applicatif fourni.
     */
    void show(AppFlowState state);
}
