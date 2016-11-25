package net.streamok.service.machinelearning

import net.streamok.fiber.node.api.DependencyProvider

class ModelCacheProvider implements DependencyProvider {

    @Override
    String key() {
        'modelCache'
    }

    @Override
    Object dependency() {
        new ModelCache()
    }

}
