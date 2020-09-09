package bot.utils

import bot.utils.models.YahooApiRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class MockDataRetriever : IDataRetriever {
    override fun isTokenExpired(retrieved: Long, expiresIn: Int): Boolean {
        return false
    }

    override fun refreshExpiredToken() {
        println("Mocking - refreshing expired token")
    }

    override fun getAuthenticationToken() {
        println("Mocking - getting authentication token")
    }

    override fun grabData(url: String): Document {
        return Jsoup.parse(
            "<matchup>\n" +
                    "\t<week> 16 </week>\n" +
                    "\t<week_start> 2018-12-18 </week_start>\n" +
                    "\t<week_end> 2018-12-24 </week_end>\n" +
                    "\t<status> postevent </status>\n" +
                    "\t<is_playoffs> 1 </is_playoffs>\n" +
                    "\t<is_consolation> 0 </is_consolation>\n" +
                    "\t<is_matchup_recap_available> 1 </is_matchup_recap_available>\n" +
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
                    "\t\t\t<team_logos>\n" +
                    "\t\t\t\t<team_logo>\n" +
                    "\t\t\t\t\t<size> large </size>\n" +
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
                    "\t\t\t<managers>\n" +
                    "\t\t\t\t<manager>\n" +
                    "\t\t\t\t\t<manager_id> 1 </manager_id>\n" +
                    "\t\t\t\t\t<nickname> Matt F </nickname>\n" +
                    "\t\t\t\t\t<is_commissioner> 1 </is_commissioner>\n" +
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
                    "\t\t\t<team_logos>\n" +
                    "\t\t\t\t<team_logo>\n" +
                    "\t\t\t\t\t<size> large </size>\n" +
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
                    "\t\t\t<managers>\n" +
                    "\t\t\t\t<manager>\n" +
                    "\t\t\t\t\t<manager_id> 3 </manager_id>\n" +
                    "\t\t\t\t\t<nickname> Sean </nickname>\n" +
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
    }

    override fun retrieveGameKey(): String? {
        return "TEST GAME KEY"
    }

    override fun yahooApiRequest(yahooApiRequest: YahooApiRequest): Document {
        return when (yahooApiRequest) {
            YahooApiRequest.Transactions -> getTransactions()
            YahooApiRequest.Standings -> getStandings()
            YahooApiRequest.TeamsData -> getTeamsData()
        }
    }

    override fun getTransactions(): Document {
        // TODO: Fix this to actually be a mocked transaction document
        return Document("")
    }

    override fun getStandings(): Document {
        // TODO: Fix this to actually be a mocked standings document
        return Document("")
    }

    override fun getTeamsData(): Document {
        // TODO: Fix this to actually be a mocked teams data
        return Document("")
    }
}