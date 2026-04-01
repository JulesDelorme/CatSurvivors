package io.github.some_example_name.context.flow;

// Adapteur entre le flux applicatif et la couche d'affichage réelle.
public interface AppFlowRouter {
    void show(AppFlowState state);
}
