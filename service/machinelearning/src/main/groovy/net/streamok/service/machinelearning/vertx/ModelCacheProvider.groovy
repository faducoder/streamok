package net.streamok.service.machinelearning.vertx

import net.streamok.fiber.node.api.DependencyProvider
import net.streamok.service.machinelearning.common.ModelCache

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
