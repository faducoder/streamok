package net.streamok.service.machinelearning

import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.json.Json
import net.streamok.fiber.node.api.Fiber
import net.streamok.fiber.node.api.FiberContext
import net.streamok.fiber.node.api.FiberDefinition
import org.apache.commons.lang3.Validate
import twitter4j.Query
import twitter4j.QueryResult
import twitter4j.Status
import twitter4j.TwitterFactory
import twitter4j.conf.ConfigurationBuilder

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

import static io.vertx.core.json.Json.encode
import static net.streamok.service.machinelearning.FeatureVector.textFeatureVector

class MachineLearningIngestTrainingData implements FiberDefinition {

    @Override
    String address() {
        'machineLearning.ingestTrainingData'
    }

    @Override
    Fiber handler() {
        { FiberContext fiberContext ->
            def source = fiberContext.nonBlankHeader('source')
            def twitterTag = source.replaceFirst('twitter:', '')
            def collection = fiberContext.nonBlankHeader('collection')

            def cb = new ConfigurationBuilder()
            cb.setOAuthConsumerKey("qfSY4xyuyBeZ9pppAY7R8NASl")
                    .setOAuthConsumerSecret("ZO2NQJQOBk98JfxF6l8AUzzw8gSs4dRR1lEnvsgMG0NFA224mV")
                    .setOAuthAccessToken("804661469086359552-IWeaoXXpsCfMlkL9TFVkQ46cSU2t5jI")
                    .setOAuthAccessTokenSecret("BSwqXggpm01PldZtAfPGwAQtft6Qvi2jPWvkGuUEIrLaT");
            def twitter = new TwitterFactory(cb.build()).instance
            Query query = new Query("lang:en ${twitterTag}")
            query.setCount(1000)
            def result = twitter.search(query)


            query = new Query('lang:en #dogs')
            query.setCount(1000)
            def result2 = twitter.search(query)

            def sempahore = new AtomicInteger(result2.getTweets().size() + result.getTweets().size())
            for (Status status : result2.getTweets().findAll { it.lang == 'en' }) {
                fiberContext.vertx().eventBus().send('document.save', encode(textFeatureVector(status.text, twitterTag, false)), new DeliveryOptions().addHeader('collection', 'training_texts_' + collection)) {
                    if(sempahore.decrementAndGet() == 0) {
                        fiberContext.reply(null)
                    }
                }
            }
            for (Status status : result.getTweets()) {
                fiberContext.vertx().eventBus().send('document.save', encode(textFeatureVector(status.text, twitterTag, true)), new DeliveryOptions().addHeader('collection', 'training_texts_' + collection)) {
                    if(sempahore.decrementAndGet() == 0) {
                        fiberContext.reply(null)
                    }
                }
            }
        }
    }

}