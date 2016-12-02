package net.streamok.service.machinelearning

import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.json.Json
import net.streamok.fiber.node.api.Fiber
import net.streamok.fiber.node.api.FiberContext
import net.streamok.fiber.node.api.FiberDefinition
import twitter4j.Query
import twitter4j.QueryResult
import twitter4j.Status
import twitter4j.TwitterFactory
import twitter4j.conf.ConfigurationBuilder

import static io.vertx.core.json.Json.encode
import static net.streamok.service.machinelearning.FeatureVector.textFeatureVector

class MachineLearningIngestTrainingData implements FiberDefinition {

    @Override
    String address() {
        'machinelearning.ingestTrainingData'
    }

    @Override
    Fiber handler() {
        new Fiber() {
            @Override
            void handle(FiberContext fiberContext) {
                def source = fiberContext.nonBlankHeader('source')
                def twitterTag = source.replaceFirst('twitter:', '')

                def cb = new ConfigurationBuilder()
                cb.setOAuthConsumerKey("bnI9hm5vYMnNR7zV5BagxKW6R")
                        .setOAuthConsumerSecret("CrhMdKRt3zw1ZCqdTsXCLtQmG84zWzIc8noxP8lGMoD9RqgVUr")
                        .setOAuthAccessToken("18531491-ZARem17HyP6wZnMFhKNeFiN2qQUkzdi54KrFoF1rU")
                        .setOAuthAccessTokenSecret("nhcYaYpIjFh9bsSaRBzD1fIrpb1ugNOI79heeb83lLFOL");
                def twitter = new TwitterFactory(cb.build()).instance
                Query query = new Query("lang:en ${twitterTag}")
                query.setCount(1000)
                QueryResult result = twitter.search(query);
                for (Status status : result.getTweets()) {
                    fiberContext.vertx().eventBus().send('document.save', encode(textFeatureVector(status.text, twitterTag, true)), new DeliveryOptions().addHeader('collection', 'training_texts'))
                }

                query = new Query('"lang:en #dogs"')
                query.setCount(1000)
                result = twitter.search(query);
                for (Status status : result.getTweets().findAll{ it.lang == 'en' }) {
                    fiberContext.vertx().eventBus().send('document.save', encode(textFeatureVector(status.text, twitterTag, false)), new DeliveryOptions().addHeader('collection', 'training_texts'))
                }

                fiberContext.reply(null)
            }
        }
    }

}