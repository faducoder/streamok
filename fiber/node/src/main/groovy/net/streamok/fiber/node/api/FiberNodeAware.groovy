package net.streamok.fiber.node.api

import net.streamok.fiber.node.FiberNode

interface FiberNodeAware {

    void fiberNode(FiberNode fiberNode)

}