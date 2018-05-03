import com.mongodb.casbah.Imports._
import com.github.mikey.jsonapp._
import org.scalatra._
import javax.servlet.ServletContext

class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext) {
    val mongoClient =  MongoClient()
    val mongoColl = mongoClient("casbah_test")("test_data")
    
    context.mount(new JSONServerlet(mongoColl), "/*")
  }
}
