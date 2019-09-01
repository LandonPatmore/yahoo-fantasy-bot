
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.PublishSubject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import transformers.Team
import transformers.convertToMatchUpObject

internal class MatchUpDataTransformerKtTest {

    private lateinit var testScheduler: TestScheduler

    private lateinit var testPublishSubject: PublishSubject<Document>

    private lateinit var document: Document

    @BeforeEach
    fun init() {
        testScheduler = TestScheduler()
        testPublishSubject = PublishSubject.create()

        document = Jsoup.parse(
            "<matchup>\n" +
                    "\t<week> 16 </week>\n" +
                    "\t<week_start> 2018-12-18 </week_start>\n" +
                    "\t<week_end> 2018-12-24 </week_end>\n" +
                    "\t<status> postevent </status>\n" +
                    "\t<is_playoffs> 1 </is_playoffs>\n" +
                    "\t<is_consolation> 0 </is_consolation>\n" +
                    "\t<is_matchup_recap_available> 1 </is_matchup_recap_available>\n" +
                    "\t<matchup_recap_url> https://football.fantasysports.yahoo.com/archive/nfl/2018/1269264/recap?week=16&amp;mid1=1&amp;mid2=3 </matchup_recap_url>\n" +
                    "\t<matchup_recap_title> MegaWatts defy expectations, defeat Revenge 4 Hiroshima in 208.52-129.86 rout </matchup_recap_title>\n" +
                    "\t<matchup_grades>\n" +
                    "\t\t<matchup_grade>\n" +
                    "\t\t\t<team_key> 380.l.1269264.t.1 </team_key>\n" +
                    "\t\t\t<grade> B </grade>\n" +
                    "\t\t</matchup_grade>\n" +
                    "\t\t<matchup_grade>\n" +
                    "\t\t\t<team_key> 380.l.1269264.t.3 </team_key>\n" +
                    "\t\t\t<grade> A+ </grade>\n" +
                    "\t\t</matchup_grade>\n" +
                    "\t</matchup_grades>\n" +
                    "\t<is_tied> 0 </is_tied>\n" +
                    "\t<winner_team_key> 380.l.1269264.t.3 </winner_team_key>\n" +
                    "\t<teams count=\"2\">\n" +
                    "\t\t<team>\n" +
                    "\t\t\t<team_key> 380.l.1269264.t.1 </team_key>\n" +
                    "\t\t\t<team_id> 1 </team_id>\n" +
                    "\t\t\t<name> TEST TEAM 1 </name>\n" +
                    "\t\t\t<url> https://football.fantasysports.yahoo.com/archive/nfl/2018/1269264/1 </url>\n" +
                    "\t\t\t<team_logos>\n" +
                    "\t\t\t\t<team_logo>\n" +
                    "\t\t\t\t\t<size> large </size>\n" +
                    "\t\t\t\t\t<url> https://s.yimg.com/cv/apiv2/default/nfl/nfl_1.png </url>\n" +
                    "\t\t\t\t</team_logo>\n" +
                    "\t\t\t</team_logos>\n" +
                    "\t\t\t<division_id> 1 </division_id>\n" +
                    "\t\t\t<waiver_priority> 9 </waiver_priority>\n" +
                    "\t\t\t<faab_balance> 17 </faab_balance>\n" +
                    "\t\t\t<number_of_moves> 41 </number_of_moves>\n" +
                    "\t\t\t<number_of_trades> 0 </number_of_trades>\n" +
                    "\t\t\t<roster_adds>\n" +
                    "\t\t\t\t<coverage_type> week </coverage_type>\n" +
                    "\t\t\t\t<coverage_value> 17 </coverage_value>\n" +
                    "\t\t\t\t<value> 0 </value>\n" +
                    "\t\t\t</roster_adds>\n" +
                    "\t\t\t<clinched_playoffs> 1 </clinched_playoffs>\n" +
                    "\t\t\t<league_scoring_type> head </league_scoring_type>\n" +
                    "\t\t\t<draft_position> 7 </draft_position>\n" +
                    "\t\t\t<has_draft_grade> 1 </has_draft_grade>\n" +
                    "\t\t\t<draft_grade> B </draft_grade>\n" +
                    "\t\t\t<draft_recap_url> https://football.fantasysports.yahoo.com/archive/nfl/2018/1269264/1/draftrecap </draft_recap_url>\n" +
                    "\t\t\t<managers>\n" +
                    "\t\t\t\t<manager>\n" +
                    "\t\t\t\t\t<manager_id> 1 </manager_id>\n" +
                    "\t\t\t\t\t<nickname> Matt F </nickname>\n" +
                    "\t\t\t\t\t<guid> SCG2SW6NNCDEZVVOTVCNJVEC2U </guid>\n" +
                    "\t\t\t\t\t<is_commissioner> 1 </is_commissioner>\n" +
                    "\t\t\t\t\t<email> kuyamatt26@yahoo.com </email>\n" +
                    "\t\t\t\t\t<image_url> https://ct.yimg.com/cy/1768/39361574426_98028a_64sq.jpg </image_url>\n" +
                    "\t\t\t\t</manager>\n" +
                    "\t\t\t</managers>\n" +
                    "\t\t\t<win_probability> 0 </win_probability>\n" +
                    "\t\t\t<team_points>\n" +
                    "\t\t\t\t<coverage_type> week </coverage_type>\n" +
                    "\t\t\t\t<week> 16 </week>\n" +
                    "\t\t\t\t<total> 129.86 </total>\n" +
                    "\t\t\t</team_points>\n" +
                    "\t\t\t<team_projected_points>\n" +
                    "\t\t\t\t<coverage_type> week </coverage_type>\n" +
                    "\t\t\t\t<week> 16 </week>\n" +
                    "\t\t\t\t<total> 138.11 </total>\n" +
                    "\t\t\t</team_projected_points>\n" +
                    "\t\t</team>\n" +
                    "\t\t<team>\n" +
                    "\t\t\t<team_key> 380.l.1269264.t.3 </team_key>\n" +
                    "\t\t\t<team_id> 3 </team_id>\n" +
                    "\t\t\t<name> TEST TEAM 2 </name>\n" +
                    "\t\t\t<url> https://football.fantasysports.yahoo.com/archive/nfl/2018/1269264/3 </url>\n" +
                    "\t\t\t<team_logos>\n" +
                    "\t\t\t\t<team_logo>\n" +
                    "\t\t\t\t\t<size> large </size>\n" +
                    "\t\t\t\t\t<url> https://s.yimg.com/cv/apiv2/default/nfl/nfl_9.png </url>\n" +
                    "\t\t\t\t</team_logo>\n" +
                    "\t\t\t</team_logos>\n" +
                    "\t\t\t<division_id> 1 </division_id>\n" +
                    "\t\t\t<waiver_priority> 6 </waiver_priority>\n" +
                    "\t\t\t<faab_balance> 100 </faab_balance>\n" +
                    "\t\t\t<number_of_moves> 9 </number_of_moves>\n" +
                    "\t\t\t<number_of_trades> 0 </number_of_trades>\n" +
                    "\t\t\t<roster_adds>\n" +
                    "\t\t\t\t<coverage_type> week </coverage_type>\n" +
                    "\t\t\t\t<coverage_value> 17 </coverage_value>\n" +
                    "\t\t\t\t<value> 0 </value>\n" +
                    "\t\t\t</roster_adds>\n" +
                    "\t\t\t<clinched_playoffs> 1 </clinched_playoffs>\n" +
                    "\t\t\t<league_scoring_type> head </league_scoring_type>\n" +
                    "\t\t\t<draft_position> 8 </draft_position>\n" +
                    "\t\t\t<has_draft_grade> 1 </has_draft_grade>\n" +
                    "\t\t\t<draft_grade> B </draft_grade>\n" +
                    "\t\t\t<draft_recap_url> https://football.fantasysports.yahoo.com/archive/nfl/2018/1269264/3/draftrecap </draft_recap_url>\n" +
                    "\t\t\t<managers>\n" +
                    "\t\t\t\t<manager>\n" +
                    "\t\t\t\t\t<manager_id> 3 </manager_id>\n" +
                    "\t\t\t\t\t<nickname> Sean </nickname>\n" +
                    "\t\t\t\t\t<guid> W7I2UOJFW3J5IT4YCTQN2XNIO4 </guid>\n" +
                    "\t\t\t\t\t<email> seankrasner@yahoo.com </email>\n" +
                    "\t\t\t\t\t<image_url> https://ct.yimg.com/cy/1768/39361574426_98028a_64sq.jpg </image_url>\n" +
                    "\t\t\t\t</manager>\n" +
                    "\t\t\t</managers>\n" +
                    "\t\t\t<win_probability> 1 </win_probability>\n" +
                    "\t\t\t<team_points>\n" +
                    "\t\t\t\t<coverage_type> week </coverage_type>\n" +
                    "\t\t\t\t<week> 16 </week>\n" +
                    "\t\t\t\t<total> 208.52 </total>\n" +
                    "\t\t\t</team_points>\n" +
                    "\t\t\t<team_projected_points>\n" +
                    "\t\t\t\t<coverage_type> week </coverage_type>\n" +
                    "\t\t\t\t<week> 16 </week>\n" +
                    "\t\t\t\t<total> 131.00 </total>\n" +
                    "\t\t\t</team_projected_points>\n" +
                    "\t\t</team>\n" +
                    "\t</teams>\n" +
                    "</matchup>"
        )

        testPublishSubject.subscribeOn(testScheduler)
    }

