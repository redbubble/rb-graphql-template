package com.redbubble.perf.scenarios

import com.redbubble.gql.services.product._
import com.redbubble.perf.common.BaseSettings._
import com.redbubble.perf.operations.{DeviceRegistrationOperations, FeedOperations, ProductOperations, SearchOperations}
import com.redbubble.perf.queries.FeedQueries._
import io.gatling.core.Predef._

import scala.concurrent.duration._

final class BloatCache extends Simulation with FeedOperations with DeviceRegistrationOperations with SearchOperations with ProductOperations {
  val works = List(
    "14256085", "24062766", "23142720", "10167660", "24024054", "23015696", "23196825", "24024092", "16449686", "12425235", "23765619",
    "24044092", "22055025", "24066681", "23852986", "23126564", "11804645", "13868455", "23799820", "23572112", "21652595", "23200733",
    "24040359", "24117772", "22929500", "9151833", "17678468", "21686514", "23651020", "23564989", "23805468", "22552856", "10780159",
    "15888380", "8572168", "21652599", "22187776", "21451033", "21864655", "20469124", "21618384", "23949140", "23726376", "17765831",
    "23060947", "22778123", "11860870", "23286587", "22885881", "24011626", "23653582", "24062790", "24112812", "12926456", "22926639",
    "24023476", "23538045", "22926780", "12721213", "21058709", "23611801", "23675861", "22371478", "23666890", "22686693", "23406133",
    "22926805", "14574234", "24064757", "13186328", "23349914", "19071884", "16820040", "12939997", "20988077", "24023423", "23112736",
    "21778422", "22707989", "10602914", "22722771", "12375519", "14063667", "22643952", "23300607", "12018351", "20779235", "23300594",
    "23278026", "23301781", "21652606", "23949154", "16032188", "21636604", "21662683", "21861497", "21922888", "14848783", "23801370",
    "23669057", "9518019", "11264279", "9640866", "23855853", "21582383", "23341664", "11916245", "23666906", "20103635", "23404337",
    "9668485", "19198603", "21857675", "11227009", "11916228", "18448984", "21059606", "22498101", "14603990", "7449246", "14637090",
    "23548646", "21841565", "21652486", "14408334", "20972338", "23667019", "14286051", "23063766", "21652535", "15219063", "18828072",
    "14490668", "23669334", "20061997", "22078719", "14505483", "21841523", "17315271", "14176481", "11474859", "14405254", "13664545",
    "23080921", "23676496", "13215834", "23086748", "22701113", "15022214", "11280682", "9817348", "11380544", "14657570", "14937426",
    "23669644", "22172978", "11484041", "8643528", "14470216", "9592935", "11369689", "14708923", "13250724", "22635198", "14477343",
    "21059313", "11916415", "16314980", "23666222", "17156976", "22070065", "22719843", "22743364", "23109623", "23264438", "12162966",
    "11100659", "17638803", "23404218", "23398279", "14498976", "15549636", "20903856", "13391028", "23666866", "22410572", "17672473",
    "10612017", "23666133", "23666784", "14640887", "22384758", "13962618", "15165667", "9387147", "19876525", "13796252", "13930678",
    "22798496", "23571675", "23457362", "13773160", "23349675", "23667200", "15916595", "11489597", "22597423", "21267342", "22294617",
    "10595936", "22308502", "23563387", "13352826", "15632018", "20348217", "22869020", "9592916", "23193455", "7986895", "16705111",
    "23064481", "16205540", "23128255", "11489290", "13063852", "16059431", "11492184", "11465012", "22744205", "3362072", "9466117",
    "11492185", "16519282", "21059798", "14609276", "14225111", "16261762", "22751399", "13389987", "11490216", "23717790", "14545330",
    "13544585", "14512130", "22802661", "17684960", "16204272", "16587597", "13930730", "21126963", "22950163", "11919344", "21008452",
    "22811822", "15398574", "14817952", "24109031", "12059067", "9898559", "9785372", "15305477", "21652546", "18645070", "12206722",
    "23535610", "19584564", "22388938", "22309618", "18743248", "23615029", "12372416", "18364208", "13649948", "21490883", "12139011",
    "21267381", "15265193", "9785222", "14657242", "22127129", "21201234", "21841499", "11076933", "11547329", "14137558", "14005229",
    "10045983", "21059484", "20820311", "17400416", "14065508", "22933669", "13141090", "19605560", "22133288", "12220307", "11490177",
    "7414586", "14848778", "10875006", "16025467", "15620063", "11218846", "13818274", "15301543", "7230167", "16725789", "14065483",
    "15926120", "17711038", "1228146", "8996865", "13175435", "14671459", "15447380", "11180243", "10275080", "21217321", "21267331",
    "3405773", "13045081", "12726400", "15205850", "15796224", "13483307", "14689489", "22011025", "11189996", "21548716", "15582808",
    "9586809", "22149347", "17191266", "13275089", "11916908", "9692931", "10383373", "16001854", "22586650", "9692286", "22743843",
    "14005190", "3444833", "14404838", "7000381", "11920631", "15967335", "10279789", "14451733", "12347267", "11657650", "13660701",
    "12100486", "23346196", "12780975", "22498939", "22149326", "24184151", "22838750", "15610259", "11490201", "13272311", "8538720",
    "22849256", "16012992", "15587120", "12651815", "17191375", "15557916", "22407993", "14595490", "15284140", "16012371", "22999083"
  )
  val scn = scenario("Bloat Cache")
      .exec(registerDevice)
      .exec(faturedFeeds)
      .pause(1.seconds)
      .foreach(works, "work") {
        exec(availableProducts(WorkId("${w" + "ork}"))).pause(1.seconds)
      }
      .exec(feed(foundFeedCode))
      .pause(2.seconds)
      .exec(feed(foundFeedCode, 30))
      .pause(2.seconds)
      .exec(feed(foundFeedCode, 60))
      .pause(2.seconds)
      .exec(feed(foundFeedCode, 90))
      .pause(2.seconds)
      .exec(feed(foundFeedCode, 120))
      .pause(2.seconds)
      .exec(feed(foundFeedCode, 150))
      .pause(2.seconds)
      .exec(feed(foundFeedCode, 180))
      .pause(2.seconds)
      .exec(feed(foundFeedCode, 210))
      .pause(2.seconds)
      .exec(feed(foundFeedCode, 240))
      .pause(2.seconds)
      .exec(feed(foundFeedCode, 270))
      .pause(2.seconds)
      .exec(feed(foundFeedCode, 300))
      .pause(2.seconds)
      .exec(searchFor("dogs"))
      .pause(2.seconds)
      .exec(searchFor("dogs", 20))
      .pause(2.seconds)
      .exec(searchFor("dogs", 40))
      .pause(2.seconds)
      .exec(searchFor("dogs", 60))
      .pause(2.seconds)
      .exec(searchFor("dogs", 80))
      .pause(2.seconds)
      .exec(searchFor("dogs", 100))
      .pause(2.seconds)
      .exec(searchFor("dogs", 120))
      .pause(2.seconds)
      .exec(searchFor("dogs", 140))
      .pause(2.seconds)
      .exec(searchFor("dogs", 160))
      .pause(2.seconds)
      .exec(searchFor("dogs", 180))
      .pause(2.seconds)
      .exec(searchFor("dogs", 200))
      .pause(2.seconds)
      .exec(searchFor("dogs", 220))
      .pause(2.seconds)
      .exec(searchFor("dogs", 240))
      .pause(2.seconds)
      .exec(searchFor("cats"))
      .pause(2.seconds)
      .exec(searchFor("cats", 20))
      .pause(2.seconds)
      .exec(searchFor("cats", 40))
      .pause(2.seconds)
      .exec(searchFor("cats", 60))
      .pause(2.seconds)
      .exec(searchFor("cats", 80))
      .pause(2.seconds)
      .exec(searchFor("cats", 100))
      .pause(2.seconds)
      .exec(searchFor("cats", 120))
      .pause(2.seconds)
      .exec(searchFor("cats", 140))
      .pause(2.seconds)
      .exec(searchFor("cats", 160))
      .pause(2.seconds)
      .exec(searchFor("cats", 180))
      .pause(2.seconds)
      .exec(searchFor("cats", 200))
      .pause(2.seconds)
      .exec(searchFor("cats", 220))
      .pause(2.seconds)
      .exec(searchFor("cats", 240))
      .pause(2.seconds)
      .exec(searchFor("mice"))
      .pause(2.seconds)
      .exec(searchFor("mice", 20))
      .pause(2.seconds)
      .exec(searchFor("mice", 40))
      .pause(2.seconds)
      .exec(searchFor("mice", 60))
      .pause(2.seconds)
      .exec(searchFor("mice", 80))
      .pause(2.seconds)
      .exec(searchFor("mice", 100))
      .pause(2.seconds)
      .exec(searchFor("mice", 120))
      .pause(2.seconds)
      .exec(searchFor("mice", 140))
      .pause(2.seconds)
      .exec(searchFor("mice", 160))
      .pause(2.seconds)
      .exec(searchFor("mice", 180))
      .pause(2.seconds)
      .exec(searchFor("mice", 200))
      .pause(2.seconds)
      .exec(searchFor("mice", 220))
      .pause(2.seconds)
      .exec(searchFor("mice", 240))
      .pause(2.seconds)

  setUp(
    scn.inject(rampUpUsers)
  ).protocols(jsonHttpProtocol())
}
