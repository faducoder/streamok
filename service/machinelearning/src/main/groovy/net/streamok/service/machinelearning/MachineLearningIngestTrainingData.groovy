package net.streamok.service.machinelearning

import io.vertx.core.eventbus.DeliveryOptions
import net.streamok.fiber.node.api.Fiber
import net.streamok.fiber.node.api.FiberDefinition
import twitter4j.Query
import twitter4j.TwitterFactory
import twitter4j.conf.ConfigurationBuilder

import static io.vertx.core.json.Json.encode
import static net.streamok.lib.vertx.Handlers.completeIteration
import static net.streamok.service.machinelearning.FeatureVector.textFeatureVector

class MachineLearningIngestTrainingData implements FiberDefinition {

    @Override
    String address() {
        'machineLearning.ingestTrainingData'
    }

    @Override
    Fiber handler() {
        { operation ->
            def source = operation.nonBlankHeader('source')

            if(source.startsWith('twitter:')) {
                def twitterTag = source.replaceFirst('twitter:', '')
                def collection = operation.nonBlankHeader('collection')

                def twitterConfiguration = new ConfigurationBuilder()
                twitterConfiguration.setOAuthConsumerKey("qfSY4xyuyBeZ9pppAY7R8NASl")
                        .setOAuthConsumerSecret("ZO2NQJQOBk98JfxF6l8AUzzw8gSs4dRR1lEnvsgMG0NFA224mV")
                        .setOAuthAccessToken("804661469086359552-IWeaoXXpsCfMlkL9TFVkQ46cSU2t5jI")
                        .setOAuthAccessTokenSecret("BSwqXggpm01PldZtAfPGwAQtft6Qvi2jPWvkGuUEIrLaT");
                def twitter = new TwitterFactory(twitterConfiguration.build()).instance

                def query = new Query("lang:en ${twitterTag}")
                query.setCount(1000)
                def positiveTweets = twitter.search(query).tweets

                query = new Query('lang:en #dogs')
                query.setCount(1000)
                def negativeTweets = twitter.search(query).tweets

                def positives = positiveTweets.collect { encode(textFeatureVector(it.text, twitterTag, true)) }
                def negatives = negativeTweets.collect { encode(textFeatureVector(it.text, twitterTag, false)) }
                completeIteration(positives + negatives) { iteration ->
                    operation.vertx().eventBus().send('document.save', iteration.element(), new DeliveryOptions().addHeader('collection', 'training_texts_' + collection)) {
                        iteration.ifFinished {
                            operation.reply(null)
                        }
                    }
                }
            } else {
                operation.fail(100, "Unknown ingestion source: ${source}")
            }
        }
    }

}