    @Test
    fun convertToMatchUpObject() {
        val testObserver = TestObserver.create<Pair<Team, Team>>()

        testPublishSubject
            .convertToMatchUpObject()
            .subscribe(testObserver)
        testPublishSubject.onNext(document)

        val t = Team(
            "TEST TEAM 1",
            1,
            9,
            17,
            41,
            0,
            0.0,
            129.86,
            138.11,
            true
        )

        val t2 = Team(
            "TEST TEAM 2",
            3,
            6,
            100,
            9,
            0,
            1.0,
            208.52,
            131.00,
            true
        )

        val observed = testObserver.values()[0]

        compareTeams(t, observed.first)
        compareTeams(t2, observed.second)
    }

    private fun compareTeams(teamOne: Team, teamTwo: Team) {
        assertEquals(teamOne.name, teamTwo.name)
        assertEquals(teamOne.id, teamTwo.id)
        assertEquals(teamOne.waiverPriority, teamTwo.waiverPriority)
        assertEquals(teamOne.faabBalance, teamTwo.faabBalance)
        assertEquals(teamOne.numberOfMoves, teamTwo.numberOfMoves)
        assertEquals(teamOne.numberOfTrades, teamTwo.numberOfTrades)
        assertEquals(teamOne.winProbability * 100, teamTwo.winProbability)
        assertEquals(teamOne.points, teamTwo.points)
        assertEquals(teamOne.projectedPoints, teamTwo.projectedPoints)
        assertEquals(teamOne.clinchedPlayoffs, teamTwo.clinchedPlayoffs)
    }
}