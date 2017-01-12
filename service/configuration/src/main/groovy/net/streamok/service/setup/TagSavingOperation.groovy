package net.streamok.service.setup

import net.streamok.fiber.node.api.OperationDefinition
import net.streamok.fiber.node.api.OperationHandler
import static io.vertx.core.json.Json.encode

class TagSavingOperation implements OperationDefinition {

    // TODO, reuse/share somehow
    private final TAGS_COLLECTION = 'tags'

    @Override
    String address() {
        'saveTags'
    }

    @Override
    OperationHandler handler() {
        { context ->
            def tags = context.body(String[]).collect { [name: it, '_id': it] }

            tags.collect {
                context.send('document.save', it, ['collection': TAGS_COLLECTION])
            }
        }
    }
}
