package net.streamok.service.setup

import io.vertx.core.Handler
import net.streamok.fiber.node.api.OperationContext
import net.streamok.fiber.node.api.OperationDefinition
import net.streamok.fiber.node.api.OperationHandler

import java.util.concurrent.CountDownLatch

import static java.util.concurrent.TimeUnit.SECONDS

class TagLoadingOperation implements OperationDefinition {

    private final String DEFAULT_SOURCE = 'twitter'
    private final String ML_DATASET_COLLECTION = 'testing' // TODO, load from config or sth
    private final TAG_COLLECTION = 'tags'
    private final MAX_TAGS_READ = 10000

    @Override
    String address() {
        'loadTags'
    }

    @Override
    OperationHandler handler() {
        { context ->
            context.send('document.find', ['size': MAX_TAGS_READ], ['collection': TAG_COLLECTION], Object) { tags ->
                def semaphore = new CountDownLatch(tags.size())
                tags.collect { createTag(it['name'], context) {
                    semaphore.countDown()
                } }
                semaphore.await(15, SECONDS)
                teachMachine(context)
            }
            context.reply(null)
        }
    }

    private

    def createTag(String tag, OperationContext context, Handler<Object> callback){
        def header = [
                'source': "$DEFAULT_SOURCE:$tag",
                'collection': ML_DATASET_COLLECTION
        ]
        context.send('machineLearning.ingestTrainingData', null, header, Object, callback)
    }

    def teachMachine(OperationContext context){
        context.send('machineLearning.trainTextLabelModel', null, ['dataset': ML_DATASET_COLLECTION])
    }
}
